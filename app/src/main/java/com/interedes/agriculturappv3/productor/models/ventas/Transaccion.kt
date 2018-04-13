package com.interedes.agriculturappv3.productor.models.ventas

import android.os.Parcel
import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.interedes.agriculturappv3.productor.models.parcelable.KParcelable
import com.interedes.agriculturappv3.productor.models.parcelable.parcelableCreator
import com.interedes.agriculturappv3.productor.models.parcelable.readDate
import com.interedes.agriculturappv3.productor.models.parcelable.writeDate
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

                       @SerializedName("FechaString")
                       @Column(name = "FechaString")
                       var FechaString: String? = null,

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

): KParcelable {

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




    private constructor(p: Parcel) : this(
            Id = p.readLong(),
            Concepto = p.readString(),
            Fecha_Transaccion = p.readDate(),
            NaturalezaId = p.readLong(),
            PucId = p.readLong(),
            TerceroId = p.readLong(),
            Nombre_Tercero = p.readString(),
            Identificacion_Tercero = p.readString(),
            Valor_Total = p.readDouble(),
            Valor_Unitario = p.readDouble(),
            Cantidad = p.readLong(),
            Cultivo_Id = p.readLong(),
            Nombre_Cultivo = p.readString(),
            CategoriaPuk_Id = p.readLong(),
            Descripcion_Puk = p.readString(),
            Nombre_Detalle_Producto_Cultivo = p.readString(),
            Nombre_Estado_Transaccion = p.readString())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(Id!!)
        dest.writeString(Concepto)
        dest.writeLong(EstadoId!!)
        dest.writeDate(Fecha_Transaccion)
        dest.writeLong(NaturalezaId!!)
        dest.writeLong(PucId!!)
        dest.writeLong(TerceroId!!)
        dest.writeString(Nombre_Tercero)
        dest.writeString(Identificacion_Tercero)
        dest.writeDouble(Valor_Total!!)
        dest.writeDouble(Valor_Unitario!!)
        dest.writeLong(Cantidad!!)

        dest.writeLong(Cultivo_Id!!)
        dest.writeString(Nombre_Cultivo)
        dest.writeLong(CategoriaPuk_Id!!)
        dest.writeString(Descripcion_Puk)
        dest.writeString(Nombre_Detalle_Producto_Cultivo)
        dest.writeString(Nombre_Estado_Transaccion)

    }

    companion object {
        @JvmField val CREATOR = parcelableCreator(::Transaccion)
    }


}