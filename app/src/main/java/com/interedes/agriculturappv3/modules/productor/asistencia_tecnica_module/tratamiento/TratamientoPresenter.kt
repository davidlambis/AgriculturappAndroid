package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.tratamiento

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.tratamiento.Tratamiento
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.tratamiento.events.TratamientoEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.insumos.Insumo
import com.interedes.agriculturappv3.modules.models.tratamiento.calificacion.Calificacion_Tratamiento
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.services.resources.Const_Resources
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList

class TratamientoPresenter(var view: ITratamiento.View?) : ITratamiento.Presenter {

    var eventBus: EventBus? = null
    var interactor: ITratamiento.Interactor? = null

    //GLOBALS
    var listUnidadProductivaGlobal: List<Unidad_Productiva>? = ArrayList<Unidad_Productiva>()
    var listLoteGlobal: List<Lote>? = ArrayList<Lote>()
    var listCultivosGlobal: List<Cultivo>? = ArrayList<Cultivo>()
    var listUnidadMedidaGlobal: List<Unidad_Medida>? = ArrayList<Unidad_Medida>()

    init {
        eventBus = GreenRobotEventBus()
        interactor = TratamientoInteractor()
    }




    //region Métodos Interfaz
    override fun onCreate() {
        eventBus?.register(this)
        getListas()
    }

    override fun onDestroy() {
        view = null
        eventBus?.unregister(this)
    }

    override fun getTratamiento(insumoId: Long?) {
        interactor?.getTratamiento(insumoId)
    }

    override fun getListas() {
        interactor?.getListas()
    }

    override fun setListSpinnerUnidadProductiva() {
        view?.setListUnidadProductiva(listUnidadProductivaGlobal)
    }

    override fun setListSpinnerLote(unidad_productiva_id: Long?) {
        val list = listLoteGlobal?.filter { lote: Lote -> lote.Unidad_Productiva_Id == unidad_productiva_id }
        view?.setListLotes(list)
    }

    override fun setListSpinnerCultivo(lote_id: Long?) {
        val list = listCultivosGlobal?.filter { cultivo: Cultivo -> cultivo.LoteId == lote_id }
        view?.setListCultivos(list)
    }

    override fun validarListasAddControlPlaga(): Boolean? {
        if (view?.validarListasAddControlPlaga() == true) {
            return true
        }
        return false
    }

    override fun setListSpinnerUnidadMedida() {
        view?.setListUnidadMedida(listUnidadMedidaGlobal)
    }

    override fun validarCampos(): Boolean? {
        if (view?.validarCampos() == true) {
            return true
        }
        return false
    }

    override fun registerControlPlaga(controlPlaga: ControlPlaga, cultivo_id: Long?) {
        view?.showProgress()
        view?.disableInputs()
        interactor?.registerControlPlaga(controlPlaga, cultivo_id,checkConnection())
    }

    //region Conectividad
    private val mNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var extras = intent.extras
            if (extras != null) {
                view?.onEventBroadcastReceiver(extras, intent)
            }
        }
    }

    override fun checkConnection(): Boolean {
        return ConnectivityReceiver.isConnected
    }

    override fun onResume(context: Context) {
        context.registerReceiver(mNotificationReceiver, IntentFilter(Const_Resources.SERVICE_CONECTIVITY))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiver);
    }
    //endregion

    @Subscribe
    override fun onEventMainThread(tratamientoEvent: TratamientoEvent?) {
        when (tratamientoEvent?.eventType) {
            TratamientoEvent.SET_EVENT -> {
                val tratamiento = tratamientoEvent.objectMutable as Tratamiento
                view?.setTratamiento(tratamiento)
            }

            TratamientoEvent.EVENT_CALIFICACION_TRATAMIENTO -> {
                val calificacion = tratamientoEvent.objectMutable as Calificacion_Tratamiento
                if(calificacion.User_Id!=null){
                    view?.setDisabledCalificacion(calificacion)
                }else{
                    view?.setEnabledCalificacion(calificacion)
                }
            }

            TratamientoEvent.EVENT_INSUMO -> {
                val insumo = tratamientoEvent.objectMutable as Insumo
                view?.setInsumo(insumo)
            }

            TratamientoEvent.SAVE_CONTROL_PLAGA_EVENT -> {
                val lista_control_plagas = tratamientoEvent.mutableList as List<ControlPlaga>
                view?.loadControlPlagas(lista_control_plagas)
            }

            TratamientoEvent.LIST_EVENT_UP -> {
                listUnidadProductivaGlobal = tratamientoEvent.mutableList as List<Unidad_Productiva>
            }

            TratamientoEvent.LIST_EVENT_LOTE -> {
                listLoteGlobal = tratamientoEvent.mutableList as List<Lote>
            }

            TratamientoEvent.LIST_EVENT_CULTIVO -> {
                listCultivosGlobal = tratamientoEvent.mutableList as List<Cultivo>
            }

            TratamientoEvent.LIST_EVENT_UNIDAD_MEDIDA -> {
                listUnidadMedidaGlobal = tratamientoEvent.mutableList as List<Unidad_Medida>
            }

            TratamientoEvent.ERROR_EVENT -> {
                onMessageError(tratamientoEvent.mensajeError)
            }
            TratamientoEvent.CALIFICATE_EVENT_EXIST -> {
                onMessageCalificateExistOk()
            }

            TratamientoEvent.REQUEST_CALIFICATE_EVENT_TRATAMIENTO_OK -> {
                onMessageOk()
            }

        }
    }

    override fun sendCalificationTratamietno(tratamiento: Tratamiento?, calificacion: Double?) {
        view?.showProgress()
        if(checkConnection()){
            interactor?.sendCalificationTratamietno(tratamiento,calificacion)
        }else{

            onMessageConectionError()
        }
    }

    //endregion

    //region METHODS RESPONSE

    private fun onMessageOk() {
        view?.hideProgress()
        view?.hideProgressHud()
        view?.requestResponseOk()
    }

    private fun onMessageCalificateExistOk() {
        view?.hideProgress()
        view?.hideProgressHud()
        view?.requestResponseExistCalicated()
    }

    private fun onMessageError(error: String?) {
        view?.enableInputs()
        view?.hideProgress()
        view?.hideProgressHud()
        view?.requestResponseError(error)
    }

    private fun onMessageConectionError() {
        view?.hideProgress()
        view?.hideProgressHud()
        view?.verificateConnection()
    }
    //endregion
}