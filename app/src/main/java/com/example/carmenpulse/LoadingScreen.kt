package com.example.carmenpulse

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(onFinished: () -> Unit) {
    val titleAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        titleAlpha.animateTo(1f, animationSpec = tween(800))
        delay(1700)
        onFinished()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // CarmenPulse Title + Heartbeat Logo
        val primaryColor = MaterialTheme.colorScheme.primary
        Row(
            modifier = Modifier.alpha(titleAlpha.value),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CarmenPulse",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
            Spacer(modifier = Modifier.width(8.dp))
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
                    color = primaryColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Getting things ready...",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.alpha(titleAlpha.value)
        )

        Spacer(modifier = Modifier.height(32.dp))

        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.alpha(titleAlpha.value)
        )
    }
}
