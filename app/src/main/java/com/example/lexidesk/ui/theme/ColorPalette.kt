package com.example.lexidesk.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

enum class ThemeMode(val title: String) {
    SYSTEM("System Default"),
    LIGHT("Light Mode"),
    DARK("Dark Mode");

    companion object {
        fun fromString(value: String): ThemeMode {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.title.equals(value, ignoreCase = true) } ?: SYSTEM
        }
    }
}

val AppLightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = SlateSurface,
    primaryContainer = Color(0xFFE2E8F0),
    onPrimaryContainer = NavyDark,
    secondary = TealSecondary,
    onSecondary = SlateSurface,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = TealDark,
    tertiary = AmberAccent,
    onTertiary = SlateSurface,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF92400E),
    error = RedUrgent,
    onError = SlateSurface,
    background = SlateBackground,
    onBackground = SlateTextPrimary,
    surface = SlateSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = SlateTextSecondary,
    outline = SlateBorder,
    outlineVariant = Color(0xFFE2E8F0),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8FAFC),
    surfaceContainer = Color(0xFFF1F5F9),
    surfaceContainerHigh = Color(0xFFE2E8F0),
    surfaceContainerHighest = Color(0xFFCBD5E1)
)

val AppDarkColorScheme = darkColorScheme(
    primary = TealLight,
    onPrimary = NavyDark,
    primaryContainer = Color(0xFF1E293B),
    onPrimaryContainer = TealLight,
    secondary = TealSecondary,
    onSecondary = SlateSurface,
    secondaryContainer = Color(0xFF134E4A),
    onSecondaryContainer = Color(0xFF99F6E4),
    tertiary = Color(0xFFFBBF24),
    onTertiary = NavyDark,
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = Color(0xFFFDE68A),
    error = Color(0xFFF87171),
    onError = NavyDark,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = DarkBorder,
    outlineVariant = Color(0xFF1E293B),
    surfaceContainerLowest = Color(0xFF070D18),
    surfaceContainerLow = Color(0xFF0F172A),
    surfaceContainer = Color(0xFF131D31),
    surfaceContainerHigh = Color(0xFF1E293B),
    surfaceContainerHighest = Color(0xFF334155)
)
