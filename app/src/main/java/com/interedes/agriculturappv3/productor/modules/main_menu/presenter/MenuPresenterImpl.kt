package com.interedes.agriculturappv3.productor.modules.main_menu.presenter

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.interedes.agriculturappv3.AgriculturApplication
import com.interedes.agriculturappv3.productor.modules.main_menu.ui.MainViewMenu
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver

/**
 * Created by EnuarMunoz on 8/03/18.
 */
class MenuPresenterImpl(var menuMainView: MainViewMenu?): ConnectivityReceiver.connectivityReceiverListener, MenuPresenter {

    var connectivityReceiver: ConnectivityReceiver? = null

    override fun onCreate() {
        connectivityReceiver = ConnectivityReceiver()
    }

    override fun onDestroy(context:Context) {
        context.unregisterReceiver(connectivityReceiver)
        menuMainView = null

    }

    override fun onResume(context: Context) {

       /* intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        val connectivityReceiver = ConnectivityReceiver()
        context.registerReceiver(connectivityReceiver, intentFilter)*/
        /*register connection status listener*/
        context.registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        AgriculturApplication.instance.setConnectivityListener(this)
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
            menuMainView?.onConnectivity()
            //Snackbar.make(container, getString(R.string.internet_connected), Snackbar.LENGTH_SHORT).show()
        } else {
            menuMainView?.offConnectivity()
            //Snackbar.make(container, getString(R.string.not_internet_connected), Snackbar.LENGTH_SHORT).show()
        }
    }
}