package com.example.workouttracker.ui.components.reusable

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.ColorSecondary
import com.example.workouttracker.ui.theme.ColorWhite
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme

/**
 * Smaller input field with less content padding compared to the default input field
 * @param modifier the modifier of the label
 * @param value the initial value
 * @param onValueChange callback to execute on value change
 * @param keyboardOptions the input type - text / number, default is text
 * @param visualTransformation the text visual transformation (e.g for password), default is none
 * @param style the text style
 */
@Composable
fun SmallInputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    style: TextStyle = LocalTextStyle.current
) {
    val customTextSelectionColors = TextSelectionColors(
        handleColor = ColorBorder,
        backgroundColor = ColorBorder.copy(alpha = 0.4f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        Field(
            modifier = modifier,
            value = value,
            onValueChange = { onValueChange(it) },
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            keyboardActions = keyboardActions,
            style = style
        )
    }
}

@Composable
fun Field(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    style: TextStyle = LocalTextStyle.current
) {
    var isFocused by remember { mutableStateOf(false) }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        textStyle = style.merge(TextStyle(color = ColorWhite)),
        cursorBrush = SolidColor(ColorBorder),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = when {
                            isFocused -> ColorBorder
                            else -> ColorSecondary
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 4.dp)
                    .fillMaxWidth()
            ) {
                innerTextField()
            }
        }
    )
}

@Preview()
@Composable
fun SmallInputFieldPreview() {
    WorkoutTrackerTheme {
        WorkoutTrackerTheme {
            SmallInputField(
                value = "12",
                onValueChange = {  },
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
