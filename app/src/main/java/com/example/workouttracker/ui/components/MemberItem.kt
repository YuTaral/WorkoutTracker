package com.example.workouttracker.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttracker.data.models.TeamMemberModel
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.R
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.PaddingVerySmall
import com.example.workouttracker.utils.Utils
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.example.workouttracker.ui.reusable.CustomCheckbox
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.ColorWhite
import com.example.workouttracker.ui.theme.labelSmall
import com.example.workouttracker.ui.theme.labelSmallGreen
import com.example.workouttracker.ui.theme.labelSmallOrange

/** User -> Team state - whether the user has been invited, removed and etc. */
enum class MemberTeamState(private var stringId: Int, private var style: TextStyle) {
    NOT_INVITED(0, labelSmall),
    INVITED(R.string.invited_lbl, labelSmallOrange),
    ACCEPTED(R.string.in_team_lbl, labelSmallGreen);

    fun getStyle(): TextStyle {
        return style
    }

    fun getTextId(): Int {
        return stringId
    }
}

/**
 * Display single member of team
 * @param member the team member
 * @param showButton whether to show button for invite / remove
 * @param showSelection show checkbox for selection
 * @param onAction action to execute on button click
 * @param onSelect callback to execute when the row is selected
 */
@Composable
fun MemberItem(
        member: TeamMemberModel,
        showButton: Boolean,
        showSelection: Boolean = false,
        onAction: (TeamMemberModel) -> Unit = {},
        onSelect: (TeamMemberModel) -> Unit = {}
) {
    val state = MemberTeamState.valueOf(member.teamState)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = PaddingSmall)
            .clickable(
                enabled = true,
                onClick = {
                    onSelect(member)
                }
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imagePainter: Painter = if (!member.image.isEmpty()) {
                val bitmap = Utils.convertStringToBitmap(member.image)
                BitmapPainter(bitmap.asImageBitmap())
            } else {
                rememberVectorPainter(Icons.Default.Person)
            }

            Image(
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, ColorBorder, CircleShape),
                painter = imagePainter,
                contentDescription = "Image",
                colorFilter = ColorFilter.tint(color = ColorWhite),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(PaddingVerySmall)
            ) {
                Label(
                    text = member.fullName,
                    textAlign = TextAlign.Left
                )
                Label(
                    text = if (state != MemberTeamState.NOT_INVITED)
                        stringResource(id = state.getTextId())
                    else "",
                    style = state.getStyle(),
                    textAlign = TextAlign.Left,
                )
            }

            if (showButton) {
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(
                            enabled = true,
                            onClick = { onAction(member) }
                        ),
                    painter = if (state == MemberTeamState.NOT_INVITED) painterResource(id = R.drawable.icon_invite_member)
                              else painterResource(id = R.drawable.icon_remove_member),
                    contentDescription = null,
                )
            } else if (showSelection && state == MemberTeamState.ACCEPTED) {
                CustomCheckbox(
                    checked = member.selectedForAssign,
                    onValueChange = { onSelect(member) },
                    text = ""
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = PaddingVerySmall / 2),
            color = ColorBorder,
            thickness = 1.dp
        )
    }
}


@Preview
@Composable
private fun MemberItemPreview() {
    WorkoutTrackerTheme {
        MemberItem(TeamMemberModel(1L, 1L, "1", "Test user", "", MemberTeamState.INVITED.name), true, false, {})
    }
}