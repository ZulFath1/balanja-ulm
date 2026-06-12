package com.example.balanja.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.domain.model.Stall
import com.example.balanja.domain.model.RecentSearch
import com.example.balanja.domain.usecase.stall.GetAllStallsUseCase
import com.example.balanja.domain.usecase.search.GetRecentSearchesUseCase
import com.example.balanja.domain.usecase.search.AddRecentSearchUseCase
import com.example.balanja.domain.usecase.search.ClearRecentSearchesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val allStalls: List<Stall> = emptyList(),
    val filteredStalls: List<Stall> = emptyList(),
    val recentSearches: List<RecentSearch> = emptyList(),
    val error: String? = null
)

class SearchViewModel(
    private val getAllStallsUseCase: GetAllStallsUseCase,
    private val getRecentSearchesUseCase: GetRecentSearchesUseCase,
    private val addRecentSearchUseCase: AddRecentSearchUseCase,
    private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        loadStalls()
        loadRecentSearches()
    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            getRecentSearchesUseCase().collect { recentList ->
                _uiState.update { it.copy(recentSearches = recentList) }
            }
        }
    }

    private fun loadStalls() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                getAllStallsUseCase().collect { stalls ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            allStalls = stalls,
                            filteredStalls = filterStalls(stalls, state.searchQuery)
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to load stalls") }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredStalls = filterStalls(state.allStalls, query)
            )
        }
    }

    private fun filterStalls(stalls: List<Stall>, query: String): List<Stall> {
        if (query.isBlank()) return emptyList() // or return allStalls if you want to show all initially
        val lowerCaseQuery = query.lowercase()
        return stalls.filter { it.name.lowercase().contains(lowerCaseQuery) }
    }

    fun onStallClicked(stall: Stall) {
        viewModelScope.launch {
            val recentSearch = RecentSearch(
                stallId = stall.id,
                name = stall.name,
                location = stall.location,
                priceRange = "Rp ${stall.priceMin} - Rp ${stall.priceMax}",
                photoUrl = stall.imageUrl,
                averageRating = stall.rating,
                isOpen = stall.isOpen,
                timestamp = System.currentTimeMillis()
            )
            addRecentSearchUseCase(recentSearch)
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            clearRecentSearchesUseCase()
        }
    }
}
