package com.example.balanja.domain.repository

import com.example.balanja.domain.model.Stall
import kotlinx.coroutines.flow.Flow

interface StallRepository {
    fun getAllStalls(): Flow<List<Stall>>
    fun getStallById(stallId: String): Flow<Stall?>
    suspend fun updateStallStatus(stallId: String, isOpen: Boolean): Result<Unit>
}