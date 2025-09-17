package com.example.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.managers.SharedPrefsManager
import com.example.workouttracker.data.network.repositories.NotificationRepository
import com.example.workouttracker.data.network.repositories.SystemLogRepository
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.ui.managers.AskQuestionDialogManager
import com.example.workouttracker.ui.managers.DatePickerDialogManager
import com.example.workouttracker.ui.managers.DialogManager
import com.example.workouttracker.ui.managers.DisplayAskQuestionDialogEvent
import com.example.workouttracker.ui.managers.LoadingManager
import com.example.workouttracker.ui.managers.PagerManager
import com.example.workouttracker.ui.managers.Question
import com.example.workouttracker.ui.managers.SnackbarManager
import com.example.workouttracker.ui.managers.SystemLogManager
import com.example.workouttracker.ui.managers.VibrationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/** MainViewModel to manage the state of the main screen */
@HiltViewModel
class MainViewModel @Inject constructor(
    private var systemLogRepository: SystemLogRepository,
    var userRepository: UserRepository,
    var notificationRepository: NotificationRepository,
    var sharedPrefsManager: SharedPrefsManager,
    var vibrationManager: VibrationManager,
    var askQuestionManager: AskQuestionDialogManager,
    var datePickerManager: DatePickerDialogManager,
    var dialogManager: DialogManager,
    var loadingManager: LoadingManager,
    var snackbarManager: SnackbarManager,
    var pagerManager: PagerManager,
    var systemLogManager: SystemLogManager
): ViewModel() {

    /** Job to refresh the notification on every N seconds */
    private var refreshJob: Job? = null

    /** Schedule background job to refresh the notification on every 30 seconds */
    fun scheduleRefreshNotification() {
        refreshJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                notificationRepository.refreshNotification()
                delay(30000L)
            }
        }
    }

    /** Cancel the refresh notification */
    fun cancelRefreshNotification() {
        refreshJob?.cancel()
    }

    /**
     * Show the dialog to ask user to grant camera permissions
     * @param callback callback to execute on confirm
     */
    fun showAllowCameraQuestion(callback: () -> Unit) {
        viewModelScope.launch {
            askQuestionManager.askQuestion(
                DisplayAskQuestionDialogEvent(
                    question = Question.ALLOW_CAMERA_PERMISSION,
                    onConfirm = { callback() }
                ),
            )
        }
    }

    /**
     * Show the dialog to ask user to grant all permissions, starting with camera
     * @param onConfirm callback to execute on confirm
     */
    fun showAskForAllPermissions(onConfirm: () -> Unit) {
        viewModelScope.launch {
            userRepository.requestPermissions.collect {
                viewModelScope.launch {
                    askQuestionManager.askQuestion(DisplayAskQuestionDialogEvent(
                        question = Question.GRANT_PERMISSIONS,
                        onConfirm = { onConfirm() }
                    ))
                }
            }
        }
    }

    /**
     * Change displayed page
     * @param page the new page
     */
    fun changePage(page: Page) {
        viewModelScope.launch {
            pagerManager.changePageSelection(page)
        }
    }

    /**
     * Add system log to the database
     * @param exception the exception to log
     */
    fun addSystemLog(exception: Exception) {
        viewModelScope.launch(Dispatchers.IO) {
            systemLogRepository.addSystemLog(
                message = exception.message.toString(),
                stackTrace = exception.stackTraceToString()
            )
        }
    }
}