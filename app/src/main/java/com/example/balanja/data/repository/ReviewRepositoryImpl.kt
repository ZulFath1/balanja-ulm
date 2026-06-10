package com.example.balanja.data.repository

import com.example.balanja.domain.model.Review
import com.example.balanja.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ReviewRepositoryImpl : ReviewRepository {
    override fun observeReviews(stallId: String): Flow<List<Review>> = flow { emit(emptyList()) }
    override fun observeMyReviews(userId: String): Flow<List<Review>> = flow { emit(emptyList()) }
    override suspend fun addReview(stallId: String, review: Review): Result<Unit> = Result.success(Unit)
    override suspend fun updateReview(stallId: String, reviewId: String, review: Review): Result<Unit> = Result.success(Unit)
    override suspend fun deleteReview(stallId: String, reviewId: String): Result<Unit> = Result.success(Unit)
}
