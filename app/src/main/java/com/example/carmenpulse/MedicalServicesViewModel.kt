package com.example.carmenpulse

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carmenpulse.data.models.AppNotification
import com.example.carmenpulse.data.models.MedicalServices
import com.example.carmenpulse.data.repository.AuthRepository
import com.example.carmenpulse.data.repository.MedicalServiceRepository
import com.example.carmenpulse.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicalServicesViewModel : ViewModel() {

    private val _services = MutableStateFlow<List<MedicalServices>>(emptyList())
    val services: StateFlow<List<MedicalServices>> = _services.asStateFlow()

    private val _interestedServiceIds = MutableStateFlow<Set<Long>>(emptySet())
    val interestedServiceIds: StateFlow<Set<Long>> = _interestedServiceIds.asStateFlow()

    private val _interestCounts = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val interestCounts: StateFlow<Map<Long, Int>> = _interestCounts.asStateFlow()

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

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _isLoadingNotifications = MutableStateFlow(false)
    val isLoadingNotifications: StateFlow<Boolean> = _isLoadingNotifications.asStateFlow()

    val hasUnread: StateFlow<Boolean> = NotificationRepository.hasUnread

    private val _selectedStatus = MutableStateFlow("All")
    val selectedStatus: StateFlow<String> = _selectedStatus.asStateFlow()

    private val pageSize = 10L

    init {
        fetchServices()
        fetchInterests()
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

    fun fetchInterests() {
        viewModelScope.launch {
            try {
                val profile = AuthRepository.getCurrentProfile()
                if (profile != null) {
                    _interestedServiceIds.value = MedicalServiceRepository.fetchUserInterestedServiceIds(profile.user_id)
                }
                _interestCounts.value = MedicalServiceRepository.fetchServiceInterestsCounts()
            } catch (_: Exception) {
            }
        }
    }

    fun toggleInterest(serviceId: Long) {
        viewModelScope.launch {
            val profile = AuthRepository.getCurrentProfile() ?: return@launch
            val currentlyInterested = _interestedServiceIds.value.contains(serviceId)
            val newInterested = !currentlyInterested

            val updatedSet = if (newInterested) {
                _interestedServiceIds.value + serviceId
            } else {
                _interestedServiceIds.value - serviceId
            }
            _interestedServiceIds.value = updatedSet

            val currentCount = _interestCounts.value[serviceId] ?: 0
            val newCount = if (newInterested) currentCount + 1 else (currentCount - 1).coerceAtLeast(0)
            _interestCounts.value = _interestCounts.value + (serviceId to newCount)

            try {
                MedicalServiceRepository.toggleServiceInterest(serviceId, profile.user_id, newInterested)
            } catch (_: Exception) {
                fetchInterests()
            }
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
                fetchInterests()
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

    fun fetchNotifications(context: Context? = null) {
        viewModelScope.launch {
            _isLoadingNotifications.value = true
            try {
                val list = NotificationRepository.fetchRecentNotifications(context, 30)
                _notifications.value = list
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingNotifications.value = false
            }
        }
    }

    fun markNotificationsAsRead(context: Context) {
        NotificationRepository.markAllAsRead(context, _notifications.value)
    }
}
