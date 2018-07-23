package com.interedes.agriculturappv3.modules.ofertas

import android.util.Base64
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.Notification.FcmNotificationBuilder
import com.interedes.agriculturappv3.modules.models.chat.Room
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.lote.Lote_Table
import com.interedes.agriculturappv3.modules.models.ofertas.*
import com.interedes.agriculturappv3.modules.models.produccion.Produccion_Table
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.producto.Producto_Table
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva_Table
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.modules.ofertas.events.OfertasEvent
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.listas.Listas
import com.interedes.agriculturappv3.services.resources.*
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class OfertasRepository : IOfertas.Repository {

    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null

     var mUserDBRef: DatabaseReference? = null
     var mRoomDBRef: DatabaseReference? = null

    private var mReceiverId: String? = null
    private var mSenderId: String? = null

    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()

        mUserDBRef = Chat_Resources.mUserDBRef
        mRoomDBRef = Chat_Resources.mRoomDBRef
    }

    override fun getUserLogued():Usuario?{
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    //region Métodos Interfaz
    override fun getListas() {
        val usuario=getLastUserLogued()

        val listUnidadProductiva: List<Unidad_Productiva> = SQLite.select().from(Unidad_Productiva::class.java)
                .where(Unidad_Productiva_Table.UsuarioId.eq(usuario?.Id))
                .queryList()

        val listLotes = SQLite.select().from(Lote::class.java)
                .where(Lote_Table.UsuarioId.eq(usuario?.Id))
                .queryList()


        val listCultivos = SQLite.select().from(Cultivo::class.java!!)
                .where(Cultivo_Table.UsuarioId.eq(usuario?.Id))
                .queryList()

        val listProductos = SQLite.select()
                .from(Producto::class.java)
                .where(Producto_Table.userId.eq(usuario?.Id))
                .queryList()


        postEventListUnidadProductiva(OfertasEvent.LIST_EVENT_UP, listUnidadProductiva, null)
        postEventListLotes(OfertasEvent.LIST_EVENT_LOTE, listLotes, null)
        postEventListCultivos(OfertasEvent.LIST_EVENT_CULTIVO, listCultivos, null)
        postEventListProductos(OfertasEvent.LIST_EVENT_PRODUCTO, listProductos, null)
    }


    fun getLastUp(usuario:Usuario?): Unidad_Productiva? {

        if(usuario!=null){

            val lastUnidadProductiva = SQLite.select()
                    .from(Unidad_Productiva::class.java)
                    .where(Unidad_Productiva_Table.UsuarioId.eq(usuario?.Id)).orderBy(Unidad_Productiva_Table.Unidad_Productiva_Id, false)
                    .querySingle()
            return lastUnidadProductiva
        }else{
            val lastUnidadProductiva = SQLite.select()
                    .from(Unidad_Productiva::class.java)
                    .orderBy(Unidad_Productiva_Table.Unidad_Productiva_Id, false)
                    .querySingle()
            return lastUnidadProductiva

        }
    }

    fun getLastLote(usuario:Usuario?): Lote? {


        if(usuario!=null){
            val lastLote = SQLite.select().from(Lote::class.java)
                    .where(Lote_Table.UsuarioId.eq(usuario?.Id))
                    .orderBy(Lote_Table.LoteId, false).querySingle()
            return lastLote


        }else{
            val lastLote = SQLite.select().from(Lote::class.java)
                    .orderBy(Lote_Table.LoteId, false).querySingle()
            return lastLote

        }

    }

    fun getLastCultivo(usuario:Usuario?): Cultivo? {
        if(usuario!=null){
            val lastCultivo = SQLite.select().from(Cultivo::class.java)
                    .where(Cultivo_Table.UsuarioId.eq(usuario?.Id))
                    .orderBy(Cultivo_Table.CultivoId, false).querySingle()
            return lastCultivo

        }else{
            val lastCultivo = SQLite.select().from(Cultivo::class.java)
                    .orderBy(Cultivo_Table.CultivoId, false).querySingle()
            return lastCultivo
        }
    }

    fun getLastOferta(): Oferta? {
        val lastOferta = SQLite.select().from(Oferta::class.java).where().orderBy(Oferta_Table.Oferta_Id, false).querySingle()
        return lastOferta
    }

    fun getLastDetalleOferta(): DetalleOferta? {
        val lastDetalleOferta = SQLite.select().from(DetalleOferta::class.java).where().orderBy(DetalleOferta_Table.Detalle_Oferta_Id, false).querySingle()
        return lastDetalleOferta
    }

    fun getLastProducto(usuario:Usuario?): Producto? {

        if(usuario!=null){
            val lastProducto = SQLite.select().from(Producto::class.java)
                    .where(Producto_Table.userId.eq(usuario?.Id)).orderBy(Producto_Table.ProductoId, false).querySingle()
            return lastProducto

        }else{
            val lastProducto = SQLite.select().from(Producto::class.java)
                    .orderBy(Producto_Table.ProductoId, false).querySingle()
            return lastProducto
        }



    }

    override fun getListOfertas(productoId: Long?,checkConection: Boolean) {
        /*val list_static_ofertas = Listas.listStaticOfertas()
        for (item in list_static_ofertas) {
            item.save()
        }*/
        //TODO con onexion a internet
        if(checkConection){
            val usuario= getLastUserLogued()

            var queryOfertas = ""
            var callOfertas: Call<OfertaResponse>? = null
            val orderDEsc = Listas.queryOrderByDesc("Id")
            if (usuario?.RolNombre.equals(RolResources.COMPRADOR)) {
                queryOfertas = Listas.queryGeneralWithContains("Oferta","UsuarioId", usuario?.Id.toString())
                callOfertas = apiService?.getOfertasComprador(queryOfertas, orderDEsc)
            } else {
                queryOfertas = Listas.queryGeneralWithContains("Oferta","usuarioto", usuario?.Id.toString())
                callOfertas = apiService?.getOfertasProductor(queryOfertas, orderDEsc)
            }

            callOfertas?.enqueue(object : Callback<OfertaResponse> {
                override fun onResponse(call: Call<OfertaResponse>?, response: Response<OfertaResponse>?) {
                    if (response != null && response.code() == 200) {
                        if (usuario?.RolNombre.equals(RolResources.PRODUCTOR)) {
                            val listOferta = SQLite.select().from(Oferta::class.java).where(Oferta_Table.UsuarioTo.eq(usuario?.Id)).queryList()
                            for (oferta in listOferta) {
                                SQLite.delete<DetalleOferta>(DetalleOferta::class.java)
                                        .where(DetalleOferta_Table.OfertasId.eq(oferta.Oferta_Id))
                                        .async()
                                        .execute()
                                oferta.delete()
                            }
                        }else{
                            SQLite.delete<Oferta>(Oferta::class.java)
                                    .async()
                                    .execute()

                            SQLite.delete<DetalleOferta>(DetalleOferta::class.java)
                                    .async()
                                    .execute()
                        }


                        val detalleOfertas = response.body()?.value as MutableList<DetalleOferta>
                        for (detalleoferta in detalleOfertas) {
                            val ofertaVerficateSave = SQLite.select()
                                    .from(Oferta::class.java)
                                    .where(Oferta_Table.Id_Remote.eq(detalleoferta.Oferta?.Id_Remote))
                                    .querySingle()

                            //TODO Verifica si tiene pendiente actualizacion por sincronizar
                            if (ofertaVerficateSave != null) {
                                detalleoferta.Oferta?.Oferta_Id = ofertaVerficateSave.Oferta_Id
                            } else {
                                val last_oferta = getLastOferta()
                                if (last_oferta == null) {
                                    detalleoferta.Oferta?.Oferta_Id = 1
                                } else {
                                    detalleoferta.Oferta?.Oferta_Id = last_oferta.Oferta_Id!! + 1
                                }
                            }
                            detalleoferta.Oferta!!.CreatedOnLocal = detalleoferta.Oferta!!.getFechaDate(detalleoferta.Oferta!!.CreatedOn)
                            detalleoferta.Oferta!!.UpdatedOnLocal = detalleoferta.Oferta!!.getFechaDate(detalleoferta.Oferta!!.UpdatedOn)
                            detalleoferta.Oferta!!.Nombre_Estado_Oferta = if (detalleoferta.Oferta!!.Estado_Oferta != null) detalleoferta.Oferta!!.Estado_Oferta?.Nombre else null
                            detalleoferta.Oferta!!.save()


                            val detalleOfertaVerficateSave = SQLite.select()
                                    .from(DetalleOferta::class.java)
                                    .where(DetalleOferta_Table.Id_Remote.eq(detalleoferta.Id_Remote))
                                    .querySingle()
                            if (detalleOfertaVerficateSave != null) {
                                detalleoferta.Detalle_Oferta_Id = detalleOfertaVerficateSave.Detalle_Oferta_Id
                            } else {
                                val last_detalle_oferta = getLastDetalleOferta()
                                if (last_detalle_oferta == null) {
                                    detalleoferta?.Detalle_Oferta_Id = 1
                                } else {
                                    detalleoferta?.Detalle_Oferta_Id = last_detalle_oferta.Detalle_Oferta_Id!! + 1
                                }
                            }
                            detalleoferta.NombreUnidadMedidaPrecio = if (detalleoferta.UnidadMedida != null) detalleoferta.UnidadMedida?.Descripcion else null
                            detalleoferta.OfertasId = detalleoferta.Oferta?.Oferta_Id
                            detalleoferta.Detalle_Oferta_Id = detalleOfertaVerficateSave?.Detalle_Oferta_Id
                            detalleoferta.save()

                            if (usuario?.RolNombre.equals(RolResources.COMPRADOR)) {
                                //UserTo
                                val usuario = detalleoferta?.Producto?.Cultivo?.Lote?.UnidadProductiva?.Usuario
                                if (usuario != null) {
                                    usuario.save()
                                }

                                //TODO Unidades Productivas
                                val unidaProductiva = detalleoferta?.Producto?.Cultivo?.Lote?.UnidadProductiva
                                if (unidaProductiva != null) {
                                    var unidadProductivaVerficateSave = SQLite.select()
                                            .from(Unidad_Productiva::class.java)
                                            .where(Unidad_Productiva_Table.Id_Remote.eq(unidaProductiva.Id_Remote))
                                            .querySingle()
                                    //TODO Verifica si ya existe
                                    if (unidadProductivaVerficateSave != null) {
                                        unidaProductiva.Unidad_Productiva_Id = unidadProductivaVerficateSave?.Unidad_Productiva_Id
                                    } else {
                                        val last_up = getLastUp(null)
                                        if (last_up == null) {
                                            unidaProductiva.Unidad_Productiva_Id = 1
                                        } else {
                                            unidaProductiva.Unidad_Productiva_Id = last_up.Unidad_Productiva_Id!! + 1
                                        }
                                    }
                                    unidaProductiva.UsuarioId = usuario?.Id
                                    unidaProductiva.Nombre_Ciudad = if (unidaProductiva.Ciudad != null) unidaProductiva.Ciudad?.Nombre else null
                                    unidaProductiva.Nombre_Unidad_Medida = if (unidaProductiva.UnidadMedida != null) unidaProductiva.UnidadMedida?.Descripcion else null
                                    unidaProductiva.Nombre_Departamento = if (unidaProductiva.Ciudad != null) unidaProductiva.Ciudad?.Departamento?.Nombre else null
                                    unidaProductiva.save()
                                }


                                //TODO Lote
                                val lote = detalleoferta?.Producto?.Cultivo?.Lote
                                if (lote != null) {
                                    val loteVerficateSave = SQLite.select()
                                            .from(Lote::class.java)
                                            .where(Lote_Table.Id_Remote.eq(lote.Id_Remote))
                                            .querySingle()
                                    //TODO Verifica si ya existe
                                    if (loteVerficateSave != null) {
                                        lote.LoteId = loteVerficateSave.LoteId
                                    } else {
                                        val last_lote = getLastLote(null)
                                        if (last_lote == null) {
                                            lote.LoteId = 1
                                        } else {
                                            lote.LoteId = last_lote.LoteId!! + 1
                                        }
                                    }

                                    val coordenadas = lote.Localizacion
                                    if (coordenadas != null || coordenadas != "") {
                                        val separated = coordenadas?.split("/".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                                        val latitud = separated!![0].toDoubleOrNull() // this will contain "Fruit"
                                        val longitud = separated!![1].toDoubleOrNull() // this will contain " they taste good"
                                        lote.Latitud = latitud
                                        lote.Longitud = longitud
                                        lote.Coordenadas = coordenadas
                                    }

                                    lote.Unidad_Productiva_Id = unidaProductiva?.Unidad_Productiva_Id
                                    //lote.Nombre_Unidad_Medida= if (lote.UnidadMedida!=null) lote.UnidadMedida?.Descripcion else null
                                    lote.Nombre_Unidad_Productiva = unidaProductiva?.nombre
                                    lote.Nombre = if (lote.Nombre == null) "" else lote.Nombre
                                    lote.Descripcion = if (lote.Descripcion == null) "" else lote.Descripcion
                                    // lote.EstadoSincronizacion=true
                                    // lote.Estado_SincronizacionUpdate=true
                                    lote.save()
                                }

                                //TODO Cultivo
                                val cultivo = detalleoferta?.Producto?.Cultivo
                                if (cultivo != null) {
                                    val cultivoVerficateSave = SQLite.select()
                                            .from(Cultivo::class.java)
                                            .where(Cultivo_Table.Id_Remote.eq(cultivo?.Id_Remote))
                                            .querySingle()
                                    //TODO Verifica si tiene pendiente actualizacion por sincronizar
                                    if (cultivoVerficateSave != null) {
                                        cultivo.CultivoId = cultivoVerficateSave.CultivoId
                                    } else {
                                        val last_cultivo = getLastCultivo(null)
                                        if (last_cultivo == null) {
                                            cultivo.CultivoId = 1
                                        } else {
                                            cultivo.CultivoId = last_cultivo.CultivoId!! + 1
                                        }
                                    }

                                    cultivo.LoteId = lote?.LoteId
                                    cultivo.NombreUnidadProductiva = unidaProductiva?.nombre
                                    cultivo.NombreLote = lote?.Nombre
                                    cultivo.EstadoSincronizacion = true
                                    cultivo.Estado_SincronizacionUpdate = true
                                    cultivo.stringFechaInicio = cultivo.getFechaStringFormatApi(cultivo.FechaIncio)
                                    cultivo.stringFechaFin = cultivo.getFechaStringFormatApi(cultivo.FechaFin)

                                    //cultivo.Nombre_Tipo_Producto= if (detalleTipoProducto.tipoProducto!=null) detalleTipoProducto?.tipoProducto?.Nombre else null
                                    //cultivo.Nombre_Detalle_Tipo_Producto=detalleTipoProducto.Nombre
                                    //cultivo.Id_Tipo_Producto= detalleTipoProducto.TipoProductoId
                                    //cultivo.Nombre_Unidad_Medida=if (cultivo.unidadMedida!=null) cultivo.unidadMedida?.Descripcion else null
                                    //cultivo.EstadoSincronizacion=true
                                    //cultivo.Estado_SincronizacionUpdate=true
                                    cultivo.save()
                                }

                                //TODO Producto
                                val producto = detalleoferta?.Producto
                                if (producto != null) {
                                    val productoVerficateSave = SQLite.select()
                                            .from(Producto::class.java)
                                            .where(Producto_Table.Id_Remote.eq(producto.Id_Remote))
                                            .querySingle()

                                    if (productoVerficateSave != null) {
                                        producto.ProductoId = productoVerficateSave.ProductoId
                                    } else {
                                        val last_producto = getLastProducto(null)
                                        if (last_producto == null) {
                                            producto.ProductoId = 1
                                        } else {
                                            producto.ProductoId = last_producto.ProductoId!! + 1
                                        }
                                    }
                                    producto.EmailProductor = usuario?.Email
                                    producto.NombreProductor = "${usuario?.Nombre} ${usuario?.Apellidos}"
                                    producto.CodigoUp = unidaProductiva?.Unidad_Productiva_Id.toString()
                                    producto.Ciudad = unidaProductiva?.Nombre_Ciudad
                                    producto.Departamento = unidaProductiva?.Nombre_Departamento
                                    //producto.TipoProductoId=detalleTipoProducto.TipoProductoId
                                    producto.NombreCultivo = cultivo?.Nombre
                                    producto.NombreLote = if (lote != null) lote.Nombre else null
                                    producto.NombreUnidadProductiva = if (unidaProductiva != null) unidaProductiva.nombre else null
                                    producto.NombreUnidadMedidaCantidad = if (producto.UnidadMedida != null) producto.UnidadMedida?.Descripcion else null
                                    producto.NombreCalidad = if (producto.Calidad != null) producto.Calidad?.Nombre else null
                                    producto.NombreUnidadMedidaPrecio = producto.PrecioUnidadMedida
                                    producto.Usuario_Logued = usuario?.Id
                                    //producto.NombreDetalleTipoProducto=detalleTipoProducto.Nombre
                                    //producto.Estado_Sincronizacion=true
                                    //producto.Estado_SincronizacionUpdate=true
                                    try {
                                        val base64String = producto?.Imagen
                                        val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                                        val byte = Base64.decode(base64Image, Base64.DEFAULT)
                                        producto.blobImagen = Blob(byte)
                                    } catch (ex: Exception) {
                                        var ss = ex.toString()
                                        Log.d("Convert Image", "defaultValue = " + ss);
                                    }
                                    producto.save()
                                }
                            } else if (usuario?.RolNombre.equals(RolResources.PRODUCTOR)) {
                                if (detalleoferta.Oferta?.Usuario != null) {
                                    /*if(oferta.Usuario?.Fotopefil!=null){
                                        try {
                                            val base64String = oferta.Usuario?.Fotopefil
                                            val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                                            val byte = Base64.decode(base64Image, Base64.DEFAULT)
                                            oferta.Usuario?.blobImagenUser = Blob(byte)
                                        }catch (ex:Exception){
                                            var ss= ex.toString()
                                            Log.d("Convert Image", "defaultValue = " + ss);
                                        }
                                    }*/
                                    detalleoferta.Oferta?.Usuario!!.save()
                                }
                            }
                        }

                        val listaOfertas = getOfertas(productoId)
                        postEventOk(OfertasEvent.READ_EVENT, listaOfertas, null)
                    } else {
                        postEventError(OfertasEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
                    }
                }
                override fun onFailure(call: Call<OfertaResponse>?, t: Throwable?) {
                    postEventError(OfertasEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            })
        }

        //TODO Sin conexion a internet
        else{
            val listaOfertas = getOfertas(productoId)
            postEventOk(OfertasEvent.READ_EVENT, listaOfertas, null)
        }

    }

    override fun getOfertas(productoId: Long?): List<Oferta> {
        val usuario= getLastUserLogued();
        val listResponse= ArrayList<Oferta>()
        if(usuario?.RolNombre.equals(RolResources.COMPRADOR)){

            if (productoId == null) {
                val ofertaResponse = SQLite.select()
                        .from(Oferta::class.java)
                        .where(Oferta_Table.UsuarioId.eq(usuario?.Id))
                        .queryList()

                for (oferta in ofertaResponse){
                    val usuario= SQLite.select().from(Usuario::class.java).where(Usuario_Table.Id.eq(oferta.UsuarioTo)).querySingle()
                    if(usuario!=null){
                        oferta.Usuario=usuario
                    }

                    val detalleOferta= SQLite.select().from(DetalleOferta::class.java).where(DetalleOferta_Table.OfertasId.eq(oferta.Oferta_Id)).querySingle()
                    if(detalleOferta!=null){
                        oferta.DetalleOfertaSingle=detalleOferta
                        val producto=SQLite.select().from(Producto::class.java).where(Producto_Table.Id_Remote.eq(detalleOferta.ProductoId)).querySingle()
                        if(producto!=null){
                            oferta.Producto=producto
                        }
                    }
                    listResponse.add(oferta)
                }
            } else {


                val ofertaResponse = SQLite.select().from(Oferta::class.java)
                        .where(Oferta_Table.ProductoId.eq(productoId))
                        .and(Oferta_Table.UsuarioTo.eq(usuario?.Id))
                        .queryList()
                for (oferta in ofertaResponse){
                    val usuario= SQLite.select().from(Usuario::class.java).where(Usuario_Table.Id.eq(oferta.UsuarioTo)).querySingle()
                    if(usuario!=null){
                        oferta.Usuario=usuario
                    }
                    val detalleOferta= SQLite.select().from(DetalleOferta::class.java).where(DetalleOferta_Table.OfertasId.eq(oferta.Oferta_Id)).querySingle()
                    if(detalleOferta!=null){
                        oferta.DetalleOfertaSingle=detalleOferta
                        val producto=SQLite.select().from(Producto::class.java).where(Producto_Table.Id_Remote.eq(detalleOferta.ProductoId)).querySingle()
                        if(producto!=null){
                            oferta.Producto=producto
                        }
                    }
                    listResponse.add(oferta)
                }
            }

        }else if(usuario?.RolNombre.equals(RolResources.PRODUCTOR)){
            if (productoId == null) {
                var list = SQLite.select()
                        .from(Oferta::class.java)
                        .queryList()

                val ofertaResponse = SQLite.select()
                        .from(Oferta::class.java)
                        .where(Oferta_Table.UsuarioTo.eq(usuario?.Id))
                        .queryList()


                for (oferta in ofertaResponse){
                    val usuario= SQLite.select().from(Usuario::class.java).where(Usuario_Table.Id.eq(oferta.UsuarioId)).querySingle()
                    if(usuario!=null){
                        oferta.Usuario=usuario
                    }
                    val detalleOferta= SQLite.select().from(DetalleOferta::class.java).where(DetalleOferta_Table.OfertasId.eq(oferta.Oferta_Id)).querySingle()
                    if(detalleOferta!=null){
                        oferta.DetalleOfertaSingle=detalleOferta
                        val producto=SQLite.select().from(Producto::class.java).where(Producto_Table.Id_Remote.eq(detalleOferta.ProductoId)).querySingle()
                        if(producto!=null){
                            oferta.Producto=producto
                        }
                    }
                    listResponse.add(oferta)
                }
            } else {
                val ofertaResponse = SQLite.select().from(Oferta::class.java)
                        .where(Oferta_Table.ProductoId.eq(productoId))
                        .and(Oferta_Table.UsuarioTo.eq(usuario?.Id))
                        .queryList()
                for (oferta in ofertaResponse){
                    val usuario= SQLite.select().from(Usuario::class.java).where(Usuario_Table.Id.eq(oferta.UsuarioId)).querySingle()
                    if(usuario!=null){
                        oferta.Usuario=usuario
                    }
                    val detalleOferta= SQLite.select().from(DetalleOferta::class.java).where(DetalleOferta_Table.OfertasId.eq(oferta.Oferta_Id)).querySingle()
                    if(detalleOferta!=null){
                        oferta.DetalleOfertaSingle=detalleOferta
                        val producto=SQLite.select().from(Producto::class.java).where(Producto_Table.Id_Remote.eq(detalleOferta.ProductoId)).querySingle()
                        if(producto!=null){
                            oferta.Producto=producto
                        }
                    }
                    listResponse.add(oferta)
                }
            }

        }

        return listResponse;
    }

    /*
    override fun navigationChat(oferta: Oferta,checkConection: Boolean) {

        if(checkConection){

            val userLogued= getLastUserLogued()
            var usuario:Usuario?=null
            if(userLogued?.RolNombre.equals(RolResources.COMPRADOR)){
                usuario= SQLite.select().from(Usuario::class.java).where(Usuario_Table.Id.eq(oferta.UsuarioTo)).querySingle()
            }else{
                usuario= SQLite.select().from(Usuario::class.java).where(Usuario_Table.Id.eq(oferta.UsuarioId)).querySingle()
            }


            sendMessageNotificationUser(usuario,oferta,true)
        }else{

            val userLogued= getLastUserLogued()

            var usuario:Usuario?=null
            if(userLogued?.RolNombre.equals(RolResources.COMPRADOR)){
                 usuario= SQLite.select().from(Usuario::class.java).where(Usuario_Table.Id.eq(oferta.UsuarioTo)).querySingle()
            }else{
                 usuario= SQLite.select().from(Usuario::class.java).where(Usuario_Table.Id.eq(oferta.UsuarioId)).querySingle()
            }

            postEventNavigationToChatSMS(OfertasEvent.NAVIGATION_CHAT_SMS,usuario)
        }
    }
    */

    override fun updateOferta(oferta: Oferta,productoId:Long?,checkConection:Boolean){
        //TODO si existe coneccion a internet
        if(checkConection){
            //TODO se valida estado de sincronizacion  para actualizar
            val postOferta = PostOferta(
                    oferta.Id_Remote,
                    oferta.CreatedOn,
                    oferta.EstadoOfertaId,
                    oferta.UpdatedOn,
                    oferta.UsuarioId,
                    oferta.UsuarioTo
            )
                val call = apiService?.updateOferta(postOferta, oferta.Id_Remote!!)
                call?.enqueue(object : Callback<PostOferta> {
                    override fun onResponse(call: Call<PostOferta>?, response: Response<PostOferta>?) {
                        if (response != null && response.code() == 200) {
                            oferta.update()
                            val usuario= SQLite.select().from(Usuario::class.java).where(Usuario_Table.Id.eq(oferta.UsuarioId)).querySingle()
                            sendMessageNotificationUser(usuario,oferta)
                            postEventOk(OfertasEvent.UPDATE_EVENT, getOfertas(productoId), oferta)
                        } else {
                            postEventError(OfertasEvent.ERROR_EVENT, "Comprueba tu conexión")
                        }
                    }
                    override fun onFailure(call: Call<PostOferta>?, t: Throwable?) {
                        postEventError(OfertasEvent.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })
        }
        //TODO sin conexion a internet
        else{
            postEventError(OfertasEvent.ERROR_VERIFICATE_CONECTION, null)
        }
    }


    private fun sendMessageNotificationUser(usuarioFrom: Usuario?,oferta:Oferta?) {
        if(usuarioFrom!=null){
            val query = mUserDBRef?.orderByChild("correo")?.equalTo(usuarioFrom.Email)
            query?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // dataSnapshot is the "issue" node with all children with id 0
                        var user: UserFirebase?=null
                        for (issue in dataSnapshot.children) {
                            // do something with the individual "issues"
                            user = issue.getValue<UserFirebase>(UserFirebase::class.java)


                            val userLogued= getLastUserLogued()
                            if(userLogued?.RolNombre.equals(RolResources.COMPRADOR)){
                                mReceiverId=user?.User_Id
                                mSenderId=FirebaseAuth.getInstance().currentUser!!.uid

                            }else{
                                mReceiverId=FirebaseAuth.getInstance().currentUser!!.uid
                                mSenderId= user?.User_Id
                            }
                            //if not current user, as we do not want to show ourselves then chat with ourselves lol
                        }
                        findRoomUser(user!!,oferta)
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    //var error = databaseError.message
                    //postEventError(OfertasEvent.ERROR_EVENT, error)
                    //postEvent(RequestEventDetalleProducto.OK_SEND_EVENT_OFERTA, null, null,null)
                }
            })
        }
    }

    private fun findRoomUser(userFirebase: UserFirebase,oferta:Oferta?) {
        //val query = mRoomDBRef?.child("/"+mCompradorSenderId+"/"+mProductorReceiverId)
        val query = mRoomDBRef?.child(mSenderId)?.orderByChild("user_To")?.equalTo(Chat_Resources.getRoomById(mReceiverId))
        query?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    //var room = dataSnapshot.value as HashMap<*, *>
                    //roomId=room.get("idRoom") as String;
                    var roomFind= Room()
                    for (issue in dataSnapshot.children) {
                        val room = issue.getValue<Room>(Room::class.java)
                        //roomId=room?.IdRoom
                        roomFind= room!!
                    }
                    val message=getMessageOferta(oferta!!,userFirebase)
                    val producto =SQLite.select().from(Producto::class.java).where(Producto_Table.Id_Remote.eq(oferta.ProductoId)).querySingle()
                    sendPushNotificationToReceiver(message,userFirebase,producto?.Imagen,roomFind,oferta!!)
                   /* if(navigateChat){
                        postEventNavigationToChat(OfertasEvent.NAVIGATION_CHAT_ONLINE,roomFind,userFirebase)
                    }else{
                    }*/
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                //val error = databaseError.message
                //postEventError(OfertasEvent.ERROR_EVENT, error)
            }
        })
    }


    private fun sendPushNotificationToReceiver(message: String,userSelected:UserFirebase,imagen:String?,room:Room,oferta:Oferta) {
        var NOTIFICATION_TYPE_CONFIRM_OFERTA=""
        if(oferta.Nombre_Estado_Oferta.equals(EstadosOfertasResources.RECHAZADO_STRING)){
            NOTIFICATION_TYPE_CONFIRM_OFERTA=NotificationTypeResources.NOTIFICATION_TYPE_REFUSED_OFERTA
        }else if(oferta.Nombre_Estado_Oferta.equals(EstadosOfertasResources.CONFIRMADO_STRING)){
            NOTIFICATION_TYPE_CONFIRM_OFERTA=NotificationTypeResources.NOTIFICATION_TYPE_CONFIRM_OFERTA
        }
        val fcmNotificationBuilder= FcmNotificationBuilder()
        fcmNotificationBuilder.title=userSelected.Nombre+" ${userSelected.Apellido}"
        fcmNotificationBuilder.image_url=imagen
        fcmNotificationBuilder.message=message
        fcmNotificationBuilder.user_name=userSelected.Nombre+" ${userSelected.Apellido}"
        fcmNotificationBuilder.ui=userSelected.User_Id
        fcmNotificationBuilder.receiver_firebase_token=userSelected.TokenFcm
        fcmNotificationBuilder.room_id=room.IdRoom
        fcmNotificationBuilder.type_notification= NOTIFICATION_TYPE_CONFIRM_OFERTA
        fcmNotificationBuilder.send()
    }


    private fun getMessageOferta(oferta: Oferta,userFirebase: UserFirebase): String {

        var message= ""
        var disponibilidad = ""
        var productoCantidad=""
        var calidad=""

        //val producto =SQLite.select().from(Producto::class.java).where(Producto_Table.Id_Remote.eq(oferta.ProductoId)).querySingle()

        if(oferta.DetalleOfertaSingle!=null){
            if (oferta.DetalleOfertaSingle?.Cantidad.toString().contains(".0")) {
                disponibilidad = String.format("%.0f",
                        oferta.DetalleOfertaSingle?.Cantidad)
            } else {
                disponibilidad = oferta.DetalleOfertaSingle?.Cantidad.toString()
            }
        }

        if(oferta.Producto!=null){
            productoCantidad= String.format("%s %s ", disponibilidad, oferta.Producto?.NombreUnidadMedidaCantidad)
            calidad= String.format("%s",oferta.Producto?.NombreCalidad)
        }

        message=String.format("El productor %s %s ha  %s tu oferta de %s de  %s de %s"
                ,userFirebase.Nombre,userFirebase.Apellido,oferta.Nombre_Estado_Oferta,productoCantidad, oferta.Producto?.Nombre,calidad)

        return  message
    }

    override fun getProducto(productoId: Long?) {
        val producto = SQLite.select().from(Producto::class.java).where(Producto_Table.ProductoId.eq(productoId)).querySingle()
        postEventOkProducto(OfertasEvent.GET_EVENT_PRODUCTO, producto)
    }

    fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }


    //endregion

    //region Events

    private fun postEventNavigationToChatSMS(type: Int, usuario: Usuario?) {
        var usuarioMutable: Object? = null
        if (usuario!= null) {
            usuarioMutable = usuario as Object
        }


        postEvent(type, null, usuarioMutable, null,null)
    }

    private fun postEventNavigationToChat(type: Int, room: Room?,userFirebase: UserFirebase?) {
        var roomMutable: Object? = null
        if (room != null) {
            roomMutable = room as Object
        }
        var userFirebaseMutable: Object? = null
        if (userFirebase != null) {
            userFirebaseMutable = userFirebase as Object
        }

        postEvent(type, null, roomMutable, userFirebaseMutable,null)
    }

    private fun postEventOkProducto(type: Int, producto: Producto?) {
        var productoMutable: Object? = null
        if (producto != null) {
            productoMutable = producto as Object
        }
        postEvent(type, null, productoMutable, null,null)
    }

    private fun postEventListUnidadProductiva(type: Int, listUp: List<Unidad_Productiva>?, messageError: String?) {
        val upMutable = listUp as MutableList<Object>
        postEvent(type, upMutable, null,null, messageError)
    }

    private fun postEventListLotes(type: Int, listLote: List<Lote>?, messageError: String?) {
        val loteMutable = listLote as MutableList<Object>
        postEvent(type, loteMutable, null, null,messageError)
    }

    private fun postEventListCultivos(type: Int, listCultivo: List<Cultivo>?, messageError: String?) {
        val cultivoMutable = listCultivo as MutableList<Object>
        postEvent(type, cultivoMutable, null,null, messageError)
    }

    private fun postEventListProductos(type: Int, listProductos: List<Producto>?, messageError: String?) {
        val productoMutable = listProductos as MutableList<Object>
        postEvent(type, productoMutable, null,null, messageError)
    }

    private fun postEventOk(type: Int, ofertas: List<Oferta>?, oferta: Oferta?) {
        var ofertaListMitable = ofertas as MutableList<Object>
        var ofertaMutable: Object? = null
        if (oferta != null) {
            ofertaMutable = oferta as Object
        }
        postEvent(type, ofertaListMitable, ofertaMutable, null,null)
    }

    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,null,messageError)
    }


    //Main Post Event
    private fun postEvent(type: Int, listModel1: MutableList<Object>?, model: Object?,model2:Object?, errorMessage: String?) {
        val event = OfertasEvent(type, listModel1, model,model2, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
}