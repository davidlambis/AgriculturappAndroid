package com.interedes.agriculturappv3.asistencia_tecnica.activities.login.ui

interface LoginView {

    fun showProgress()
    fun hideProgress()
    fun enableInputs()
    fun disableInputs()

    //fun validarCampos(): Boolean?
    fun ingresar()
    fun errorIngresar(error: String?)
    fun navigateToMainActivity()
}