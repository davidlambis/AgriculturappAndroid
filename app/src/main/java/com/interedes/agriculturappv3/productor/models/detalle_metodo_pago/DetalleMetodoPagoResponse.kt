package com.interedes.agriculturappv3.productor.models.detalle_metodo_pago

import com.google.gson.annotations.SerializedName


class DetalleMetodoPagoResponse {

    @SerializedName("value")
    var value: MutableList<DetalleMetodoPago>? = null
}