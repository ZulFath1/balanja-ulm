package com.example.balanja.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.domain.model.Stall
import com.example.balanja.domain.usecase.stall.GetAllStallsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MapUiState(
    val isLoading: Boolean = true,
    val stalls: List<Stall> = emptyList(),
    val error: String? = null,
    val selectedStallLocation: Stall? = null
)

class MapViewModel(
    private val stallId: String,
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
                    val selectedStall = stallList.find { it.id == stallId }
                    _uiState.value = MapUiState(
                        isLoading = false,
                        stalls = stallList,
                        selectedStallLocation = selectedStall
                    )
                }
            } catch (e: Exception) {
                _uiState.value = MapUiState(isLoading = false, error = e.message ?: "Failed to load stalls")
            }
        }
    }
}

