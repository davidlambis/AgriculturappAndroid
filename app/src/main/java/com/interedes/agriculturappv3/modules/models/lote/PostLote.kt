package com.interedes.agriculturappv3.modules.models.lote

import com.google.gson.annotations.SerializedName
import com.raizlabs.android.dbflow.annotation.Column
import java.math.BigDecimal
import java.util.*


data class PostLote(@SerializedName("Id")
                    var Id: Long? = 0,

                    @SerializedName("Area")
                    var Area: BigDecimal? = null,

                    @SerializedName("Codigo")
                    var Codigo: String? = null,

                    @SerializedName("Nombre")
                    var Nombre: String? = null,

                    @SerializedName("Descripcion")
                    var Descripcion: String? = null,

                    @SerializedName("Localizacion")
                    var Localizacion: String? = null,

                    @SerializedName("Localizacion_Poligono")
                    var Localizacion_Poligono: String? = null,

                    @SerializedName("UnidadMedidaId")
                    var Unidad_Medida_Id: Long? = null,

                    @SerializedName("unidadproductivaId")
                    var Unidad_Productiva_Id: Long? = null) {

}

