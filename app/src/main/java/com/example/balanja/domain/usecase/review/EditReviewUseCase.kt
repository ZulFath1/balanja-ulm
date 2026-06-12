package com.example.balanja.domain.usecase.review

import com.example.balanja.domain.model.Review
import com.example.balanja.domain.repository.ReviewRepository

class EditReviewUseCase(private val repository: ReviewRepository) {
    suspend operator fun invoke(stallId: String, reviewId: String, review: Review): Result<Unit> {
        if (review.rating < 1 || review.rating > 5) return Result.failure(Exception("Rating must be between 1 and 5"))
        if (review.comment.isBlank()) return Result.failure(Exception("Comment cannot be empty"))
        return repository.updateReview(stallId, reviewId, review)
    }
}

