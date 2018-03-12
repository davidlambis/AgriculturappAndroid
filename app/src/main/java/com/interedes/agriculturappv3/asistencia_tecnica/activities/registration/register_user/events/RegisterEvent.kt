package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.events

import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPago

data class RegisterEvent(var eventType: Int, var mutableList: MutableList<Object>? = null, var mensajeError: String?) {

    companion object {
        val onRegistroExitoso: Int = 0
        val onErrorRegistro: Int = 1
        val onMetodoPagoExitoso: Int = 2
        val onLoadInfoError: Int = 3
        val onDetalleMetodosPagoExitoso: Int = 4
    }

}