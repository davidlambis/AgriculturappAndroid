package com.interedes.agriculturappv3.modules.models.sincronizacion

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.ventas.Transaccion

/**
 * Created by EnuarMunoz on 19/05/18.
 */
class GetSynProductosUserResponse {
    @SerializedName("value")
    var value: MutableList<Producto>? = null
}