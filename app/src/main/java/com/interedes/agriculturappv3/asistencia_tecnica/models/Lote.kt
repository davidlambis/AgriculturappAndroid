package com.interedes.agriculturappv3.asistencia_tecnica.models

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel

/**
 * Created by EnuarMunoz on 7/03/18.
 */
/*
data class Lote(val Nombre: String?,
                var Descripcion: String?,
                var Area:Double?,
                var Coordenadas:String?,
                var Unidad_Medida_Id: Long?,
                var Unidad_Productiva_Id:Long?) {}*/

@Table(name = "Lote", database = DataSource::class)
class Lote : BaseModel() {

    @PrimaryKey(autoincrement = true)
    @SerializedName("Id")
    @Column(name = "Id")
    var Id: Long = 0

    @SerializedName("Nombre")
    @Column(name = "Nombre")
    var Nombre: String? =null

    @SerializedName("Descripcion")
    @Column(name = "Descripcion")
    var Descripcion: String? =null

    @SerializedName("Area")
    @Column(name = "Area")
    var Area: Double? =null

    @SerializedName("Latitud")
    @Column(name = "Latitud")
    var Latitud: Double? =null

    @SerializedName("Longitud")
    @Column(name = "Longitud")
    var Longitud: Double? =null

    @SerializedName("Poligono_Lote")
    @Column(name = "Poligono_Lote")
    var Poligono_Lote: String? =null

    @SerializedName("Coordenadas")
    @Column(name = "Coordenadas")
    var Coordenadas: String? =null

    @SerializedName("Unidad_Medida_Id")
    @Column(name = "Unidad_Medida_Id")
    var Unidad_Medida_Id: Long? =null


    @SerializedName("Unidad_Productiva_Id")
    @Column(name = "Unidad_Productiva_Id")
    var Unidad_Productiva_Id: Long? =null


    @Column(name = "Nombre_Unidad_Productiva")
    var Nombre_Unidad_Productiva: String? =null
}