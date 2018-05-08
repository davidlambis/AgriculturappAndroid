package com.interedes.agriculturappv3.productor.models.unidad_productiva


import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.util.*

@Table(database = DataSource::class)
data class Unidad_Productiva(@PrimaryKey
                            @SerializedName("Id")
                            @Column(name = "Id")
                            var Id: Long? = 0,

                             @SerializedName("Area")
                            @Column(name = "Area")
                            var Area: Double? = 0.0,

                             @SerializedName("CiudadId")
                            @Column(name = "CiudadId")
                            var CiudadId: Long? = 0,

                             @SerializedName("Codigo")
                            @Column(name = "Codigo")
                            var Codigo: String? = null,

                             @SerializedName("UnidadMedidaId")
                            @Column(name = "UnidadMedidaId")
                            var UnidadMedidaId: Long? = null,

                             @SerializedName("UsuarioId")
                            @Column(name = "UsuarioId")
                            var UsuarioId: UUID? = null,

                             @SerializedName("descripcion")
                            @Column(name = "descripcion")
                            var descripcion: String? = null,

                             @SerializedName("nombre")
                            @Column(name = "nombre")
                            var nombre: String? = null,

                             @Column(name = "Coordenadas")
                            var Coordenadas: String? = null,

                             @Column(name = "Direccion")
                            var Direccion: String? = null,

                             @Column(name = "Latitud")
                            var Latitud: Double? = 0.0,

                             @Column(name = "Longitud")
                            var Longitud: Double? = 0.0,

                             @Column(name = "DireccionAproximadaGps")
                            var DireccionAproximadaGps: String? = null,

                             @Column(name = "Postal_Code_Gps")
                            var Postal_Code_Gps: String? = null,

                             @Column(getterName = "getConfiguration_Point")
                            var Configuration_Point: Boolean? = null,

                             @Column(getterName = "getConfiguration_Poligon")
                            var Configuration_Poligon: Boolean? = null,

                             @Column(name = "Nombre_Departamento")
                            var Nombre_Departamento: String? = null,

                             @Column(name = "Nombre_Ciudad")
                            var Nombre_Ciudad: String? = null,

                             @Column(name = "Nombre_Unidad_Medida")
                            var Nombre_Unidad_Medida: String? = null,

                             @Column(getterName = "getEstado_Sincronizacion")
                            var Estado_Sincronizacion: Boolean? = false

) {
    override fun toString(): String {
        return nombre!!
    }
}



