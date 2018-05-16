package com.interedes.agriculturappv3.modules.models.unidad_medida

import com.google.gson.annotations.SerializedName

class CategoriaMedidaResponse {

    @SerializedName("value")
    var value: MutableList<CategoriaMedida>? = null
}