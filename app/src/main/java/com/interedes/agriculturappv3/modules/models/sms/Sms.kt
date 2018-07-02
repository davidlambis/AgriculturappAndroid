package com.interedes.agriculturappv3.modules.models.sms

import android.os.Parcel
import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.interedes.agriculturappv3.modules.models.parcelable.KParcelable
import com.interedes.agriculturappv3.modules.models.parcelable.parcelableCreator
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table


@Table(name = "Sms", database = DataSource::class)
 data class Sms(

        @PrimaryKey()
        @Column(name = "Id")
        var Id: Long? = 0,

        @Column(name = "Id_Message")
        var Id_Message: String? = null,

        @Column(name = "Address")
        var Address: String? = null,

        @Column(name = "Message")
        var Message: String? = null,
        @Column(name = "ReadState")
        var ReadState: String? = null, //"0" for have not read sms and "1" for have read sms

        @Column(name = "FechaSms")
        var FechaSms: String? = null,

        @Column(name = "Folder_Name")
        var Folder_Name: String? = null,

        @Column(name = "Emisor_Type_Message")
        var Emisor_Type_Message: String? = null,

        @Column(name = "ContactName")
        var ContactName: String? = null,

        @Column(name = "Room_Id")
        var Room_Id: String? = null,

        @Column(name = "SenderId")
        var SenderId: String? = null,

        @Column(name = "ReceiverId")
        var ReceiverId: String? = null,

        @Column(name = "Date")
        var Date: String? = null,

        @Column(name = "Hour")
        var Hour: String? = null,

        @Column(name = "Timestamp")
        var Timestamp:Long=0


 ): KParcelable {

       private constructor(p: Parcel) : this(
               Id = p.readLong(),
               Id_Message = p.readString(),
               Address = p.readString(),
               Message = p.readString(),
               ReadState = p.readString(),
               FechaSms = p.readString(),
               Folder_Name = p.readString(),
               Emisor_Type_Message = p.readString(),
               ContactName = p.readString(),
               Room_Id = p.readString(),
               SenderId = p.readString(),
               ReceiverId = p.readString(),
               Date = p.readString(),
               Hour = p.readString(),
               Timestamp = p.readLong()

               )


       override fun writeToParcel(dest: Parcel, flags: Int) {
              dest.writeLong(Id!!)
              dest.writeString(Id_Message)
              dest.writeString(Address)
              dest.writeString(Message)
              dest.writeString(ReadState)
              dest.writeString(FechaSms)
              dest.writeString(Folder_Name)
              dest.writeString(Emisor_Type_Message)
              dest.writeString(ContactName)
              dest.writeString(Room_Id)
              dest.writeString(SenderId)
              dest.writeString(ReceiverId)
              dest.writeString(Date)
              dest.writeString(Hour)
              dest.writeLong(Timestamp)
       }

       companion object {
              @JvmField val CREATOR = parcelableCreator(::Sms)
       }

}