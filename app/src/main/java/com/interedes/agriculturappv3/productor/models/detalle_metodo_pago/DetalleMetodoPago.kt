package com.interedes.agriculturappv3.productor.models.detalle_metodo_pago

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

/*
@Table(name = "DetalleMetodoPago", database = DataSource::class)
class DetalleMetodoPago : BaseModel() {

    @PrimaryKey(autoincrement = true)
    @SerializedName("Id")
    @Column(name = "Id")
    var Id: Long? = 0

    @SerializedName("Nombre")
    @Column(name = "Nombre")
    var Nombre: String? = null

    @SerializedName("MetodoPagoId")
    @Column(name = "MetodoPagoId")
    var MetodoPagoId: Long? = 0
} */


@Table(database = DataSource::class)
data class DetalleMetodoPago(@PrimaryKey
                             @SerializedName("Id")
                             @Column(name = "Id")
                             var Id: Long? = 0,
                             @SerializedName("Nombre")
                             @Column(name = "Nombre")
                             var Nombre: String? = null,
                             @SerializedName("MetodoPagoId")
                             @Column(name = "MetodoPagoId")
                             var MetodoPagoId: Long? = 0) {

    override fun toString(): String {
        return Nombre.toString()
    }
}