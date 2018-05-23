package com.interedes.agriculturappv3.modules.models.detalletipoproducto

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

@Table(name = "DetalleTipoProducto", database = DataSource::class)
data class DetalleTipoProducto(@PrimaryKey(autoincrement = true)
                          @SerializedName("Id")
                          @Column(name = "Id")
                          var Id: Long? = 0,

                          @SerializedName("Descripcion")
                          @Column(name = "Descripcion")
                          var Descripcion: String? = null,

                          @SerializedName("Nombre")
                          @Column(name = "Nombre")
                          var Nombre: String? = null,

                          @SerializedName("TipoProductoId")
                          @Column(name = "TipoProductoId")
                          var TipoProductoId: Long? = 0,

                           @SerializedName("TipoProducto")
                            var tipoProducto: TipoProducto?= null,

                            @SerializedName("Cultivos")
                            var cultivos: ArrayList<Cultivo>?= null


                               )










{

    override fun toString(): String {
        return Nombre!!
    }

}