package com.example.balanja.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetFilterRow(
    maxPrice: Int,
    onPriceChange: (Int) -> Unit
) {
    // Definisi batas harga untuk filter
    val budgetOptions = listOf(5000, 10000, 20000, 50000)

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(budgetOptions) { price ->
            val isSelected = maxPrice == price
            FilterChip(
                selected = isSelected,
                onClick = { onPriceChange(price) },
                label = { Text("≤ Rp${price / 1000}k") },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    selectedContainerColor = Color(0xFF870500),
                    labelColor = Color.Gray,
                    selectedLabelColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = Color(0xFFE5E7EB),
                    selectedBorderColor = Color(0xFF870500)
                ),
                shape = RoundedCornerShape(100.dp)
            )
        }
    }
}