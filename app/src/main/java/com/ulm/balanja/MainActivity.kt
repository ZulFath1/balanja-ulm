package com.ulm.balanja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ulm.balanja.ui.theme.BalanjaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BalanjaTheme {
                // AppNavigation() akan diisi di BLJA-DEV-05
            }
        }
    }
}