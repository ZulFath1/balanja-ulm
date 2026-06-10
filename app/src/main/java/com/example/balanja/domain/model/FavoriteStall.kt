package com.example.balanja.domain.model

data class FavoriteStall(
    val stallId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val location: String = "",
    val ratingAverage: Double = 0.0,
    val priceMin: Int = 0,
    val priceMax: Int = 0,
    val savedAt: Long = 0L
)
