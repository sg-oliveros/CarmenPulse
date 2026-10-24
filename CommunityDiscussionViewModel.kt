package com.example.carmenpulse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carmenpulse.data.models.Comment
import com.example.carmenpulse.data.models.MedicalServices
import com.example.carmenpulse.data.repository.AuthRepository
import com.example.carmenpulse.data.repository.CommentRepository
import com.example.carmenpulse.data.repository.MedicalServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val list = CommentRepository.fetchComments(serviceId, limit = currentLimit)
                _comments.value = list
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to load comments"
            } finally {
                _isLoading.value = false
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
        val userId = currentUserId ?: 0L
        if (content.isBlank()) return
        viewModelScope.launch {
            try {
                CommentRepository.postComment(serviceId, userId, content.trim())
                loadComments(serviceId)
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to post comment"
            }
        }
    }
}
