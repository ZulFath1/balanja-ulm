package com.example.balanja.presentation.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.data.api.cloudinary.CloudinaryApiService
import com.example.balanja.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import android.content.Context
import java.io.InputStream

data class EditProfileUiState(
    val name: String = "",
    val existingPhotoUrl: String? = null,
    val selectedImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class EditProfileViewModel(
    private val authRepository: AuthRepository,
    private val cloudinaryApiService: CloudinaryApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val user = authRepository.getCurrentUser()
        if (user != null) {
            _uiState.update { 
                it.copy(
                    name = user.name ?: "",
                    existingPhotoUrl = user.photoUrl
                )
            }
        }
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onImageSelected(uri: Uri?) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    fun saveProfile(context: Context) {
        val currentState = _uiState.value
        if (currentState.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nama tidak boleh kosong") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                var newPhotoUrl = currentState.existingPhotoUrl

                // Upload image if a new one is selected
                if (currentState.selectedImageUri != null) {
                    val uri = currentState.selectedImageUri
                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    if (inputStream != null) {
                        val bytes = inputStream.readBytes()
                        val requestFile = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                        
                        val multipartBody = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", "profile.jpg", requestFile)
                            .addFormDataPart("upload_preset", "balanja_preset")
                            .build()

                        val response = cloudinaryApiService.uploadImage(multipartBody)
                        newPhotoUrl = response.secureUrl
                    }
                }

                // Update Auth Profile
                val result = authRepository.updateProfile(currentState.name, newPhotoUrl)
                if (result.isSuccess) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = result.exceptionOrNull()?.message ?: "Gagal memperbarui profil"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Terjadi kesalahan: ${e.message}"
                    ) 
                }
            }
        }
    }
}
