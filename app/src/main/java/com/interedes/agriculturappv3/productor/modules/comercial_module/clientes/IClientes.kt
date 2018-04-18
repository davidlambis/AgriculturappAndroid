package com.interedes.agriculturappv3.productor.modules.comercial_module.clientes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.productor.models.usuario.Usuario
import com.interedes.agriculturappv3.productor.modules.comercial_module.clientes.events.ClientesEvent


class IClientes {

    interface View {
        fun showProgress()
        fun hideProgress()

        fun setListClientes(listClientes: List<Usuario>)
        fun setResults(clientes: Int)
        /* fun setListUnidadProductiva(listUnidadProductiva: List<UnidadProductiva>?)
         fun setListLotes(listLotes: List<Lote>?)
         fun setListCultivos(listCultivos: List<Cultivo>?)
         fun setListProductos(listProductos: List<Producto>?)*/
        fun verificateConnection(): AlertDialog?

        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)
        /*fun confirmDelete(venta: Compras): AlertDialog?
        fun setProducto(producto: Producto?)

        fun showAlertDialogFilterVenta()
        fun validarListas(): Boolean? = false*/
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)
        fun onEventMainThread(event: ClientesEvent?)

        /*fun setListSpinnerUnidadProductiva()
        fun setListSpinnerLote(unidad_productiva_id: Long?)
        fun setListSpinnerCultivo(lote_id: Long?)
        fun setListSpinnerProducto(cultivo_id: Long?) */
        fun checkConnection(): Boolean

        /*fun getListas()

        fun validarListas(): Boolean? = false*/
        fun getListClientes()
        //fun getProducto(productoId: Long?)
    }

    interface Interactor {
        fun getListClientes()
        /*fun getListas()
        fun getProducto(productoId: Long?)*/
    }

    interface Repository {
        //fun getListas()
        fun getListClientes()
        /*fun getVentas(productoId: Long?): List<Compras>
        fun getProducto(productoId: Long?)*/
    }
}