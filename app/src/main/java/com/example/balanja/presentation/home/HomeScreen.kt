package com.example.balanja.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balanja.ui.component.StallCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    // Membaca state dari ViewModel (survive rotasi layar)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        // Sapaan
                        Text(
                            text = "Selamat Pagi!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        // Headline dengan kata "Balanja" yang di-styling khusus
                        Text(
                            text = buildAnnotatedString {
                                append("Mau ")
                                withStyle(style = SpanStyle(color = Color(0xFF870500), fontWeight = FontWeight.ExtraBold, fontStyle = FontStyle.Italic)) {
                                    append("Balanja")
                                }
                                append(" apa hari ini?")
                            },
                            fontSize = 20.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBF9F8))
            )
        },
        containerColor = Color(0xFFFBF9F8)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // --- Slot Placeholder Widget Cuaca (Tugas BLJA-09) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Memuat cuaca kampus...", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- Menampilkan Konten Berdasarkan State ---
            when (uiState) {
                is HomeUiState.Loading -> {
                    // Tampilan saat data sedang diambil dari Firebase
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF870500))
                    }
                }
                is HomeUiState.Error -> {
                    // Tampilan jika terjadi error (koneksi terputus, dll)
                    val errorMessage = (uiState as HomeUiState.Error).message
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = errorMessage, color = Color.Red)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.fetchStalls() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF870500))
                            ) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
                is HomeUiState.Success -> {
                    // Tampilan daftar stan jika berhasil dimuat
                    val stalls = (uiState as HomeUiState.Success).stalls
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp) // Memberi jarak agar tidak tertutup Bottom Navigation nanti
                    ) {
                        items(stalls) { stall ->
                            StallCard(
                                stall = stall,
                                onClick = { stallId -> onNavigateToDetail(stallId) }
                            )
                        }
                    }
                }
            }
        }
    }
}