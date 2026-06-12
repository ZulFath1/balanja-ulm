package com.example.balanja.presentation.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.navigation.NavController
import com.example.balanja.domain.model.Review
import com.example.balanja.ui.component.PrimaryButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.balanja.ui.component.LocalSnackbarHostState
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReviewsScreen(
    navController: NavController,
    viewModel: MyReviewsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showZoomedImage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Ulasan Saya",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Kembali", 
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.reviews.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Belum ada ulasan yang Anda buat.", 
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        PrimaryButton(
                            text = "Jelajahi Stan",
                            onClick = { navController.navigate("home") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.reviews) { reviewWithStall ->
                            MyReviewItem(
                                reviewWithStall = reviewWithStall,
                                onEdit = { navController.navigate("write_review/${reviewWithStall.review.stallId}?reviewId=${reviewWithStall.review.id}") },
                                onDelete = { viewModel.deleteReview(reviewWithStall.review.stallId, reviewWithStall.review.id) },
                                onImageClick = { imageUrl -> showZoomedImage = imageUrl }
                            )
                        }
                    }
                }
            }
        }
        
        showZoomedImage?.let { imageUrl ->
            Dialog(
                onDismissRequest = { showZoomedImage = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.9f))
                        .clickable { showZoomedImage = null }
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Zoomed Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )
                    IconButton(
                        onClick = { showZoomedImage = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tutup", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun MyReviewItem(reviewWithStall: ReviewWithStall, onEdit: () -> Unit, onDelete: () -> Unit, onImageClick: (String) -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    val review = reviewWithStall.review

    val dateStr = if (review.createdAt > 0) {
        SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date(review.createdAt)).uppercase()
    } else {
        "01 JAN 2023"
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Ulasan?") },
            text = { Text("Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Ulasan berhasil dihapus!", withDismissAction = true)
                        kotlinx.coroutines.delay(1500)
                        onDelete()
                    }
                    showDeleteDialog = false
                }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                AsyncImage(
                    model = reviewWithStall.stallImageUrl.takeIf { it.isNotBlank() } ?: "https://ui-avatars.com/api/?name=${reviewWithStall.stallName.replace(" ", "+")}&background=random",
                    contentDescription = "Foto Toko",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reviewWithStall.stallName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    Text(
                        text = dateStr,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= review.rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "\"${review.comment}\"",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    val allImages = if (review.imageUrls.isNotEmpty()) review.imageUrls else listOfNotNull(review.imageUrl).filter { it.isNotBlank() }
                    if (allImages.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(allImages) { imgUrl ->
                                AsyncImage(
                                    model = imgUrl,
                                    contentDescription = "Foto Review",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onImageClick(imgUrl) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.End, 
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        TextButton(onClick = onEdit) {
                            Text("Ubah", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = { showDeleteDialog = true }) {
                            Text("Hapus", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
