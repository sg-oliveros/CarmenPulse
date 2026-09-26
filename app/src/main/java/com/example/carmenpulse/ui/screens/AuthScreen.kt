package com.example.carmenpulse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import com.example.carmenpulse.LoginScreen
import com.example.carmenpulse.SignUpScreen
import com.example.carmenpulse.ui.components.AuthToggleSwitch
import com.example.carmenpulse.ui.components.CurvedBackground
import com.example.carmenpulse.ui.theme.BrandGreen

@Composable
fun AuthScreen() {
    var isLoginSelected by remember { mutableStateOf(true) }

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
                isLoginSelected = isLoginSelected,
                onToggle = { isLoginSelected = it }
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Form Content Layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp)
            ) {
                if (isLoginSelected) {
                    LoginScreen(onNavigateToSignUp = { isLoginSelected = false })
                } else {
                    SignUpScreen(onNavigateToLogin = { isLoginSelected = true })
                }
            }
        }
    }
}

@Composable
fun LogoWithHeartbeat() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "CarmenPulse",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = BrandGreen
        )
        Spacer(modifier = Modifier.width(8.dp))
        //for the logo and curve design
        Canvas(modifier = Modifier.size(40.dp, 30.dp)) {
            val path = Path().apply {
                val w = size.width
                val h = size.height
                moveTo(0f, h * 0.5f)
                lineTo(w * 0.2f, h * 0.5f)
                lineTo(w * 0.25f, h * 0.35f)
                lineTo(w * 0.35f, h * 0.65f)
                lineTo(w * 0.4f, h * 0.5f)
                lineTo(w * 0.55f, h * 0.5f)
                lineTo(w * 0.65f, h * 0.1f)
                lineTo(w * 0.75f, h * 0.9f)
                lineTo(w * 0.85f, h * 0.5f)
                lineTo(w, h * 0.5f)
            }
            drawPath(
                path = path,
                color = BrandGreen,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}
