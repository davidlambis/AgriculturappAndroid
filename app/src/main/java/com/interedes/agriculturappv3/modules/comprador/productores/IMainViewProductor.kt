package com.interedes.agriculturappv3.modules.comprador.productores

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.modules.comprador.productores.events.RequestEventProductor
import com.interedes.agriculturappv3.modules.models.producto.Producto

interface IMainViewProductor {

    interface MainView {
        //Progress and progress Hud
        fun showProgress()
        fun hideProgress()
        fun showProgressHud()
        fun hideProgressHud()

        //Fun  CRUD
        fun setListProducto(listTipoProducto: List<Producto>)
        fun setResults(productos:Int)

        //Response Notify
        fun requestResponseOK()
        fun requestResponseError(error: String?)
        fun onMessageOk(colorPrimary: Int, msg: String?)
        fun onMessageError(colorPrimary: Int, msg: String?)

        //Dialog
        fun verificateConnection(): AlertDialog?

        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)

        //Events
        fun onEventMainThread(requestEvent: RequestEventProductor?)

        //Methods
        fun getListProducto(tipoProducto:Long)

        //Conecttion
        fun checkConnection(): Boolean
    }

    interface Interactor {
        fun execute(checkConection:Boolean,tipoProducto:Long)
    }

    interface Repository {
        fun getListTipoProductos(checkConection:Boolean,tipoProducto:Long)
    }

}