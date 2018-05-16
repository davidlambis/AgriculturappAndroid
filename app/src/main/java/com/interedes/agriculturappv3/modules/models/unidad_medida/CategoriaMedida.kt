package com.interedes.agriculturappv3.modules.models.unidad_medida

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

@Table(database = DataSource::class)
data class CategoriaMedida(@PrimaryKey
                           @SerializedName("Id")
                           @Column(name = "Id")
                           var Id: Long? = 0,
                           @SerializedName("nombreCategoria")
                           @Column(name = "nombreCategoria")
                           var nombreCategoria: String? = null,
                           @SerializedName("UnidadMedidas")
                           var UnidadMedidas: ArrayList<Unidad_Medida>?= ArrayList<Unidad_Medida>()
                           ) {

    override fun toString(): String {
        return nombreCategoria!!
    }
}