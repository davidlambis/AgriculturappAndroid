package com.interedes.agriculturappv3.modules.models.compras

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.text.SimpleDateFormat
import java.util.*

@Table(database = DataSource::class)
data class Compras(@PrimaryKey(autoincrement = true)
                   @SerializedName("Id")
                   @Column(name = "Id")
                   var Id: Long? = 0,
                   @SerializedName("CodigoCupon")
                   @Column(name = "CodigoCupon")
                   var CodigoCupon: String? = null,
                   @SerializedName("CompraStatus")
                   @Column(name = "CompraStatus")
                   var CompraStatus: Int? = 0,
                   @SerializedName("CreatedOn")
                   @Column(name = "CreatedOn")
                   var CreatedOn: Date? = null,
                   @SerializedName("Descuento")
                   @Column(name = "Descuento")
                   var Descuento: Double? = null,
                   @SerializedName("Impuesto")
                   @Column(name = "Impuesto")
                   var Impuesto: Double? = null,
                   @SerializedName("MetodoPago")
                   @Column(name = "MetodoPago")
                   var MetodoPago: String? = null,
                   @SerializedName("TotalCompra")
                   @Column(name = "TotalCompra")
                   var TotalCompra: Double? = null,
                   @SerializedName("UpdatedOn")
                   @Column(name = "UpdatedOn")
                   var UpdatedOn: Date? = null,
                   @SerializedName("UsuarioId")
                   @Column(name = "UsuarioId")
                   var UsuarioId: Long? = 0,
                   @Column(name = "NombreUsuario")
                   var NombreUsuario: String? = null,
                   @Column(name = "UsuarioVendedorId")
                   var UsuarioVendedorId: Long? = 0,
                   @Column(name = "ProductoId")
                   var ProductoId: Long? = 0,
                   @Column(name = "NombreProducto")
                   var NombreProducto: String? = null) {

    fun getCreatedOnFormat(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(CreatedOn)
    }

    fun getUpdatedOnFormat(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(UpdatedOn)
    }
}