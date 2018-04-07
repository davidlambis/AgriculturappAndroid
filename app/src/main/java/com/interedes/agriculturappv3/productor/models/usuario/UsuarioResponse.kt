package com.interedes.agriculturappv3.productor.models.usuario

import com.google.gson.annotations.SerializedName


class UsuarioResponse {

    @SerializedName("value")
    var value: List<Usuario>? = null
}