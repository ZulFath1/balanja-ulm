package com.example.balanja.domain.usecase

import com.example.balanja.domain.model.User
import com.example.balanja.domain.repository.AuthRepository

class SignInUseCase(
    private val authRepository: AuthRepository
) {
    /**
     * Memanggil fungsi signIn dari AuthRepository.
     * Menggunakan operator 'invoke' agar class ini bisa dipanggil layaknya sebuah fungsi.
     */
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Email dan kata sandi tidak boleh kosong."))
        }

        return authRepository.signIn(email, password)
    }
}