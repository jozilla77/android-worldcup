package com.jozilla.worldcup.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Vibrant HSL-equivalent Tailored Palette
val NeonPink = Color(0xFFFF1493)
val DarkNeonPink = Color(0xFFC71585)
val CyberCyan = Color(0xFF00F5FF)
val MatteBlack = Color(0xFF0F0F12)
val DeepCharcoal = Color(0xFF16161D)
val GlassCard = Color(0x33FFFFFF)
val GlassCardBorder = Color(0x1AFFFFFF)
val SoftGray = Color(0xFFA0A0AB)

val FieldGreen = Color(0xFF0F5229)
val PitchDarkGreen = Color(0xFF0B3A1C)

val CardYellow = Color(0xFFFFD700)
val CardRed = Color(0xFFFF3B30)

private val DarkColorScheme = darkColorScheme(
    primary = NeonPink,
    secondary = CyberCyan,
    tertiary = DarkNeonPink,
    background = MatteBlack,
    surface = DeepCharcoal,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = NeonPink,
    secondary = CyberCyan,
    tertiary = DarkNeonPink,
    background = Color(0xFFFFF0F5), // Lavender blush for cute light mode
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

val WorldCupTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = 0.5.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        letterSpacing = 0.25.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 1.sp
    )
)

@Composable
fun WorldCupTheme(
    darkTheme: Boolean = true, // Force dark theme by default for neon glow effect!
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WorldCupTypography,
        content = content
    )
}
