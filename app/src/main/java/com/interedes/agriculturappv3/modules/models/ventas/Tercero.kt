package com.interedes.agriculturappv3.modules.models.ventas

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.util.*


@Table(database = DataSource::class)
data class Tercero(@PrimaryKey
                   @Column(name = "TerceroId")
                   var TerceroId: Long? = 0,

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
                   var Estado_SincronizacionUpdate: Boolean? = false,

                   @SerializedName("Id")
                   @Column(name = "Id_Remote")
                   var Id_Remote: Long? = 0,

                   @Column(name = "Usuario_Id")
                   var Usuario_Id: UUID? = null


) {

    override fun toString(): String {
        return Nombre!!
    }
}