package com.example.workouttracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.data.models.NotificationModel
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.ColorWhite
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.utils.Utils
import java.util.Date

/** Single notification item
 * @param notification the notification data
 * @param onClick callback to execute on notification click
 */
@Composable
fun NotificationItem(
    notification: NotificationModel,
    onClick: (NotificationModel) -> Unit,
    onRemoveClick: (Long) -> Unit
) {
    var imagePainter: Painter = rememberVectorPainter(Icons.Default.Person)
    var imageColorFilter: ColorFilter? = ColorFilter.tint(color = ColorWhite)
    var textStyle =  MaterialTheme.typography.labelMedium
    var backgroundColor = ColorAccent

    if (!notification.image.isEmpty()) {
        val bitmap = Utils.convertStringToBitmap(notification.image)
        imagePainter = BitmapPainter(bitmap.asImageBitmap())
        imageColorFilter = null
    }

    if (!notification.isActive) {
        textStyle = labelMediumGrey
        backgroundColor = Color.Transparent
    }

    Surface(
        modifier = Modifier
            .padding(vertical = PaddingVerySmall)
            .clickable(
                enabled = true,
                onClick = {
                    if (!notification.clickDisabled) {
                        onClick(notification)
                    }
                }
            ),
        color = backgroundColor,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            width = 1.dp,
            color = ColorAccent,
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingVerySmall),
                horizontalArrangement = Arrangement.spacedBy(PaddingVerySmall),
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, ColorBorder, CircleShape),
                    painter = imagePainter,
                    contentDescription = "Team image",
                    colorFilter = if (notification.image.isEmpty()) ColorFilter.tint(color = ColorWhite)
                                  else null,
                )

                Label(
                    modifier = Modifier.weight(1f),
                    text = notification.notificationText,
                    maxLines = 3,
                    textAlign = TextAlign.Start,
                    style = textStyle
                )

                Image(
                    imageVector = (Icons.Default.Close),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(
                            enabled = true,
                            onClick = { onRemoveClick(notification.id) }
                        ),
                    contentDescription = "Team image",
                    colorFilter = ColorFilter.tint(color = ColorWhite),
                )
            }

            Label(
                modifier = Modifier.padding(start = PaddingSmall),
                text = Utils.defaultFormatDateTime(notification.dateTime),
                style = textStyle
            )
        }
    }
}

@Preview
@Composable
private fun NotificationItemPreview() {
    WorkoutTrackerTheme {
        NotificationItem(NotificationModel(1L, "You have received new notification and you must take action", Date(), true,  "", "", 1L, false), {}, {})
    }
}