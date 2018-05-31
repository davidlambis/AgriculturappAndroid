package com.interedes.agriculturappv3.modules.models.lote

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel
import java.util.*


@Table(name = "Lote", database = DataSource::class)


class Lote : BaseModel() {

    @PrimaryKey
    @Column(name = "LoteId")
    var LoteId: Long = 0

    @SerializedName("Area")
    @Column(name = "Area")
    var Area: Double? = null

    @SerializedName("Codigo")
    @Column(name = "Codigo")
    var Codigo: String? = null

    @SerializedName("Localizacion")
    @Column(name = "Localizacion")
    var Localizacion: String? = null

    @SerializedName("Localizacion_Poligono")
    @Column(name = "Localizacion_Poligono")
    var Localizacion_Poligono: String? = null

    @SerializedName("UnidadMedidaId")
    @Column(name = "Unidad_Medida_Id")
    var Unidad_Medida_Id: Long? = null

    @SerializedName("unidadproductivaId")
    @Column(name = "Unidad_Productiva_Id")
    var Unidad_Productiva_Id: Long? = null

    @SerializedName("Nombre")
    @Column(name = "Nombre")
    var Nombre: String? = null

    @SerializedName("Descripcion")
    @Column(name = "Descripcion")
    var Descripcion: String? = null

    //@SerializedName("Latitud")
    @Column(name = "Latitud")
    var Latitud: Double? = null

    //@SerializedName("Longitud")
    @Column(name = "Longitud")
    var Longitud: Double? = null

    // @SerializedName("Poligono_Lote")
    @Column(name = "Poligono_Lote")
    var Poligono_Lote: String? = null

    // @SerializedName("Coordenadas")
    @Column(name = "Coordenadas")
    var Coordenadas: String? = null

    @Column(name = "Nombre_Unidad_Productiva")
    var Nombre_Unidad_Productiva: String? = null

    @Column(name = "Nombre_Unidad_Medida")
    var Nombre_Unidad_Medida: String? = null

    @SerializedName("Id")
    @Column(name = "Id_Remote")
    var Id_Remote: Long? = 0

    @Column(name = "UsuarioId")
    var UsuarioId: UUID? = null

    @Column(getterName = "getEstadoSincronizacion")
    var EstadoSincronizacion: Boolean? = false

    @Column(getterName = "getEstado_SincronizacionUpdate")
    var Estado_SincronizacionUpdate: Boolean? = false

    @SerializedName("UnidadMedida")
    var UnidadMedida: Unidad_Medida?= null

    @SerializedName("Cultivos")
    var Cultivos: ArrayList<Cultivo>?= null

    @SerializedName("UnidadProductiva")
    var UnidadProductiva: Unidad_Productiva?=null

    override fun toString(): String {

        if(Nombre.equals("")){
            return Coordenadas!!
        }
        return Nombre!!
    }



}