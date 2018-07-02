package com.interedes.agriculturappv3.modules.models.chat

import com.google.gson.annotations.SerializedName

data class RoomConversation(
        var UserFirebase:UserFirebase?= null,
        var Room: Room? = null
) {


}