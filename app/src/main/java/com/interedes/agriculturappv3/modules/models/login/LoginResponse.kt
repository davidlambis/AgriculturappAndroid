package com.interedes.agriculturappv3.modules.models.login

import com.google.gson.annotations.SerializedName

class LoginResponse {
    @SerializedName("access_token")
    var access_token: String? = null
}