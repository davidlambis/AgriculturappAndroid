package com.interedes.agriculturappv3.modules.models.usuario

import com.google.gson.annotations.SerializedName

data class RequestCredentials(
        @SerializedName("oldPassword")
        var oldPassword: String? = null,

        @SerializedName("newPassword")
        var newPassword: String? = null
) {
}