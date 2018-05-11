package com.interedes.agriculturappv3.productor.models.tratamiento.calificacion

import com.google.gson.annotations.SerializedName
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey

data class PostCalificacion(
                            @SerializedName("Id")
                            var Id: Long? = 0,

                            @SerializedName("Nombre")
                            var Nombre: String? = null,

                            @SerializedName("TratamientoId")
                            var TratamientoId: Long? = 0,

                            @SerializedName("Valor")
                            var Valor: Double? = 0.0,

                            @SerializedName("userId")
                            var userId: String? = null) {
}