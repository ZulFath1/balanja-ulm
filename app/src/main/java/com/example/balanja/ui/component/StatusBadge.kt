package com.example.balanja.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balanja.ui.theme.BalanjaColor

@Composable
fun StatusBadge(isOpen: Boolean, modifier: Modifier = Modifier) {
    val bg    = if (isOpen) BalanjaColor.SuccessLight else BalanjaColor.DangerLight
    val text  = if (isOpen) BalanjaColor.Success      else BalanjaColor.Danger
    val label = if (isOpen) "BUKA" else "TUTUP"

    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(100.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, color = text, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.05.sp)
    }
}
