package com.interedes.agriculturappv3.modules.models.departments

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

@Table(database = DataSource::class)
data class Ciudad(@PrimaryKey
                  @SerializedName("Id")
                  @Column(name = "Id")
                  var Id: Long? = 0,
                  @SerializedName("Nombre")
                  @Column(name = "Nombre")
                  var Nombre: String? = null,
                  @SerializedName("departmentoId")
                  @Column(name = "departmentoId")
                  var departmentoId: Long? = 0,

                  @SerializedName("Departamento")
                  var Departamento: Departamento?= null

                  ) {

    override fun toString(): String {
        return Nombre!!
    }
}