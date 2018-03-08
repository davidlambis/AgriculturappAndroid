package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.presenter

import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.events.RequestEvent
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.interactor.LoteInteractor
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.interactor.LoteInteractorImpl
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.ui.MainViewLote
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

    override fun getLotes() {
        loteInteractor?.execute()
    }


    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(event: RequestEvent?) {
            when (event?.eventType) {
                RequestEvent.READ_EVENT -> {
                    var loteList= event.mutableList as List<Lote>
                    loteMainView?.setListLotes(loteList)
                }
                RequestEvent.SAVE_EVENT ->{
                    var loteList= event.mutableList as List<Lote>
                    loteMainView?.setListLotes(loteList)
                    onLoteSaveOk()
                }
                RequestEvent.UPDATE_EVENT -> {
                    var loteList= event.mutableList as List<Lote>
                    loteMainView?.setListLotes(loteList)
                    onLoteUpdateOk()
                }
                RequestEvent.DELETE_EVENT -> {
                    var loteList= event.mutableList as List<Lote>
                    loteMainView?.setListLotes(loteList)
                    onLoteDeleteOk()
                }
                RequestEvent.ERROR_EVENT -> {
                    onMessageError(event.mensajeError)
                }
        }

    }

    //endregion

    //region Methods

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

    override fun updateLote(lote: Lote) {
        loteMainView?.showProgress()
        loteInteractor?.registerLote(lote)
    }


    override fun deleteLote(lote: Lote) {
        loteMainView?.showProgress()
        loteInteractor?.registerLote(lote)
    }

    //endregion

    //region Acciones de Respuesta a Post de Eventos
    private fun onLoteSaveOk() {
        onMessageOk()
    }

    private fun onLoteUpdateOk() {
        onMessageOk()
    }

    private fun onLoteDeleteOk() {
        loteMainView?.requestResponseOk()
    }


    //endregion


    //region Messages/Notificaciones

    private fun onMessageOk() {
        loteMainView?.enableInputs()
        loteMainView?.hideProgress()
        loteMainView?.limpiarCampos()
        loteMainView?.hideElements()
        loteMainView?.requestResponseOk()
    }

    private fun onMessageError(error: String?) {
        loteMainView?.enableInputs()
        loteMainView?.hideProgress()
        loteMainView?.requestResponseError(error)
    }
    //endregion

}