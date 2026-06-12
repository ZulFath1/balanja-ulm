package com.example.balanja.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.domain.model.Stall
import com.example.balanja.domain.usecase.favorite.GetFavoritesUseCase
import com.example.balanja.domain.usecase.favorite.DeleteFavoriteUseCase
import com.example.balanja.domain.usecase.stall.GetAllStallsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class FavoriteUiState(
    val isLoading: Boolean = true,
    val favorites: List<Stall> = emptyList(),
    val error: String? = null
)

class FavoriteStallsViewModel(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase,
    private val getAllStallsUseCase: GetAllStallsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            try {
                combine(
                    getFavoritesUseCase(),
                    getAllStallsUseCase()
                ) { favList, stallList ->
                    val favIds = favList.map { it.stallId }.toSet()
                    stallList.filter { favIds.contains(it.id) }
                }.collect { syncedFavorites ->
                    _uiState.value = FavoriteUiState(isLoading = false, favorites = syncedFavorites)
                }
            } catch (e: Exception) {
                _uiState.value = FavoriteUiState(isLoading = false, error = e.message ?: "Gagal memuat favorit")
            }
        }
    }

    fun removeFavorite(stallId: String) {
        viewModelScope.launch {
            deleteFavoriteUseCase(stallId)
        }
    }
}

