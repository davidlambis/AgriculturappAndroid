package com.interedes.agriculturappv3.activities.registration.register_user.ui

import com.interedes.agriculturappv3.productor.models.detalle_metodo_pago.DetalleMetodoPago
import com.interedes.agriculturappv3.productor.models.metodopago.MetodoPago

interface RegisterUserView {
    fun disableInputs()
    fun enableInputs()
    fun hideMetodosPago()

    fun disableBanco()
    fun showBanco()

    fun showProgress()
    fun hideProgress()

    fun hasNotConnectivity()

    fun loadMetodosPago()

    //fun loadMetodosPago()
    fun loadMetodosPagoError(error: String?)

    //Métodos Pago
    fun setMetodosPago(metodosPago: List<MetodoPago>?)
    fun getSqliteMetodosPago()

    //Detalle Métodos Pago
    fun setDetalleMetodosPago(detalleMetodosPago: List<DetalleMetodoPago>?)

    fun limpiarCambios()

    fun navigateToParentActivity()


    fun validarCampos(): Boolean?
    fun registerUsuario()
    fun registroExitoso()
    fun registroError(error: String?)

    fun onMessageOk(colorPrimary: Int, message: String?)
    fun onMessageError(colorPrimary: Int, message: String?)


}