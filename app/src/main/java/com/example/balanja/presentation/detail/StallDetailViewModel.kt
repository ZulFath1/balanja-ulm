package com.example.balanja.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.domain.model.Stall
import com.example.balanja.domain.usecase.GetStallDetailUseCase
import com.example.balanja.domain.usecase.ToggleStallStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface StallDetailUiState {
    object Loading : StallDetailUiState
    data class Success(val stall: Stall, val reviews: List<com.example.balanja.domain.model.Review> = emptyList()) : StallDetailUiState
    data class Error(val message: String) : StallDetailUiState
}

class StallDetailViewModel(
    private val getStallDetailUseCase: GetStallDetailUseCase,
    private val toggleStallStatusUseCase: ToggleStallStatusUseCase,
    private val getReviewsUseCase: com.example.balanja.domain.usecase.GetReviewsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<StallDetailUiState>(StallDetailUiState.Loading)
    val uiState: StateFlow<StallDetailUiState> = _uiState.asStateFlow()

    fun loadStall(stallId: String) {
        viewModelScope.launch {
            _uiState.value = StallDetailUiState.Loading

            getStallDetailUseCase(stallId)
                .catch { exception ->
                    _uiState.value = StallDetailUiState.Error(exception.message ?: "Gagal memuat detail stan")
                }
                .collect { stall ->
                    if (stall != null) {
                        getReviewsUseCase(stallId).collect { reviews ->
                            _uiState.value = StallDetailUiState.Success(stall, reviews)
                        }
                    } else {
                        _uiState.value = StallDetailUiState.Error("Stan tidak ditemukan.")
                    }
                }
        }
    }

    fun toggleStatus(stallId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            val newStatus = !currentStatus
            toggleStallStatusUseCase(stallId, newStatus)
                .onSuccess {
                    val currentState = _uiState.value
                    if (currentState is StallDetailUiState.Success) {
                        _uiState.value = StallDetailUiState.Success(
                            currentState.stall.copy(isOpen = newStatus)
                        )
                    }
                }
                .onFailure {
                    // Jika gagal, Anda bisa menambahkan logika notifikasi error di sini
                }
        }
    }
}