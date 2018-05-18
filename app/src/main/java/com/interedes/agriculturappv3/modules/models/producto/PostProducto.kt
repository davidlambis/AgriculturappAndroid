package com.interedes.agriculturappv3.modules.models.producto

import com.google.gson.annotations.SerializedName
import com.raizlabs.android.dbflow.annotation.Column


data class PostProducto(@SerializedName("Id")
                        @Column(name = "Id")
                        var Id: Long? = 0,

                        @SerializedName("CalidadId")
                        @Column(name = "CalidadId")
                        var CalidadId: Long? = 0,

                        @SerializedName("CategoriaId")
                        @Column(name = "CategoriaId")
                        var CategoriaId: Long? = 0,

                        @SerializedName("CodigoUp")
                        @Column(name = "CodigoUp")
                        var CodigoUp: String? = null,

                        @SerializedName("Descripcion")
                        @Column(name = "Descripcion")
                        var Descripcion: String? = null,

                        @SerializedName("FechaLimiteDisponibilidad")
                        @Column(name = "FechaLimiteDisponibilidad")
                        var FechaLimiteDisponibilidad: String? = null,

                        @SerializedName("Imagen")
                        @Column(name = "Imagen")
                        var Imagen: String? = null,

                        @SerializedName("IsEnable")
                        @Column(getterName = "getEnabled")
                        var Enabled: Boolean? = null,

                        @SerializedName("Precio")
                        @Column(name = "Precio")
                        var Precio: Double? = 0.0,

                        @SerializedName("PrecioSpecial")
                        @Column(name = "PrecioSpecial")
                        var PrecioSpecial: Double? = 0.0,

                        @SerializedName("Stock")
                        @Column(name = "Stock")
                        var Stock: Double? = 0.0,

                        @SerializedName("cultivoId")
                        @Column(name = "cultivoId")
                        var cultivoId: Long? = null,

                        @SerializedName("nombre")
                        @Column(name = "nombre")
                        var nombre: String? = null,

                        @SerializedName("UnidadMedidaId")
                        @Column(name = "Unidad_Medida_Id")
                        var Unidad_Medida_Id: Long? = null,

                        @SerializedName("PrecioUnidadMedida")
                        @Column(name = "PrecioUnidadMedida")
                        var PrecioUnidadMedida: String? = null

                        ) {


}