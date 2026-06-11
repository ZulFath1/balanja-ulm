package com.example.balanja

import android.app.Application

class BalanjaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            com.google.firebase.database.FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        } catch (e: Exception) {
            // Already initialized or another error
        }
        AppContainer.init(this)
    }
}