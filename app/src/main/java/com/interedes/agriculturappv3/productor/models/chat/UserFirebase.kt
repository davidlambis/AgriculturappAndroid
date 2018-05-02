package com.interedes.agriculturappv3.productor.models.chat

import com.google.gson.annotations.SerializedName
import java.util.*

data class UserFirebase(
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

        @SerializedName("Imagen")
        var Imagen: String? = null
) {
}