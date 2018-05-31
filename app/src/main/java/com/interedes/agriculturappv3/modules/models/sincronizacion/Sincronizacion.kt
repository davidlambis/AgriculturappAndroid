package com.interedes.agriculturappv3.modules.models.sincronizacion

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.data.Blob
import java.text.SimpleDateFormat
import java.util.*


@Table(name = "Sincronizacion", database = DataSource::class)
data class Sincronizacion(@PrimaryKey(autoincrement = true)
                          @SerializedName("Id")
                          @Column(name = "Id")
                          var Id: Long? = 0,

                          @SerializedName("Usuario_Id")
                          @Column(name = "Usuario_Id")
                          var Usuario_Id: UUID? = null,

                          @Column(name = "Fecha")
                          var Fehca: Date? = null,

                          @Column(name = "FechaString_Sync")
                          var FechaString_Sync: String? = null,

                          @Column(name = "updatedAt")
                          var updatedAt: Calendar = Calendar.getInstance(),

                          @Column(name = "Hora")
                          var Hora: String? = null,

                          @Column(name = "Cuenta")
                          var Cuenta: String? = null,

                          @Column(name = "Usuario")
                          var Usuario: String? = null,

                          @Column(name = "Cantidad_Sync")
                          var Cantidad_Sync: Long? = 0,

                          @Column(getterName = "getSincronizacion_Upload")
                          var Sincronizacion_Upload: Boolean? = false,

                          @Column(getterName = "getSincronizacion_Download")
                          var Sincronizacion_Download: Boolean? = false
                          ) {

    fun getFechaStringSync(): String {
        val dateFormatFecha = SimpleDateFormat("MM-dd-yyyy")
        val FechaString = dateFormatFecha.format(Calendar.getInstance().getTime())
        return FechaString
    }

    fun getHoraStringSync(): String {
        var cal = Calendar.getInstance()
        var timeFormat = SimpleDateFormat("HH:mm")
        var hora = timeFormat.format(cal.time)
        return hora
    }





}