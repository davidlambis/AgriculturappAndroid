package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos.events

class CultivoEvent(var eventType: Int, var mutableList: MutableList<Object>? = null, var objectMutable: Object? = null, var mensajeError: String?) {

    companion object {
        val READ_EVENT: Int = 0
        val SAVE_EVENT: Int = 1
        val UPDATE_EVENT: Int = 2
        val DELETE_EVENT: Int = 3
        val ERROR_EVENT: Int = 4

        val ERROR_DIALOG_EVENT: Int = 5
        val LIST_EVENT_UNIDAD_PRODUCTIVA: Int = 6
        val LIST_EVENT_UNIDAD_MEDIDA: Int = 7
        val LIST_EVENT_LOTES: Int = 8

        //Events On item Click
        val ITEM_EVENT: Int = 9
        val ITEM_READ_EVENT: Int = 10
        val ITEM_EDIT_EVENT: Int = 11
        val ITEM_DELETE_EVENT: Int = 12
    }
}