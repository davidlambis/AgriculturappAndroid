package com.interedes.agriculturappv3.productor.models.cultivo

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.text.SimpleDateFormat
import java.util.*

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
                   var FechaFin: Date? = null,

                   @SerializedName("FechaIncio")
                   @Column(name = "FechaIncio")
                   var FechaIncio: Date? = null,

                   @SerializedName("LoteId")
                   @Column(name = "LoteId")
                   var LoteId: Long? = 0,

                   @SerializedName("Nombre")
                   @Column(name = "Nombre")
                   var Nombre: String? = null,

                   @SerializedName("siembraTotal")
                   @Column(name = "siembraTotal")
                   var siembraTotal: Long? = 0,

                   @Column(name = "Unidad_Medida_Id")
                   var Unidad_Medida_Id: Long? = null,

                   @Column(name = "Nombre_Unidad_Medida")
                   var Nombre_Unidad_Medida: String? = null,

                   @Column(name = "NombreUnidadProductiva")
                   var NombreUnidadProductiva: String? = null,

                   @Column(name = "NombreLote")
                   var NombreLote: String? = null,

                   @Column(name = "Nombre_Tipo_Producto")
                   var Nombre_Tipo_Producto: String? = null,

                   @Column(name = "Id_Tipo_Producto")
                   var Id_Tipo_Producto: Long? = null,

                   @Column(name = "Nombre_Detalle_Tipo_Producto")
                   var Nombre_Detalle_Tipo_Producto: String? = null,

                   @Column(getterName = "getEstadoSincronizacion")
                   var EstadoSincronizacion: Boolean? = false) {

    override fun toString(): String {
        return String.format("%s - %s", Nombre, Nombre_Detalle_Tipo_Producto);
    }

    fun getNombreCultio(): String {
        return String.format("%s - %s", Nombre, Nombre_Detalle_Tipo_Producto);
    }


    fun getFechaIncioFormat(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(FechaIncio)
    }

    fun getFechaFinFormat(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(FechaFin)
    }

}