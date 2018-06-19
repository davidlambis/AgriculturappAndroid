package com.interedes.agriculturappv3.modules.ofertas

import android.util.Base64
import android.util.Log
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
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
import com.interedes.agriculturappv3.services.resources.EstadosOfertasResources
import com.interedes.agriculturappv3.services.resources.RolResources
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class OfertasRepository : IOfertas.Repository {

    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null

    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }

    override fun getUserLogued():Usuario?{
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    //region Métodos Interfaz
    override fun getListas() {
        var usuario=getLastUserLogued()

        val listUnidadProductiva: List<Unidad_Productiva> = SQLite.select().from(Unidad_Productiva::class.java)
                .where(Unidad_Productiva_Table.UsuarioId.eq(usuario?.Id))
                .queryList()

        val listLotes = SQLite.select().from(Lote::class.java)
                .where(Lote_Table.UsuarioId.eq(usuario?.Id))
                .queryList()


        var listCultivos = SQLite.select().from(Cultivo::class.java!!)
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
            var usuario= getLastUserLogued()

            var queryOfertas =""
            var callOfertas:Call<OfertaResponse>?=null
            val orderDEsc=Listas.queryOrderByDesc("Id")
            if(usuario?.RolNombre.equals(RolResources.COMPRADOR)){
                queryOfertas = Listas.queryGeneral("UsuarioId",usuario?.Id.toString())
                callOfertas = apiService?.getOfertasComprador(queryOfertas,orderDEsc)
            }else {
                queryOfertas= Listas.queryGeneral("usuarioto",usuario?.Id.toString())
                callOfertas = apiService?.getOfertasProductor(queryOfertas,orderDEsc)
            }
            callOfertas?.enqueue(object : Callback<OfertaResponse> {
                override fun onResponse(call: Call<OfertaResponse>?, response: Response<OfertaResponse>?) {
                    if (response != null && response.code() == 200) {

                        if(usuario?.RolNombre.equals(RolResources.PRODUCTOR)){
                            var listOferta=SQLite.select().from(Oferta::class.java).where(Oferta_Table.UsuarioTo.eq(usuario?.Id)).queryList()
                            for (oferta in listOferta){
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


                        val ofertas = response.body()?.value as MutableList<Oferta>
                        for(oferta in ofertas){
                            var ofertaVerficateSave= SQLite.select()
                                    .from(Oferta::class.java)
                                    .where(Oferta_Table.Id_Remote.eq(oferta.Id_Remote))
                                    .querySingle()
                            //TODO Verifica si tiene pendiente actualizacion por sincronizar
                            if (ofertaVerficateSave!=null){
                                oferta.Oferta_Id=ofertaVerficateSave.Oferta_Id
                            }else{
                                val last_oferta = getLastOferta()
                                if (last_oferta == null) {
                                    oferta?.Oferta_Id = 1
                                } else {
                                    oferta?.Oferta_Id  = last_oferta.Oferta_Id!! + 1
                                }
                            }

                            oferta.CreatedOnLocal=oferta.getFechaDate(oferta.CreatedOn)
                            oferta.UpdatedOnLocal=oferta.getFechaDate(oferta.UpdatedOn)
                            oferta.Nombre_Estado_Oferta=if(oferta.Estado_Oferta!=null)oferta.Estado_Oferta?.Nombre else null
                            oferta.save()

                            if(oferta.DetalleOferta!=null){
                                for(detalleoferta in oferta.DetalleOferta!!){
                                    var detalleOfertaVerficateSave= SQLite.select()
                                            .from(DetalleOferta::class.java)
                                            .where(DetalleOferta_Table.Id_Remote.eq(detalleoferta.Id_Remote))
                                            .querySingle()
                                    if (detalleOfertaVerficateSave!=null){
                                        detalleoferta.Detalle_Oferta_Id=detalleOfertaVerficateSave.Detalle_Oferta_Id
                                    }else{
                                        val last_detalle_oferta = getLastDetalleOferta()
                                        if (last_detalle_oferta == null) {
                                            detalleoferta?.Detalle_Oferta_Id = 1
                                        } else {
                                            detalleoferta?.Detalle_Oferta_Id  = last_detalle_oferta.Detalle_Oferta_Id!! + 1
                                        }
                                    }
                                    detalleoferta.NombreUnidadMedidaPrecio=if(detalleoferta.UnidadMedida!= null)detalleoferta.UnidadMedida?.Descripcion else null
                                    detalleoferta.OfertasId=oferta.Oferta_Id
                                    detalleoferta.Detalle_Oferta_Id=detalleOfertaVerficateSave?.Detalle_Oferta_Id
                                    detalleoferta.save()

                                    //Ofertas Rol Comprador
                                    if(usuario?.RolNombre.equals(RolResources.COMPRADOR)){
                                        //UserTo
                                        var usuario=detalleoferta?.Producto?.Cultivo?.Lote?.UnidadProductiva?.Usuario
                                        if(usuario!=null){
                                            usuario.save()
                                        }

                                        //TODO Unidades Productivas
                                        var unidaProductiva=detalleoferta?.Producto?.Cultivo?.Lote?.UnidadProductiva
                                        if(unidaProductiva!=null){
                                            var unidadProductivaVerficateSave= SQLite.select()
                                                    .from(Unidad_Productiva::class.java)
                                                    .where(Unidad_Productiva_Table.Id_Remote.eq(unidaProductiva.Id_Remote))
                                                    .querySingle()
                                            //TODO Verifica si ya existe
                                            if (unidadProductivaVerficateSave !=null){
                                                unidaProductiva.Unidad_Productiva_Id=unidadProductivaVerficateSave?.Unidad_Productiva_Id
                                            }else{
                                                val last_up = getLastUp(null)
                                                if (last_up == null) {
                                                    unidaProductiva.Unidad_Productiva_Id = 1
                                                } else {
                                                    unidaProductiva.Unidad_Productiva_Id = last_up.Unidad_Productiva_Id!! + 1
                                                }
                                            }
                                            unidaProductiva.UsuarioId=usuario?.Id
                                            unidaProductiva.Nombre_Ciudad= if (unidaProductiva.Ciudad!=null) unidaProductiva.Ciudad?.Nombre else null
                                            unidaProductiva.Nombre_Unidad_Medida= if (unidaProductiva.UnidadMedida!=null) unidaProductiva.UnidadMedida?.Descripcion else null
                                            unidaProductiva.Nombre_Departamento= if (unidaProductiva.Ciudad!=null) unidaProductiva.Ciudad?.Departamento?.Nombre else null
                                            unidaProductiva.save()
                                        }


                                        //TODO Lote
                                        var lote=detalleoferta?.Producto?.Cultivo?.Lote
                                        if(lote!=null){
                                            var loteVerficateSave= SQLite.select()
                                                    .from(Lote::class.java)
                                                    .where(Lote_Table.Id_Remote.eq(lote.Id_Remote))
                                                    .querySingle()
                                            //TODO Verifica si ya existe
                                            if (loteVerficateSave!=null){
                                                lote.LoteId=loteVerficateSave.LoteId
                                            }else{
                                                val last_lote = getLastLote(null)
                                                if (last_lote == null) {
                                                    lote.LoteId = 1
                                                } else {
                                                    lote.LoteId = last_lote.LoteId!! + 1
                                                }
                                            }

                                            val coordenadas =lote.Localizacion
                                            if(coordenadas!=null || coordenadas!=""){
                                                val separated = coordenadas?.split("/".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                                                var latitud= separated!![0].toDoubleOrNull() // this will contain "Fruit"
                                                var longitud=separated!![1].toDoubleOrNull() // this will contain " they taste good"
                                                lote.Latitud=latitud
                                                lote.Longitud=longitud
                                                lote.Coordenadas=coordenadas
                                            }

                                            lote.Unidad_Productiva_Id=unidaProductiva?.Unidad_Productiva_Id
                                            //lote.Nombre_Unidad_Medida= if (lote.UnidadMedida!=null) lote.UnidadMedida?.Descripcion else null
                                            lote.Nombre_Unidad_Productiva= unidaProductiva?.nombre
                                            lote.Nombre= if (lote.Nombre==null) "" else lote.Nombre
                                            lote.Descripcion= if (lote.Descripcion==null) "" else lote.Descripcion
                                            // lote.EstadoSincronizacion=true
                                            // lote.Estado_SincronizacionUpdate=true
                                            lote.save()
                                        }

                                        //TODO Cultivo
                                        var cultivo=detalleoferta?.Producto?.Cultivo
                                        if(cultivo!=null){
                                            var cultivoVerficateSave= SQLite.select()
                                                    .from(Cultivo::class.java)
                                                    .where(Cultivo_Table.Id_Remote.eq(cultivo?.Id_Remote))
                                                    .querySingle()
                                            //TODO Verifica si tiene pendiente actualizacion por sincronizar
                                            if (cultivoVerficateSave!=null){
                                                cultivo.CultivoId=cultivoVerficateSave.CultivoId
                                            }else{
                                                val last_cultivo = getLastCultivo(null)
                                                if (last_cultivo == null) {
                                                    cultivo.CultivoId = 1
                                                } else {
                                                    cultivo.CultivoId = last_cultivo.CultivoId!! + 1
                                                }
                                            }

                                            cultivo.LoteId=lote?.LoteId
                                            cultivo.NombreUnidadProductiva= unidaProductiva?.nombre
                                            cultivo.NombreLote= lote?.Nombre
                                            cultivo.EstadoSincronizacion= true
                                            cultivo.Estado_SincronizacionUpdate= true
                                            cultivo.stringFechaInicio=cultivo.getFechaStringFormatApi(cultivo.FechaIncio)
                                            cultivo.stringFechaFin=cultivo.getFechaStringFormatApi(cultivo.FechaFin)

                                            //cultivo.Nombre_Tipo_Producto= if (detalleTipoProducto.tipoProducto!=null) detalleTipoProducto?.tipoProducto?.Nombre else null
                                            //cultivo.Nombre_Detalle_Tipo_Producto=detalleTipoProducto.Nombre
                                            //cultivo.Id_Tipo_Producto= detalleTipoProducto.TipoProductoId
                                            //cultivo.Nombre_Unidad_Medida=if (cultivo.unidadMedida!=null) cultivo.unidadMedida?.Descripcion else null
                                            //cultivo.EstadoSincronizacion=true
                                            //cultivo.Estado_SincronizacionUpdate=true
                                            cultivo.save()
                                        }

                                        //TODO Producto
                                        var producto=detalleoferta?.Producto
                                        if(producto!=null){
                                            var productoVerficateSave= SQLite.select()
                                                    .from(Producto::class.java)
                                                    .where(Producto_Table.Id_Remote.eq(producto.Id_Remote))
                                                    .querySingle()

                                            if (productoVerficateSave!=null){
                                                producto.ProductoId=productoVerficateSave.ProductoId
                                            }else {
                                                val last_producto = getLastProducto(null)
                                                if (last_producto == null) {
                                                    producto.ProductoId = 1
                                                } else {
                                                    producto.ProductoId = last_producto.ProductoId!! + 1
                                                }
                                            }
                                            producto.EmailProductor=usuario?.Email
                                            producto.NombreProductor="${usuario?.Nombre} ${usuario?.Apellidos}"
                                            producto.CodigoUp=unidaProductiva?.Unidad_Productiva_Id.toString()
                                            producto.Ciudad=unidaProductiva?.Nombre_Ciudad
                                            producto.Departamento=unidaProductiva?.Nombre_Departamento
                                            //producto.TipoProductoId=detalleTipoProducto.TipoProductoId
                                            producto.NombreCultivo= cultivo?.Nombre
                                            producto.NombreLote= if(lote!=null)lote.Nombre else null
                                            producto.NombreUnidadProductiva= if(unidaProductiva!=null)unidaProductiva.nombre else null
                                            producto.NombreUnidadMedidaCantidad=if(producto.UnidadMedida!= null)producto.UnidadMedida?.Descripcion else null
                                            producto.NombreCalidad=if(producto.Calidad!=null)producto.Calidad?.Nombre else null
                                            producto.NombreUnidadMedidaPrecio=producto.PrecioUnidadMedida
                                            producto.Usuario_Logued=usuario?.Id
                                            //producto.NombreDetalleTipoProducto=detalleTipoProducto.Nombre
                                            //producto.Estado_Sincronizacion=true
                                            //producto.Estado_SincronizacionUpdate=true
                                            try {
                                                val base64String = producto?.Imagen
                                                val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                                                val byte = Base64.decode(base64Image, Base64.DEFAULT)
                                                producto.blobImagen = Blob(byte)
                                            }catch (ex:Exception){
                                                var ss= ex.toString()
                                                Log.d("Convert Image", "defaultValue = " + ss);
                                            }
                                            producto.save()
                                        }
                                    }

                                    //Ofertas Rol Productor
                                    else if(usuario?.RolNombre.equals(RolResources.PRODUCTOR)){
                                        if(oferta.Usuario!=null){
                                            oferta.Usuario!!.save()
                                        }
                                    }
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
        var usuario= getLastUserLogued();
        var listResponse= ArrayList<Oferta>()
        if(usuario?.RolNombre.equals(RolResources.COMPRADOR)){

            if (productoId == null) {
                var ofertaResponse = SQLite.select()
                        .from(Oferta::class.java)
                        .where(Oferta_Table.UsuarioId.eq(usuario?.Id))
                        .queryList()

                for (oferta in ofertaResponse){
                    var usuario= SQLite.select().from(Usuario::class.java).where(Usuario_Table.Id.eq(oferta.UsuarioTo)).querySingle()
                    if(usuario!=null){
                        oferta.Usuario=usuario
                    }

                    var detalleOferta= SQLite.select().from(DetalleOferta::class.java).where(DetalleOferta_Table.OfertasId.eq(oferta.Oferta_Id)).querySingle()
                    if(detalleOferta!=null){
                        oferta.DetalleOfertaSingle=detalleOferta
                        var producto=SQLite.select().from(Producto::class.java).where(Producto_Table.Id_Remote.eq(detalleOferta.ProductoId)).querySingle()
                        if(producto!=null){
                            oferta.Producto=producto
                        }
                    }
                    listResponse.add(oferta)
                }
            } else {


                var ofertaResponse = SQLite.select().from(Oferta::class.java)
                        .where(Oferta_Table.ProductoId.eq(productoId))
                        .and(Oferta_Table.UsuarioTo.eq(usuario?.Id))
                        .queryList()
                for (oferta in ofertaResponse){
                    var usuario= SQLite.select().from(Usuario::class.java).where(Usuario_Table.Id.eq(oferta.UsuarioId)).querySingle()
                    if(usuario!=null){
                        oferta.Usuario=usuario
                    }
                    var detalleOferta= SQLite.select().from(DetalleOferta::class.java).where(DetalleOferta_Table.OfertasId.eq(oferta.Oferta_Id)).querySingle()
                    if(detalleOferta!=null){
                        oferta.DetalleOfertaSingle=detalleOferta
                        var producto=SQLite.select().from(Producto::class.java).where(Producto_Table.Id_Remote.eq(detalleOferta.ProductoId)).querySingle()
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

                var ofertaResponse = SQLite.select()
                        .from(Oferta::class.java)
                        .where(Oferta_Table.UsuarioTo.eq(usuario?.Id))
                        .queryList()
                for (oferta in ofertaResponse){
                    var usuario= SQLite.select().from(Usuario::class.java).where(Usuario_Table.Id.eq(oferta.UsuarioTo)).querySingle()
                    if(usuario!=null){
                        oferta.Usuario=usuario
                    }
                    var detalleOferta= SQLite.select().from(DetalleOferta::class.java).where(DetalleOferta_Table.OfertasId.eq(oferta.Oferta_Id)).querySingle()
                    if(detalleOferta!=null){
                        oferta.DetalleOfertaSingle=detalleOferta
                        var producto=SQLite.select().from(Producto::class.java).where(Producto_Table.Id_Remote.eq(detalleOferta.ProductoId)).querySingle()
                        if(producto!=null){
                            oferta.Producto=producto
                        }
                    }
                    listResponse.add(oferta)
                }
            } else {
                var ofertaResponse = SQLite.select().from(Oferta::class.java)
                        .where(Oferta_Table.ProductoId.eq(productoId))
                        .and(Oferta_Table.UsuarioTo.eq(usuario?.Id))
                        .queryList()
                for (oferta in ofertaResponse){
                    var usuario= SQLite.select().from(Usuario::class.java).where(Usuario_Table.Id.eq(oferta.UsuarioTo)).querySingle()
                    if(usuario!=null){
                        oferta.Usuario=usuario
                    }
                    var detalleOferta= SQLite.select().from(DetalleOferta::class.java).where(DetalleOferta_Table.OfertasId.eq(oferta.Oferta_Id)).querySingle()
                    if(detalleOferta!=null){
                        oferta.DetalleOfertaSingle=detalleOferta
                        var producto=SQLite.select().from(Producto::class.java).where(Producto_Table.Id_Remote.eq(detalleOferta.ProductoId)).querySingle()
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
    private fun postEventOkProducto(type: Int, producto: Producto?) {
        var productoMutable: Object? = null
        if (producto != null) {
            productoMutable = producto as Object
        }
        postEvent(type, null, productoMutable, null)
    }

    private fun postEventListUnidadProductiva(type: Int, listUp: List<Unidad_Productiva>?, messageError: String?) {
        val upMutable = listUp as MutableList<Object>
        postEvent(type, upMutable, null, messageError)
    }

    private fun postEventListLotes(type: Int, listLote: List<Lote>?, messageError: String?) {
        val loteMutable = listLote as MutableList<Object>
        postEvent(type, loteMutable, null, messageError)
    }

    private fun postEventListCultivos(type: Int, listCultivo: List<Cultivo>?, messageError: String?) {
        val cultivoMutable = listCultivo as MutableList<Object>
        postEvent(type, cultivoMutable, null, messageError)
    }

    private fun postEventListProductos(type: Int, listProductos: List<Producto>?, messageError: String?) {
        val productoMutable = listProductos as MutableList<Object>
        postEvent(type, productoMutable, null, messageError)
    }

    private fun postEventOk(type: Int, ofertas: List<Oferta>?, oferta: Oferta?) {
        var ofertaListMitable = ofertas as MutableList<Object>
        var ofertaMutable: Object? = null
        if (oferta != null) {
            ofertaMutable = oferta as Object
        }
        postEvent(type, ofertaListMitable, ofertaMutable, null)
    }

    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,messageError)
    }


    //Main Post Event
    private fun postEvent(type: Int, listModel1: MutableList<Object>?, model: Object?, errorMessage: String?) {
        val event = OfertasEvent(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
}