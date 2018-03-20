package com.interedes.agriculturappv3.asistencia_tecnica.models.usuario

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.asistencia_tecnica.models.rol.Rol
import java.util.*

class UserResponse {

    @SerializedName("detalleMetodopagoId")
    var detalleMetodopagoId: Long? = 0

    @SerializedName("tipouser")
    var tipouser: UUID? = null

    @SerializedName("apellido")
    var apellido: String? = null

    @SerializedName("nombre")
    var nombre: String? = null

    @SerializedName("nroCuenta")
    var nroCuenta: String? = null

    @SerializedName("identification")
    var identification: String? = null

    @SerializedName("id")
    var id: UUID? = null

    @SerializedName("userName")
    var userName: String? = null

    @SerializedName("normalizedUserName")
    var normalizedUserName: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("normalizedEmail")
    var normalizedEmail: String? = null

    @SerializedName("emailConfirmed")
    var emailConfirmed: Boolean? = false

    @SerializedName("phoneNumber")
    var phoneNumber: String? = null

    @SerializedName("phoneNumberConfirmed")
    var phoneNumberConfirmed: Boolean? = false
}