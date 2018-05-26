package com.interedes.agriculturappv3.modules.models.producto

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.modules.models.detalletipoproducto.DetalleTipoProducto

/**
 * Created by EnuarMunoz on 19/05/18.
 */
class GetProductosByTipoResponse {

    ///@SerializedName("value")
    //var value: MutableList<DetalleTipoProducto>? = null


    @SerializedName("value")
    var value: MutableList<ViewProducto>? = null
}