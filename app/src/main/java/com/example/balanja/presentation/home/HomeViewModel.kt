package com.example.balanja.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.domain.model.Stall
import com.example.balanja.domain.usecase.GetAllStallsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val stalls: List<Stall>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val getAllStallsUseCase: GetAllStallsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchStalls()
    }

    fun fetchStalls() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            getAllStallsUseCase()
                .catch { exception ->
                    _uiState.value = HomeUiState.Error(exception.message ?: "Gagal memuat data stan")
                }
                .collect { stallsList ->
                    _uiState.value = HomeUiState.Success(stallsList)
                }
        }
    }
}