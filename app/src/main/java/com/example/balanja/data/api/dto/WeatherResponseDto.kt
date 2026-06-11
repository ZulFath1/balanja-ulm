package com.example.balanja.data.api.dto

data class WeatherResponseDto(
    val current: CurrentWeatherDto
)

data class CurrentWeatherDto(
    val temperature_2m: Double,
    val relative_humidity_2m: Int,
    val wind_speed_10m: Double,
    val weather_code: Int
)