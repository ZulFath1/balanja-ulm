package com.example.balanja.domain.usecase

import com.example.balanja.domain.model.User
import com.example.balanja.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RegisterUseCase(private val repository: AuthRepository) {
    operator fun invoke(name: String, email: String, password: String, role: String): Flow<Result<User>> {
        if (name.isBlank() || email.isBlank() || password.isBlank() || role.isBlank()) {
            return flow { emit(Result.failure(Exception("Semua kolom wajib diisi"))) }
        }
        if (password.length < 6) {
            return flow { emit(Result.failure(Exception("Password minimal harus 6 karakter"))) }
        }
        return repository.registerUser(name, email, password, role)
    }
}