package com.example.workouttracker.data.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.util.Date

/** WorkoutModel class representing an exercise.
 * Must correspond with server-side WorkoutModel
 */
class WorkoutModel: BaseModel {
    @SerializedName("Name")
    var name: String

    @SerializedName("StartDateTime")
    val startDateTime: Date?

    @SerializedName("ScheduledDateTime")
    val scheduledDateTime: Date?

    @SerializedName("FinishDateTime")
    var finishDateTime: Date?

    @SerializedName("Template")
    val template: Boolean

    @SerializedName("Exercises")
    val exercises: MutableList<ExerciseModel>

    @SerializedName("DurationSeconds")
    var durationSeconds: Int?

    @SerializedName("Notes")
    var notes: String

    @SerializedName("WeightUnit")
    var weightUnit: String

    @SerializedName("AssignedWorkoutId")
    var assignedWorkoutId: Long

    /** Constructor to accept serialized object
     * @param data serialized WorkoutModel object
     */
    constructor(data: String) : super(data) {
        val gson = Gson()
        val model: WorkoutModel = gson.fromJson(data, WorkoutModel::class.java)

        name = model.name
        startDateTime = model.startDateTime
        scheduledDateTime = model.scheduledDateTime
        finishDateTime = model.finishDateTime
        template = model.template
        exercises = model.exercises
        durationSeconds = model.durationSeconds
        notes = model.notes
        weightUnit = model.weightUnit
        assignedWorkoutId = model.assignedWorkoutId
    }

    /** Constructor used when new workout is created / or workout is being edited */
    constructor(idVal: Long, nameVal: String, templateVal: Boolean, exercisesVal: MutableList<ExerciseModel>, notesVal: String,
                finishDateTimeVal: Date?, durationVal: Int?, weightUnitTextVal: String = ""
    ) : super (idVal) {
        name = nameVal

        if (templateVal) {
            startDateTime = null
            scheduledDateTime = null
        } else {
            startDateTime = Date()
            scheduledDateTime = Date()
        }

        finishDateTime = finishDateTimeVal
        template = templateVal
        exercises = exercisesVal
        durationSeconds = durationVal
        notes = notesVal
        weightUnit = weightUnitTextVal
        assignedWorkoutId = 0L
    }
}