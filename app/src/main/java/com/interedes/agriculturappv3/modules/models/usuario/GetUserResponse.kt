package com.interedes.agriculturappv3.modules.models.usuario

import com.google.gson.annotations.SerializedName


class GetUserResponse {

    @SerializedName("value")
    var value: MutableList<UserLoginResponse>? = null
}