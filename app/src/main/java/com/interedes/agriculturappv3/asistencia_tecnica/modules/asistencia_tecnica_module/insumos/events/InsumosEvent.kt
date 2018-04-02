package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.insumos.events

class InsumosEvent(var eventType: Int, var mutableList: MutableList<Object>? = null, var objectMutable: Object? = null, var mensajeError: String?) {

    companion object {
        val READ_EVENT: Int = 0

        //Events On item Click
        val ITEM_EVENT: Int = 2
    }
}