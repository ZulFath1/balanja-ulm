package com.example.balanja.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary      = BalanjaColor.PrimaryLight,
    onPrimary    = Color.White,
    background   = BalanjaColor.BackgroundDark,
    onBackground = BalanjaColor.TextPrimaryDark,
    surface      = BalanjaColor.SurfaceDark,
    onSurface    = BalanjaColor.TextPrimaryDark,
    surfaceVariant = BalanjaColor.SurfaceMutedDark,
    onSurfaceVariant = BalanjaColor.TextSecondaryDark,
    error        = BalanjaColor.DangerLight,
    onError      = Color.White,
    primaryContainer = BalanjaColor.PrimaryDark,
    onPrimaryContainer = BalanjaColor.PrimaryLight,
)

private val LightColorScheme = lightColorScheme(
    primary      = BalanjaColor.Primary,
    onPrimary    = Color.White,
    background   = BalanjaColor.Background,
    onBackground = BalanjaColor.TextPrimary,
    surface      = BalanjaColor.Surface,
    onSurface    = BalanjaColor.TextPrimary,
    surfaceVariant = BalanjaColor.SurfaceMuted,
    onSurfaceVariant = BalanjaColor.TextSecondary,
    error        = BalanjaColor.Danger,
    onError      = Color.White,
    primaryContainer = Color(0xFFFFEBEB), // Sangat muda merah
    onPrimaryContainer = BalanjaColor.Primary,
)

@Composable
fun BalanjaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = BalanjaTypography,
        content     = content
    )
}