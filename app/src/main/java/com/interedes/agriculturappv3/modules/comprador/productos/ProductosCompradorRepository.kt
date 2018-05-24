package com.interedes.agriculturappv3.modules.comprador.productos

import android.util.Base64
import android.util.Log
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.comprador.productos.events.RequestEventProductosComprador
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProductoResponse
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductosCompradorRepository:IMainViewProductoComprador.Repository  {

    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null
    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }


    //region Implemnts Interface
    override fun getListTipoProductos(checkConection:Boolean) {
        if(checkConection){
            val lista_tipo_producto = SQLite.select().from(TipoProducto::class.java).queryList()
            if(lista_tipo_producto.size==0){
                val callTipoProducto = apiService?.getTipoAndDetalleTipoProducto()
                callTipoProducto?.enqueue(object : Callback<TipoProductoResponse> {
                    override fun onResponse(call: Call<TipoProductoResponse>?, response: Response<TipoProductoResponse>?) {
                        if (response != null && response.code() == 200) {
                            val tiposProducto = response.body()?.value as MutableList<TipoProducto>
                            for (item in tiposProducto) {
                                try {
                                    val base64String = item?.Icono
                                    val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                                    val byte = Base64.decode(base64Image, Base64.DEFAULT)
                                    item.Imagen = Blob(byte)

                                }catch (ex:Exception){
                                    var ss= ex.toString()
                                    Log.d("Convert Image", "defaultValue = " + ss);
                                }

                                item.save()
                                for (detalleTipoProducto in item.DetalleTipoProductos!!) {
                                    detalleTipoProducto.save()
                                }
                            }

                            val lista_tipo_producto = SQLite.select().from(TipoProducto::class.java).queryList()
                            postEventListTiposProducto(RequestEventProductosComprador.LIST_EVENT_TIPO_PRODUCTO, lista_tipo_producto)

                        } else {
                            postEventError(RequestEventProductosComprador.ERROR_EVENT, "Comprueba tu conexión a Internet")
                        }
                    }
                    override fun onFailure(call: Call<TipoProductoResponse>?, t: Throwable?) {
                        postEventError(RequestEventProductosComprador.ERROR_EVENT, "Comprueba tu conexión a Internet")
                    }
                })

            }else{
                postEventListTiposProducto(RequestEventProductosComprador.LIST_EVENT_TIPO_PRODUCTO, lista_tipo_producto)
            }

        }else{
            val lista_tipo_producto = SQLite.select().from(TipoProducto::class.java).queryList()
            postEventListTiposProducto(RequestEventProductosComprador.LIST_EVENT_TIPO_PRODUCTO, lista_tipo_producto)
        }

    }


    //endregion

    //region Events Response
    private fun postEventListTiposProducto(type: Int, listTipoProducto: List<TipoProducto>) {
        val listTipoProductotMutable = listTipoProducto as MutableList<Object>
        postEvent(type, listTipoProductotMutable, null, null)
    }

    private fun postEventError(type: Int,messageError:String) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventProductosComprador(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
}