package com.interedes.agriculturappv3.productor.models.unidad_productiva

import com.google.gson.annotations.SerializedName
import java.util.*


class ResponseUnidadProductiva {

    @SerializedName("Area")
    var Area: Double? = 0.0

    @SerializedName("CiudadId")
    var CiudadId: Long? = 0

    @SerializedName("Codigo")
    var Codigo: String? = null

    @SerializedName("Id")
    var Id: Long? = 0

    @SerializedName("UnidadMedidaId")
    var UnidadMedidaId: Long? = null

    @SerializedName("UsuarioId")
    var UsuarioId: UUID? = null

    @SerializedName("descripcion")
    var descripcion: String? = null

    @SerializedName("nombre")
    var nombre: String? = null
}