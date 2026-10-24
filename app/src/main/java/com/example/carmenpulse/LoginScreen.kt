package com.example.carmenpulse

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextDecoration
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carmenpulse.ui.components.CustomTextField
import com.example.carmenpulse.util.BiometricPromptManager

@Composable
fun LoginScreen(
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var biometricError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val activity = context as? FragmentActivity

    val authState by authViewModel.authState.collectAsState()
    val isLoading = authState is AuthState.Loading

    // Only navigate in once Supabase actually confirms success,
    // then reset so this leftover state can't leak into another screen.
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
            authViewModel.resetState()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(75.dp))

        CustomTextField(value = email, onValueChange = { email = it }, label = "E-mail")
        Spacer(modifier = Modifier.height(20.dp))

        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true,
            isPasswordVisible = passwordVisible,
            onVisibilityToggle = { passwordVisible = !passwordVisible }
        )

        if (authState is AuthState.Error) {
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Text(
            text = "Forgot Password ?",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 10.dp)
                .clickable { /* TODO: Implement forgot password action */ }
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { authViewModel.logIn(email, password) },
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth(0.85f).height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(30.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Login", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                biometricError = null
                if (activity != null) {
                    val manager = BiometricPromptManager(activity)
                    if (manager.canAuthenticate()) {
                        manager.showBiometricPrompt(
                            onSuccess = {
                                onLoginSuccess()
                            },
                            onError = { err ->
                                biometricError = err
                            }
                        )
                    } else {
                        biometricError = "Biometric authentication is not set up or supported on this device."
                    }
                } else {
                    biometricError = "Biometric prompt is not available."
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            border = BorderStroke(1.dp, Color.White),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = "Biometric Login",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Biometric Login", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        if (biometricError != null) {
            Text(
                text = biometricError!!,
                color = MaterialTheme.colorScheme.errorContainer,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Text("Don't Have An Account? ", color = Color.White, fontSize = 12.sp)
            Text(
                text = "Sign up",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onNavigateToSignUp() }
            )
        }
    }
}