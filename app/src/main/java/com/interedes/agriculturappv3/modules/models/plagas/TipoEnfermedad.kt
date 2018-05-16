package com.interedes.agriculturappv3.modules.models.plagas

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

@Table(database = DataSource::class)
data class TipoEnfermedad(@PrimaryKey
                          @SerializedName("Id")
                          @Column(name = "Id")
                          var Id: Long? = 0,
                          @SerializedName("Descripcion")
                          @Column(name = "Descripcion")
                          var Descripcion: String? = null,
                          @SerializedName("Nombre")
                          @Column(name = "Nombre")
                          var Nombre: String? = null,


                          @SerializedName("NombreCientifico")
                          @Column(name = "NombreCientifico")
                          var NombreCientifico: String? = null,
                          @SerializedName("TipoProductoId")
                          @Column(name = "TipoProductoId")
                          var TipoProductoId: Long? = 0,
                          @Column(name = "NombreTipoProducto")
                          var NombreTipoProducto: String? = null,
                          @Column(name = "Imagen")
                          var Imagen: Int? = 0



                          ) {}