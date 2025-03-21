package com.example.workouttracker.data.models

/** Model used when asking question for assign workout. The model is used only client side */
class QAssignWorkoutModel(teamNameVal: String, membersVal: String) : BaseModel(0) {
    val teamName: String = teamNameVal
    val members: String = membersVal
}