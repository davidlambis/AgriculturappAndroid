package com.interedes.agriculturappv3.asistencia_tecnica.models.login

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

data class Login(@SerializedName("username")
                 var username: String? = null,
                 @SerializedName("password")
                 var password: String? = null) {
}