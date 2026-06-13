package com.example.balanja.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Selamat Datang di Balanja ULM",
        description = "Temukan berbagai jajanan dan kantin tersembunyi di sekitar lingkungan Universitas Lambung Mangkurat dengan mudah.",
        icon = Icons.Default.Place
    ),
    OnboardingPage(
        title = "Status Real-time",
        description = "Ketahui apakah warung sedang Buka atau Tutup sebelum Anda berjalan jauh ke sana. Menghemat waktu istirahat Anda!",
        icon = Icons.Default.Info
    ),
    OnboardingPage(
        title = "Ulasan & Penilaian",
        description = "Beri ulasan dan cari tahu makanan paling direkomendasikan berdasarkan rating jujur dari sesama mahasiswa.",
        icon = Icons.Default.Star
    )
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { position ->
            PagerScreen(onboardingPage = onboardingPages[position])
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pager Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(onboardingPages.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    val width = if (pagerState.currentPage == iteration) 24.dp else 12.dp
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(color)
                            .height(12.dp)
                            .width(width)
                    )
                }
            }

            // Buttons
            if (pagerState.currentPage == onboardingPages.size - 1) {
                Button(onClick = onFinishOnboarding) {
                    Text("Mulai Jajan!")
                }
            } else {
                TextButton(onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }) {
                    Text("Selanjutnya")
                }
            }
        }
    }
}

@Composable
fun PagerScreen(onboardingPage: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = onboardingPage.icon,
            contentDescription = onboardingPage.title,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = onboardingPage.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = onboardingPage.description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
