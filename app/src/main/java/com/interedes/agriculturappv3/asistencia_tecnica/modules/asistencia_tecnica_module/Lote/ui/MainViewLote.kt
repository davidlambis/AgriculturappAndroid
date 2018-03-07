package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.ui

import android.app.Dialog
import android.support.v7.app.AlertDialog

/**
 * Created by EnuarMunoz on 7/03/18.
 */
interface MainViewLote {
    fun disableInputs()
    fun enableInputs()

    fun showProgress()
    fun hideProgress()


    fun hideElements()

    fun registerLote()
    fun validarCampos(): Boolean?
    fun limpiarCampos()


    fun requestResponseOk()
    fun requestResponseError(error : String?)


    fun onMessageOk(colorPrimary: Int, message: String?)
    fun onMessageError(colorPrimary: Int, message: String?)


    //UI
    fun showAlertDialogAddLote(): AlertDialog?
}