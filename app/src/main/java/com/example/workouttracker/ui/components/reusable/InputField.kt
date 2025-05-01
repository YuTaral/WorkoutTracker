package com.example.workouttracker.ui.components.reusable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.ColorSecondary
import com.example.workouttracker.ui.theme.ColorWhite

/**
 * The default input field displayed in the app
 * @param modifier the modifier of the label
 * @param label the label displayed at the center / top
 * @param value the initial value
 * @param onValueChange callback to execute on value change
 * @param isError whether the field value is invalid
 * @param keyboardOptions the input type - text / number, default is text
 * @param visualTransformation the text visual transformation (e.g for password), default is none
 * @param singleLine whether the field is single line
 * @param minLines the minimum lines count
 * @param maxLines the minimum lines count
 **/
@Composable
fun InputField(
        modifier: Modifier = Modifier,
        label: String,
        value: String = "",
        onValueChange: (String) -> Unit = {},
        isError: Boolean = false,
        keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        visualTransformation: VisualTransformation = VisualTransformation.None,
        keyboardActions: KeyboardActions = KeyboardActions.Default,
        singleLine: Boolean = true,
        minLines: Int = 1,
        maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.then(Modifier.fillMaxWidth()),
        label = { Text(text = label) },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedLabelColor = ColorSecondary,
            focusedLabelColor = ColorBorder,
            unfocusedBorderColor = ColorSecondary,
            focusedBorderColor = ColorBorder,
            focusedTextColor = ColorWhite,
            unfocusedTextColor = ColorWhite,
            cursorColor = ColorBorder,
            selectionColors = TextSelectionColors(ColorBorder,ColorBorder),
            errorTextColor = ColorWhite
        ),
        singleLine = singleLine,
        keyboardActions = keyboardActions,
        isError = isError,
        minLines = minLines,
        maxLines = maxLines,
    )
}


@Preview
@Composable
fun InputFieldPreview() {
    InputField(label = "Field Label")
}
