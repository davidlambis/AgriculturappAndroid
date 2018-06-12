package com.interedes.agriculturappv3.modules.productor.comercial_module.clientes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.productor.comercial_module.clientes.events.ClientesEvent
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe


class ClientesPresenter(var view: IClientes.View?) : IClientes.Presenter {

    var interactor: IClientes.Interactor? = null
    var eventBus: EventBus? = null

    companion object {
        var instance: ClientesPresenter? = null
    }

    init {
        instance = this
        interactor = ClientesInteractor()
        eventBus = GreenRobotEventBus()
    }

    //region Métodos Interfaz
    override fun onCreate() {
        eventBus?.register(this)
    }

    override fun onDestroy() {
        view = null
        eventBus?.unregister(this)
    }

    //region Conectividad
    private val mNotificationReceiverApp = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val extras = intent.extras
            if (extras != null) {
                view?.onEventBroadcastReceiver(extras, intent)
            }
        }
    }

    override fun checkConnection(): Boolean {
        return ConnectivityReceiver.isConnected
    }

    override fun onResume(context: Context) {
        context.registerReceiver(mNotificationReceiverApp, IntentFilter(Const.SERVICE_CONECTIVITY))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiverApp)
    }

    @Subscribe
    override fun onEventMainThread(event: ClientesEvent?) {
        when (event?.eventType) {
            ClientesEvent.READ_EVENT -> {
                val list = event.mutableList as List<Usuario>
                view?.setListClientes(list)
            }
        }
    }

    override fun getListClientes() {
        interactor?.getListClientes()
    }
    //endregion


}