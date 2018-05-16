package com.interedes.agriculturappv3.modules.models.producto

import com.google.gson.annotations.SerializedName


class CalidadProductoResponse {

    @SerializedName("value")
    var value: MutableList<CalidadProducto>? = null

}