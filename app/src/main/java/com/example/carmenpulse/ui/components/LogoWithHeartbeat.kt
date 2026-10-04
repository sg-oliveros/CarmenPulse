package com.example.carmenpulse.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carmenpulse.ui.theme.BrandGreen

@Composable
fun LogoWithHeartbeat(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "CarmenPulse",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = BrandGreen
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Canvas for the heartbeat curve design
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
