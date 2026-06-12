package com.example.balanja.domain.usecase.review

import com.example.balanja.domain.repository.ReviewRepository

class DeleteReviewUseCase(private val repository: ReviewRepository) {
    suspend operator fun invoke(stallId: String, reviewId: String): Result<Unit> {
        return repository.deleteReview(stallId, reviewId)
    }
}

