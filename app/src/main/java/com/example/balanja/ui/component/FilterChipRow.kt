package com.example.balanja.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FilterChipRow(
    selectedRating: Float?,
    onFilterChange: (Float?) -> Unit
) {
    val ratingOptions = listOf(5.0f to "★ 5", 4.0f to "★ 4+", 3.0f to "★ 3+")

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(ratingOptions) { (value, label) ->
            val isSelected = selectedRating == value
            FilterChip(
                selected = isSelected,
                onClick = { onFilterChange(value) },
                label = { Text(label) },
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