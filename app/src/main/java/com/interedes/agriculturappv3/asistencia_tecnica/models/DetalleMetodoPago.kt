package com.interedes.agriculturappv3.asistencia_tecnica.models

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel


@Table(name = "DetalleMetodoPago", database = DataSource::class)
class DetalleMetodoPago : BaseModel() {

    @PrimaryKey(autoincrement = true)
    @SerializedName("Id")
    @Column(name = "Id")
    var Id: Long? = 0

    @SerializedName("Nombre")
    @Column(name = "Nombre")
    var Nombre: String? = null

    @SerializedName("MetodoPagoId")
    @Column(name = "MetodoPagoId")
    var MetodoPagoId: Long? = 0
}