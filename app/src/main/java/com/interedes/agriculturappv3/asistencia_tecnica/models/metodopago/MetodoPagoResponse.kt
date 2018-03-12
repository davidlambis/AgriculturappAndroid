package com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago

import com.google.gson.annotations.SerializedName

class MetodoPagoResponse {

    @SerializedName("value")
    var value: MutableList<MetodoPago>? = null
}