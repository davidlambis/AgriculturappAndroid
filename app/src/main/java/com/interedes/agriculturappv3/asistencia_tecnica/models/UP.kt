package com.interedes.agriculturappv3.asistencia_tecnica.models

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table


/**
 * Created by EnuarMunoz on 12/03/18.
 */


//@Table(database = DataSource::class)
data  class UP(
       // @PrimaryKey
       // @SerializedName("Id")
       // @Column(name = "Id")
        var Id: Long?,

        // @SerializedName("UpName")
        //@Column(name = "UpName")
        var UpName: String?,

        //@SerializedName("UpDpto")
        //@Column(name = "UpDpto")
        var UpDpto:Int?,

        //@SerializedName("UpMpio")
        //@Column(name = "UpMpio")
        var UpMpio:Int?,

        //@SerializedName("UpArea")
        //@Column(name = "UpArea")
        var UpArea: Long? ) {


    override fun toString(): String {
        return UpName!!
    }
}



