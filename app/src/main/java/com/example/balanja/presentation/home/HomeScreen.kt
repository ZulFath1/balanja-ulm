package com.example.balanja.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.balanja.presentation.util.UiState
import com.example.balanja.ui.component.StallCard
import com.example.balanja.ui.component.WeatherWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()

    // Pull-to-refresh state — selesai saat kedua state bukan Loading
    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(uiState, weatherState) {
        if (uiState !is HomeUiState.Loading && weatherState !is UiState.Loading) {
            isRefreshing = false
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    androidx.compose.material3.pulltorefresh.PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.onRefresh()
        },
        modifier = Modifier.fillMaxSize()
    ) {
            val favoritesList by viewModel.favorites.collectAsStateWithLifecycle()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
            ) {
                // Header
                item {
                    Column(modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)) {
                        val nameText = if (viewModel.userName.isNotBlank()) ", ${viewModel.userName}!" else "!"
                        Text(
                            text = "${viewModel.greeting}$nameText",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Gray,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = buildAnnotatedString {
                                append("Mau ")
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xFF870500),
                                        fontWeight = FontWeight.Black,
                                        fontStyle = FontStyle.Italic
                                    )
                                ) { append("Balanja") }
                                append(" apa hari ini?")
                            },
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1A1A),
                            lineHeight = 34.sp,
                            letterSpacing = (-0.5).sp
                        )
                    }
                }

                // Weather Widget
                item {
                    WeatherWidget(
                        uiState = weatherState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                when (val state = uiState) {
                    is HomeUiState.Loading -> {
                        items(5) {
                            com.example.balanja.ui.component.StallCardSkeleton()
                        }
                    }
                    is HomeUiState.Error -> {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                com.example.balanja.ui.component.EmptyStateComponent(
                                    icon = androidx.compose.material.icons.Icons.Default.Search,
                                    title = "Gagal Memuat",
                                    subtitle = state.message,
                                    actionLabel = "Coba Lagi",
                                    onAction = { viewModel.fetchStalls() }
                                )
                            }
                        }
                    }
                    is HomeUiState.Success -> {
                        val stalls = state.stalls
                        if (stalls.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    com.example.balanja.ui.component.EmptyStateComponent(
                                        icon = androidx.compose.material.icons.Icons.Default.Search,
                                        title = "Belum Ada Stan",
                                        subtitle = "Belum ada pedagang yang terdaftar."
                                    )
                                }
                            }
                        } else {
                            items(stalls) { stall ->
                                val isFavorite = favoritesList.any { it.stallId == stall.id }
                                StallCard(
                                    stall = stall,
                                    isFavorite = isFavorite,
                                    onToggleFavorite = { viewModel.toggleFavorite(stall) },
                                    onClick = { stallId -> onNavigateToDetail(stallId) }
                                )
                            }
                        }
                    }
                }
            }

    }
}