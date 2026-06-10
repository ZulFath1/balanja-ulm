package com.example.balanja.domain.usecase

import com.example.balanja.domain.model.FavoriteStall
import com.example.balanja.domain.repository.FavoriteRepository

class AddFavoriteUseCase(private val favoriteRepository: FavoriteRepository) {
    suspend operator fun invoke(stall: FavoriteStall): Result<Unit> =
        favoriteRepository.addFavorite(stall)
}
