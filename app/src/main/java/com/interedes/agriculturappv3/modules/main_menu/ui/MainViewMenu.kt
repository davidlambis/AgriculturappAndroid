package com.interedes.agriculturappv3.modules.main_menu.ui

import android.content.Context
import android.support.v7.app.AlertDialog
import com.google.firebase.auth.FirebaseUser
import com.interedes.agriculturappv3.modules.main_menu.ui.events.RequestEventMainMenu
import com.interedes.agriculturappv3.modules.models.sincronizacion.QuantitySync
import com.interedes.agriculturappv3.modules.models.usuario.Usuario

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
        fun onMessageToast(colorPrimary: Int, message: String?)

        fun requestResponseOK()
        fun requestResponseError(error: String?)

        //Alerts Dialogs
        //Verifcate conection
        fun verificateConnection(): AlertDialog?
        fun  verificateSync(quantitySync: QuantitySync?): AlertDialog?
       // fun showAlertTypeChat(): AlertDialog?
        fun showAlertTypeChat()
        fun showAlertDialogSyncDataConfirm()

        fun setQuantitySync(quantitySync: QuantitySync?)
        fun setQuantitySyncAutomatic(quantitySync: QuantitySync?)

        fun updateCountNotifications()

        fun getListasIniciales()

        ///fun syncFotosInsumosPlagas()
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy(context: Context)
        fun onResume(context: Context)
        fun onEventMainThread(event: RequestEventMainMenu?)

        //syncData
        fun syncQuantityData(automatic:Boolean)

        fun makeUserOnline(context: Context)
        fun makeUserOffline(context: Context)
        fun logOut(usuario: Usuario?)

        //Conection
        fun checkConnection(): Boolean?

        //
        fun getListasIniciales()
        fun getLastUserLogued(): Usuario?
        //fun getListSyncEnfermedadesAndTratamiento()


        //Notifications
        fun getCountNotifications():Int


    }

    interface Interactor {
        fun makeUserOnline(checkConection:Boolean,context: Context)
        fun makeUserOffline(checkConection:Boolean,context: Context)
        fun getListasIniciales()
        fun syncQuantityData(automatic:Boolean)
        fun getLastUserLogued(): Usuario?
        //fun getListSyncEnfermedadesAndTratamiento()

        //Notifications
        fun getCountNotifications():Int


        fun logOut(usuario: Usuario?)
    }


    interface Repository {
        //firebase
        fun makeUserOnline(checkConection:Boolean,context: Context)
        fun makeUserOffline(checkConection:Boolean,context: Context)
        fun verificateUserLoguedFirebaseFirebase(): FirebaseUser?
        fun loginFirebase(usuario:Usuario,context: Context)
        fun logOut(usuario: Usuario?)


        //Notifications
        fun getCountNotifications():Int


        fun getListasIniciales()
        fun syncQuantityData(automatic:Boolean)
        fun getLastUserLogued(): Usuario?
       // fun getListSyncEnfermedadesAndTratamiento()
    }
}