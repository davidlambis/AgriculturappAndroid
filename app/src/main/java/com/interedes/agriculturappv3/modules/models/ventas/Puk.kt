package com.interedes.agriculturappv3.modules.models.ventas

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table


@Table(database = DataSource::class)
data class Puk(@PrimaryKey
                      @SerializedName("Id")
                      @Column(name = "Id")
                      var Id: Long? = 0,

                      @SerializedName("CategoriaId")
                      @Column(name = "CategoriaId")
                      var CategoriaId: Long? = 0,

                       @SerializedName("Codigo")
                       @Column(name = "Codigo")
                       var Codigo: String? = null,

                       @SerializedName("Descripcion")
                       @Column(name = "Descripcion")
                       var Descripcion: String? = null


) {

    override fun toString(): String {
        return Descripcion!!
    }
}