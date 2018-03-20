package com.interedes.agriculturappv3.asistencia_tecnica.models.rol

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.util.*


@Table(database = DataSource::class)
data class Rol(@PrimaryKey
               @SerializedName("Id")
               @Column(name = "Id")
               var Id: UUID? = null,
               @SerializedName("Nombre")
               @Column(name = "Nombre")
               var Nombre: String? = null,
               @Column(name = "Imagen")
               var Imagen: Int? = 0)