package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.tratamiento.events

class TratamientoEvent(var eventType: Int, var mutableList: MutableList<Object>? = null, var objectMutable: Object? = null, var mensajeError: String?) {

    companion object {
        val SET_EVENT = 1
        val LIST_EVENT_UP: Int = 2
        val LIST_EVENT_LOTE: Int = 3
        val LIST_EVENT_CULTIVO: Int = 4
        val LIST_EVENT_UNIDAD_MEDIDA: Int = 5
        val SAVE_CONTROL_PLAGA_EVENT : Int = 6

        val EVENT_CALIFICACION_TRATAMIENTO: Int = 7
        val EVENT_INSUMO : Int = 8

        val ERROR_EVENT: Int = 9
        val REQUEST_CALIFICATE_EVENT_TRATAMIENTO_OK: Int = 10
        val CALIFICATE_EVENT_EXIST: Int = 11
    }


}