package com.interedes.agriculturappv3.productor.models.producto

import com.google.gson.annotations.SerializedName


class CalidadProductoResponse {

    @SerializedName("value")
    var value: MutableList<CalidadProducto>? = null

}