package com.interedes.agriculturappv3.productor.models.producto

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

@Table(database = DataSource::class)
data class CalidadProducto(@PrimaryKey(autoincrement = true)
                           @SerializedName("Id")
                           @Column(name = "Id")
                           var Id: Long? = 0,
                           @SerializedName("Descripcion")
                           @Column(name = "Descripcion")
                           var Descripcion: String? = null,
                           @SerializedName("Nombre")
                           @Column(name = "Nombre")
                           var Nombre: String? = null) {

    override fun toString(): String {
        return Nombre!!
    }

}