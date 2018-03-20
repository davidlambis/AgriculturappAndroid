package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.up

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.up.events.RequestEventUP
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.coords.CoordsServiceKotlin
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe

class UpPresenter(var IUpView: IUnidadProductiva.View?) : IUnidadProductiva.Presenter {


    var coordsService: CoordsServiceKotlin? = null
    var IUpInteractor: IUnidadProductiva.Interactor? = null
    var eventBus: EventBus? = null

    init {
        IUpInteractor = UpInteractor()
        eventBus = GreenRobotEventBus()
    }

    override fun onCreate() {
        eventBus?.register(this)
        getListas()

    }

    override fun onDestroy() {
        IUpView = null
        eventBus?.unregister(this)
    }

    //region Conectividad
    private val mNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var extras = intent.extras
            IUpView?.onEventBroadcastReceiver(extras, intent);
        }
    }

    override fun checkConnection(): Boolean {
        return ConnectivityReceiver.isConnected
        //showSnack(isConnected);
    }

    override fun onResume(context: Context) {
        context.registerReceiver(mNotificationReceiver, IntentFilter("CONECTIVIDAD"))
        context.registerReceiver(mNotificationReceiver, IntentFilter("LOCATION"))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiver);
    }


    //endregion

    //region Coords Service
    override fun startGps(activity: Activity) {
        coordsService = CoordsServiceKotlin(activity)
        if (CoordsServiceKotlin.instance!!.isLocationEnabled()) {
            IUpView?.showProgressHud()
        }
    }

    override fun closeServiceGps() {
        if (coordsService != null) {
            //CoordsService.instance?.closeService()
            CoordsServiceKotlin.instance?.closeService()
            //coordsService!!.closeService()
        }
    }
    //endregion

    @Subscribe
    override fun onEventMainThread(requestEvent: RequestEventUP?) {
        when (requestEvent?.eventType) {
            RequestEventUP.READ_EVENT -> {
                IUpView?.setListUps(requestEvent.mutableList as List<UnidadProductiva>)
            }
            RequestEventUP.SAVE_EVENT -> {
                IUpView?.setListUps(requestEvent.mutableList as List<UnidadProductiva>)
                closeServiceGps()
                onUPsaveOk()
            }
            RequestEventUP.UPDATE_EVENT -> {
                IUpView?.setListUps(requestEvent.mutableList as List<UnidadProductiva>)
                onUPUpdateOk()
            }
            RequestEventUP.DELETE_EVENT -> {
                IUpView?.setListUps(requestEvent.mutableList as List<UnidadProductiva>)
                onUPDeleteOk()
            }
            RequestEventUP.ERROR_EVENT -> {
                onMessageError(requestEvent.mensajeError)
            }


            RequestEventUP.ADD_LOCATION_EVENT -> {
                IUpView?.requestResponseOK()
            }
            RequestEventUP.ADD_POLIGON_EVENT -> {
                IUpView?.requestResponseOK()
            }

            RequestEventUP.LIST_EVENT_UNIDAD_MEDIDA -> {
                var list = requestEvent.mutableList as List<Unidad_Medida>
                IUpView?.setListUnidadMedida(list)

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

    //region Methods

    override fun validarCampos(): Boolean {
        if (IUpView?.validarCampos() == true) {
            return true
        }
        return false
    }

    override fun registerUP(unidadProductivaModel: UnidadProductiva?) {
        IUpView?.disableInputs()
        IUpView?.showProgress()
        IUpInteractor?.registerUP(unidadProductivaModel)
    }

    override fun updateUP(unidadProductivaModel: UnidadProductiva?) {
        IUpView?.showProgress()
        IUpInteractor?.registerUP(unidadProductivaModel)
    }

    override fun deleteUP(unidadProductivaModel: UnidadProductiva?) {
        IUpView?.showProgress()
        IUpInteractor?.registerUP(unidadProductivaModel)
    }

    override fun getUps() {
        IUpInteractor?.execute()
    }

    override fun getListas() {
        IUpInteractor?.getListas()
    }

    //endregion
}