package com.interedes.agriculturappv3.productor.models.producto

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.data.Blob
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by usuario on 20/03/2018.
 */


@Table(database = DataSource::class)
data class Producto(@PrimaryKey(autoincrement = true)
                    @SerializedName("Id")
                    @Column(name = "Id")
                    var Id: Long? = 0,

                    @SerializedName("CalidadId")
                    @Column(name = "CalidadId")
                    var CalidadId: Long? = null,

                    @SerializedName("CategoriaId")
                    @Column(name = "CategoriaId")
                    var CategoriaId: Long? = null,

                    @SerializedName("nombre")
                    @Column(name = "Nombre")
                    var Nombre: String? = null,

                    @SerializedName("Descripcion")
                    @Column(name = "Descripcion")
                    var Descripcion: String? = null,

                    @SerializedName("FechaLimiteDisponibilidad")
                    @Column(name = "FechaLimiteDisponibilidad")
                    var FechaLimiteDisponibilidad: Date? = null,

                    @SerializedName("Imagen")
                    @Column(name = "Imagen")
                    var Imagen: Blob? = null,
/*
                    @SerializedName("IsEnabled")
                    @Column(getterName = "getIsEnabled")
                    var IsEnabled: Boolean? = null,*/

                    @SerializedName("IsEnable")
                    @Column(getterName = "getEnabled")
                    var Enabled: Boolean? = null,

                    @SerializedName("Precio")
                    @Column(name = "Precio")
                    var Precio: Double? = 0.0,

                    @SerializedName("PrecioSpecial")
                    @Column(name = "PrecioSpecial")
                    var PrecioSpecial: Double? = 0.0,

                    @SerializedName("Stock")
                    @Column(name = "Stock")
                    var Stock: Double? = 0.0,


                    @SerializedName("cultivoId")
                    @Column(name = "CultivoId")
                    var CultivoId: Long? = null,

                    @Column(name = "NombreUnidadMedida")
                    var NombreUnidadMedida: String? = null,

                    @Column(name = "NombreUnidadProductiva")
                    var NombreUnidadProductiva: String? = null,

                    @Column(name = "NombreLote")
                    var NombreLote: String? = null,

                    @Column(name = "NombreCultivo")
                    var NombreCultivo: String? = null,

                    @Column(name = "NombreDetalleTipoProducto")
                    var NombreDetalleTipoProducto: String? = null,

                    @Column(name = "NombreCalidad")
                    var NombreCalidad: String? = null) {

    override fun toString(): String {
        return Descripcion!!
    }

    fun getFechaLimiteDisponibilidadFormat(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(FechaLimiteDisponibilidad)
    }


}

