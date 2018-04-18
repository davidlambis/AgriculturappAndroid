package com.interedes.agriculturappv3.productor

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.productor.models.Departamento


class TestResponse {

    @SerializedName("departCiudades")
    var departCiudades: MutableList<Departamento> ?= null

}