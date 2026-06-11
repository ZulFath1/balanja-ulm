package com.example.balanja.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.balanja.domain.model.MenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StallDetailScreen(
    viewModel: StallDetailViewModel,
    stallId: String,
    onNavigateBack: () -> Unit,
    onNavigateToReview: (String) -> Unit,
    onNavigateToMap: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()

    LaunchedEffect(stallId) {
        viewModel.loadStall(stallId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState is StallDetailUiState.Success) (uiState as StallDetailUiState.Success).stall.name else "Detail Stan", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBF9F8))
            )
        },
        bottomBar = {
            if (uiState is StallDetailUiState.Success) {
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    PaddingValues(16.dp)
                    Button(
                        onClick = { onNavigateToReview(stallId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF870500)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Tulis Ulasan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
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
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            StallStatusToggle(
                                isOpen = stall.isOpen,
                                onToggle = { newStatus ->
                                    viewModel.toggleStatus(stall.id, stall.isOpen)
                                }
                            )
                            AsyncImage(
                                model = stall.imageUrl,
                                contentDescription = "Foto ${stall.name}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                                    .background(Color.LightGray)
                            )

                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stall.name,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF111111),
                                        modifier = Modifier.weight(1f)
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .background(Color(0xFFFEF3C7), RoundedCornerShape(100.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Rating",
                                            tint = Color(0xFFF59E0B),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${stall.rating} (${stall.reviewCount})",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF111111)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = stall.location.uppercase(),
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    OutlinedButton(
                                        onClick = { onNavigateToMap(stall.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF870500)),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF870500)),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Place,
                                            contentDescription = "Map",
                                            modifier = Modifier.size(16.dp),
                                            tint = Color(0xFF870500)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Cek Lokasi", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Deskripsi",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stall.description,
                                    fontSize = 14.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "Daftar Menu",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        val menuList = stall.menu.values.toList()
                        if (menuList.isEmpty()) {
                            item {
                                Text(
                                    text = "Belum ada menu yang ditambahkan.",
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = Color.Gray,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        } else {
                            items(menuList) { menuItem ->
                                MenuItemRow(menuItem)
                            }
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Ulasan Komunitas",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        if (reviewsList.isEmpty()) {
                            item {
                                Text(
                                    text = "Jadilah yang pertama mengulas stan ini!",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    color = Color.Gray,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        } else {
                            items(reviewsList) { review ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = review.userName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = "Rating",
                                                    tint = Color(0xFFF59E0B),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = review.rating.toString(),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = review.comment,
                                            fontSize = 14.sp,
                                            color = Color.DarkGray
                                        )
                                        if (review.attributes.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                review.attributes.forEach { attr ->
                                                    Box(
                                                        modifier = Modifier
                                                            .background(Color(0xFFFEF3C7), RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(text = attr, fontSize = 10.sp, color = Color(0xFF870500))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItemRow(menuItem: MenuItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (menuItem.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = menuItem.imageUrl,
                    contentDescription = menuItem.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = menuItem.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
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
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Rp${menuItem.price / 1000}k",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF870500)
            )
        }
    }
}

@Composable
fun StallStatusToggle(
    isOpen: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = if (isOpen) "Warung Sedang BUKA" else "Warung Sedang TUTUP",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isOpen,
            onCheckedChange = { onToggle(it) }
        )
    }
}