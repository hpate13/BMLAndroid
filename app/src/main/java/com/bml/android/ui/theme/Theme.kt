package com.bml.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// BTD6-inspired palette: warm balloon oranges/reds.
private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB74D),
    secondary = Color(0xFFEF5350),
    tertiary = Color(0xFFFF8A65),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFFE65100),
    secondary = Color(0xFFD32F2F),
    tertiary = Color(0xFFF57C00),
)

@Composable
fun BMLTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
