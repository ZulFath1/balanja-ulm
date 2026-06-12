package com.example.balanja.domain.usecase.auth

import com.example.balanja.domain.model.User
import com.example.balanja.domain.repository.AuthRepository

class SignInWithGoogleUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(idToken: String): Result<User> {
        return authRepository.signInWithGoogle(idToken)
    }
}

