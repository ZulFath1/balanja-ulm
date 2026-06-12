package com.example.balanja.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userName: String = "Pengguna",
    val email: String = "",
    val ownedStalls: List<com.example.balanja.domain.model.Stall> = emptyList(),
    val isLoading: Boolean = true
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val stallRepository: com.example.balanja.domain.repository.StallRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                _uiState.update { 
                    it.copy(
                        userName = user.name ?: "Pengguna Balanja",
                        email = user.email ?: "",
                        isLoading = false
                    ) 
                }
                
                // Fetch owned stalls
                stallRepository.getStallsByOwnerId(user.id).collect { stalls ->
                    _uiState.update { it.copy(ownedStalls = stalls) }
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onLogoutComplete()
        }
    }

    fun updateProfileName(newName: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.updateProfileName(newName)
            if (result.isSuccess) {
                // Update local UI state
                _uiState.update { 
                    it.copy(
                        userName = newName,
                        isLoading = false
                    ) 
                }
                onResult(true, null)
            } else {
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, result.exceptionOrNull()?.message)
            }
        }
    }
}
