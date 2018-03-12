package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.up

import com.interedes.agriculturappv3.asistencia_tecnica.models.UP
import com.interedes.agriculturappv3.events.RequestEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus

class UpPresenter(var IUpView: IUnidadProductiva.View?): IUnidadProductiva.Presenter{

    var IUpModel: IUnidadProductiva.Model? = null
    var eventBus : EventBus ?=null

    init {
        IUpModel = UpModel()
        eventBus = GreenRobotEventBus()
    }

    override fun onCreate() {
        eventBus?.register(this)
    }

    override fun onDestroy() {
        IUpView=null
    }

    override fun onEventMainThread(requestEvent: RequestEvent?) {
        when (requestEvent?.eventType){
            RequestEvent.READ_EVENT -> {
                IUpView?.setListUps(requestEvent.mutableList as List<UP>)
            }
            RequestEvent.SAVE_EVENT -> {
                IUpView?.setListUps(requestEvent.mutableList as List<UP>)
                onUPsaveOk()
            }
            RequestEvent.UPDATE_EVENT -> {
                IUpView?.setListUps(requestEvent.mutableList as List<UP>)
                onUPUpdateOk()
            }
            RequestEvent.DELETE_EVENT -> {
                IUpView?.setListUps(requestEvent.mutableList as List<UP>)
                onUPDeleteOk()
            }
            RequestEvent.ERROR_EVENT -> {
                onMessageError(requestEvent.mensajeError)
            }
        }
    }

    //region Acciones de Respuesta a Post de Eventos
    private fun onUPsaveOk() {
        onMessageOk()
    }

    private fun onUPUpdateOk() {
        onMessageOk()
    }

    private fun onUPDeleteOk() {
        IUpView?.requestResponseOK()
    }

    //region Messages/Notificaciones

    private fun onMessageOk() {
        IUpView?.enableInputs()
        IUpView?.hideProgress()
        IUpView?.limpiarCampos()
        IUpView?.hideElements()
        IUpView?.requestResponseOK()
    }

    private fun onMessageError(error: String?) {
        IUpView?.enableInputs()
        IUpView?.hideProgress()
        IUpView?.requestResponseError(error)
    }
    //endregion

    override fun validarCampos(): Boolean {
        if (IUpView?.validarCampos()==true){
            return true
        }
        return false
    }

    override fun registerUP(UpModel: UP?) {
        IUpView?.disableInputs()
        IUpView?.showProgress()
        IUpModel?.registerUP(UpModel)
    }

    override fun updateUP(UpModel: UP?) {
        IUpView?.showProgress()
        IUpModel?.registerUP(UpModel)
    }

    override fun deleteUP(UpModel: UP?) {
        IUpView?.showProgress()
        IUpModel?.registerUP(UpModel)
    }

    override fun getUps() {
        IUpModel?.execute()
    }
}