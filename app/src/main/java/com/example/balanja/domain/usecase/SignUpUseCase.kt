package com.example.balanja.domain.usecase

import com.example.balanja.domain.model.User
import com.example.balanja.domain.repository.AuthRepository

class SignUpUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Email dan kata sandi tidak boleh kosong"))
        }
        
        // Validasi domain email ULM
        if (!email.endsWith("@mhs.ulm.ac.id") && !email.endsWith("@ulm.ac.id")) {
            return Result.failure(Exception("Hanya email dengan domain @mhs.ulm.ac.id atau @ulm.ac.id yang diizinkan untuk mendaftar."))
        }
        
        return authRepository.signUp(email, password)
    }
}
