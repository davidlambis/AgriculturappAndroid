package com.interedes.agriculturappv3.modules.main_menu.ui

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.interedes.agriculturappv3.AgriculturApplication
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.main_menu.ui.MainViewMenu
import com.interedes.agriculturappv3.modules.main_menu.ui.events.RequestEventMainMenu
import com.interedes.agriculturappv3.modules.models.sincronizacion.QuantitySync
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import com.interedes.agriculturappv3.services.jobs.SyncFotoProductosJob
import org.greenrobot.eventbus.Subscribe

/**
 * Created by EnuarMunoz on 8/03/18.
 */
class MenuPresenterImpl(var mainView: MainViewMenu.MainView?): ConnectivityReceiver.connectivityReceiverListener, MainViewMenu.Presenter {


    private var INSTANCE_CONECTION: Boolean = false

    var eventBus: EventBus? = null
    var connectivityReceiver: ConnectivityReceiver? = null
    var interactor: MainViewMenu.Interactor? = null


    init {

        interactor = MenuInteractor()
        eventBus = GreenRobotEventBus()
    }

    override fun onCreate() {
        eventBus?.register(this)
        connectivityReceiver = ConnectivityReceiver()


    }

    override fun onDestroy(context:Context) {
        context.unregisterReceiver(connectivityReceiver)
        mainView = null
        eventBus?.unregister(this)

    }

    override fun onResume(context: Context) {

       /* intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        val connectivityReceiver = ConnectivityReceiver()
        context.registerReceiver(connectivityReceiver, intentFilter)*/
        /*register connection status listener*/

        if(!INSTANCE_CONECTION){
            context.registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
            AgriculturApplication.instance.setConnectivityListener(this)
            INSTANCE_CONECTION=true
        }
    }


    //region Conectividad a Internet
    //Conexión a Internet

    //Revisar manualmente
    override fun checkConnection(): Boolean? {
        return ConnectivityReceiver.isConnected
        //showSnack(isConnected);
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            mainView?.onConnectivity()
            //Snackbar.make(container, getString(R.string.internet_connected), Snackbar.LENGTH_SHORT).show()
        } else {
            mainView?.offConnectivity()
            //Snackbar.make(container, getString(R.string.not_internet_connected), Snackbar.LENGTH_SHORT).show()
        }
    }


    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(event: RequestEventMainMenu?) {
        when (event?.eventType) {
            RequestEventMainMenu.SYNC_EVENT -> {
                onMessageOk()
            }
            RequestEventMainMenu.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

            RequestEventMainMenu.SYNC_RESUME_AUTOMATIC -> {
                //onMessageError(event.mensajeError)
                val quantitySync = event.objectMutable as QuantitySync
                mainView?.setQuantitySyncAutomatic(quantitySync)
            }

            RequestEventMainMenu.UPDATE_BADGE_NOTIIFCATIONS -> {
                //onMessageError(event.mensajeError)
                mainView?.updateCountNotifications()
            }

            RequestEventMainMenu.ERROR_VERIFICATE_CONECTION -> {
                mainView?.verificateConnection()
            }

            RequestEventMainMenu.ERROR_VERIFICATE_CONECTION -> {
                mainView?.verificateConnection()
            }



            /*RequestEventMainMenu.SYNC_FOTOS_INSUMOS_PLAGAS -> {
                mainView?.syncFotosInsumosPlagas()
            }*/

            RequestEventMainMenu.SYNC_RESUME -> {
                val quantitySync = event.objectMutable as QuantitySync
                mainView?.setQuantitySync(quantitySync)
                mainView?.hideProgressHud()
            }

            RequestEventMainMenu.SYNC_FOTOS_PRODUCTOS -> {
                SyncFotoProductosJob.scheduleFotosProductosJob()
            }
        }
    }
    //endregion


    //region REQUEST REPOSITORY
    override fun getListasIniciales() {
        if (checkConnection()!!) {
            interactor?.getListasIniciales()
        }
    }



    override fun checkListPlagas(): Long {
        return  interactor?.checkListPlagas()!!
    }

    override fun syncQuantityData(automatic:Boolean) {
        //mainView?.showProgressHud()
        interactor?.syncQuantityData(automatic)
    }


    override fun getLastUserLogued(): Usuario? {
        //mainView?.showProgressHud()
        return  interactor?.getLastUserLogued()
    }


    override fun navigateChatOnline( usuario: Usuario) {
        interactor?.navigateChatOnline(checkConnection()!!,usuario)
    }

    override fun navigateChatOnlineNotification(uiFirebase: String) {
        interactor?.navigateChatOnlineNotification(uiFirebase)
    }

    /*override fun getListSyncEnfermedadesAndTratamiento() {
        mainView?.showProgressBar()
        interactor?.getListSyncEnfermedadesAndTratamiento()
    }*/

    override fun makeUserOnline(context: Context) {
        interactor?.makeUserOnline(checkConnection()!!,context)
    }

    override fun makeUserOffline(context: Context) {
        interactor?.makeUserOffline(checkConnection()!!,context)
    }

    //Online
    override fun logOut(usuario: Usuario?) {
        interactor?.logOut(usuario)
    }


    override fun getCountNotifications(): Int {
       return  interactor?.getCountNotifications()!!
    }
    //endregion

    private fun onMessageOk() {
        mainView?.hideProgressBar()
        mainView?.requestResponseOK()
    }

    private fun onMessageError(error: String?) {
        mainView?.hideProgressBar()
        mainView?.requestResponseError(error)
    }

}