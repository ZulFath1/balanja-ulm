package com.example.balanja.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val stallId: String,
    val name: String,
    val location: String,
    val priceRange: String,
    val photoUrl: String,
    val averageRating: Double,
    val isOpen: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
