package com.example.balanja.domain.usecase.weather

import com.example.balanja.domain.model.Weather
import com.example.balanja.domain.repository.WeatherRepository

class GetCampusWeatherUseCase(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(): Result<Weather> {
        return repository.getCampusWeather()
    }
}
