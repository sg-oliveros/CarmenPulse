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
import com.example.carmenpulse.ui.theme.DarkGreen

@Composable
fun LoginScreen(onNavigateToSignUp: () -> Unit) {
    // State variables for form inputs and password visibility toggle
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top spacing to keep the form away from the green background curve
        Spacer(modifier = Modifier.height(75.dp))

        // E-mail input field
        CustomTextField(value = email, onValueChange = { email = it }, label = "E-mail")
        Spacer(modifier = Modifier.height(20.dp))

        // Password input field with visibility toggle
        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true,
            isPasswordVisible = passwordVisible,
            onVisibilityToggle = { passwordVisible = !passwordVisible }
        )

        // Forgot password action link
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

        Spacer(modifier = Modifier.height(80.dp))

        // Login submit button
        Button(
            onClick = { /* TODO: Implement authentication login logic */ },
            modifier = Modifier.fillMaxWidth(0.85f).height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(30.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Text("Login", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bottom row to navigate to the Sign Up screen
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
