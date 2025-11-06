package com.example.workouttracker.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workouttracker.R
import com.example.workouttracker.data.models.TeamModel
import com.example.workouttracker.ui.extensions.customBorder
import com.example.workouttracker.ui.reusable.DialogButton
import com.example.workouttracker.ui.reusable.ImageButton
import com.example.workouttracker.ui.reusable.Label
import com.example.workouttracker.ui.reusable.Spinner
import com.example.workouttracker.ui.reusable.SpinnerItem
import com.example.workouttracker.ui.reusable.TwoTextsSwitch
import com.example.workouttracker.ui.theme.DialogFooterSize
import com.example.workouttracker.ui.theme.PaddingMedium
import com.example.workouttracker.ui.theme.PaddingSmall
import com.example.workouttracker.utils.Constants.ViewTeamAs
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.viewmodel.AssignedWorkoutsFiltersViewModel
import java.util.Date

/**
 * Dialog to display the filters for assigned workouts screen
 * @param viewTeamAs the current team type view filter
 * @param startDate the current start date filter
 * @param teamFilter the current team filter
 * @param onApply callback to be invoked when the user applies the filters
 */
@Composable
fun AssignedWorkoutsFiltersDialog(
    vm: AssignedWorkoutsFiltersViewModel = hiltViewModel(),
    viewTeamAs: ViewTeamAs,
    startDate: Date,
    teamFilter: TeamModel,
    onApply: (ViewTeamAs, Date, TeamModel) -> Unit
) {
    LaunchedEffect(Unit) {
        vm.initializeData(
            startDateValue = startDate,
            teamFilterValue = teamFilter,
            selectedTeamTypeValue = viewTeamAs
        )
    }
    val startDateFilter by vm.startDate.collectAsStateWithLifecycle()
    val teamFilter by vm.teamFilter.collectAsStateWithLifecycle()
    val myTeams by vm.teamRepository.teams.collectAsStateWithLifecycle()
    val spinnerItems: List<SpinnerItem> = myTeams.map { team ->
        val teamImagePainter = if (!team.image.isEmpty()) {
            val bitmap = Utils.convertStringToBitmap(team.image)
            BitmapPainter(bitmap.asImageBitmap())
        } else {
            painterResource(id = R.drawable.icon_team_default_picture)
        }

        SpinnerItem(
            key = team.id.toString(),
            text = team.name,
            imagePainter = teamImagePainter
        )
    }
    val selectedSpinnerItem = spinnerItems.find { it.key == teamFilter.id.toString() }
    val selectedTeamType by vm.selectedTeamType.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PaddingMedium)
    ) {
        TwoTextsSwitch(
            modifier = Modifier.padding(PaddingSmall),
            selectedValue = stringResource(id = selectedTeamType.getStringId()),
            leftText = stringResource(id = ViewTeamAs.COACH.getStringId()),
            rightText = stringResource(id = ViewTeamAs.MEMBER.getStringId()),
            onSelectionChanged = { vm.updateSelectedTeamType(it) }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingSmall)
                .clickable(
                    enabled = true,
                    onClick = { vm.showDatePicker() }
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Label(
                text = String.format(
                    stringResource(id = R.string.workouts_filter_lbl),
                    Utils.defaultFormatDate(startDateFilter)
                ),
            )

            ImageButton(
                onClick = { vm.showDatePicker() },
                image = Icons.Default.DateRange
            )
        }

        Spinner(
            modifier = Modifier.padding(horizontal = PaddingSmall),
            items = spinnerItems,
            selectedItem = selectedSpinnerItem,
            onItemSelected = {
                vm.updateTeamFilter(it)
            },
            isInDialog = true
        )

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(DialogFooterSize)
        ) {
            DialogButton(
                modifier = Modifier
                    .customBorder()
                    .weight(1f),
                text = stringResource(R.string.apply_btn),
                onClick = {
                    onApply(selectedTeamType, startDateFilter, teamFilter)
                }
            )
        }
    }
}
