package com.interedes.agriculturappv3.asistencia_tecnica.models

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

@Table(database = DataSource::class)
data class Cultivo(@PrimaryKey(autoincrement = true)
                   @SerializedName("Id")
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

                   @Column(name = "Unidad_Medida_Id")
                   var Unidad_Medida_Id: Long? = null,

                   @Column(name = "Nombre_Unidad_Medida")
                   var Nombre_Unidad_Medida: String? = null,

                   @Column(name = "Nombre_Detalle_Tipo_Producto")
                   var Nombre_Detalle_Tipo_Producto: String? = null) {

    override fun toString(): String {
        return Nombre!!
    }
}