package com.example.balanja.data.repository

import com.example.balanja.data.local.dao.FavoriteStallDao
import com.example.balanja.data.local.entity.FavoriteStallEntity
import com.example.balanja.domain.model.FavoriteStall
import com.example.balanja.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val favoriteStallDao: FavoriteStallDao
) : FavoriteRepository {

    override fun observeFavorites(): Flow<List<FavoriteStall>> {
        return favoriteStallDao.getAllFavorites().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addFavorite(stall: FavoriteStall): Result<Unit> {
        return try {
            favoriteStallDao.insertFavorite(stall.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFavorite(stallId: String): Result<Unit> {
        return try {
            favoriteStallDao.deleteFavorite(stallId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isFavorite(stallId: String): Boolean {
        return try {
            favoriteStallDao.isFavorite(stallId).first()
        } catch (e: Exception) {
            false
        }
    }

    private fun FavoriteStallEntity.toDomain() = FavoriteStall(
        stallId = stallId,
        name = name,
        location = location,
        imageUrl = photoUrl,
        ratingAverage = averageRating,
        priceMin = 0, // Since we stored priceRange, we could parse it or just ignore. For now set 0.
        priceMax = 0,
        isOpen = isOpen,
        savedAt = savedAt
    )

    private fun FavoriteStall.toEntity() = FavoriteStallEntity(
        stallId = stallId,
        name = name,
        location = location,
        priceRange = "$priceMin - $priceMax",
        photoUrl = imageUrl,
        averageRating = ratingAverage,
        isOpen = isOpen,
        savedAt = savedAt
    )
}
