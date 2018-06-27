package com.interedes.agriculturappv3.modules.models.chat

import com.google.gson.annotations.SerializedName
import java.util.*

open  class UserFirebase(
        @SerializedName("User_Id")
        var User_Id: String? = null,

        @SerializedName("Nombre")
        var Nombre: String? = null,

        @SerializedName("Apellido")
        var Apellido: String? = null,

        @SerializedName("Cedula")
        var Cedula: String? = null,

        @SerializedName("Correo")
        var Correo: String? = null,

        @SerializedName("Rol")
        var Rol: String? = null,

        @SerializedName("Telefono")
        var Telefono: String? = null,

        @SerializedName("Status")
        var Status: String? = null,

        @SerializedName("Last_Online")
        var Last_Online: Long? = null,

        @SerializedName("Token_Account")
        var Token_Account: String? = null,

        @SerializedName("Id_Account_Remote")
        var Id_Account_Remote: String? = null,

        @SerializedName("Imagen")
        var Imagen: String? = null,

        @SerializedName("StatusTokenFcm")
        var StatusTokenFcm: Boolean? = null,


        @SerializedName("TokenFcm")
        var TokenFcm: String? = null



) {


}