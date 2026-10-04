package com.example.carmenpulse.ui.screens

import android.util.Patterns
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carmenpulse.ui.components.CurvedBackground
import com.example.carmenpulse.ui.components.LogoWithHeartbeat
import com.example.carmenpulse.ui.theme.BrandGreen
import com.example.carmenpulse.ui.theme.LoginGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ForgotPasswordScreen(
    onNavigateToLogin: () -> Unit = {}
) {
    var email by rememberSaveable { mutableStateOf("") }
    var isError by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var isSuccess by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    fun validateAndSubmit() {
        val trimmedEmail = email.trim()
        val isValidEmail = trimmedEmail.isNotBlank() &&
                Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()

        if (!isValidEmail) {
            isError = true
            errorMessage = "Please enter a valid email address."
        } else {
            isError = false
            errorMessage = ""
            isLoading = true

            coroutineScope.launch {
                // --- BACKEND INTEGRATION POINT ---
                // Connect actual backend password reset service here (e.g. FirebaseAuth.getInstance().sendPasswordResetEmail(email))
                delay(1500)
                isLoading = false
                isSuccess = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Bottom Curved Background Accent
        CurvedBackground(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.BottomCenter)
        )

        // Scrollable Content Layer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // CarmenPulse Brand Logo
            LogoWithHeartbeat()

            Spacer(modifier = Modifier.height(36.dp))

            if (isSuccess) {
                // Success Title
                Text(
                    text = "Reset link sent!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandGreen,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Success Subtitle
                Text(
                    text = "Check your email for instructions to reset your password.",
                    fontSize = 14.sp,
                    color = Color(0xFF757575),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Success Icon with Ray Bursts
                CheckmarkWithRays()

                Spacer(modifier = Modifier.height(40.dp))

                // Back to Login Button
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LoginGreen),
                    shape = RoundedCornerShape(28.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Text(
                        text = "Back to Login",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Screen Title
                Text(
                    text = "Forgot Password",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandGreen,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Supporting Instructions
                Text(
                    text = "Enter your email address and we'll send you a link to reset your password.",
                    fontSize = 14.sp,
                    color = Color(0xFF757575),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Email Pill Input Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (isError) {
                                isError = false
                                errorMessage = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        placeholder = {
                            Text(
                                text = "Email",
                                color = Color(0xFF9E9E9E),
                                fontSize = 15.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = "Email Icon",
                                tint = Color(0xFF757575)
                            )
                        },
                        singleLine = true,
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                validateAndSubmit()
                            }
                        ),
                        shape = RoundedCornerShape(28.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            disabledContainerColor = Color(0xFFF5F5F5),
                            focusedBorderColor = if (isError) Color(0xFFD32F2F) else Color.Transparent,
                            unfocusedBorderColor = if (isError) Color(0xFFD32F2F) else Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    if (isError && errorMessage.isNotBlank()) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFD32F2F),
                            fontSize = 12.sp,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 16.dp, top = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Send Reset Link Button
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        validateAndSubmit()
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LoginGreen,
                        disabledContainerColor = LoginGreen.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(28.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    if (isLoading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Sending...",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Text(
                            text = "Send Reset Link",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Bottom Login Link Text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Remember your password? ",
                        color = Color(0xFF757575),
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Login",
                        color = BrandGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun CheckmarkWithRays(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        // Sunburst ray dashes
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = size / 2f
            val radiusInner = size.width * 0.38f
            val radiusOuter = size.width * 0.46f
            val rayCount = 8

            for (i in 0 until rayCount) {
                val angle = Math.toRadians((i * (360f / rayCount)).toDouble())
                val startX = center.width + (radiusInner * cos(angle)).toFloat()
                val startY = center.height + (radiusInner * sin(angle)).toFloat()
                val endX = center.width + (radiusOuter * cos(angle)).toFloat()
                val endY = center.height + (radiusOuter * sin(angle)).toFloat()

                drawLine(
                    color = BrandGreen,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Green Circle Container with Checkmark
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFFC8E6C9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success Checkmark",
                tint = BrandGreen,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
