package com.interedes.agriculturappv3.services.notifications.events


data class RequestEventFirebaseService (var eventType: Int,
                          var mutableList: MutableList<Object>? = null,
                          var objectMutable: Object? = null,
                          var mensajeError: String?
) {
    companion object {
        val POST_SYNC_EVENT_TOKEN: Int = 0
        val ERROR_EVENT: Int = 1
        val ERROR_VERIFICATE_CONECTION=2
    }
}
