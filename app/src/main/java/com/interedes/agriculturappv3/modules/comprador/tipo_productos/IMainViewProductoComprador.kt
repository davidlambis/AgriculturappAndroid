package com.interedes.agriculturappv3.modules.comprador.tipo_productos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.modules.comprador.tipo_productos.events.RequestEventProductosComprador
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto

interface IMainViewProductoComprador {

    interface MainView {
        //Progress and progress Hud
        fun showProgress()
        fun hideProgress()
        fun showProgressHud()
        fun hideProgressHud()

        //Fun  CRUD
        fun setListTipoProducto(listTipoProducto: List<TipoProducto>)
        fun setResults(unidadesProductivas:Int)

        //Response Notify
        fun requestResponseOK()
        fun requestResponseError(error: String?)
        fun onMessageOk(colorPrimary: Int, msg: String?)
        fun onMessageError(colorPrimary: Int, msg: String?)

        //Dialog
        fun verificateConnection(): AlertDialog?

        //Navigate
        fun navigateDetalleTipoProducto(idtipoProducto:Long)

        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)

        //Events
        fun onEventMainThread(requestEvent: RequestEventProductosComprador?)

        //Methods
        fun getListTipoProducto()

        //Conecttion
        fun checkConnection(): Boolean
    }

    interface Interactor {
        fun execute(checkConection:Boolean)
    }

    interface Repository {
        fun getListTipoProductos(checkConection:Boolean)
    }
}