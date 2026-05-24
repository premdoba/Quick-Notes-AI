package com.example.quicknotes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 🌑 Dark Theme — Matte Black + Electric Cyan
private val GraphiteVioletDarkTheme = darkColorScheme(

    primary = Color(0xFF37E2FF),
    secondary = Color(0xFF00B8D4),
    tertiary = Color(0xFFB6F7FF),

    background = Color(0xFF030405),
    surface = Color(0xFF0D1114),
    surfaceVariant = Color(0xFF161C21),

    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,

    onBackground = Color(0xFFF2FCFF),
    onSurface = Color(0xFFF2FCFF),
    onSurfaceVariant = Color(0xFFABC0C7),

    outline = Color(0xFF273239),
    outlineVariant = Color(0xFF1A2126)
)

// ☀️ Light Theme — Clean Cyan
private val GraphiteVioletLightTheme = lightColorScheme(

    primary = Color(0xFF0097A7),
    secondary = Color(0xFF007C91),
    tertiary = Color(0xFFA5F3FC),

    background = Color(0xFFF5FDFF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFEAF7FA),

    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,

    onBackground = Color(0xFF102126),
    onSurface = Color(0xFF102126),
    onSurfaceVariant = Color(0xFF55707A),

    outline = Color(0xFFD0E4E8),
    outlineVariant = Color(0xFFE6F4F7)
)

@Composable
fun QuickNotesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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