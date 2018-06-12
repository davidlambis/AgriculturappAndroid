package com.interedes.agriculturappv3.modules.main_menu.fragment.presenter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.main_menu.fragment.interactor.MainMenuFragmentInteractor
import com.interedes.agriculturappv3.modules.main_menu.fragment.interactor.MainMenuFragmentInteractorImpl
import com.interedes.agriculturappv3.modules.main_menu.fragment.ui.MainMenuFragmentView
import com.interedes.agriculturappv3.events.RequestEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe


class MainMenuFragmentPresenterImpl(var view: MainMenuFragmentView?) : MainMenuFragmentPresenter {


    var eventBus: EventBus? = null
    var interactor: MainMenuFragmentInteractor? = null

    init {
        eventBus = GreenRobotEventBus()
        interactor = MainMenuFragmentInteractorImpl()
    }

    //region Métodos Interfaz
    override fun onCreate() {
        eventBus?.register(this)
    }

    override fun onDestroy() {
        view = null
        eventBus?.unregister(this)
    }

    @Subscribe
    override fun onEventMainThread(event: RequestEvent?) {
        when (event?.eventType) {
            RequestEvent.ERROR_EVENT -> {
                view?.errorLogOut(event.mensajeError)
            }

            RequestEvent.UPDATE_EVENT -> {
                view?.navigateToLogin()
            }
        }
    }

    //endregion

    //region Conectividad
    //region Conectividad
    private val mNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var extras = intent.extras
            if(extras!=null){
                view?.onEventBroadcastReceiver(extras, intent);
            }

        }
    }

    override fun checkConnection(): Boolean {
        return ConnectivityReceiver.isConnected
        //showSnack(isConnected);
    }

    override fun onResume(context: Context) {
        /*context.registerReceiver(mNotificationReceiver, IntentFilter("CONECTIVIDAD"))
        context.registerReceiver(mNotificationReceiver, IntentFilter("LOCATION"))*/


        context.registerReceiver(mNotificationReceiver, IntentFilter(Const.SERVICE_CONECTIVITY))
        context.registerReceiver(mNotificationReceiver, IntentFilter(Const.SERVICE_LOCATION))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiver);
    }

    //endregion

}