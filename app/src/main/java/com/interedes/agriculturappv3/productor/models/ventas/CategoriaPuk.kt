package com.interedes.agriculturappv3.productor.models.ventas

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table



@Table(database = DataSource::class)
data class CategoriaPuk(@PrimaryKey(autoincrement = true)
                   @SerializedName("Id")
                   @Column(name = "Id")
                   var Id: Long? = 0,

                   @SerializedName("Nombre")
                   @Column(name = "Nombre")
                   var Nombre: String? = null,

                   @SerializedName("SIGLA")
                   @Column(name = "Sigla")
                   var Sigla: String? = null

                   ) {

    override fun toString(): String {
        return Nombre!!
    }
}