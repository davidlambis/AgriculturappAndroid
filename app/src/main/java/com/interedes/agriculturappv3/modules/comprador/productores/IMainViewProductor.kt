package com.interedes.agriculturappv3.modules.comprador.productores

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.modules.comprador.productores.events.RequestEventProductor
import com.interedes.agriculturappv3.modules.comprador.productores.resources.RequestFilter
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto

interface IMainViewProductor {

    interface MainView {
        //Progress and progress Hud
        fun showProgress()
        fun hideProgress()
        fun showProgressHud()
        fun hideProgressHud()

        //Fun  CRUD
        fun setListProducto(listTipoProducto: List<Producto>)
        fun setListProductoFirts(listTipoProducto: List<Producto>)
        fun setResults(productos:Int)
        fun addNewItem(producto:Producto)

        //Response Notify
        fun requestResponseOK()
        fun requestResponseError(error: String?)
        fun onMessageOk(colorPrimary: Int, msg: String?)
        fun onMessageError(colorPrimary: Int, msg: String?)

        //Dialog
        fun verificateConnection(): AlertDialog?
        fun showAlertDialogFilterProducts()

        //Navigate
        fun navigateDetalleTipoProductoUser(poducto:Producto)


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

        fun getListProducto(filter: RequestFilter)

        fun getTipoProducto(tipoProducto:Long):TipoProducto?

        //Listas
        fun getListDepartmentCities()

        //Conecttion
        fun checkConnection(): Boolean
    }

    interface Interactor {
        fun getTipoProducto(tipoProducto:Long):TipoProducto?

        fun execute(filter: RequestFilter)

        fun getListDepartmentCities()
    }

    interface Repository {
        fun getTipoProducto(tipoProducto:Long):TipoProducto?
        fun getListTipoProductos(filter: RequestFilter)

        fun getListDepartmentCities()
    }

}