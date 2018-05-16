package com.interedes.agriculturappv3.modules.models.usuario

import com.google.gson.annotations.SerializedName


class UsuarioResponse {

    @SerializedName("value")
    var value: List<Usuario>? = null
}