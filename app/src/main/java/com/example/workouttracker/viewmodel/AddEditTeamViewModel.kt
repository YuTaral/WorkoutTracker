package com.example.workouttracker.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.R
import com.example.workouttracker.data.models.TeamCoachModel
import com.example.workouttracker.data.models.TeamMemberModel
import com.example.workouttracker.data.models.TeamModel
import com.example.workouttracker.data.network.repositories.TeamRepository
import com.example.workouttracker.ui.dialogs.ManageMembersDialog
import com.example.workouttracker.ui.managers.AskQuestionDialogManager
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.DisplayAskQuestionDialogEvent
import com.example.workouttracker.ui.managers.ImagePickerEventBus
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.ui.managers.Question
import com.example.workouttracker.ui.managers.SnackbarManager
import com.example.workouttracker.ui.managers.VibrationManager
import com.example.workouttracker.utils.ResourceProvider
import com.example.workouttracker.utils.Utils
import com.example.workouttracker.utils.interfaces.IImagePicker
import com.example.workouttracker.viewmodel.ManageTeamsViewModel.ViewTeamAs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTeamViewModel @Inject constructor(
    var teamRepository: TeamRepository,
    private var resourceProvider: ResourceProvider,
    private val imagePickerBus: ImagePickerEventBus,
    private val vibrationManager: VibrationManager,
    private var askQuestionManager: AskQuestionDialogManager,
    private var dialogManager: DialogManager
): ViewModel(), IImagePicker {

    /** Class representing the UI state fields */
    data class UIState(
        val image: String = "",
        val name: String = "",
        val description: String = "",
        val nameError: String? = null
    )

    /** Class representing the data when team is being viewed as member */
    data class ViewTeamAsMemberData(
        val teamImage: String = "",
        val teamName: String = "",
        val teamDescr: String = "",
        val coach: TeamCoachModel = TeamCoachModel(),
        val members: List<TeamMemberModel> = listOf()
    )

    /** Dialog state */
    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()

    /** Member UI state */
    private val _memberUIState = MutableStateFlow(ViewTeamAsMemberData())
    val memberUIState = _memberUIState.asStateFlow()

    /**
     * Initialize the data in the view model when the screen is shown
     */
    fun initialize(team: TeamModel?) {
        viewModelScope.launch {
            teamRepository.updateSelectedTeam(team)
        }

        if (teamRepository.selectedTeam.value == null) {
            updateImage("")
            updateName("")
            updateDescription("")

            viewModelScope.launch {
                teamRepository.refreshMyTeamMembers(teamId = 0L)
            }
        } else {
            updateImage(teamRepository.selectedTeam.value!!.image)
            updateName(teamRepository.selectedTeam.value!!.name)
            updateDescription(teamRepository.selectedTeam.value!!.description)

            viewModelScope.launch(Dispatchers.IO) {
                teamRepository.refreshMyTeamMembers(teamId = team!!.id)
            }

            if (teamRepository.selectedTeam.value!!.viewTeamAs == ViewTeamAs.MEMBER.name) {
                viewModelScope.launch(Dispatchers.IO) {
                    teamRepository.getTeamDetailsAsMember(
                        teamId = teamRepository.selectedTeam.value!!.id,
                        onSuccess = { updateMemberUIState(it) }
                    )
                }
            } else {
                _memberUIState.value = ViewTeamAsMemberData()
            }
        }
    }

    /** Update the image in the UI with the provided value */
    fun updateImage(value: String) {
        _uiState.update { it.copy(image = value) }
    }

    /** Update the name in the UI with the provided value */
    fun updateName(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    /** Update the description in the UI with the provided value */
    fun updateDescription(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    /** Update the name error in the UI with the provided value */
    fun updateNameError(value: String?) {
        _uiState.update { it.copy(nameError = value) }
    }

    /** Change the team image on click */
    fun onImageClick() {
        viewModelScope.launch {
            imagePickerBus.requestImagePicker(this@AddEditTeamViewModel)
        }
    }

    /** Add / edit the team on save button click */
    fun saveTeam() {
        if (!validate()) {
            return
        }

        if (teamRepository.selectedTeam.value == null) {
            addTeam()
        } else {
            editTeam()
        }
    }

    /** Ask question to confirm team deletion */
    fun askDeleteTeam() {
        viewModelScope.launch {
            askQuestionManager.askQuestion(DisplayAskQuestionDialogEvent(
                question = Question.DELETE_TEAM,
                show = true,
                onConfirm = {
                    viewModelScope.launch(Dispatchers.IO) {
                        teamRepository.deleteTeam(
                            teamId = teamRepository.selectedTeam.value!!.id,
                            onSuccess = {
                                viewModelScope.launch {
                                    PagerManager.changePageSelection(Page.ManageTeams(teamType = ViewTeamAs.COACH))
                                }
                            }
                        )
                    }
                },
                formatQValues = listOf(teamRepository.selectedTeam.value!!.name)
            ))
        }
    }

    /** Show manage members dialog */
    fun showManageMembers() {
        viewModelScope.launch {
            dialogManager.showDialog(
                title = resourceProvider.getString(R.string.manage_team_members_lbl),
                dialogName = "ManageMembersDialog",
                content = { ManageMembersDialog() }
            )
        }
    }

    /** Ask for confirmation to leave the team */
    fun askLeaveTeam() {
        viewModelScope.launch {
            askQuestionManager.askQuestion(DisplayAskQuestionDialogEvent(
                question = Question.LEAVE_TEAM,
                show = true,
                onConfirm = {
                    viewModelScope.launch(Dispatchers.IO) {
                        teamRepository.leaveTeam(
                            teamId = teamRepository.selectedTeam.value!!.id,
                            onSuccess = {
                                viewModelScope.launch {
                                    PagerManager.changePageSelection(Page.ManageTeams(ViewTeamAs.MEMBER))
                                }
                            }
                        )
                    }
                },
                formatQValues = listOf(teamRepository.selectedTeam.value!!.name)
            ))
        }
    }

    /** Send request to add new team */
    private fun addTeam() {
        val team = TeamModel(
            idVal = 0,
            imageVal = _uiState.value.image,
            nameVal = _uiState.value.name,
            descriptionVal = _uiState.value.description
        )

        viewModelScope.launch(Dispatchers.IO) {
            teamRepository.addTeam(
                team = team,
                onSuccess = {
                    viewModelScope.launch {
                        PagerManager.changePageSelection(Page.ManageTeams(teamType = ViewTeamAs.COACH))
                    }
                }
            )
        }
    }

    /** Send request to edit the selected team */
    private fun editTeam() {
        val team = TeamModel(
            idVal = teamRepository.selectedTeam.value!!.id,
            imageVal = _uiState.value.image,
            nameVal = _uiState.value.name,
            descriptionVal = _uiState.value.description
        )

        viewModelScope.launch(Dispatchers.IO) {
            teamRepository.editTeam(
                team = team,
                onSuccess = {
                    viewModelScope.launch {
                        PagerManager.changePageSelection(Page.ManageTeams(teamType = ViewTeamAs.COACH))
                    }
                }
            )
        }
    }

    /** Validate the fields in the UI, return true if valid, false otherwise */
    private fun validate(): Boolean {
        if (_uiState.value.name.isEmpty()) {
            viewModelScope.launch { vibrationManager.makeVibration() }
            updateNameError(resourceProvider.getString(R.string.error_msg_enter_team_name))
            return false
        } else {
            updateNameError(null)
        }

        return true
    }

    /** Update the member ui state with the provided data */
    private fun updateMemberUIState(data: List<String>) {
        _memberUIState.update {
            it.copy(teamImage = teamRepository.selectedTeam.value!!.image)
                .copy(teamName = teamRepository.selectedTeam.value!!.name)
                .copy(teamDescr = teamRepository.selectedTeam.value!!.description)
                .copy(coach = TeamCoachModel(data[0]))
                .copy(members = data.drop(1).map { TeamMemberModel(it) })
        }
    }

    override fun onImageUploadSuccess(bitmap: Bitmap) {
        updateImage(Utils.convertBitmapToString(bitmap))
    }

    override fun onImageUploadFail() {
        viewModelScope.launch {
            SnackbarManager.showSnackbar(R.string.error_msg_failed_to_upload_image)
        }
    }
}