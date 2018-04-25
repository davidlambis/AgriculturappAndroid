package com.interedes.agriculturappv3.productor.models.cultivo

import com.google.gson.annotations.SerializedName
import com.raizlabs.android.dbflow.annotation.Column
import java.util.*


data class PostCultivo(@SerializedName("Id")
                       @Column(name = "Id")
                       var Id: Long? = 0,

                       @SerializedName("Descripcion")
                       @Column(name = "Descripcion")
                       var Descripcion: String? = null,

                       @SerializedName("DetalleTipoProductoId")
                       @Column(name = "DetalleTipoProductoId")
                       var DetalleTipoProductoId: Long? = 0,

                       @SerializedName("EstimadoCosecha")
                       @Column(name = "EstimadoCosecha")
                       var EstimadoCosecha: Double? = 0.0,

                       @SerializedName("FechaFin")
                       @Column(name = "FechaFin")
                       var FechaFin: String? = null,

                       @SerializedName("FechaIncio")
                       @Column(name = "FechaIncio")
                       var FechaIncio: String? = null,

                       @SerializedName("LoteId")
                       @Column(name = "LoteId")
                       var LoteId: Long? = 0,

                       @SerializedName("Nombre")
                       @Column(name = "Nombre")
                       var Nombre: String? = null,

                       @SerializedName("siembraTotal")
                       @Column(name = "siembraTotal")
                       var siembraTotal: Long? = 0) {}