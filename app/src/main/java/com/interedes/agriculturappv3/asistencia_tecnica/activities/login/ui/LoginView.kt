package com.interedes.agriculturappv3.asistencia_tecnica.activities.login.ui

import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario

interface LoginView {

    fun showProgress()
    fun hideProgress()
    fun enableInputs()
    fun disableInputs()

    fun validarCampos(): Boolean?
    fun ingresar(email:String , password:String)
    fun errorIngresar(error: String?)
    fun navigateToMainActivity()

    fun getUsuario()
    fun requestResponseOk()
    fun requestResponseError(error : String?)
    fun onMessageOk(colorPrimary: Int, message: String?)
    fun onMessageError(colorPrimary: Int, message: String?)
}