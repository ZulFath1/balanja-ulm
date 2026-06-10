package com.example.balanja.domain.repository

import com.example.balanja.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun observeReviews(stallId: String): Flow<List<Review>>
    fun observeMyReviews(userId: String): Flow<List<Review>>
    suspend fun addReview(stallId: String, review: Review): Result<Unit>
    suspend fun updateReview(stallId: String, reviewId: String, review: Review): Result<Unit>
    suspend fun deleteReview(stallId: String, reviewId: String): Result<Unit>
}
