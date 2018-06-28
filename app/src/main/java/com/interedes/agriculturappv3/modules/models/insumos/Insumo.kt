package com.interedes.agriculturappv3.modules.models.insumos

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.data.Blob

@Table(name = "Insumo", database = DataSource::class)
data class Insumo(@PrimaryKey
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
                  @Column(name = "TipoEnfermedadId")
                  var TipoEnfermedadId: Long? = 0,

                  @Column(name = "Foto")
                  var Foto: Int? = 0,

                  @SerializedName("Imagen")
                  @Column(name = "Imagen")
                  var Imagen: String? = null,

                  @Column(name = "blobImagen")
                  var blobImagen: Blob? = null,

                  @SerializedName("NombreLaboratorio")
                  @Column(name = "NombreLaboratorio")
                  var NombreLaboratorio: String? = null,

                  @SerializedName("NombreTipoInsumo")
                  @Column(name = "NombreTipoInsumo")
                  var NombreTipoInsumo: String? = null,

                  @Column(getterName = "getFotoLoaded")
                  var FotoLoaded: Boolean? = false,

                  @SerializedName("Laboratorio")
                  var Laboratorio: Laboratorio?=Laboratorio(),

                  @SerializedName("TipoInsumo")
                  var TipoInsumo: TipoInsumo?=TipoInsumo()

                  ) {

    override fun toString(): String {
        return Nombre!!
    }
}