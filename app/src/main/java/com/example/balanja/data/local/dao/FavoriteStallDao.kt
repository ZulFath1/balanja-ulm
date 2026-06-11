package com.example.balanja.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.balanja.data.local.entity.FavoriteStallEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteStallDao {
    @Query("SELECT * FROM favorite_stalls ORDER BY savedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteStallEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(stall: FavoriteStallEntity): @JvmSuppressWildcards Long

    @Query("DELETE FROM favorite_stalls WHERE stallId = :stallId")
    suspend fun deleteFavorite(stallId: String): @JvmSuppressWildcards Int

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_stalls WHERE stallId = :stallId)")
    fun isFavorite(stallId: String): Flow<Boolean>
}
