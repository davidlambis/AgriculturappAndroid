package com.interedes.agriculturappv3.modules.models.ventas.RequestApi

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.modules.models.ventas.CategoriaPuk

class CategoriaPucResponse {
    @SerializedName("value")
    var value: MutableList<CategoriaPuk>? = null
}