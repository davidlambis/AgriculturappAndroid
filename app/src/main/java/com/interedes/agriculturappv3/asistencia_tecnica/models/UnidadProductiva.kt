package com.interedes.agriculturappv3.asistencia_tecnica.models

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table


/**
 * Created by EnuarMunoz on 12/03/18.
 */


//@Table(database = DataSource::class)
@Table(database = DataSource::class)
data class UnidadProductiva(@PrimaryKey(autoincrement = true)
                            @SerializedName("Id")
                            @Column(name = "Id")
                            var Id: Long? = 0,

                            @SerializedName("Nombre")
                            @Column(name = "Nombre")
                            var Nombre: String? = null,

                            @SerializedName("Descripcion")
                            @Column(name = "Descripcion")
                            var Descripcion: String? = null,

                            @SerializedName("CiudadId")
                            @Column(name = "CiudadId")
                            var UpMpio: Long? = 0,

                            @SerializedName("Area")
                            @Column(name = "Area")
                            var UpArea: Double? = 0.0,


                            @SerializedName("UnidadMedidaId")
                            @Column(name = "UnidadMedidaId")
                            var UnidadMedidaId: Long? =null,

                            @SerializedName("Usuario_Id")
                            @Column(name = "Usuario_Id")
                            var Usuario_Id: Long? = 0,

                            //Geolocalizacion UnidadProductiva

                            @SerializedName("Coordenadas")
                            @Column(name = "Coordenadas")
                            var Coordenadas: String? = null,

                            @SerializedName("Direccion")
                            @Column(name = "Direccion")
                            var Direccion: String? = null,

                            @SerializedName("Latitud")
                            @Column(name = "Latitud")
                            var Latitud: Double? = 0.0,

                            @SerializedName("Longitud")
                            @Column(name = "Longitud")
                            var Longitud: Double? = 0.0,

                            @SerializedName("DireccionAproximadaGps")
                            @Column(name = "DireccionAproximadaGps")
                            var DireccionAproximadaGps: String? = null,

                            @SerializedName("Postal_Code_Gps")
                            @Column(name = "Postal_Code_Gps")
                            var Postal_Code_Gps: String? = null,

                            @Column(getterName = "getConfiguration_Point")
                            var Configuration_Point: Boolean? = null,

                            @Column(getterName = "getConfiguration_Poligon")
                            var Configuration_Poligon: Boolean? = null,


                            //Ubicacion UnidadProductiva
                            @Column(name = "Nombre_Departamento")
                            var Nombre_Departamento: String? = null,

                            @Column(name = "Nombre_Ciudad")
                            var Nombre_Ciudad: String? = null,


                            @Column(name = "Nombre_Unidad_Medida")
                            var Nombre_Unidad_Medida: String? =null




) {
    override fun toString(): String {
        return Nombre!!
    }

}


/*
Geocoder geocoder = new Geocoder(context, Locale.getDefault());
List<Address> addresses  = geocoder.getFromLocation(latitude,longitude, 1);
String city = addresses.get(0).getLocality();
String state = addresses.get(0).getAdminArea();
String zip = addresses.get(0).getPostalCode();
String country = addresses.get(0).getCountryName();
 */
