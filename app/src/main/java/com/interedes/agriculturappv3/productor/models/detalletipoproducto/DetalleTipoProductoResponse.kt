package com.interedes.agriculturappv3.productor.models.detalletipoproducto

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.productor.models.tipoproducto.TipoProducto


class DetalleTipoProductoResponse {

    @SerializedName("value")
    var value: MutableList<DetalleTipoProducto>? = null
}