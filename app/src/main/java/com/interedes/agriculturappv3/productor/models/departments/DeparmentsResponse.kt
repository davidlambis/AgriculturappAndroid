package com.interedes.agriculturappv3.productor.models.departments

import com.google.gson.annotations.SerializedName


class DeparmentsResponse {

    /*@SerializedName("departCiudades")
    var departCiudades: MutableList<Departamento> ?= null*/

    @SerializedName("value")
    var value: MutableList<Departamento>? = null

}