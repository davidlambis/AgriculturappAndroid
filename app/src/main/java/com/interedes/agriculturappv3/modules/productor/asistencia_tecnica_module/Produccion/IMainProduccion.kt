package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.Produccion

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.produccion.Produccion
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.Produccion.events.RequestEventProduccion

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
        fun setListUnidadProductiva(listUnidadProductiva: List<Unidad_Productiva>?)
        fun setListLotes(listLotes:List<Lote>?)
        fun setListCultivos(listCultivos:List<Cultivo>?)
        fun setCultivo(cultivo: Cultivo?)


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
        fun saveProduccion(produccion: Produccion,cultivo_id:Long,checkConection:Boolean)
        fun updateProduccion(produccion: Produccion,cultivo_id:Long,checkConection:Boolean)
        fun deleteProducccion(produccion: Produccion,cultivo_id:Long?,checkConection:Boolean)
        fun execute(cultivo_id:Long?)
        fun getListas()
        fun getCultivo(cultivo_id:Long?)
    }

    interface Repository {
        fun getListas()
        fun getListProduccion(cultivo_id:Long?)
        fun getProductions(cultivo_id:Long?): List<Produccion>
        fun saveProduccionLocal(produccion: Produccion,cultivo_id:Long)
        fun updateProduccion(produccion: Produccion,cultivo_id:Long,checkConection:Boolean)
        fun saveProduccion(produccion: Produccion,cultivo_id:Long,checkConection:Boolean)
        fun deleteProduccion(produccion: Produccion,cultivo_id:Long?,checkConection:Boolean)
        fun getCultivo(cultivo_id:Long?)
    }
}