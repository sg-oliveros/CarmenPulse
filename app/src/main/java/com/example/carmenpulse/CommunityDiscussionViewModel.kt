package com.example.carmenpulse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carmenpulse.data.models.Comment
import com.example.carmenpulse.data.models.Posts
import com.example.carmenpulse.data.repository.AuthRepository
import com.example.carmenpulse.data.repository.CommentRepository
import com.example.carmenpulse.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommunityDiscussionViewModel : ViewModel() {

    private val _threads = MutableStateFlow<List<Posts>>(emptyList())
    val threads: StateFlow<List<Posts>> = _threads.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentUserId: Long? = null

    init {
        viewModelScope.launch {
            try {
                currentUserId = AuthRepository.getCurrentProfile()?.user_id
            } catch (_: Exception) {
            }
        }
        fetchThreads()
    }

    fun fetchThreads() {
        viewModelScope.launch {
            try {
                _threads.value = PostRepository.fetchPosts()
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to load announcements"
            }
        }
    }

    fun loadComments(postId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _comments.value = CommentRepository.fetchComments(postId)
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to load comments"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun postComment(postId: Long, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            try {
                var userId = currentUserId
                if (userId == null) {
                    userId = AuthRepository.getCurrentProfile()?.user_id
                    currentUserId = userId
                }
                if (userId == null) {
                    _error.value = "User not logged in or profile not found."
                    return@launch
                }
                CommentRepository.postComment(postId, userId, content.trim())
                loadComments(postId)
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to post comment"
            }
        }
    }
}
