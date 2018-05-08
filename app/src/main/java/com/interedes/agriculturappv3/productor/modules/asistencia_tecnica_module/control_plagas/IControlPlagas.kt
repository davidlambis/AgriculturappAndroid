package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.control_plagas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.productor.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.control_plagas.events.ControlPlagasEvent


interface IControlPlagas {

    interface View {
        fun showAlertDialogFilterControlPlaga()
        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)

        //Set sppiners
        fun setListUnidadProductiva(listUnidadProductiva: List<Unidad_Productiva>?)

        fun setListLotes(listLotes: List<Lote>?)
        fun setListCultivos(listCultivos: List<Cultivo>?)

        fun setListControlPlagas(listControlPlagas: List<ControlPlaga>)
        fun setResults(controlPlagas: Int)

        fun showProgress()
        fun hideProgress()

        fun validarListasFilter(): Boolean
        fun setCultivo(cultivo: Cultivo?)
        fun confirmDelete(controlPlaga: ControlPlaga): AlertDialog?

        fun verificateConnection(): AlertDialog?
        fun requestResponseOK()

        fun onMessageOk(colorPrimary: Int, msg: String?)
        fun onMessageError(colorPrimary: Int, msg: String?)

        fun updatePlaga(controlPlaga: ControlPlaga?)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)
        //Conection
        fun checkConnection(): Boolean

        fun getListas()

        //Events
        fun onEventMainThread(event: ControlPlagasEvent?)

        //Methods View
        fun setListSpinnerUnidadProductiva()

        fun setListSpinnerLote(unidad_productiva_id: Long?)
        fun setListSpinnerCultivo(lote_id: Long?)

        fun getListControlPlaga(cultivo_id: Long?)

        fun validarListasFilter(): Boolean
        fun getCultivo(cultivo_id: Long?)

        fun deleteControlPlaga(controlPlaga: ControlPlaga, cultivo_id:Long?)
        fun updateControlPlaga(controlPlaga: ControlPlaga?)
    }

    interface Interactor {
        fun getListas()
        fun getListControlPlaga(cultivo_id: Long?)
        fun getCultivo(cultivo_id: Long?)
        fun deleteControlPlaga(controlPlaga: ControlPlaga, cultivo_id:Long?)
        fun updateControlPlaga(controlPlaga: ControlPlaga?)
    }

    interface Repository {
        fun getListas()
        fun getListControlPlaga(cultivo_id: Long?)
        fun getControlPlagas(cultivo_id: Long?): List<ControlPlaga>
        fun getCultivo(cultivo_id: Long?)
        fun deleteControlPlaga(controlPlaga: ControlPlaga, cultivo_id:Long?)
        fun updateControlPlaga(controlPlaga: ControlPlaga?)
    }
}