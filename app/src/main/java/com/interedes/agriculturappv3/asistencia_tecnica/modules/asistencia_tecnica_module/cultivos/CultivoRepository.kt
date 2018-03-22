package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos

import com.interedes.agriculturappv3.asistencia_tecnica.models.*
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos.events.CultivoEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.listas.Listas
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite

class CultivoRepository : ICultivo.Repository {


    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
    }

    //region Métodos Interfaz
    override fun getListas() {
        //get Unidades Productivas
        val listUnidadesProductivas: List<UnidadProductiva> = SQLite.select().from(UnidadProductiva::class.java).queryList()
        if (listUnidadesProductivas.size > 0) {
            postEventListUnidadProductiva(CultivoEvent.LIST_EVENT_UNIDAD_PRODUCTIVA, listUnidadesProductivas, null)
        } else {
            postEventError(CultivoEvent.ERROR_DIALOG_EVENT, "No hay Unidades productivas registradas")
        }
        val listTipoProducto = Listas.listaTipoProducto()
        postEventListTipoProducto(CultivoEvent.LIST_EVENT_TIPO_PRODUCTO, listTipoProducto, null)
        val listUnidadMedida = Listas.listaUnidadMedida()
        postEventListUnidadMedida(CultivoEvent.LIST_EVENT_UNIDAD_MEDIDA, listUnidadMedida, null)
    }

    override fun loadLotesSpinner(unidadProductivaId: Long?) {
        val listLotes: List<Lote> = SQLite.select().from(Lote::class.java).where(Lote_Table.Unidad_Productiva_Id.eq(unidadProductivaId)).queryList()
        if (listLotes.size > 0) {
            postEventListLotes(CultivoEvent.LIST_EVENT_LOTES, listLotes, null)
        } else {
            postEventError(CultivoEvent.ERROR_DIALOG_EVENT, "No hay lotes Registrados")
        }
    }

    override fun loadLotesSpinnerSearch(unidadProductivaId: Long?) {
        val listLotes: List<Lote> = SQLite.select().from(Lote::class.java).where(Lote_Table.Unidad_Productiva_Id.eq(unidadProductivaId)).queryList()
        if (listLotes.size > 0) {
            postEventListLotes(CultivoEvent.LIST_EVENT_LOTES_SEARCH, listLotes, null)
        } else {
            postEventError(CultivoEvent.ERROR_EVENT, "No hay lotes Registrados")
        }
    }

    override fun loadDetalleTipoProducto(tipoProductoId: Long?) {
        val listDetalleTipoProducto = ArrayList<DetalleTipoProducto>()
        for (item in Listas.listaDetalleTipoProducto()) {
            if (item.TipoProductoId == tipoProductoId) {
                listDetalleTipoProducto.add(item)
            }
        }
        postEventListDetalleTipoProducto(CultivoEvent.LIST_EVENT_DETALLE_TIPO_PRODUCTO, listDetalleTipoProducto, null)

    }

    override fun searchCultivos(loteId: Long?) {
        val list_cultivos = getCultivosByLote(loteId)
        postEventOk(CultivoEvent.SEARCH_EVENT, list_cultivos, null)
    }

    override fun getListAllCultivos() {
        val list_all_cultivos = getAllCultivos()
        postEventOk(CultivoEvent.READ_EVENT, list_all_cultivos, null)
    }

    override fun saveCultivo(cultivo: Cultivo) {
        cultivo.save()
        postEventOk(CultivoEvent.SAVE_EVENT, getAllCultivos(), cultivo)
    }

    override fun getCultivosByLote(loteId: Long?): List<Cultivo> {
        return SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.LoteId.eq(loteId)).queryList()
    }

    override fun updateCultivo(cultivo: Cultivo) {
        cultivo.update()
        postEventOk(CultivoEvent.UPDATE_EVENT, getCultivosByLote(cultivo.LoteId), cultivo)
    }

    override fun deleteCultivo(cultivo: Cultivo) {
        cultivo.delete()
        postEventOk(CultivoEvent.DELETE_EVENT, getCultivosByLote(cultivo.LoteId), cultivo)
    }
    //endregion

    //region Métodos
    fun getAllCultivos(): List<Cultivo> {
        return SQLite.select().from(Cultivo::class.java).queryList()
    }
    //endregion

    //region Events
    private fun postEventListUnidadProductiva(type: Int, listUnidadProductiva: List<UnidadProductiva>?, messageError: String?) {
        val unidadProductivaMutable = listUnidadProductiva as MutableList<Object>
        postEvent(type, unidadProductivaMutable, null, messageError)
    }

    private fun postEventListTipoProducto(type: Int, listTipoProducto: List<TipoProducto>?, messageError: String?) {
        val tipoProductoMutable = listTipoProducto as MutableList<Object>
        postEvent(type, tipoProductoMutable, null, messageError)
    }

    private fun postEventListDetalleTipoProducto(type: Int, listDetalleTipoProducto: List<DetalleTipoProducto>?, messageError: String?) {
        val detalleTipoProductoMutable = listDetalleTipoProducto as MutableList<Object>
        postEvent(type, detalleTipoProductoMutable, null, messageError)
    }

    private fun postEventListUnidadMedida(type: Int, listUnidadMedida: List<Unidad_Medida>?, messageError: String?) {
        val unidadMedidaMutable = listUnidadMedida as MutableList<Object>
        postEvent(type, unidadMedidaMutable, null, messageError)
    }

    private fun postEventListLotes(type: Int, listLote: List<Lote>?, messageError: String?) {
        val loteMutable = listLote as MutableList<Object>
        postEvent(type, loteMutable, null, messageError)
    }

    private fun postEventError(type: Int, messageError: String?) {
        val event = CultivoEvent(type, null, null, messageError)
        event.eventType = type
        eventBus?.post(event)
    }

    private fun postEventOk(type: Int, listCultivos: List<Cultivo>?, cultivo: Cultivo?) {
        val cultivoListMutable = listCultivos as MutableList<Object>
        var cultivoMutable: Object? = null
        if (cultivo != null) {
            cultivoMutable = cultivo as Object
        }
        postEvent(type, cultivoListMutable, cultivoMutable, null)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel: MutableList<Object>?, model: Object?, errorMessage: String?) {
        val event = CultivoEvent(type, listModel, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }

    //endregion

}