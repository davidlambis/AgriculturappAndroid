package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas

import com.interedes.agriculturappv3.asistencia_tecnica.models.plagas.TipoEnfermedad
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.events.PlagasEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.listas.Listas


class PlagaRepository : IPlaga.Repository {

    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
    }

    //region Interfaz
    override fun getPlagasByTipoProducto(tipoProductoId: Long?) {
        val lista_tipo_enfermedad = Listas.listaTipoEnfermedad()
        val lista = ArrayList<TipoEnfermedad>()
        for (item in lista_tipo_enfermedad) {
            if (item.TipoProductoId == tipoProductoId) {
                lista.add(item)
            }
        }
        postEventOk(PlagasEvent.READ_EVENT, lista, null)
    }


    override fun setPlaga(tipoEnfermedadId: Long?) {
        val lista_tipo_enfermedad = Listas.listaTipoEnfermedad()
        val lista = ArrayList<TipoEnfermedad>()
        for (item in lista_tipo_enfermedad) {
            if (item.Id == tipoEnfermedadId) {
                lista.add(item)
            }
        }
        postEventOk(PlagasEvent.SET_EVENT, lista, null)
    }
    //endregion

    //region Events
    //Main Post Event
    private fun postEventOk(type: Int, listPlagas: List<TipoEnfermedad>?, plaga: TipoEnfermedad?) {
        val plagaListMutable = listPlagas as MutableList<Object>
        var plagaMutable: Object? = null
        if (plaga != null) {
            plagaMutable = plaga as Object
        }
        postEvent(type, plagaListMutable, plagaMutable, null)
    }

    private fun postEvent(type: Int, listModel: MutableList<Object>?, model: Object?, errorMessage: String?) {
        val event = PlagasEvent(type, listModel, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion

}