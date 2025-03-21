package com.example.workouttracker.di

import android.content.Context
import com.example.workouttracker.data.managers.NetworkManager
import com.example.workouttracker.data.managers.SharedPrefsManager
import com.example.workouttracker.data.network.APIService
import com.example.workouttracker.data.network.IAPIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAPIService(sharedPrefsManager: SharedPrefsManager): IAPIService {
        val apiService = APIService(sharedPrefsManager)
        return apiService.getInstance()
    }

    @Provides
    @Singleton
    fun provideNetworkManager(@ApplicationContext context: Context): NetworkManager {
        return NetworkManager(context)
    }
}