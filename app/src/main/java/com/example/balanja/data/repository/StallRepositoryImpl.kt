package com.example.balanja.data.repository

import com.example.balanja.domain.model.MenuItem
import com.example.balanja.domain.model.Stall
import com.example.balanja.domain.repository.StallRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StallRepositoryImpl : StallRepository {
    override fun observeStalls(): Flow<List<Stall>> = flow { emit(emptyList()) }
    override fun observeStallById(stallId: String): Flow<Stall?> = flow { emit(null) }
    override fun observeMenuItems(stallId: String): Flow<List<MenuItem>> = flow { emit(emptyList()) }
    override suspend fun updateStallStatus(stallId: String, isOpen: Boolean): Result<Unit> = Result.success(Unit)
}
