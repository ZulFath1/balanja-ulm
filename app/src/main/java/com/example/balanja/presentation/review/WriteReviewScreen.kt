package com.example.balanja.presentation.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import android.net.Uri
import coil.compose.AsyncImage
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import com.example.balanja.ui.component.PrimaryButton
import com.example.balanja.ui.theme.BalanjaColor
import com.example.balanja.ui.component.LocalSnackbarHostState
import kotlinx.coroutines.launch

/**
 * BLJA-03: ULASAN/REVIEW Screen (Write Review)
 *
 * Allows users to submit text-based reviews with star ratings.
 *
 * Requirements:
 * - Star rating selection (1-5)
 * - Comment/review text input
 * - Optional attribute tags
 * - NO photo upload button
 * - Save directly to Firebase Realtime Database
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteReviewScreen(
    navController: NavController,
    stallId: String,
    reviewId: String? = null,
    viewModel: WriteReviewViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 3),
        onResult = { uris -> 
            selectedImageUris = (selectedImageUris + uris).distinct().take(3)
        }
    )

    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            launch {
                snackbarHostState.showSnackbar("Ulasan berhasil dikirim!", withDismissAction = true)
            }
            kotlinx.coroutines.delay(1500)
            navController.popBackStack()
            // Setelah berhasil, arahkan langsung ke halaman daftar ulasan komunitas
            navController.navigate(com.example.balanja.ui.navigation.Screen.CommunityReview.createRoute(stallId))
        }
    }

    val attributeOptions = listOf(
        "Porsi Banyak",
        "Rasa Mantap",
        "Cepat",
        "Sesuai harga",
        "Pelayanan Baik",
        "Lokasi Strategis"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val goldStarColor = Color(0xFFFFC107) // Golden yellow stars stay gold
    val lightBackgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val selectedAttributeColor = MaterialTheme.colorScheme.primary
    val selectedAttributeBg = MaterialTheme.colorScheme.primaryContainer

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tulis Ulasan", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = primaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = lightBackgroundColor,
                    titleContentColor = textColor
                )
            )
        },
        containerColor = lightBackgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ulasan",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Normal,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Penilaian anda akan mempermudah orang lain",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Star Rating
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(5) { index ->
                        val starRating = index + 1
                        val isSelected = uiState.rating >= starRating
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Star $starRating",
                            tint = if (isSelected) goldStarColor else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { viewModel.updateRating(starRating) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Comment Section
            Text(
                text = "DETAILKAN PENGALAMAN ANDA",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = labelColor,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = uiState.comment,
                onValueChange = { viewModel.updateComment(it) },
                placeholder = { Text("ketikkan di dalam sini", color = Color.LightGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Attributes Section
            Text(
                text = "QUICK ATTRIBUTES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = labelColor,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
            androidx.compose.foundation.layout.FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                attributeOptions.forEach { attribute ->
                    val isSelected = uiState.selectedAttributes.contains(attribute)
                    Box(
                        modifier = Modifier
                            .clickable { viewModel.toggleAttribute(attribute) }
                            .background(
                                color = if (isSelected) selectedAttributeBg else MaterialTheme.colorScheme.surface,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) selectedAttributeColor else MaterialTheme.colorScheme.outlineVariant,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = attribute,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) selectedAttributeColor else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Photo Upload Section
            Text(
                text = "TAMBAHKAN FOTO (OPSIONAL)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = labelColor,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(selectedImageUris) { uri ->
                    Box {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Selected Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .background(Color.LightGray, androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                        )
                        IconButton(
                            onClick = { 
                                selectedImageUris = selectedImageUris.filter { it != uri }
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(24.dp)
                                .background(Color.Black.copy(alpha = 0.5f), androidx.compose.foundation.shape.CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Hapus", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                
                if (selectedImageUris.size < 3) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(Color(0xFFF3F4F6), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0xFFEFE8E6), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                                .clickable { 
                                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) 
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Upload Photo",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (selectedImageUris.isEmpty()) "Pilih Foto" else "Tambah Foto", 
                                    color = Color.Gray, 
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Error Message
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    fontSize = 12.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
                )
            }

            // Submit Button
            androidx.compose.material3.Button(
                onClick = { 
                    val images = selectedImageUris.mapNotNull { uri ->
                        val extension = context.contentResolver.getType(uri)?.split("/")?.lastOrNull() ?: "jpg"
                        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        if (bytes != null) bytes to extension else null
                    }
                    viewModel.submitReview(images) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = primaryColor
                ),
                enabled = !uiState.isSaving && uiState.rating > 0 && uiState.comment.isNotBlank()
            ) {
                if (uiState.isSaving) {
                    androidx.compose.material3.CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Kirim Ulasan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "▷", fontSize = 18.sp, fontWeight = FontWeight.Bold) // A simple play/arrow symbol
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
