package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos

import com.interedes.agriculturappv3.asistencia_tecnica.models.Cultivo
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos.events.CultivoEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.listas.Listas
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
        val listUnidadMedida = Listas.listaUnidadMedida()
        postEventListUnidadProductiva(CultivoEvent.LIST_EVENT_UNIDAD_PRODUCTIVA, listUnidadesProductivas, null)
        postEventListUnidadMedida(CultivoEvent.LIST_EVENT_UNIDAD_MEDIDA, listUnidadMedida, null)
    }

    override fun getListCultivos() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCultivos(): List<Cultivo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveCultivo(cultivo: Cultivo) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateCultivo(cultivo: Cultivo) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteCultivo(cultivo: Cultivo) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    //endregion

    //region Events
    private fun postEventListUnidadProductiva(type: Int, listUnidadProductiva: List<UnidadProductiva>?, messageError: String?) {
        val unidadProductivaMutable = listUnidadProductiva as MutableList<Any>
        postEvent(type, unidadProductivaMutable, null, messageError)
    }

    private fun postEventListUnidadMedida(type: Int, listUnidadMedida: List<Unidad_Medida>?, messageError: String?) {
        val unidadMedidaMutable = listUnidadMedida as MutableList<Any>
        postEvent(type, unidadMedidaMutable, null, messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel: MutableList<Any>?, model: Any?, errorMessage: String?) {
        val event = CultivoEvent(type, listModel, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }

    //endregion

}