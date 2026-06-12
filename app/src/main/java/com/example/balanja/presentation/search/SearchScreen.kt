package com.example.balanja.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.balanja.ui.component.StallCard
import com.example.balanja.ui.component.EmptyStateComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Pencarian",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("Ketik nama warung...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Ikon Cari",
                        tint = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 120.dp)
                    ) {
                        items(5) {
                            com.example.balanja.ui.component.StallCardSkeleton()
                        }
                    }
                }
                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.error!!, color = Color.Red)
                    }
                }
                uiState.searchQuery.isBlank() -> {
                    if (uiState.recentSearches.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Terakhir Dilihat",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            TextButton(onClick = { viewModel.clearRecentSearches() }) {
                                Text("Hapus Semua", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 120.dp)
                        ) {
                            items(uiState.recentSearches) { recent ->
                                val stall = uiState.allStalls.find { it.id == recent.stallId }
                                if (stall != null) {
                                    StallCard(
                                        stall = stall,
                                        onClick = { 
                                            viewModel.onStallClicked(stall)
                                            onNavigateToDetail(it) 
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        EmptyStateComponent(
                            icon = Icons.Default.Search,
                            title = "Mulai Pencarian",
                            subtitle = "Ketik nama warung atau pedagang yang ingin Anda cari di atas."
                        )
                    }
                }
                uiState.filteredStalls.isEmpty() -> {
                    EmptyStateComponent(
                        icon = Icons.Default.Search,
                        title = "Warung Tidak Ditemukan",
                        subtitle = "Coba gunakan kata kunci lain yang lebih umum."
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 120.dp)
                    ) {
                        items(uiState.filteredStalls) { stall ->
                            StallCard(
                                stall = stall,
                                onClick = { 
                                    viewModel.onStallClicked(stall)
                                    onNavigateToDetail(it) 
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}