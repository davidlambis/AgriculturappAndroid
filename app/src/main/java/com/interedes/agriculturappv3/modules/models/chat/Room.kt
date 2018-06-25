package com.interedes.agriculturappv3.modules.models.chat

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*


data class Room(


        @SerializedName("IdRoom")
        var IdRoom: String? = null,

        @SerializedName("User_From")
        var User_From: String? = null,

        @SerializedName("User_To")
        var User_To: String? = null,

        @SerializedName("Date")
        var Date: Long? = null,

        @SerializedName("Date_Last")
        var Date_Last: Long? = null,

        @SerializedName("DateString")
        var DateString: String? = null,

        @SerializedName("LastMessage")
        var LastMessage: String? = null

        ) {

        fun getDateFormatApi(date: Date): String? {
                val format1 = SimpleDateFormat("yyyy-MM-dd")
                return format1.format(date)
        }

}