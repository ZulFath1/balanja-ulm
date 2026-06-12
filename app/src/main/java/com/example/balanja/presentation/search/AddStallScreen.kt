package com.example.balanja.presentation.search

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balanja.ui.component.PrimaryButton

/**
 * BLJA-05: TAMBAH PEDAGANG Screen
 * 
 * This screen allows users to propose a new stall/vendor to be added to the platform.
 * The submission is done via an external Google Form instead of direct upload.
 */
@Composable
fun AddStallScreen(
    viewModel: AddStallViewModel = AddStallViewModel()
) {
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFBF9F8))
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Tambah Pedagang",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF111111)
                )
            }
        },
        containerColor = Color(0xFFFBF9F8)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Icon Background
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFFFEF2F2), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Store,
                    contentDescription = "Toko",
                    tint = Color(0xFF870500),
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Punya Rekomendasi Warung?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111111),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Kami selalu ingin memperluas jangkauan! Jika Anda mengetahui warung atau pedagang kaki lima di sekitar kampus ULM yang belum terdaftar di aplikasi Balanja ULM, ayo beritahu kami.\n\nIsi formulir singkat dengan informasi pedagang tersebut. Tim kami akan segera meninjaunya agar mahasiswa lain juga bisa menikmatinya!",
                fontSize = 15.sp,
                lineHeight = 24.sp,
                color = Color(0xFF555555),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Submit Button
            PrimaryButton(
                text = "Buka Formulir Pengajuan",
                onClick = { viewModel.openGoogleForm(context) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
