package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.models.TrainingProgramModel
import com.example.workouttracker.data.network.repositories.TrainingProgramRepository
import com.example.workouttracker.ui.dialogs.AddEditTrainingPlanDialog
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** View model to control the UI state of Training Program screen */
@HiltViewModel
class ManageTrainingPlanViewModel @Inject constructor(
    var trainingProgramRepository: TrainingProgramRepository,
    private var dialogManager: DialogManager,
    private var resourceProvider: ResourceProvider,
    private var pagerManager: PagerManager
): ViewModel() {

    /** Initialize data */
    fun initializeData() {
        viewModelScope.launch {
            trainingProgramRepository.refreshTrainingPrograms()
        }
    }

    /** Show dialog to add training plan */
    fun showAddTrainingPlan() {
        trainingProgramRepository.updateSelectedTrainingProgram(null)

        viewModelScope.launch {
            dialogManager.showDialog(
                title = resourceProvider.getString(R.string.add_training_plan),
                dialogName = "AddEditTrainingPlanDialog",
                content = { AddEditTrainingPlanDialog() }
            )
        }
    }

    /**
     * Select the training program
     * @param trainingProgram the training program to select
     */
    fun selectTrainingProgram(trainingProgram: TrainingProgramModel?) {
        trainingProgramRepository.updateSelectedTrainingProgram(trainingProgram)

        if (trainingProgram != null) {
            viewModelScope.launch {
                pagerManager.changePageSelection(Page.SelectedTrainingPlan)
            }
        }
    }
}