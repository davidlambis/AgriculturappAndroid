package com.interedes.agriculturappv3.asistencia_tecnica.models.usuario

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.asistencia_tecnica.models.rol.Rol


class UsuarioResponse {

    @SerializedName("value")
    var value: List<Usuario>? = null
}