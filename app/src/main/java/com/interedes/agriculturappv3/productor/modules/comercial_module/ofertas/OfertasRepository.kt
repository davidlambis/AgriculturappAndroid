package com.interedes.agriculturappv3.productor.modules.comercial_module.ofertas

import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.ofertas.Oferta
import com.interedes.agriculturappv3.productor.models.ofertas.Oferta_Table
import com.interedes.agriculturappv3.productor.models.producto.Producto
import com.interedes.agriculturappv3.productor.models.producto.Producto_Table
import com.interedes.agriculturappv3.productor.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.productor.modules.comercial_module.ofertas.events.OfertasEvent
import com.interedes.agriculturappv3.services.listas.Listas
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite

class OfertasRepository : IOfertas.Repository {

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
        postEventListUnidadProductiva(OfertasEvent.LIST_EVENT_UP, listUnidadProductiva, null)
        postEventListLotes(OfertasEvent.LIST_EVENT_LOTE, listLotes, null)
        postEventListCultivos(OfertasEvent.LIST_EVENT_CULTIVO, listCultivos, null)
        postEventListProductos(OfertasEvent.LIST_EVENT_PRODUCTO, listProductos, null)
    }

    override fun getListOfertas(productoId: Long?) {
        val list_static_ofertas = Listas.listStaticOfertas()
        for (item in list_static_ofertas) {
            item.save()
        }
        val listaOfertas = getOfertas(productoId)
        postEventOk(OfertasEvent.READ_EVENT, listaOfertas, null)
    }

    override fun getOfertas(productoId: Long?): List<Oferta> {
        var listResponse: List<Oferta>? = null
        if (productoId == null) {
            listResponse = SQLite.select().from(Oferta::class.java).queryList()
        } else {
            listResponse = SQLite.select().from(Oferta::class.java).where(Oferta_Table.ProductoId.eq(productoId)).queryList()
        }
        return listResponse;
    }

    override fun getProducto(productoId: Long?) {
        val producto = SQLite.select().from(Producto::class.java).where(Producto_Table.Id.eq(productoId)).querySingle()
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

    private fun postEventOk(type: Int, ofertas: List<Oferta>?, oferta: Oferta?) {
        var ofertaListMitable = ofertas as MutableList<Object>
        var ofertaMutable: Object? = null
        if (oferta != null) {
            ofertaMutable = oferta as Object
        }
        postEvent(type, ofertaListMitable, ofertaMutable, null)
    }


    //Main Post Event
    private fun postEvent(type: Int, listModel1: MutableList<Object>?, model: Object?, errorMessage: String?) {
        val event = OfertasEvent(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
}