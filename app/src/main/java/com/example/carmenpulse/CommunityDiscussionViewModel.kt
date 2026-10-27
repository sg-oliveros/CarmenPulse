package com.example.carmenpulse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carmenpulse.data.models.Comment
import com.example.carmenpulse.data.models.MedicalServices
import com.example.carmenpulse.data.repository.AuthRepository
import com.example.carmenpulse.data.repository.CommentRepository
import com.example.carmenpulse.data.repository.MedicalServiceRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CommunityDiscussionViewModel : ViewModel() {

    private val _services = MutableStateFlow<List<MedicalServices>>(emptyList())
    val services: StateFlow<List<MedicalServices>> = _services.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentUserId: Long? = null
    private var currentLimit: Long = 30
    private var hasMoreToLoad = true
    private var pollJob: Job? = null

    init {
        viewModelScope.launch {
            currentUserId = AuthRepository.getCurrentProfile()?.user_id
        }
        fetchServices()
    }

    fun fetchServices() {
        viewModelScope.launch {
            try {
                _services.value = MedicalServiceRepository.fetchMedicalServices(limit = 100)
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to load medical services"
            }
        }
    }

    fun loadComments(serviceId: Long) {
        currentLimit = 30
        hasMoreToLoad = true
        pollJob?.cancel()

        pollJob = viewModelScope.launch {
            _isLoading.value = true
            try {
                val list = CommentRepository.fetchComments(serviceId, limit = currentLimit)
                _comments.value = list
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to load comments"
            } finally {
                _isLoading.value = false
            }

            // Real-time background sync loop
            while (isActive) {
                delay(2500) // Poll for new comments every 2.5 seconds
                try {
                    val latest = CommentRepository.fetchComments(serviceId, limit = currentLimit)
                    if (latest != _comments.value) {
                        _comments.value = latest
                    }
                } catch (_: Exception) {}
            }
        }
    }

    fun loadMoreComments(serviceId: Long) {
        if (!hasMoreToLoad || _isLoading.value) return
        viewModelScope.launch {
            try {
                val newLimit = currentLimit + 30
                val list = CommentRepository.fetchComments(serviceId, limit = newLimit)
                if (list.size <= _comments.value.size) {
                    hasMoreToLoad = false
                } else {
                    currentLimit = newLimit
                    _comments.value = list
                }
            } catch (_: Exception) {}
        }
    }

    fun postComment(serviceId: Long, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            try {
                val profile = AuthRepository.getCurrentProfile()
                if (profile == null) {
                    _error.value = "User session expired. Please log in again."
                    return@launch
                }
                CommentRepository.postComment(serviceId, profile.user_id, content.trim())
                loadComments(serviceId)
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to post comment"
            }
        }
    }
}
