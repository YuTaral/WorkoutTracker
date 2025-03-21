package com.example.workouttracker.data.models

import com.google.gson.annotations.SerializedName

/** ExerciseModel class representing an exercise.
 * Must correspond with server-side ExerciseModel
 */
class ExerciseModel: BaseModel {
    @SerializedName("Name")
    val name: String

    @SerializedName("MuscleGroup")
    val muscleGroup: MuscleGroupModel

    @SerializedName("Sets")
    val sets: MutableList<SetModel>

    @SerializedName("MGExerciseId")
    val mGExerciseId: Long?

    @SerializedName("Notes")
    var notes: String

    /** Constructor used when new exercise is created */
    constructor(nameVal: String, muscleGroupVal: MuscleGroupModel, setsCountVal: Int, repsVal: Int,
                weightVal: Double, restVal: Int, completedVal: Boolean, mGExerciseIdVal: Long?, notesVal: String) : super(0) {
        name = nameVal
        muscleGroup = muscleGroupVal
        sets = mutableListOf()
        mGExerciseId = mGExerciseIdVal

        for (i in 0..<setsCountVal) {
            sets.add(i, SetModel(0, repsVal, weightVal, restVal, completedVal, false))
        }

        notes = notesVal
    }

    /** Constructor used when exercise is updated */
    constructor(idVal:Long, nameVal:  String, muscleGroupVal: MuscleGroupModel, setsVal: MutableList<SetModel>,
                mGExerciseIdVal: Long?, notesVal: String) : super(idVal) {
        name = nameVal
        muscleGroup = muscleGroupVal
        sets = setsVal
        mGExerciseId = mGExerciseIdVal
        notes = notesVal
    }
}