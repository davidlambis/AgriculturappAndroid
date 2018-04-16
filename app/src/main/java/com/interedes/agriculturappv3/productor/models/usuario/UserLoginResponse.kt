package com.interedes.agriculturappv3.productor.models.usuario

import com.google.gson.annotations.SerializedName
import java.util.*


class UserLoginResponse {

    @SerializedName("DetalleMetodopagoId")
    var detalleMetodopagoId: Long? = 0

    @SerializedName("Tipouser")
    var tipouser: UUID? = null

    @SerializedName("Apellido")
    var apellido: String? = null

    @SerializedName("Nombre")
    var nombre: String? = null

    @SerializedName("NroCuenta")
    var nroCuenta: String? = null

    @SerializedName("Identification")
    var identification: String? = null

    @SerializedName("Id")
    var id: UUID? = null

    @SerializedName("UserName")
    var userName: String? = null

    @SerializedName("NormalizedUserName")
    var normalizedUserName: String? = null

    @SerializedName("Email")
    var email: String? = null

    @SerializedName("NormalizedEmail")
    var normalizedEmail: String? = null

    @SerializedName("EmailConfirmed")
    var emailConfirmed: Boolean? = false

    @SerializedName("PhoneNumber")
    var phoneNumber: String? = null

    @SerializedName("PhoneNumberConfirmed")
    var phoneNumberConfirmed: Boolean? = false
}