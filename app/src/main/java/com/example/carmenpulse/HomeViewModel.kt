package com.example.carmenpulse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carmenpulse.data.models.Posts
import com.example.carmenpulse.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {
    private val _advisories = MutableStateFlow<List<Advisory>>(emptyList())
    val advisories: StateFlow<List<Advisory>> = _advisories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchAdvisories()
    }


    fun fetchAdvisories() {

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val posts = PostRepository.fetchPosts()
                _advisories.value = posts.map { it.toAdvisory() }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load advisories"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleInterest(advisoryId: String, isInterested: Boolean) {
        val currentList = _advisories.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == advisoryId }
        
        if (index != -1) {
            val advisory = currentList[index]
            val newCount = if (isInterested) advisory.initialInterestedCount + 1
            else advisory.initialInterestedCount - 1
            
            currentList[index] = advisory.copy(initialInterestedCount = newCount)
            _advisories.value = currentList
        }
    }
}

private fun Posts.toAdvisory(): Advisory {
    val formattedDate = try {
        OffsetDateTime.parse(created_at)
            .format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    } catch (e: Exception) {
        created_at
    }
    return Advisory(
        id = post_id.toString(),
        title = title,
        date = formattedDate,
        category = category,
        snippet = content,
        initialInterestedCount = 0
    )
}
