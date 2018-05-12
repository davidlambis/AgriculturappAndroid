package com.interedes.agriculturappv3.productor.models.ventas

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

               @SerializedName("NITRut")
               @Column(name = "NitRut")
               var NitRut: String? = null,

                   @Column(getterName = "getEstado_Sincronizacion")
                   var Estado_Sincronizacion: Boolean? = false


) {

    override fun toString(): String {
        return Nombre!!
    }
}