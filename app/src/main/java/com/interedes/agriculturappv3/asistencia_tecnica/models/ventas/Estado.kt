package com.interedes.agriculturappv3.asistencia_tecnica.models.ventas

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table



@Table(database = DataSource::class)
data class  Estado(@PrimaryKey(autoincrement = true)
                        @SerializedName("Id")
                        @Column(name = "Id")
                        var Id: Long? = 0,

                        @SerializedName("Nombre")
                        @Column(name = "Nombre")
                        var Nombre: String? = null


) {

    override fun toString(): String {
        return Nombre!!
    }
}