package com.example.balanja.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balanja.ui.component.FilterChipRow
import com.example.balanja.ui.component.StallCard
import com.example.balanja.ui.component.BudgetFilterRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val stalls by viewModel.filteredStalls.collectAsState()
    val selectedRating by viewModel.selectedRating.collectAsState()
    val maxPrice by viewModel.maxPrice.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cari Warung", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBF9F8))
            )
        },
        containerColor = Color(0xFFFBF9F8)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Search Input Field
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari warung, menu, atau lokasi...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )



            Spacer(modifier = Modifier.height(8.dp))

            FilterChipRow(
                selectedRating = selectedRating,
                onFilterChange = { viewModel.onRatingFilterChange(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            BudgetFilterRow(
                maxPrice = maxPrice,
                onPriceChange = { viewModel.onPriceFilterChange(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hasil Pencarian
            if (stalls.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Warung tidak ditemukan", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(stalls, key = { it.id }) { stall ->
                        StallCard(
                            stall = stall,
                            onClick = { onNavigateToDetail(stall.id) }
                        )
                    }
                }
            }
        }
    }
}