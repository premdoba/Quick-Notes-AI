package com.example.quicknotes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GraphiteVioletTheme = darkColorScheme(
    primary = Color(0xFF9D7BFF),        // Soft violet (premium highlight)
    secondary = Color(0xFF7663B8),   // Muted violet-gray (matches primary)
    tertiary = Color(0xFFFFB86C),       // Warm orange accent (small highlights)

    background = Color(0xFF0A0A0D),     // Deep black
    surface = Color(0xFF141418),        // Graphite surface
    surfaceVariant = Color(0xFF1F1F26), // Card color

    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.Black,

    onBackground = Color(0xFFF1F1F1),   // Soft white
    onSurface = Color(0xFFF1F1F1),
    onSurfaceVariant = Color(0xFFB8B8B8),

    outline = Color(0xFF2E2E36),
    outlineVariant = Color(0xFF1C1C22)
)

@Composable
fun QuickNotesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GraphiteVioletTheme,
        typography = Typography,
        content = content
    )
}