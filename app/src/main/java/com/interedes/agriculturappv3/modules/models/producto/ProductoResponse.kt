package com.interedes.agriculturappv3.modules.models.producto

import com.google.gson.annotations.SerializedName

data class ProductoResponse(
        @SerializedName("value")
        var value: MutableList<Producto>? = null) {

}