package com.example.workouttracker.ui.theme

import com.example.workouttracker.ui.components.reusable.AppBackground
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ColorAccent,
    secondary = ColorWhite,
    tertiary = ColorAccent,
    background = Color.Transparent,
    surface = BackgroundEnd,
    onPrimary = ColorWhite,
    onSecondary = ColorWhite,
    onTertiary = ColorWhite,
    onBackground = ColorWhite,
    onSurface = ColorWhite,
)

private val LightColorScheme = lightColorScheme(
    primary = ColorAccent,
    secondary = BackgroundEnd,
    tertiary = ColorAccent,
    background = ColorWhite,
    surface = ColorWhite,
)

@Composable
fun WorkoutTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AppBackground()
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
