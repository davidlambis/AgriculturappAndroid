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
        var PucId: Long? = 0,

        @SerializedName("TerceroId")
        var TerceroId: Long? = 0,


        @SerializedName("Valor")
        var Valor_Total: Double? = 0.0,


        @SerializedName("Cantidad")
        var Cantidad: Double? = 0.0,


        @SerializedName("Cultivo_Id")
        var Cultivo_Id: Long? = 0,

        @SerializedName("userId")
        var userId: UUID? = null


) {

}