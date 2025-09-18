package com.example.workouttracker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.data.models.TeamModel
import com.example.workouttracker.ui.theme.ColorBorder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import com.example.workouttracker.ui.reusable.DialogButton
import com.example.workouttracker.ui.reusable.ErrorLabel
import com.example.workouttracker.ui.reusable.FragmentButton
import com.example.workouttracker.ui.reusable.ImageButton
import com.example.workouttracker.ui.reusable.InputField
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.components.MemberItem
import com.example.workouttracker.ui.theme.BottomSheetsDialogFooterSize
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.LazyListBottomPadding
import com.example.workouttracker.ui.theme.PaddingLarge
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.ui.theme.labelMediumBold
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.viewmodel.AddEditTeamViewModel
import com.example.workouttracker.viewmodel.ManageTeamsViewModel.ViewTeamAs

/**
 * Screen to allow the user to add / edit team or view the team as member
 * @param team the team to edit, if the mode is add, team is null
 */
@Composable
fun SelectedTeamScreen(team: TeamModel?, vm: AddEditTeamViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        vm.initialize(team)
    }

    if (team == null || team.viewTeamAs == ViewTeamAs.COACH.name) {
        AddEditTeamScreen(
            team = team,
            vm = vm
        )
    } else {
        ViewTeamAsMemberScreen(
            team = team,
            vm = vm
        )
    }
}

/**
 * Add / edit team as coach screen
 * @param team the team to edit, if the mode is add, team is null
 * @param vm the screen's view model
 */
@Composable
fun AddEditTeamScreen(team: TeamModel?, vm: AddEditTeamViewModel) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val members by vm.teamRepository.teamMembers.collectAsStateWithLifecycle()
    val teamImagePainter = if (!uiState.image.isEmpty()) {
        val bitmap = Utils.convertStringToBitmap(uiState.image)
        BitmapPainter(bitmap.asImageBitmap())
    } else {
        painterResource(id = R.drawable.icon_team_default_picture)
    }
    val descriptionFocusReq = remember { FocusRequester() }
    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(PaddingSmall)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PaddingSmall)
        ) {

            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(2.dp, ColorBorder, CircleShape)
                        .clickable(
                            enabled = true,
                            onClick = { vm.onImageClick() }
                        ),
                    painter = teamImagePainter,
                    contentDescription = "Team image"
                )
                ImageButton(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(start = PaddingLarge * 10),
                    onClick = { vm.updateImage("") },
                    image = Icons.Default.Close,
                    buttonColor = Color.Transparent,
                    imageColor = ColorAccent
                )
            }

            InputField(
                label = stringResource(id = R.string.team_name_lbl),
                value = uiState.name,
                onValueChange = {
                    if (it.length < 50) {
                        vm.updateName(it)
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { descriptionFocusReq.requestFocus() }),
                isError = uiState.nameError != null
            )

            uiState.nameError?.let {
                ErrorLabel(
                    modifier = Modifier.padding(horizontal = PaddingSmall),
                    text = uiState.nameError!!
                )
            }

            InputField(
                modifier = Modifier.focusRequester(descriptionFocusReq),
                label = stringResource(id = R.string.team_description_lbl),
                value = uiState.description,
                onValueChange = {
                    if (it.length < 4000) {
                        vm.updateDescription(it)
                    }
                },
                isError = false,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                singleLine = false,
                minLines = 3,
                maxLines = 3
            )

            if (team != null) {
                Row(modifier = Modifier.height(BottomSheetsDialogFooterSize)) {
                    DialogButton(
                        text = stringResource(id = R.string.manage_members_btn),
                        onClick = { vm.showManageMembers() }
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    contentPadding = PaddingValues(bottom = LazyListBottomPadding)
                ) {
                    items(members) {
                        MemberItem(
                            member = it,
                            showButton = false,
                            onAction = {}
                        )
                    }
                }
            }
        }

        Row(modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            if (team == null) {
                FragmentButton(
                    text = stringResource(id = R.string.save_btn),
                    onClick = { vm.saveTeam() }
                )
            } else {
                FragmentButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = PaddingSmall),
                    text = stringResource(id = R.string.delete_btn),
                    onClick = { vm.askDeleteTeam() }
                )

                FragmentButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = PaddingSmall),
                    text = stringResource(id = R.string.save_btn),
                    onClick = { vm.saveTeam() }
                )
            }
        }
    }
}

/**
 * View team as member screen
 * @param team the team
 * @param vm the screen's view model
 */
@Composable
fun ViewTeamAsMemberScreen(team: TeamModel, vm: AddEditTeamViewModel) {
    val uiState by vm.memberUIState.collectAsStateWithLifecycle()
    val teamMembersListState = rememberLazyListState()
    val teamImagePainter = if (!uiState.teamImage.isEmpty()) {
        val bitmap = Utils.convertStringToBitmap(uiState.teamImage)
        BitmapPainter(bitmap.asImageBitmap())
    } else {
        painterResource(id = R.drawable.icon_team_default_picture)
    }
    val coachImagePainter = if (!uiState.coach.image.isEmpty()) {
        val bitmap = Utils.convertStringToBitmap(uiState.coach.image)
        BitmapPainter(bitmap.asImageBitmap())
    } else {
        rememberVectorPainter(Icons.Default.Person)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(PaddingSmall)
        ) {
            Row(modifier = Modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier
                    .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(PaddingSmall)
                ) {
                    Image(
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .border(1.dp, ColorBorder, CircleShape),
                        painter = teamImagePainter,
                        contentDescription = "Team image"
                    )
                    Label(
                        modifier = Modifier.fillMaxWidth(),
                        text = uiState.teamName,
                        style = labelMediumBold,
                        textAlign = TextAlign.Center
                    )
                }

                Column(modifier = Modifier
                    .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(PaddingSmall)
                ) {
                    Image(
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .border(1.dp, ColorBorder, CircleShape),
                        painter = coachImagePainter,
                        contentDescription = "Coach image"
                    )
                    Label(
                        modifier = Modifier.fillMaxWidth(),
                        text = uiState.coach.fullName,
                        style = labelMediumBold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Label(
                modifier = Modifier.padding(top = PaddingSmall),
                text = uiState.teamDescr,
                style = labelMediumGrey,
                textAlign = TextAlign.Start,
                maxLines = 5
            )

            HorizontalDivider(
                modifier = Modifier.padding(top = PaddingSmall),
                color = ColorBorder,
                thickness = 1.dp
            )

            Label(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingSmall),
                text = String.format(stringResource(id = R.string.other_members_lbl), uiState.members.size),
                style = labelMediumGrey,
                textAlign = TextAlign.Center
            )

            LazyColumn(
                modifier = Modifier.padding(bottom = PaddingLarge),
                state = teamMembersListState,
            ) {
                items(uiState.members) {
                    MemberItem(
                        member = it,
                        showButton = false,
                        onAction = { }
                    )
                }
            }
        }

        FragmentButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = PaddingSmall),
            text = stringResource(id = R.string.leave_team_btn),
            onClick = { vm.askLeaveTeam() }
        )
    }
}

@Preview
@Composable
private fun AddEditTeamScreenPreview() {
    WorkoutTrackerTheme {
        SelectedTeamScreen(null)
    }
}

