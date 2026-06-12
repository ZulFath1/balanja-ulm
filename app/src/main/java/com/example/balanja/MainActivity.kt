package com.example.balanja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.balanja.data.local.prefs.ThemePreferenceManager
import com.example.balanja.ui.navigation.AppNavigation
import com.example.balanja.ui.theme.BalanjaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeManager = ThemePreferenceManager.getInstance(this)
        setContent {
            val isDarkTheme by themeManager.isDarkMode.collectAsState()
            BalanjaTheme(darkTheme = isDarkTheme) {
                AppNavigation()
            }
        }
    }
}
