package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.presenter

import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.interactor.LoteInteractor
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.interactor.LoteInteractorImpl
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.ui.MainViewLote
import com.interedes.agriculturappv3.events.RegisterEvent
import com.interedes.agriculturappv3.events.RequestEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by EnuarMunoz on 7/03/18.
 */
class LotePresenterImpl(var loteMainView: MainViewLote?): LotePresenter {

    var loteInteractor: LoteInteractor? = null
    var eventBus: EventBus? = null

    init {
        loteInteractor=LoteInteractorImpl()
        eventBus = GreenRobotEventBus()
    }

    override fun onCreate() {
        eventBus?.register(this)
    }

    override fun onDestroy() {
        loteMainView = null
    }

    @Subscribe
    override fun onEventMainThread(event: RequestEvent?) {
        when (event?.eventType) {
            RequestEvent.onRequestOk -> {
                onRegistroOk()
            }
            RequestEvent.onRequestError -> {
                onRegistroError(event.mensajeError!!)
            }
        }

    }

    override fun validarCampos(): Boolean? {
        if (loteMainView?.validarCampos() == true) {
            return true
        }
        return false
    }

    override fun registerLote(lote: Lote) {
        loteMainView?.disableInputs()
        loteMainView?.showProgress()
        loteInteractor?.registerLote(lote)

    }

    //Acciones de Respuesta a Post de Eventos
    private fun onRegistroOk() {
        loteMainView?.enableInputs()
        loteMainView?.hideProgress()
        loteMainView?.limpiarCampos()
        loteMainView?.hideElements()
        loteMainView?.requestResponseOk()
    }

    private fun onRegistroError(error: String) {
        loteMainView?.enableInputs()
        loteMainView?.hideProgress()
        loteMainView?.requestResponseError(error)
    }
}