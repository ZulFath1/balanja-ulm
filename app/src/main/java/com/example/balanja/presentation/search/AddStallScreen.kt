package com.example.balanja.presentation.search

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balanja.ui.component.PrimaryButton

/**
 * BLJA-05: TAMBAH PEDAGANG Screen
 * 
 * This screen allows users to propose a new stall/vendor to be added to the platform.
 * The submission is done via an external Google Form instead of direct upload.
 * 
 * Requirements:
 * - Display informational text about submitting stall proposals
 * - Provide a button to open the Google Form in external browser
 * - No GPS tracking, camera, or image uploads required
 */
@Composable
fun AddStallScreen(
    viewModel: AddStallViewModel = AddStallViewModel()
) {
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF9F8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "Tambah Pedagang",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Information Text
            Text(
                text = "Ingin menambahkan pedagang atau warung baru di Balanja ULM?",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF555555),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Kami mengundang Anda untuk memberikan saran tentang pedagang atau warung makan yang baru di sekitar kampus ULM Banjarmasin. " +
                        "Saran Anda sangat membantu kami untuk memperluas dan memperbaiki database toko kami.\n\n" +
                        "Mohon isi formulir di bawah ini dengan informasi lengkap tentang pedagang yang ingin Anda tambahkan. " +
                        "Tim kami akan meninjau setiap saran dan menggunakannya untuk meningkatkan layanan Balanja ULM.",
                fontSize = 14.sp,
                color = Color(0xFF666666),
                lineHeight = 22.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Submit Button
            PrimaryButton(
                text = "Buka Formulir Pengajuan",
                onClick = { viewModel.openGoogleForm(context) },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
