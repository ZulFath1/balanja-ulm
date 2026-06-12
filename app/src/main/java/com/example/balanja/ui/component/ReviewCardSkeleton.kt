package com.example.balanja.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun ReviewCardSkeleton(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                // Profile Picture Placeholder
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(shimmerBrush())
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Name Placeholder
                    Box(
                        modifier = Modifier
                            .size(width = 120.dp, height = 16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(shimmerBrush())
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    // Date Placeholder
                    Box(
                        modifier = Modifier
                            .size(width = 80.dp, height = 12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(shimmerBrush())
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Stars Placeholder
                    Box(
                        modifier = Modifier
                            .size(width = 100.dp, height = 16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(shimmerBrush())
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Comment Line 1
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(shimmerBrush())
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    // Comment Line 2
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(shimmerBrush())
                    )
                }
            }
        }
    }
}
