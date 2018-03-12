package com.interedes.agriculturappv3.asistencia_tecnica.models

import android.text.BoringLayout
import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.R.string.name
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel
import java.sql.Timestamp


@Table(database = DataSource::class)
data class Usuario(@PrimaryKey
                   @SerializedName("Id")
                   @Column(name = "Id")
                   var Id: Long? = 0,

                   @SerializedName("Nombre")
                   @Column(name = "Nombre")
                   var Nombre: String? = null,

                   @SerializedName("Apellidos")
                   @Column(name = "Apellidos")
                   var Apellidos: String? = null,

                   @SerializedName("Email")
                   @Column(name = "Email")
                   var Email: String? = null,

                   @SerializedName("EmailConfirmed")
                   @Column(getterName = "getEmailConfirmed")
                   var EmailConfirmed: Boolean? = false,

                   @Column(name = "Contrasena")
                   var Contrasena: String? = null,

                   @SerializedName("Identificacion")
                   @Column(name = "Identificacion")
                   var Identificacion: String? = null,

                   @SerializedName("Estado")
                   @Column(name = "Estado")
                   var Estado: String? = null,

                   @SerializedName("FechaRegistro")
                   @Column(name = "FechaRegistro")
                   var FechaRegistro: String? = null,

                   @SerializedName("Fotopefil")
                   @Column(name = "Fotopefil")
                   var Fotopefil: String? = null,

                   @SerializedName("Nro_movil")
                   @Column(name = "Nro_movil")
                   var Nro_movil: String? = null,

                   @SerializedName("NumeroCuenta")
                   @Column(name = "NumeroCuenta")
                   var NumeroCuenta: String? = null,

                   @SerializedName("PhoneNumber")
                   @Column(name = "PhoneNumber")
                   var PhoneNumber: String? = null,

                   @SerializedName("PhoneNumberConfirmed")
                   @Column(getterName  = "getPhoneNumberConfirmed")
                   var PhoneNumberConfirmed: Boolean? = false,

                   @SerializedName("UserName")
                   @Column(name = "UserName")
                   var UserName: String? = null,

                   @SerializedName("DetalleMetodoPagoId")
                   @Column(name = "DetalleMetodoPagoId")
                   var DetalleMetodoPagoId: Long? = 0,

                   @SerializedName("RolId")
                   @Column(name = "RolId")
                   var RolId: Long? = 0,

                   @Column(name = "DetalleMetodoPagoNombre")
                   var DetalleMetodoPagoNombre: String? = null,

                   @Column(name = "RolNombre")
                   var RolNombre: String? = null) {



}


