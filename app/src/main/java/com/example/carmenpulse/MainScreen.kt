package com.example.carmenpulse

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier

@Composable
fun MainScreen(
    currentUser: UserData = UserData(name = "Resident", email = "resident@example.com"),
    onLogout: () -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.MedicalServices, contentDescription = "Services") },
                    label = { Text("Services") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Forum, contentDescription = "Forum") },
                    label = { Text("Forum") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeAdvisoriesFeedScreen(
                    userName = currentUser.name,
                    onCommentClick = { advisory ->
                        // Switch to Forum tab (index 2) when Q&A thread is clicked
                        selectedTab = 2
                    }
                )
                1 -> MedicalServicesScreen()
                2 -> CommunityDiscussionScreen(
                    currentUser = currentUser,
                    onBack = { selectedTab = 0 }
                )
                3 -> ProfileScreen(
                    currentUser = currentUser,
                    onLogout = onLogout
                )
            }
        }
    }
}
