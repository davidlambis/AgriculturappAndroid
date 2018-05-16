package com.interedes.agriculturappv3.modules.models

import com.google.gson.annotations.SerializedName

class GenericResponse {

    @SerializedName("value")
    var value: MutableList<Any>? = null
}