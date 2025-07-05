package com.example.workouttracker.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.viewmodel.AssignWorkoutViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.R
import com.example.workouttracker.data.models.TeamMemberModel
import com.example.workouttracker.data.models.TeamModel
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.ui.components.MemberItem
import com.example.workouttracker.ui.components.TeamItem
import com.example.workouttracker.ui.components.WorkoutItem
import com.example.workouttracker.ui.reusable.ImageButton
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.LazyListBottomPadding
import com.example.workouttracker.ui.theme.PaddingLarge
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.viewmodel.AssignWorkoutViewModel.Mode
import kotlinx.coroutines.flow.StateFlow

/** The screen to allow coaches to assign workouts to team members */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AssignWorkoutScreen(vm: AssignWorkoutViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        vm.initializeData()
    }

    val mode by vm.mode.collectAsStateWithLifecycle()
    val selectedTeam by vm.teamRepository.selectedTeam.collectAsStateWithLifecycle()
    val teamMembers by vm.teamRepository.teamMembers.collectAsStateWithLifecycle()
    val templates by vm.templatesRepository.templates.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = mode,
        transitionSpec = {
            fadeIn().togetherWith(fadeOut())
        },
        label = "Screen Transition"
    ) { mode ->
        when (mode) {
            Mode.SELECT_TEAM -> {
                SelectTeamScreen(
                    teamsStateFlow = vm.teamRepository.teams,
                    onClick = { vm.selectTeam(it) }
                )
            }
            Mode.SELECT_MEMBERS -> {
                if (selectedTeam != null) {
                    SelectMembersScreen(
                        team = selectedTeam!!,
                        members = teamMembers,
                        onClick = { vm.selectMember(it) },
                        onBack = { vm.selectTeam(null) },
                        onForward = { vm.updateSelectedMode(Mode.SELECT_WORKOUT) }
                    )
                }
            }
            else -> {
                if (selectedTeam != null && teamMembers.isNotEmpty()) {
                    SelectWorkoutScreen(
                        team = selectedTeam!!,
                        members = teamMembers.filter { it.selectedForAssign },
                        templates = templates,
                        onClick = { vm.selectWorkoutTemplate(it) },
                        onBack = { vm.updateSelectedMode(Mode.SELECT_MEMBERS) },
                    )
                }
            }
        }
    }
}

/**
 * Screen to select team
 * @param teamsStateFlow state flow value with the teams
 * @param onClick callback to execute on team click
 */
@Composable
private fun SelectTeamScreen(teamsStateFlow: StateFlow<MutableList<TeamModel>>, onClick: (TeamModel) -> Unit) {
    var lazyListState = rememberLazyListState()
    val teams by teamsStateFlow.collectAsStateWithLifecycle()

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = PaddingSmall)
    ) {
        Label(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = PaddingSmall),
            text = stringResource(id = R.string.select_team_lbl),
            style = MaterialTheme.typography.titleSmall
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            contentPadding = PaddingValues(bottom = LazyListBottomPadding)
        ) {
            items(teams) {
                TeamItem(
                    team = it,
                    onClick = { onClick(it) }
                )
            }
        }
    }
}

/**
 * Screen to select members
 * @param team the selected team
 * @param members the team members
 * @param onClick callback to execute on member click
 * @param onBack callback to execute on arrow back click
 * @param onForward callback to execute on arrow forward click
 */
@Composable
private fun SelectMembersScreen(
    team: TeamModel,
    members: List<TeamMemberModel>,
    onClick: (TeamMemberModel) -> Unit,
    onBack: () -> Unit,
    onForward: () -> Unit,
) {
    val teamMembersListState = rememberLazyListState()

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = PaddingSmall)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = PaddingSmall / 2))
            {
                TeamItem(
                    team = team,
                    onClick = {}
                )

                Label(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = PaddingSmall),
                    text = stringResource(id = R.string.select_members_lbl),
                    style = MaterialTheme.typography.titleSmall
                )

                LazyColumn(
                    modifier = Modifier.padding(bottom = PaddingLarge, start = PaddingSmall, end = PaddingSmall),
                    state = teamMembersListState,
                ) {
                    items(members) {
                        MemberItem(
                            member = it,
                            showButton = false,
                            showSelection = true,
                            onSelect = { onClick(it) }
                        )
                    }
                }
            }

            ImageButton(
                modifier = Modifier.align(Alignment.BottomStart),
                onClick = { onBack() },
                image = Icons.AutoMirrored.Filled.ArrowBack
            )

            ImageButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = { onForward()  },
                image = Icons.AutoMirrored.Filled.ArrowForward
            )
        }
    }
}

/**
 * Screen to select workout
 * @param team the selected team
 * @param members the team members
 * @param templates the user templates
 * @param onClick callback to execute on workout click
 * @param onBack callback to execute on arrow back click
 */
@Composable
private fun SelectWorkoutScreen(
    team: TeamModel,
    members: List<TeamMemberModel>,
    templates: List<WorkoutModel>,
    onClick: (WorkoutModel) -> Unit,
    onBack: () -> Unit,
) {
    val lazyListState = rememberLazyListState()

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = PaddingSmall / 2)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = PaddingSmall)
            ) {
                TeamItem(
                    team = team,
                    onClick = {}
                )

                Label(
                    modifier = Modifier.fillMaxWidth(),
                    text = members.joinToString(", ") { it.fullName },
                    textAlign = TextAlign.Left,
                    maxLines = 5
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = PaddingSmall),
                    color = ColorBorder,
                    thickness = 1.dp
                )

                Label(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = PaddingSmall),
                    text = stringResource(id = R.string.select_workout_lbl),
                    style = MaterialTheme.typography.titleSmall
                )

                if (templates.isEmpty()) {
                    Label(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.no_templates),
                        textAlign = TextAlign.Center,
                        style = labelMediumGrey,
                        maxLines = 5
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = lazyListState,
                        contentPadding = PaddingValues(bottom = LazyListBottomPadding)
                    ) {
                        items(templates) { item ->
                            WorkoutItem(
                                workout = item,
                                weightUnit = "",
                                onClick = { onClick(it) }
                            )
                        }
                    }
                }
            }

            ImageButton(
                modifier = Modifier.align(Alignment.BottomStart),
                onClick = { onBack() },
                image = Icons.AutoMirrored.Filled.ArrowBack
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Preview
@Composable
private fun AssignWorkoutScreenPreview() {
    WorkoutTrackerTheme {
        AssignWorkoutScreen()
    }
}