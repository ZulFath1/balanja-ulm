//package com.example.balanja.presentation.review
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Star
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextFieldDefaults
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.SavedStateHandle
//import androidx.navigation.NavController
//import com.example.balanja.ui.component.PrimaryButton
//import com.example.balanja.ui.theme.BalanjaColor
//
///**
// * BLJA-03: ULASAN/REVIEW Screen (Write Review)
// *
// * Allows users to submit text-based reviews with star ratings.
// *
// * Requirements:
// * - Star rating selection (1-5)
// * - Comment/review text input
// * - Optional attribute tags
// * - NO photo upload button
// * - Save directly to Firebase Realtime Database
// */
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun WriteReviewScreen(
//    navController: NavController,
//    stallId: String,
//    reviewId: String? = null,
//    viewModel: WriteReviewViewModel = WriteReviewViewModel(
//        savedStateHandle = androidx.lifecycle.SavedStateHandle().apply {
//            set("stallId", stallId)
//            set("reviewId", reviewId)
//        }
//    )
//) {
//    val uiState by viewModel.uiState.collectAsState()
//
//    val attributeOptions = listOf(
//        "Harga Terjangkau",
//        "Pelayanan Baik",
//        "Rasa Enak",
//        "Lokasi Strategis",
//        "Bersih",
//        "Porsi Besar"
//    )
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Tulis Ulasan") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = BalanjaColor.Primary
//                )
//            )
//        },
//        containerColor = Color(0xFFFBF9F8)
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//                .padding(innerPadding)
//                .padding(20.dp)
//        ) {
//            // Star Rating Section
//            Text(
//                text = "Rating Bintang",
//                fontSize = 14.sp,
//                fontWeight = FontWeight.SemiBold,
//                color = Color(0xFF333333),
//                modifier = Modifier.padding(bottom = 12.dp)
//            )
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 24.dp),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                repeat(5) { index ->
//                    val starRating = index + 1
//                    IconButton(
//                        onClick = { viewModel.updateRating(starRating) },
//                        modifier = Modifier
//                            .weight(1f)
//                            .background(
//                                color = if (uiState.rating >= starRating)
//                                    Color(0xFFFFC107)
//                                else
//                                    Color(0xFFE0E0E0),
//                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
//                            )
//                    ) {
//                        Icon(
//                            Icons.Filled.Star,
//                            contentDescription = "Star $starRating",
//                            tint = Color.White
//                        )
//                    }
//                }
//            }
//
//            if (uiState.rating > 0) {
//                Text(
//                    text = "Rating: ${uiState.rating} dari 5 bintang",
//                    fontSize = 12.sp,
//                    color = Color(0xFF666666),
//                    modifier = Modifier.padding(bottom = 16.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Comment Section
//            Text(
//                text = "Ulasan Anda",
//                fontSize = 14.sp,
//                fontWeight = FontWeight.SemiBold,
//                color = Color(0xFF333333),
//                modifier = Modifier.padding(bottom = 12.dp)
//            )
//
//            OutlinedTextField(
//                value = uiState.comment,
//                onValueChange = { viewModel.updateComment(it) },
//                placeholder = { Text("Tulis pengalaman Anda...") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(140.dp)
//                    .padding(bottom = 24.dp),
//                colors = TextFieldDefaults.outlinedTextFieldColors(
//                    focusedBorderColor = BalanjaColor.Primary,
//                    unfocusedBorderColor = Color(0xFFDDDDDD)
//                )
//            )
//
//            // Attributes Section
//            Text(
//                text = "Pilih Atribut (Opsional)",
//                fontSize = 14.sp,
//                fontWeight = FontWeight.SemiBold,
//                color = Color(0xFF333333),
//                modifier = Modifier.padding(bottom = 12.dp)
//            )
//
//            repeat((attributeOptions.size + 1) / 2) { row ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 12.dp),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    repeat(2) { col ->
//                        val index = row * 2 + col
//                        if (index < attributeOptions.size) {
//                            val attribute = attributeOptions[index]
//                            val isSelected = uiState.selectedAttributes.contains(attribute)
//
//                            Box(
//                                modifier = Modifier
//                                    .weight(1f)
//                                    .background(
//                                        color = if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
//                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
//                                    )
//                                    .padding(12.dp)
//                            ) {
//                                Text(
//                                    text = attribute,
//                                    fontSize = 12.sp,
//                                    color = if (isSelected) BalanjaColor.Primary else Color(0xFF666666),
//                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
//                                    modifier = Modifier
//                                        .align(Alignment.Center)
//                                        .let {
//                                            it.also {
//                                                // Make it clickable
//                                            }
//                                        }
//                                )
//                            }
//                        } else {
//                            Spacer(modifier = Modifier.weight(1f))
//                        }
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Error Message
//            if (uiState.error != null) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color(0xFFFFEBEE), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
//                        .padding(12.dp)
//                ) {
//                    Text(
//                        text = uiState.error!!,
//                        fontSize = 12.sp,
//                        color = Color(0xFFC62828)
//                    )
//                }
//                Spacer(modifier = Modifier.height(12.dp))
//            }
//
//            // Submit Button
//            PrimaryButton(
//                text = "Simpan Ulasan",
//                onClick = { viewModel.submitReview() },
//                isLoading = uiState.isSaving,
//                enabled = !uiState.isSaving && uiState.rating > 0 && uiState.comment.isNotBlank()
//            )
//        }
//    }
//}
