package com.interedes.agriculturappv3.productor.models.tratamiento

import com.google.gson.annotations.SerializedName


class TratamientoResponse {
    @SerializedName("value")
    var value: MutableList<Tratamiento>? = null
}