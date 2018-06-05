package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.UnidadProductiva

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.modules.models.departments.Ciudad
import com.interedes.agriculturappv3.modules.models.departments.Departamento
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.UnidadProductiva.events.RequestEventUP

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
        fun showProgressHudCoords()
        fun hideProgressHudCoords()
        fun hideProgressHud()

        //Fun Lote CRUD
        fun registerUp()

        fun updateUp(unidadProductiva: Unidad_Productiva?)
        fun setListUps(listUnidadProductivas: List<Unidad_Productiva>)
        fun setResults(unidadesProductivas: Int)


        ///ListUnidad Medida
        fun setListUnidadMedida(listUnidadMedida: List<Unidad_Medida>)

        fun setListUnidadMedidaAdapterSpinner()

        fun requestResponseOK()
        fun requestResponseError(error: String?)

        fun onMessageOk(colorPrimary: Int, msg: String?)
        fun onMessageError(colorPrimary: Int, msg: String?)

        //Dialog
        fun showAlertDialogAddUnidadProductiva(unidadProductiva: Unidad_Productiva?)

        fun verificateConnection(): AlertDialog?


        //Geocoder
        fun getAddressGps(latitud: Double, longitud: Double): String?

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

        fun registerUP(unidadProductivaModel: Unidad_Productiva?)
        fun updateUP(unidadProductivaModel: Unidad_Productiva?)
        fun deleteUP(unidadProductivaModel: Unidad_Productiva?)
        fun getUps()


        fun getListas()
        fun setListDepartamentos()
        fun setListMunicipios(departamentoId: Long?)


        //Coords Service
        fun startGps(activity: Activity)
        fun closeServiceGps()
        fun getStatusServiceCoords():Boolean?
        fun setStatusServiceCoords(status:Boolean?)

        //Conecttion
        fun checkConnection(): Boolean


    }

    interface Interactor {
        fun registerUP(unidadProductivaModel: Unidad_Productiva?,checkConection:Boolean)
        fun updateUP(unidadProductivaModel: Unidad_Productiva?,checkConection:Boolean)
        fun deleteUP(unidadProductivaModel: Unidad_Productiva?,checkConection:Boolean)
        fun execute()
        fun getListas()
    }

    interface Repo {
        //val uPs: List<Unidad_Productiva>
        fun getListas()

        fun getListUPs()
        fun getUPs(): List<Unidad_Productiva>
        fun saveUpLocal(mUnidadProductiva: Unidad_Productiva)
        fun saveUp(mUnidadProductiva: Unidad_Productiva,checkConection:Boolean)
        fun updateUp(mUnidadProductiva: Unidad_Productiva?,checkConection:Boolean)
        fun deleteUp(mUnidadProductiva: Unidad_Productiva,checkConection:Boolean)
        fun getLastUserLogued(): Usuario?
        fun getLastUp(): Unidad_Productiva?
    }
}