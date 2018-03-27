package com.interedes.agriculturappv3.asistencia_tecnica.models.control_plaga

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

@Table(database = DataSource::class)
data class ControlPlaga(@PrimaryKey(autoincrement = true)
                        @SerializedName("Id")
                        @Column(name = "Id")
                        var Id: Long? = 0,
                        @SerializedName("CultivoId")
                        @Column(name = "CultivoId")
                        var CultivoId: Long? = 0,
                        @SerializedName("Dosis")
                        @Column(name = "Dosis")
                        var Dosis: String? = null,
                        @SerializedName("EnfermedadesId")
                        @Column(name = "EnfermedadesId")
                        var EnfermedadesId: Long? = 0,
                        @SerializedName("Fecha_aplicacion")
                        @Column(name = "Fecha_aplicacion")
                        var Fecha_aplicacion: String? = null,
                        @SerializedName("TratamientoId")
                        @Column(name = "TratamientoId")
                        var TratamientoId: Long? = 0,
                        @SerializedName("UnidadMedidaId")
                        @Column(name = "UnidadMedidaId")
                        var UnidadMedidaId: Long? = 0) {


}