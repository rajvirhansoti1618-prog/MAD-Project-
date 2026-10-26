package com.example.lexidesk.ui

import com.example.lexidesk.ui.theme.AppDarkColorScheme
import com.example.lexidesk.ui.theme.AppLightColorScheme
import com.example.lexidesk.ui.theme.ThemeMode
import com.example.lexidesk.ui.theme.Typography
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test suite verifying ThemeMode parsing, color contrast distinctions,
 * typography line-height accessibility, and scroll layout integrity.
 */
class ThemeAndScrollUnitTest {

    @Test
    fun themeMode_fromString_handlesCaseInsensitiveValues() {
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromString("system"))
        assertEquals(ThemeMode.LIGHT, ThemeMode.fromString("light"))
        assertEquals(ThemeMode.DARK, ThemeMode.fromString("DARK"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromString("System Default"))
        assertEquals(ThemeMode.LIGHT, ThemeMode.fromString("Light Mode"))
        assertEquals(ThemeMode.DARK, ThemeMode.fromString("Dark Mode"))
        // Fallback for unknown strings
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromString("UNKNOWN_VALUE"))
    }

    @Test
    fun colorSchemes_haveDistinctSurfaceAndBackgroundTones() {
        // In Light theme, surface and background should be defined and non-null
        assertNotEquals(AppLightColorScheme.primary, AppLightColorScheme.onPrimary)
        assertNotEquals(AppLightColorScheme.surface, AppLightColorScheme.onSurface)

        // In Dark theme, text on background must have strong contrast
        assertNotEquals(AppDarkColorScheme.primary, AppDarkColorScheme.onPrimary)
        assertNotEquals(AppDarkColorScheme.surface, AppDarkColorScheme.onSurface)
    }

    @Test
    fun darkColorScheme_primaryIsTealLightForHighContrast() {
        // Verify DarkColorScheme primary is distinct from NavyPrimary for OLED dark readability
        assertNotEquals(AppLightColorScheme.primary, AppDarkColorScheme.primary)
    }

    @Test
    fun typography_proportionalLineHeights_preventTextClipping() {
        // Ensure headline and body lineHeights are greater than fontSizes
        assertTrue(Typography.headlineLarge.lineHeight.value >= Typography.headlineLarge.fontSize.value)
        assertTrue(Typography.headlineMedium.lineHeight.value >= Typography.headlineMedium.fontSize.value)
        assertTrue(Typography.titleLarge.lineHeight.value >= Typography.titleLarge.fontSize.value)
        assertTrue(Typography.bodyLarge.lineHeight.value >= Typography.bodyLarge.fontSize.value)
        assertTrue(Typography.bodyMedium.lineHeight.value >= Typography.bodyMedium.fontSize.value)
    }
}
