package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BebasNeueFontFamily
import com.example.ui.theme.ChiselDeepExtrusion
import com.example.ui.theme.ChiselShadedFacet
import com.example.ui.theme.ChiselShadow
import com.example.ui.theme.ChiselWhiteHighlight
import kotlinx.coroutines.flow.SharedFlow

/**
 * 3D Chisel Bevel dynamic auto-scaling hero display with centered single operator
 * and interactive operand sub-tags ("unknown" -> user tagged).
 */
@Composable
fun Chisel3DDisplay(
    displayText: String,
    formulaText: String,
    firstOperand: String?,
    secondOperand: String?,
    activeOperator: String?,
    firstOperandTag: String?,
    secondOperandTag: String?,
    accentColor: Color,
    isCalculationSuccess: Boolean,
    successPulseFlow: SharedFlow<Unit>,
    onOperandClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scalePulse = remember { Animatable(1.0f) }
    val bloomGlow = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        successPulseFlow.collect {
            scalePulse.animateTo(
                targetValue = 1.05f,
                animationSpec = tween(durationMillis = 140)
            )
            scalePulse.animateTo(
                targetValue = 1.0f,
                animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium)
            )
        }
    }

    LaunchedEffect(isCalculationSuccess) {
        if (isCalculationSuccess) {
            bloomGlow.animateTo(1.0f, animationSpec = tween(120))
            bloomGlow.animateTo(0.0f, animationSpec = tween(400))
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("chisel_3d_display"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Operand Columns with SINGLE CENTERED OPERATOR (e.g. 65 [boxes] × 335 [people])
        if (firstOperand != null && activeOperator != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 2.dp)
                    .testTag("interactive_operand_row")
            ) {
                // Operand 1 Block: Tag pill on top, Number underneath (both clickable)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOperandClick(1) }
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .testTag("operand_block_1")
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (!firstOperandTag.isNullOrBlank() && firstOperandTag != "unknown") {
                                    accentColor.copy(alpha = 0.25f)
                                } else Color(0x18FFFFFF)
                            )
                            .border(
                                1.dp,
                                if (!firstOperandTag.isNullOrBlank() && firstOperandTag != "unknown") {
                                    accentColor.copy(alpha = 0.6f)
                                } else Color(0x28FFFFFF),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = firstOperandTag?.ifBlank { "unknown" } ?: "unknown",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (!firstOperandTag.isNullOrBlank() && firstOperandTag != "unknown") {
                                accentColor
                            } else Color(0xFF9EABB8)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = firstOperand,
                        fontFamily = BebasNeueFontFamily,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // SINGLE CENTERED OPERATOR (appears only once in the middle between the operand blocks)
                Text(
                    text = activeOperator,
                    fontFamily = BebasNeueFontFamily,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                // Operand 2 Block: Tag pill on top, Number underneath (both clickable)
                if (!secondOperand.isNullOrEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onOperandClick(2) }
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                            .testTag("operand_block_2")
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (!secondOperandTag.isNullOrBlank() && secondOperandTag != "unknown") {
                                        accentColor.copy(alpha = 0.25f)
                                    } else Color(0x18FFFFFF)
                                )
                                .border(
                                    1.dp,
                                    if (!secondOperandTag.isNullOrBlank() && secondOperandTag != "unknown") {
                                        accentColor.copy(alpha = 0.6f)
                                    } else Color(0x28FFFFFF),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = secondOperandTag?.ifBlank { "unknown" } ?: "unknown",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (!secondOperandTag.isNullOrBlank() && secondOperandTag != "unknown") {
                                    accentColor
                                } else Color(0xFF9EABB8)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = secondOperand,
                            fontFamily = BebasNeueFontFamily,
                            fontSize = 24.sp,
                            color = Color.White
                        )
                    }
                }
            }
        } else if (displayText != "0") {
            // Idle/first entry: show tag pill on top, clickable to name before adding operators
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOperandClick(1) }
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .testTag("operand_block_1")
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (!firstOperandTag.isNullOrBlank() && firstOperandTag != "unknown") {
                                accentColor.copy(alpha = 0.25f)
                            } else Color(0x18FFFFFF)
                        )
                        .border(
                            1.dp,
                            if (!firstOperandTag.isNullOrBlank() && firstOperandTag != "unknown") {
                                accentColor.copy(alpha = 0.6f)
                            } else Color(0x28FFFFFF),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = firstOperandTag?.ifBlank { "unknown" } ?: "unknown",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (!firstOperandTag.isNullOrBlank() && firstOperandTag != "unknown") {
                            accentColor
                        } else Color(0xFF9EABB8)
                    )
                }
            }
        }

        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val availableWidth = maxWidth.value
            val charCount = displayText.length

            val baseSp = when {
                charCount <= 1 -> 175f
                charCount == 2 -> 135f
                charCount == 3 -> 110f
                charCount == 4 -> 88f
                charCount == 5 -> 72f
                charCount in 6..7 -> 58f
                charCount in 8..10 -> 46f
                charCount in 11..13 -> 36f
                else -> 28f
            }

            val widthFactor = (availableWidth / 360f).coerceIn(0.75f, 1.3f)
            val targetFontSize = (baseSp * widthFactor).coerceIn(24f, 185f)

            val animatedFontSize by animateFloatAsState(
                targetValue = targetFontSize,
                animationSpec = spring(
                    dampingRatio = 0.82f,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "WhatsAppStyleFontScaling"
            )

            val fontSp = animatedFontSize.sp

            if (displayText == "0" && formulaText.isEmpty()) {
                SculptedZeroDisplay(
                    modifier = Modifier
                        .scale(scalePulse.value)
                        .padding(vertical = 12.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .scale(scalePulse.value)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Pass 1: Deep ambient directional cast shadow
                    Text(
                        text = displayText,
                        fontFamily = BebasNeueFontFamily,
                        fontSize = fontSp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            color = Color.Transparent,
                            shadow = Shadow(
                                color = ChiselShadow,
                                offset = Offset(-18f, 32f),
                                blurRadius = 48f
                            )
                        ),
                        modifier = Modifier
                            .offset(x = (-4).dp, y = 8.dp)
                            .alpha(0.85f)
                    )

                    // Pass 2: Directional extrusion rim
                    Text(
                        text = displayText,
                        fontFamily = BebasNeueFontFamily,
                        fontSize = fontSp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = ChiselDeepExtrusion,
                        modifier = Modifier.offset(x = (-2).dp, y = 4.dp)
                    )

                    // Pass 3: Shaded bevel slope
                    Text(
                        text = displayText,
                        fontFamily = BebasNeueFontFamily,
                        fontSize = fontSp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = ChiselShadedFacet,
                        modifier = Modifier.offset(x = (-1).dp, y = 2.dp)
                    )

                    // Pass 4: Front illuminated chisel face with linear gradient brush & theme accent glint
                    Text(
                        text = displayText,
                        fontFamily = BebasNeueFontFamily,
                        fontSize = fontSp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    ChiselWhiteHighlight,
                                    Color(0xFFE2E7EE),
                                    accentColor.copy(alpha = 0.35f),
                                    ChiselWhiteHighlight
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(400f, 600f)
                            ),
                            shadow = Shadow(
                                color = accentColor.copy(alpha = 0.4f),
                                offset = Offset(0f, -2f),
                                blurRadius = 8f
                            )
                        )
                    )

                    // Pass 5: Bloom glow overlay on completion
                    if (bloomGlow.value > 0.01f) {
                        Text(
                            text = displayText,
                            fontFamily = BebasNeueFontFamily,
                            fontSize = fontSp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White.copy(alpha = bloomGlow.value * 0.45f),
                            modifier = Modifier.blur(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SculptedZeroDisplay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(230.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val width = 76.dp.toPx()
            val height = 190.dp.toPx()
            val corner = width / 2f

            // 1. Directional cast drop shadow on matte dark slate
            drawRoundRect(
                color = Color(0xD9000000),
                topLeft = Offset(centerX - width / 2f - 22.dp.toPx(), centerY - height / 2f + 20.dp.toPx()),
                size = Size(width, height),
                cornerRadius = CornerRadius(corner, corner),
                alpha = 0.7f
            )

            // 2. Outer extruded 3D bevel body
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF5A5E66),
                        Color(0xFF33363D),
                        Color(0xFF1E2024),
                        Color(0xFF141518)
                    ),
                    start = Offset(centerX - width, centerY - height),
                    end = Offset(centerX + width, centerY + height)
                ),
                topLeft = Offset(centerX - width / 2f, centerY - height / 2f),
                size = Size(width, height),
                cornerRadius = CornerRadius(corner, corner)
            )

            // 3. Highlighted specular outer rim
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x99FFFFFF),
                        Color(0x22FFFFFF),
                        Color(0x00FFFFFF)
                    ),
                    start = Offset(centerX - width / 2f, centerY - height / 2f),
                    end = Offset(centerX + width / 2f, centerY + height / 2f)
                ),
                topLeft = Offset(centerX - width / 2f, centerY - height / 2f),
                size = Size(width, height),
                cornerRadius = CornerRadius(corner, corner),
                style = Stroke(width = 2.dp.toPx())
            )

            // 4. Recessed inner hollow slot
            val slotWidth = 14.dp.toPx()
            val slotHeight = 140.dp.toPx()
            val slotCorner = slotWidth / 2f

            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF090A0C),
                        Color(0xFF111215),
                        Color(0xFF1A1C20)
                    ),
                    start = Offset(centerX, centerY - slotHeight / 2f),
                    end = Offset(centerX, centerY + slotHeight / 2f)
                ),
                topLeft = Offset(centerX - slotWidth / 2f, centerY - slotHeight / 2f),
                size = Size(slotWidth, slotHeight),
                cornerRadius = CornerRadius(slotCorner, slotCorner)
            )

            drawRoundRect(
                color = Color(0x22FFFFFF),
                topLeft = Offset(centerX - slotWidth / 2f, centerY - slotHeight / 2f),
                size = Size(slotWidth, slotHeight),
                cornerRadius = CornerRadius(slotCorner, slotCorner),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}
