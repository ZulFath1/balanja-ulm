package com.example.balanja.presentation.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.domain.model.Review
import com.example.balanja.domain.repository.AuthRepository
import com.example.balanja.domain.usecase.review.AddReviewUseCase
import com.example.balanja.domain.usecase.review.EditReviewUseCase
import com.example.balanja.domain.usecase.review.GetReviewsUseCase
import com.example.balanja.domain.usecase.review.RecalculateStallRatingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

data class WriteReviewUiState(
    val rating: Int = 0,
    val comment: String = "",
    val selectedAttributes: List<String> = emptyList(),
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val imageUrls: List<String> = emptyList()
)

class WriteReviewViewModel(
    private val stallId: String,
    private val reviewId: String?,
    private val addReviewUseCase: AddReviewUseCase,
    private val editReviewUseCase: EditReviewUseCase,
    private val getReviewsUseCase: GetReviewsUseCase,
    private val recalculateStallRatingUseCase: RecalculateStallRatingUseCase,
    private val authRepository: AuthRepository,
    private val cloudinaryApiService: com.example.balanja.data.api.cloudinary.CloudinaryApiService
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
                        selectedAttributes = it.attributes,
                        imageUrls = if (it.imageUrls.isNotEmpty()) it.imageUrls else listOfNotNull(it.imageUrl)
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

    fun submitReview(images: List<Pair<ByteArray, String>>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            val currentUser = authRepository.getCurrentUser()
            if (currentUser == null) {
                _uiState.update { it.copy(isSaving = false, error = "User not logged in") }
                return@launch
            }
            
            val finalImageUrls = _uiState.value.imageUrls.toMutableList()
            
            if (images.isNotEmpty()) {
                try {
                    // Start uploading all images
                    val uploadResponses = images.map { (imageBytes, fileExtension) ->
                        val mediaType = "image/$fileExtension".toMediaTypeOrNull()
                        val requestBody = imageBytes.toRequestBody(mediaType)
                        
                        val multipartBody = okhttp3.MultipartBody.Builder()
                            .setType(okhttp3.MultipartBody.FORM)
                            .addFormDataPart("file", "upload.$fileExtension", requestBody)
                            .addFormDataPart("upload_preset", "balanja_preset")
                            .build()
                        
                        cloudinaryApiService.uploadImage(multipartBody)
                    }
                    finalImageUrls.addAll(uploadResponses.map { it.secureUrl })
                } catch (e: retrofit2.HttpException) {
                    val errorBody = e.response()?.errorBody()?.string() ?: e.message()
                    _uiState.update { it.copy(isSaving = false, error = "Gagal mengunggah gambar: $errorBody") }
                    return@launch
                } catch (e: Exception) {
                    _uiState.update { it.copy(isSaving = false, error = "Gagal mengunggah gambar: ${e.message}") }
                    return@launch
                }
            }
            
            val userNameToSave = if (currentUser.name.isNotBlank()) currentUser.name else "Mahasiswa ULM"
            
            val review = Review(
                userId = currentUser.id,
                userName = userNameToSave,
                rating = _uiState.value.rating,
                comment = _uiState.value.comment,
                attributes = _uiState.value.selectedAttributes,
                imageUrls = finalImageUrls,
                imageUrl = finalImageUrls.firstOrNull(), // Keep for backward compatibility
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                userPhotoUrl = currentUser.photoUrl
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

