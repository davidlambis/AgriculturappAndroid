package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.up

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.events.RequestEvent

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
        fun setResults(unidadesProductivas:Int)

        fun requestResponseOK()
        fun requestResponseError(error: String?)

        fun onMessageOk(colorPrimary: Int, msg: String?)
        fun onMessageError(colorPrimary: Int, msg: String?)

        //Dialog
        fun showAlertDialogAddUnidadProductiva(unidadProductiva:UnidadProductiva?): AlertDialog?

        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)


        fun onEventMainThread(requestEvent: RequestEvent?)

        fun validarCampos(): Boolean

        fun registerUP(unidadProductivaModel: UnidadProductiva?)
        fun updateUP(unidadProductivaModel: UnidadProductiva?)
        fun deleteUP(unidadProductivaModel: UnidadProductiva?)
        fun getUps()



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
    }

    interface Repo {
        //val uPs: List<UnidadProductiva>
        fun getListUPs()
        fun getUPs(): List<UnidadProductiva>
        fun saveUp(mUnidadProductiva: UnidadProductiva)
        fun updateUp(mUnidadProductiva: UnidadProductiva)
        fun deleteUp(mUnidadProductiva: UnidadProductiva)
    }
}