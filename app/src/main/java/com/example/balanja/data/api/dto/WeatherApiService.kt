package com.example.balanja.data.api

import com.example.balanja.data.api.dto.WeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") lat: Double = ULM_LAT,
        @Query("longitude") lon: Double = ULM_LON,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code",
        @Query("timezone") timezone: String = "Asia/Bangkok"
    ): WeatherResponseDto

    companion object {
        const val BASE_URL = "https://api.open-meteo.com/v1/"
        const val ULM_LAT = -3.3194
        const val ULM_LON = 114.5908
    }
}