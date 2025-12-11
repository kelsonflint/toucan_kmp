package com.kelson.toucan.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kelson.toucan.domain.models.PromptType

// Toucan Bird Color Palette
// Inspired by the majestic Toco Toucan

// Beak colors - the iconic gradient
val ToucanOrange = Color(0xFFFF6D00)       // Vibrant orange beak
val ToucanYellow = Color(0xFFFFAB00)       // Yellow-orange beak
val ToucanRed = Color(0xFFE53935)          // Red beak tip

// Body colors
val ToucanBlack = Color(0xFF1A1A1A)        // Black plumage
val ToucanCharcoal = Color(0xFF2D2D2D)     // Softer black

// Chest/throat colors
val ToucanWhite = Color(0xFFFFFFFF)        // White chest
val ToucanCream = Color(0xFFFFF8E1)        // Warm cream

// Eye ring / tropical accents
val ToucanTeal = Color(0xFF00BFA5)         // Tropical teal (eye ring)
val ToucanBlue = Color(0xFF29B6F6)         // Sky blue accent

// Derived colors
val ToucanOrangeDark = Color(0xFFE65100)
val ToucanYellowLight = Color(0xFFFFE082)

// Prompt type colors - toucan-inspired and vibrant
object PromptColors {
    // Normal - Toucan teal (eye ring color)
    val NormalBackground = Color(0xFF00BFA5)
    val NormalText = Color(0xFFFFFFFF)

    // Minigame - Toucan yellow-orange (beak gradient)
    val MinigameBackground = Color(0xFFFFAB00)
    val MinigameText = Color(0xFF1A1A1A)

    // Virus - Toucan red (beak tip)
    val VirusBackground = Color(0xFFE53935)
    val VirusText = Color(0xFFFFFFFF)

    // Virus Cure - Tropical blue (sky)
    val VirusCureBackground = Color(0xFF29B6F6)
    val VirusCureText = Color(0xFF1A1A1A)

    // Bottoms Up - Deep purple (party vibes)
    val BottomsUpBackground = Color(0xFF7B1FA2)
    val BottomsUpText = Color(0xFFFFFFFF)

    // Punishment - Dark orange (warning)
    val PunishmentBackground = Color(0xFFE65100)
    val PunishmentText = Color(0xFFFFFFFF)

    // Ardente - Hot pink (spicy)
    val ArdenteBackground = Color(0xFFD81B60)
    val ArdenteText = Color(0xFFFFFFFF)
}

data class PromptTypeColors(
    val background: Color,
    val text: Color,
    val label: String
)

@Composable
fun getPromptTypeColors(type: PromptType, isVirusCure: Boolean = false): PromptTypeColors {
    return when {
        isVirusCure -> PromptTypeColors(
            background = PromptColors.VirusCureBackground,
            text = PromptColors.VirusCureText,
            label = "CURED!"
        )
        type == PromptType.Normal -> PromptTypeColors(
            background = PromptColors.NormalBackground,
            text = PromptColors.NormalText,
            label = "NORMAL"
        )
        type == PromptType.Minigame -> PromptTypeColors(
            background = PromptColors.MinigameBackground,
            text = PromptColors.MinigameText,
            label = "MINIGAME"
        )
        type == PromptType.Virus -> PromptTypeColors(
            background = PromptColors.VirusBackground,
            text = PromptColors.VirusText,
            label = "VIRUS"
        )
        type == PromptType.BottomsUp -> PromptTypeColors(
            background = PromptColors.BottomsUpBackground,
            text = PromptColors.BottomsUpText,
            label = "BOTTOMS UP"
        )
        type == PromptType.Punishment -> PromptTypeColors(
            background = PromptColors.PunishmentBackground,
            text = PromptColors.PunishmentText,
            label = "PUNISHMENT"
        )
        type == PromptType.Ardente -> PromptTypeColors(
            background = PromptColors.ArdenteBackground,
            text = PromptColors.ArdenteText,
            label = "ARDENTE"
        )
        else -> PromptTypeColors(
            background = PromptColors.NormalBackground,
            text = PromptColors.NormalText,
            label = "NORMAL"
        )
    }
}

private val ToucanLightColorScheme = lightColorScheme(
    // Primary - the iconic orange beak
    primary = ToucanOrange,
    onPrimary = ToucanBlack,
    primaryContainer = ToucanYellow,
    onPrimaryContainer = ToucanBlack,

    // Secondary - tropical teal (eye ring)
    secondary = ToucanTeal,
    onSecondary = ToucanWhite,
    secondaryContainer = ToucanTeal.copy(alpha = 0.2f),
    onSecondaryContainer = ToucanTeal,

    // Tertiary - red accent (beak tip)
    tertiary = ToucanRed,
    onTertiary = ToucanWhite,

    // Background - warm cream (like the chest)
    background = ToucanCream,
    onBackground = ToucanBlack,

    // Surface - white chest
    surface = ToucanWhite,
    onSurface = ToucanBlack,
    surfaceVariant = ToucanYellowLight,
    onSurfaceVariant = ToucanCharcoal,

    // Outline
    outline = ToucanOrangeDark,
    outlineVariant = ToucanYellowLight,

    // Error
    error = ToucanRed,
    onError = ToucanWhite
)

private val ToucanDarkColorScheme = darkColorScheme(
    // Primary - orange beak pops on dark
    primary = ToucanOrange,
    onPrimary = ToucanBlack,
    primaryContainer = ToucanOrangeDark,
    onPrimaryContainer = ToucanYellow,

    // Secondary - teal accent
    secondary = ToucanTeal,
    onSecondary = ToucanBlack,
    secondaryContainer = ToucanTeal.copy(alpha = 0.3f),
    onSecondaryContainer = ToucanTeal,

    // Tertiary - red
    tertiary = ToucanRed,
    onTertiary = ToucanWhite,

    // Background - black plumage
    background = ToucanBlack,
    onBackground = ToucanWhite,

    // Surface - charcoal feathers
    surface = ToucanCharcoal,
    onSurface = ToucanWhite,
    surfaceVariant = Color(0xFF3D3D3D),
    onSurfaceVariant = ToucanYellowLight,

    // Outline
    outline = ToucanOrange,
    outlineVariant = ToucanCharcoal,

    // Error
    error = ToucanRed,
    onError = ToucanWhite
)

val ToucanShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun ToucanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ToucanDarkColorScheme else ToucanLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = ToucanShapes,
        content = content
    )
}
