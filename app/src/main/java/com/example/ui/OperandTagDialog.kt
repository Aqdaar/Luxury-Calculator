package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BebasNeueFontFamily

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OperandTagDialog(
    operandValue: String,
    currentTag: String?,
    dynamicSuggestions: List<String>,
    accentColor: Color,
    onSaveTag: (String) -> Unit,
    onClearTag: () -> Unit,
    onDismiss: () -> Unit
) {
    var tagText by remember { mutableStateOf(currentTag ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF161820),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.testTag("operand_tag_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Label,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "NAME THIS VALUE: $operandValue",
                    fontFamily = BebasNeueFontFamily,
                    fontSize = 22.sp,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Assign a label to replace 'unknown' ($operandValue = boxes)",
                    fontSize = 12.sp,
                    color = Color(0xFF9EABB8)
                )

                OutlinedTextField(
                    value = tagText,
                    onValueChange = { tagText = it },
                    placeholder = { Text("boxes, people, hours", color = Color(0xFF6B7280)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("operand_tag_input")
                )

                Text(
                    text = "FREQUENT & RECENT TAGS",
                    fontFamily = BebasNeueFontFamily,
                    fontSize = 13.sp,
                    color = accentColor,
                    letterSpacing = 0.5.sp
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    dynamicSuggestions.take(8).forEach { suggestion ->
                        val isSelected = tagText.equals(suggestion, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) accentColor.copy(alpha = 0.25f) else Color(0x12FFFFFF)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) accentColor else Color(0x18FFFFFF),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { tagText = suggestion }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = suggestion,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFFC7CDD8)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tagText.isNotBlank()) {
                        onSaveTag(tagText.trim())
                    } else {
                        onClearTag()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Tag", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row {
                if (!currentTag.isNullOrBlank()) {
                    TextButton(onClick = onClearTag) {
                        Text("Reset to Unknown", color = Color(0xFFEF4444), fontSize = 12.sp)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Color(0xFF9CA3AF))
                }
            }
        }
    )
}
