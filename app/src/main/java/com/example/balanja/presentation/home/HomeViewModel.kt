package com.example.balanja.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.AppContainer
import com.example.balanja.domain.model.Stall
import com.example.balanja.domain.model.Weather
import com.example.balanja.domain.usecase.GetStallsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getStallsUseCase: GetStallsUseCase = AppContainer.getStallsUseCase
) : ViewModel() {
    private val _stalls = MutableStateFlow<List<Stall>>(emptyList())
    val stalls: StateFlow<List<Stall>> = _stalls.asStateFlow()

    private val _weatherState = MutableStateFlow<Weather?>(null)
    val weatherState: StateFlow<Weather?> = _weatherState.asStateFlow()

    init { loadStalls() }

    private fun loadStalls() {
        viewModelScope.launch {
            getStallsUseCase().collect { _stalls.value = it }
        }
    }
}
