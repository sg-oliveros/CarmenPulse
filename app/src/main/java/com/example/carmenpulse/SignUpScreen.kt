package com.example.carmenpulse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextDecoration
import com.example.carmenpulse.ui.components.CustomTextField

@Composable
fun SignUpScreen(onNavigateToLogin: () -> Unit) {
    // State variables for sign-up form inputs and password visibility toggle
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top spacing to keep the form away from the green background curve
        Spacer(modifier = Modifier.height(75.dp))

        // Full Name input field
        CustomTextField(value = fullName, onValueChange = { fullName = it }, label = "Full Name")
        Spacer(modifier = Modifier.height(12.dp))

        // E-mail input field
        CustomTextField(value = email, onValueChange = { email = it }, label = "E-mail")
        Spacer(modifier = Modifier.height(12.dp))

        // Password input field
        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true,
            isPasswordVisible = passwordVisible,
            onVisibilityToggle = { passwordVisible = !passwordVisible }
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Confirm Password input field
        CustomTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            isPassword = true,
            isPasswordVisible = passwordVisible,
            onVisibilityToggle = { passwordVisible = !passwordVisible }
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Sign Up submit button
        Button(
            onClick = { /* TODO: Implement sign-up registration logic */ },
            modifier = Modifier.fillMaxWidth(0.85f).height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(30.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Text("Sign Up", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bottom row to navigate back to the Login screen
        Row {
            Text("Already Have An Account? ", color = Color.White, fontSize = 12.sp)
            Text(
                text = "Login",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
}
