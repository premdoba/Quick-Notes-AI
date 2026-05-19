package com.example.quicknotes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 🌙 Dark Theme (your current theme)
private val GraphiteVioletDarkTheme = darkColorScheme(
    primary = Color(0xFF9D7BFF),
    secondary = Color(0xFF7663B8),
    tertiary = Color(0xFFFFB86C),

    background = Color(0xFF0A0A0D),
    surface = Color(0xFF141418),
    surfaceVariant = Color(0xFF1F1F26),

    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.Black,

    onBackground = Color(0xFFF1F1F1),
    onSurface = Color(0xFFF1F1F1),
    onSurfaceVariant = Color(0xFFB8B8B8),

    outline = Color(0xFF2E2E36),
    outlineVariant = Color(0xFF1C1C22)
)

// ☀️ Light Theme (matching style)
private val GraphiteVioletLightTheme = lightColorScheme(
    primary = Color(0xFF6B4DFF),        // Stronger violet for light mode
    secondary = Color(0xFF5C4BA0),      // Muted violet
    tertiary = Color(0xFFFF8C42),       // Warm orange accent

    background = Color(0xFFF8F8FC),     // Soft off-white
    surface = Color(0xFFFFFFFF),        // Pure white surface
    surfaceVariant = Color(0xFFF0F0F7), // Light card surface

    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,

    onBackground = Color(0xFF121212),
    onSurface = Color(0xFF121212),
    onSurfaceVariant = Color(0xFF55555F),

    outline = Color(0xFFCCCCD6),
    outlineVariant = Color(0xFFE5E5EF)
)

@Composable
fun QuickNotesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // auto device theme
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        GraphiteVioletDarkTheme
    } else {
        GraphiteVioletLightTheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}