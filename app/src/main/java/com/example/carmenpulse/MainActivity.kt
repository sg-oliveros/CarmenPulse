package com.example.carmenpulse

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.example.carmenpulse.data.SupabaseClient
import com.example.carmenpulse.ui.screens.AuthScreen
import com.example.carmenpulse.ui.theme.CarmenPulseTheme
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables edge-to-edge drawing under status & navigation bars
        enableEdgeToEdge()

        setContent {
            CarmenPulseTheme {
                // Surface provides the default background color from your Theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val sessionStatus by SupabaseClient.supabase.auth.sessionStatus.collectAsState()
                    var isLoading by remember { mutableStateOf(true) }
                    var isLoggedIn by remember(sessionStatus) {
                        mutableStateOf(sessionStatus is SessionStatus.Authenticated)
                    }

                    LaunchedEffect(sessionStatus) {
                        if (sessionStatus is SessionStatus.Authenticated) {
                            isLoggedIn = true
                        } else if (sessionStatus is SessionStatus.NotAuthenticated) {
                            isLoggedIn = false
                        }
                    }

                    if (isLoading) {
                        LoadingScreen(
                            onFinished = { isLoading = false }
                        )
                    } else if (isLoggedIn) {
                        MainScreen(
                            onLogout = { isLoggedIn = false }
                        )
                    } else {
                        AuthScreen(
                            onLoginSuccess = {
                                isLoggedIn = true
                            }
                        )
                    }
                }
            }
        } 
    }
}
