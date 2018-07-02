package com.interedes.agriculturappv3.activities.chat.online.conversations_user

class Conversacion_Interactor :IMainViewConversacion.Interactor{
    var repository: IMainViewConversacion.Repository? = null

    init {
        repository = Conversacion_Repository()
    }

    override fun getListRoom(checkConection: Boolean) {
        repository?.getListRoom(checkConection)
    }
}