package com.interedes.agriculturappv3.activities.login.ui

import android.content.Context
import com.interedes.agriculturappv3.modules.models.usuario.Usuario

interface LoginView {

    fun showProgress()
    fun hideProgress()
    fun enableInputs()
    fun disableInputs()

    fun validarCampos(): Boolean?
    fun ingresar()
    fun errorIngresar(error: String?)
    fun navigateToMainActivity(usuario:Usuario?)

    fun onMessageOk(colorPrimary: Int, message: String?)
    fun onMessageError(colorPrimary: Int, message: String?)

    //Conexión a Internet
    fun registerInternetReceiver()

    fun unRegisterInternetReceiver()
    fun checkConnection(): Boolean?
}