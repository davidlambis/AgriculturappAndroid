package com.interedes.agriculturappv3.modules.productor.comercial_module.ventas

import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.compras.Compras
import com.interedes.agriculturappv3.modules.models.compras.Compras_Table
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.producto.Producto_Table
import com.interedes.agriculturappv3.modules.productor.comercial_module.ofertas.events.OfertasEvent
import com.interedes.agriculturappv3.modules.productor.comercial_module.ventas.events.VentasEvent
import com.interedes.agriculturappv3.services.listas.Listas
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite


class VentasRepository : IVentas.Repository {

    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
    }

    //region Métodos Interfaz
    override fun getListas() {
        val listUnidadProductiva = SQLite.select().from(Unidad_Productiva::class.java).queryList()
        val listLotes = SQLite.select().from(Lote::class.java).queryList()
        val listCultivos = SQLite.select().from(Cultivo::class.java).queryList()
        val listProductos = SQLite.select().from(Producto::class.java).queryList()
        postEventListUnidadProductiva(VentasEvent.LIST_EVENT_UP, listUnidadProductiva, null)
        postEventListLotes(VentasEvent.LIST_EVENT_LOTE, listLotes, null)
        postEventListCultivos(VentasEvent.LIST_EVENT_CULTIVO, listCultivos, null)
        postEventListProductos(VentasEvent.LIST_EVENT_PRODUCTO, listProductos, null)
    }

    override fun getListVentas(productoId: Long?) {
        val list_static_detalle_ventas = Listas.listaDetalleVentasProductor()
        val list_static_ventas = Listas.listVentasProductor()
        for (item in list_static_detalle_ventas) {
            if (item.ProductoId == productoId) {
                for (i in list_static_ventas) {
                    if (i.Id == item.ComprasId) {
                        i.save()
                    }
                }
            }
        }

        val listaVentas = getVentas(productoId)
        postEventOk(OfertasEvent.READ_EVENT, listaVentas, null)
    }

    override fun getVentas(productoId: Long?): List<Compras> {
        var listResponse: List<Compras>? = null
        if (productoId == null) {
            listResponse = SQLite.select().from(Compras::class.java).queryList()
        } else {
            listResponse = SQLite.select().from(Compras::class.java).where(Compras_Table.ProductoId.eq(productoId)).queryList()
        }
        return listResponse
    }

    override fun getProducto(productoId: Long?) {
        val producto = SQLite.select().from(Producto::class.java).where(Producto_Table.ProductoId.eq(productoId)).querySingle()
        postEventOkProducto(OfertasEvent.GET_EVENT_PRODUCTO, producto)
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

    private fun postEventOk(type: Int, ventas: List<Compras>?, venta: Compras?) {
        var ventasListMitable = ventas as MutableList<Object>
        var ventaMutable: Object? = null
        if (venta != null) {
            ventaMutable = venta as Object
        }
        postEvent(type, ventasListMitable, ventaMutable, null)
    }


    //Main Post Event
    private fun postEvent(type: Int, listModel1: MutableList<Object>?, model: Object?, errorMessage: String?) {
        val event = VentasEvent(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion

}