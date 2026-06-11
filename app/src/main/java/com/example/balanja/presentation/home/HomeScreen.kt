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
    onNavigateToDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Selamat Pagi!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = buildAnnotatedString {
                                append("Mau ")
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xFF870500),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontStyle = FontStyle.Italic
                                    )
                                ) { append("Balanja") }
                                append(" apa hari ini?")
                            },
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF870500)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBF9F8))
            )
        },
        containerColor = Color(0xFFFBF9F8)
    ) { paddingValues ->

        @OptIn(ExperimentalMaterial3Api::class)
        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.onRefresh()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // ── Widget Cuaca aktif (menggantikan placeholder BLJA-09) ─────
                Spacer(modifier = Modifier.height(8.dp))
                WeatherWidget(
                    uiState = weatherState,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // ── Daftar Stan (tidak berubah) ───────────────────────────────
                when (uiState) {
                    is HomeUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF870500))
                        }
                    }
                    is HomeUiState.Error -> {
                        val errorMessage = (uiState as HomeUiState.Error).message
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = errorMessage, color = Color.Red)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.fetchStalls() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF870500)
                                    )
                                ) {
                                    Text("Coba Lagi")
                                }
                            }
                        }
                    }
                    is HomeUiState.Success -> {
                        val stalls = (uiState as HomeUiState.Success).stalls
                        if (stalls.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Belum ada pedagang yang terdaftar.",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(stalls) { stall ->
                                    StallCard(
                                        stall = stall,
                                        onClick = { stallId -> onNavigateToDetail(stallId) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}