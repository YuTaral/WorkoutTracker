package com.example.workouttracker.ui.reusable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.ColorDialogBackground
import com.example.workouttracker.ui.theme.labelMediumBold
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.SpinnerBorderSize
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import androidx.compose.ui.platform.LocalDensity

/**
 * Data class representing an item in the spinner
 * @param key the unique key of the item
 * @param text the text to display for the item
 * @param imagePainter image painter
 */
data class SpinnerItem(
    val key: String,
    val text: String,
    val imagePainter: Painter? = null
)

/**
 * Custom implementation of spinner
 * @param modifier the modifier to apply padding to the most outer element of the spinner
 * @param items the items in the spinner
 * @param selectedItem the selected item
 * @param onItemSelected the callback to execute on item selection
 * @param isInDialog true if the spinner is insidea dialog, false otherwsise
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Spinner(
    modifier: Modifier = Modifier,
    items: List<SpinnerItem>,
    selectedItem: SpinnerItem?,
    onItemSelected: (String) -> Unit,
    isInDialog: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    val iconRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "iconRotation"
    )

    var borderShape = RoundedCornerShape(
        topStart = SpinnerBorderSize,
        topEnd = SpinnerBorderSize,
        bottomStart = SpinnerBorderSize,
        bottomEnd = SpinnerBorderSize
    )
    var color = if (isInDialog) ColorDialogBackground else Color.Black
    var drpDownMenuParentWidth by remember { mutableIntStateOf(0) }

    if (expanded) {
        borderShape = RoundedCornerShape(
            topStart = SpinnerBorderSize,
            topEnd = SpinnerBorderSize,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                drpDownMenuParentWidth = coordinates.size.width
            }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ColorAccent, borderShape)
                .clickable { expanded = !expanded },
            color = color
        ) {
            Row(
                modifier = Modifier.padding(start = PaddingSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Label(
                    modifier = Modifier.weight(1f),
                    text = selectedItem?.text ?: "",
                    style = labelMediumBold,
                    textAlign = TextAlign.Start
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier
                        .size(50.dp)
                        .rotate(iconRotation),
                    tint = ColorAccent
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { drpDownMenuParentWidth.toDp() })
                .background(color)
                .border(
                    1.dp,
                    ColorAccent,
                    RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                ),
            containerColor = Color.Transparent
        ) {
            items.forEach { spinnerItem ->
                DropdownMenuItem(
                    text = { Label(text = spinnerItem.text) },
                    onClick = {
                        onItemSelected(spinnerItem.key)
                        expanded = false
                    },
                    trailingIcon = {
                        spinnerItem.imagePainter?.let {
                            Image(
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(35.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, ColorBorder, CircleShape),
                                painter = it,
                                contentDescription = "Team image"
                            )
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun SpinnerPreview() {
    var selectedItemKey by remember { mutableStateOf("1") }

    val items = listOf(
        SpinnerItem(
            key = "1",
            text = "Option 1",
            imagePainter = null
        ),
        SpinnerItem(
            key = "2",
            text = "Option 2",
            imagePainter = null
        ),SpinnerItem(
            key = "3",
            text = "Option 3",
            imagePainter = null
        )
    )

    WorkoutTrackerTheme {
        Spinner(
            items = items,
            selectedItem = items[0],
            onItemSelected = { selectedItemKey = it }
        )
    }
}
