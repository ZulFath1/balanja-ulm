package com.example.balanja.domain.usecase

import com.example.balanja.domain.model.FavoriteStall
import com.example.balanja.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class GetFavoritesUseCase(private val favoriteRepository: FavoriteRepository) {
    operator fun invoke(): Flow<List<FavoriteStall>> = favoriteRepository.observeFavorites()
}
