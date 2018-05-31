package com.interedes.agriculturappv3.modules.models.control_plaga

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Table(database = DataSource::class)
data class ControlPlaga(@PrimaryKey
                        @Column(name = "ControlPlagaId")
                        var ControlPlagaId: Long? = 0,

                        @SerializedName("CultivoId")
                        @Column(name = "CultivoId")
                        var CultivoId: Long? = 0,

                        @SerializedName("Dosis")
                        @Column(name = "Dosis")
                        var Dosis: Double? = null,

                        @SerializedName("EnfermedadesId")
                        @Column(name = "EnfermedadesId")
                        var EnfermedadesId: Long? = 0,


                        @Column(name = "Fecha_aplicacion_local")
                        var Fecha_aplicacion_local: Date? = null,

                        @SerializedName("TratamientoId")
                        @Column(name = "TratamientoId")
                        var TratamientoId: Long? = 0,

                        @SerializedName("UnidadMedidaId")
                        @Column(name = "UnidadMedidaId")
                        var UnidadMedidaId: Long? = 0,

                        @Column(name = "NombrePlaga")
                        var NombrePlaga: String? = null,

                        @Column(name = "Fecha_Erradicacion_Local")
                        var Fecha_Erradicacion_Local: Date? = null,

                        @SerializedName("FechaErradicacion")
                        var FechaErradicacion: String? = null,

                        @SerializedName("Fecha_aplicacion")
                        var Fecha_aplicacion: String? = null,

                        @SerializedName("estadoRadicacion")
                        @Column(getterName = "getEstadoErradicacion")
                        var EstadoErradicacion: Boolean? = false,

                        @Column(getterName = "getEstado_Sincronizacion")
                        var Estado_Sincronizacion: Boolean? = false,

                        @Column(getterName = "getEstado_SincronizacionUpdate")
                        var Estado_SincronizacionUpdate: Boolean? = false,

                        @SerializedName("Id")
                        @Column(name = "Id_Remote")
                        var Id_Remote: Long? = 0,

                        @Column(name = "UsuarioId")
                        var UsuarioId: UUID? = null

                        ) {

    fun getFechaAplicacionFormat(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Fecha_aplicacion_local)
    }

    fun getFechaErradicacionFormat(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Fecha_Erradicacion_Local)
    }



    fun getFechaAplicacionFormatApi(): String? {
        val format1 = SimpleDateFormat("yyyy-MM-dd")
        return format1.format(Fecha_aplicacion_local)
    }

    fun getFechaErradicacionFormatApi(): String? {
        if(Fecha_Erradicacion_Local!=null){
            val format1 = SimpleDateFormat("yyyy-MM-dd")
            return format1.format(Fecha_Erradicacion_Local)
        }
        return null
    }


    fun getFechaDate(stringDate:String?):Date?{
        val dateString = stringDate
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        var convertedDate = Date()
        try {
            convertedDate = dateFormat.parse(dateString)
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return convertedDate
    }

}