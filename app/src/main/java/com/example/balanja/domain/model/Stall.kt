package com.example.balanja.domain.model

import com.google.firebase.database.PropertyName

data class Stall(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val location: String = "",
    val priceMin: Int = 0,
    val priceMax: Int = 0,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    @get:PropertyName("isOpen")
    @set:PropertyName("isOpen")
    var isOpen: Boolean = false,
    val imageUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val ownerId: String = "",
    val menu: Map<String, MenuItem> = emptyMap()
)
