package com.example.workouttracker.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.ui.reusable.ImageButton
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.reusable.TwoTextsSwitch
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.ui.theme.LazyListBottomPadding
import com.example.workouttracker.ui.theme.PaddingMedium
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.viewmodel.ManageTeamsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign
import com.example.workouttracker.ui.components.TeamItem
import com.example.workouttracker.viewmodel.ManageTeamsViewModel.ViewTeamAs

/**
 * The screen displaying the teams the user owns / participates in
 * @param teamType the initial value of team type
 */
@Composable
fun ManageTeamsScreen(teamType: ViewTeamAs, vm: ManageTeamsViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        vm.initializeData(teamType = teamType)
    }

    val selectedTeamType by vm.selectedTeamType.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val teams by vm.teamRepository.teams.collectAsStateWithLifecycle()

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(PaddingSmall)
    ) {
        TwoTextsSwitch(
            modifier = Modifier.padding(horizontal = PaddingMedium),
            selectedValue = stringResource(id = selectedTeamType.getStringId()),
            leftText = stringResource(id = ViewTeamAs.COACH.getStringId()),
            rightText = stringResource(id = ViewTeamAs.MEMBER.getStringId()),
            onSelectionChanged = { vm.updateSelectedTeamType(it) }
        )

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(top = PaddingMedium)
        ) {
            if (teams.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(PaddingMedium)
                ) {
                    Label(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = vm.getNoTeamsMessage()),
                        style = labelMediumGrey,
                        maxLines = 2,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    contentPadding = PaddingValues(bottom = LazyListBottomPadding)
                ) {
                    items(teams) {
                        TeamItem(
                            team = it,
                            onClick = { vm.updateSelectedTeam(it) }
                        )
                    }
                }
            }

            if (selectedTeamType == ViewTeamAs.COACH) {
                ImageButton(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onClick = { vm.showAddTeam() },
                    image = Icons.Default.Add
                )
            }
        }
    }
}

@Preview
@Composable
private fun ManageTeamsScreenPreview() {
    WorkoutTrackerTheme {
        ManageTeamsScreen(ViewTeamAs.COACH)
    }
}