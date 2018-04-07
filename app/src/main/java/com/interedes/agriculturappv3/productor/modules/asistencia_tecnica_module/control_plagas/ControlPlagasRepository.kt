package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.control_plagas

import com.interedes.agriculturappv3.productor.models.Cultivo
import com.interedes.agriculturappv3.productor.models.Cultivo_Table
import com.interedes.agriculturappv3.productor.models.Lote
import com.interedes.agriculturappv3.productor.models.UnidadProductiva
import com.interedes.agriculturappv3.productor.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.productor.models.control_plaga.ControlPlaga_Table
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.control_plagas.events.ControlPlagasEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite

class ControlPlagasRepository : IControlPlagas.Repository {


    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
    }

    //region Métodos Interfaz
    override fun getListas() {
        val listUnidadProductiva = SQLite.select().from(UnidadProductiva::class.java).queryList()
        val listLotes = SQLite.select().from(Lote::class.java).queryList()
        val listCultivos = SQLite.select().from(Cultivo::class.java).queryList()

        postEventListUnidadProductiva(ControlPlagasEvent.LIST_EVENT_UP, listUnidadProductiva, null)
        postEventListLotes(ControlPlagasEvent.LIST_EVENT_LOTE, listLotes, null)
        postEventListCultivos(ControlPlagasEvent.LIST_EVENT_CULTIVO, listCultivos, null)
    }

    override fun getListControlPlaga(cultivo_id: Long?) {
        val listaControlPlagas = getControlPlagas(cultivo_id)
        postEventOk(ControlPlagasEvent.READ_EVENT, listaControlPlagas, null);
    }

    override fun getControlPlagas(cultivo_id: Long?): List<ControlPlaga> {
        val listResponse: List<ControlPlaga>
        if (cultivo_id == null) {
            listResponse = SQLite.select().from(ControlPlaga::class.java).queryList()
        } else {
            listResponse = SQLite.select().from(ControlPlaga::class.java).where(ControlPlaga_Table.CultivoId.eq(cultivo_id)).queryList()
        }
        return listResponse
    }

    override fun getCultivo(cultivo_id: Long?) {
        val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.Id.eq(cultivo_id)).querySingle()
        postEventOkCultivo(ControlPlagasEvent.GET_EVENT_CULTIVO, cultivo)
    }

    override fun deleteControlPlaga(controlPlaga: ControlPlaga, cultivo_id: Long?) {
        controlPlaga.delete()
        postEventOk(ControlPlagasEvent.DELETE_EVENT, getControlPlagas(cultivo_id),controlPlaga);
    }

    override fun updateControlPlaga(controlPlaga: ControlPlaga?) {
        controlPlaga?.update()

    }
    //endregion

    //region Events
    private fun postEventListUnidadProductiva(type: Int, listUp: List<UnidadProductiva>?, messageError: String?) {
        var upMutable = listUp as MutableList<Object>
        postEvent(type, upMutable, null, messageError)
    }

    private fun postEventListLotes(type: Int, listLote: List<Lote>?, messageError: String?) {
        var loteMutable = listLote as MutableList<Object>
        postEvent(type, loteMutable, null, messageError)
    }

    private fun postEventListCultivos(type: Int, listCultivo: List<Cultivo>?, messageError: String?) {
        var cultivoMutable = listCultivo as MutableList<Object>
        postEvent(type, cultivoMutable, null, messageError)
    }

    private fun postEventOk(type: Int, controlPlagas: List<ControlPlaga>?, controlPlaga: ControlPlaga?) {
        val controlPlagaListMutable = controlPlagas as MutableList<Object>
        var controlPlagaMutable: Object? = null
        if (controlPlaga != null) {
            controlPlagaMutable = controlPlaga as Object
        }
        postEvent(type, controlPlagaListMutable, controlPlagaMutable, null)
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
        val event = ControlPlagasEvent(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
}