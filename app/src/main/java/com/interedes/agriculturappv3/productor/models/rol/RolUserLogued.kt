package com.interedes.agriculturappv3.productor.models.rol

import com.google.gson.annotations.SerializedName
import java.util.*

data class RolUserLogued(

        @SerializedName("Id")
        var Id: UUID? = null,

        @SerializedName("Nombre")
        var Nombre: String? = null,

        @SerializedName("Icono")
        var Icono: String? = null

) {
}