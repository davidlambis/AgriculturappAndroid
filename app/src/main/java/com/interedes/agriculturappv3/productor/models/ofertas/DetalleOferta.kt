package com.interedes.agriculturappv3.productor.models.ofertas

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

@Table(database = DataSource::class)
data class DetalleOferta(@PrimaryKey(autoincrement = true)
                         @SerializedName("Id")
                         @Column(name = "Id")
                         var Id: Long? = 0,
                         @SerializedName("CalidadId")
                         @Column(name = "CalidadId")
                         var CalidadId: Long? = 0,
                         @SerializedName("OfertasId")
                         @Column(name = "OfertasId")
                         var OfertasId: Long? = 0,
                         @SerializedName("ProductoId")
                         @Column(name = "ProductoId")
                         var ProductoId: Long? = 0,
                         @SerializedName("UnidadMedidaId")
                         @Column(name = "UnidadMedidaId")
                         var UnidadMedidaId: Long? = 0,
                         @SerializedName("Valor_Oferta")
                         @Column(name = "Valor_Oferta")
                         var Valor_Oferta: Double? = 0.0,
                         @SerializedName("Valor_minimo")
                         @Column(name = "Valor_minimo")
                         var Valor_minimo: Double? = 0.0,
                         @SerializedName("Valor_transaccion")
                         @Column(name = "Valor_transaccion")
                         var Valor_transaccion: Double? = 0.0) {
}