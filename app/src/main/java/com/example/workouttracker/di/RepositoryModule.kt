package com.example.workouttracker.di

import com.example.workouttracker.data.managers.NetworkManager
import com.example.workouttracker.data.managers.SharedPrefsManager
import com.example.workouttracker.data.network.APIService
import com.example.workouttracker.data.network.repositories.ExerciseRepository
import com.example.workouttracker.data.network.repositories.MuscleGroupRepository
import com.example.workouttracker.data.network.repositories.NotificationRepository
import com.example.workouttracker.data.network.repositories.TrainingPlanRepository
import com.example.workouttracker.data.network.repositories.SystemLogRepository
import com.example.workouttracker.data.network.repositories.TeamRepository
import com.example.workouttracker.data.network.repositories.UserProfileRepository
import com.example.workouttracker.data.network.repositories.UserRepository
import com.example.workouttracker.data.network.repositories.WorkoutRepository
import com.example.workouttracker.data.network.repositories.WorkoutTemplatesRepository

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

    @Provides
    @Singleton
    fun provideWorkoutRepository(
        apiService: APIService,
        networkManager: NetworkManager
    ): WorkoutRepository {
        return WorkoutRepository(apiService, networkManager)
    }

    @Provides
    @Singleton
    fun provideMuscleGroupRepository(
        apiService: APIService,
        networkManager: NetworkManager
    ): MuscleGroupRepository {
        return MuscleGroupRepository(apiService, networkManager)
    }

    @Provides
    @Singleton
    fun provideExerciseRepository(
        apiService: APIService,
        networkManager: NetworkManager
    ): ExerciseRepository {
        return ExerciseRepository(apiService, networkManager)
    }

    @Provides
    @Singleton
    fun provideUserProfileRepository(apiService: APIService, networkManager: NetworkManager): UserProfileRepository {
        return UserProfileRepository(apiService, networkManager)
    }

    @Provides
    @Singleton
    fun provideWorkoutTemplatesRepository(
        apiService: APIService,
        networkManager: NetworkManager
    ): WorkoutTemplatesRepository {
        return WorkoutTemplatesRepository(apiService, networkManager)
    }

    @Provides
    @Singleton
    fun provideTeamRepository(
        apiService: APIService,
        networkManager: NetworkManager
    ): TeamRepository {
        return TeamRepository(apiService, networkManager)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        apiService: APIService,
        networkManager: NetworkManager
    ): NotificationRepository {
        return NotificationRepository(apiService, networkManager)
    }

    @Provides
    @Singleton
    fun provideSystemLogRepository(
        apiService: APIService,
        networkManager: NetworkManager
    ): SystemLogRepository {
        return SystemLogRepository(apiService, networkManager)
    }

    @Provides
    @Singleton
    fun provideProgramRepository(
        apiService: APIService,
        networkManager: NetworkManager
    ): TrainingPlanRepository {
        return TrainingPlanRepository(apiService, networkManager)
    }
}