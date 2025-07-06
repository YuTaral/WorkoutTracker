package com.example.workouttracker.utils

import com.example.workouttracker.BuildConfig

/** Object to with all constants */
object Constants {
    const val URL: String = BuildConfig.BASE_URL
    const val SECURE_PREFS_FILE_NAME = "secure_prefs"
    const val AUTH_TOKEN_KEY = "auth_token"
    const val SERIALIZED_USER_KEY = "serialized_user"
    const val FIRST_START_KEY = "first_start"
    val VALIDATION_FAILED_VIBRATION = longArrayOf(0, 350)
    val REQUEST_ERROR_VIBRATION = longArrayOf(0, 250)
    val TIMER_END_VIBRATION = longArrayOf(0, 500, 500, 500, 500, 500)
    const val IMAGE_WIDTH = 256
    const val IMAGE_HEIGHT = 256
    const val SUCCESS_MSG = "Success"

    /** Enum with notification types */
    enum class NotificationType {
        INVITED_TO_TEAM,
        JOINED_TEAM,
        DECLINED_TEAM_INVITATION,
        WORKOUT_ASSIGNED,
        WORKOUT_ASSIGNMENT_COMPLETED
    }

    /** Object containing request end point values */
    object RequestEndPoints {
        private const val USERS = "users"

        const val USER_PROFILES = "user-profiles"
        const val WORKOUTS = "workouts"
        const val EXERCISES = "exercises"
        const val MUSCLE_GROUPS = "muscle-groups"
        const val WORKOUT_TEMPLATES = "workout-templates"
        const val TEAMS = "teams"
        const val NOTIFICATIONS = "notifications"

        const val REGISTER = "$USERS/register"
        const val LOGIN = "$USERS/login"
        const val LOGOUT = "$USERS/logout"
        const val CHANGE_PASSWORD = "$USERS/change-password"
        const val VALIDATE_TOKEN = "$USERS/validate-token"

        const val WEIGHT_UNITS  = "$WORKOUTS/weight-units"

        const val TO_WORKOUT = "$EXERCISES/to-workout"
        const val EXERCISE_FROM_WORKOUT = "$EXERCISES/exercise-from-workout"
        const val COMPLETE_SET = "$EXERCISES/complete-set"
        const val EXERCISES_FOR_MG = "$EXERCISES/by-mg-id"
        const val MG_EXERCISE = "$EXERCISES/mg-exercise"

        const val DEFAULT_VALUES = "$USER_PROFILES/default-values"

        const val LEAVE_TEAM  = "$TEAMS/leave"
        const val INVITE_MEMBER  = "$TEAMS/invite-member"
        const val REMOVE_MEMBER  = "$TEAMS/remove-member"
        const val ACCEPT_TEAM_INVITE  = "$TEAMS/accept-invite"
        const val DECLINE_TEAM_INVITE  = "$TEAMS/decline-invite"
        const val MY_TEAMS  = "$TEAMS/my-teams"
        const val MY_TEAMS_WITH_MEMBERS  = "$TEAMS/my-teams-with-members"
        const val USERS_TO_INVITE  = "$TEAMS/users-to-invite"
        const val MY_TEAM_MEMBERS  = "$TEAMS/my-team-members"
        const val JOINED_TEAM_MEMBERS  = "$TEAMS/joined-team-members"
        const val ASSIGN_WORKOUT  = "$TEAMS/assign-workout"

        const val JOIN_TEAM_NOTIFICATION_DETAILS  = "$NOTIFICATIONS/join-team-notification-details"
        const val REFRESH_NOTIFICATIONS = "$NOTIFICATIONS/refresh-notifications"

        const val GET_WORKOUT_TEMPLATE = "$WORKOUT_TEMPLATES/get-template-by-assigned-workout"

        const val FINISH_WORKOUT = "$WORKOUTS/finish"
    }
}