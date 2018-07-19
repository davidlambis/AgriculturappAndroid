package com.interedes.agriculturappv3.modules.main_menu.ui.events

import android.content.Intent
import com.interedes.agriculturappv3.modules.models.chat.Room
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase

data class RequestSendChat( var room: Room?=null,var userFirebase: UserFirebase?=null) {
}
