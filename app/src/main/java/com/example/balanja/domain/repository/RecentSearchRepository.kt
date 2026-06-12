package com.example.balanja.domain.repository

import com.example.balanja.domain.model.RecentSearch
import kotlinx.coroutines.flow.Flow

interface RecentSearchRepository {
    fun getRecentSearches(): Flow<List<RecentSearch>>
    suspend fun addRecentSearch(recentSearch: RecentSearch)
    suspend fun clearRecentSearches()
}
