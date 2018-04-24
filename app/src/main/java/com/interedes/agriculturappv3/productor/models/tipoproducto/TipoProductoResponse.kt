package com.interedes.agriculturappv3.productor.models.tipoproducto

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.productor.models.rol.Rol

class TipoProductoResponse {

    @SerializedName("value")
    var value: MutableList<TipoProducto>? = null
}