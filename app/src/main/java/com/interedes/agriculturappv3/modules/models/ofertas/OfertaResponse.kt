package com.interedes.agriculturappv3.modules.models.ofertas

import com.google.gson.annotations.SerializedName

class OfertaResponse {
    @SerializedName("value")
    var value: MutableList<Oferta>? = null
}