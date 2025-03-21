package com.example.workouttracker.data.network

/** CustomResponse class to define the fields which are returned on request response */
data class CustomResponse(
    val code: Int,
    val message: String,
    val data: List<String>,
    val notification: Boolean
)