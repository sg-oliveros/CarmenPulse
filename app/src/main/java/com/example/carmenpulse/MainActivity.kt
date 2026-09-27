package com.example.carmenpulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.carmenpulse.ui.screens.AuthScreen
import com.example.carmenpulse.ui.theme.CarmenPulseTheme

class MainActivity : ComponentActivity() {
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
                    var isLoggedIn by remember { mutableStateOf(false) }

                    if (isLoggedIn) {
                        MainScreen(
                            onLogout = { isLoggedIn = false }
                        )
                    } else {
                        AuthScreen(
                            onLoginSuccess = { isLoggedIn = true }
                        )
                    }
                }
            }
        }
    }
}