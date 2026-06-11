package com.example.balanja.domain.usecase

import com.example.balanja.domain.model.Review
import com.example.balanja.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow

class GetMyReviewsUseCase(private val repository: ReviewRepository) {
    operator fun invoke(userId: String): Flow<List<Review>> = repository.observeMyReviews(userId)
}
