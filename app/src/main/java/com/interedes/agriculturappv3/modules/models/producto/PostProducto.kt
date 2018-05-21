package com.interedes.agriculturappv3.modules.models.producto

import com.google.gson.annotations.SerializedName
import com.raizlabs.android.dbflow.annotation.Column
import java.util.*


data class PostProducto(@SerializedName("Id")
                        var Id: Long? = 0,

                        @SerializedName("CalidadId")
                        var CalidadId: Long? = 0,

                        @SerializedName("CategoriaId")
                        var CategoriaId: Long? = 0,

                        @SerializedName("CodigoUp")
                        var CodigoUp: String? = null,

                        @SerializedName("Descripcion")
                        var Descripcion: String? = null,

                        @SerializedName("FechaLimiteDisponibilidad")
                        var FechaLimiteDisponibilidad: String? = null,

                        @SerializedName("Imagen")
                        var Imagen: String? = null,

                        @SerializedName("IsEnable")
                        var Enabled: Boolean? = null,

                        @SerializedName("Precio")
                        var Precio: Double? = 0.0,

                        @SerializedName("PrecioSpecial")
                        var PrecioSpecial: Double? = 0.0,

                        @SerializedName("Stock")
                        var Stock: Double? = 0.0,

                        @SerializedName("cultivoId")
                        var cultivoId: Long? = null,

                        @SerializedName("nombre")
                        var nombre: String? = null,

                        @SerializedName("UnidadMedidaId")
                        var Unidad_Medida_Id: Long? = null,

                        @SerializedName("PrecioUnidadMedida")
                        var PrecioUnidadMedida: String? = null,

                        @SerializedName("userId")
                        var userId: UUID? = null

                        ) {


}