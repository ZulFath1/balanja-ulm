package com.example.balanja.data.repository

import com.example.balanja.domain.model.User
import com.example.balanja.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AuthRepositoryImpl : AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://balanja-ulm-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

    override fun registerUser(
        name: String,
        email: String,
        password: String,
        role: String
    ): Flow<Result<User>> = callbackFlow {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                val userNode = mapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "role" to role
                )

                database.child("users").child(uid).setValue(userNode)
                    .addOnSuccessListener {
                        val newUser = User(uid, name, email, role)
                        trySend(Result.success(newUser))
                    }
                    .addOnFailureListener { exception ->
                        trySend(Result.failure(exception))
                    }
            }
            .addOnFailureListener { exception ->
                trySend(Result.failure(exception))
            }
        awaitClose()
    }

    override fun loginUser(email: String, password: String): Flow<Result<User>> = callbackFlow {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                database.child("users").child(uid).get()
                    .addOnSuccessListener { snapshot ->
                        val name = snapshot.child("name").value.toString()
                        val fetchedEmail = snapshot.child("email").value.toString()
                        val role = snapshot.child("role").value.toString()
                        val user = User(uid, name, fetchedEmail, role)
                        trySend(Result.success(user))
                    }
                    .addOnFailureListener { exception ->
                        trySend(Result.failure(exception))
                    }
            }
            .addOnFailureListener { exception ->
                trySend(Result.failure(exception))
            }
        awaitClose()
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(
            uid = firebaseUser.uid,
            name = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            role = ""
        )
    }

    override fun logout() {
        auth.signOut()
    }
}