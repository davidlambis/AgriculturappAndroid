package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos.events

class CultivoEvent(var eventType: Int, var mutableList: MutableList<Any>? = null, var objectMutable: Any? = null, var mensajeError: String?) {

    companion object {
        val READ_EVENT: Int = 0
        val SAVE_EVENT: Int = 1
        val UPDATE_EVENT: Int = 2
        val DELETE_EVENT: Int = 3
        val ERROR_EVENT: Int = 4

        val LIST_EVENT_UNIDAD_PRODUCTIVA: Int = 5
        val LIST_EVENT_UNIDAD_MEDIDA: Int = 6
    }
}