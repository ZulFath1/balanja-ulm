package com.example.balanja.domain.usecase.search

import com.example.balanja.domain.model.RecentSearch
import com.example.balanja.domain.repository.RecentSearchRepository

class AddRecentSearchUseCase(private val repository: RecentSearchRepository) {
    suspend operator fun invoke(recentSearch: RecentSearch) {
        repository.addRecentSearch(recentSearch)
    }
}

