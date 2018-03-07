package com.interedes.agriculturappv3.events

/**
 * Created by EnuarMunoz on 7/03/18.
 */
data class RequestEvent (var eventType: Int, var mensajeError: String?) {

    companion object {
        val onRequestOk: Int = 0
        val onRequestError: Int = 1
    }
}