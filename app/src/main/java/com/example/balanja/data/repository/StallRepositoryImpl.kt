package com.example.balanja.data.repository

import com.example.balanja.domain.model.Stall
import com.example.balanja.domain.repository.StallRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class StallRepositoryImpl(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) : StallRepository {

    private val stallsRef = database.getReference("stalls")

    override fun getAllStalls(): Flow<List<Stall>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stalls = mutableListOf<Stall>()
                for (childSnapshot in snapshot.children) {
                    val stall = childSnapshot.getValue(Stall::class.java)
                    if (stall != null) {
                        // Memastikan ID terisi dari key Firebase jika kosong
                        stalls.add(stall.copy(id = childSnapshot.key ?: stall.id))
                    }
                }
                trySend(stalls)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        stallsRef.addValueEventListener(listener)

        // Membersihkan listener ketika flow dibatalkan/ditutup
        awaitClose { stallsRef.removeEventListener(listener) }
    }

    override fun getStallById(stallId: String): Flow<Stall?> = callbackFlow {
        val stallRef = stallsRef.child(stallId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stall = snapshot.getValue(Stall::class.java)?.copy(id = snapshot.key ?: stallId)
                trySend(stall)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        stallRef.addValueEventListener(listener)
        awaitClose { stallRef.removeEventListener(listener) }
    }

    override suspend fun updateStallStatus(stallId: String, isOpen: Boolean): Result<Unit> {
        return try {
            stallsRef.child(stallId).child("isOpen").setValue(isOpen).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}