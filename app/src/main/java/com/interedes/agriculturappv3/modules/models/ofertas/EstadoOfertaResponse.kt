package com.interedes.agriculturappv3.modules.models.ofertas

import com.google.gson.annotations.SerializedName

class EstadoOfertaResponse {
    @SerializedName("value")
    var value: MutableList<EstadoOferta>? = null
}