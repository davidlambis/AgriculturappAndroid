package com.interedes.agriculturappv3.modules.models.departments

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel

/*@Table(database = DataSource::class)
data class Departamento(@PrimaryKey
                        @SerializedName("Id")
                        @Column(name = "Id")
                        var Id: Long? = 0,
                        @SerializedName("nombre")
                        @Column(name = "nombre")
                        var nombre: String? = null,
                        @SerializedName("codigodpto")
                        @Column(name = "codigodpto")
                        var codigodpto: Long? = 0,
                        @SerializedName("ciudades")
                        @Column(name = "ciudades")
                        var ciudades: ArrayList<Ciudad>? = null) {

    override fun toString(): String {
        return nombre!!
    }

}*/


//Constructor

@Table(database = DataSource::class)
class Departamento : BaseModel() {

    //Atributes
    //Methods
    @SerializedName("Id")
    @PrimaryKey
    var Id: Long ?= 0

    @SerializedName("Nombre")
    @Column
    var Nombre: String ?= null

    @SerializedName("codigodpto")
    @Column
    var codigodpto: Long ?= 0

    @SerializedName("Ciudads")
    var ciudades: ArrayList<Ciudad> ?= ArrayList<Ciudad>()

    override fun toString(): String {
        return Nombre!!
    }
}
