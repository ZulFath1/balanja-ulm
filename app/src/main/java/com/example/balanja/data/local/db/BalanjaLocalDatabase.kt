package com.example.balanja.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.balanja.data.local.dao.FavoriteStallDao
import com.example.balanja.data.local.dao.RecentSearchDao
import com.example.balanja.data.local.entity.FavoriteStallEntity
import com.example.balanja.data.local.entity.RecentSearchEntity

@Database(entities = [FavoriteStallEntity::class, RecentSearchEntity::class], version = 2, exportSchema = false)
abstract class BalanjaLocalDatabase : RoomDatabase() {
    abstract fun favoriteStallDao(): FavoriteStallDao
    abstract fun recentSearchDao(): RecentSearchDao

    companion object {
        @Volatile private var INSTANCE: BalanjaLocalDatabase? = null
        
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `recent_searches` (`stallId` TEXT NOT NULL, `name` TEXT NOT NULL, `location` TEXT NOT NULL, `priceRange` TEXT NOT NULL, `photoUrl` TEXT NOT NULL, `averageRating` REAL NOT NULL, `isOpen` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`stallId`))")
            }
        }

        fun getInstance(context: Context): BalanjaLocalDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext, 
                    BalanjaLocalDatabase::class.java, 
                    "balanja_db"
                )
                .addMigrations(MIGRATION_1_2)
                .build().also { INSTANCE = it }
            }
        }
    }
}

