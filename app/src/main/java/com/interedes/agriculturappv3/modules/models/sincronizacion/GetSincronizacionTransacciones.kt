package com.interedes.agriculturappv3.modules.models.sincronizacion

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.modules.models.ventas.Transaccion

/**
 * Created by EnuarMunoz on 17/05/18.
 */
class GetSincronizacionTransacciones {
    @SerializedName("value")
    var value: MutableList<Transaccion>? = null
}