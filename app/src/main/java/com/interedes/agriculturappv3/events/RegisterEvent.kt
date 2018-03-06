package com.interedes.agriculturappv3.events

data class RegisterEvent(val eventType: Int, val mensajeError: String) {

    companion object {
        val onRegistroExitoso: Int = 0
        val onErrorRegistro: Int = 1
    }

}