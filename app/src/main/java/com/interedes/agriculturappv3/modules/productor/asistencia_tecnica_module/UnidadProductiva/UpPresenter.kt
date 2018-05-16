package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.UnidadProductiva

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.UnidadProductiva.events.RequestEventUP
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.departments.Ciudad
import com.interedes.agriculturappv3.modules.models.departments.Departamento
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.coords.CoordsServiceKotlin
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList

class UpPresenter(var IUpView: IUnidadProductiva.View?) : IUnidadProductiva.Presenter {


    var coordsService: CoordsServiceKotlin? = null
    var IUpInteractor: IUnidadProductiva.Interactor? = null
    var eventBus: EventBus? = null
    var listDepartamentoGlobal: List<Departamento>? = ArrayList<Departamento>()
    var listMunicipiosGlobal: List<Ciudad>? = ArrayList<Ciudad>()
    private var serviceCoordsIsRunning:Boolean?=false;

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
        context.registerReceiver(mNotificationReceiver, IntentFilter(Const.SERVICE_CONECTIVITY))
        context.registerReceiver(mNotificationReceiver, IntentFilter(Const.SERVICE_LOCATION))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiver);
    }


    //endregion

    //region Coords Service
    override fun startGps(activity: Activity) {
        coordsService = CoordsServiceKotlin(activity)
        if (CoordsServiceKotlin.instance!!.isLocationEnabled()) {
            IUpView?.showProgressHudCoords()
        }
    }

    override fun getStatusServiceCoords(): Boolean? {
       return serviceCoordsIsRunning
    }

    override fun setStatusServiceCoords(status: Boolean?) {
        serviceCoordsIsRunning=status
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
                IUpView?.setListUps(requestEvent.mutableList as List<Unidad_Productiva>)
            }
            RequestEventUP.SAVE_EVENT -> {
                IUpView?.setListUps(requestEvent.mutableList as List<Unidad_Productiva>)
                closeServiceGps()
                onUPsaveOk()
            }
            RequestEventUP.UPDATE_EVENT -> {
                IUpView?.setListUps(requestEvent.mutableList as List<Unidad_Productiva>)
                onUPUpdateOk()
            }
            RequestEventUP.DELETE_EVENT -> {
                IUpView?.setListUps(requestEvent.mutableList as List<Unidad_Productiva>)
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

        ////EVENTS ONITEM CLICK
            RequestEventUP.ITEM_EVENT -> {
                var unidadProductiva = requestEvent.objectMutable as Unidad_Productiva
                IUpView?.showAlertDialogAddUnidadProductiva(unidadProductiva)
            }

            RequestEventUP.ITEM_EDIT_EVENT -> {
                var unidadProductiva = requestEvent.objectMutable as Unidad_Productiva
                IUpView?.showAlertDialogAddUnidadProductiva(unidadProductiva)
            }

        //List
            RequestEventUP.LIST_EVENT_UNIDAD_MEDIDA -> {
                var list = requestEvent.mutableList as List<Unidad_Medida>
                IUpView?.setListUnidadMedida(list)
            }

            RequestEventUP.LIST_EVENT_DEPARTAMENTOS -> {
                val list_departamentos = requestEvent.mutableList as List<Departamento>
                listDepartamentoGlobal = list_departamentos
            }

            RequestEventUP.LIST_EVENT_CIUDADES -> {
                val list_ciudades = requestEvent.mutableList as List<Ciudad>
                listMunicipiosGlobal = list_ciudades
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


    //region Methods

    override fun validarCampos(): Boolean {
        if (IUpView?.validarCampos() == true) {
            return true
        }
        return false
    }

    override fun registerUP(unidadProductivaModel: Unidad_Productiva?) {
        IUpView?.disableInputs()
        IUpView?.showProgress()
        if (checkConnection()) {
            IUpInteractor?.registerOnlineUP(unidadProductivaModel)
        } else {
            IUpInteractor?.registerUP(unidadProductivaModel)
        }
    }

    override fun updateUP(unidadProductivaModel: Unidad_Productiva?) {
        if (checkConnection()) {
            IUpView?.showProgress()
            IUpInteractor?.updateUP(unidadProductivaModel)
        } else {
            onMessageConectionError()
        }
    }

    override fun deleteUP(unidadProductivaModel: Unidad_Productiva?) {
        IUpView?.showProgress()
        if(unidadProductivaModel?.Estado_Sincronizacion==true){
            if (checkConnection()) {
                IUpInteractor?.deleteUP(unidadProductivaModel)
            } else {
                onMessageConectionError()
            }
        }else{
            IUpInteractor?.deleteUP(unidadProductivaModel)
        }
    }

    override fun getUps() {
        IUpInteractor?.execute()
    }

    override fun getListas() {
        IUpInteractor?.getListas()
    }

    override fun setListDepartamentos() {
        IUpView?.setListSpinnerDepartamentos(listDepartamentoGlobal!!)
    }

    override fun setListMunicipios(departamentoId: Long?) {
        val list = listMunicipiosGlobal?.filter { municipio: Ciudad -> municipio.departmentoId == departamentoId }
        IUpView?.setListSpinnerMunicipios(list!!)
    }

    //endregion


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

    private fun onMessageConectionError() {
        IUpView?.hideProgress()
        IUpView?.verificateConnection()
    }
    //endregion
}