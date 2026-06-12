package com.example.balanja.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.domain.model.Review
import com.example.balanja.domain.repository.AuthRepository
import com.example.balanja.domain.repository.StallRepository
import com.example.balanja.domain.usecase.DeleteReviewUseCase
import com.example.balanja.domain.usecase.GetMyReviewsUseCase
import com.example.balanja.domain.usecase.RecalculateStallRatingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReviewWithStall(
    val review: Review,
    val stallName: String,
    val stallImageUrl: String
)

data class MyReviewsUiState(
    val isLoading: Boolean = true,
    val reviews: List<ReviewWithStall> = emptyList(),
    val error: String? = null
)

class MyReviewsViewModel(
    private val getMyReviewsUseCase: GetMyReviewsUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
    private val recalculateStallRatingUseCase: RecalculateStallRatingUseCase,
    private val authRepository: AuthRepository,
    private val stallRepository: StallRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyReviewsUiState())
    val uiState: StateFlow<MyReviewsUiState> = _uiState.asStateFlow()

    init {
        loadMyReviews()
    }

    private fun loadMyReviews() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, error = "Silakan login terlebih dahulu") }
                return@launch
            }

            try {
                getMyReviewsUseCase(userId).collect { reviews ->
                    val reviewsWithStalls = reviews.map { review ->
                        val stall = stallRepository.getStallById(review.stallId).firstOrNull()
                        ReviewWithStall(
                            review = review,
                            stallName = stall?.name ?: "Toko Tidak Diketahui",
                            stallImageUrl = stall?.imageUrl ?: ""
                        )
                    }
                    _uiState.update { it.copy(isLoading = false, reviews = reviewsWithStalls, error = null) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deleteReview(stallId: String, reviewId: String) {
        viewModelScope.launch {
            deleteReviewUseCase(stallId, reviewId).onSuccess {
                recalculateStallRatingUseCase(stallId)
            }
        }
    }
}
