package com.interedes.agriculturappv3.asistencia_tecnica.models.produccion

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.sql.Blob
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by usuario on 20/03/2018.
 */


@Table(database = DataSource::class)
data class Produccion(@PrimaryKey(autoincrement = true)
                      @SerializedName("Id")
                      @Column(name = "Id")
                      var Id: Long? = 0,

                      @SerializedName("CultivoId")
                      @Column(name = "CultivoId")
                      var CultivoId: Long? = null,

                      @SerializedName("FechaInicio")
                      @Column(name = "FechaInicio")
                      var FechaInicio: Date? = null,

                      @SerializedName("FechaFin")
                      @Column(name = "FechaFin")
                      var FechaFin: Date? = null,

                      @SerializedName("Descripcion")
                      @Column(name = "Descripcion")
                      var Descripcion: String? = null,

                      @SerializedName("produccionEstimada")
                      @Column(name = "ProduccionReal")
                      var ProduccionReal: Double? = null,

                      @SerializedName("UnidadMedidaId")
                      @Column(name = "UnidadMedidaId")
                      var UnidadMedidaId: Long? = null,

                      @Column(name = "NombreUnidadMedida")
                      var NombreUnidadMedida: String? = null
) {

    override fun toString(): String {
        return Descripcion!!
    }




    fun getFechaInicioFormat(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(FechaInicio)
    }

    fun getFechafinFormat(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(FechaFin)
    }


}
