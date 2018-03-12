package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.asistencia_tecnica.models.UP
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.adapter.ListenerAdapterEvent

/**
 * Created by EnuarMunoz on 7/03/18.
 */
interface MainViewLote {
    //Acions Elements
    fun disableInputs()
    fun enableInputs()

    fun showProgress()
    fun hideProgress()


    fun hideElementsAndSetPropertiesOnConectionInternet()
    fun showElementsAndSetPropertiesOffConnectioninternet()


    //Set Properties
    fun setPropertiesTypeLocationGps()
    fun setPropertiesTypeLocationManual()

    //Fun Lote CRUD
    fun registerLote()
    fun updateLote()
    fun setListLotes(lotes:List<Lote>)
    fun setResults(lotes:Int)

    //List UP
    fun loadListUp()
    fun setListUP(listUp:List<UP>)

    //VALIDATION
    fun validarCampos(): Boolean?
    fun limpiarCampos()

    //Response Notify
    fun requestResponseOk()
    fun requestResponseError(error : String?)

    fun onMessageOk(colorPrimary: Int, message: String?)
    fun onMessageError(colorPrimary: Int, message: String?)


    //UI
    fun showAlertDialogAddLote(lote:Lote?): AlertDialog?
    fun showAlertTypeLocationLote(): AlertDialog?
    fun confirmDelete(lote:Lote):AlertDialog?

    //Events
    fun onEventBroadcastReceiver(extras: Bundle,intent: Intent)
}