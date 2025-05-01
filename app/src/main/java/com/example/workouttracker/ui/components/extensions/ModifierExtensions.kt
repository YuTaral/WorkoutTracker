package com.example.workouttracker.ui.components.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.workouttracker.ui.theme.ColorBorder

/**
* Extension to the Modifier class to add custom border which is displayed only on the specified sides -
* top + start/end
* @param start true to add border at the start, false otherwise
* @param end true to add border at the end, false otherwise
*/
fun Modifier.customBorder (
    start: Boolean = false,
    end: Boolean = false,
    color: Color = ColorBorder,
    strokeWidth: Dp = 1.dp
): Modifier = then(
    Modifier.drawBehind {
        val stroke = strokeWidth.toPx()

        drawLine(
            color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = stroke
        )

        if (start) {
            drawLine(
                color,
                start = Offset(0f, 0f),
                end = Offset(0f, size.height),
                strokeWidth = stroke
            )
        }

        if (end) {
            drawLine(
                color,
                start = Offset(size.width, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = stroke
            )
        }
    }
)