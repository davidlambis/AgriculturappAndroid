package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.ui

interface RegisterUserView {
    fun disableInputs()
    fun enableInputs()

    fun showProgress()
    fun hideProgress()

    fun hasNotConnectivity()

    fun loadInfo()
    fun limpiarCambios()

    fun navigateToParentActivity()


    fun validarCampos(): Boolean?
    fun registerUsuario()
    fun registroExitoso()
    fun registroError(error : String?)
}