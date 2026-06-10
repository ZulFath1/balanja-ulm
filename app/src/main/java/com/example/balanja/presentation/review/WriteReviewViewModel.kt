package com.example.balanja.presentation.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.AppContainer
import com.example.balanja.domain.model.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * State for WriteReview screen
 */
data class WriteReviewUiState(
    val stallId: String = "",
    val reviewId: String? = null,
    val rating: Int = 0,
    val comment: String = "",
    val selectedAttributes: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val existingReview: Review? = null
)

/**
 * ViewModel for WriteReview (BLJA-03: ULASAN) screen
 * 
 * Handles submission of text and star rating reviews to Firebase Realtime Database.
 * No photo upload capability as per Sprint 2 requirements.
 */
class WriteReviewViewModel(
    savedStateHandle: SavedStateHandle,
    private val reviewRepository: com.example.balanja.domain.repository.ReviewRepository = AppContainer.reviewRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WriteReviewUiState())
    val uiState: StateFlow<WriteReviewUiState> = _uiState.asStateFlow()
    
    init {
        val stallId = savedStateHandle.get<String>("stallId") ?: ""
        val reviewId = savedStateHandle.get<String>("reviewId")
        
        _uiState.value = _uiState.value.copy(
            stallId = stallId,
            reviewId = reviewId
        )
        
        // Load existing review if in edit mode
        if (reviewId != null) {
            loadReview(stallId, reviewId)
        }
    }
    
    private fun loadReview(stallId: String, reviewId: String) {
        // TODO: Load existing review from repository
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
    
    fun updateRating(rating: Int) {
        _uiState.value = _uiState.value.copy(rating = rating)
    }
    
    fun updateComment(comment: String) {
        _uiState.value = _uiState.value.copy(comment = comment)
    }
    
    fun toggleAttribute(attribute: String) {
        val currentAttributes = _uiState.value.selectedAttributes
        _uiState.value = _uiState.value.copy(
            selectedAttributes = if (currentAttributes.contains(attribute)) {
                currentAttributes - attribute
            } else {
                currentAttributes + attribute
            }
        )
    }
    
    fun submitReview() {
        val state = _uiState.value
        
        // Validation
        if (state.rating == 0) {
            _uiState.value = _uiState.value.copy(error = "Mohon berikan rating bintang")
            return
        }
        
        if (state.comment.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Mohon tuliskan ulasan Anda")
            return
        }
        
        _uiState.value = _uiState.value.copy(isSaving = true, error = null)
        
        viewModelScope.launch {
            try {
                val review = Review(
                    stallId = state.stallId,
                    rating = state.rating,
                    comment = state.comment,
                    attributes = state.selectedAttributes,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                
                // Save review to Realtime Database
                // TODO: Implement actual save logic
                
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successMessage = "Ulasan berhasil disimpan!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Gagal menyimpan ulasan: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}
