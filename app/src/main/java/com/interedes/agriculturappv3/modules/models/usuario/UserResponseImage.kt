package com.interedes.agriculturappv3.modules.models.usuario

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.modules.models.rol.Rol
import com.raizlabs.android.dbflow.data.Blob
import java.util.*

data class UserResponseImage(

        @SerializedName("Id")
        var Id: UUID? = null,

        @SerializedName("Apellidos")
        var Apellidos: String? = null,

        @SerializedName("DetallemetodoPagoId")
        var DetalleMetodoPagoId: Long? = 0,

        @SerializedName("Email")
        var Email: String? = null,

        @SerializedName("EmailConfirmed")
        var EmailConfirmed: Boolean? = false,

        @SerializedName("Estado")
        var Estado: String? = null,

        @SerializedName("FechaRegistro")
        var FechaRegistro: String? = null,

        @SerializedName("Fotopefil")
        var Fotopefil: String? = null,

        @SerializedName("Identificacion")
        var Identificacion: String? = null,

        @SerializedName("Nombre")
        var Nombre: String? = null,

        @SerializedName("Nro_movil")
        var Nro_movil: String? = null,

        @SerializedName("NumeroCuenta")
        var NumeroCuenta: String? = null,

        @SerializedName("PhoneNumber")
        var PhoneNumber: String? = null,

        @SerializedName("PhoneNumberConfirmed")
        var PhoneNumberConfirmed: Boolean? = false,

        @SerializedName("RolId")
        var RolId: UUID? = null,

        @SerializedName("UserName")
        var UserName: String? = null,

        @SerializedName("Rol")
        var Rol: Rol?=null

) {
}