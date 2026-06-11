package com.example.balanja.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.balanja.data.local.dao.FavoriteStallDao
import com.example.balanja.data.local.entity.FavoriteStallEntity

@Database(entities = [FavoriteStallEntity::class], version = 1, exportSchema = false)
abstract class BalanjaLocalDatabase : RoomDatabase() {
    abstract fun favoriteStallDao(): FavoriteStallDao

    companion object {
        @Volatile private var INSTANCE: BalanjaLocalDatabase? = null
        
        fun getInstance(context: Context): BalanjaLocalDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext, 
                    BalanjaLocalDatabase::class.java, 
                    "balanja_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
