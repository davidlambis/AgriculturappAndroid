package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.insumos.events

class InsumosEvent(var eventType: Int, var mutableList: MutableList<Object>? = null, var objectMutable: Object? = null, var mensajeError: String?) {

    companion object {
        val READ_EVENT: Int = 0
        val SAVE_EVENT: Int = 1
        val OPEN_EVENT: Int = 2
        val DELETE_EVENT: Int = 3
        val ERROR_EVENT: Int = 4
        val SET_EVENT: Int = 9


        //Events On item Click
        val ITEM_EVENT: Int = 5
        val ITEM_READ_EVENT: Int = 6
        val ITEM_EDIT_EVENT: Int = 7
        val ITEM_DELETE_EVENT: Int = 8
    }
}