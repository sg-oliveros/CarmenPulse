package com.example.carmenpulse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.carmenpulse.LoginScreen
import com.example.carmenpulse.SignUpScreen
import com.example.carmenpulse.ui.components.AuthToggleSwitch
import com.example.carmenpulse.ui.components.CurvedBackground
import com.example.carmenpulse.ui.components.LogoWithHeartbeat

enum class AuthMode {
    LOGIN,
    SIGN_UP,
    FORGOT_PASSWORD
}

@Composable
fun AuthScreen(
    onLoginSuccess: (email: String, name: String) -> Unit = { _, _ -> },
    onSignUpSuccess: (name: String, email: String) -> Unit = { _, _ -> }
) {
    var authMode by rememberSaveable { mutableStateOf(AuthMode.LOGIN) }

    if (authMode == AuthMode.FORGOT_PASSWORD) {
        ForgotPasswordScreen(
            onNavigateToLogin = { authMode = AuthMode.LOGIN }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Dynamic Curved Background Layer
            CurvedBackground(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 220.dp)
            )

            // UI Content Overlay Layer
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(90.dp))

                // App Title Logo with Heartbeat
                LogoWithHeartbeat()

                Spacer(modifier = Modifier.height(30.dp))

                // Switcher Pill Toggle
                AuthToggleSwitch(
                    isLoginSelected = (authMode == AuthMode.LOGIN),
                    onToggle = { isLogin ->
                        authMode = if (isLogin) AuthMode.LOGIN else AuthMode.SIGN_UP
                    }
                )

                Spacer(modifier = Modifier.height(60.dp))

                // Form Content Layer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 28.dp)
                ) {
                    if (authMode == AuthMode.LOGIN) {
                        LoginScreen(
                            onNavigateToSignUp = { authMode = AuthMode.SIGN_UP },
                            onNavigateToForgotPassword = { authMode = AuthMode.FORGOT_PASSWORD },
                            onLoginSuccess = onLoginSuccess
                        )
                    } else {
                        SignUpScreen(
                            onNavigateToLogin = { authMode = AuthMode.LOGIN },
                            onSignUpSuccess = onSignUpSuccess
                        )
                    }
                }
            }
        }
    }
}
