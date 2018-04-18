package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.UnidadProductiva

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.productor.models.Ciudad
import com.interedes.agriculturappv3.productor.models.Departamento
import com.interedes.agriculturappv3.productor.models.unidad_productiva.UnidadProductiva
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.productor.models.usuario.Usuario
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.UnidadProductiva.events.RequestEventUP

/**
 * Created by usuario on 16/03/2018.
 */
interface IUnidadProductiva {
    interface View {
        fun validarCampos(): Boolean
        fun limpiarCampos()

        fun disableInputs()
        fun enableInputs()

        fun showProgress()
        fun hideProgress()
        fun hideElements()

        //ProgresHud
        fun showProgressHud()

        fun hideProgressHud()

        //Fun Lote CRUD
        fun registerUp()

        fun updateUp()
        fun setListUps(listUnidadProductivas: List<UnidadProductiva>)
        fun setResults(unidadesProductivas: Int)


        ///ListUnidad Medida
        fun setListUnidadMedida(listUnidadMedida: List<Unidad_Medida>)

        fun setListUnidadMedidaAdapterSpinner()

        fun requestResponseOK()
        fun requestResponseError(error: String?)

        fun onMessageOk(colorPrimary: Int, msg: String?)
        fun onMessageError(colorPrimary: Int, msg: String?)

        //Dialog
        fun showAlertDialogAddUnidadProductiva(unidadProductiva: UnidadProductiva?)

        fun verificateConnection(): AlertDialog?

        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)

        fun setListSpinnerDepartamentos(listDepartamentos: List<Departamento>)
        fun setListSpinnerMunicipios(listMunicipios: List<Ciudad>)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)


        fun onEventMainThread(requestEvent: RequestEventUP?)

        fun validarCampos(): Boolean

        fun registerUP(unidadProductivaModel: UnidadProductiva?)
        fun updateUP(unidadProductivaModel: UnidadProductiva?)
        fun deleteUP(unidadProductivaModel: UnidadProductiva?)
        fun getUps()


        fun getListas()
        fun setListDepartamentos()
        fun setListMunicipios(departamentoId: Long?)


        //Coords Service
        fun startGps(activity: Activity)

        fun closeServiceGps()
        //Conecttion
        fun checkConnection(): Boolean


    }

    interface Interactor {
        fun registerUP(unidadProductivaModel: UnidadProductiva?)
        fun updateUP(unidadProductivaModel: UnidadProductiva?)
        fun deleteUP(unidadProductivaModel: UnidadProductiva?)
        fun execute()
        fun getListas()
        fun registerOnlineUP(unidadProductivaModel: UnidadProductiva?)
    }

    interface Repo {
        //val uPs: List<UnidadProductiva>
        fun getListas()

        fun getListUPs()
        fun getUPs(): List<UnidadProductiva>
        fun saveUp(mUnidadProductiva: UnidadProductiva)
        fun registerOnlineUP(mUnidadProductiva: UnidadProductiva?)
        fun updateUp(mUnidadProductiva: UnidadProductiva)
        fun deleteUp(mUnidadProductiva: UnidadProductiva)
        fun getLastUserLogued(): Usuario?
        fun getLastUp(): UnidadProductiva?
    }
}