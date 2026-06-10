package com.example.balanja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.balanja.ui.navigation.AppNavigation
import com.example.balanja.ui.theme.BalanjaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BalanjaTheme {
                AppNavigation()
            }
        }
    }
}