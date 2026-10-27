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

    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var resetEmailInput by remember { mutableStateOf("") }
    var otpCodeInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    val resetPasswordState by authViewModel.resetPasswordState.collectAsState()

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
                .clickable {
                    resetEmailInput = email
                    otpCodeInput = ""
                    newPasswordInput = ""
                    confirmPasswordInput = ""
                    authViewModel.clearResetPasswordState()
                    showForgotPasswordDialog = true
                }
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

    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                showForgotPasswordDialog = false
                authViewModel.clearResetPasswordState()
            },
            title = {
                Text(
                    text = when (resetPasswordState) {
                        is ResetPasswordState.CodeSent -> "Verify 6-Digit Code"
                        is ResetPasswordState.CodeVerified -> "Set New Password"
                        is ResetPasswordState.Success -> "Password Reset Complete"
                        else -> "Reset Password"
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (resetPasswordState) {
                        is ResetPasswordState.CodeSent -> {
                            Text(
                                text = "A 6-digit code has been sent to $resetEmailInput. Enter it below to verify.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = otpCodeInput,
                                onValueChange = { otpCodeInput = it },
                                label = { Text("6-Digit Code") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        is ResetPasswordState.CodeVerified -> {
                            Text(
                                text = "Code verified! Enter your new password below.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = newPasswordInput,
                                onValueChange = { newPasswordInput = it },
                                label = { Text("New Password") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = confirmPasswordInput,
                                onValueChange = { confirmPasswordInput = it },
                                label = { Text("Confirm New Password") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            if (newPasswordInput.isNotBlank() && confirmPasswordInput.isNotBlank() && newPasswordInput != confirmPasswordInput) {
                                Text(
                                    text = "Passwords do not match.",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        is ResetPasswordState.Success -> {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = (resetPasswordState as ResetPasswordState.Success).message,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        else -> {
                            Text(
                                text = "Enter your registered email address and we will send you a 6-digit reset code.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = resetEmailInput,
                                onValueChange = { resetEmailInput = it },
                                label = { Text("E-mail Address") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    if (resetPasswordState is ResetPasswordState.Loading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Processing...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    if (resetPasswordState is ResetPasswordState.Error) {
                        Text(
                            text = (resetPasswordState as ResetPasswordState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                when (resetPasswordState) {
                    is ResetPasswordState.CodeSent -> {
                        Button(
                            onClick = {
                                authViewModel.verifyResetCode(resetEmailInput, otpCodeInput)
                            },
                            enabled = otpCodeInput.isNotBlank() && resetPasswordState !is ResetPasswordState.Loading,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Verify Code")
                        }
                    }

                    is ResetPasswordState.CodeVerified -> {
                        Button(
                            onClick = {
                                authViewModel.updateNewPassword(newPasswordInput)
                            },
                            enabled = newPasswordInput.isNotBlank() &&
                                    confirmPasswordInput.isNotBlank() &&
                                    newPasswordInput == confirmPasswordInput &&
                                    resetPasswordState !is ResetPasswordState.Loading,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Update Password")
                        }
                    }

                    is ResetPasswordState.Success -> {
                        Button(
                            onClick = {
                                showForgotPasswordDialog = false
                                authViewModel.clearResetPasswordState()
                            },
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Done")
                        }
                    }

                    else -> {
                        Button(
                            onClick = {
                                authViewModel.sendPasswordResetEmail(resetEmailInput)
                            },
                            enabled = resetEmailInput.isNotBlank() && resetPasswordState !is ResetPasswordState.Loading,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Send Reset Code")
                        }
                    }
                }
            },
            dismissButton = {
                if (resetPasswordState !is ResetPasswordState.Success) {
                    TextButton(
                        onClick = {
                            showForgotPasswordDialog = false
                            authViewModel.clearResetPasswordState()
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}