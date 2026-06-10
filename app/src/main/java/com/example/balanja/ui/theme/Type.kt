package com.example.balanja.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.balanja.R

val BalanjaFontFamily = FontFamily(
    Font(R.font.plus_jakarta_sans_regular,   FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium,    FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold,  FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold,      FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold),
)

val BalanjaTypography = Typography(
    titleLarge      = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp),
    headlineMedium  = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.Bold,      fontSize = 24.sp),
    headlineSmall   = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.Bold,      fontSize = 20.sp),
    titleMedium     = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.SemiBold,  fontSize = 18.sp),
    bodyLarge       = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.Normal,    fontSize = 16.sp),
    bodyMedium      = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.Normal,    fontSize = 14.sp),
    labelSmall      = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.Medium,    fontSize = 12.sp),
)