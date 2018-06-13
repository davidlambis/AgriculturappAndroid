package com.interedes.agriculturappv3.modules.models.producto

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.data.Blob
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


@Table(database = DataSource::class)
data class Producto(@PrimaryKey

                    @Column(name = "ProductoId")
                    var ProductoId: Long? = 0,

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

                    @SerializedName("PrecioUnidadMedida")
                    @Column(name = "PrecioUnidadMedida")
                    var PrecioUnidadMedida: String? = null,

                    @SerializedName("cultivoId")
                    @Column(name = "cultivoId")
                    var cultivoId: Long? = null,

                    @SerializedName("UnidadMedidaId")
                    @Column(name = "Unidad_Medida_Id")
                    var Unidad_Medida_Id: Long? = null,

                    @SerializedName("nombre")
                    @Column(name = "Nombre")
                    var Nombre: String? = null,

                    @Column(name = "blobImagen")
                    var blobImagen: Blob? = null,

                    @Column(name = "NombreUnidadMedidaCantidad")
                    var NombreUnidadMedidaCantidad: String? = null,

                    @Column(name = "NombreUnidadMedidaPrecio")
                    var NombreUnidadMedidaPrecio: String? = null,

                    @Column(name = "NombreUnidadProductiva")
                    var NombreUnidadProductiva: String? = null,

                    @Column(name = "NombreLote")
                    var NombreLote: String? = null,

                    @Column(name = "NombreCultivo")
                    var NombreCultivo: String? = null,

                    @Column(name = "NombreDetalleTipoProducto")
                    var NombreDetalleTipoProducto: String? = null,

                    @Column(name = "NombreCalidad")
                    var NombreCalidad: String? = null,

                    @SerializedName("userId")
                    @Column(name = "userId")
                    var userId: UUID? = null,


                    @Column(name = "Usuario_Logued")
                    var Usuario_Logued: UUID? = null,


                    @SerializedName("Id")
                    @Column(name = "Id_Remote")
                    var Id_Remote: Long? = 0,


                    @Column(name = "TipoProductoId")
                    var TipoProductoId: Long? = 0,


                    @Column(name = "Ciudad")
                    var Ciudad: String? = null,

                    @Column(name = "Depatamento")
                    var Departamento: String? = null,

                    @Column(name = "NombreProductor")
                    var NombreProductor: String? = null,

                    @Column(name = "EmailProductor")
                    var EmailProductor: String? = null,

                    @Column(name = "TelefonoProductor")
                    var TelefonoProductor: String? = null,


                    @Column(getterName = "getEstado_Sincronizacion")
                    var Estado_Sincronizacion: Boolean? = false,

                    @Column(getterName = "getEstado_SincronizacionUpdate")
                    var Estado_SincronizacionUpdate: Boolean? = false,

                    @SerializedName("Cultivo")
                    var Cultivo:Cultivo?=null,

                    @SerializedName("UnidadMedida")
                    var UnidadMedida:Unidad_Medida?=null,

                    @SerializedName("Calidad")
                    var Calidad:CalidadProducto?=null

) {

    override fun toString(): String {
        return Nombre!!
    }

    fun getFechaLimiteDisponibilidadFormat(): String {

        try {

            val dateString = FechaLimiteDisponibilidad
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(dateString)
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return sdf.format(convertedDate)
        }catch (ex:Exception){
            // Log.println(ex.toString())
            return  ""
        }
    }


    fun getFechaLimiteDisponibilidadFormatApi(): String {

        try {
            val dateString = FechaLimiteDisponibilidad
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(dateString)
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(convertedDate)

        }catch (ex:Exception){
            // Log.println(ex.toString())

            return  ""
        }

    }


    fun getFechaLimiteDisponibilidadFormatDate(stringDate:String?):Date?{
        val dateString = stringDate
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        var convertedDate = Date()
        try {
            convertedDate = dateFormat.parse(dateString)
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return convertedDate
    }



}
