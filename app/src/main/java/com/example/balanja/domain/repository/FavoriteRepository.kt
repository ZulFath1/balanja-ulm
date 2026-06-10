package com.example.balanja.domain.repository

import com.example.balanja.domain.model.FavoriteStall
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavorites(): Flow<List<FavoriteStall>>
    suspend fun addFavorite(stall: FavoriteStall): Result<Unit>
    suspend fun deleteFavorite(stallId: String): Result<Unit>
    suspend fun isFavorite(stallId: String): Boolean
}
