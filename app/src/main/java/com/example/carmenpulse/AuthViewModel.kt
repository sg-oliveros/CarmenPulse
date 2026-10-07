package com.example.carmenpulse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carmenpulse.data.models.User
import com.example.carmenpulse.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException

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
                val rawMessage = e.localizedMessage ?: e.message ?: ""
                val errorMessage = when {

                    isNetworkError(rawMessage, e) ->
                        "No internet connection."

                    rawMessage.contains("already registered", ignoreCase = true) ||
                            rawMessage.contains("already exists", ignoreCase = true) ->
                        "This Gmail address is already exist."

                    rawMessage.contains("email", ignoreCase = true) && rawMessage.contains("invalid", ignoreCase = true) ->
                        "Please enter a valid Gmail address."

                    rawMessage.contains("password", ignoreCase = true) && (rawMessage.contains("short", ignoreCase = true) || rawMessage.contains("characters", ignoreCase = true)) ->
                        "Password is too weak or short."

                    else ->
                        "Sign up failed. Please check your details and try again."
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
                val rawMessage = e.localizedMessage ?: e.message ?: ""
                val errorMessage = when {

                    isNetworkError(rawMessage, e) ->
                        "No internet connection."

                    rawMessage.contains("Invalid login credentials", ignoreCase = true) ||
                            rawMessage.contains("User not found", ignoreCase = true) ||
                            rawMessage.contains("Email not found", ignoreCase = true) ||
                            rawMessage.contains("status: 400", ignoreCase = true) ->
                        "Wrong Gmail or Password."

                    rawMessage.contains("invalid email", ignoreCase = true) ->
                        "Please enter a valid Gmail address."

                    else ->
                        "Login failed. Please check your credentials and try again."
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

    private fun isNetworkError(message: String, exception: Exception): Boolean {
        val networkKeywords = listOf(
            "Unable to resolve host",
            "Failed to connect",
            "UnknownHostException",
            "ConnectException",
            "SocketException",
            "SocketTimeoutException",
            "Network",
            "Offline",
            "timeout"
        )
        return networkKeywords.any { message.contains(it, ignoreCase = true) } ||
                exception is UnknownHostException ||
                exception is ConnectException
    }
}