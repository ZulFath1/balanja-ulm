package com.example.balanja.domain.usecase.favorite

import com.example.balanja.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class IsFavoriteUseCase(private val favoriteRepository: FavoriteRepository) {
    operator fun invoke(stallId: String): Flow<Boolean> = favoriteRepository.isFavorite(stallId)
}

