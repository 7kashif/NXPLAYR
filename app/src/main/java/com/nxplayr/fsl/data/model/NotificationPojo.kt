package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class NotificationPojo(
    @SerializedName("data")
        var `data`: List<NotificationData> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
) : Serializable

data class NotificationData(
        @SerializedName("notificationID")
        var notificationID: String = "",
        @SerializedName("notificationMessageText")
        var notificationMessageText: String = "",
        @SerializedName("notificationReadStatus")
        var notificationReadStatus: String = "",
        @SerializedName("notificationReferenceKey")
        var notificationReferenceKey: String = "",
        @SerializedName("notificationSendDate")
        var notificationSendDate: String = "",
        @SerializedName("notificationSendTime")
        var notificationSendTime: String = "",
        @SerializedName("notificationTitle")
        var notificationTitle: String = "",
        @SerializedName("notificationType")
        var notificationType: String = ""
) : Serializable