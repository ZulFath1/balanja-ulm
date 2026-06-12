package com.example.balanja.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.balanja.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var editError by remember { mutableStateOf<String?>(null) }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Nama Profil") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Nama Baru") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (editError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = editError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newName.isNotBlank()) {
                            viewModel.updateProfileName(newName) { success, error ->
                                if (success) {
                                    showEditDialog = false
                                } else {
                                    editError = error
                                }
                            }
                        } else {
                            editError = "Nama tidak boleh kosong"
                        }
                    }
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Logout") },
            text = { Text("Apakah Anda yakin ingin keluar dari akun ini?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.logout {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }) {
                    Text("Keluar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Profil Saya", 
                        fontSize = 24.sp, 
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF111111)
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBF9F8))
            )
        },
        containerColor = Color(0xFFFBF9F8)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Header Profile Avatar
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .border(4.dp, Color(0xFFFEE2E2), CircleShape)
                    .padding(8.dp)
                    .background(Color(0xFFE5E7EB), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(50.dp),
                    tint = Color(0xFF9CA3AF)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = uiState.userName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111111)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { 
                            newName = uiState.userName
                            editError = null
                            showEditDialog = true 
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profil",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.email,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Menu Group 1: Ulasan, Favorit & Warung Saya
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Default.Star,
                        iconContainerColor = Color(0xFFFEF3C7),
                        iconTintColor = Color(0xFFF59E0B),
                        title = "Ulasan Saya",
                        onClick = { navController.navigate(Screen.MyReviews.route) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 1.dp,
                        color = Color(0xFFF3F4F6)
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.Favorite,
                        iconContainerColor = Color(0xFFFCE7F3),
                        iconTintColor = Color(0xFFEC4899),
                        title = "Stan Favorit",
                        onClick = { navController.navigate(Screen.Favorites.route) }
                    )
                    
                    if (uiState.ownedStalls.isNotEmpty()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 1.dp,
                            color = Color(0xFFF3F4F6)
                        )
                        ProfileMenuItem(
                            icon = Icons.Default.Settings,
                            iconContainerColor = Color(0xFFE0E7FF),
                            iconTintColor = Color(0xFF4F46E5),
                            title = "Warung Saya",
                            onClick = { 
                                if (uiState.ownedStalls.size == 1) {
                                    navController.navigate(Screen.StallDetail.createRoute(uiState.ownedStalls.first().id))
                                } else {
                                    navController.navigate("my_stalls")
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Menu Group 2: Keluar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    iconContainerColor = Color(0xFFFEE2E2),
                    iconTintColor = Color(0xFFEF4444),
                    title = "Keluar",
                    titleColor = Color(0xFFEF4444),
                    showArrow = false,
                    onClick = { showLogoutDialog = true }
                )
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    iconContainerColor: Color,
    iconTintColor: Color,
    title: String,
    titleColor: Color = Color(0xFF333333),
    showArrow: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconContainerColor, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTintColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = titleColor,
            modifier = Modifier.weight(1f)
        )
        if (showArrow) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Go",
                tint = Color(0xFF9CA3AF)
            )
        }
    }
}
