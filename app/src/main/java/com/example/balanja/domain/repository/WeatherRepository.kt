package com.example.balanja.domain.repository

import com.example.balanja.domain.model.Weather

interface WeatherRepository {
    suspend fun getCampusWeather(): Result<Weather>
}
