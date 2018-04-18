package com.interedes.agriculturappv3.productor.modules.comercial_module.ofertas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.productor.models.Cultivo
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.ofertas.Oferta
import com.interedes.agriculturappv3.productor.models.producto.Producto
import com.interedes.agriculturappv3.productor.models.unidad_productiva.UnidadProductiva
import com.interedes.agriculturappv3.productor.modules.comercial_module.ofertas.events.OfertasEvent

interface IOfertas {

    interface View {
        fun showProgress()
        fun hideProgress()

        fun setListOfertas(listOfertas: List<Oferta>)
        fun setResults(ofertas: Int)
        fun setListUnidadProductiva(listUnidadProductiva: List<UnidadProductiva>?)
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