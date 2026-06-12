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
import com.example.balanja.domain.repository.AuthRepository
import java.util.Calendar

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val stalls: List<Stall>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val getAllStallsUseCase: GetAllStallsUseCase,
    private val getCampusWeatherUseCase: GetCampusWeatherUseCase,
    private val getFavoritesUseCase: com.example.balanja.domain.usecase.GetFavoritesUseCase,
    private val addFavoriteUseCase: com.example.balanja.domain.usecase.AddFavoriteUseCase,
    private val deleteFavoriteUseCase: com.example.balanja.domain.usecase.DeleteFavoriteUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    // ─── Stall state ──────────────────────────────────────────────────────────

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val _userName = MutableStateFlow(authRepository.getCurrentUser()?.name?.takeIf { it.isNotBlank() } ?: "")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _greeting = MutableStateFlow(getGreetingTime())
    val greeting: StateFlow<String> = _greeting.asStateFlow()

    private fun getGreetingTime(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..10 -> "Selamat Pagi"
            in 11..14 -> "Selamat Siang"
            in 15..17 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }

    // ─── Weather state ────────────────────────────────────────────────────────

    private val _weatherState = MutableStateFlow<UiState<Weather>>(UiState.Loading)
    val weatherState: StateFlow<UiState<Weather>> = _weatherState.asStateFlow()

    init {
        fetchStalls()
        fetchWeather()
        fetchFavorites()
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
                    _uiState.value = HomeUiState.Success(stallsList.shuffled())
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

    // ─── Favorites ────────────────────────────────────────────────────────────

    private val _favorites = MutableStateFlow<List<com.example.balanja.domain.model.FavoriteStall>>(emptyList())
    val favorites: StateFlow<List<com.example.balanja.domain.model.FavoriteStall>> = _favorites.asStateFlow()

    private fun fetchFavorites() {
        viewModelScope.launch {
            getFavoritesUseCase().collect { favs ->
                _favorites.value = favs
            }
        }
    }

    fun toggleFavorite(stall: Stall) {
        viewModelScope.launch {
            val isFav = _favorites.value.any { it.stallId == stall.id }
            if (isFav) {
                deleteFavoriteUseCase(stall.id)
            } else {
                val fav = com.example.balanja.domain.model.FavoriteStall(
                    stallId = stall.id,
                    name = stall.name,
                    imageUrl = stall.imageUrl,
                    location = stall.location,
                    ratingAverage = stall.rating,
                    priceMin = stall.priceMin.toInt(),
                    priceMax = stall.priceMax.toInt(),
                    isOpen = stall.isOpen,
                    savedAt = System.currentTimeMillis()
                )
                addFavoriteUseCase(fav)
            }
        }
    }

    // ─── Pull-to-refresh ──────────────────────────────────────────────────────

    fun onRefresh() {
        _userName.value = authRepository.getCurrentUser()?.name?.takeIf { it.isNotBlank() } ?: ""
        _greeting.value = getGreetingTime()
        fetchStalls()
        fetchWeather()
    }
}