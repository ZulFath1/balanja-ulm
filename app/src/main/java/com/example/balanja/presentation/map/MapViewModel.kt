package com.example.balanja.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.domain.model.Stall
import com.example.balanja.domain.usecase.GetAllStallsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MapUiState(
    val isLoading: Boolean = true,
    val stalls: List<Stall> = emptyList(),
    val error: String? = null
)

class MapViewModel(
    private val getAllStallsUseCase: GetAllStallsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadStalls()
    }

    private fun loadStalls() {
        viewModelScope.launch {
            _uiState.value = MapUiState(isLoading = true)
            try {
                getAllStallsUseCase().collect { stallList ->
                    // Filter out stalls that don't have valid coordinates if needed
                    // For now we'll just include all stalls and hope they have coordinates or a way to get them.
                    _uiState.value = MapUiState(isLoading = false, stalls = stallList)
                }
            } catch (e: Exception) {
                _uiState.value = MapUiState(isLoading = false, error = e.message ?: "Failed to load stalls")
            }
        }
    }
}
