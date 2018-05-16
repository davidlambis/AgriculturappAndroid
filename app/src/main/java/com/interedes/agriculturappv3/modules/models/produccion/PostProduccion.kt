package com.interedes.agriculturappv3.modules.models.produccion

import com.google.gson.annotations.SerializedName
import com.raizlabs.android.dbflow.annotation.Column
import java.util.*


data class PostProduccion(

        @SerializedName("Id")
        @Column(name = "Id")
        var Id: Long? = 0,

        @SerializedName("CultivoId")
        @Column(name = "CultivoId")
        var CultivoId: Long? = null,
        @SerializedName("FechaFin")
        @Column(name = "FechaFin")
        var FechaFin: String? = null,
        @SerializedName("FechaInicio")
        @Column(name = "FechaInicio")
        var FechaInicio: String? = null,
        @SerializedName("unidadMedidaId")
        @Column(name = "unidadMedidaId")
        var unidadMedidaId: Long? = null,

        @SerializedName("produccionEstimada")
        @Column(name = "produccionEstimada")
        var produccionEstimada: Double? = null
) {}
