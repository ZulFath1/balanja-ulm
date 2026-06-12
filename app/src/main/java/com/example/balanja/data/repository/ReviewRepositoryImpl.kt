package com.example.balanja.data.repository

import com.example.balanja.domain.model.Review
import com.example.balanja.domain.repository.ReviewRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReviewRepositoryImpl : ReviewRepository {
    private val database = FirebaseDatabase.getInstance().reference.child("reviews")

    override fun observeReviews(stallId: String): Flow<List<Review>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reviews = snapshot.children.mapNotNull { it.getValue(Review::class.java) }
                trySend(reviews.sortedByDescending { it.createdAt })
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        val query = database.orderByChild("stallId").equalTo(stallId)
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    override fun observeMyReviews(userId: String): Flow<List<Review>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reviews = snapshot.children.mapNotNull { it.getValue(Review::class.java) }
                trySend(reviews.sortedByDescending { it.createdAt })
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        val query = database.orderByChild("userId").equalTo(userId)
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    override suspend fun addReview(stallId: String, review: Review): Result<Unit> {
        return try {
            val key = database.push().key ?: return Result.failure(Exception("Failed to generate key"))
            val newReview = review.copy(id = key, stallId = stallId)
            database.child(key).setValue(newReview).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateReview(stallId: String, reviewId: String, review: Review): Result<Unit> {
        return try {
            val updateData = mutableMapOf<String, Any>(
                "rating" to review.rating,
                "comment" to review.comment,
                "attributes" to review.attributes,
                "imageUrls" to review.imageUrls,
                "updatedAt" to System.currentTimeMillis()
            )
            review.userPhotoUrl?.let { updateData["userPhotoUrl"] = it }
            review.imageUrl?.let { updateData["imageUrl"] = it }
            
            database.child(reviewId).updateChildren(updateData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteReview(stallId: String, reviewId: String): Result<Unit> {
        return try {
            database.child(reviewId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
