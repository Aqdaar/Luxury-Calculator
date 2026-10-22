package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BebasNeueFontFamily

data class CalculationTemplate(
    val title: String,
    val category: String,
    val icon: ImageVector,
    val hint: String
)

val luxuryTemplates = listOf(
    CalculationTemplate("Grocery Split", "Grocery", Icons.Default.ShoppingCart, "Items, tax, and dividing among group"),
    CalculationTemplate("Fuel Consumption", "Fuel", Icons.Default.DirectionsCar, "Trip mileage, liters/gallons, and cost"),
    CalculationTemplate("Mortgage & Loan", "Mortgage", Icons.Default.Home, "Principal, rate interest, and monthly payment"),
    CalculationTemplate("Dining & Tip", "Dining", Icons.Default.Restaurant, "Bill total, gratuity tip %, and per-person split"),
    CalculationTemplate("Freelance & Tax", "Tax", Icons.Default.Receipt, "Hourly rate × billable hours - estimated tax"),
    CalculationTemplate("General Calc", "General", Icons.Default.Calculate, "Standard everyday luxury arithmetic")
)

@Composable
fun SessionNamingDialog(
    initialName: String,
    accentColor: Color,
    onConfirm: (sessionName: String, templateCategory: String) -> Unit,
    onDismiss: () -> Unit
) {
    var sessionName by remember { mutableStateOf(if (initialName == "General" || initialName == "New Calculation") "" else initialName) }
    var selectedCategory by remember { mutableStateOf("General") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF161820),
        shape = RoundedCornerShape(26.dp),
        modifier = Modifier.testTag("session_naming_dialog"),
        title = {
            Column {
                Text(
                    text = "WHAT ARE YOU CALCULATING TODAY?",
                    fontFamily = BebasNeueFontFamily,
                    fontSize = 24.sp,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Name your calculation or pick a quick luxury template",
                    fontSize = 12.sp,
                    color = Color(0xFF8E95A5)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Freeform name field
                OutlinedTextField(
                    value = sessionName,
                    onValueChange = { sessionName = it },
                    placeholder = { Text("Weekly Groceries, Roadtrip Fuel, Loft Remodel", fontSize = 13.sp, color = Color(0xFF6B7280)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("session_name_input")
                )

                Text(
                    text = "POPULAR USE CASE TEMPLATES",
                    fontFamily = BebasNeueFontFamily,
                    fontSize = 15.sp,
                    color = accentColor,
                    letterSpacing = 0.5.sp
                )

                // Template Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    luxuryTemplates.forEach { template ->
                        val isSelected = selectedCategory == template.category
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) accentColor.copy(alpha = 0.22f) else Color(0x10FFFFFF)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) accentColor else Color(0x18FFFFFF),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedCategory = template.category
                                    sessionName = template.title
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = template.icon,
                                contentDescription = template.title,
                                tint = if (isSelected) accentColor else Color(0xFFB0B7C6),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = template.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else Color(0xFFD1D5DB)
                                )
                                Text(
                                    text = template.hint,
                                    fontSize = 10.sp,
                                    color = Color(0xFF7E8494)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = sessionName.ifBlank { "Calculation" }
                    onConfirm(finalName, selectedCategory)
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("confirm_session_name_btn")
            ) {
                Text("Start Calculation", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onConfirm("General Calculation", "General")
                }
            ) {
                Text("Skip", color = Color(0xFF9CA3AF))
            }
        }
    )
}
