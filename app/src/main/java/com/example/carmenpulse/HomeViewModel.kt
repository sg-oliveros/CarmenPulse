package com.example.carmenpulse

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    // This is the private state that only the ViewModel can modify
    private val _advisories = MutableStateFlow<List<Advisory>>(emptyList())
    
    // This is the public state that the UI observes
    val advisories: StateFlow<List<Advisory>> = _advisories.asStateFlow()

    init {
        // When the ViewModel starts, we fetch the data
        fetchAdvisories()
    }

    private fun fetchAdvisories() {
        // --- BACKEND INTEGRATION POINT ---
        
        //MOCK DATA
        _advisories.value = listOf(
            Advisory(
                "1",
                "Dengue Prevention & Clean-Up Drive",
                "June 15, 2026",
                "Immunization",
                "Free misting and larvicide distribution scheduled across Purok 1 to 4 starting 7:00 AM...",
                45
            ),
            Advisory(
                "2",
                "Free Vitamin Distribution for Children",
                "June 18, 2026",
                "Dental Mission",
                "Bring your child's health record card to the Barangay Carmen Health Center to claim supplements.",
                28
            )
        )
    }

    // Function to handle interactions (which would also sync with a backend)
    fun toggleInterest(advisoryId: String, isInterested: Boolean) {
        val currentList = _advisories.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == advisoryId }
        
        if (index != -1) {
            val advisory = currentList[index]
            val newCount = if (isInterested) advisory.initialInterestedCount + 1 else advisory.initialInterestedCount - 1
            
            currentList[index] = advisory.copy(initialInterestedCount = newCount)
            _advisories.value = currentList
        }
    }
}
