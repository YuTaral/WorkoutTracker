package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.models.TrainingPlanModel
import com.example.workouttracker.data.network.repositories.TrainingPlanRepository
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
    var trainingProgramRepository: TrainingPlanRepository,
    private var dialogManager: DialogManager,
    private var resourceProvider: ResourceProvider,
    private var pagerManager: PagerManager
): ViewModel() {

    /** Initialize data */
    fun initializeData() {
        viewModelScope.launch {
            trainingProgramRepository.refreshTrainingPlans()
        }
    }

    /** Show dialog to add training plan */
    fun showAddTrainingPlan() {
        trainingProgramRepository.updateSelectedTrainingPlan(null)

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
    fun selectTrainingProgram(trainingProgram: TrainingPlanModel?) {
        trainingProgramRepository.updateSelectedTrainingPlan(trainingProgram)

        if (trainingProgram != null) {
            viewModelScope.launch {
                pagerManager.changePageSelection(Page.SelectedTrainingPlan)
            }
        }
    }
}