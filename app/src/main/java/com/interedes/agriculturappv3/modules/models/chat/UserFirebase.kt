package com.interedes.agriculturappv3.modules.models.chat

import android.os.Parcel
import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.interedes.agriculturappv3.modules.models.parcelable.KParcelable
import com.interedes.agriculturappv3.modules.models.parcelable.parcelableCreator
import com.interedes.agriculturappv3.modules.models.parcelable.readBoolean
import com.interedes.agriculturappv3.modules.models.parcelable.writeBoolean
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.util.*

@Table(database = DataSource::class)
open class UserFirebase(

        @PrimaryKey()
        @SerializedName("User_Id")
        var User_Id: String? = null,

        @SerializedName("Nombre")
        @Column(name = "Nombre")
        var Nombre: String? = null,

        @SerializedName("Apellido")
        @Column(name = "Apellido")
        var Apellido: String? = null,

        @SerializedName("Cedula")
        @Column(name = "Cedula")
        var Cedula: String? = null,

        @SerializedName("Correo")
        @Column(name = "Correo")
        var Correo: String? = null,

        @SerializedName("Rol")
        @Column(name = "Rol")
        var Rol: String? = null,

        @SerializedName("Telefono")
        @Column(name = "Telefono")
        var Telefono: String? = null,

        @SerializedName("Status")
        @Column(name = "Status")
        var Status: String? = null,

        @SerializedName("Last_Online")
        @Column(name = "Last_Online")
        var Last_Online: Long? = 0,

        @SerializedName("Token_Account")
        @Column(name = "Token_Account")
        var Token_Account: String? = null,

        @SerializedName("Id_Account_Remote")
        @Column(name = "Id_Account_Remote")
        var Id_Account_Remote: String? = null,


        @SerializedName("Imagen")
        @Column(name = "Imagen")
        var Imagen: String? = null,


        @SerializedName("StatusTokenFcm")
        @Column(getterName = "getStatusTokenFcm")
        var StatusTokenFcm: Boolean = false,

        @SerializedName("TokenFcm")
        @Column(name = "TokenFcm")
        var TokenFcm: String? = null



): KParcelable {

        private constructor(p: Parcel) : this(
                User_Id = p.readString(),
                Nombre = p.readString(),
                Apellido = p.readString(),
                Cedula = p.readString(),
                Correo = p.readString(),
                Rol = p.readString(),
                Telefono = p.readString(),
                Status = p.readString(),
                Last_Online = p.readLong(),
                Token_Account = p.readString(),
                Id_Account_Remote = p.readString(),
                Imagen=p.readString(),
                StatusTokenFcm=p.readBoolean(),
                TokenFcm=p.readString())

        override fun writeToParcel(dest: Parcel, flags: Int) {
                dest.writeString(User_Id)
                dest.writeString(Nombre)
                dest.writeString(Apellido)
                dest.writeString(Cedula)
                dest.writeString(Correo)
                dest.writeString(Rol)
                dest.writeString(Telefono)
                dest.writeString(Status)
                dest.writeLong(Last_Online!!)
                dest.writeString(Token_Account)
                dest.writeString(Id_Account_Remote)
                dest.writeString(Imagen)
                dest.writeBoolean(StatusTokenFcm)
                dest.writeString(TokenFcm)
        }

        companion object {
                @JvmField val CREATOR = parcelableCreator(::UserFirebase)
        }
}