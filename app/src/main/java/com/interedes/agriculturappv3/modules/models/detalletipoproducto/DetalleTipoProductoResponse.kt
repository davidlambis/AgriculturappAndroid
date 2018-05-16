package com.interedes.agriculturappv3.modules.models.detalletipoproducto

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto


class DetalleTipoProductoResponse {

    @SerializedName("value")
    var value: MutableList<DetalleTipoProducto>? = null
}