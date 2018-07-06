package com.interedes.agriculturappv3.modules.models.Notification

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.util.*

@Table(database = DataSource::class)
data class NotificationLocal(
        @PrimaryKey
        @Column(name = "Id")
        var Id: Long? = 0,

        @SerializedName("title")
        @Column(name = "title")
        var title: String? = null,

        @SerializedName("message")
        @Column(name = "message")
        var message: String? = null,

        @SerializedName("image_url")
        @Column(name = "image_url")
        var image_url: String? = null,

        @SerializedName("user_name")
        @Column(name = "user_name")
        var user_name: String? = null,

        @SerializedName("ui")
        @Column(name = "ui")
        var ui: String? = null,

        @SerializedName("fcm_token")
        @Column(name = "fcm_token")
        var fcm_token: String? = null,

        @SerializedName("room_id")
        @Column(name = "room_id")
        var room_id: String? = null,

        @SerializedName("type_notification")
        @Column(name = "type_notification")
        var type_notification: String? = null,

        @Column(getterName = "getReadNotification")
        var ReadNotification: Boolean = false,

        @Column(name = "userLoguedId")
        var userLoguedId: UUID? = null

        ) {
}