package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BebasNeueFontFamily
import com.example.ui.theme.LuxuryPalettes
import com.example.ui.theme.LuxuryWallpaperBackground
import com.example.ui.theme.WallpaperStyle

@Composable
fun OnboardingFlow(
    selectedWallpaper: WallpaperStyle,
    onSelectWallpaper: (WallpaperStyle) -> Unit,
    selectedAccentColor: Color,
    onSelectAccentColor: (Color) -> Unit,
    isTalkBackEnabled: Boolean,
    onTalkBackToggle: (Boolean) -> Unit,
    isAmbientEnabled: Boolean,
    onAmbientToggle: (Boolean) -> Unit,
    onCompleteOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableIntStateOf(0) }
    var customHue by remember { mutableFloatStateOf(42f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("onboarding_flow")
    ) {
        // Dynamic procedural wallpaper under the onboarding cards
        LuxuryWallpaperBackground(
            wallpaperStyle = selectedWallpaper,
            accentColor = selectedAccentColor
        )

        // Dim overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xBB0A0B0E))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (currentPage < 3) {
                    TextButton(onClick = onCompleteOnboarding) {
                        Text("Skip", color = Color(0xFF9CA3AF), fontSize = 14.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.height(36.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Page Content Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (currentPage) {
                    0 -> OnboardingArtCard1(selectedAccentColor)
                    1 -> OnboardingWallpaperCard(
                        selectedWallpaper = selectedWallpaper,
                        onSelectWallpaper = onSelectWallpaper,
                        selectedAccentColor = selectedAccentColor,
                        onSelectAccentColor = onSelectAccentColor,
                        customHue = customHue,
                        onCustomHueChange = { hue ->
                            customHue = hue
                            onSelectAccentColor(Color.hsv(hue, 0.85f, 0.98f))
                        }
                    )
                    2 -> OnboardingKeepCard(selectedAccentColor)
                    3 -> OnboardingTalkBackCard(
                        selectedAccentColor = selectedAccentColor,
                        isTalkBackEnabled = isTalkBackEnabled,
                        onTalkBackToggle = onTalkBackToggle,
                        isAmbientEnabled = isAmbientEnabled,
                        onAmbientToggle = onAmbientToggle
                    )
                }
            }

            // Bottom Navigation & Page Indicator Dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Indicator Dots
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 0..3) {
                        Box(
                            modifier = Modifier
                                .size(if (i == currentPage) 24.dp else 8.dp, 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (i == currentPage) selectedAccentColor else Color(0x33FFFFFF)
                                )
                        )
                    }
                }

                // Next or Get Started Button
                Button(
                    onClick = {
                        if (currentPage < 3) {
                            currentPage++
                        } else {
                            onCompleteOnboarding()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = selectedAccentColor),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.testTag("onboarding_next_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (currentPage < 3) "Continue" else "Start Calculating",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (currentPage < 3) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingArtCard1(accentColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Sculpted Art Graphic
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.35f),
                            Color(0x15FFFFFF),
                            Color.Transparent
                        )
                    )
                )
                .border(1.5.dp, accentColor.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(72.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "LUXURY CALCULATOR",
            fontFamily = BebasNeueFontFamily,
            fontSize = 34.sp,
            color = Color.White,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Experience precision calculation elevated with Apple liquid glass optics, sculpted 3D chiseled numerals, and sensory elegance.",
            fontSize = 14.sp,
            color = Color(0xFFBAC0CD),
            textAlign = TextAlign.Center,
            lineHeight = 21.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun OnboardingWallpaperCard(
    selectedWallpaper: WallpaperStyle,
    onSelectWallpaper: (WallpaperStyle) -> Unit,
    selectedAccentColor: Color,
    onSelectAccentColor: (Color) -> Unit,
    customHue: Float,
    onCustomHueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0x30FFFFFF))
            .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(24.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Wallpaper,
                contentDescription = null,
                tint = selectedAccentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "CHOOSE YOUR ATMOSPHERE",
                fontFamily = BebasNeueFontFamily,
                fontSize = 22.sp,
                color = Color.White,
                letterSpacing = 1.sp
            )
        }

        Text(
            text = "Select your starting luxury wallpaper backdrop and accent glow.",
            fontSize = 12.sp,
            color = Color(0xFF9EABB8)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Wallpaper selector
        WallpaperStyle.values().forEach { style ->
            val isSelected = selectedWallpaper == style
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected) selectedAccentColor.copy(alpha = 0.22f) else Color(0x12FFFFFF)
                    )
                    .border(
                        1.dp,
                        if (isSelected) selectedAccentColor else Color(0x18FFFFFF),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelectWallpaper(style) }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = style.displayName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.White else Color(0xFFC7CDD8),
                    modifier = Modifier.weight(1f)
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = selectedAccentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "COLOR PALETTE & PICKER",
            fontFamily = BebasNeueFontFamily,
            fontSize = 14.sp,
            color = selectedAccentColor
        )

        // Palette circles
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            items(LuxuryPalettes.all) { palette ->
                val isSelected = selectedAccentColor == palette.primaryAccent
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(palette.primaryAccent)
                        .border(
                            2.dp,
                            if (isSelected) Color.White else Color(0x40000000),
                            CircleShape
                        )
                        .clickable { onSelectAccentColor(palette.primaryAccent) }
                )
            }
        }

        // Custom Hue slider
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.ColorLens,
                contentDescription = null,
                tint = selectedAccentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Slider(
                value = customHue,
                onValueChange = onCustomHueChange,
                valueRange = 0f..360f,
                colors = SliderDefaults.colors(
                    thumbColor = selectedAccentColor,
                    activeTrackColor = selectedAccentColor,
                    inactiveTrackColor = Color(0x33FFFFFF)
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OnboardingKeepCard(accentColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0x30FFFFFF))
            .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(24.dp))
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Notes,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(54.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "KEEP CALCULATION JOURNAL",
            fontFamily = BebasNeueFontFamily,
            fontSize = 26.sp,
            color = Color.White,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Never lose calculations. Save and name entries in a Google Keep-style visual card grid.",
            fontSize = 13.sp,
            color = Color(0xFFBAC0CD),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Feature checklist
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            KeepFeatureBullet("Search by formula, title, or result numbers", accentColor)
            KeepFeatureBullet("One-tap snippet copy & share to Drive / WhatsApp", accentColor)
            KeepFeatureBullet("Call back saved totals right into live keypad", accentColor)
            KeepFeatureBullet("Presets: Grocery Split, Fuel, Mortgage & Dining", accentColor)
        }
    }
}

