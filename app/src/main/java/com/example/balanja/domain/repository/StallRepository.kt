package com.example.balanja.domain.repository

import com.example.balanja.domain.model.MenuItem
import com.example.balanja.domain.model.Stall
import kotlinx.coroutines.flow.Flow

interface StallRepository {
    fun observeStalls(): Flow<List<Stall>>
    fun observeStallById(stallId: String): Flow<Stall?>
    fun observeMenuItems(stallId: String): Flow<List<MenuItem>>
    suspend fun updateStallStatus(stallId: String, isOpen: Boolean): Result<Unit>
}
