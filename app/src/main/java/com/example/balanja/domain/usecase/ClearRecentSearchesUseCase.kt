package com.example.balanja.domain.usecase

import com.example.balanja.domain.repository.RecentSearchRepository

class ClearRecentSearchesUseCase(private val repository: RecentSearchRepository) {
    suspend operator fun invoke() {
        repository.clearRecentSearches()
    }
}
