package com.interedes.agriculturappv3.modules.models.produccion

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by usuario on 20/03/2018.
 */
@Table(database = DataSource::class)
data class Produccion(@PrimaryKey
                      @Column(name = "ProduccionId")
                      var ProduccionId: Long? = 0,

                      @SerializedName("CultivoId")
                      @Column(name = "CultivoId")
                      var CultivoId: Long? = null,


                      @Column(name = "FechaInicio")
                      var FechaInicioProduccion: Date? = null,
                      

                      @Column(name = "FechaFin")
                      var FechaFinProduccion: Date? = null,

                      @SerializedName("Descripcion")
                      @Column(name = "Descripcion")
                      var Descripcion: String? = null,

                      @SerializedName("produccionEstimada")
                      @Column(name = "ProduccionReal")
                      var ProduccionReal: Double? = null,

                      @SerializedName("unidadMedidaId")
                      @Column(name = "UnidadMedidaId")
                      var UnidadMedidaId: Long? = null,

                      @Column(name = "NombreUnidadMedida")
                      var NombreUnidadMedida: String? = null,

                      @Column(name = "NombreUnidadProductiva")
                      var NombreUnidadProductiva: String? = null,

                      @Column(name = "NombreLote")
                      var NombreLote: String? = null,

                      @Column(name = "NombreCultivo")
                      var NombreCultivo: String? = null,

                      @SerializedName("FechaInicio")
                      var StringFechaInicio: String? = null,

                      @SerializedName("FechaFin")
                      var StringFechaFin: String? = null,


                      @SerializedName("Id")
                      @Column(name = "Id_Remote")
                      var Id_Remote: Long? = 0,



                      @Column(getterName = "getEstado_Sincronizacion")
                      var Estado_Sincronizacion: Boolean? = false,

                      @Column(getterName = "getEstado_SincronizacionUpdate")
                      var Estado_SincronizacionUpdate: Boolean? = false,

                      @SerializedName("UnidadMedida")
                      var unidadMedida: Unidad_Medida?= null
) {

    fun getFechaInicioFormat(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(FechaInicioProduccion)
    }

    fun getFechafinFormat(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(FechaFinProduccion)
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





}
