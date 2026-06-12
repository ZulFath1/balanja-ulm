package com.example.balanja.domain.usecase.search

import com.example.balanja.domain.repository.RecentSearchRepository

class ClearRecentSearchesUseCase(private val repository: RecentSearchRepository) {
    suspend operator fun invoke() {
        repository.clearRecentSearches()
    }
}

