package com.example.carmenpulse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Surface
import com.example.carmenpulse.ui.theme.BrandGreen

@Composable
fun AuthToggleSwitch(
    isLoginSelected: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .width(280.dp)
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color(0xFFF5F5F5)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (isLoginSelected) BrandGreen else Color.Transparent)
                    .clickable { onToggle(true) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Login",
                    color = if (isLoginSelected) Color.White else Color.LightGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (!isLoginSelected) BrandGreen else Color.Transparent)
                    .clickable { onToggle(false) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sign Up",
                    color = if (!isLoginSelected) Color.White else Color.LightGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}
