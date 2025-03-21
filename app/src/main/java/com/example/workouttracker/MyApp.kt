package com.example.workouttracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** Class inheriting Application to enable dependency injection with Hilt */
@HiltAndroidApp
class MyApp: Application()