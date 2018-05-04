package com.interedes.agriculturappv3.activities.chat.chat_sms

data class SmsUser(
                var _id: String? = null,
                var _address: String? = null,
                var _user_name: String? = null,
                var _msg: String? = null,
                var _readState: String? = null, //"0" for have not read sms and "1" for have read sms
                var _time: String? = null,
                var _folderName: String? = null) {
}