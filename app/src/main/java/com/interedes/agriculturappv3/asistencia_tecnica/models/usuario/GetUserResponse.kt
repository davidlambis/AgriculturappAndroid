package com.interedes.agriculturappv3.asistencia_tecnica.models.usuario

import com.google.gson.annotations.SerializedName


class GetUserResponse {

    @SerializedName("value")
    var value: List<UserResponse>? = null
}