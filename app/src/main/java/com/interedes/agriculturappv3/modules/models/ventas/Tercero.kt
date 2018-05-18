package com.interedes.agriculturappv3.modules.models.ventas

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table


@Table(database = DataSource::class)
data class Tercero(@PrimaryKey
                   @SerializedName("Id")
                   @Column(name = "Id")
                   var Id: Long? = 0,

                   @SerializedName("Nombre")
                   @Column(name = "Nombre")
                   var Nombre: String? = null,

                   @SerializedName("Apellido")
                   @Column(name = "Apellido")
                   var Apellido: String? = null,

                   @SerializedName("Nit_RUC")
                   @Column(name = "NitRut")
                   var NitRut: String? = null,

                   @SerializedName("Direccion")
                   @Column(name = "Direccion")
                   var Direccion: String? = null,


                   @Column(getterName = "getEstado_Sincronizacion")
                   var Estado_Sincronizacion: Boolean? = false,

                   @Column(getterName = "getEstado_SincronizacionUpdate")
                   var Estado_SincronizacionUpdate: Boolean? = false


) {

    override fun toString(): String {
        return Nombre!!
    }
}