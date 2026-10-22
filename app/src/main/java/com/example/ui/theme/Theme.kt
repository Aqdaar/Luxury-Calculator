package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CalculatorDarkColorScheme = darkColorScheme(
    primary = AppleAmberOperator,
    onPrimary = Color.Black,
    secondary = RadiantCoralEquals,
    onSecondary = Color.White,
    tertiary = KeypadNumberText,
    background = DarkCanvas,
    onBackground = KeypadNumberText,
    surface = DarkSurface,
    onSurface = KeypadNumberText,
    surfaceVariant = DarkSurfaceHighlight,
    onSurfaceVariant = KeypadFuncText
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CalculatorDarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
