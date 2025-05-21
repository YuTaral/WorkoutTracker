package com.example.workouttracker.ui.managers

import com.example.workouttracker.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** Enum with all questions */
enum class Question(private val titleId: Int, private val questionId: Int,
                    private val confirmButtonTextId: Int, private val cancelBtnTextId: Int) {

    LOG_OUT(R.string.q_log_out_title, R.string.q_log_out_text, R.string.yes_btn, R.string.no_btn),
    DELETE_WORKOUT(R.string.question_delete_workout_title, R.string.question_delete_workout_text, R.string.yes_btn, R.string.no_btn),
    IMAGE_SELECTION_OPTIONS(R.string.question_choose_image_title, R.string.question_choose_image_text, R.string.camera_btn, R.string.gallery_btn),
    ALLOW_CAMERA_PERMISSION(R.string.question_go_to_settings_title, R.string.question_go_to_settings_text, R.string.go_to_settings_btn, R.string.no_btn),
    DELETE_MG_EXERCISE(R.string.question_delete_exercise_title, R.string.question_delete_exercise_text, R.string.yes_btn, R.string.no_btn);

    /** Returns the question title */
    fun getTitle(): Int {
        return titleId
    }

    /** Returns the question text */
    fun getQuestionText(): Int {
        return questionId
    }

    /** Returns the confirm button text */
    fun getConfirmButtonText(): Int {
        return confirmButtonTextId
    }

    /** Returns the cancel button text */
    fun getCancelButtonText(): Int {
        return cancelBtnTextId
    }
}

/** Data class containing the show/hide dialog event */
data class DisplayAskQuestionDialogEvent(
    val question: Question?,
    val show: Boolean = false,
    val onCancel: () -> Unit = {},
    val onConfirm: () -> Unit = {},
    val formatQValues: List<String> = listOf()
)

/** Class to trigger events when we need to ask user for confirmation */
object AskQuestionDialogManager {
    private val _events = MutableSharedFlow<DisplayAskQuestionDialogEvent>(replay = 0, extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    /** Show Ask Question dialog */
    suspend fun askQuestion(event: DisplayAskQuestionDialogEvent) {
        _events.emit(event)
    }

    /** Hide Ask Question dialog */
    suspend fun hideQuestion() {
        _events.emit(DisplayAskQuestionDialogEvent(null, false))
    }
}