package com.interedes.agriculturappv3.modules.models.ventas.RequestApi

import android.renderscript.Double4
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
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
        var Valor: BigDecimal? = null,


        @SerializedName("Cantidad")
        var Cantidad: BigDecimal? = null,


        @SerializedName("CultivoId")
        var CultivoId: Long? = 0,

        @SerializedName("userId")
        var userId: UUID? = null


) {

}