package com.olddragon.charactercreator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BlackBullsColorScheme = darkColorScheme(
    primary = BlackBullsRed,
    onPrimary = BlackBullsWhite,
    primaryContainer = BlackBullsGray,
    onPrimaryContainer = BlackBullsGold,
    
    secondary = BlackBullsGold,
    onSecondary = BlackBullsBlack,
    secondaryContainer = BlackBullsDarkGray,
    onSecondaryContainer = BlackBullsGold,
    
    tertiary = AccentPurple,
    onTertiary = BlackBullsWhite,
    
    background = BlackBullsBlack,
    onBackground = BlackBullsWhite,
    
    surface = CardBackground,
    onSurface = BlackBullsWhite,
    surfaceVariant = BlackBullsGray,
    onSurfaceVariant = BlackBullsSilver,
    
    error = ErrorRed,
    onError = Color.White,
    
    outline = BlackBullsGray,
    outlineVariant = BlackBullsDarkGray
)

@Composable
fun BlackBullsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BlackBullsColorScheme,
        typography = Typography,
        content = content
    )
}
