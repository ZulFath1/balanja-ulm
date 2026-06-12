package com.example.balanja.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.balanja.domain.model.MenuItem
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StallDetailScreen(
    viewModel: StallDetailViewModel,
    stallId: String,
    onNavigateBack: () -> Unit,
    onNavigateToReview: (String) -> Unit,
    onNavigateToMap: (String) -> Unit,
    onNavigateToCommunityReview: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()

    LaunchedEffect(stallId) {
        viewModel.loadStall(stallId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Detail Tempat", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color(0xFF870500)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color(0xFF870500))
                    }
                },
                actions = {
                    if (uiState is StallDetailUiState.Success) {
                        IconButton(onClick = { viewModel.toggleFavorite() }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorit",
                                tint = if (isFavorite) Color.Red else Color.Gray
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            if (uiState is StallDetailUiState.Success) {
                ExtendedFloatingActionButton(
                    onClick = { onNavigateToReview(stallId) },
                    containerColor = Color(0xFF870500),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                    icon = { Icon(Icons.Default.RateReview, "Tulis Ulasan") },
                    text = { Text("Tulis Ulasan", fontWeight = FontWeight.Bold) }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = Color(0xFFFBF9F8)
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            val currentState = uiState
            when (currentState) {
                is StallDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF870500)
                    )
                }
                is StallDetailUiState.Error -> {
                    Text(
                        text = currentState.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is StallDetailUiState.Success -> {
                    val stall = currentState.stall
                    val reviewsList = currentState.reviews

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp) // space for FAB
                    ) {
                        item {
                            // Hero Image with Floating Card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                            ) {
                                // Background Image & Gradient overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                ) {
                                    AsyncImage(
                                        model = stall.imageUrl,
                                        contentDescription = "Foto ${stall.name}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    // Gradient overlay from transparent to dark
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                                                    startY = 100f
                                                )
                                            )
                                    )
                                    // Overlaid Text
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(start = 24.dp, end = 24.dp, bottom = 120.dp) // Leave space for floating card
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Place, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(stall.location.uppercase(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = stall.name,
                                            color = Color.White,
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 32.sp
                                        )
                                    }
                                }

                                // Floating Card
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                        .align(Alignment.BottomCenter)
                                        .offset(y = 40.dp), // Move it down to overlap the bottom edge
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        // Actions Row
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            // Tag "BUKA" / "TUTUP"
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        color = if (stall.isOpen) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = if (stall.isOpen) "BUKA" else "TUTUP",
                                                    color = if (stall.isOpen) Color(0xFF2E7D32) else Color(0xFFC62828),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                            }
                                            
                                            Spacer(modifier = Modifier.width(8.dp))
                                            
                                            // Rating Button -> Scroll to bottom
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { onNavigateToCommunityReview(stall.id) }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("${stall.rating} (${stall.reviewCount})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            // Map Button
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { onNavigateToMap(stall.id) }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF870500), modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Lokasi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF870500))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("ABOUT THE STALL", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color.Gray, letterSpacing = 1.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "\"${stall.description}\"",
                                            fontSize = 14.sp,
                                            fontStyle = FontStyle.Italic,
                                            fontFamily = FontFamily.Serif,
                                            color = Color(0xFF555555),
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                            // Spacer to account for the overlapping card's offset
                            Spacer(modifier = Modifier.height(64.dp))
                        }

                        // Menu Categories
                        val menuList = stall.menu.values.toList()
                        val drinkKeywords = listOf("es", "teh", "kopi", "jus", "air", "minuman", "milk", "boba", "nutrisari", "sirup", "soda")
                        val (drinks, foods) = menuList.partition { item -> 
                            drinkKeywords.any { item.name.lowercase().contains(it) }
                        }

                        if (foods.isNotEmpty()) {
                            item {
                                MenuSectionHeader("Menu")
                            }
                            items(foods) { item ->
                                MenuItemRow(item, isDrink = false)
                            }
                        }

                        if (drinks.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                MenuSectionHeader("Refreshments")
                            }
                            items(drinks) { item ->
                                MenuItemRow(item, isDrink = true)
                            }
                        }
                        
                        if (menuList.isEmpty()) {
                            item {
                                Text(
                                    text = "Belum ada menu yang ditambahkan.",
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
                                    color = Color.Gray,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            if (currentState.isOwner) {
                                StallStatusToggle(
                                    isOpen = stall.isOpen,
                                    onToggle = { newStatus -> viewModel.toggleStatus(stall.id, stall.isOpen) }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MenuSectionHeader(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF111111)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Color.LightGray.copy(alpha = 0.5f))
        )
    }
}

@Composable
fun MenuItemRow(menuItem: MenuItem, isDrink: Boolean = false) {
    val priceStr = "Rp" + NumberFormat.getNumberInstance(Locale("id", "ID")).format(menuItem.price)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image or Placeholder
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF3F4F6)),
            contentAlignment = Alignment.Center
        ) {
            if (menuItem.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = menuItem.imageUrl,
                    contentDescription = menuItem.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = if (isDrink) Icons.Outlined.LocalDrink else Icons.Outlined.Restaurant,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = menuItem.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111111),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (menuItem.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = menuItem.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = priceStr,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF870500)
        )
    }
}

@Composable
fun StallStatusToggle(
    isOpen: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOpen) Color(0xFFF1F8E9) else Color(0xFFFFEBEE)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Subtle flat look
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(if (isOpen) Color(0xFFC8E6C9) else Color(0xFFFFCDD2), shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = if (isOpen) Color(0xFF2E7D32) else Color(0xFFC62828),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isOpen) "Warung Sedang BUKA" else "Warung Sedang TUTUP",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = if (isOpen) Color(0xFF2E7D32) else Color(0xFFB71C1C)
                )
                Text(
                    text = "Sebagai pemilik, Anda bisa mengubah status warung.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    lineHeight = 14.sp
                )
            }
            Switch(
                checked = isOpen,
                onCheckedChange = { onToggle(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF2E7D32),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFC62828),
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}