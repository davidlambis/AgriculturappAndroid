package com.interedes.agriculturappv3.modules.models.chat

import com.google.gson.annotations.SerializedName


data class Conversation(
        @SerializedName("IdRoom")
        var IdRoom: String? = null,

        @SerializedName("User_From")
        var User_From: String? = null,

        @SerializedName("User_To")
        var User_To: String? = null
        ) {
}