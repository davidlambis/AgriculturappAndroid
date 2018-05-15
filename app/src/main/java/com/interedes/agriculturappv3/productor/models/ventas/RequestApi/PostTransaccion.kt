package com.interedes.agriculturappv3.productor.models.ventas.RequestApi

import com.google.gson.annotations.SerializedName
import java.util.*

data class PostTransaccion(

        @SerializedName("Id")
        var Id: Long? = 0,

        @SerializedName("Concepto")
        var Concepto: String? = null,

        @SerializedName("EstadoId")
        var EstadoId: Long? = 0,

        @SerializedName("Fecha")
        var Fecha_Transaccion: String? = null,

        @SerializedName("NaturalezaId")
        var NaturalezaId: Long? = 0,

        @SerializedName("PUCId")
        var PUCId: Long? = 0,

        @SerializedName("TerceroId")
        var TerceroId: Long? = 0,


        @SerializedName("Valor")
        var Valor: Double? = 0.0,


        @SerializedName("Cantidad")
        var Cantidad: Double? = 0.0,


        @SerializedName("CultivoId")
        var CultivoId: Long? = 0,

        @SerializedName("userId")
        var userId: UUID? = null


) {

}