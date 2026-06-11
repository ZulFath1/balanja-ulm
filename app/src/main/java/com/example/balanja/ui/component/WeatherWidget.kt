package com.example.balanja.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balanja.domain.model.Weather
import com.example.balanja.presentation.util.UiState

// ─── Color tokens (sesuai design spec) ───────────────────────────────────────
private val Primary = Color(0xFF870500)
private val TextSecondary = Color(0xFF555555)
private val TextMuted = Color(0xFF9E9E9E)
private val SurfaceWhite = Color(0xFFFFFFFF)

// ─── Public entry point ───────────────────────────────────────────────────────

@Composable
fun WeatherWidget(
    uiState: UiState<Weather>,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is UiState.Loading -> WeatherShimmer(modifier)
        is UiState.Success -> WeatherContent(weather = uiState.data, modifier = modifier)
        is UiState.Error   -> WeatherError(modifier)
    }
}

// ─── Success state ────────────────────────────────────────────────────────────

@Composable
private fun WeatherContent(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "weather_fade_in"
    )
    LaunchedEffect(Unit) { visible = true }

    WeatherCard(modifier = modifier.alpha(alpha)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            WeatherIcon(
                weatherCode = weather.iconCode,
                modifier = Modifier.size(48.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Suhu — H2 Bold, Primary, 1 desimal
                Text(
                    text = "${"%.1f".format(weather.temperature)}°C",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                // Deskripsi — Body, TextSecondary, capitalize huruf pertama
                Text(
                    text = weather.description.capitalize(Locale.current),
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                // Lokasi — Caption, TextMuted
                Text(
                    text = "📍 ${weather.cityName}",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Spacer(Modifier.height(4.dp))
                // Kelembaban — Caption, TextMuted
                Text(
                    text = "💧 ${weather.humidity}%",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }
    }
}

// ─── Weather icon (Open-Meteo weather_code → emoji) ──────────────────────────

@Composable
private fun WeatherIcon(
    weatherCode: String,
    modifier: Modifier = Modifier
) {
    val emoji = when (weatherCode.toIntOrNull()) {
        0           -> "☀️"
        1, 2        -> "🌤️"
        3           -> "☁️"
        45, 48      -> "🌫️"
        51, 53, 55  -> "🌦️"
        61, 63, 65  -> "🌧️"
        80, 81, 82  -> "⛈️"
        95, 96, 99  -> "⛈️"
        else        -> "🌡️"
    }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text = emoji, fontSize = 36.sp)
    }
}

// ─── Loading state — shimmer ──────────────────────────────────────────────────

@Composable
private fun WeatherShimmer(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color(0xFFE0E0E0),
        Color(0xFFF5F5F5),
        Color(0xFFE0E0E0)
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )

    WeatherCard(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(brush)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .width(80.dp).height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(120.dp).height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .width(90.dp).height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(60.dp).height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
        }
    }
}

// ─── Error state ──────────────────────────────────────────────────────────────

@Composable
private fun WeatherError(modifier: Modifier = Modifier) {
    WeatherCard(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Cuaca tidak tersedia",
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}

// ─── Shared card shell ────────────────────────────────────────────────────────

@Composable
private fun WeatherCard(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            content = content
        )
    }
}