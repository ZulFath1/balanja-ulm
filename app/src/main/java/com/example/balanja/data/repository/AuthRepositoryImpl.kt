package com.example.balanja.data.repository

import com.example.balanja.domain.model.User
import com.example.balanja.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {
    
    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                val user = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    name = firebaseUser.displayName ?: email.split("@")[0],
                    createdAt = firebaseUser.metadata?.creationTimestamp ?: 0L
                )
                Result.success(user)
            } else {
                Result.failure(Exception("Autentikasi gagal: User tidak ditemukan"))
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(Exception("Akun tidak ditemukan. Periksa email Anda."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("Email atau kata sandi salah."))
        } catch (e: Exception) {
            Result.failure(Exception("Gagal login: ${e.message}"))
        }
    }

    
    
    override fun signOut() {
        firebaseAuth.signOut()
    }
    
    override fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }
    
    override fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}
