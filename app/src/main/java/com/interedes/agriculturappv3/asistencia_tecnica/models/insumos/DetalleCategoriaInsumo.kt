package com.interedes.agriculturappv3.asistencia_tecnica.models.insumos

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

@Table(name = "DetalleCategoriaInsumo", database = DataSource::class)
data class DetalleCategoriaInsumo(@PrimaryKey(autoincrement = true)
                                  @SerializedName("Id")
                                  @Column(name = "Id")
                                  var Id: Long? = 0,
                                  @SerializedName("Categoria_InsumoId")
                                  @Column(name = "Categoria_InsumoId")
                                  var Categoria_InsumoId: Long? = 0,
                                  @SerializedName("Descripcion")
                                  @Column(name = "Descripcion")
                                  var Descripcion: String? = null,
                                  @SerializedName("Letra")
                                  @Column(name = "Letra")
                                  var Letra: String? = null) {

}