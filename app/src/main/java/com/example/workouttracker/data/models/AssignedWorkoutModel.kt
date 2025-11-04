package com.example.workouttracker.data.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * AssignedWorkoutModel class representing an assigned workout.
 * Must correspond with server-side AssignedWorkoutModel
 */
class AssignedWorkoutModel : BaseModel {

    @SerializedName("WorkoutModel")
    var workoutModel: WorkoutModel

    @SerializedName("TeamName")
    var teamName: String

    @SerializedName("TeamImage")
    var teamImage: String

    @SerializedName("TeamId")
    var teamId: Long

    @SerializedName("UserFullName")
    var userFullName: String

    @SerializedName("ScheduledForDate")
    var scheduledForDate: Date

    @SerializedName("DateTimeCompleted")
    var dateTimeCompleted: Date? = null

    /**
     * Constructor to accept serialized object
     * @param data serialized AssignedWorkoutModel object
     */
    constructor(data: String) : super(data) {
        val gson = Gson()
        val model: AssignedWorkoutModel = gson.fromJson(data, AssignedWorkoutModel::class.java)

        this.workoutModel = model.workoutModel
        this.teamName = model.teamName
        this.teamImage = model.teamImage
        this.teamId = model.teamId
        this.userFullName = model.userFullName
        this.scheduledForDate = model.scheduledForDate
        this.dateTimeCompleted = model.dateTimeCompleted
    }

    /** Empty constructor for previews */
    constructor(workoutModel: WorkoutModel) : super(0L) {
        this.workoutModel = workoutModel
        this.teamName = "Test team name"
        this.teamImage = ""
        this.teamId = 0L
        this.userFullName = "Test user full name"
        this.scheduledForDate = Date()
        this.dateTimeCompleted = null
    }
}
