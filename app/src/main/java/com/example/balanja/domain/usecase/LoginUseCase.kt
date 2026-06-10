package com.example.balanja.domain.usecase

import com.example.balanja.domain.model.User
import com.example.balanja.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginUseCase(private val repository: AuthRepository) {
    operator fun invoke(email: String, password: String): Flow<Result<User>> {
        if (email.isBlank() || password.isBlank()) {
            return flow { emit(Result.failure(Exception("Email dan password wajib diisi"))) }
        }
        return repository.loginUser(email, password)
    }
}