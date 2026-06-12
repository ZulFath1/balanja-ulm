package com.example.balanja.domain.usecase.search

import com.example.balanja.domain.model.RecentSearch
import com.example.balanja.domain.repository.RecentSearchRepository
import kotlinx.coroutines.flow.Flow

class GetRecentSearchesUseCase(private val repository: RecentSearchRepository) {
    operator fun invoke(): Flow<List<RecentSearch>> = repository.getRecentSearches()
}

