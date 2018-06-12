package com.interedes.agriculturappv3.modules.models.ofertas

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/**
 * Created by EnuarMunoz on 11/06/18.
 */
data class PostDetalleOferta(

        @SerializedName("Id")
        var Id: Long? = 0,

        @SerializedName("CalidadId")
        var CalidadId: Long? = 0,

        @SerializedName("Cantidad")
        var Cantidad: Double? = 0.0,

        @SerializedName("OfertasId")
        var OfertasId: Long? = 0,

        @SerializedName("ProductoId")
        var ProductoId: Long? = 0,

        @SerializedName("UnidadMedidaId")
        var UnidadMedidaId: Long? = 0,

        @SerializedName("Valor_Oferta")
        var Valor_Oferta: BigDecimal? = null,

        @SerializedName("Valor_minimo")
        var Valor_minimo: BigDecimal? = null,

        @SerializedName("Valor_transaccion")
        var Valor_transaccion: BigDecimal? =null

    ) {


    }