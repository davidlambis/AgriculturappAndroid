package com.interedes.agriculturappv3.modules.models.tratamiento.calificacion

import com.google.gson.annotations.SerializedName

class ResponseCalificacion {
    @SerializedName("value")
    var value: MutableList<Calificacion_Tratamiento>? = null
}