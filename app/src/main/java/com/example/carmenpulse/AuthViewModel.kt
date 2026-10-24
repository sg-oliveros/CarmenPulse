package com.example.carmenpulse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carmenpulse.data.models.User
import com.example.carmenpulse.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    object Success : AuthState
    data class Error(val message: String) : AuthState
}

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _profile = MutableStateFlow<User?>(null)
    val profile: StateFlow<User?> = _profile.asStateFlow()

    fun signUp(email: String, password: String, firstName: String, lastName: String, contactNumber: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                AuthRepository.signUp(email, password, firstName, lastName, contactNumber)
                _authState.value = AuthState.Success
                loadProfile()
            } catch (e: Exception) {
                val rawMessage = e.localizedMessage ?: ""
                val errorMessage = when {
                    rawMessage.contains("already registered", ignoreCase = true) || 
                    rawMessage.contains("already exists", ignoreCase = true) -> 
                        "This email is already registered."
                    rawMessage.contains("password", ignoreCase = true) && (rawMessage.contains("short", ignoreCase = true) || rawMessage.contains("characters", ignoreCase = true)) -> 
                        "Password is too weak or short."
                    rawMessage.contains("email", ignoreCase = true) && rawMessage.contains("invalid", ignoreCase = true) -> 
                        "Please enter a valid email address."
                    else -> 
                        "Sign up failed. Please check your details."
                }
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    fun logIn(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                AuthRepository.logIn(email, pass)
                _authState.value = AuthState.Success
                loadProfile()
            } catch (e: Exception) {
                val errorMessage = if (e.localizedMessage?.contains("Invalid login credentials", ignoreCase = true) == true ||
                    e.localizedMessage?.contains("invalid", ignoreCase = true) == true ||
                    e.localizedMessage?.contains("status: 400", ignoreCase = true) == true) {
                    "Invalid email or password."
                } else {
                    e.localizedMessage ?: "Login failed. Please check your credentials."
                }
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            AuthRepository.logOut()
            _profile.value = null
            _authState.value = AuthState.Idle
        }
    }

    // Call this right after Success has been acted on (navigated away),
    // so leftover Success state can't leak into the next screen shown.
    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun loadProfile() {
        viewModelScope.launch {
            try {
                _profile.value = AuthRepository.getCurrentProfile()
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    fun fetchProfile() = loadProfile()
}