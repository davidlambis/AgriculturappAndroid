package com.interedes.agriculturappv3.modules.models.Notification

import com.google.gson.annotations.SerializedName


data class DataPostNotifcation(@SerializedName("title")
                            var title: String? = null,

                            @SerializedName("message")
                            var message: String? = null,

                            @SerializedName("image_url")
                            var image_url: String? = null

                         ) {
}