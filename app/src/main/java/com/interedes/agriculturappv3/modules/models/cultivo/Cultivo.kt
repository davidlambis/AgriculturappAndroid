package com.interedes.agriculturappv3.modules.models.cultivo

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table.FechaIncio
import com.interedes.agriculturappv3.modules.models.detalletipoproducto.DetalleTipoProducto
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.produccion.Produccion
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

@Table(database = DataSource::class)
data class Cultivo(@PrimaryKey
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
                   var FechaFin: String ? = null,

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
                   var EstadoSincronizacion: Boolean? = false,

                   @Column(getterName = "getEstado_SincronizacionUpdate")
                   var Estado_SincronizacionUpdate: Boolean? = false,

                   @Column(name = "stringFechaInicio")
                   var stringFechaInicio: String? = null,

                   @Column(name = "stringFechaFin")
                   var stringFechaFin: String? = null,

                   @SerializedName("UnidadMedida")
                   var unidadMedida: Unidad_Medida?= null,

                   @SerializedName("Produccions")
                   var produccions:  ArrayList<Produccion>?= null,

                   @SerializedName("ControlPlagas")
                   var controlPlagas:  ArrayList<ControlPlaga>?= null,

                   @SerializedName("DetalleTipoProducto")
                   var detalleTipoProducto: DetalleTipoProducto?= null,

                   @SerializedName("Lote")
                   var Lote: Lote?=null

                   ) {

    override fun toString(): String {
        return String.format("%s - %s", Nombre, Nombre_Detalle_Tipo_Producto);
    }

    fun getNombreCultio(): String {
        return String.format("%s - %s", Nombre, Nombre_Detalle_Tipo_Producto);
    }


    fun getFechaDate(stringDate:String?):Date?{
        val dateString = stringDate
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        var convertedDate = Date()
        try {
            convertedDate = dateFormat.parse(dateString)
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return convertedDate
    }


    fun getFechaFormat(date:Date?): String {

        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return sdf.format(date)
        }catch (ex:Exception){
            // Log.println(ex.toString())

            return  ""
        }


    }






/*
    fun getFechaIncioFormat(): String {
        val sdf = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault())
        return sdf.format(FechaIncio)
    }

    fun getFechaFinFormat(): String {
        val sdf = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault())
        return sdf.format(FechaFin)
    } */

}