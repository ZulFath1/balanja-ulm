package com.example.balanja.domain.model

data class Stall(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val location: String = "",
    val imageUrl: String = "",
    val priceMin: Int = 0,
    val priceMax: Int = 0,
    val ratingAverage: Double = 0.0,
    val reviewCount: Int = 0,
    val isOpen: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val ownerId: String = "",
    val createdAt: Long = 0L
)
