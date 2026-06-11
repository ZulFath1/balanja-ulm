package com.example.balanja.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.domain.model.User
import com.example.balanja.domain.usecase.SignInUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Representasi status UI untuk proses Autentikasi.
 * Menggunakan sealed interface agar penanganan state di Jetpack Compose lebih terstruktur.
 */
sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: User) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)

    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /**
     * Fungsi untuk memicu proses login.
     */
    fun signIn(email: String, password: String) {
        // Mengubah state menjadi Loading saat proses dimulai
        _uiState.value = AuthUiState.Loading

        viewModelScope.launch {
            val result = signInUseCase(email, password)

            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user)
            }.onFailure { exception ->
                _uiState.value = AuthUiState.Error(exception.message ?: "Terjadi kesalahan yang tidak diketahui")
            }
        }
    }

    /**
     * Fungsi opsional untuk mengembalikan state ke Idle jika pengguna
     * ingin menghapus pesan kesalahan atau mencoba ulang.
     */
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}