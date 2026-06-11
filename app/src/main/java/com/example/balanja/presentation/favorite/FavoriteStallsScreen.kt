package com.example.balanja.presentation.favorite

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.balanja.domain.model.Stall
import com.example.balanja.ui.component.StallCard
import com.example.balanja.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteStallsScreen(
    navController: NavController,
    viewModel: FavoriteStallsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stan Favorit Saya", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBF9F8))
            )
        },
        containerColor = Color(0xFFFBF9F8)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(5) {
                            com.example.balanja.ui.component.StallCardSkeleton()
                        }
                    }
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error!!,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.favorites.isEmpty() -> {
                    com.example.balanja.ui.component.EmptyStateComponent(
                        icon = Icons.Default.FavoriteBorder,
                        title = "Belum Ada Favorit",
                        subtitle = "Tambahkan stan favorit Anda dengan menekan ikon hati.",
                        actionLabel = "Jelajahi Balanja",
                        onAction = { navController.navigate(Screen.Home.route) }
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.favorites, key = { it.stallId }) { fav ->
                            // Convert FavoriteStall to Stall to use StallCard
                            val stall = Stall(
                                id = fav.stallId,
                                name = fav.name,
                                location = fav.location,
                                rating = fav.ratingAverage,
                                isOpen = fav.isOpen,
                                imageUrl = fav.imageUrl
                            )
                            StallCard(
                                stall = stall,
                                isFavorite = true,
                                onToggleFavorite = { viewModel.removeFavorite(fav.stallId) },
                                onClick = { id -> navController.navigate(Screen.StallDetail.createRoute(id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}
