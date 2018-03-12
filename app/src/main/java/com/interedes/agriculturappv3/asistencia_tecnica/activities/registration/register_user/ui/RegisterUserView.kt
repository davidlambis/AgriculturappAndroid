package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.ui

import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPago

interface RegisterUserView {
    fun disableInputs()
    fun enableInputs()

    fun showProgress()
    fun hideProgress()

    fun hasNotConnectivity()

    fun loadInfo()
    //fun loadMetodosPago()
    fun loadMetodosPagoError(error : String?)
    fun setMetodosPago(metodosPago: List<MetodoPago>?)

    fun limpiarCambios()

    fun navigateToParentActivity()


    fun validarCampos(): Boolean?
    fun registerUsuario()
    fun registroExitoso()
    fun registroError(error : String?)

    fun onMessageOk(colorPrimary: Int, message: String?)
    fun onMessageError(colorPrimary: Int, message: String?)
}