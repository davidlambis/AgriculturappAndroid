package com.interedes.agriculturappv3.modules.credentials.events



data class RequestCredentialsEvents (var eventType: Int,
                                var mutableList: MutableList<Object>? = null,
                                var objectMutable: Object? = null,
                                var mensajeError: String?
) {
    companion object {
        val SAVE_EVENT: Int = 1
        val UPDATE_EVENT: Int = 2
        val DELETE_EVENT: Int = 3
        val ERROR_EVENT: Int = 4

        val ERROR_PASSWORD_EVENT: Int = 5



        //Error Connection
        val ERROR_VERIFICATE_CONECTION: Int = 7

    }
}