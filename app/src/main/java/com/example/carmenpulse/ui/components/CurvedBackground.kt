package com.example.carmenpulse.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import com.example.carmenpulse.ui.theme.BrandGreen

@Composable
fun CurvedBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            // Start lower on the sides
            moveTo(0f, height * 0.15f)
            // Peak at the very top of the canvas for a wide dome
            quadraticTo(
                width / 2f, 0f,
                width, height * 0.15f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = path,
            color = BrandGreen
        )
    }
}
