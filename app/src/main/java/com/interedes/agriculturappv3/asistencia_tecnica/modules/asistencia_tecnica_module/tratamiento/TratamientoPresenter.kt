package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.tratamiento

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.asistencia_tecnica.models.Cultivo
import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.asistencia_tecnica.models.Tratamiento
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.asistencia_tecnica.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.tratamiento.events.TratamientoEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList

class TratamientoPresenter(var view: ITratamiento.View?) : ITratamiento.Presenter {

    var eventBus: EventBus? = null
    var interactor: ITratamiento.Interactor? = null

    //GLOBALS
    var listUnidadProductivaGlobal: List<UnidadProductiva>? = ArrayList<UnidadProductiva>()
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

    override fun setListSpinnerCultivo(lote_id: Long?, tipoProductoId: Long?) {
        val list = listCultivosGlobal?.filter { cultivo: Cultivo -> cultivo.LoteId == lote_id && cultivo.Id_Tipo_Producto == tipoProductoId }
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
        interactor?.registerControlPlaga(controlPlaga, cultivo_id)
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
        context.registerReceiver(mNotificationReceiver, IntentFilter(Const.SERVICE_CONECTIVITY))
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
            TratamientoEvent.SAVE_CONTROL_PLAGA_EVENT -> {
                val lista_control_plagas = tratamientoEvent.mutableList as List<ControlPlaga>
                view?.loadControlPlagas(lista_control_plagas)
            }

            TratamientoEvent.LIST_EVENT_UP -> {
                listUnidadProductivaGlobal = tratamientoEvent.mutableList as List<UnidadProductiva>
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

        }
    }
    //endregion
}