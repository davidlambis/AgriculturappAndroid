package com.interedes.agriculturappv3.modules.main_menu.ui.events




data class RequestEventMainMenu (var eventType: Int,
                                    var mutableList: MutableList<Object>? = null,
                                    var objectMutable: Object? = null,
                                    var mensajeError: String?
) {
    companion object {
        val SYNC_EVENT: Int = 0
        val ERROR_EVENT: Int = 1
        val ERROR_VERIFICATE_CONECTION=2

        val SYNC_RESUME=3
        val SYNC_RESUME_AUTOMATIC=4
        val SYNC_FOTOS_INSUMOS_PLAGAS=5

        val UPDATE_BADGE_NOTIIFCATIONS=6
        val SYNC_FOTOS_PRODUCTOS=7


    }
}
