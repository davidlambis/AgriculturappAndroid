package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Produccion

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.asistencia_tecnica.models.Cultivo
import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.asistencia_tecnica.models.produccion.Produccion
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Produccion.events.RequestEventProduccion
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.UnidadProductiva.events.RequestEventUP
import com.interedes.agriculturappv3.events.RequestEvent

/**
 * Created by usuario on 20/03/2018.
 */
interface IMainProduccion {

    interface MainView {
        fun validarCampos(): Boolean
        fun validarListasAddProduccion(): Boolean

        fun limpiarCampos()

        fun disableInputs()
        fun enableInputs()

        fun showProgress()
        fun hideProgress()

        //ProgresHud
        fun showProgressHud()
        fun hideProgressHud()

        //Fun Lote CRUD
        fun registerProduccion()
        fun updateProduccion(produccion:Produccion)
        fun setListProduccion(listProduccion: List<Produccion>)
        fun setResults(unidadesProductivas:Int)

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


        //Dialog
        fun showAlertDialogAddProduccion(produccion: Produccion?)
        fun verificateConnection(): AlertDialog?
        fun confirmDelete(producccion:Produccion):AlertDialog?
        fun showAlertDialogFilterProduccion(isFilter:Boolean?)

        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)

        //Events
        fun onEventMainThread(requestEvent: RequestEventProduccion?)

        //Validacion
        fun validarCampos(): Boolean?
        fun validarListasAddProduccion(): Boolean?


        //Methods
        fun registerProdcuccion(produccin: Produccion,cultivo_id:Long)
        fun updateProducccion(produccin: Produccion,cultivo_id:Long)
        fun deleteProduccion(produccin: Produccion,cultivo_id:Long?)
        fun getListProduccion(cultivo_id:Long?)
        fun getListas()
        fun getCultivo(cultivo_id:Long?)


        //Methods View
        fun setListSpinnerUnidadProductiva()
        fun setListSpinnerUnidadMedida()
        fun setListSpinnerLote(unidad_productiva_id:Long?)
        fun setListSpinnerCultivo(lote_id:Long?)


        //Conecttion
        fun checkConnection(): Boolean
    }

    interface Interactor {
        fun registerProduccion(produccion: Produccion,cultivo_id:Long)
        fun updateProducccion(produccion: Produccion,cultivo_id:Long)
        fun deleteProducccion(produccion: Produccion,cultivo_id:Long?)
        fun execute(cultivo_id:Long?)
        fun getListas()
        fun getCultivo(cultivo_id:Long?)
    }

    interface Repository {
        fun getListas()
        fun getListProduccion(cultivo_id:Long?)
        fun getProductions(cultivo_id:Long?): List<Produccion>
        fun saveProduccion(produccion: Produccion,cultivo_id:Long)
        fun updateProduccion(produccion: Produccion,cultivo_id:Long)
        fun deleteProduccion(produccion: Produccion,cultivo_id:Long?)
        fun getCultivo(cultivo_id:Long?)
    }
}