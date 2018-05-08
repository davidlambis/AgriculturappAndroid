package com.interedes.agriculturappv3.productor.modules.comercial_module.ventas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.compras.Compras
import com.interedes.agriculturappv3.productor.models.producto.Producto
import com.interedes.agriculturappv3.productor.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.productor.modules.comercial_module.ventas.events.VentasEvent

interface IVentas {

    interface View {
        fun showProgress()
        fun hideProgress()

        fun setListVentas(listVentas: List<Compras>)
        fun setResults(ofertas: Int)
        fun setListUnidadProductiva(listUnidadProductiva: List<Unidad_Productiva>?)
        fun setListLotes(listLotes: List<Lote>?)
        fun setListCultivos(listCultivos: List<Cultivo>?)
        fun setListProductos(listProductos: List<Producto>?)
        fun verificateConnection(): AlertDialog?
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)
        fun confirmDelete(venta: Compras): AlertDialog?
        fun setProducto(producto: Producto?)

        fun showAlertDialogFilterVenta()
        fun validarListas(): Boolean? = false
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)
        fun onEventMainThread(event: VentasEvent?)
        fun setListSpinnerUnidadProductiva()
        fun setListSpinnerLote(unidad_productiva_id: Long?)
        fun setListSpinnerCultivo(lote_id: Long?)
        fun setListSpinnerProducto(cultivo_id: Long?)
        fun checkConnection(): Boolean
        fun getListas()

        fun validarListas(): Boolean? = false
        fun getListVentas(productoId: Long?)
        fun getProducto(productoId: Long?)
    }

    interface Interactor {
        fun getListVentas(productoId: Long?)
        fun getListas()
        fun getProducto(productoId: Long?)
    }

    interface Repository {
        fun getListas()
        fun getListVentas(productoId: Long?)
        fun getVentas(productoId: Long?): List<Compras>
        fun getProducto(productoId: Long?)
    }

}