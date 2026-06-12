package com.example.balanja.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.domain.model.Stall
import com.example.balanja.domain.repository.AuthRepository
import com.example.balanja.domain.repository.StallRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyStallsUiState(
    val stalls: List<Stall> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class MyStallsViewModel(
    private val authRepository: AuthRepository,
    private val stallRepository: StallRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyStallsUiState())
    val uiState: StateFlow<MyStallsUiState> = _uiState.asStateFlow()

    init {
        loadMyStalls()
    }

    private fun loadMyStalls() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val user = authRepository.getCurrentUser()
            if (user != null) {
                stallRepository.getStallsByOwnerId(user.id).collect { stalls ->
                    _uiState.update { it.copy(stalls = stalls, isLoading = false) }
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Pengguna tidak ditemukan") }
            }
        }
    }
}
