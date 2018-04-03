package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.control_plagas.events


data class ControlPlagasEvent(var eventType: Int,
                              var mutableList: MutableList<Object>? = null,
                              var objectMutable: Object? = null,
                              var mensajeError: String?
) {

    companion object {
        //LISTAS
        val LIST_EVENT_UP: Int = 1
        val LIST_EVENT_LOTE: Int = 2
        val LIST_EVENT_CULTIVO: Int = 3

        val READ_EVENT: Int = 4
        val GET_EVENT_CULTIVO: Int = 5
        val ITEM_DELETE_EVENT: Int = 6
        val DELETE_EVENT: Int = 7
        val ITEM_ERRADICAR_EVENT: Int = 8
    }
}