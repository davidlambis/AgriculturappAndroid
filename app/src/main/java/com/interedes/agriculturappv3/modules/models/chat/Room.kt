package com.interedes.agriculturappv3.modules.models.chat

import android.os.Parcel
import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.interedes.agriculturappv3.modules.models.parcelable.KParcelable
import com.interedes.agriculturappv3.modules.models.parcelable.parcelableCreator
import com.interedes.agriculturappv3.modules.models.parcelable.readDate
import com.interedes.agriculturappv3.modules.models.parcelable.writeDate
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.text.SimpleDateFormat
import java.util.*

@Table(database = DataSource::class)
data class Room(
        @PrimaryKey()
        @SerializedName("IdRoom")
        var IdRoom: String? = null,

        @SerializedName("User_From")
        @Column(name = "User_From")
        var User_From: String? = null,

        @SerializedName("User_To")
        @Column(name = "User_To")
        var User_To: String? = null,

        @SerializedName("Date")
        @Column(name = "Date")
        var Date: Long? = null,

        @SerializedName("Date_Last")
        @Column(name = "Date_Last")
        var Date_Last: Long? = null,

        @SerializedName("DateString")
        @Column(name = "DateString")
        var DateString: String? = null,

        @SerializedName("LastMessage")
        @Column(name = "LastMessage")
        var LastMessage: String? = null

        ):KParcelable {

        fun getDateFormatApi(date: Date): String? {
                val format1 = SimpleDateFormat("yyyy-MM-dd")
                return format1.format(date)
        }


        private constructor(p: Parcel) : this(
                IdRoom = p.readString(),
                User_From = p.readString(),
                User_To = p.readString(),
                Date = p.readLong(),
                Date_Last = p.readLong(),
                DateString = p.readString(),
                LastMessage = p.readString())

        override fun writeToParcel(dest: Parcel, flags: Int) {
                dest.writeString(IdRoom!!)
                dest.writeString(User_From)
                dest.writeString(User_To!!)
                dest.writeLong(Date!!)
                dest.writeLong(Date_Last!!)
                dest.writeString(DateString!!)
                dest.writeString(LastMessage!!)
        }

        companion object {
                @JvmField val CREATOR = parcelableCreator(::Room)
        }

}