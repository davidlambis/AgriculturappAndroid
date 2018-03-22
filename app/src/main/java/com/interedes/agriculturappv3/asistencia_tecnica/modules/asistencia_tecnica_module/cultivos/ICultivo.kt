package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.asistencia_tecnica.models.*
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos.events.CultivoEvent

interface ICultivo {
    interface View {
        fun validarCampos(): Boolean
        fun limpiarCampos()

        fun disableInputs()
        fun enableInputs()

        fun showProgress()
        fun hideProgress()
        fun hideLotes()

        //Fun Cultivo CRUD
        fun registerCultivo()

        fun updateCultivo()
        fun deleteCultivo(cultivo: Cultivo): AlertDialog?
        fun setListCultivos(listCultivos: List<Cultivo>)
        fun setResults(cultivos: Int)


        //Dialog
        fun requestResponseDialogOK()

        fun requestResponseDialogError(error: String?)
        fun onMessageDialogOk(colorPrimary: Int, msg: String?)
        fun onMessageDialogError(colorPrimary: Int, msg: String?)

        //
        fun requestResponseOk()

        fun requestResponseError(error: String?)
        fun onMessageOk(colorPrimary: Int, msg: String?)
        fun onMessageError(colorPrimary: Int, msg: String?)

        //Dialog
        fun showAlertDialogCultivo(cultivo: Cultivo?): AlertDialog?

        //Spinners y Date Pickers
        //Unidades Productivas
        fun setListUnidadProductiva(listUnidadProductiva: List<UnidadProductiva>)

        //Tipo Producto
        fun setListTipoProducto(listTipoProducto: List<TipoProducto>)

        //Detalle Tipo Producto
        fun setListDetalleTipoProducto(listDetalleTipoProducto: List<DetalleTipoProducto>)

        //Unidad Medida
        fun setListUnidadMedidas(listUnidadMedida: List<Unidad_Medida>)

        //Lotes
        fun setListLotes(listLotes: List<Lote>)

        fun setAdaptersSpinner()

        fun setAdaptersSpinnersSearch()
        fun setListLotesSearch(listLotes: List<Lote>)

        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)

        fun searchCultivos(loteId: Long?)
        fun validarCamposSearch(): Boolean
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)


        fun onEventMainThread(cultivoEvent: CultivoEvent?)

        fun validarCampos(): Boolean

        fun registerCultivo(cultivo: Cultivo?)
        fun updateCultivo(cultivo: Cultivo?)
        fun deleteCultivo(cultivo: Cultivo?)
        fun getAllCultivos()

        fun getListas()
        fun loadDetalleTipoProducto(tipoProductoId: Long?)
        fun loadLotesSpinner(unidadProductivaId: Long?)
        fun loadLotesSpinnerSearch(unidadProductivaId: Long?)

        //Conecttion
        fun checkConnection(): Boolean

        //Search
        fun searchCultivos(loteId: Long?)

        fun validarCamposSearch(): Boolean
    }

    interface Interactor {
        fun registerCultivo(cultivo: Cultivo?)
        fun updateCultivo(cultivo: Cultivo?)
        fun deleteCultivo(cultivo: Cultivo?)
        fun getAllCultivos()
        fun getListas()
        fun loadLotesSpinner(unidadProductivaId: Long?)
        fun loadLotesSpinnerSearch(unidadProductivaId: Long?)
        fun searchCultivos(loteId: Long?)
        fun loadDetalleTipoProducto(tipoProductoId: Long?)
    }

    interface Repository {
        fun getListas()
        fun loadLotesSpinner(unidadProductivaId: Long?)
        fun loadLotesSpinnerSearch(unidadProductivaId: Long?)
        fun getListAllCultivos()
        fun getCultivosByLote(loteId: Long?): List<Cultivo>
        fun saveCultivo(cultivo: Cultivo)
        fun updateCultivo(cultivo: Cultivo)
        fun deleteCultivo(cultivo: Cultivo)
        fun searchCultivos(loteId: Long?)
        fun loadDetalleTipoProducto(tipoProductoId: Long?)
    }

}