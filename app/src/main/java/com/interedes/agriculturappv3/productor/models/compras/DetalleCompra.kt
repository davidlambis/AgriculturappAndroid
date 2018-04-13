package com.interedes.agriculturappv3.productor.models.compras

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

@Table(database = DataSource::class)
data class DetalleCompra(@PrimaryKey(autoincrement = true)
                         @SerializedName("Id")
                         @Column(name = "Id")
                         var Id: Long? = 0,
                         @SerializedName("ComprasId")
                         @Column(name = "ComprasId")
                         var ComprasId: Long? = 0,
                         @SerializedName("Impuesto")
                         @Column(name = "Impuesto")
                         var Impuesto: Double? = 0.0,
                         @SerializedName("Precio")
                         @Column(name = "Precio")
                         var Precio: Double? = 0.0,
                         @SerializedName("ProductoId")
                         @Column(name = "ProductoId")
                         var ProductoId: Long? = 0,
                         @SerializedName("Quantity")
                         @Column(name = "Quantity")
                         var Quantity: Double? = 0.0,
                         @SerializedName("descripcion")
                         @Column(name = "descripcion")
                         var descripcion: String? = null) {


}