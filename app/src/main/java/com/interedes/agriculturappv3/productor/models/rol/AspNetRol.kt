package com.interedes.agriculturappv3.productor.models.rol

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.util.*

@Table(database = DataSource::class)
data class AspNetRol(@PrimaryKey
                     @SerializedName("Id")
                     @Column(name = "Id")
                     var Id: UUID? = null,
                     @SerializedName("Name")
                     @Column(name = "Name")
                     var Name: String? = null,
                     @SerializedName("NormalizedName")
                     @Column(name = "NormalizedName")
                     var NormalizedName: String? = null)
