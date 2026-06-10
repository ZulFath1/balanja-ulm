package com.example.balanja.presentation.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanja.data.repository.AuthRepositoryImpl
import com.example.balanja.domain.model.User
import com.example.balanja.domain.usecase.LoginUseCase
import com.example.balanja.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepositoryImpl()
    private val loginUseCase = LoginUseCase(repository)
    private val registerUseCase = RegisterUseCase(repository)

    private val _authState = mutableStateOf<Result<User>?>(null)
    val authState: State<Result<User>?> = _authState

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginUseCase(email, password)
                .onStart { _isLoading.value = true }
                .collect { result ->
                    _isLoading.value = false
                    _authState.value = result
                }
        }
    }

    fun register(name: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            registerUseCase(name, email, password, role)
                .onStart { _isLoading.value = true }
                .collect { result ->
                    _isLoading.value = false
                    _authState.value = result
                }
        }
    }

    fun clearState() {
        _authState.value = null
    }
}