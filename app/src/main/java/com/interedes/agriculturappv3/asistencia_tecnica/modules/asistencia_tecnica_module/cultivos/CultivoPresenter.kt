package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.asistencia_tecnica.models.Cultivo
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos.events.CultivoEvent
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.up.events.RequestEventUP
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe

class CultivoPresenter(var view: ICultivo.View?) : ICultivo.Presenter {

    var interactor: ICultivo.Interactor? = null
    var eventBus: EventBus? = null

    init {
        interactor = CultivoInteractor()
        eventBus = GreenRobotEventBus()
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

    //region Conectividad
    private val mNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var extras = intent.extras
            view?.onEventBroadcastReceiver(extras, intent);
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

    @Subscribe
    override fun onEventMainThread(cultivoEvent: CultivoEvent?) {
        when (cultivoEvent?.eventType) {
            CultivoEvent.LIST_EVENT_UNIDAD_PRODUCTIVA -> {
                val list_unidad_productiva = cultivoEvent.mutableList as List<UnidadProductiva>
                view?.setListUnidadProductiva(list_unidad_productiva)
            }
            CultivoEvent.LIST_EVENT_UNIDAD_MEDIDA -> {
                val list_unidad_medida = cultivoEvent.mutableList as List<Unidad_Medida>
                view?.setListUnidadMedidas(list_unidad_medida)
            }
        }
    }

    override fun validarCampos(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerCultivo(cultivo: Cultivo?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateCultivo(cultivo: Cultivo?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteCultivo(cultivo: Cultivo?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCultivos() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getListas() {
        interactor?.getListas()
    }

    //endregion

}