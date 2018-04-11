package com.interedes.agriculturappv3.productor.models.ventas

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

                       @Column(name = "Identificacion_Tercero")
                       var Identificacion_Tercero: String? = null,

                       @SerializedName("Valor")
                       @Column(name = "Valor")
                       var Valor_Total: Double? = 0.0,

                       @SerializedName("Valor_Unitario")
                       @Column(name = "Valor_Unitario")
                       var Valor_Unitario: Double? = 0.0,

                       @SerializedName("Cantidad")
                       @Column(name = "Cantidad")
                       var Cantidad: Long? = 0,


                       @SerializedName("Cultivo_Id")
                       @Column(name = "Cultivo_Id")
                       var Cultivo_Id: Long? = 0,

                       @Column(name = "Nombre_Cultivo")
                       var Nombre_Cultivo: String? = null,

                       @Column(name = "CategoriaPuk_Id")
                       var CategoriaPuk_Id: Long? = null,

                       @Column(name = "Descripcion_Puk")
                       var Descripcion_Puk: String? = null,

                       @Column(name = "Nombre_Detalle_Producto_Cultivo")
                       var Nombre_Detalle_Producto_Cultivo: String? = null,

                       @Column(name = "Nombre_Estado_Transaccion")
                       var Nombre_Estado_Transaccion: String? = null

) {

    fun getFechaUpdateAt(): String {
        val cal = updatedAt
        cal.add(Calendar.DATE, 1)
        val format1 = SimpleDateFormat("dd/MM/yyyy")
       // val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format1.format(cal.getTime() )
    }

    fun getFechaTransacccion(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Fecha_Transaccion)
    }


}