package com.example.balanja.data.repository

import com.example.balanja.domain.model.FavoriteStall
import com.example.balanja.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteRepositoryImpl : FavoriteRepository {
    override fun observeFavorites(): Flow<List<FavoriteStall>> = flow { emit(emptyList()) }
    override suspend fun addFavorite(stall: FavoriteStall): Result<Unit> = Result.success(Unit)
    override suspend fun deleteFavorite(stallId: String): Result<Unit> = Result.success(Unit)
    override suspend fun isFavorite(stallId: String): Boolean = false
}
