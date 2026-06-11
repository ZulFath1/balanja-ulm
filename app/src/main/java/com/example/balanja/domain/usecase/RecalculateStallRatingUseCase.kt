package com.example.balanja.domain.usecase

import com.example.balanja.domain.repository.StallRepository
import com.example.balanja.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.first

class RecalculateStallRatingUseCase(
    private val stallRepository: StallRepository,
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(stallId: String): Result<Unit> {
        return try {
            val reviews = reviewRepository.observeReviews(stallId).first()
            val reviewCount = reviews.size
            val averageRating = if (reviewCount > 0) {
                reviews.map { it.rating }.average()
            } else {
                0.0
            }
            
            // Assume StallRepository has updateRating method, let's just make it generic or create it.
            // For now, we update it via stallRepository.
            stallRepository.updateStallRating(stallId, averageRating, reviewCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
