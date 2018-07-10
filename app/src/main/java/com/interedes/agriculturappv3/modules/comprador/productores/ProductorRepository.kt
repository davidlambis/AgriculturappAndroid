package com.interedes.agriculturappv3.modules.comprador.productores

import android.util.Base64
import android.util.Log
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.comprador.productores.events.RequestEventProductor
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.detalletipoproducto.DetalleTipoProducto
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.lote.Lote_Table
import com.interedes.agriculturappv3.modules.models.produccion.Produccion
import com.interedes.agriculturappv3.modules.models.producto.GetProductosByTipoResponse
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.producto.Producto_Table
import com.interedes.agriculturappv3.modules.models.producto.ViewProducto
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto_Table
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva_Table
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.listas.Listas
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.R.attr.delay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


import java.util.concurrent.TimeUnit;
import android.widget.Toast









class ProductorRepository:IMainViewProductor.Repository {


    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null
    var mCompositeDisposable: CompositeDisposable?=null;
    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
        mCompositeDisposable = CompositeDisposable()
    }


    override fun getTipoProducto(tipoProducto: Long): TipoProducto? {
        var tipoProducto= SQLite.select().from(TipoProducto::class.java).where(TipoProducto_Table.Id.eq(tipoProducto)).querySingle()
        return tipoProducto
    }

    override fun getListTipoProductos(checkConection: Boolean,tipoProductoId:Long,top:Int,skip:Int,isFirst:Boolean) {

        if(checkConection){
            //Get Productos by user
            val queryProductos = Listas.queryGeneralLong("tipo_producto_id",tipoProductoId)
            //val callProductos = apiService?.getProductosByTipoProductos(queryProductos,top,skip)
            val callProductos = apiService?.getProductosByTipoProductos(queryProductos)
           // mCompositeDisposable?.add(callProductos?.observeOn(AndroidSchedulers.mainThread())?.subscribeOn(Schedulers.io())?.subscribe(this::handleResponse,this::handleError)!!);
            val searchDisposable = callProductos?.delay(500, TimeUnit.MILLISECONDS)?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe({ searchResponse ->
                //Log.d("search", searchString)
                val list =searchResponse.value
                //TODO Delete information in local, add new remote
                //val list = response.body()?.value as MutableList<ViewProducto>
                val listProductos=ArrayList<Producto>()
                for (item in list!!){

                    //TODO Usuario
                    val usuario=Usuario()
                    usuario.Id=item.usuario_id
                    usuario.Nombre=item.nombre_usuario
                    usuario.Apellidos=item.apellido_usuario
                    usuario.Email=item.email_usuario
                    usuario.Estado=item.estado_usuario
                    usuario.DetalleMetodoPagoId=item.detalle_metodopago_id
                    usuario.FechaRegistro=item.fecharegistro_usuario
                    usuario.Fotopefil=item.fotoperfil_usuario
                    usuario.Identificacion= item.identificacion_usuario
                    usuario.PhoneNumber=item.phone_number_usuario
                    usuario.NumeroCuenta=item.numero_cuenta_usuario
                    usuario.save()


                    //TODO Unidades Productivas
                    val unidaProductiva= Unidad_Productiva()

                    var unidadProductivaVerficateSave= SQLite.select()
                            .from(Unidad_Productiva::class.java)
                            .where(Unidad_Productiva_Table.Id_Remote.eq(item.unidad_productiva_id))
                            .querySingle()
                    //TODO Verifica si ya existe
                    if (unidadProductivaVerficateSave !=null){
                        unidaProductiva.Unidad_Productiva_Id=unidadProductivaVerficateSave?.Unidad_Productiva_Id
                    }else{
                        val last_up = getLastUp()
                        if (last_up == null) {
                            unidaProductiva.Unidad_Productiva_Id = 1
                        } else {
                            unidaProductiva.Unidad_Productiva_Id = last_up.Unidad_Productiva_Id!! + 1
                        }
                    }
                    unidaProductiva.CiudadId=item.ciudad_id
                    unidaProductiva.UnidadMedidaId=item.unidadmedida_id_up
                    unidaProductiva.descripcion=item.descripcion_up
                    unidaProductiva.nombre=item.nombre_up
                    unidaProductiva.Area=item.area_up
                    unidaProductiva.Id_Remote=item.unidad_productiva_id
                    unidaProductiva.UsuarioId=usuario?.Id
                    unidaProductiva.Nombre_Ciudad= item.nombre_ciudad
                    unidaProductiva.Nombre_Unidad_Medida=item.descripcion_unidadmedida_up
                    unidaProductiva.Nombre_Departamento=item.nombre_departamento

                    //Localizacion UP


                    unidaProductiva.Estado_Sincronizacion=false
                    unidaProductiva.Estado_SincronizacionUpdate=false
                    unidaProductiva.save()


                    //TODO Lote
                    val lote=Lote()
                    if(lote!=null){
                        var loteVerficateSave= SQLite.select()
                                .from(Lote::class.java)
                                .where(Lote_Table.Id_Remote.eq(item.lote_id))
                                .querySingle()
                        //TODO Verifica si ya existe
                        if (loteVerficateSave!=null){
                            lote.LoteId=loteVerficateSave.LoteId
                        }else{
                            val last_lote = getLastLote()
                            if (last_lote == null) {
                                lote.LoteId = 1
                            } else {
                                lote.LoteId = last_lote.LoteId!! + 1
                            }
                        }

                        val coordenadas =item.localizacion_lote
                        if(coordenadas!=null){
                            if(coordenadas.isNotEmpty()){
                                val separated = coordenadas?.split("/".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                                var latitud= separated!![0].toDoubleOrNull() // this will contain "Fruit"
                                var longitud=separated!![1].toDoubleOrNull() // this will contain " they taste good"
                                lote.Latitud=latitud
                                lote.Longitud=longitud
                                lote.Coordenadas=coordenadas
                            }
                        }
                        lote.Area=item.area_lote
                        lote.Unidad_Medida_Id=item.unidadmedida_id_lote
                        lote.UsuarioId=item.usuario_id
                        lote.Unidad_Productiva_Id=unidaProductiva?.Unidad_Productiva_Id
                        lote.Nombre_Unidad_Medida= item.descripcion_unidadmedida_lote
                        lote.Nombre_Unidad_Productiva= unidaProductiva?.nombre
                        lote.Nombre= item.nombre_lote
                        lote.Descripcion= item.descripcion_lote
                        lote.Id_Remote=item.lote_id
                        lote.EstadoSincronizacion=false
                        lote.Estado_SincronizacionUpdate=false
                        lote.save()
                    }


                    //TODO Cultivo
                    val cultivo=Cultivo()
                    val cultivoVerficateSave= SQLite.select()
                            .from(Cultivo::class.java)
                            .where(Cultivo_Table.Id_Remote.eq(item.cultivoid))
                            .querySingle()
                    //TODO Verifica si tiene pendiente actualizacion por sincronizar
                    if (cultivoVerficateSave!=null){
                        cultivo.CultivoId=cultivoVerficateSave.CultivoId
                    }else{
                        val last_cultivo = getLastCultivo()
                        if (last_cultivo == null) {
                            cultivo.CultivoId = 1
                        } else {
                            cultivo.CultivoId = last_cultivo.CultivoId!! + 1
                        }
                    }

                    cultivo.FechaFin=item.fechafin_cultivo
                    cultivo.FechaIncio= item.fechainicio_cultivo
                    cultivo.DetalleTipoProductoId= item.detalle_tipo_productoid
                    cultivo.EstimadoCosecha=item.estimado_cosecha
                    cultivo.siembraTotal=item.siembratotal_cultivo
                    cultivo.Id_Remote=item.cultivoid
                    cultivo.Nombre= item.nombre_cultivo
                    cultivo.Unidad_Medida_Id= item.unidadmedida_id_cultivo
                    cultivo.UsuarioId=item.usuario_id
                    cultivo.LoteId=lote?.LoteId
                    cultivo.Descripcion=item.descripcion_cultivo
                    cultivo.NombreUnidadProductiva= unidaProductiva?.nombre
                    cultivo.NombreLote= lote?.Nombre

                    cultivo.stringFechaInicio=cultivo.getFechaStringFormatApi(item.fechainicio_cultivo)
                    cultivo.stringFechaFin=cultivo.getFechaStringFormatApi(item.fechafin_cultivo)

                    cultivo.Nombre_Tipo_Producto= item.nombre_tipoproducto
                    cultivo.Nombre_Detalle_Tipo_Producto=item.nombre_detalle_tipoproducto
                    cultivo.Id_Tipo_Producto= item.tipo_producto_id
                    cultivo.Nombre_Unidad_Medida=item.descripcion_unidadmedida_cultivo

                    cultivo.EstadoSincronizacion=false
                    cultivo.Estado_SincronizacionUpdate=false
                    cultivo.save()


                    //TODO Producto
                    val producto=Producto()
                    val productoVerficateSave= SQLite.select()
                            .from(Producto::class.java)
                            .where(Producto_Table.Id_Remote.eq(item.id))
                            .querySingle()

                    //TODO Verifica si tiene pendiente actualizacion por sincronizar
                    if (productoVerficateSave!=null){
                        producto.ProductoId=productoVerficateSave.ProductoId
                    }else {
                        val last_producto = getLastProducto()
                        if (last_producto == null) {
                            producto.ProductoId = 1
                        } else {
                            producto.ProductoId = last_producto.ProductoId!! + 1
                        }
                    }
                    producto.Usuario= usuario
                    producto.userId=item.usuario_id
                    producto.Id_Remote= item.id
                    producto.CalidadId= item.calidad_id
                    producto.CategoriaId=0
                    producto.Descripcion= item.descripcion_producto
                    producto.FechaLimiteDisponibilidad= item.fechalimite_disponibilidad
                    producto.Enabled=item.is_enabled_producto
                    producto.Precio= item.precio_producto
                    producto.PrecioSpecial= item.precio_escpecial_producto
                    producto.Stock= item.stock_producto
                    producto.PrecioUnidadMedida= item.precio_unidadmedida_producto
                    producto.cultivoId=cultivo.CultivoId
                    producto.Unidad_Medida_Id= item.unidadmedida_id_producto
                    producto.Nombre= item.nombre_producto


                    producto.EmailProductor=usuario?.Email
                    producto.NombreProductor="${usuario?.Nombre} ${usuario?.Apellidos}"
                    producto.CodigoUp=unidaProductiva?.Unidad_Productiva_Id.toString()
                    producto.Ciudad=unidaProductiva?.Nombre_Ciudad
                    producto.Departamento=unidaProductiva?.Nombre_Departamento
                    producto.TipoProductoId=item.tipo_producto_id
                    producto.NombreCultivo= cultivo.Nombre
                    producto.NombreLote= lote.Nombre
                    producto.NombreUnidadProductiva= unidaProductiva.nombre
                    producto.NombreUnidadMedidaCantidad=item.nombre_unidadmedida_producto
                    producto.NombreCalidad=item.nombre_calidad
                    producto.NombreUnidadMedidaPrecio=producto.PrecioUnidadMedida
                    producto.Usuario_Logued=getLastUserLogued()?.Id
                    producto.NombreDetalleTipoProducto=item.nombre_detalle_tipoproducto
                    producto.TelefonoProductor=usuario.PhoneNumber
                    producto.Estado_Sincronizacion=false
                    producto.Estado_SincronizacionUpdate=false

                    producto.Imagen=item?.imagen_producto

                    try {
                        val base64String = item?.imagen_producto
                        val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                        val byte = Base64.decode(base64Image, Base64.DEFAULT)
                        producto.blobImagen = Blob(byte)
                    }catch (ex:Exception){
                        var ss= ex.toString()
                        Log.d("Convert Image", "defaultValue = " + ss);
                    }
                    producto.save()

                    postEventOk(RequestEventProductor.ITEM_NEW_EVENT,null,producto)

                    listProductos.add(producto)
                }



                postEventOk(RequestEventProductor.LIST_EVENT,listProductos,null)

                /*
                if(isFirst){
                    postEventOk(RequestEventProductor.LOAD_DATA_FIRTS,listProductos,null)
                }else{
                    postEventOk(RequestEventProductor.READ_EVENT,listProductos,null)
                }*/

                //view.showSearchResult(searchResponse.items())
            },

            { throwable ->{
                    val error= throwable.toString()
                    postEventError(RequestEventProductor.ERROR_EVENT, error)
                }
            })



            //val seacrh =callProductos?.delay(500, TimeUnit.MILLISECONDS)?.subscribeOn(Schedulers.computation())?.observeOn(AndroidSchedulers.mainThread()).subscribe()


            // val callProductos = apiService?.getProductosByTipoOffPaginate(queryProductos)



            /*
            callProductos?.enqueue(object : Callback<GetProductosByTipoResponse> {
                override fun onResponse(call: Call<GetProductosByTipoResponse>?, response: Response<GetProductosByTipoResponse>?) {
                    if (response != null && response.code() == 200) {

                        //TODO Delete information in local, add new remote
                        val list = response.body()?.value as MutableList<ViewProducto>
                        var listProductos=ArrayList<Producto>()

                        for (item in list){

                            //TODO Usuario
                            val usuario=Usuario()
                            usuario.Id=item.usuario_id
                            usuario.Nombre=item.nombre_usuario
                            usuario.Apellidos=item.apellido_usuario
                            usuario.Email=item.email_usuario
                            usuario.Estado=item.estado_usuario
                            usuario.DetalleMetodoPagoId=item.detalle_metodopago_id
                            usuario.FechaRegistro=item.fecharegistro_usuario
                            usuario.Fotopefil=item.fotoperfil_usuario
                            usuario.Identificacion= item.identificacion_usuario
                            usuario.PhoneNumber=item.phone_number_usuario
                            usuario.NumeroCuenta=item.numero_cuenta_usuario
                            usuario.save()




                            //TODO Unidades Productivas
                            val unidaProductiva= Unidad_Productiva()

                            var unidadProductivaVerficateSave= SQLite.select()
                                    .from(Unidad_Productiva::class.java)
                                    .where(Unidad_Productiva_Table.Id_Remote.eq(item.unidad_productiva_id))
                                    .querySingle()
                            //TODO Verifica si ya existe
                            if (unidadProductivaVerficateSave !=null){
                                unidaProductiva.Unidad_Productiva_Id=unidadProductivaVerficateSave?.Unidad_Productiva_Id
                            }else{
                                val last_up = getLastUp()
                                if (last_up == null) {
                                    unidaProductiva.Unidad_Productiva_Id = 1
                                } else {
                                    unidaProductiva.Unidad_Productiva_Id = last_up.Unidad_Productiva_Id!! + 1
                                }
                            }
                            unidaProductiva.CiudadId=item.ciudad_id
                            unidaProductiva.UnidadMedidaId=item.unidadmedida_id_up
                            unidaProductiva.descripcion=item.descripcion_up
                            unidaProductiva.nombre=item.nombre_up
                            unidaProductiva.Area=item.area_up
                            unidaProductiva.Id_Remote=item.unidad_productiva_id
                            unidaProductiva.UsuarioId=usuario?.Id
                            unidaProductiva.Nombre_Ciudad= item.nombre_ciudad
                            unidaProductiva.Nombre_Unidad_Medida=item.descripcion_unidadmedida_up
                            unidaProductiva.Nombre_Departamento=item.nombre_departamento

                            //Localizacion UP


                            unidaProductiva.Estado_Sincronizacion=false
                            unidaProductiva.Estado_SincronizacionUpdate=false
                            unidaProductiva.save()


                            //TODO Lote
                            val lote=Lote()
                            if(lote!=null){
                                var loteVerficateSave= SQLite.select()
                                        .from(Lote::class.java)
                                        .where(Lote_Table.Id_Remote.eq(item.lote_id))
                                        .querySingle()
                                //TODO Verifica si ya existe
                                if (loteVerficateSave!=null){
                                    lote.LoteId=loteVerficateSave.LoteId
                                }else{
                                    val last_lote = getLastLote()
                                    if (last_lote == null) {
                                        lote.LoteId = 1
                                    } else {
                                        lote.LoteId = last_lote.LoteId!! + 1
                                    }
                                }

                                val coordenadas =item.localizacion_lote
                                if(coordenadas!=null){
                                    if(coordenadas.isNotEmpty()){
                                        val separated = coordenadas?.split("/".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                                        var latitud= separated!![0].toDoubleOrNull() // this will contain "Fruit"
                                        var longitud=separated!![1].toDoubleOrNull() // this will contain " they taste good"
                                        lote.Latitud=latitud
                                        lote.Longitud=longitud
                                        lote.Coordenadas=coordenadas
                                    }
                                }
                                lote.Area=item.area_lote
                                lote.Unidad_Medida_Id=item.unidadmedida_id_lote
                                lote.UsuarioId=item.usuario_id
                                lote.Unidad_Productiva_Id=unidaProductiva?.Unidad_Productiva_Id
                                lote.Nombre_Unidad_Medida= item.descripcion_unidadmedida_lote
                                lote.Nombre_Unidad_Productiva= unidaProductiva?.nombre
                                lote.Nombre= item.nombre_lote
                                lote.Descripcion= item.descripcion_lote
                                lote.Id_Remote=item.lote_id
                                lote.EstadoSincronizacion=false
                                lote.Estado_SincronizacionUpdate=false
                                lote.save()
                            }


                            //TODO Cultivo
                            val cultivo=Cultivo()
                            val cultivoVerficateSave= SQLite.select()
                                    .from(Cultivo::class.java)
                                    .where(Cultivo_Table.Id_Remote.eq(item.cultivoid))
                                    .querySingle()
                            //TODO Verifica si tiene pendiente actualizacion por sincronizar
                            if (cultivoVerficateSave!=null){
                                cultivo.CultivoId=cultivoVerficateSave.CultivoId
                            }else{
                                val last_cultivo = getLastCultivo()
                                if (last_cultivo == null) {
                                    cultivo.CultivoId = 1
                                } else {
                                    cultivo.CultivoId = last_cultivo.CultivoId!! + 1
                                }
                            }

                            cultivo.FechaFin=item.fechafin_cultivo
                            cultivo.FechaIncio= item.fechainicio_cultivo
                            cultivo.DetalleTipoProductoId= item.detalle_tipo_productoid
                            cultivo.EstimadoCosecha=item.estimado_cosecha
                            cultivo.siembraTotal=item.siembratotal_cultivo
                            cultivo.Id_Remote=item.cultivoid
                            cultivo.Nombre= item.nombre_cultivo
                            cultivo.Unidad_Medida_Id= item.unidadmedida_id_cultivo
                            cultivo.UsuarioId=item.usuario_id
                            cultivo.LoteId=lote?.LoteId
                            cultivo.Descripcion=item.descripcion_cultivo
                            cultivo.NombreUnidadProductiva= unidaProductiva?.nombre
                            cultivo.NombreLote= lote?.Nombre

                            cultivo.stringFechaInicio=cultivo.getFechaStringFormatApi(item.fechainicio_cultivo)
                            cultivo.stringFechaFin=cultivo.getFechaStringFormatApi(item.fechafin_cultivo)

                            cultivo.Nombre_Tipo_Producto= item.nombre_tipoproducto
                            cultivo.Nombre_Detalle_Tipo_Producto=item.nombre_detalle_tipoproducto
                            cultivo.Id_Tipo_Producto= item.tipo_producto_id
                            cultivo.Nombre_Unidad_Medida=item.descripcion_unidadmedida_cultivo

                            cultivo.EstadoSincronizacion=false
                            cultivo.Estado_SincronizacionUpdate=false
                            cultivo.save()


                            //TODO Producto
                            val producto=Producto()
                            val productoVerficateSave= SQLite.select()
                                    .from(Producto::class.java)
                                    .where(Producto_Table.Id_Remote.eq(item.id))
                                    .querySingle()

                            //TODO Verifica si tiene pendiente actualizacion por sincronizar
                            if (productoVerficateSave!=null){
                                producto.ProductoId=productoVerficateSave.ProductoId
                            }else {
                                val last_producto = getLastProducto()
                                if (last_producto == null) {
                                    producto.ProductoId = 1
                                } else {
                                    producto.ProductoId = last_producto.ProductoId!! + 1
                                }
                            }
                            producto.userId=item.usuario_id
                            producto.Id_Remote= item.id
                            producto.CalidadId= item.calidad_id
                            producto.CategoriaId=0
                            producto.Descripcion= item.descripcion_producto
                            producto.FechaLimiteDisponibilidad= item.fechalimite_disponibilidad
                            producto.Enabled=item.is_enabled_producto
                            producto.Precio= item.precio_producto
                            producto.PrecioSpecial= item.precio_escpecial_producto
                            producto.Stock= item.stock_producto
                            producto.PrecioUnidadMedida= item.precio_unidadmedida_producto
                            producto.cultivoId=cultivo.CultivoId
                            producto.Unidad_Medida_Id= item.unidadmedida_id_producto
                            producto.Nombre= item.nombre_producto


                            producto.EmailProductor=usuario?.Email
                            producto.NombreProductor="${usuario?.Nombre} ${usuario?.Apellidos}"
                            producto.CodigoUp=unidaProductiva?.Unidad_Productiva_Id.toString()
                            producto.Ciudad=unidaProductiva?.Nombre_Ciudad
                            producto.Departamento=unidaProductiva?.Nombre_Departamento
                            producto.TipoProductoId=item.tipo_producto_id
                            producto.NombreCultivo= cultivo.Nombre
                            producto.NombreLote= lote.Nombre
                            producto.NombreUnidadProductiva= unidaProductiva.nombre
                            producto.NombreUnidadMedidaCantidad=item.nombre_unidadmedida_producto
                            producto.NombreCalidad=item.nombre_calidad
                            producto.NombreUnidadMedidaPrecio=producto.PrecioUnidadMedida
                            producto.Usuario_Logued=getLastUserLogued()?.Id
                            producto.NombreDetalleTipoProducto=item.nombre_detalle_tipoproducto
                            producto.TelefonoProductor=usuario.PhoneNumber
                            producto.Estado_Sincronizacion=false
                            producto.Estado_SincronizacionUpdate=false

                            producto.Imagen=item?.imagen_producto

                            try {
                                val base64String = item?.imagen_producto
                                val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                                val byte = Base64.decode(base64Image, Base64.DEFAULT)
                                producto.blobImagen = Blob(byte)
                            }catch (ex:Exception){
                                var ss= ex.toString()
                                Log.d("Convert Image", "defaultValue = " + ss);
                            }
                            producto.save()
                            listProductos.add(producto)
                        }

                        if(isFirst){
                            postEventOk(RequestEventProductor.LOAD_DATA_FIRTS,listProductos,null)
                        }else{
                            postEventOk(RequestEventProductor.READ_EVENT,listProductos,null)
                        }

                    } else {
                        postEventError(RequestEventProductor.ERROR_EVENT, "Comprueba tu conexión a Internet")
                    }
                }
                override fun onFailure(call: Call<GetProductosByTipoResponse>?, t: Throwable?) {
                    postEventError(RequestEventProductor.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            })

            */


            /*
            callProductos?.enqueue(object : Callback<GetProductosByTipoResponse> {
                override fun onResponse(call: Call<GetProductosByTipoResponse>?, response: Response<GetProductosByTipoResponse>?) {
                    if (response != null && response.code() == 200) {

                        //TODO Delete information in local, add new remote
                        SQLite.delete<Unidad_Productiva>(Unidad_Productiva::class.java)
                                .async()
                                .execute()

                        SQLite.delete<Lote>(Lote::class.java)
                                .async()
                                .execute()

                        SQLite.delete<Cultivo>(Cultivo::class.java)
                                .async()
                                .execute()

                        SQLite.delete<Produccion>(Produccion::class.java)
                                .async()
                                .execute()

                        SQLite.delete<ControlPlaga>(ControlPlaga::class.java)
                                .async()
                                .execute()



                        val list = response.body()?.value as MutableList<DetalleTipoProducto>


                        for (detalleTipoProducto in list){

                            detalleTipoProducto.save()

                            if(detalleTipoProducto.cultivos?.size!!>0){

                                for(cultivo in detalleTipoProducto?.cultivos!!){
                                    var usuario=cultivo?.Lote?.UnidadProductiva?.Usuario
                                    if(usuario!=null){
                                        usuario.save()
                                    }

                                    //TODO Unidades Productivas
                                    var unidaProductiva=cultivo?.Lote?.UnidadProductiva
                                    if(unidaProductiva!=null){
                                        var unidadProductivaVerficateSave= SQLite.select()
                                                .from(Unidad_Productiva::class.java)
                                                .where(Unidad_Productiva_Table.Id_Remote.eq(unidaProductiva.Id_Remote))
                                                .querySingle()
                                        //TODO Verifica si ya existe
                                        if (unidadProductivaVerficateSave !=null){
                                            unidaProductiva.Unidad_Productiva_Id=unidadProductivaVerficateSave?.Unidad_Productiva_Id
                                        }else{
                                            val last_up = getLastUp()
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

                                        if(unidaProductiva.LocalizacionUps?.size!!>0){
                                            for (localizacion in unidaProductiva.LocalizacionUps!!){
                                                unidaProductiva.DireccionAproximadaGps=localizacion.DireccionAproximadaGps
                                                unidaProductiva.Latitud=localizacion.Latitud
                                                unidaProductiva.Longitud=localizacion.Longitud
                                                unidaProductiva.Coordenadas=localizacion.Coordenadas
                                                unidaProductiva.Direccion=localizacion.Direccion
                                                unidaProductiva.Configuration_Point=true
                                                unidaProductiva.Configuration_Poligon=false
                                                unidaProductiva.LocalizacionUpId=localizacion.Id
                                            }
                                        }

                                        unidaProductiva.Estado_Sincronizacion=true
                                        unidaProductiva.Estado_SincronizacionUpdate=true

                                        unidaProductiva.save()
                                    }


                                    //TODO Lote
                                    var lote=cultivo?.Lote
                                    if(lote!=null){
                                        var loteVerficateSave= SQLite.select()
                                                .from(Lote::class.java)
                                                .where(Lote_Table.Id_Remote.eq(lote.Id_Remote))
                                                .querySingle()
                                        //TODO Verifica si ya existe
                                        if (loteVerficateSave!=null){
                                            lote.LoteId=loteVerficateSave.LoteId
                                        }else{
                                            val last_lote = getLastLote()
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
                                        lote.Nombre_Unidad_Medida= if (lote.UnidadMedida!=null) lote.UnidadMedida?.Descripcion else null
                                        lote.Nombre_Unidad_Productiva= unidaProductiva?.nombre
                                        lote.Nombre= if (lote.Nombre==null) "" else lote.Nombre
                                        lote.Descripcion= if (lote.Descripcion==null) "" else lote.Descripcion
                                        lote.EstadoSincronizacion=true
                                        lote.Estado_SincronizacionUpdate=true
                                        lote.save()
                                    }

                                    //TODO Cultivo

                                    var cultivoVerficateSave= SQLite.select()
                                            .from(Cultivo::class.java)
                                            .where(Cultivo_Table.Id_Remote.eq(cultivo.Id_Remote))
                                            .querySingle()
                                    //TODO Verifica si tiene pendiente actualizacion por sincronizar
                                    if (cultivoVerficateSave!=null){
                                        cultivo.CultivoId=cultivoVerficateSave.CultivoId
                                    }else{
                                        val last_cultivo = getLastCultivo()
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

                                    cultivo.Nombre_Tipo_Producto= if (detalleTipoProducto.tipoProducto!=null) detalleTipoProducto?.tipoProducto?.Nombre else null
                                    cultivo.Nombre_Detalle_Tipo_Producto=detalleTipoProducto.Nombre
                                    cultivo.Id_Tipo_Producto= detalleTipoProducto.TipoProductoId
                                    cultivo.Nombre_Unidad_Medida=if (cultivo.unidadMedida!=null) cultivo.unidadMedida?.Descripcion else null
                                    cultivo.EstadoSincronizacion=true
                                    cultivo.Estado_SincronizacionUpdate=true
                                    cultivo.save()

                                    if(cultivo?.productos?.size!!>0){
                                        for(producto in cultivo?.productos!!){
                                            var productoVerficateSave= SQLite.select()
                                                    .from(Producto::class.java)
                                                    .where(Producto_Table.Id_Remote.eq(producto.Id_Remote))
                                                    .querySingle()

                                            //TODO Verifica si tiene pendiente actualizacion por sincronizar
                                            if (productoVerficateSave!=null){
                                                producto.ProductoId=productoVerficateSave.ProductoId
                                            }else {
                                                val last_producto = getLastProducto()
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
                                            producto.TipoProductoId=detalleTipoProducto.TipoProductoId
                                            producto.NombreCultivo= cultivo.Nombre
                                            producto.NombreLote= if(lote!=null)lote.Nombre else null
                                            producto.NombreUnidadProductiva= if(unidaProductiva!=null)unidaProductiva.nombre else null
                                            producto.NombreUnidadMedidaCantidad=if(producto.UnidadMedida!= null)producto.UnidadMedida?.Descripcion else null
                                            producto.NombreCalidad=if(producto.Calidad!=null)producto.Calidad?.Nombre else null
                                            producto.NombreUnidadMedidaPrecio=producto.PrecioUnidadMedida
                                            producto.Usuario_Logued=getLastUserLogued()?.Id
                                            producto.NombreDetalleTipoProducto=detalleTipoProducto.Nombre
                                            producto.Estado_Sincronizacion=true
                                            producto.Estado_SincronizacionUpdate=true
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
                                }
                            }
                        }


                        postEventOk(RequestEventProductor.READ_EVENT,getListProductos(tipoProductoId),null)

                    } else {
                        postEventError(RequestEventProductor.ERROR_EVENT, "Comprueba tu conexión a Internet")
                    }
                }
                override fun onFailure(call: Call<GetProductosByTipoResponse>?, t: Throwable?) {
                    postEventError(RequestEventProductor.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }*/

        }else{

            val list= getListProductos(tipoProductoId)
            if(list!=null){
                for(item in list!!){
                    postEventOk(RequestEventProductor.ITEM_NEW_EVENT,null,item)
                }
                postEventOk(RequestEventProductor.LIST_EVENT,list,null)
            }else{
                postEventOk(RequestEventProductor.LIST_EVENT, ArrayList<Producto>(),null)
            }



        }
    }



    fun getListProductos(tipoProducto: Long): List<Producto>? {
        val productos = SQLite.select().from(Producto::class.java).where(Producto_Table.TipoProductoId.eq(tipoProducto)).queryList()
        return productos
    }

    fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }


    fun getLastUp(): Unidad_Productiva? {
        val lastUnidadProductiva = SQLite.select().from(Unidad_Productiva::class.java).where().orderBy(Unidad_Productiva_Table.Unidad_Productiva_Id, false).querySingle()
        return lastUnidadProductiva
    }

    fun getLastLote(): Lote? {
        val lastLote = SQLite.select().from(Lote::class.java).where().orderBy(Lote_Table.LoteId, false).querySingle()
        return lastLote
    }

    fun getLastCultivo(): Cultivo? {
        val lastCultivo = SQLite.select().from(Cultivo::class.java).where().orderBy(Cultivo_Table.CultivoId, false).querySingle()
        return lastCultivo
    }

    fun getLastProducto(): Producto? {
        val lastProducto = SQLite.select().from(Producto::class.java).where().orderBy(Producto_Table.ProductoId, false).querySingle()
        return lastProducto
    }



    //region Events


    private fun postEventOk(type: Int, listProducto: List<Producto>?, producto: Producto?) {


        var productoMutable: Object? = null
        var listProductoMutable: MutableList<Object>? = null



        if (listProducto != null) {
            listProductoMutable = listProducto as MutableList<Object>
        }

        if (producto != null) {
            productoMutable = producto as Object
        }
        postEvent(type, listProductoMutable, productoMutable, null)
    }


    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventProductor(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
    //endregion
}