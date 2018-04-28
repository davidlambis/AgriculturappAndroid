package com.interedes.agriculturappv3.productor.models

import com.google.gson.annotations.SerializedName

data class ResetPassword(@SerializedName("username")
                         var username: String? = null) {

}
