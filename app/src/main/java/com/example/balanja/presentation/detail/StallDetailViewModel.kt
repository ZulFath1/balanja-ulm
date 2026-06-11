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
import com.example.balanja.domain.repository.AuthRepository

sealed interface StallDetailUiState {
    object Loading : StallDetailUiState
    data class Success(
        val stall: Stall, 
        val reviews: List<com.example.balanja.domain.model.Review> = emptyList(),
        val isOwner: Boolean = false
    ) : StallDetailUiState
    data class Error(val message: String) : StallDetailUiState
}

class StallDetailViewModel(
    private val getStallDetailUseCase: GetStallDetailUseCase,
    private val toggleStallStatusUseCase: ToggleStallStatusUseCase,
    private val getReviewsUseCase: com.example.balanja.domain.usecase.GetReviewsUseCase,
    private val isFavoriteUseCase: com.example.balanja.domain.usecase.IsFavoriteUseCase,
    private val addFavoriteUseCase: com.example.balanja.domain.usecase.AddFavoriteUseCase,
    private val deleteFavoriteUseCase: com.example.balanja.domain.usecase.DeleteFavoriteUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StallDetailUiState>(StallDetailUiState.Loading)
    val uiState: StateFlow<StallDetailUiState> = _uiState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    fun loadStall(stallId: String) {
        viewModelScope.launch {
            _uiState.value = StallDetailUiState.Loading

            // Listen to favorite status
            launch {
                isFavoriteUseCase(stallId).collect { fav ->
                    _isFavorite.value = fav
                }
            }

            getStallDetailUseCase(stallId)
                .catch { exception ->
                    _uiState.value = StallDetailUiState.Error(exception.message ?: "Gagal memuat detail stan")
                }
                .collect { stall ->
                    if (stall != null) {
                        getReviewsUseCase(stallId).collect { reviews ->
                            val isOwner = stall.ownerId.isNotEmpty() && stall.ownerId == authRepository.getCurrentUserId()
                            _uiState.value = StallDetailUiState.Success(stall, reviews, isOwner)
                        }
                    } else {
                        _uiState.value = StallDetailUiState.Error("Stan tidak ditemukan.")
                    }
                }
        }
    }

    fun toggleFavorite() {
        val currentState = _uiState.value
        if (currentState is StallDetailUiState.Success) {
            val stall = currentState.stall
            viewModelScope.launch {
                if (_isFavorite.value) {
                    deleteFavoriteUseCase(stall.id)
                } else {
                    val fav = com.example.balanja.domain.model.FavoriteStall(
                        stallId = stall.id,
                        name = stall.name,
                        imageUrl = stall.imageUrl,
                        location = stall.location,
                        ratingAverage = stall.rating,
                        priceMin = stall.priceMin.toInt(),
                        priceMax = stall.priceMax.toInt(),
                        isOpen = stall.isOpen,
                        savedAt = System.currentTimeMillis()
                    )
                    addFavoriteUseCase(fav)
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
                            stall = currentState.stall.copy(isOpen = newStatus),
                            reviews = currentState.reviews,
                            isOwner = currentState.isOwner
                        )
                    }
                }
                .onFailure {
                    // Jika gagal, Anda bisa menambahkan logika notifikasi error di sini
                }
        }
    }
}