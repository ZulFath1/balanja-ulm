package com.example.balanja.presentation.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.domain.model.Review
import com.example.balanja.domain.repository.AuthRepository
import com.example.balanja.domain.usecase.AddReviewUseCase
import com.example.balanja.domain.usecase.EditReviewUseCase
import com.example.balanja.domain.usecase.GetReviewsUseCase
import com.example.balanja.domain.usecase.RecalculateStallRatingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WriteReviewUiState(
    val rating: Int = 0,
    val comment: String = "",
    val selectedAttributes: List<String> = emptyList(),
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class WriteReviewViewModel(
    private val stallId: String,
    private val reviewId: String?,
    private val addReviewUseCase: AddReviewUseCase,
    private val editReviewUseCase: EditReviewUseCase,
    private val getReviewsUseCase: GetReviewsUseCase,
    private val recalculateStallRatingUseCase: RecalculateStallRatingUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriteReviewUiState())
    val uiState: StateFlow<WriteReviewUiState> = _uiState.asStateFlow()

    init {
        if (reviewId != null) {
            loadExistingReview(stallId, reviewId)
        }
    }

    private fun loadExistingReview(stallId: String, reviewId: String) {
        viewModelScope.launch {
            val reviews = getReviewsUseCase(stallId).firstOrNull()
            val review = reviews?.find { it.id == reviewId }
            review?.let {
                _uiState.update { state ->
                    state.copy(
                        rating = it.rating,
                        comment = it.comment,
                        selectedAttributes = it.attributes
                    )
                }
            }
        }
    }

    fun updateRating(rating: Int) {
        _uiState.update { it.copy(rating = rating) }
    }

    fun updateComment(comment: String) {
        _uiState.update { it.copy(comment = comment) }
    }

    fun toggleAttribute(attribute: String) {
        _uiState.update { state ->
            val current = state.selectedAttributes.toMutableList()
            if (current.contains(attribute)) {
                current.remove(attribute)
            } else {
                current.add(attribute)
            }
            state.copy(selectedAttributes = current)
        }
    }

    fun submitReview() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            val currentUserUid = authRepository.getCurrentUserId()
            if (currentUserUid == null) {
                _uiState.update { it.copy(isSaving = false, error = "User not logged in") }
                return@launch
            }
            
            val review = Review(
                userId = currentUserUid,
                userName = "Pengguna", // Or fetch from profile
                rating = _uiState.value.rating,
                comment = _uiState.value.comment,
                attributes = _uiState.value.selectedAttributes,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            val result = if (reviewId != null) {
                editReviewUseCase(stallId, reviewId, review)
            } else {
                addReviewUseCase(stallId, review)
            }

            result.onSuccess {
                recalculateStallRatingUseCase(stallId)
                _uiState.update { it.copy(isSaving = false, isSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false, error = e.message ?: "Failed to save review") }
            }
        }
    }
}
