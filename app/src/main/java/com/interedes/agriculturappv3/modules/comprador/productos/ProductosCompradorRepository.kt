package com.interedes.agriculturappv3.modules.comprador.productos

import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.comprador.productos.events.RequestEventProductosComprador
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.sql.language.SQLite

class ProductosCompradorRepository:IMainViewProductoComprador.Repository  {

    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null
    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }


    //region Implemnts Interface
    override fun getListTipoProductos() {
        val lista_tipo_producto = SQLite.select().from(TipoProducto::class.java).queryList()
        postEventListTiposProducto(RequestEventProductosComprador.LIST_EVENT_TIPO_PRODUCTO, lista_tipo_producto)
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