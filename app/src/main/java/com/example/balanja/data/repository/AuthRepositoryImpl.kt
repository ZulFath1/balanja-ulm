package com.example.balanja.data.repository

import com.example.balanja.domain.model.User
import com.example.balanja.domain.repository.AuthRepository

class AuthRepositoryImpl : AuthRepository {
    override suspend fun signIn(email: String, password: String): Result<User> = Result.success(User())
    override fun signOut() {}
    override fun getCurrentUserId(): String? = null
    override fun isLoggedIn(): Boolean = false
}
