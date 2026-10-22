package com.example.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

enum class WallpaperStyle(val displayName: String, val description: String) {
    OBSIDIAN_GOLD("Obsidian Gold Vein", "Deep black marble with gold and smoke filaments"),
    PRISMATIC_GLASS("Prismatic Liquid Glass", "Dark frosted glass refraction with chromatic glow"),
    COSMIC_VELVET("Cosmic Velvet", "Midnight velvet nebula with glowing dust"),
    TITANIUM_SLATE("Brushed Titanium", "Dark slate minimal metallic surface")
}

data class LuxuryPalette(
    val name: String,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val auraColor: Color
)

object LuxuryPalettes {
    val RoyalGold = LuxuryPalette(
        name = "Royal Gold",
        primaryAccent = Color(0xFFFFB800),
        secondaryAccent = Color(0xFFFFD56B),
        auraColor = Color(0x33FFB800)
    )

    val PlatinumIce = LuxuryPalette(
        name = "Platinum Diamond",
        primaryAccent = Color(0xFFE2E8F0),
        secondaryAccent = Color(0xFFFFFFFF),
        auraColor = Color(0x22FFFFFF)
    )

    val EmeraldJade = LuxuryPalette(
        name = "Emerald Jade",
        primaryAccent = Color(0xFF10B981),
        secondaryAccent = Color(0xFF34D399),
        auraColor = Color(0x3310B981)
    )

    val SapphireIce = LuxuryPalette(
        name = "Sapphire Azure",
        primaryAccent = Color(0xFF38BDF8),
        secondaryAccent = Color(0xFF67E8F9),
        auraColor = Color(0x3338BDF8)
    )

    val RoseGold = LuxuryPalette(
        name = "Rose Gold",
        primaryAccent = Color(0xFFFB7185),
        secondaryAccent = Color(0xFFFDA4AF),
        auraColor = Color(0x33FB7185)
    )

    val RubyCrimson = LuxuryPalette(
        name = "Ruby Crimson",
        primaryAccent = Color(0xFFF43F5E),
        secondaryAccent = Color(0xFFFB7185),
        auraColor = Color(0x33F43F5E)
    )

    val AmethystLuxe = LuxuryPalette(
        name = "Amethyst Luxe",
        primaryAccent = Color(0xFFA855F7),
        secondaryAccent = Color(0xFFC084FC),
        auraColor = Color(0x33A855F7)
    )

    val all = listOf(RoyalGold, PlatinumIce, EmeraldJade, SapphireIce, RoseGold, RubyCrimson, AmethystLuxe)
}

/**
 * Procedural Luxury Wallpaper Background rendering deep obsidian marble,
 * prismatic glass refractions, or cosmic velvet auras based on user selection.
 */
@Composable
fun LuxuryWallpaperBackground(
    wallpaperStyle: WallpaperStyle,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C0D0F))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            when (wallpaperStyle) {
                WallpaperStyle.OBSIDIAN_GOLD -> {
                    // 1. Deep obsidian base gradient
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0A0B0D),
                                Color(0xFF121418),
                                Color(0xFF090A0C)
                            )
                        )
                    )

                    // 2. Soft smoke nebula clouds
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x18303642), Color.Transparent),
                            center = Offset(width * 0.25f, height * 0.35f),
                            radius = width * 0.6f
                        ),
                        center = Offset(width * 0.25f, height * 0.35f),
                        radius = width * 0.6f
                    )

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x1420242C), Color.Transparent),
                            center = Offset(width * 0.8f, height * 0.7f),
                            radius = width * 0.7f
                        ),
                        center = Offset(width * 0.8f, height * 0.7f),
                        radius = width * 0.7f
                    )

                    // 3. Luxurious gold & smoke marble veins
                    val vein1 = Path().apply {
                        moveTo(0f, height * 0.18f)
                        cubicTo(
                            width * 0.3f, height * 0.15f,
                            width * 0.45f, height * 0.32f,
                            width * 0.75f, height * 0.28f
                        )
                        cubicTo(
                            width * 0.88f, height * 0.26f,
                            width * 0.95f, height * 0.42f,
                            width, height * 0.48f
                        )
                    }
                    drawPath(
                        path = vein1,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0x00FFB800),
                                Color(0x40FFD700),
                                Color(0x60FFAA00),
                                Color(0x15FFFFFF),
                                Color(0x00FFB800)
                            ),
                            start = Offset(0f, height * 0.18f),
                            end = Offset(width, height * 0.48f)
                        ),
                        style = Stroke(width = 3.5f)
                    )

                    // Secondary delicate hairline fracture
                    val vein2 = Path().apply {
                        moveTo(width * 0.15f, height * 0.65f)
                        cubicTo(
                            width * 0.4f, height * 0.72f,
                            width * 0.55f, height * 0.60f,
                            width * 0.85f, height * 0.85f
                        )
                    }
                    drawPath(
                        path = vein2,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x35E6C687),
                                Color(0x10FFFFFF),
                                Color.Transparent
                            )
                        ),
                        style = Stroke(width = 2.0f)
                    )
                }

                WallpaperStyle.PRISMATIC_GLASS -> {
                    // Prismatic glass refractions
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0F1117),
                                Color(0xFF151824),
                                Color(0xFF0C0E14)
                            ),
                            start = Offset.Zero,
                            end = Offset(width, height)
                        )
                    )
                    // Glass refractive diagonal bands
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(accentColor.copy(alpha = 0.12f), Color.Transparent),
                            center = Offset(width * 0.5f, height * 0.25f),
                            radius = width * 0.7f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x1800E5FF), Color.Transparent),
                            center = Offset(width * 0.85f, height * 0.55f),
                            radius = width * 0.5f
                        )
                    )
                }

                WallpaperStyle.COSMIC_VELVET -> {
                    // Deep cosmic indigo
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF171329),
                                Color(0xFF0E0B19),
                                Color(0xFF07050D)
                            ),
                            center = Offset(width * 0.5f, height * 0.4f),
                            radius = height * 0.8f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(accentColor.copy(alpha = 0.15f), Color.Transparent),
                            center = Offset(width * 0.3f, height * 0.2f),
                            radius = width * 0.6f
                        )
                    )
                }

                WallpaperStyle.TITANIUM_SLATE -> {
                    // Brushed metallic dark slate
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF16181C),
                                Color(0xFF1B1E24),
                                Color(0xFF121417)
                            )
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x12FFFFFF), Color.Transparent),
                            center = Offset(width * 0.5f, height * 0.3f),
                            radius = width * 0.5f
                        )
                    )
                }
            }
        }
    }
}
