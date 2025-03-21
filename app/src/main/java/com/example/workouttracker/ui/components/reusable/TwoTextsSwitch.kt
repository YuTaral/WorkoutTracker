package com.example.workouttracker.ui.components.reusable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.workouttracker.ui.theme.ColorAccent

/**
 * Switch to allow to choose a value between 2 texts
 * @param modifier the default modifier
 * @param leftText the text displayed in the left
 * @param rightText the text displayed in the right
 * @param onSelectionChanged callback to execute when selection changes
 * */
@Composable
fun TwoTextsSwitch(modifier: Modifier = Modifier,
                   leftText: String = "Left",
                   rightText: String = "Right",
                   onSelectionChanged: (Boolean) -> Unit = {}
) {
    var isLeftSelected by remember { mutableStateOf(true) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .border(border = BorderStroke(2.dp, ColorAccent), shape = RoundedCornerShape(25.dp))
    ) {
        Text(
            text = leftText,
            modifier = Modifier
                .weight(1f)
                .background(if (isLeftSelected) ColorAccent else Color.Transparent, shape = RoundedCornerShape(topStart = 25.dp, bottomStart = 25.dp, topEnd = 0.dp, bottomEnd = 0.dp))
                .clickable {
                    if (!isLeftSelected) {
                        isLeftSelected = true
                        onSelectionChanged(true)
                    }
                }
                .padding(vertical = 5.dp),
            color = Color.White,
            fontWeight = if (isLeftSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
        )

        Text(
            text = rightText,
            modifier = Modifier
                .weight(1f)
                .background(if (!isLeftSelected) ColorAccent else Color.Transparent, RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 25.dp, bottomEnd = 25.dp))
                .clickable {
                    if (isLeftSelected) {
                        isLeftSelected = false
                        onSelectionChanged(false)
                    }
                }
                .padding(vertical = 5.dp),
            color = Color.White,
            fontWeight = if (!isLeftSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomSwitch() {
    TwoTextsSwitch { isLeftSelected ->
        println("Selected: ${if (isLeftSelected) "Left" else "Right"}")
    }
}

