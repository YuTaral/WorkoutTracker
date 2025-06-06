package com.example.workouttracker.ui.reusable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.ColorWhite
import com.example.workouttracker.ui.theme.MediumImageButtonSize

@Composable
/** Custom button with image inside
 * @param modifier the modifier
 * @param onClick callback to execute on click
 * @param image the image
 * @param size the image size
 * @param buttonColor the button color
 * @param imageColor the image color
 */
fun ImageButton(modifier: Modifier = Modifier, onClick: () -> Unit, image: ImageVector, size: Dp = MediumImageButtonSize,
                buttonColor: Color = ColorAccent, imageColor: Color = ColorWhite) {
    Button(
        shape = MaterialTheme.shapes.small,
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        onClick = onClick
    ) {
        Image(
            modifier = Modifier.size(size),
            imageVector = image,
            colorFilter = ColorFilter.tint(color = imageColor),
            contentDescription = "",
        )
    }
}