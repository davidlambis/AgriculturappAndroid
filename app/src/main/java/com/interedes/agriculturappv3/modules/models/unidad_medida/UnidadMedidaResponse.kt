package com.interedes.agriculturappv3.modules.models.unidad_medida

import com.google.gson.annotations.SerializedName

class UnidadMedidaResponse {

    @SerializedName("value")
    var value: MutableList<Unidad_Medida>? = null
}