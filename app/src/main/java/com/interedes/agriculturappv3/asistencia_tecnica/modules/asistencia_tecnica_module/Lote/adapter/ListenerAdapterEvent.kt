package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.adapter

/**
 * Created by EnuarMunoz on 9/03/18.
 */

data class ListenerAdapterEvent (var eventType:Int, var objectMutable: Object? = null) {

    companion object {
        val ITEM_EVENT: Int = 0
        val READ_EVENT: Int = 1
        val EDIT_EVENT: Int = 2
        val DELETE_EVENT: Int = 3
    }
}
