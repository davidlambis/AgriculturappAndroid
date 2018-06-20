package com.interedes.agriculturappv3.modules.productor.comercial_module.productos

import android.util.Log
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.lote.Lote_Table
import com.interedes.agriculturappv3.modules.models.ofertas.DetalleOferta
import com.interedes.agriculturappv3.modules.models.ofertas.DetalleOferta_Table
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta_Table
import com.interedes.agriculturappv3.modules.models.producto.CalidadProducto
import com.interedes.agriculturappv3.modules.models.producto.PostProducto
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.producto.Producto_Table
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida_Table
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva_Table
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.modules.productor.comercial_module.productos.events.ProductosEvent
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.resources.CategoriaMediaResources
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream


class ProductosRepository : IProductos.Repository {
    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null

    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }

    //region Métodos Interfaz
    override fun getListas() {
        var usuario= getLastUserLogued()

        val listUnidadProductiva: List<Unidad_Productiva> = SQLite.select().from(Unidad_Productiva::class.java)
                .where(Unidad_Productiva_Table.UsuarioId.eq(usuario?.Id))
                .queryList()

        val listLotes = SQLite.select().from(Lote::class.java)
                .where(Lote_Table.UsuarioId.eq(usuario?.Id))
                .queryList()


        var listCultivos = SQLite.select().from(Cultivo::class.java!!)
                .where(Cultivo_Table.UsuarioId.eq(usuario?.Id))
                .queryList()
        //val listUnidadMedida = Listas.listaUnidadMedida()
        val listUnidadMedidaCantidades = SQLite.select().from(Unidad_Medida::class.java).where(Unidad_Medida_Table.CategoriaMedidaId.eq(CategoriaMediaResources.Cosecha)).queryList()
        val listUnidadMedidaPrecios = SQLite.select().from(Unidad_Medida::class.java).where(Unidad_Medida_Table.CategoriaMedidaId.eq(CategoriaMediaResources.Moneda)).queryList()

        val listCalidadesProducto = SQLite.select().from(CalidadProducto::class.java).queryList()

        postEventListUnidadMedida(ProductosEvent.LIST_EVENT_UNIDAD_MEDIDA_PRICE, listUnidadMedidaPrecios, null)
        postEventListUnidadMedida(ProductosEvent.LIST_EVENT_UNIDAD_MEDIDA_CANTIDAD, listUnidadMedidaCantidades, null)
        postEventListUnidadProductiva(ProductosEvent.LIST_EVENT_UP, listUnidadProductiva, null)
        postEventListLotes(ProductosEvent.LIST_EVENT_LOTE, listLotes, null)
        postEventListCultivos(ProductosEvent.LIST_EVENT_CULTIVO, listCultivos, null)
        postEventListCalidades(ProductosEvent.LIST_EVENT_CALIDADES, listCalidadesProducto, null)
    }

    override fun getListProducto(cultivo_id: Long?) {
        val listaProductos = getProductos(cultivo_id)
        postEventOk(ProductosEvent.READ_EVENT, listaProductos, null);
    }

    override fun getProductos(cultivo_id: Long?): List<Producto> {
        var usuarioLogued= getLastUserLogued()
        val list= SQLite.select().from(Producto::class.java).queryList()

        var listResponse: List<Producto>? = null
        if (cultivo_id == null) {
            listResponse = SQLite.select().from(Producto::class.java)
                    .where(Producto_Table.userId.eq(usuarioLogued?.Id))
                    .queryList()
        } else {
            listResponse = SQLite.select().from(Producto::class.java)
                    .where(Producto_Table.userId.eq(usuarioLogued?.Id))
                    .and(Producto_Table.cultivoId.eq(cultivo_id)).queryList()
        }
        return listResponse;
    }



    override fun registerProducto(mProducto: Producto, cultivo_id: Long,checkConection:Boolean) {

        var usuarioLogued= getLastUserLogued()
        mProducto.userId=usuarioLogued?.Id
        mProducto.Usuario_Logued=usuarioLogued?.Id

        //TODO si existe conexion a internet
       if(checkConection){

           val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(cultivo_id)).querySingle()
           if (cultivo?.EstadoSincronizacion == true) {

               /*
               val data = mProducto.blobImagen?.getBlob()
               var byteFoto:String="data:image/jpeg;base64,"

               if(data!=null){
                   val bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
                   byteFoto+=getEncoded64ImageStringFromBitmap(bitmap)
               }
               */
               val postProducto = PostProducto(0,
                       mProducto.CalidadId,
                       1,
                       mProducto.CodigoUp,
                       mProducto.Descripcion,
                       mProducto.FechaLimiteDisponibilidad,
                       mProducto.Imagen,
                       true,
                       mProducto.Precio,
                       mProducto.PrecioSpecial,
                       mProducto.Stock,
                       cultivo.Id_Remote,
                       mProducto.Nombre,
                       mProducto.Unidad_Medida_Id,
                       mProducto.PrecioUnidadMedida,
                       mProducto.userId
               )

               val call = apiService?.postProducto(postProducto)
               call?.enqueue(object : Callback<Producto> {
                   override fun onResponse(call: Call<Producto>?, response: Response<Producto>?) {
                       if (response != null && response.code() == 201) {
                           val value = response.body()

                           mProducto.Id_Remote = value?.Id_Remote!!


                           val last_producto = getLastProducto()
                           if (last_producto == null) {
                               mProducto.ProductoId = 1
                           } else {
                               mProducto.ProductoId = last_producto.ProductoId!! + 1
                           }

                           mProducto.Estado_Sincronizacion = true
                           mProducto.Estado_SincronizacionUpdate = true

                           mProducto.save()
                           postEventOk(ProductosEvent.SAVE_EVENT, getProductos(cultivo_id), null)
                       } else {
                           postEventError(ProductosEvent.ERROR_EVENT, "Comprueba tu conexión")
                           Log.e("error", response?.message().toString())
                       }
                   }
                   override fun onFailure(call: Call<Producto>?, t: Throwable?) {
                       postEventError(ProductosEvent.ERROR_EVENT, "Comprueba tu conexión")
                   }
               })
           }
           //TODO con conexion a internet sin sincronizacion, registro local
           else {
               registerProductoLocal(mProducto, cultivo_id)
           }

       }
       //TODO sin conexion a internet, registro local
       else{
           registerProductoLocal(mProducto,cultivo_id)
       }
    }

    fun getEncoded64ImageStringFromBitmap(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteFormat = stream.toByteArray()
        // get the base 64 string
        //String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP)
    }
    override fun registerProductoLocal(mProducto: Producto, cultivo_id: Long) {
        val last_producto = getLastProducto()
        if (last_producto == null) {
            mProducto.ProductoId = 1
        } else {
            mProducto.ProductoId = last_producto.ProductoId!! + 1
        }

        mProducto.save()
        postEventOk(ProductosEvent.SAVE_EVENT, getProductos(cultivo_id), null)
    }

    override fun updateProducto(mProducto: Producto, cultivo_id: Long,checkConection:Boolean) {

        var usuarioLogued= getLastUserLogued()
        mProducto.userId=usuarioLogued?.Id
        mProducto.Usuario_Logued=usuarioLogued?.Id
        //TODO si existe coneccion a internet
        if(checkConection){
            //TODO se valida estado de sincronizacion  para actualizar,actualizacion remota
            if (mProducto.Estado_Sincronizacion == true) {

                val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(cultivo_id)).querySingle()
                val postProducto = PostProducto(mProducto.Id_Remote,
                        mProducto.CalidadId,
                        1,
                        mProducto.CodigoUp,
                        mProducto.Descripcion,
                        mProducto.FechaLimiteDisponibilidad,
                        mProducto.Imagen,
                        true,
                        mProducto.Precio,
                        mProducto.PrecioSpecial,
                        mProducto.Stock,
                        cultivo?.Id_Remote,
                        mProducto.Nombre,
                        mProducto.Unidad_Medida_Id,
                        mProducto.PrecioUnidadMedida,
                        mProducto?.userId
                )

                val call = apiService?.updateProducto(postProducto, mProducto.Id_Remote!!)
                call?.enqueue(object : Callback<Producto> {
                    override fun onResponse(call: Call<Producto>?, response: Response<Producto>?) {
                        if (response != null && response.code() == 200) {

                            mProducto.update()
                            postEventOk(ProductosEvent.SAVE_EVENT, getProductos(cultivo_id), null)
                        } else {
                            postEventError(ProductosEvent.ERROR_EVENT, "Comprueba tu conexión")
                        }
                    }
                    override fun onFailure(call: Call<Producto>?, t: Throwable?) {
                        postEventError(ProductosEvent.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })
            }
            //TODO con  conexion a internet, pero no se ha sincronizado,actualizacion local
            else {
                mProducto?.Estado_SincronizacionUpdate = false
                mProducto.update()
                postEventOk(ProductosEvent.SAVE_EVENT, getProductos(cultivo_id), null)
            }
        }
        //TODO sin conexion a internet, actualizacion local
        else{
            mProducto?.Estado_SincronizacionUpdate = false
            mProducto.update()
            postEventOk(ProductosEvent.SAVE_EVENT, getProductos(cultivo_id), null)
        }
    }



    override fun deleteProducto(mProducto: Producto, cultivo_id: Long?,checkConection:Boolean) {
        //TODO se valida estado de sincronizacion  para eliminar
        if (mProducto.Estado_Sincronizacion == true) {
            //TODO si existe coneccion a internet se elimina
            if(checkConection){
                val call = apiService?.deleteProducto(mProducto.Id_Remote!!)
                call?.enqueue(object : Callback<Producto> {
                    override fun onResponse(call: Call<Producto>?, response: Response<Producto>?) {
                        if (response != null && response.code() == 204) {
                            //mProducto.delete()
                            deleteProducto(mProducto)
                            postEventOk(ProductosEvent.DELETE_EVENT, getProductos(cultivo_id), null)
                        }
                    }
                    override fun onFailure(call: Call<Producto>?, t: Throwable?) {
                        postEventError(ProductosEvent.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })
            }else{
                postEventError(ProductosEvent.ERROR_VERIFICATE_CONECTION, null)
            }
        } else {
            //TODO No sincronizado, Eliminar de manera local
            //Verificate if cultivos register
            deleteProducto(mProducto)
            postEventOk(ProductosEvent.DELETE_EVENT, getProductos(cultivo_id), null)
        }
    }

    fun deleteProducto(producto: Producto) {
            var listDetalleOferta = SQLite.select().from(DetalleOferta::class.java).where(DetalleOferta_Table.ProductoId.eq(producto?.ProductoId)).queryList()
            for (detalleoferta in listDetalleOferta) {

                SQLite.delete<Oferta>(Oferta::class.java)
                        .where(Oferta_Table.Oferta_Id.eq(detalleoferta.OfertasId))
                        .async()
                        .execute()

                detalleoferta.delete()
            }
            producto.delete()
    }


    fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    override fun getCultivo(cultivo_id: Long?) {
        val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(cultivo_id)).querySingle()
        postEventOkCultivo(ProductosEvent.GET_EVENT_CULTIVO, cultivo)
    }

    override fun getLastProducto(): Producto? {
        //val list= SQLite.select().from(Producto::class.java).queryList()

        val lastProducto = SQLite.select().from(Producto::class.java).where(Producto_Table.ProductoId.notEq(0)).orderBy(Producto_Table.ProductoId, false).querySingle()
        return lastProducto
    }


    //endregion

    //region Events

    private fun postEventListUnidadMedida(type: Int, listUnidadMedida: List<Unidad_Medida>?, messageError: String?) {
        val upMutable = listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable, null, messageError)
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

    private fun postEventListCalidades(type: Int, listCalidad: List<CalidadProducto>?, messageError: String?) {
        val calidadMutable = listCalidad as MutableList<Object>
        postEvent(type, calidadMutable, null, messageError)
    }


    private fun postEventError(type: Int, messageError: String?) {
        val event = ProductosEvent(type, null, null, messageError)
        event.eventType = type
        eventBus?.post(event)
    }

    private fun postEventOk(type: Int, productos: List<Producto>?, producto: Producto?) {
        var productoListMitable = productos as MutableList<Object>
        var productoMutable: Object? = null
        if (producto != null) {
            productoMutable = producto as Object
        }
        postEvent(type, productoListMitable, productoMutable, null)
    }

    private fun postEventOkCultivo(type: Int, cultivo: Cultivo?) {
        var CultivoMutable: Object? = null
        if (cultivo != null) {
            CultivoMutable = cultivo as Object
        }
        postEvent(type, null, CultivoMutable, null)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1: MutableList<Object>?, model: Object?, errorMessage: String?) {
        val event = ProductosEvent(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }

    //endregiopn
}