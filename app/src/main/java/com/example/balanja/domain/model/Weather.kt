package com.example.balanja.domain.model

data class Weather(
    val temperature: Double = 0.0,
    val description: String = "",
    val iconCode: String = "",
    val humidity: Int = 0,
    val windSpeed: Double = 0.0,
    val cityName: String = "Banjarmasin",
    val fetchedAt: Long = 0L
)
