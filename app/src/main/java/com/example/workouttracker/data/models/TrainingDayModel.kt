package com.example.workouttracker.data.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** TrainingDayModel class representing a training day as port of training plan.
 * Must correspond with server-side TrainingDayModel
 */
class TrainingDayModel: BaseModel {

    @SerializedName("ProgramId")
    var programId: Long

    @SerializedName("Workouts")
    var workouts: List<WorkoutModel>

    /** Constructor to deserialized TrainingDayModel object
     * @param data serialized TrainingDayModel object
     */
    constructor(data: String) : super(data) {
        val gson = Gson()
        val model: TrainingDayModel = gson.fromJson(data, TrainingDayModel::class.java)

        programId = model.programId
        workouts = model.workouts
    }

    /** Constructor used when new training day is created */
    constructor(programIdVal: Long) : super(0) {
        programId = programIdVal
        workouts = listOf()
    }
}