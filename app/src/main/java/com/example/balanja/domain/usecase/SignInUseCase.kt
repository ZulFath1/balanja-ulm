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

        // Validasi domain email ULM
        if (!email.endsWith("@mhs.ulm.ac.id") && !email.endsWith("@ulm.ac.id")) {
            return Result.failure(Exception("Hanya email dengan domain @mhs.ulm.ac.id atau @ulm.ac.id yang diizinkan untuk masuk."))
        }

        return authRepository.signIn(email, password)
    }
}