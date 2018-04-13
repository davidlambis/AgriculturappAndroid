package com.interedes.agriculturappv3.activities.login.events


data class LoginEvent(var eventType: Int,
                      var mutableList: MutableList<Object>? = null,
                      var objectMutable: Object? = null,
                      var mensajeError: String?
) {
    companion object {
        val SAVE_EVENT: Int = 0
        val ERROR_EVENT: Int = 1
    }
}