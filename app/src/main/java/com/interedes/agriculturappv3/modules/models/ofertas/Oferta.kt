package com.interedes.agriculturappv3.modules.models.ofertas

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Table(database = DataSource::class)
data class Oferta(@PrimaryKey
                  @SerializedName("Oferta_Id")
                  @Column(name = "Oferta_Id")
                  var Oferta_Id: Long? = 0,



                  @SerializedName("EstadoOfertaId")
                  @Column(name = "EstadoOfertaId")
                  var EstadoOfertaId: Long? = 0,


                  @SerializedName("CreatedOnLocal")
                  @Column(name = "CreatedOnLocal")
                  var CreatedOnLocal: Date? = null,

                  @SerializedName("UpdatedOnLocal")
                  @Column(name = "UpdatedOnLocal")
                  var UpdatedOnLocal: Date? = null,


                  @SerializedName("CreatedOn")
                  @Column(name = "CreatedOn")
                  var CreatedOn: String? = null,

                  @SerializedName("UpdatedOn")
                  @Column(name = "UpdatedOn")
                  var UpdatedOn: String? = null,

                  @SerializedName("UsuarioId")
                  @Column(name = "UsuarioId")
                  var UsuarioId: UUID? = null,

                  @SerializedName("usuarioto")
                  @Column(name = "UsuarioTo")
                  var UsuarioTo: UUID? = null,

                  @SerializedName("Id")
                  @Column(name = "Id_Remote")
                  var Id_Remote: Long? = 0,


                  @Column(name = "Nombre_Estado_Oferta")
                  var Nombre_Estado_Oferta: String? = null,






                  //DetalleOferta
                  @Column(name = "CalidadId")
                  var CalidadId: Long? = 0,

                  @Column(name = "Cantidad")
                  var Cantidad: Double? = 0.0,

                  @Column(name = "ProductoId")
                  var ProductoId: Long? = 0,

                  @Column(name = "UnidadMedidaId")
                  var UnidadMedidaId: Long? = 0,

                  @Column(name = "Valor_Oferta")
                  var Valor_Oferta: Double? = 0.0,

                  @Column(name = "NombreUnidadMedidaPrecio")
                  var NombreUnidadMedidaPrecio: String? = null,


                  @SerializedName("DetalleOferta")
                  var DetalleOferta: ArrayList<DetalleOferta>?= null,

                  @SerializedName("EstadoOfertum")
                  var Estado_Oferta: EstadoOferta?= null,


                  var DetalleOfertaSingle: DetalleOferta?= null,

                  var Producto: Producto?= null,

                  @SerializedName("Usuario")
                    var Usuario: Usuario?= null

                  ) {

    fun getDateFormatApi(date:Date): String? {
        val format1 = SimpleDateFormat("yyyy-MM-dd")
        return format1.format(date)
    }

    fun getFechaDate(stringDate:String?):Date?{
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

    fun getCreatedOnFormat(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(CreatedOnLocal)
    }

    fun getUpdatedOnFormat(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(UpdatedOnLocal)
    }
}