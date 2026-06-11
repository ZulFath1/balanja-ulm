package com.example.balanja.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.domain.model.Stall
import com.example.balanja.domain.model.Weather
import com.example.balanja.domain.usecase.GetAllStallsUseCase
import com.example.balanja.domain.usecase.GetCampusWeatherUseCase
import com.example.balanja.presentation.util.UiState
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
    private val getAllStallsUseCase: GetAllStallsUseCase,
    private val getCampusWeatherUseCase: GetCampusWeatherUseCase
) : ViewModel() {

    // ─── Stall state ──────────────────────────────────────────────────────────

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // ─── Weather state ────────────────────────────────────────────────────────

    private val _weatherState = MutableStateFlow<UiState<Weather>>(UiState.Loading)
    val weatherState: StateFlow<UiState<Weather>> = _weatherState.asStateFlow()

    init {
        fetchStalls()
        fetchWeather()
    }

    // ─── Stall ────────────────────────────────────────────────────────────────

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

    // ─── Weather ──────────────────────────────────────────────────────────────

    fun fetchWeather() {
        viewModelScope.launch {
            _weatherState.value = UiState.Loading
            getCampusWeatherUseCase()
                .onSuccess { weather ->
                    _weatherState.value = UiState.Success(weather)
                }
                .onFailure { error ->
                    _weatherState.value = UiState.Error(
                        error.message ?: "Gagal memuat data cuaca"
                    )
                }
        }
    }

    // ─── Pull-to-refresh ──────────────────────────────────────────────────────

    fun onRefresh() {
        fetchStalls()
        fetchWeather()
    }
}