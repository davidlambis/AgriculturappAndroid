package com.interedes.agriculturappv3.asistencia_tecnica.models.login

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Table

class LoginResponse {

    @SerializedName("access_token")
    var access_token: String? = null
}