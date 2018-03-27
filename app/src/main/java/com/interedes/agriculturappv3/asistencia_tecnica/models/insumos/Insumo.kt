package com.interedes.agriculturappv3.asistencia_tecnica.models.insumos

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

@Table(name = "Insumo", database = DataSource::class)
data class Insumo(@PrimaryKey(autoincrement = true)
                  @SerializedName("Id")
                  @Column(name = "Id")
                  var Id: Long? = 0,
                  @SerializedName("Descripcion")
                  @Column(name = "Descripcion")
                  var Descripcion: String? = null,
                  @SerializedName("Fecha_Vencimiento")
                  @Column(name = "Fecha_Vencimiento")
                  var Fecha_Vencimiento: String? = null,
                  @SerializedName("Nombre")
                  @Column(name = "Nombre")
                  var Nombre: String? = null,
                  @SerializedName("Tipo_InsumoId")
                  @Column(name = "Tipo_InsumoId")
                  var Tipo_InsumoId: Long? = 0,
                  @SerializedName("laboratorioId")
                  @Column(name = "laboratorioId")
                  var laboratorioId: Long? = 0,
                  @SerializedName("lote_laboratorio")
                  @Column(name = "lote_laboratorio")
                  var lote_laboratorio: String? = null,
                  @Column(name = "EnfermedadId")
                  var EnfermedadId: Long? = 0,
                  @Column(name = "Imagen")
                  var Imagen: Int? = 0) {

    override fun toString(): String {
        return Nombre!!
    }
}