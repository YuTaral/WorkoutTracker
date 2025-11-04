package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.models.TrainingDayModel
import com.example.workouttracker.data.network.repositories.TrainingPlanRepository
import com.example.workouttracker.data.network.repositories.WorkoutTemplatesRepository
import com.example.workouttracker.ui.dialogs.AddEditTrainingPlanDialog
import com.example.workouttracker.ui.dialogs.AddEditTrainingDayDialog
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** View model to control the UI state of Training Program screen */
@HiltViewModel
class SelectedTrainingPlanViewModel @Inject constructor(
    var trainingProgramRepository: TrainingPlanRepository,
    private var templatesRepository: WorkoutTemplatesRepository,
    private var dialogManager: DialogManager,
    private var resourceProvider: ResourceProvider
): ViewModel() {

    /** Show the dialog to edit training program */
    fun showEditTrainingPlan() {
        viewModelScope.launch {
            dialogManager.showDialog(
                title = resourceProvider.getString(R.string.edit_training_plan_dialog_title),
                dialogName = "AddEditTrainingPlanDialog",
                content = { AddEditTrainingPlanDialog() }
            )
        }
    }

    /** Show dialog to select workout for the selected training day or remove the day */
    fun showAddEditTrainingDay(trainingDayModel: TrainingDayModel?, rowNumberVal: Int) {
        var rowNumber = rowNumberVal
        var model = trainingDayModel

        if (model == null) {
            // Create new training day, using the current days + 1 for number
            rowNumber = trainingProgramRepository.selectedTrainingPlan.value!!.trainingDays.size + 1
            model = TrainingDayModel(trainingProgramRepository.selectedTrainingPlan.value!!.id)
        }

        viewModelScope.launch(Dispatchers.IO) {
            templatesRepository.refreshTemplates()

            withContext(Dispatchers.Main) {
                dialogManager.showDialog(
                    title = String.format(resourceProvider.getString(R.string.edit_day_number), rowNumber),
                    dialogName = "AddEditTrainingDayDialog",
                    content = { AddEditTrainingDayDialog(model = model) }
                )
            }
        }
    }
}