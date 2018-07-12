package com.interedes.agriculturappv3.modules.models.control_plaga

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class PostControlPlaga(
        @SerializedName("Id")
        var Id: Long = 0,

        @SerializedName("CultivoId")
        var CultivoId: Long? = 0,

        @SerializedName("Dosis")
        var Dosis: Double? = null,

        @SerializedName("EnfermedadesId")
        var EnfermedadesId: Long? = 0,

        @SerializedName("Fecha_aplicacion")
        var Fecha_aplicacion: String? = null,

        @SerializedName("TratamientoId")
        var TratamientoId: Long? = 0,

        @SerializedName("UnidadMedidaId")
        var UnidadMedidaId: Long? = 0,


        @SerializedName("FechaErradicacion")
        var FechaErradicacion: String? = null,

        @SerializedName("estadoRadicacion")
        var estadoRadicacion: Boolean? = false

) {



}