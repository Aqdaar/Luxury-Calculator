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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.ui.theme.LuxuryPalette
import com.example.ui.theme.LuxuryPalettes
import com.example.ui.theme.WallpaperStyle
import com.example.ui.theme.BebasNeueFontFamily

@Composable
fun WallpaperThemeDialog(
    currentWallpaper: WallpaperStyle,
    onSelectWallpaper: (WallpaperStyle) -> Unit,
    currentAccentColor: Color,
    onSelectAccentColor: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    var customHue by remember { mutableFloatStateOf(42f) } // Default warm gold hue
    var isCustomPickerActive by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF161820),
        shape = RoundedCornerShape(26.dp),
        modifier = Modifier.testTag("wallpaper_theme_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Wallpaper,
                    contentDescription = null,
                    tint = currentAccentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "WALLPAPER & COLOR THEME",
                    fontFamily = BebasNeueFontFamily,
                    fontSize = 22.sp,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Section 1: Wallpaper Selection
                Text(
                    text = "LUXURY WALLPAPER BACKDROP",
                    fontFamily = BebasNeueFontFamily,
                    fontSize = 14.sp,
                    color = currentAccentColor,
                    letterSpacing = 0.5.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    WallpaperStyle.values().forEach { style ->
                        val isSelected = currentWallpaper == style
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) currentAccentColor.copy(alpha = 0.22f) else Color(0x10FFFFFF)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) currentAccentColor else Color(0x15FFFFFF),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectWallpaper(style) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = style.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else Color(0xFFCAD0DC)
                                )
                                Text(
                                    text = style.description,
                                    fontSize = 10.sp,
                                    color = Color(0xFF7E8494)
                                )
                            }
                        }
                    }
                }

                // Section 2: Curated Luxury Palettes
                Text(
                    text = "CURATED LUXURY PALETTES",
                    fontFamily = BebasNeueFontFamily,
                    fontSize = 14.sp,
                    color = currentAccentColor,
                    letterSpacing = 0.5.sp
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(LuxuryPalettes.all) { palette ->
                        val isSelected = currentAccentColor == palette.primaryAccent
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    isCustomPickerActive = false
                                    onSelectAccentColor(palette.primaryAccent)
                                }
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(palette.primaryAccent)
                                    .border(
                                        2.dp,
                                        if (isSelected) Color.White else Color(0x40000000),
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = palette.name.split(" ")[0],
                                fontSize = 10.sp,
                                color = if (isSelected) currentAccentColor else Color(0xFF8E95A5)
                            )
                        }
                    }
                }

                // Section 3: Color Picker (Hue Wheel Slider)
                Text(
                    text = "CUSTOM COLOR PICKER",
                    fontFamily = BebasNeueFontFamily,
                    fontSize = 14.sp,
                    color = currentAccentColor,
                    letterSpacing = 0.5.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = "Color Picker",
                        tint = currentAccentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(
                        value = customHue,
                        onValueChange = { hue ->
                            customHue = hue
                            isCustomPickerActive = true
                            val customColor = Color.hsv(hue, 0.85f, 0.98f)
                            onSelectAccentColor(customColor)
                        },
                        valueRange = 0f..360f,
                        colors = SliderDefaults.colors(
                            thumbColor = currentAccentColor,
                            activeTrackColor = currentAccentColor,
                            inactiveTrackColor = Color(0x33FFFFFF)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(currentAccentColor)
                            .border(1.dp, Color.White, CircleShape)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = currentAccentColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Apply Theme", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFF9CA3AF))
            }
        }
    )
}
