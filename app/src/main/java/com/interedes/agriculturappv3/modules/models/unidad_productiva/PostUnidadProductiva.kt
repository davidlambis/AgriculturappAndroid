package com.interedes.agriculturappv3.modules.models.unidad_productiva

import com.google.gson.annotations.SerializedName
import com.raizlabs.android.dbflow.annotation.Column
import java.math.BigDecimal
import java.util.*


data class PostUnidadProductiva(@SerializedName("Id")
                                var Id: Long? = 0,

                                @SerializedName("Area")
                                var Area: BigDecimal? = null,

                                @SerializedName("CiudadId")
                                var CiudadId: Long? = 0,

                                @SerializedName("Codigo")
                                var Codigo: String? = null,

                                @SerializedName("UnidadMedidaId")
                                var UnidadMedidaId: Long? = null,

                                @SerializedName("UsuarioId")
                                var UsuarioId: UUID? = null,

                                @SerializedName("descripcion")
                                var descripcion: String? = null,

                                @SerializedName("nombre")
                                var nombre: String? = null) {

}