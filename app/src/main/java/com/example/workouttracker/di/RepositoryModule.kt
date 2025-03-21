package com.example.workouttracker.di

import com.example.workouttracker.data.managers.NetworkManager
import com.example.workouttracker.data.managers.SharedPrefsManager
import com.example.workouttracker.data.network.APIService
import com.example.workouttracker.data.network.repositories.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(apiService: APIService,
                              sharedPrefsManager: SharedPrefsManager,
                              networkManager: NetworkManager): UserRepository {
        return UserRepository(sharedPrefsManager, apiService, networkManager)
    }

}