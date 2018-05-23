package com.interedes.agriculturappv3.modules.main_menu.ui

import android.content.Context
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.modules.main_menu.ui.events.RequestEventMainMenu
import com.interedes.agriculturappv3.modules.models.sincronizacion.QuantitySync

/**
 * Created by EnuarMunoz on 8/03/18.
 */
interface MainViewMenu {


    interface MainView {
        fun onConnectivity()
        fun offConnectivity()

        //ProgresHud
        fun showProgressHud()
        fun hideProgressHud()

        fun showProgressBar()
        fun hideProgressBar()

        fun onMessageOk(colorPrimary: Int, message: String?)
        fun onMessageError(colorPrimary: Int, message: String?)

        fun requestResponseOK()
        fun requestResponseError(error: String?)
        //Verifcate conection
        fun verificateConnection(): AlertDialog?
        fun  verificateSync(quantitySync: QuantitySync?): AlertDialog?

        fun setQuantitySync(quantitySync: QuantitySync?)

        fun getListasIniciales()
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy(context: Context)
        fun onResume(context: Context)
        fun onEventMainThread(event: RequestEventMainMenu?)

        //syncData
        fun syncData()
        fun syncQuantityData()
        //Conection
        fun checkConnection(): Boolean?

        //
        fun getListasIniciales()
    }

    interface Interactor {
        fun getListasIniciales()
        fun syncQuantityData()
      fun syncData()
    }


    interface Repository {

        fun getListasIniciales()
        fun syncQuantityData()
        fun syncData()
    }
}