package com.example.balanja.presentation.review

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.domain.model.Review
import com.example.balanja.domain.usecase.GetReviewsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CommunityReviewUiState {
    object Loading : CommunityReviewUiState
    data class Success(val reviews: List<Review>) : CommunityReviewUiState
    data class Error(val message: String) : CommunityReviewUiState
}

class CommunityReviewViewModel(
    private val getReviewsUseCase: GetReviewsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<CommunityReviewUiState>(CommunityReviewUiState.Loading)
    val uiState: StateFlow<CommunityReviewUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        Log.d("Lifecycle", "CommunityReviewViewModel - INIT")
    }

    fun loadReviews(stallId: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = CommunityReviewUiState.Loading
            getReviewsUseCase(stallId).collect { reviews ->
                _uiState.value = CommunityReviewUiState.Success(reviews)
            }
        }
    }

    override fun onCleared() {
        Log.d("Lifecycle", "CommunityReviewViewModel - ON CLEARED")
        super.onCleared()
    }
}
