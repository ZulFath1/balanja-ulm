package com.example.balanja.domain.repository

import com.example.balanja.domain.model.User

interface AuthRepository {
    suspend fun signUp(email: String, password: String): Result<User>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    fun signOut()
    fun getCurrentUserId(): String?
    fun getCurrentUser(): User?
    fun isLoggedIn(): Boolean
    suspend fun updateProfileName(newName: String): Result<Unit>
}
