package com.example.balanja.domain.usecase.stall

import com.example.balanja.domain.model.Stall
import com.example.balanja.domain.repository.StallRepository
import kotlinx.coroutines.flow.Flow

class GetStallDetailUseCase(private val stallRepository: StallRepository) {
    operator fun invoke(stallId: String): Flow<Stall?> {
        return stallRepository.getStallById(stallId)
    }
}   
