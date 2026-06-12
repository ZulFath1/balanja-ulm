package com.example.balanja.domain.usecase.stall

import com.example.balanja.domain.model.Stall
import com.example.balanja.domain.repository.StallRepository
import kotlinx.coroutines.flow.Flow

class GetAllStallsUseCase(private val stallRepository: StallRepository) {
    operator fun invoke(): Flow<List<Stall>> {
        return stallRepository.getAllStalls()
    }
}