@Composable
private fun KeepFeatureBullet(text: String, accentColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 12.sp, color = Color(0xFFD6DBE5))
    }
}

@Composable
private fun OnboardingTalkBackCard(
    selectedAccentColor: Color,
    isTalkBackEnabled: Boolean,
    onTalkBackToggle: (Boolean) -> Unit,
    isAmbientEnabled: Boolean,
    onAmbientToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0x30FFFFFF))
            .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.RecordVoiceOver,
                contentDescription = null,
                tint = selectedAccentColor,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "TALKBACK & SOUNDSCAPES",
                fontFamily = BebasNeueFontFamily,
                fontSize = 24.sp,
                color = Color.White,
                letterSpacing = 1.sp
            )
        }

        Text(
            text = "Fine-tune spoken accessibility audio and acoustic harmonic soundscapes.",
            fontSize = 12.sp,
            color = Color(0xFF9EABB8)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // TalkBack Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "TalkBack Spoken Feedback",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text(
                    text = "Announces digits, operations, and results for blind and visually impaired users.",
                    fontSize = 11.sp,
                    color = Color(0xFF8E95A5)
                )
            }
            Switch(
                checked = isTalkBackEnabled,
                onCheckedChange = onTalkBackToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = selectedAccentColor
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Ambient Soundscape Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ambient Soundscape",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text(
                    text = "Zen singing bowls, theta alpha drone, and crystal rain harmonics.",
                    fontSize = 11.sp,
                    color = Color(0xFF8E95A5)
                )
            }
            Switch(
                checked = isAmbientEnabled,
                onCheckedChange = onAmbientToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = selectedAccentColor
                )
            )
        }
    }
}
