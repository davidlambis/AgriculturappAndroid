package com.interedes.agriculturappv3.modules.models.usuario

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.data.Blob
import java.util.*


@Table(database = DataSource::class)
data class Usuario(@PrimaryKey
                   @SerializedName("Id")
                   @Column(name = "Id")
                   var Id: UUID? = null,

                   @SerializedName("Apellidos")
                   @Column(name = "Apellidos")
                   var Apellidos: String? = null,

                   @SerializedName("DetallemetodoPagoId")
                   @Column(name = "DetallemetodoPagoId")
                   var DetalleMetodoPagoId: Long? = 0,

                   @SerializedName("Email")
                   @Column(name = "Email")
                   var Email: String? = null,

                   @SerializedName("EmailConfirmed")
                   @Column(getterName = "getEmailConfirmed")
                   var EmailConfirmed: Boolean? = false,

                   @SerializedName("Estado")
                   @Column(name = "Estado")
                   var Estado: String? = null,

                   @SerializedName("FechaRegistro")
                   @Column(name = "FechaRegistro")
                   var FechaRegistro: String? = null,

                   @SerializedName("Fotopefil")
                   @Column(name = "Fotopefil")
                   var Fotopefil: String? = null,

                   @SerializedName("Identificacion")
                   @Column(name = "Identificacion")
                   var Identificacion: String? = null,

                   @SerializedName("Nombre")
                   @Column(name = "Nombre")
                   var Nombre: String? = null,

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
                   @Column(getterName = "getPhoneNumberConfirmed")
                   var PhoneNumberConfirmed: Boolean? = false,

                   @SerializedName("RolId")
                   @Column(name = "RolId")
                   var RolId: UUID? = null,

                   @SerializedName("UserName")
                   @Column(name = "UserName")
                   var UserName: String? = null,

                   @Column(name = "Contrasena")
                   var Contrasena: String? = null,

                   @Column(name = "DetalleMetodoPagoNombre")
                   var DetalleMetodoPagoNombre: String? = null,

                   @Column(name = "RolNombre")
                   var RolNombre: String? = null,

                   @Column(name = "AccessToken")
                   var AccessToken: String? = null,

                   @Column(getterName = "getUsuarioRemembered")
                   var UsuarioRemembered: Boolean? = false,

                   @Column(name = "SessionId")
                   var SessionId: Long? = 0,

                   @Column(name = "IdFirebase")
                   var IdFirebase: String? = null,

                   @Column(name = "blobImagen")
                   var blobImagen: Blob? = null,

                   @Column(getterName = "getEstado_SincronizacionUpdate")
                   var Estado_SincronizacionUpdate: Boolean? = false


) {}


