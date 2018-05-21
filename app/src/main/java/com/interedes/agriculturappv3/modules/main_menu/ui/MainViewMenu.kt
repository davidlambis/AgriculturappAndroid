package com.interedes.agriculturappv3.modules.main_menu.ui

import android.content.Context
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.modules.main_menu.ui.events.RequestEventMainMenu

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

        fun onMessageOk(colorPrimary: Int, message: String?)
        fun onMessageError(colorPrimary: Int, message: String?)

        fun requestResponseOK()
        fun requestResponseError(error: String?)
        //Verifcate conection
        fun verificateConnection(): AlertDialog?
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy(context: Context)
        fun onResume(context: Context)
        fun onEventMainThread(event: RequestEventMainMenu?)

        //syncData
        fun syncData()

        //Conection
        fun checkConnection(): Boolean?
    }

    interface Interactor {
      fun syncData()
    }


    interface Repository {
        fun syncData()
    }
}