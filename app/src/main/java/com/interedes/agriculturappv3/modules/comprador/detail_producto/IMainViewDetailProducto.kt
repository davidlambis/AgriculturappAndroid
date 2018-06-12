package com.interedes.agriculturappv3.modules.comprador.detail_producto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.modules.comprador.detail_producto.events.RequestEventDetalleProducto
import com.interedes.agriculturappv3.modules.models.ofertas.DetalleOferta
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.models.usuario.Usuario

interface IMainViewDetailProducto {

    interface MainView {
        //Progress and progress Hud
        fun showProgress()
        fun hideProgress()
        fun showProgressHud()
        fun hideProgressHud()

        //Validaciones
        fun validarAddOferta(): Boolean

        //Response Notify
        fun requestResponseOK()
        fun requestResponseError(error: String?)
        fun onMessageOk(colorPrimary: Int, msg: String?)
        fun onMessageError(colorPrimary: Int, msg: String?)

        //Dialog
        fun verificateConnection(): AlertDialog?
        fun showAlertDialogOfertar(producto:Producto?)
        fun setListMoneda(listMoneda: List<Unidad_Medida>?)
        fun showConfirmOferta()
        fun sucessResponseOferta()


        //Methods
        fun postOferta()


        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)

        //Events
        fun onEventMainThread(requestEvent: RequestEventDetalleProducto?)

        //validaciones
        fun validarCamposAddOferta(): Boolean

        //Methods
        fun getLastUserLogued(): Usuario?
        fun getProducto(producto_id:Long):Producto?
        fun getTipoProducto(tipo_producto_id:Long):TipoProducto?
        fun verificateCantProducto(producto_id:Long?,catnidad:Double?):Boolean?

        //Oferta
        fun postOferta(oferta: Oferta)

        //Set ListSppiner
        fun setListSpinnerMoneda()
        //Conecttion
        fun checkConnection(): Boolean
        fun getListas()
    }

    interface Interactor {
        fun getLastUserLogued(): Usuario?
        fun getProducto(producto_id:Long):Producto?
        fun getTipoProducto(tipo_producto_id:Long):TipoProducto?
        fun verificateCantProducto(producto_id:Long?,catnidad:Double?):Boolean?

        //Oferta
        fun postOferta(oferta: Oferta,checkConection:Boolean)

        fun getListas()
    }

    interface Repository {
        fun getLastUserLogued(): Usuario?
        fun getProducto(producto_id:Long):Producto?
        fun getTipoProducto(tipo_producto_id:Long):TipoProducto?
        fun verificateCantProducto(producto_id:Long?,catnidad:Double?):Boolean?

        //Oferta
        fun postOferta(oferta: Oferta,checkConection:Boolean)
        fun saveOfertaLocal(oferta: Oferta, detalleOferta: DetalleOferta)
        fun getLastOferta(): Oferta?
        fun getLastDetalleOferta(): DetalleOferta?

        fun getListas()
    }

}