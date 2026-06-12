package com.example.balanja.domain.usecase.stall

import com.example.balanja.domain.repository.StallRepository

class ToggleStallStatusUseCase(private val stallRepository: StallRepository) {
    suspend operator fun invoke(stallId: String, isOpen: Boolean): Result<Unit> {
        return stallRepository.updateStallStatus(stallId, isOpen)
    }
}
