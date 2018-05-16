package com.interedes.agriculturappv3.modules.models.tratamiento.calificacion

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table


@Table(database = DataSource::class)
data class Calificacion_Tratamiento(@PrimaryKey
                                    @SerializedName("Id")
                                    @Column(name = "Id")
                                    var Id: Long? = 0,

                                    @SerializedName("Nombre")
                                    @Column(name = "Nombre")
                                    var Nombre: String? = null,

                                    @SerializedName("TratamientoId")
                                    @Column(name = "TratamientoId")
                                    var TratamientoId: Long? = 0,

                                    @SerializedName("Valor")
                                    @Column(name = "Valor")
                                    var Valor: Double? = 0.0,

                                    var Valor_Promedio: Double? = 0.0,

                                    @SerializedName("userId")
                                    @Column(name = "User_Id")
                                    var User_Id: String? = null


) {
}