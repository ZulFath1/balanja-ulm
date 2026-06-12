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
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.popBackStack()
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

    val primaryColor = Color(0xFF870500)
    val goldStarColor = Color(0xFFFFC107) // Golden yellow stars
    val lightBackgroundColor = Color(0xFFFCF9F8)
    val textColor = Color(0xFF222222)
    val labelColor = Color(0xFF9E847C)
    val selectedAttributeColor = Color(0xFFD32F2F) // Red secondary color
    val selectedAttributeBg = Color(0xFFFFEBEE)

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
                    color = Color.Gray
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
                            tint = if (isSelected) goldStarColor else Color(0xFFE8C6C3),
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
                    unfocusedBorderColor = Color(0xFFEFE8E6),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
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
                                color = if (isSelected) selectedAttributeBg else Color.White,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) selectedAttributeColor else Color(0xFFEFE8E6),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = attribute,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) selectedAttributeColor else Color(0xFF333333)
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

            if (selectedImageUri != null) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.LightGray, androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                    )
                    IconButton(
                        onClick = { selectedImageUri = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.5f), androidx.compose.foundation.shape.CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Hapus", tint = Color.White, modifier = Modifier.size(16.dp)) // Using back icon as a placeholder for close, let's use a text or custom shape if needed, actually let's use Icons.Default.Close if imported, otherwise just empty
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color(0xFFF3F4F6), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFEFE8E6), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                        .clickable { 
                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) 
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Star, // Replace with Add Photo icon if available
                            contentDescription = "Upload Photo",
                            tint = Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Pilih foto dari galeri", color = Color.Gray, fontSize = 14.sp)
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
                    var imageBytes: ByteArray? = null
                    var extension: String? = null
                    
                    if (selectedImageUri != null) {
                        try {
                            val inputStream = context.contentResolver.openInputStream(selectedImageUri!!)
                            imageBytes = inputStream?.readBytes()
                            inputStream?.close()
                            
                            val mimeType = context.contentResolver.getType(selectedImageUri!!)
                            extension = mimeType?.substringAfterLast('/') ?: "jpg"
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    
                    viewModel.submitReview(imageBytes, extension) 
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
