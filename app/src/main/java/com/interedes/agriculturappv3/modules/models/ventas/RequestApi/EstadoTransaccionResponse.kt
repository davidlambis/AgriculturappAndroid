package com.interedes.agriculturappv3.modules.models.ventas.RequestApi

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.modules.models.ventas.Estado_Transaccion

class EstadoTransaccionResponse {
    @SerializedName("value")
    var value: MutableList<Estado_Transaccion>? = null
}