package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BebasNeueFontFamily

/**
 * Apple-grade Liquid Glass Button with realistic frosted backdrop,
 * upper glossy meniscus refraction, specular rim glint, and dynamic accent tinting.
 */
@Composable
fun LiquidGlassButton(
    label: String,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActiveOperator: Boolean = false,
    accentColor: Color = Color(0xFFFFB800),
    fontSize: Int = 36
) {
    var isPressed by remember { mutableStateOf(false) }

    // Fluid spring bounce scale animation on tap
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.91f else 1.0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 650f),
        label = "LiquidGlassScale"
    )

    // Frosted glass background brush
    val backgroundBrush = remember(isPressed, isActiveOperator, accentColor) {
        when {
            isActiveOperator -> Brush.verticalGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.45f),
                    accentColor.copy(alpha = 0.20f),
                    Color(0x1A000000)
                )
            )
            isPressed -> Brush.verticalGradient(
                colors = listOf(
                    Color(0x40FFFFFF),
                    Color(0x18FFFFFF),
                    Color(0x25000000)
                )
            )
            else -> Brush.verticalGradient(
                colors = listOf(
                    Color(0x24FFFFFF), // Translucent frosted top
                    Color(0x0CFFFFFF), // Mid fade
                    Color(0x18000000)  // Deep shadow floor
                )
            )
        }
    }

    // Specular border rim
    val specularRimBrush = remember(isActiveOperator, accentColor) {
        if (isActiveOperator) {
            Brush.linearGradient(
                colors = listOf(
                    accentColor,
                    accentColor.copy(alpha = 0.6f),
                    Color(0x30FFFFFF)
                ),
                start = Offset.Zero,
                end = Offset(200f, 200f)
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color(0x60FFFFFF), // Bright light source top-left
                    Color(0x20FFFFFF),
                    Color(0x08FFFFFF),
                    Color(0x30000000)  // Ambient bottom occlusion
                ),
                start = Offset.Zero,
                end = Offset(180f, 180f)
            )
        }
    }

    val shape = RoundedCornerShape(26.dp)

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 64.dp, minHeight = 64.dp)
            .scale(scale)
            .clip(shape)
            .background(brush = backgroundBrush, shape = shape)
            .border(width = 1.2.dp, brush = specularRimBrush, shape = shape)
            .semantics {
                role = Role.Button
                contentDescription = getAccessibilityLabel(label)
            }
            .pointerInput(label) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
            .testTag("btn_${sanitizeTag(label)}"),
        contentAlignment = Alignment.Center
    ) {
        // Subtle upper glossy reflection sheen (Apple liquid glass meniscus)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x30FFFFFF),
                        Color(0x05FFFFFF),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = size.height * 0.52f
                ),
                topLeft = Offset(2f, 2f),
                size = Size(size.width - 4f, size.height * 0.48f),
                cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx())
            )
        }

        if (label == "⌫") {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Backspace",
                tint = if (isActiveOperator) accentColor else textColor,
                modifier = Modifier.size(26.dp)
            )
        } else {
            Text(
                text = label,
                fontFamily = BebasNeueFontFamily,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Normal,
                color = if (isActiveOperator) accentColor else textColor,
                letterSpacing = 0.5.sp
            )
        }
    }
}

private fun getAccessibilityLabel(label: String): String {
    return when (label) {
        "C" -> "Clear all"
        "%" -> "Percent"
        "⌫" -> "Backspace"
        "÷" -> "Divide"
        "×" -> "Multiply"
        "-" -> "Minus"
        "+" -> "Plus"
        "=" -> "Equals"
        "." -> "Point"
        "+/-" -> "Plus or minus"
        else -> label
    }
}

private fun sanitizeTag(label: String): String {
    return when (label) {
        "+" -> "plus"
        "-" -> "minus"
        "×" -> "multiply"
        "÷" -> "divide"
        "=" -> "equals"
        "." -> "decimal"
        "%" -> "percent"
        "⌫" -> "backspace"
        "+/-" -> "plus_minus"
        "C" -> "clear"
        else -> label
    }
}
