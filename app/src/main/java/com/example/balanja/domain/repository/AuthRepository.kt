package com.example.balanja.domain.repository

import com.example.balanja.domain.model.User

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<User>
    fun signOut()
    fun getCurrentUserId(): String?
    fun getCurrentUser(): User?
    fun isLoggedIn(): Boolean
}
