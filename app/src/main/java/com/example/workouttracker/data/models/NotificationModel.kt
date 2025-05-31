package com.example.workouttracker.data.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.util.Date

/** NotificationModel class representing a notification.
 *  Must correspond with server-side NotificationModel
 */
class NotificationModel: BaseModel {

    @SerializedName("NotificationText")
    val notificationText: String

    @SerializedName("DateTime")
    val dateTime: Date

    @SerializedName("IsActive")
    val isActive: Boolean

    @SerializedName("Type")
    val type: String

    @SerializedName("Image")
    val image: String

    @SerializedName("TeamId")
    val teamId: Long?

    @SerializedName("ClickDisabled")
    val clickDisabled: Boolean

    /** Constructor to deserialized NotificationModel object
     * @param data serialized NotificationModel object
     */
    constructor(data: String) : super(data) {
        val gson = Gson()
        val model: NotificationModel = gson.fromJson(data, NotificationModel::class.java)

        notificationText = model.notificationText
        dateTime = model.dateTime
        isActive = model.isActive
        type = model.type
        image = model.image
        teamId = model.teamId
        clickDisabled = model.clickDisabled
    }

    constructor(idVal: Long, notificationTextVal: String, dateTimeVal: Date, isActiveVal: Boolean, typeVal: String,
                imageVal: String, teamIdVal: Long, clickDisabledVal: Boolean) : super(idVal) {
        notificationText = notificationTextVal
        dateTime = dateTimeVal
        isActive = isActiveVal
        type = typeVal
        image = imageVal
        teamId = teamIdVal
        clickDisabled = clickDisabledVal
    }
}