package com.interedes.agriculturappv3.modules.comprador.detail_producto

import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.comprador.detail_producto.events.RequestEventDetalleProducto
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.producto.Producto_Table
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto_Table
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.sql.language.SQLite

class DetailProductoRepository :IMainViewDetailProducto.Repository {



    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null
    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }


    override fun getProducto(producto_id: Long): Producto? {
        var producto= SQLite.select().from(Producto::class.java).where(Producto_Table.ProductoId.eq(producto_id)).querySingle()
        return producto
    }

    override fun getTipoProducto(tipo_producto_id: Long): TipoProducto? {
        var tipoProducto= SQLite.select().from(TipoProducto::class.java).where(TipoProducto_Table.Id.eq(tipo_producto_id)).querySingle()
        return tipoProducto
    }

    override fun verificateCantProducto(producto_id:Long?,cantidad:Double?):Boolean? {
        var response= false
        var producto= SQLite.select().from(Producto::class.java).where(Producto_Table.ProductoId.eq(producto_id)).querySingle()
        if(cantidad!!<=producto?.Stock!!){
            response=true
        }
        return response
    }


    //region Events


    private fun postEventOk(type: Int, listProducto: List<Producto>?, producto: Producto?) {
        val productoListMutable = listProducto as MutableList<Object>
        var productoMutable: Object? = null
        if (producto != null) {
            productoMutable = producto as Object
        }
        postEvent(type, productoListMutable, productoMutable, null)
    }


    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventDetalleProducto(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
    //endregion

}