package com.interedes.agriculturappv3.modules.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.google.firebase.auth.FirebaseUser
import com.interedes.agriculturappv3.modules.account.events.RequestEventAccount
import com.interedes.agriculturappv3.modules.comprador.detail_producto.events.RequestEventDetalleProducto
import com.interedes.agriculturappv3.modules.models.detalle_metodo_pago.DetalleMetodoPago
import com.interedes.agriculturappv3.modules.models.metodopago.MetodoPago
import com.interedes.agriculturappv3.modules.models.usuario.Usuario

interface IMainViewAccount {

    interface MainView {
        //Progress and progress Hud
        fun showProgress()
        fun hideProgress()
        fun showProgressHud()
        fun hideProgressHud()

        //Validaciones
        fun validarUpdateUser(): Boolean

        //Response Notify
        fun requestResponseOK()
        fun requestResponseError(error: String?)
        fun onMessageOk(colorPrimary: Int, msg: String?)
        fun onMessageError(colorPrimary: Int, msg: String?)

        //Response Api
        fun uplodateFotoUserAccount()

        //Dialog
        fun verificateConnection(): AlertDialog?
        fun setListMetodoPago(listMetodoPago: List<MetodoPago>?)
        fun setListDetalleMetodoPago(listDetalleMetodoPago: List<DetalleMetodoPago>?)


        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)

        //Events
        fun onEventMainThread(requestEvent: RequestEventAccount?)


        //Validacion
        fun validarUpdateUser(): Boolean

        //Set ListSppiner
        fun setListSpinnerMetodoPago()
        fun setListSpinnerDetalleMetodoPago(metodopagoId: Long?)

        //Methods
        fun getUserLogued():Usuario?
        fun updateUserLogued(usuario:Usuario)

        //Conecttion
        fun checkConnection(): Boolean
        fun getListas()


        fun verificateUserLoguedFirebaseFirebase(): FirebaseUser?
        fun changeFotoUserAccount()
    }

    interface Interactor {
        fun getUserLogued():Usuario?
        fun updateUserLogued(usuario:Usuario,checkConction:Boolean)
        fun verificateUserLoguedFirebaseFirebase(): FirebaseUser?
        fun changeFotoUserAccount(checkConction:Boolean)
        fun getListas()
    }

    interface Repository {

        fun changeFotoUserAccount(checkConction:Boolean)
        fun loginFirebase(usuario:Usuario,isUpdatePhotoAccount:Boolean)
        fun verificateUserLoguedFirebaseFirebase(): FirebaseUser?

        fun getUserLogued():Usuario?
        fun updateUserLogued(usuario:Usuario,checkConction:Boolean)

        fun getListas()
    }

}