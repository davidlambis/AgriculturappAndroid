package com.interedes.agriculturappv3.productor.models.plagas

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.data.Blob

@Table(database = DataSource::class)
data class FotoEnfermedad(@PrimaryKey
                          @SerializedName("Id")
                          @Column(name = "Id")
                          var Id: Long? = 0,

                          @SerializedName("Descripcion")
                          @Column(name = "Descripcion")
                          var Descripcion: String? = null,

                          @SerializedName("FechaCreacion")
                          @Column(name = "FechaCreacion")
                          var FechaCreacion: String? = null,


                          @SerializedName("Hora")
                          @Column(name = "Hora")
                          var Hora: String? = null,
                          @SerializedName("Ruta")
                          @Column(name = "Ruta")
                          var Ruta: String? = null,
                          @SerializedName("Titulo")
                          @Column(name = "Titulo")
                          var Titulo: String? = null,
                          @SerializedName("EnfermedadesId")
                          @Column(name = "EnfermedadesId")
                          var EnfermedadesId: Long? = 0,

                          @Column(name = "blobImagen")
                          var blobImagen: Blob? = null

                          ) {
}