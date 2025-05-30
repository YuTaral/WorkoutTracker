package com.example.workouttracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.example.workouttracker.ui.theme.BackgroundStart
import com.example.workouttracker.ui.theme.BackgroundEnd

/** The app background composable */
@Composable
fun AppBackground(content: @Composable () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundStart, BackgroundEnd)
                )
            )
    ) {
        content()
    }
}
