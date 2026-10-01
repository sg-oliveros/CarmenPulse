package com.example.carmenpulse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carmenpulse.data.models.MedicalServices
import com.example.carmenpulse.data.repository.MedicalServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicalServicesViewModel: ViewModel() {

    private val _services = MutableStateFlow<List<MedicalServices>>(emptyList())
    val services: StateFlow<List<MedicalServices>> = _services.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchServices()
    }

    fun fetchServices() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _services.value = MedicalServiceRepository.fetchMedicalServices()
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to laod medical services"
            } finally {
                _isLoading.value = false
            }
        }
    }


}