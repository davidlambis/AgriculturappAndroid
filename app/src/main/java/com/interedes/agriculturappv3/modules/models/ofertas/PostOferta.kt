package com.interedes.agriculturappv3.modules.models.ofertas

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by EnuarMunoz on 11/06/18.
 */
data class PostOferta (

        @SerializedName("Id")
        var Id: Long? = 0,

        @SerializedName("CreatedOn")
        var CreatedOn: String? = null,

        @SerializedName("EstadoOferta")
        var EstadoOferta: Int? = 0,

        @SerializedName("EstadoOfertaId")
        var EstadoOfertaId: Long? = 0,

        @SerializedName("UpdatedOn")
        var UpdatedOn: String? = null,

        @SerializedName("UsuarioId")
        var UsuarioId: UUID? = null,

        @SerializedName("usuarioto")
        var usuarioto: UUID? = null

        ){


}