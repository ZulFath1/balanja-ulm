package com.example.balanja.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StallCardSkeleton(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // --- Foto Stan Placeholder ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(shimmerBrush())
            ) {
                // Badge Placeholder
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .size(width = 60.dp, height = 24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.5f))
                        .align(Alignment.TopStart)
                )

                // Favorite Icon Placeholder
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.5f))
                )
            }

            // --- Informasi Stan Placeholder ---
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Title Placeholder
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(shimmerBrush())
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Rating Placeholder
                    Box(
                        modifier = Modifier
                            .size(width = 48.dp, height = 24.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(shimmerBrush())
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Price Placeholder
                Box(
                    modifier = Modifier
                        .size(width = 120.dp, height = 18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush())
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Location Placeholder
                Box(
                    modifier = Modifier
                        .size(width = 180.dp, height = 16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush())
                )
            }
        }
    }
}
