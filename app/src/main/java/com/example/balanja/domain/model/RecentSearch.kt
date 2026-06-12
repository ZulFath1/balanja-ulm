package com.example.balanja.domain.model

data class RecentSearch(
    val stallId: String,
    val name: String,
    val location: String,
    val priceRange: String,
    val photoUrl: String,
    val averageRating: Double,
    val isOpen: Boolean,
    val timestamp: Long
)
