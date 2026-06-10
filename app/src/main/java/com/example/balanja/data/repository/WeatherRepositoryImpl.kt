package com.example.balanja.data.repository

import com.example.balanja.domain.model.Weather
import com.example.balanja.domain.repository.WeatherRepository

class WeatherRepositoryImpl : WeatherRepository {
    override suspend fun getCampusWeather(): Result<Weather> = Result.success(Weather())
}
