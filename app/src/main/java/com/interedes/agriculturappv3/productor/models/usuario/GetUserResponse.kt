package com.interedes.agriculturappv3.productor.models.usuario

import com.google.gson.annotations.SerializedName


class GetUserResponse {

    @SerializedName("value")
    var value: List<UserResponse>? = null
}