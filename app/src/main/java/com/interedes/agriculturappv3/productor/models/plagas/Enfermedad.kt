package com.interedes.agriculturappv3.productor.models.plagas

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

@Table(database = DataSource::class)
data class Enfermedad(@PrimaryKey(autoincrement = true)
                      @SerializedName("Id")
                      @Column(name = "Id")
                      var Id: Long? = 0,
                      @SerializedName("Codigo")
                      @Column(name = "Codigo")
                      var Codigo: String? = null,
                      @SerializedName("TipoEnfermedadId")
                      @Column(name = "TipoEnfermedadId")
                      var TipoEnfermedadId: Long? = 0) {}