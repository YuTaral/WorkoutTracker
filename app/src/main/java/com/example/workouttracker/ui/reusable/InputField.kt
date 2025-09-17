package com.example.workouttracker.ui.reusable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.workouttracker.R
import com.example.workouttracker.ui.theme.ColorAccent
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
 * @param style the text style
 */
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
    maxLines: Int = 1,
    style: TextStyle = LocalTextStyle.current
) {
    // Detect if this is a password field by checking if the visualTransformation is PasswordVisualTransformation
    val isPasswordField = visualTransformation is PasswordVisualTransformation

    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.then(Modifier.fillMaxWidth()),
        label = { Text(text = label) },
        visualTransformation = when {
            isPasswordField && !passwordVisible -> PasswordVisualTransformation()
            else -> VisualTransformation.None
        },
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedLabelColor = ColorSecondary,
            focusedLabelColor = ColorBorder,
            unfocusedBorderColor = ColorSecondary,
            focusedBorderColor = ColorBorder,
            focusedTextColor = ColorWhite,
            unfocusedTextColor = ColorWhite,
            cursorColor = ColorBorder,
            selectionColors = TextSelectionColors(ColorBorder, ColorBorder),
            errorTextColor = ColorWhite
        ),
        singleLine = singleLine,
        keyboardActions = keyboardActions,
        isError = isError,
        minLines = minLines,
        maxLines = maxLines,
        textStyle = style,
        trailingIcon = {
            if (isPasswordField) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = if (passwordVisible) painterResource(id = R.drawable.icon_password_visible)
                                  else painterResource(id = R.drawable.icon_password_not_visible),
                        contentDescription = null,
                        tint = ColorAccent
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun InputFieldPreview() {
    InputField(
        label = "Password",
    )
}
