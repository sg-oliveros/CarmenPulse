package com.example.carmenpulse.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF00390A),
    primaryContainer = Color(0xFF1B4D2E),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFF66BB6A),
    onSecondary = Color(0xFF00390B),
    secondaryContainer = Color(0xFF2E5135),
    onSecondaryContainer = Color(0xFFE8F5E9),
    tertiary = Color(0xFFA5D6A7),
    onTertiary = Color(0xFF00380E),
    background = Color(0xFF101411),
    onBackground = Color(0xFFE1E3DF),
    surface = Color(0xFF1B211C),
    onSurface = Color(0xFFE1E3DF),
    surfaceVariant = Color(0xFF2D352E),
    onSurfaceVariant = Color(0xFFC2C9C0),
    outline = Color(0xFF8C938B),
    outlineVariant = Color(0xFF424942)
)

private val LightColorScheme = lightColorScheme(
    primary = LoginGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF002105),
    secondary = BrandGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E9),
    onSecondaryContainer = Color(0xFF1B5E20),
    tertiary = Color(0xFF388E3C),
    onTertiary = Color.White,
    background = Color(0xFFF6FBF6),
    onBackground = Color(0xFF191C19),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF191C19),
    surfaceVariant = Color(0xFFE0E5DD),
    onSurfaceVariant = Color(0xFF424940),
    outline = Color(0xFF72796F),
    outlineVariant = Color(0xFFC2C9BD)
)

@Composable
fun CarmenPulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
