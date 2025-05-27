package com.example.workouttracker.ui.components.reusable

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.R
import com.example.workouttracker.data.models.TeamModel
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.LabelMediumGrey
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.ui.theme.labelLargeBold
import com.example.workouttracker.utils.Utils

/**
 * Single team item
 * @param team the team
 */
@Composable
fun TeamItem(team: TeamModel) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = PaddingSmall)
    ) {
        Row {
            DisplayImage(team.image)

            Column(modifier = Modifier
                .padding(PaddingVerySmall)
            ) {
                Label(
                    text = team.name,
                    style = labelLargeBold,
                    maxLines = 2,
                    textAlign = TextAlign.Left
                )
                Label(
                    modifier = Modifier.padding(top = PaddingVerySmall),
                    text = team.description,
                    style = LabelMediumGrey,
                    maxLines = 3,
                    textAlign = TextAlign.Left
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = PaddingVerySmall),
            color = ColorBorder,
            thickness = 1.dp
        )
    }
}

/** Display the team image
 * @param image the image as string
 */
@Composable
private fun DisplayImage(image: String) {
    val profileImagePainter = if (image.isEmpty()) {
        val bitmap = Utils.convertStringToBitmap(image)
        BitmapPainter(bitmap.asImageBitmap())
    } else {
        painterResource(id = R.drawable.icon_team_default_picture)
    }

    Image(
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .border(1.dp, ColorBorder, CircleShape),
        painter = profileImagePainter,
        contentDescription = "Team image"
    )
}

@Preview
@Composable
private fun TeamItemPreview() {
    WorkoutTrackerTheme {
        TeamItem(TeamModel(1L, "", "My first team", "This is the best team ever"))
    }
}