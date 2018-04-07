package com.interedes.agriculturappv3.productor.modules.main_menu.fragment.presenter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import com.interedes.agriculturappv3.productor.models.usuario.Usuario
import com.interedes.agriculturappv3.productor.modules.main_menu.fragment.interactor.MainMenuFragmentInteractor
import com.interedes.agriculturappv3.productor.modules.main_menu.fragment.interactor.MainMenuFragmentInteractorImpl
import com.interedes.agriculturappv3.productor.modules.main_menu.fragment.ui.MainMenuFragmentView
import com.interedes.agriculturappv3.events.RequestEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
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

    //Cerrar Sesión
    override fun logOut(usuario: Usuario?) {
        if (view?.getConnectivityState()!!) {
            interactor?.logOut(usuario)
        } else {
            interactor?.offlineLogOut(usuario)
        }
    }
    //endregion

    //region Conectividad
    private val mNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val extras: Bundle = intent.extras
            view?.onEventBroadcastReceiver(extras, intent);
        }
    }

    override fun onResume(context: Context) {
        context.registerReceiver(mNotificationReceiver, IntentFilter("CONECTIVIDAD"))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiver);
    }

    //endregion

}