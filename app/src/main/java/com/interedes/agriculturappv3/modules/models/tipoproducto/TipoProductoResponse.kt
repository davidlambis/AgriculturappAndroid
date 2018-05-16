package com.interedes.agriculturappv3.modules.models.tipoproducto

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.modules.models.rol.Rol

class TipoProductoResponse {

    @SerializedName("value")
    var value: MutableList<TipoProducto>? = null
}