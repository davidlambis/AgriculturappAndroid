package com.interedes.agriculturappv3.modules.models.Notification

import android.os.Parcel
import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.interedes.agriculturappv3.modules.models.parcelable.KParcelable
import com.interedes.agriculturappv3.modules.models.parcelable.parcelableCreator
import com.interedes.agriculturappv3.modules.models.parcelable.readBoolean
import com.interedes.agriculturappv3.modules.models.parcelable.writeBoolean
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
        var userLoguedId: UUID? = null,

        @Column(name = "parameter")
        var parameter: String? = null,

        @Column(name = "time")
        var time: Long = 0

        ): KParcelable {

        private constructor(p: Parcel) : this(
                Id = p.readLong(),
                title = p.readString(),
                message = p.readString(),
                image_url = p.readString(),
                user_name = p.readString(),
                ui = p.readString(),
                fcm_token = p.readString(),
                room_id = p.readString(),
                type_notification = p.readString(),
                ReadNotification = p.readBoolean(),
                parameter = p.readString(),
                time = p.readLong()

        )


        override fun writeToParcel(dest: Parcel, flags: Int) {
                dest.writeLong(Id!!)
                dest.writeString(title)
                dest.writeString(message)
                dest.writeString(image_url)
                dest.writeString(user_name)
                dest.writeString(ui)
                dest.writeString(fcm_token)
                dest.writeString(room_id)
                dest.writeString(type_notification)
                dest.writeBoolean(ReadNotification)
                dest.writeString(parameter)
                dest.writeLong(time!!)
        }

        companion object {
                @JvmField val CREATOR = parcelableCreator(::NotificationLocal)
        }
}