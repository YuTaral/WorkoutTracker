package com.example.workouttracker.data.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** TrainingPlanModel class representing a training day as port of training plan.
 * Must correspond with server-side TrainingPlanModel
 */
class TrainingPlanModel: BaseModel {
    @SerializedName("Name")
    var name: String

    @SerializedName("Description")
    var description: String

    @SerializedName("TrainingDays")
    var trainingDays: MutableList<TrainingDayModel>

    /** Constructor to deserialized TrainingDayModel object
     * @param data serialized TrainingDayModel object
     */
    constructor(data: String) : super(data) {
        val gson = Gson()
        val model: TrainingPlanModel = gson.fromJson(data, TrainingPlanModel::class.java)

        name = model.name
        description = model.description
        trainingDays = model.trainingDays
    }

    /** Constructor to create new objects with id = 0 */
    constructor(idVal: Long, nameVal: String, descriptionVal: String): super (idVal) {
        name = nameVal
        description = descriptionVal
        trainingDays = mutableListOf()
    }
}