package com.example.balanja.data.repository

import com.example.balanja.data.api.WeatherApiService
import com.example.balanja.data.api.dto.WeatherResponseDto
import com.example.balanja.domain.model.Weather
import com.example.balanja.domain.repository.WeatherRepository
import java.io.IOException

class WeatherRepositoryImpl(
    private val api: WeatherApiService
) : WeatherRepository {

    override suspend fun getCampusWeather(): Result<Weather> {
        return try {
            val response = api.getCurrentWeather()
            Result.success(response.toDomain())
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun WeatherResponseDto.toDomain(): Weather {
        val c = current
        return Weather(
            temperature = c.temperature_2m,
            description = mapWeatherCodeToDescription(c.weather_code),
            iconCode = c.weather_code.toString(),
            humidity = c.relative_humidity_2m,
            windSpeed = c.wind_speed_10m,
            cityName = "Banjarmasin",
            fetchedAt = System.currentTimeMillis()
        )
    }

    private fun mapWeatherCodeToDescription(code: Int): String {
        return when (code) {
            0 -> "Cerah"
            1, 2, 3 -> "Berawan"
            45, 48 -> "Berkabut"
            51, 53, 55 -> "Gerimis"
            61, 63, 65 -> "Hujan"
            80, 81, 82 -> "Hujan Lebat"
            95, 96, 99 -> "Badai Petir"
            else -> "Tidak diketahui"
        }
    }
}