package com.interedes.agriculturappv3.productor.models.unidad_medida

import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.annotation.Column
import com.google.gson.annotations.SerializedName
/**
 * Created by EnuarMunoz on 17/03/18.
 */


@Table(database = DataSource::class)
data class Unidad_Medida(@PrimaryKey(autoincrement = true)
                            @SerializedName("Id")
                            @Column(name = "Id")
                            var Id: Long? = 0,

                            @SerializedName("Descripcion")
                            @Column(name = "Descripcion")
                            var Descripcion: String? = null,

                            @SerializedName("Sigla")
                            @Column(name = "Sigla")
                            var Sigla: String? = null

) {
    override fun toString(): String {
        return Descripcion!!
    }

}