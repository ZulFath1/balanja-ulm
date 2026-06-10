package com.example.balanja.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun BalanjaTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary      = BalanjaColor.Primary,
        onPrimary    = Color.White,
        background   = BalanjaColor.Background,
        onBackground = BalanjaColor.TextPrimary,
        surface      = BalanjaColor.Surface,
        onSurface    = BalanjaColor.TextPrimary,
        error        = BalanjaColor.Danger,
        onError      = Color.White,
    )
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = BalanjaTypography,
        content     = content
    )
}