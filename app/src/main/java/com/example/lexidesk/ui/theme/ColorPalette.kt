package com.example.lexidesk.ui.theme

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
    primaryContainer = SlateSurfaceVariant,
    onPrimaryContainer = NavyPrimary,
    secondary = TealSecondary,
    onSecondary = SlateSurface,
    secondaryContainer = SlateSurfaceVariant,
    onSecondaryContainer = TealDark,
    tertiary = AmberAccent,
    background = SlateBackground,
    onBackground = SlateTextPrimary,
    surface = SlateSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = SlateTextSecondary,
    outline = SlateBorder
)

val AppDarkColorScheme = darkColorScheme(
    primary = TealLight,
    onPrimary = NavyDark,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = DarkTextPrimary,
    secondary = TealSecondary,
    onSecondary = SlateSurface,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = TealLight,
    tertiary = AmberAccent,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder
)
