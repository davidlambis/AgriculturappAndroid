package com.interedes.agriculturappv3.productor.modules.comercial_module.productos

import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.Cultivo
import com.interedes.agriculturappv3.productor.models.Cultivo_Table
import com.interedes.agriculturappv3.productor.models.Lote
import com.interedes.agriculturappv3.productor.models.UnidadProductiva
import com.interedes.agriculturappv3.productor.models.producto.CalidadProducto
import com.interedes.agriculturappv3.productor.models.producto.Producto
import com.interedes.agriculturappv3.productor.models.producto.Producto_Table
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.productor.modules.comercial_module.productos.events.ProductosEvent
import com.interedes.agriculturappv3.services.listas.Listas
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite


class ProductosRepository : IProductos.Repository {
    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
    }

    //region Métodos Interfaz
    override fun getListas() {
        val listUnidadProductiva = SQLite.select().from(UnidadProductiva::class.java).queryList()
        val listLotes = SQLite.select().from(Lote::class.java).queryList()
        val listCultivos = SQLite.select().from(Cultivo::class.java).queryList()
        val listUnidadMedida = Listas.listaUnidadMedida()
        val listCalidadesProducto = Listas.listaCalidadProducto()

        postEventListUnidadMedida(ProductosEvent.LIST_EVENT_UNIDAD_MEDIDA, listUnidadMedida, null)
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
        var listResponse: List<Producto>? = null
        if (cultivo_id == null) {
            listResponse = SQLite.select().from(Producto::class.java).queryList()
        } else {
            listResponse = SQLite.select().from(Producto::class.java).where(Producto_Table.CultivoId.eq(cultivo_id)).queryList()
        }
        return listResponse;
    }

    override fun registerProducto(producto: Producto, cultivo_id: Long) {
        producto.save()
        var listProductos = getProductos(cultivo_id)
        postEventOk(ProductosEvent.SAVE_EVENT,listProductos,null);
    }

    override fun updateProducto(producto: Producto, cultivo_id: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteProducto(producto: Producto, cultivo_id: Long?) {
        producto.delete()
        postEventOk(ProductosEvent.DELETE_EVENT, getProductos(cultivo_id),producto)
    }

    override fun getCultivo(cultivo_id: Long?) {
        val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.Id.eq(cultivo_id)).querySingle()
        postEventOkCultivo(ProductosEvent.GET_EVENT_CULTIVO, cultivo)
    }



    //endregion

    //region Events

    private fun postEventListUnidadMedida(type: Int, listUnidadMedida: List<Unidad_Medida>?, messageError: String?) {
        val upMutable = listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable, null, messageError)
    }

    private fun postEventListUnidadProductiva(type: Int, listUp: List<UnidadProductiva>?, messageError: String?) {
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