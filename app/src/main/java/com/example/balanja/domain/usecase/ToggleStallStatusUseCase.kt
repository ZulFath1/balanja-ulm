package com.example.balanja.domain.usecase

import com.example.balanja.domain.repository.StallRepository

class ToggleStallStatusUseCase(private val stallRepository: StallRepository) {
    suspend operator fun invoke(stallId: String, isOpen: Boolean): Result<Unit> =
        stallRepository.updateStallStatus(stallId, isOpen)
}
