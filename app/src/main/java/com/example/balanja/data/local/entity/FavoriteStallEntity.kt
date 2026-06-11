package com.example.balanja.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_stalls")
data class FavoriteStallEntity(
    @PrimaryKey val stallId: String,
    val name: String,
    val location: String,
    val priceRange: String,
    val photoUrl: String,
    val averageRating: Double,
    val isOpen: Boolean,
    val savedAt: Long = System.currentTimeMillis()
)
