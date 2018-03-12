package com.interedes.agriculturappv3.events

/**
 * Created by EnuarMunoz on 12/03/18.
 */

data class ListEvent (var eventType: Int, var mutableList: MutableList<Object>? = null, var mensajeError: String?) {

    companion object {
        val LIST_EVENT: Int = 0
        val ERROR_EVENT: Int = 1
    }
}

