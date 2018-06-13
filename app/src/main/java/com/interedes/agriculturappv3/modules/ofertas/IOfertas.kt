package com.interedes.agriculturappv3.modules.ofertas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.ofertas.events.OfertasEvent

interface IOfertas {

    interface View {
        fun showProgress()
        fun hideProgress()

        fun setListOfertas(listOfertas: List<Oferta>)
        fun setResults(ofertas: Int)
        fun setListUnidadProductiva(listUnidadProductiva: List<Unidad_Productiva>?)
        fun setListLotes(listLotes: List<Lote>?)
        fun setListCultivos(listCultivos: List<Cultivo>?)
        fun setListProductos(listProductos: List<Producto>?)
        fun verificateConnection(): AlertDialog?
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)
        fun confirmDelete(oferta: Oferta): AlertDialog?
        fun setProducto(producto: Producto?)

        fun showAlertDialogFilterOferta()
        fun validarListas(): Boolean? = false
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)
        fun onEventMainThread(event: OfertasEvent?)
        fun setListSpinnerUnidadProductiva()
        fun setListSpinnerLote(unidad_productiva_id: Long?)
        fun setListSpinnerCultivo(lote_id: Long?)
        fun setListSpinnerProducto(cultivo_id: Long?)
        fun checkConnection(): Boolean
        fun getListas()

        fun validarListas(): Boolean? = false
        fun getListOfertas(productoId: Long?)
        fun getProducto(productoId: Long?)
    }

    interface Interactor {
        fun getListOfertas(productoId: Long?)
        fun getListas()
        fun getProducto(productoId: Long?)
    }

    interface Repository {
        fun getListas()
        fun getListOfertas(productoId: Long?)
        fun getOfertas(productoId: Long?): List<Oferta>
        fun getProducto(productoId: Long?)
    }
}