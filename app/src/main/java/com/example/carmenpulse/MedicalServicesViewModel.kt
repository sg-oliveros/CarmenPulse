package com.example.carmenpulse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carmenpulse.data.models.MedicalServices
import com.example.carmenpulse.data.repository.MedicalServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicalServicesViewModel : ViewModel() {

    private val _services = MutableStateFlow<List<MedicalServices>>(emptyList())
    val services: StateFlow<List<MedicalServices>> = _services.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _canLoadMore = MutableStateFlow(true)
    val canLoadMore: StateFlow<Boolean> = _canLoadMore.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatus = MutableStateFlow("All")
    val selectedStatus: StateFlow<String> = _selectedStatus.asStateFlow()

    private val pageSize = 10L

    init {
        fetchServices()
    }

    fun onSearchQuerySubmit(query: String) {
        _searchQuery.value = query
        fetchServices()
    }

    fun onStatusSelected(status: String) {
        if (_selectedStatus.value != status) {
            _selectedStatus.value = status
            fetchServices()
        }
    }

    fun fetchServices() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = MedicalServiceRepository.fetchMedicalServices(
                    statusFilter = _selectedStatus.value,
                    searchQuery = _searchQuery.value,
                    limit = pageSize,
                    offset = 0
                )
                _services.value = result
                _canLoadMore.value = result.size.toLong() >= pageSize
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to load medical services"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMoreServices() {
        if (_isLoading.value || _isLoadingMore.value || !_canLoadMore.value) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            try {
                val currentOffset = _services.value.size.toLong()
                val nextBatch = MedicalServiceRepository.fetchMedicalServices(
                    statusFilter = _selectedStatus.value,
                    searchQuery = _searchQuery.value,
                    limit = pageSize,
                    offset = currentOffset
                )
                if (nextBatch.isNotEmpty()) {
                    _services.value = _services.value + nextBatch
                }
                _canLoadMore.value = nextBatch.size.toLong() >= pageSize
            } catch (_: Exception) {
                // Keep existing items if background load fails
            } finally {
                _isLoadingMore.value = false
            }
        }
    }
}
