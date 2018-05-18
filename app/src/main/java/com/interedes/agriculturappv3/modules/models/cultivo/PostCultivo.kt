package com.interedes.agriculturappv3.modules.models.cultivo

import com.google.gson.annotations.SerializedName
import com.raizlabs.android.dbflow.annotation.Column
import java.util.*


data class PostCultivo(@SerializedName("Id")

                       var Id: Long? = 0,

                       @SerializedName("Descripcion")
                       var Descripcion: String? = null,

                       @SerializedName("DetalleTipoProductoId")
                       var DetalleTipoProductoId: Long? = 0,

                       @SerializedName("UnidadMedidaId")
                       var UnidadMedidaId: Long? = 0,

                       @SerializedName("EstimadoCosecha")
                       var EstimadoCosecha: Double? = 0.0,

                       @SerializedName("FechaFin")
                       var FechaFin: String? = null,

                       @SerializedName("FechaIncio")
                       var FechaIncio: String? = null,

                       @SerializedName("LoteId")
                       var LoteId: Long? = 0,

                       @SerializedName("Nombre")
                       var Nombre: String? = null,

                       @SerializedName("siembraTotal")
                       var siembraTotal: Long? = 0) {}