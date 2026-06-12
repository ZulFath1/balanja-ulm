package com.example.balanja.data.local.prefs

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemePreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    
    private val _isDarkMode = MutableStateFlow(prefs.getBoolean(KEY_DARK_MODE, false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleTheme(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, isDark).apply()
        _isDarkMode.value = isDark
    }

    companion object {
        private const val KEY_DARK_MODE = "dark_mode_enabled"
        
        @Volatile
        private var instance: ThemePreferenceManager? = null

        fun getInstance(context: Context): ThemePreferenceManager {
            return instance ?: synchronized(this) {
                instance ?: ThemePreferenceManager(context.applicationContext).also { instance = it }
            }
        }
    }
}

