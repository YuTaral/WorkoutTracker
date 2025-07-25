package com.example.workouttracker.ui.reusable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.labelMediumBold
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.SpinnerBorderSize
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme

/**
 * Custom implementation of spinner
 * @param modifier the modifier to apply padding to the most outer element of the spinner
 * @param items the items in the spinner
 * @param selectedItemKey the selected item key
 * @param onItemSelected the callback to execute on item selection
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Spinner(
    modifier: Modifier = Modifier,
    items: Map<String, String>,
    selectedItemKey: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val iconRotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "iconRotation")
    var borderShape = RoundedCornerShape(
        topStart = SpinnerBorderSize,
        topEnd = SpinnerBorderSize,
        bottomStart = SpinnerBorderSize,
        bottomEnd = SpinnerBorderSize
    )

    if (expanded) {
        borderShape = RoundedCornerShape(
            topStart = SpinnerBorderSize,
            topEnd = SpinnerBorderSize,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
    }

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ColorAccent, borderShape)
                .clickable { expanded = !expanded },
            color = Color.Black
        ) {
            Row(
                modifier = Modifier.padding(start = PaddingSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Label(
                    modifier = Modifier.weight(1f),
                    text = items[selectedItemKey] ?: "",
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
                .fillMaxWidth()
                .padding(horizontal = PaddingSmall)
                .background(Color.Black)
                .border(
                    1.dp,
                    ColorAccent,
                    RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                ),
            containerColor = Color.Transparent
        ) {
            items.forEach { (key, value) ->
                DropdownMenuItem(
                    text = { Label(text = value) },
                    onClick = {
                        onItemSelected(key)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Preview
@Composable
fun SpinnerPreview() {
    var selectedItemKey by remember { mutableStateOf("1") }
    val items = mapOf(
        "1" to "Option 1",
        "2" to "Option 2",
        "3" to "Option 3"
    )

    WorkoutTrackerTheme {
        Spinner(
            items = items,
            selectedItemKey = selectedItemKey,
            onItemSelected = { selectedItemKey = it }
        )
    }
}
