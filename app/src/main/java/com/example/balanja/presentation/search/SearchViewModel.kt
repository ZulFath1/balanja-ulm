package com.example.balanja.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.domain.model.Stall
import com.example.balanja.domain.usecase.GetAllStallsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel(
    private val getAllStallsUseCase: GetAllStallsUseCase
) : ViewModel() {
    private val _selectedRating = MutableStateFlow<Float?>(null)
    val selectedRating = _selectedRating.asStateFlow()

    private val _maxPrice = MutableStateFlow<Int>(Int.MAX_VALUE)
    val maxPrice = _maxPrice.asStateFlow()

    private val _allStalls = MutableStateFlow<List<Stall>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val filteredStalls = combine(_allStalls, _searchQuery, _selectedRating, _maxPrice) { stalls, query, rating, price ->
        stalls.filter { stall ->
            val matchesQuery = query.isBlank() || stall.name.contains(query, ignoreCase = true)
            val matchesRating = rating == null || stall.rating >= rating
            val matchesPrice = stall.priceMin <= price
            matchesQuery && matchesRating && matchesPrice
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadStalls()
    }

    private fun loadStalls() {
        viewModelScope.launch {
            getAllStallsUseCase().collect { _allStalls.value = it }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onRatingFilterChange(rating: Float?) {
        _selectedRating.value = if (_selectedRating.value == rating) null else rating
    }

    fun onPriceFilterChange(price: Int) {
        _maxPrice.value = if (_maxPrice.value == price) Int.MAX_VALUE else price
    }
}