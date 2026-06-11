package com.example.balanja.domain.usecase

import com.example.balanja.domain.model.Stall
import com.example.balanja.domain.repository.StallRepository
import kotlinx.coroutines.flow.Flow

class GetStallsUseCase(private val stallRepository: StallRepository) {
    operator fun invoke(): Flow<List<Stall>> = stallRepository.getAllStalls()
}