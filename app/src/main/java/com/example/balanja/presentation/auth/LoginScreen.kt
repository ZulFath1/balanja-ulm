package com.example.balanja.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balanja.ui.component.AuthFooter
import com.example.balanja.ui.component.BalanjaTextField

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val goldColor = Color(0xFF836F1E)
    val backgroundColor = MaterialTheme.colorScheme.background

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onNavigateToHome()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Balanja",
            fontSize = 42.sp,
            fontWeight = FontWeight.ExtraBold,
            color = primaryColor
        )

        Spacer(modifier = Modifier.height(48.dp))


        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Masukkan email ULM.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            BalanjaTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Masukkan email ULM.",
                leadingIcon = Icons.Default.School,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Kata Sandi",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            BalanjaTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Kata Sandi",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                isPasswordVisible = isPasswordVisible,
                onVisibilityToggle = { isPasswordVisible = !isPasswordVisible },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (email.isNotBlank() && password.isNotBlank()) viewModel.signIn(email, password)
                    }
                )
            )
        }

        if (uiState is AuthUiState.Error) {
            Text(
                text = (uiState as AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.signIn(email, password) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
            enabled = uiState !is AuthUiState.Loading && email.isNotBlank() && password.isNotBlank()
        ) {
            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text(text = "Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Akun ULM kamu belum terdaftar? Daftar di sini",
            color = primaryColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onNavigateToRegister() }
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}
// Removed duplicated BalanjaTextField, we should use the one in ui.component but since its signature differs, I will update the ui.component one. Wait, if I remove this, LoginScreen will use the one in ui.component which expects label, etc. I need to keep it temporarily or update ui.component.BalanjaTextField first. I will keep it here for a moment and only remove the footer for now.