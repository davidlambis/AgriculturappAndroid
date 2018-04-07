package com.interedes.agriculturappv3.productor.models.control_plaga

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.text.SimpleDateFormat
import java.util.*

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
                        var Fecha_aplicacion: Date? = null,
                        @SerializedName("TratamientoId")
                        @Column(name = "TratamientoId")
                        var TratamientoId: Long? = 0,
                        @SerializedName("UnidadMedidaId")
                        @Column(name = "UnidadMedidaId")
                        var UnidadMedidaId: Long? = 0,
                        @Column(name = "NombrePlaga")
                        var NombrePlaga: String? = null,
                        @Column(name = "Fecha_Erradicacion")
                        var Fecha_Erradicacion: Date? = null,
                        @Column(getterName = "getEstadoErradicacion")
                        var EstadoErradicacion: Boolean? = false) {

    fun getFechaAplicacionFormat(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Fecha_aplicacion)
    }

    fun getFechaErradicacionFormat(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Fecha_Erradicacion)
    }

}