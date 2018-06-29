package com.interedes.agriculturappv3.modules.models.sms

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table


@Table(name = "Sms", database = DataSource::class)
 data class Sms(

        @PrimaryKey(autoincrement = true)
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
 ) {

}