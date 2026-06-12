package com.example.balanja.presentation.stall

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel

/**
 * ViewModel for the AddStall (BLJA-05: TAMBAH PEDAGANG) screen.
 * This screen provides information about proposing a new stall/vendor
 * and opens an external Google Form for submission.
 */
class AddStallViewModel : ViewModel() {
    
    /**
     * Opens the Google Form for stall proposal submission
     */
    fun openGoogleForm(context: Context) {
        val googleFormUrl = "https://forms.gle/QpvDUyHWbRxnjDjc7"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(googleFormUrl)
        }
        context.startActivity(intent)
    }
}

