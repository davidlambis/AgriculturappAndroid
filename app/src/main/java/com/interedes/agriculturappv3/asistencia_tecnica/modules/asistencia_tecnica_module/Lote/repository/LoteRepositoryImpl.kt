package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.repository

import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.events.RequestEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus

/**
 * Created by EnuarMunoz on 7/03/18.
 */
class LoteRepositoryImpl:LoteRepository {

    var eventBus: EventBus? = null
    init {
        eventBus = GreenRobotEventBus()
    }

    //region METHODS
    override fun registerLote(usuario: Lote) {
        postEventOk(RequestEvent.onRequestOk);
    }

    //endregion

    //region Events
    private fun postEventOk(type: Int) {
        postEvent(type, null)
    }

    private fun postEventError(type: Int,messageError:String) {
        postEvent(type, messageError)
    }

    private fun postEvent(type: Int, errorMessage: String?) {
        val registerEvent = RequestEvent(type, errorMessage)
        registerEvent.eventType = type
        registerEvent.mensajeError = errorMessage
        eventBus?.post(registerEvent)
    }

    //endregion

}