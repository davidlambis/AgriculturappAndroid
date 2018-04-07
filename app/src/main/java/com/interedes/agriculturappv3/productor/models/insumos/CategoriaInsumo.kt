package com.interedes.agriculturappv3.productor.models.insumos

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

@Table(name = "CategoriaInsumo", database = DataSource::class)
data class CategoriaInsumo(@PrimaryKey(autoincrement = true)
                           @SerializedName("Id")
                           @Column(name = "Id")
                           var Id: Long? = 0,
                           @SerializedName("Nombre")
                           @Column(name = "Nombre")
                           var Nombre: String? = null,
                           @SerializedName("NombreColor")
                           @Column(name = "NombreColor")
                           var NombreColor: String? = null,
                           @SerializedName("codigoColor")
                           @Column(name = "codigoColor")
                           var codigoColor: String? = null) {

    override fun toString(): String {
        return Nombre!!
    }
}