package com.interedes.agriculturappv3.productor.models.tipoproducto

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.interedes.agriculturappv3.productor.models.detalletipoproducto.DetalleTipoProducto
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.data.Blob

@Table(name = "TipoProducto", database = DataSource::class)
data class TipoProducto(@PrimaryKey
                        @SerializedName("Id")
                        @Column(name = "Id")
                        var Id: Long? = 0,

                        @SerializedName("Nombre")
                        @Column(name = "Nombre")
                        var Nombre: String? = null,

                        @SerializedName("Icono")
                        @Column(name = "Icono")
                        var Icono: String? = null,

                        @Column(name = "Imagen")
                        var Imagen: Blob? = null,

                        @SerializedName("DetalleTipoProductos")
                        var DetalleTipoProductos: ArrayList<DetalleTipoProducto>?= ArrayList<DetalleTipoProducto>()
)

{

    override fun toString(): String {
        return Nombre!!
    }
}