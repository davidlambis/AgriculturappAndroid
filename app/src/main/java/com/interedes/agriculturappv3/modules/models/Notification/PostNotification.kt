package com.interedes.agriculturappv3.modules.models.Notification

import com.google.gson.annotations.SerializedName

data class PostNotification(@SerializedName("to")
                                var to: String? = null,

                                @SerializedName("data")
                                var data: NotificationLocal? = null

                                ) {

}