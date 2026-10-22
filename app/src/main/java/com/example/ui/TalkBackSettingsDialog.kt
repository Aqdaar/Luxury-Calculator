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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundscapeMode
import com.example.ui.theme.BebasNeueFontFamily

@Composable
fun TalkBackSettingsDialog(
    isTalkBackEnabled: Boolean,
    onTalkBackToggle: (Boolean) -> Unit,
    isAmbientEnabled: Boolean,
    onAmbientToggle: (Boolean) -> Unit,
    soundscapeMode: SoundscapeMode,
    onSoundscapeModeChange: (SoundscapeMode) -> Unit,
    ambientVolume: Float,
    onVolumeChange: (Float) -> Unit,
    isHapticsEnabled: Boolean,
    onHapticsToggle: (Boolean) -> Unit,
    accentColor: Color,
    onOpenSystemAccessibility: () -> Unit,
    onTestVoice: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF161820),
        shape = RoundedCornerShape(26.dp),
        modifier = Modifier.testTag("talkback_settings_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "TALKBACK & AUDIO CONFIG",
                    fontFamily = BebasNeueFontFamily,
                    fontSize = 22.sp,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // 1. In-App TalkBack Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TalkBack Voice Feedback",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Spoken feedback for every keypress, operator, and calculation result.",
                            fontSize = 12.sp,
                            color = Color(0xFF8E95A5)
                        )
                    }
                    Switch(
                        checked = isTalkBackEnabled,
                        onCheckedChange = onTalkBackToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = accentColor,
                            uncheckedTrackColor = Color(0x30FFFFFF)
                        ),
                        modifier = Modifier.testTag("talkback_switch")
                    )
                }

                // System Accessibility settings launcher
                OutlinedButton(
                    onClick = onOpenSystemAccessibility,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_system_accessibility_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "System Accessibility Settings",
                        color = accentColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // 2. Ambient Soundscape Switch (Directly in TalkBack Config)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ambient Soundscape",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Soothing acoustic harmonic frequencies for focus.",
                            fontSize = 12.sp,
                            color = Color(0xFF8E95A5)
                        )
                    }
                    Switch(
                        checked = isAmbientEnabled,
                        onCheckedChange = onAmbientToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = accentColor,
                            uncheckedTrackColor = Color(0x30FFFFFF)
                        ),
                        modifier = Modifier.testTag("ambient_soundscape_switch")
                    )
                }

                // Soundscape Mode Selector
                if (isAmbientEnabled) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "SOUNDSCAPE HARMONIC MODE",
                            fontFamily = BebasNeueFontFamily,
                            fontSize = 13.sp,
                            color = accentColor,
                            letterSpacing = 0.5.sp
                        )

                        SoundscapeMode.values().forEach { mode ->
                            val isSelected = soundscapeMode == mode
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) accentColor.copy(alpha = 0.22f) else Color(0x10FFFFFF)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) accentColor else Color(0x15FFFFFF),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onSoundscapeModeChange(mode) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = mode.displayName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) Color.White else Color(0xFFCAD0DC)
                                    )
                                    Text(
                                        text = mode.description,
                                        fontSize = 10.sp,
                                        color = Color(0xFF7E8494)
                                    )
                                }
                            }
                        }

                        // Volume Slider
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Volume",
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Slider(
                                value = ambientVolume,
                                onValueChange = onVolumeChange,
                                colors = SliderDefaults.colors(
                                    thumbColor = accentColor,
                                    activeTrackColor = accentColor,
                                    inactiveTrackColor = Color(0x33FFFFFF)
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 3. Tactile Key Haptics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tactile Haptics",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Physical impulse on button press.",
                            fontSize = 11.sp,
                            color = Color(0xFF8E95A5)
                        )
                    }
                    Switch(
                        checked = isHapticsEnabled,
                        onCheckedChange = onHapticsToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = accentColor,
                            uncheckedTrackColor = Color(0x30FFFFFF)
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onTestVoice,
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Test Voice Readout", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = Color(0xFF9CA3AF))
            }
        }
    )
}
