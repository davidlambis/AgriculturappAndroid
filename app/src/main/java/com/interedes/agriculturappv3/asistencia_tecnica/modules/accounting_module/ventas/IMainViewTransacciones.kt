package com.interedes.agriculturappv3.asistencia_tecnica.modules.accounting_module.ventas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.asistencia_tecnica.models.Cultivo
import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.asistencia_tecnica.models.produccion.Produccion
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.asistencia_tecnica.models.ventas.CategoriaPuk
import com.interedes.agriculturappv3.asistencia_tecnica.models.ventas.Puk
import com.interedes.agriculturappv3.asistencia_tecnica.models.ventas.Transaccion
import com.interedes.agriculturappv3.asistencia_tecnica.modules.accounting_module.ventas.events.RequestEventVenta

interface IMainViewTransacciones {

    interface MainView {
        fun validarCampos(): Boolean
        fun validarListasAddTransaccion(): Boolean

        fun limpiarCampos()

        fun disableInputs()
        fun enableInputs()

        fun showProgress()
        fun hideProgress()

        //ProgresHud
        fun showProgressHud()
        fun hideProgressHud()

        //Fun Lote CRUD
        fun registerTransaccion()
        fun updateTransaccion(transaccion:Transaccion)
        fun setListTransaccion(transaccion: List<Transaccion>)
        fun setResults(transacciones:Int)

        //Response Notify
        fun requestResponseOK()
        fun requestResponseError(error: String?)

        fun onMessageOk(colorPrimary: Int, msg: String?)
        fun onMessageError(colorPrimary: Int, msg: String?)

        //Set sppiners
        fun setListUnidadMedida(listUnidadMedida:List<Unidad_Medida>?)
        fun setListUnidadProductiva(listUnidadProductiva: List<UnidadProductiva>?)
        fun setListLotes(listLotes:List<Lote>?)
        fun setListCultivos(listCultivos:List<Cultivo>?)
        fun setCultivo(cultivo:Cultivo?)


        fun setListPuk(listPuk:List<Puk>?)


        //Dialog
        fun showAlertDialogAddTransaccion(transaccion: Transaccion?)
        fun verificateConnection(): AlertDialog?
        fun confirmDelete(transaccion: Transaccion):AlertDialog?
        fun showAlertDialogFilterTransaccion(isFilter:Boolean?)

        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)

        //Events
        fun onEventMainThread(requestEvent: RequestEventVenta?)

        //Validacion
        fun validarCampos(): Boolean?
        fun validarListasAddTransaccion(): Boolean?


        //Methods
        fun registerTransaccion(transaccion: Transaccion,cultivo_id:Long?)
        fun updateTransaccion(transaccion: Transaccion,cultivo_id:Long?)
        fun deleteTransaccion(transaccion: Transaccion,cultivo_id:Long?)
        fun getListTransaccion(cultivo_id:Long?)
        fun getListas()


        fun getCultivo(cultivo_id:Long?)


        //Methods View
        fun setListSpinnerUnidadProductiva()
        fun setListSpinnerUnidadMedida()
        fun setListSpinnerLote(unidad_productiva_id:Long?)
        fun setListSpinnerCultivo(lote_id:Long?)


        fun setListSpinnerPuk(categoria_puk_id:Long?)



        //Conecttion
        fun checkConnection(): Boolean
    }

    interface Interactor {
        fun registerTransaccion(transaccion: Transaccion,cultivo_id:Long?)
        fun updateTransaccion(transaccion: Transaccion,cultivo_id:Long?)
        fun deleteProducccionTransaccion(transaccion: Transaccion,cultivo_id:Long?)
        fun execute(cultivo_id:Long?)
        fun getListas()
        fun getCultivo(cultivo_id:Long?)
    }

    interface Repository {
        fun getListas()
        fun getListTransacciones(cultivo_id:Long?)
        fun getTransaccion(cultivo_id:Long?): List<Transaccion>
        fun saveTransaccion(transaccion: Transaccion,cultivo_id:Long?)
        fun updateTransaccion(transaccion: Transaccion,cultivo_id:Long?)
        fun deleteTransaccion(transaccion: Transaccion,cultivo_id:Long?)
        fun getCultivo(cultivo_id:Long?)
    }
}