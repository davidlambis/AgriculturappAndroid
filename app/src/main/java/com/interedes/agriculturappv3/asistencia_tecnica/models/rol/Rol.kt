package com.interedes.agriculturappv3.asistencia_tecnica.models.rol

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.R.string.name
import com.interedes.agriculturappv3.config.DataSource
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel


/*data class Rol(val Nombre: String, var Imagen: Int) {}*/

/*
@Table(name = "Rol", database = DataSource::class)
class Rol : BaseModel() {

    @PrimaryKey
    @SerializedName("Id")
    @Column(name = "Id")
    var Id: Long? = 0

    @SerializedName("Nombre")
    @Column(name = "Nombre")
    var Nombre: String? = null

    @Column(name = "Imagen")
    var Imagen: Int? = 0

}*/
/*
class Rol{
    @SerializedName("Id")
    var Id: Long? = 0

    @SerializedName("Nombre")
    var Nombre: String? = null
}
*/

@Table(database = DataSource::class)
data class Rol(@PrimaryKey
               @SerializedName("Id")
               @Column(name = "Id")
               var Id: Long? = 0,

               @SerializedName("Nombre")
               @Column(name = "Nombre")
               var Nombre: String? = null,

               @Column(name = "Imagen")
               var Imagen: Int? = 0) {


}