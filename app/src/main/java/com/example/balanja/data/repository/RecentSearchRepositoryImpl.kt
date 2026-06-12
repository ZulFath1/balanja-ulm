package com.example.balanja.data.repository

import com.example.balanja.data.local.dao.RecentSearchDao
import com.example.balanja.data.local.entity.RecentSearchEntity
import com.example.balanja.domain.model.RecentSearch
import com.example.balanja.domain.repository.RecentSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecentSearchRepositoryImpl(
    private val recentSearchDao: RecentSearchDao
) : RecentSearchRepository {

    override fun getRecentSearches(): Flow<List<RecentSearch>> {
        return recentSearchDao.getRecentSearches().map { entities ->
            entities.map { entity ->
                RecentSearch(
                    stallId = entity.stallId,
                    name = entity.name,
                    location = entity.location,
                    priceRange = entity.priceRange,
                    photoUrl = entity.photoUrl,
                    averageRating = entity.averageRating,
                    isOpen = entity.isOpen,
                    timestamp = entity.timestamp
                )
            }
        }
    }

    override suspend fun addRecentSearch(recentSearch: RecentSearch) {
        val entity = RecentSearchEntity(
            stallId = recentSearch.stallId,
            name = recentSearch.name,
            location = recentSearch.location,
            priceRange = recentSearch.priceRange,
            photoUrl = recentSearch.photoUrl,
            averageRating = recentSearch.averageRating,
            isOpen = recentSearch.isOpen,
            timestamp = System.currentTimeMillis() // Update timestamp to now
        )
        recentSearchDao.insertRecentSearch(entity)
    }

    override suspend fun clearRecentSearches() {
        recentSearchDao.clearRecentSearches()
    }
}
