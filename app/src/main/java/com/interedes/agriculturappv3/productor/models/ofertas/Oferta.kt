package com.interedes.agriculturappv3.productor.models.ofertas

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.text.SimpleDateFormat
import java.util.*

@Table(database = DataSource::class)
data class Oferta(@PrimaryKey(autoincrement = true)
                  @SerializedName("Id")
                  @Column(name = "Id")
                  var Id: Long? = 0,
                  @SerializedName("CreatedOn")
                  @Column(name = "CreatedOn")
                  var CreatedOn: Date? = null,
                  @SerializedName("EstadoOferta")
                  @Column(name = "EstadoOferta")
                  var EstadoOferta: Int? = 0,
                  @SerializedName("EstadoOfertaId")
                  @Column(name = "EstadoOfertaId")
                  var EstadoOfertaId: Long? = 0,
                  @SerializedName("UpdatedOn")
                  @Column(name = "UpdatedOn")
                  var UpdatedOn: Date? = null,
                  @SerializedName("UsuarioId")
                  @Column(name = "UsuarioId")
                  var UsuarioId: Long? = 0,
                  @Column(name = "ProductoId")
                  var ProductoId: Long? = 0) {

    fun getCreatedOnFormat(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(CreatedOn)
    }

    fun getUpdatedOnFormat(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(UpdatedOn)
    }
}