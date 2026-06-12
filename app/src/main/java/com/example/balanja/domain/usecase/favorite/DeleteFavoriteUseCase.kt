package com.example.balanja.domain.usecase.favorite

import com.example.balanja.domain.repository.FavoriteRepository

class DeleteFavoriteUseCase(private val favoriteRepository: FavoriteRepository) {
    suspend operator fun invoke(stallId: String): Result<Unit> =
        favoriteRepository.deleteFavorite(stallId)
}

