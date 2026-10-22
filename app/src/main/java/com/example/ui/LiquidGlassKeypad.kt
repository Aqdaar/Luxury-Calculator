package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KeypadFuncText
import com.example.ui.theme.KeypadNumberText

/**
 * 5x4 Grid layout replicating the luxury liquid glass keypad with dynamic theme accent colors.
 */
@Composable
fun LiquidGlassKeypad(
    activeOperator: String?,
    accentColor: Color,
    onButtonPress: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonRows = listOf(
        listOf(
            KeyItem("C", KeyType.FUNCTION),
            KeyItem("%", KeyType.FUNCTION),
            KeyItem("⌫", KeyType.FUNCTION),
            KeyItem("÷", KeyType.OPERATOR)
        ),
        listOf(
            KeyItem("7", KeyType.NUMBER),
            KeyItem("8", KeyType.NUMBER),
            KeyItem("9", KeyType.NUMBER),
            KeyItem("×", KeyType.OPERATOR)
        ),
        listOf(
            KeyItem("4", KeyType.NUMBER),
            KeyItem("5", KeyType.NUMBER),
            KeyItem("6", KeyType.NUMBER),
            KeyItem("-", KeyType.OPERATOR)
        ),
        listOf(
            KeyItem("1", KeyType.NUMBER),
            KeyItem("2", KeyType.NUMBER),
            KeyItem("3", KeyType.NUMBER),
            KeyItem("+", KeyType.OPERATOR)
        ),
        listOf(
            KeyItem("0", KeyType.NUMBER),
            KeyItem(".", KeyType.NUMBER),
            KeyItem("+/-", KeyType.NUMBER),
            KeyItem("=", KeyType.EQUALS)
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .testTag("liquid_glass_keypad"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        buttonRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                row.forEach { item ->
                    val textColor = when (item.type) {
                        KeyType.OPERATOR -> accentColor
                        KeyType.EQUALS -> Color(0xFFFF5E7E)
                        KeyType.FUNCTION -> KeypadFuncText
                        KeyType.NUMBER -> KeypadNumberText
                    }

                    val isActive = item.label == activeOperator

                    LiquidGlassButton(
                        label = item.label,
                        textColor = textColor,
                        isActiveOperator = isActive,
                        accentColor = accentColor,
                        fontSize = if (item.label == "+/-") 28 else 36,
                        onClick = { onButtonPress(item.label) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.15f)
                    )
                }
            }
        }
    }
}

private enum class KeyType {
    NUMBER,
    FUNCTION,
    OPERATOR,
    EQUALS
}

private data class KeyItem(
    val label: String,
    val type: KeyType
)
