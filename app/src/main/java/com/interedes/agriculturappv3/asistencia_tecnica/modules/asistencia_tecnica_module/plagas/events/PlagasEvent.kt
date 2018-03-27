package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.events


class PlagasEvent(var eventType: Int, var mutableList: MutableList<Object>? = null, var objectMutable: Object? = null, var mensajeError: String?) {

    companion object {
        val READ_EVENT: Int = 0
        val SET_EVENT: Int = 2


        //Events On item Click
        val ITEM_EVENT: Int = 3
        val ITEM_SELECT_PLAGA_EVENT : Int = 4
        val ITEM_OPEN_EVENT : Int = 5

    }
}