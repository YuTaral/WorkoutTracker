package com.example.workouttracker.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.ui.reusable.ImageButton
import com.example.workouttracker.ui.reusable.InputField
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.components.MemberItem
import com.example.workouttracker.ui.theme.ColorAccent
import com.example.workouttracker.ui.theme.ColorBorder
import com.example.workouttracker.ui.theme.PaddingLarge
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.ui.theme.WorkoutTrackerTheme
import com.example.workouttracker.ui.theme.labelMediumGrey
import com.example.workouttracker.viewmodel.ManageMembersViewModel
import androidx.compose.runtime.getValue

/** Dialog to manage team members */
@Composable
fun ManageMembersDialog(vm: ManageMembersViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        vm.initializeData()
    }

    DisposableEffect(Unit) {
        onDispose {
            vm.updateTeamsOnClose()
        }
    }

    val teamMembersListState = rememberLazyListState()
    val searchMembersListState = rememberLazyListState()
    val members by vm.teamRepository.teamMembers.collectAsStateWithLifecycle()
    val searchResults by vm.searchResult.collectAsStateWithLifecycle()
    val searchTerm by vm.search.collectAsStateWithLifecycle()
    val showNoUsersFound by vm.showNoUsersFound.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingSmall),
        verticalArrangement = Arrangement.spacedBy(PaddingSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InputField(
                value = searchTerm,
                modifier = Modifier.weight(1f),
                label = stringResource(id = R.string.search_members_lbl),
                onValueChange = { vm.updateSearch(it) }
            )

            ImageButton(
                onClick = { vm.searchMembers() },
                image = Icons.Default.Search,
                buttonColor = Color.Transparent,
                imageColor = ColorAccent
            )
        }

        HorizontalDivider(
            color = ColorBorder,
            thickness = 2.dp
        )

        Label(
            text = String.format(stringResource(id = R.string.search_results_lbl), searchResults.size),
            style = labelMediumGrey,
            textAlign = TextAlign.Center
        )

        if (showNoUsersFound) {
            Label(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.no_users_found),
                style = labelMediumGrey,
                maxLines = 2
            )
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(min = 100.dp, max = 100.dp),
                state = searchMembersListState,
            ) {
                items(searchResults) {
                    MemberItem(
                        member = it,
                        showButton = true,
                        onAction = { vm.onClick(it) }
                    )
                }
            }
        }

        HorizontalDivider(
            color = ColorBorder,
            thickness = 2.dp
        )

        Label(
            text = String.format(stringResource(id = R.string.team_members_lbl), members.size),
            style = labelMediumGrey,
            textAlign = TextAlign.Center
        )

        LazyColumn(
            modifier = Modifier
                .heightIn(min = 250.dp, max = 250.dp)
                .padding(bottom = PaddingLarge),
            state = teamMembersListState,
        ) {
            items(members) {
                MemberItem(
                    member = it,
                    showButton = true,
                    onAction = { vm.onClick(it) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun ManageMembersPreview() {
    WorkoutTrackerTheme {
        ManageMembersDialog()
    }
}