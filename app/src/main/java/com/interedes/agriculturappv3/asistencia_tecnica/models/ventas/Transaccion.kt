package com.interedes.agriculturappv3.asistencia_tecnica.models.ventas

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.text.SimpleDateFormat
import java.util.*


@Table(database = DataSource::class)
data class Transaccion(@PrimaryKey(autoincrement = true)
                       @SerializedName("Id")
                       @Column(name = "Id")
                       var Id: Long? = 0,

                       @SerializedName("Concepto")
                       @Column(name = "Concepto")
                       var Concepto: String? = null,

                       @SerializedName("EstadoId")
                       @Column(name = "EstadoId")
                       var EstadoId: Long? = 0,

                       @SerializedName("Fecha")
                       @Column(name = "Fecha_Transaccion")
                       var Fecha_Transaccion: Date? = null,

                       @Column(name = "updated_at")
                       var updatedAt: Calendar = Calendar.getInstance(),

                       @SerializedName("NaturalezaId")
                       @Column(name = "NaturalezaId")
                       var NaturalezaId: Long? = 0,

                       @SerializedName("PucId")
                       @Column(name = "PucId")
                       var PucId: Long? = 0,

                       @SerializedName("TerceroId")
                       @Column(name = "TerceroId")
                       var TerceroId: Long? = 0,


                       @Column(name = "Nombre_Tercero")
                       var Nombre_Tercero: String? = null,

                       @SerializedName("Valor")
                       @Column(name = "Valor")
                       var Valor: Double? = 0.0,

                       @SerializedName("Cantidad")
                       @Column(name = "Cantidad")
                       var Cantidad: Long? = 0,


                       @SerializedName("Cultivo_Id")
                       @Column(name = "Cultivo_Id")
                       var Cultivo_Id: Long? = 0,

                       @Column(name = "Nombre_Cultivo")
                       var Nombre_Cultivo: String? = null,

                       @Column(name = "Descripcion_Puk")
                       var Descripcion_Puk: String? = null



) {

    fun getFechaUpdateAt(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(updatedAt)
    }

    fun getFechaTransacccion(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Fecha_Transaccion)
    }

}