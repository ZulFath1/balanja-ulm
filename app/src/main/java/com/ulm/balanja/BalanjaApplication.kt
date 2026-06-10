package com.ulm.balanja

import android.app.Application

class BalanjaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase persistence akan diaktifkan di Sprint 5 (BLJA-FIX-05)
        // Siapkan class-nya dari sekarang agar tidak perlu refactor nanti
    }
}