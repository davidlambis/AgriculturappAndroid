package com.interedes.agriculturappv3.events

data class RegisterEvent(var eventType: Int, var mensajeError: String?) {

    companion object {
        val onRegistroExitoso: Int = 0
        val onErrorRegistro: Int = 1
    }

}