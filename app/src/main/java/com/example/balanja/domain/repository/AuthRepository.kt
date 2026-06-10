package com.example.balanja.domain.repository

import com.example.balanja.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun registerUser(name: String, email: String, password: String, role: String): Flow<Result<User>>
    fun loginUser(email: String, password: String): Flow<Result<User>>
    fun getCurrentUser(): User?
    fun logout()
}