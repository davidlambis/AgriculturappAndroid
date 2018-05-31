package com.interedes.agriculturappv3.modules.account.events



data class RequestEventAccount (var eventType: Int,
                                        var mutableList: MutableList<Object>? = null,
                                        var objectMutable: Object? = null,
                                        var mensajeError: String?
) {
    companion object {
        val SAVE_EVENT: Int = 1
        val UPDATE_EVENT: Int = 2
        val DELETE_EVENT: Int = 3
        val ERROR_EVENT: Int = 4

        val LIST_EVENT_METODO_PAGO: Int = 5
        val LIST_EVENT_DETALLE_METODO_PAGO: Int = 6

        //Error Connection
        val ERROR_VERIFICATE_CONECTION: Int = 7

    }
}