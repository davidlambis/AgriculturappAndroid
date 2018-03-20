package com.interedes.agriculturappv3.asistencia_tecnica.models.usuario

import com.google.gson.annotations.SerializedName
import java.util.*

data class User(
        @SerializedName("DetalleMetodopagoId")
        var DetalleMetodopagoId: Long? = 0,

        @SerializedName("NroCuenta")
        var NroCuenta: String? = null,

        @SerializedName("Email")
        var Email: String? = null,

        @SerializedName("Password")
        var Password: String? = null,

        @SerializedName("ConfirmPassword")
        var ConfirmPassword: String? = null,

        @SerializedName("Tipouser")
        var Tipouser: UUID? = null,

        @SerializedName("PhoneNumber")
        var PhoneNumber: String? = null,

        @SerializedName("Nombre")
        var Nombre: String? = null,

        @SerializedName("Apellido")
        var Apellido: String? = null,

        @SerializedName("Identification")
        var Identification: String? = null) {}