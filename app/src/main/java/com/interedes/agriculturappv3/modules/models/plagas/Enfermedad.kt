package com.interedes.agriculturappv3.modules.models.plagas

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.data.Blob

@Table(database = DataSource::class)
data class Enfermedad(@PrimaryKey
                      @SerializedName("Id")
                      @Column(name = "Id")
                      var Id: Long? = 0,
                      @SerializedName("Codigo")
                      @Column(name = "Codigo")
                      var Codigo: String? = null,

                      @SerializedName("TipoProductoId")
                      @Column(name = "TipoProductoId")
                      var TipoProductoId: Long? = 0,

                      @SerializedName("TipoEnfermedadId")
                      @Column(name = "TipoEnfermedadId")
                      var TipoEnfermedadId: Long? = 0,

                      @Column(name = "Descripcion")
                      var Descripcion: String? = null,

                      @Column(name = "NombreTipoEnfermedad")
                      var NombreTipoEnfermedad: String? = null,

                      @Column(name = "NombreTipoProducto")
                      var NombreTipoProducto: String? = null,

                      @Column(name = "NombreCientificoTipoEnfermedad")
                      var NombreCientificoTipoEnfermedad: String? = null,

                      @Column(name = "DescripcionTipoEnfermedad")
                      var DescripcionTipoEnfermedad: String? = null,


                      @Column(name = "blobImagenEnfermedad")
                      var blobImagenEnfermedad: Blob? = null,

                      @Column(name = "RutaImagenEnfermedad")
                      var RutaImagenEnfermedad: String? = null,


                      @SerializedName("TipoEnfermedad")
                      var TipoEnfermedad: TipoEnfermedad?= null,

                      @SerializedName("TipoProducto")
                      var TipoProducto: TipoProducto?= null,

                      @SerializedName("Fotos")
                      var Fotos: ArrayList<FotoEnfermedad> ?= ArrayList<FotoEnfermedad>()

) {}