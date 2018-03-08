package com.interedes.agriculturappv3.events

import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import java.util.*

/**
 * Created by EnuarMunoz on 7/03/18.
 */


data class RequestEvent (var eventType: Int, var mutableList: MutableList<Object>? = null, var objectMutable: Object? = null, var mensajeError: String?) {

    companion object {
        val READ_EVENT: Int = 0
        val SAVE_EVENT: Int = 1
        val UPDATE_EVENT: Int = 2
        val DELETE_EVENT: Int = 3
        val ERROR_EVENT: Int = 4
    }
}


