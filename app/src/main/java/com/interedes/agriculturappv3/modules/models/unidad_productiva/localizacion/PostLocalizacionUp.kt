package com.interedes.agriculturappv3.modules.models.unidad_productiva.localizacion

import com.google.gson.annotations.SerializedName


data class PostLocalizacionUp(@SerializedName("Id")
                                var Id: Long? = 0,

                                @SerializedName("Barrio")
                                var Barrio: String? = null,

                                @SerializedName("Coordenadas")
                                var Coordenadas: String? = null,

                                @SerializedName("Direccion")
                                var Direccion: String? = null,

                                @SerializedName("DireccionAproximadaGps")
                                var DireccionAproximadaGps: String? = null,

                                @SerializedName("Latitud")
                                var Latitud: Double? = null,

                                @SerializedName("Longitud")
                                var Longitud: Double? = null,

                                @SerializedName("ReferenciaLocalizacion")
                                var ReferenciaLocalizacion: String? = null,

                                @SerializedName("Localidad")
                                var Localidad: String? = null,

                                @SerializedName("Sector")
                                var Sector: String? = null,


                                @SerializedName("UnidadProductivaId")
                                var UnidadProductivaId: Long? = 0,

                                @SerializedName("veredad")
                                var Vereda: String? = null) {

}