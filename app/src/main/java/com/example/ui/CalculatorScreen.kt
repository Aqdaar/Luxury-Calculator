package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.CalculatorViewModel
import com.example.ui.theme.LuxuryWallpaperBackground

/**
 * Main Calculator Screen delivering the exact minimalist dark luxury visual craft
 * with Apple liquid glass keys, procedural obsidian marble, centered single operator,
 * interactive operand sub-tags ("unknown" -> named values), Google Keep-style calculation
 * notebook, and TalkBack accessibility.
 */
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val savedCalculations by viewModel.savedCalculations.collectAsState()

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("calculator_screen")
    ) {
        // Procedural Luxury Wallpaper (Obsidian Gold Vein, Prismatic Glass, etc.)
        LuxuryWallpaperBackground(
            wallpaperStyle = uiState.wallpaperStyle,
            accentColor = uiState.accentColor
        )

        // Radial ambient aura behind hero numbers
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.TopCenter)
                .padding(top = statusBarPadding + 40.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            uiState.accentColor.copy(alpha = 0.14f),
                            Color(0x06FFFFFF),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = statusBarPadding, bottom = navBarPadding)
        ) {
            // Top Bar with Session Name on Left and Luxury Action Controls on Right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Top-Left: Active Calculation Name & Category Badge (Clickable to rename)
                Box(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x22FFFFFF))
                        .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(16.dp))
                        .clickable { viewModel.showSessionNameDialog(true) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("session_name_chip"),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🏷️ ${uiState.sessionName}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Rename Calculation",
                            tint = uiState.accentColor,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Top-Right Quick Action Group
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Google Keep History Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0x18FFFFFF))
                            .border(1.dp, Color(0x30FFFFFF), CircleShape)
                            .clickable { viewModel.showKeepHistory(true) }
                            .testTag("keep_history_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notes,
                            contentDescription = "Open Keep Calculation Notes",
                            tint = uiState.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // 2. Wallpaper & Color Theme Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0x18FFFFFF))
                            .border(1.dp, Color(0x30FFFFFF), CircleShape)
                            .clickable { viewModel.showWallpaperThemeSettings(true) }
                            .testTag("theme_picker_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wallpaper,
                            contentDescription = "Wallpaper and Color Picker",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // 3. TalkBack & Soundscape Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (uiState.isTalkBackEnabled || uiState.isAmbientEnabled) {
                                    uiState.accentColor.copy(alpha = 0.25f)
                                } else Color(0x18FFFFFF)
                            )
                            .border(
                                1.dp,
                                if (uiState.isTalkBackEnabled || uiState.isAmbientEnabled) {
                                    uiState.accentColor
                                } else Color(0x30FFFFFF),
                                CircleShape
                            )
                            .clickable { viewModel.showTalkBackSettings(true) }
                            .testTag("talkback_audio_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (uiState.isAmbientEnabled) Icons.Default.GraphicEq else Icons.Default.RecordVoiceOver,
                            contentDescription = "TalkBack Accessibility and Soundscapes",
                            tint = if (uiState.isTalkBackEnabled || uiState.isAmbientEnabled) uiState.accentColor else Color(0xFFD1D5DB),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Middle Display Area: 3D Faceted Auto-Scaling Chisel Output + Centered Single Operator and Operand Tags
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Chisel3DDisplay(
                    displayText = uiState.displayText,
                    formulaText = uiState.formulaText,
                    firstOperand = uiState.firstOperand,
                    secondOperand = uiState.secondOperand,
                    activeOperator = uiState.activeOperator,
                    firstOperandTag = uiState.firstOperandTag,
                    secondOperandTag = uiState.secondOperandTag,
                    accentColor = uiState.accentColor,
                    isCalculationSuccess = uiState.isCalculationSuccess,
                    successPulseFlow = viewModel.successAnimationPulse,
                    onOperandClick = { index -> viewModel.openOperandTagDialog(index) }
                )
            }

            // Bottom Area: Apple Liquid Glass Keypad
            LiquidGlassKeypad(
                activeOperator = uiState.activeOperator,
                accentColor = uiState.accentColor,
                onButtonPress = { viewModel.onButtonPress(it) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Overlay 1: Art-Filled Onboarding Flow
        if (uiState.showOnboarding) {
            OnboardingFlow(
                selectedWallpaper = uiState.wallpaperStyle,
                onSelectWallpaper = { viewModel.setWallpaper(it) },
                selectedAccentColor = uiState.accentColor,
                onSelectAccentColor = { viewModel.setAccentColor(it) },
                isTalkBackEnabled = uiState.isTalkBackEnabled,
                onTalkBackToggle = { viewModel.toggleTalkBack(it) },
                isAmbientEnabled = uiState.isAmbientEnabled,
                onAmbientToggle = { viewModel.toggleAmbient(it) },
                onCompleteOnboarding = { viewModel.completeOnboarding() }
            )
        }

        // Overlay 2: Google Keep Style Calculation Journal (with Notch insets & BackHandler)
        if (uiState.showKeepHistory) {
            KeepHistoryScreen(
                calculations = savedCalculations,
                customTags = uiState.customTags,
                accentColor = uiState.accentColor,
                onCallBack = { calc -> viewModel.callBackCalculation(calc) },
                onUpdateCalculation = { calc -> viewModel.updateCalculation(calc) },
                onDeleteCalculation = { id -> viewModel.deleteCalculation(id) },
                onDeleteCalculations = { ids -> viewModel.deleteCalculations(ids) },
                onAddTag = { viewModel.addCustomTag(it) },
                onDeleteTag = { viewModel.deleteCustomTag(it) },
                onDismiss = { viewModel.showKeepHistory(false) }
            )
        }

        // Overlay 3: Session Naming Modal ("What are you calculating today?")
        if (uiState.showSessionNameDialog) {
            SessionNamingDialog(
                initialName = uiState.sessionName,
                accentColor = uiState.accentColor,
                onConfirm = { name, category ->
                    viewModel.startNamedSession(name, category)
                },
                onDismiss = { viewModel.showSessionNameDialog(false) }
            )
        }

        // Overlay 4: TalkBack & Soundscape Config Dialog
        if (uiState.showTalkBackSettings) {
            TalkBackSettingsDialog(
                isTalkBackEnabled = uiState.isTalkBackEnabled,
                onTalkBackToggle = { viewModel.toggleTalkBack(it) },
                isAmbientEnabled = uiState.isAmbientEnabled,
                onAmbientToggle = { viewModel.toggleAmbient(it) },
                soundscapeMode = uiState.soundscapeMode,
                onSoundscapeModeChange = { viewModel.setSoundscapeMode(it) },
                ambientVolume = uiState.ambientVolume,
                onVolumeChange = { viewModel.setAmbientVolume(it) },
                isHapticsEnabled = uiState.isHapticsEnabled,
                onHapticsToggle = { viewModel.toggleHaptics(it) },
                accentColor = uiState.accentColor,
                onOpenSystemAccessibility = { viewModel.voiceManager.openAccessibilitySettings(context) },
                onTestVoice = { viewModel.voiceManager.speakAnnouncement("TalkBack audio readout is active on Luxury Calculator") },
                onDismiss = { viewModel.showTalkBackSettings(false) }
            )
        }

        // Overlay 5: Wallpaper & Color Theme Dialog
        if (uiState.showWallpaperThemeSettings) {
            WallpaperThemeDialog(
                currentWallpaper = uiState.wallpaperStyle,
                onSelectWallpaper = { viewModel.setWallpaper(it) },
                currentAccentColor = uiState.accentColor,
                onSelectAccentColor = { viewModel.setAccentColor(it) },
                onDismiss = { viewModel.showWallpaperThemeSettings(false) }
            )
        }

        // Overlay 6: Interactive Operand Naming Modal (65 = boxes)
        if (uiState.showOperandTagDialog) {
            OperandTagDialog(
                operandValue = uiState.editingOperandValue,
                currentTag = if (uiState.editingOperandIndex == 1) uiState.firstOperandTag else uiState.secondOperandTag,
                dynamicSuggestions = uiState.dynamicSuggestions,
                accentColor = uiState.accentColor,
                onSaveTag = { tag -> viewModel.assignOperandTag(uiState.editingOperandIndex, tag) },
                onClearTag = { viewModel.clearOperandTag(uiState.editingOperandIndex) },
                onDismiss = { viewModel.closeOperandTagDialog() }
            )
        }
    }
}
