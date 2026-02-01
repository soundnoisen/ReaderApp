package com.example.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme = darkColorScheme(
    primary = DarkOrangeAccent,
    background = DarkBackground,
    primaryContainer = DarkPrimaryContainer,
    secondaryContainer = DarkSecondaryContainer,
    onBackground = DarkHintText,
    onPrimary = DarkBackground,
    onSecondary = GrayText,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkRedError,
    outline = DarkOutline,
    tertiaryContainer = DarkHintProgress,
    onSecondaryContainer =  DarkHintOnSecondaryContainer,
    surfaceContainer = DarkContainer,
    onTertiary = DarkOnTertiary
)

private val LightColorScheme = lightColorScheme(
    primary = OrangeAccent,
    background = WhiteBackground,
    primaryContainer = LightPrimaryContainer,
    secondaryContainer = LightSecondaryContainer,
    onBackground = BlackText,
    onPrimary = WhiteText,
    onSecondary = LightHintText,
    errorContainer = LightErrorContainer,
    onErrorContainer = RedError,
    outline = LightOutline,
    tertiaryContainer = LightHintProgress,
    onSecondaryContainer =  LightHintOnSecondaryContainer,
    surfaceContainer = LightContainer,
    onTertiary = LightOnTertiary
)

@Composable
fun ReaderAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}