package com.example.taskconvertaiapp.shared.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    secondary = SecondaryDark,
    surface = SurfaceDark,
    onPrimary = OnPrimary,
    onSecondary = OnPrimary,
    onSurface = OnSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBase,
    secondary = SecondaryBase,
    surface = SurfaceLight,
    background = SurfaceLight,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onSurface = OnSurfaceLight,
    onBackground = OnSurfaceLight

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun TaskConvertAIAppTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = provideTypography(),
        content = content
    )
}
