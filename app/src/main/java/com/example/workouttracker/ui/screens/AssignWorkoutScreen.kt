package com.example.workouttracker.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.viewmodel.AssignWorkoutViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.R
import com.example.workouttracker.data.models.BaseModel
import com.example.workouttracker.data.models.TeamMemberModel
import com.example.workouttracker.data.models.TrainingPlanModel
import com.example.workouttracker.data.models.WorkoutModel
import com.example.workouttracker.ui.components.MemberItem
import com.example.workouttracker.ui.components.TeamItem
import com.example.workouttracker.ui.components.TrainingPlanItem
import com.example.workouttracker.ui.components.WorkoutItem
import com.example.workouttracker.ui.reusable.ImageButton
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.reusable.TwoTextsSwitch
import com.example.workouttracker.ui.theme.LazyListBottomPadding
import com.example.workouttracker.ui.theme.PaddingLarge
import com.example.workouttracker.ui.theme.PaddingMedium
import com.example.workouttracker.ui.theme.labelMediumBold
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.viewmodel.AssignWorkoutViewModel.Mode
import com.example.workouttracker.viewmodel.AssignWorkoutViewModel.WorkoutSelection

/** The screen to allow coaches to assign workouts to team members */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AssignWorkoutScreen(vm: AssignWorkoutViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        vm.initializeData()
    }

    val mode by vm.mode.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = mode,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
        label = "Screen Transition"
    ) { currentMode ->
        when (currentMode) {
            Mode.SELECT_TEAM -> SelectTeamScreen(vm)
            Mode.SELECT_MEMBERS -> SelectMembersScreen(vm)
            Mode.SELECT_WORKOUT -> SelectWorkoutScreen(vm)
        }
    }
}

/**
 * Screen to select team
 */
@Composable
private fun SelectTeamScreen(vm: AssignWorkoutViewModel) {
    val teams by vm.teamRepository.teams.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    Column(
        modifier = Modifier
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

        if (teams.isEmpty()) {
            Label(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.no_teams),
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
                items(teams, key = { it.id }) {
                    TeamItem(
                        team = it,
                        onClick = { vm.selectTeam(it) }
                    )
                }
            }
        }
    }
}

/**
 * Screen to select members
 */
@Composable
private fun SelectMembersScreen(vm: AssignWorkoutViewModel) {
    val selectedTeam by vm.teamRepository.selectedTeam.collectAsStateWithLifecycle()
    val members by vm.teamRepository.teamMembers.collectAsStateWithLifecycle()

    if (selectedTeam == null) return

    val teamMembersListState = rememberLazyListState()
    val onClick = remember { { member: TeamMemberModel -> vm.selectMember(member) } }
    val onBack = remember { { vm.selectTeam(null) } }
    val onForward = remember { { vm.updateSelectedMode(Mode.SELECT_WORKOUT) } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingSmall)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = PaddingSmall / 2)
            ) {
                TeamItem(team = selectedTeam!!, onClick = {})

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
                    items(members, key = { it.id }) {
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
                onClick = { onForward() },
                image = Icons.AutoMirrored.Filled.ArrowForward
            )
        }
    }
}

/**
 * Screen to select workout
 */
@Composable
private fun SelectWorkoutScreen(vm: AssignWorkoutViewModel) {
    val selectedTeam by vm.teamRepository.selectedTeam.collectAsStateWithLifecycle()
    val members by vm.teamRepository.teamMembers.collectAsStateWithLifecycle()
    val templates by vm.templatesRepository.templates.collectAsStateWithLifecycle()
    val trainingPlans by vm.trainingPlanRepository.trainingPlans.collectAsStateWithLifecycle()
    val selectedWorkoutSelection by vm.selectedWorkoutSelection.collectAsStateWithLifecycle()
    val startDate by vm.startDate.collectAsStateWithLifecycle()

    if (selectedTeam == null || members.isEmpty()) return

    val data by remember(selectedWorkoutSelection, templates, trainingPlans) {
        derivedStateOf {
            when (selectedWorkoutSelection) {
                WorkoutSelection.SINGLE_WORKOUT -> templates
                WorkoutSelection.TRAINING_PLAN -> trainingPlans
            }
        }
    }

    val lazyListState = rememberLazyListState()

    val onClick = remember { { item: BaseModel -> vm.selectWorkouts(item) } }
    val onBack = remember { { vm.updateSelectedMode(Mode.SELECT_MEMBERS) } }
    val onWorkoutSelectionTypeChange = remember { { type: String -> vm.updateSelectedWorkoutSelection(type) } }
    val showDatePicker = remember { { vm.showDatePicker() } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingSmall / 2)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = PaddingSmall)
            ) {
                Label(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = selectedTeam!!.name + " - " + members.filter { it.selectedForAssign }.joinToString(", ") { it.fullName },
                    textAlign = TextAlign.Left,
                    style = labelMediumBold,
                    maxLines = 3
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = true, onClick = showDatePicker),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Label(
                        text = String.format(
                            stringResource(id = R.string.start_workouts_on),
                            Utils.defaultFormatDate(startDate)
                        ),
                        style = MaterialTheme.typography.labelSmall
                    )

                    ImageButton(
                        modifier = Modifier.size(30.dp),
                        onClick = showDatePicker,
                        image = Icons.Default.DateRange
                    )
                }

                TwoTextsSwitch(
                    modifier = Modifier.padding(horizontal = PaddingMedium, vertical = PaddingSmall),
                    selectedValue = stringResource(id = selectedWorkoutSelection.getStringId()),
                    leftText = stringResource(id = WorkoutSelection.SINGLE_WORKOUT.getStringId()),
                    rightText = stringResource(id = WorkoutSelection.TRAINING_PLAN.getStringId()),
                    onSelectionChanged = onWorkoutSelectionTypeChange
                )

                if (data.isEmpty()) {
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
                        if (selectedWorkoutSelection == WorkoutSelection.SINGLE_WORKOUT) {
                            items(data, key = { it.id }) { item ->
                                WorkoutItem(workout = item as WorkoutModel, onClick = { onClick(it) })
                            }
                        } else {
                            items(data, key = { it.id }) { item ->
                                TrainingPlanItem(trainingPlan = item as TrainingPlanModel, onClick = { onClick(it) })
                            }
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