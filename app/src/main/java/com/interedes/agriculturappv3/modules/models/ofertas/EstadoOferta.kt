package com.interedes.agriculturappv3.modules.models.ofertas

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table


@Table(database = DataSource::class)
data class EstadoOferta(@PrimaryKey
                         @SerializedName("Id")
                         @Column(name = "Id")
                         var Id: Long? = 0,

                         @SerializedName("valor")
                         @Column(name = "Valor")
                         var Valor: Long? = 0,

                         @SerializedName("nombre")
                         @Column(name = "Nombre")
                         var Nombre: String? = null
) {
}