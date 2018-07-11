package com.interedes.agriculturappv3.modules.models.Notification

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.services.resources.Const_Resources
import com.interedes.agriculturappv3.services.api.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


data class FcmNotificationBuilder(@SerializedName("title")
                            var title: String? = null,

                                  @SerializedName("message")
                            var message: String? = null,

                                  @SerializedName("image_url")
                            var image_url: String? = null,

                                  @SerializedName("ui")
                             var ui: String? = null,

                                  @SerializedName("user_name")
                             var user_name: String? = null,

                                  @SerializedName("receiver_firebase_token")
                             var receiver_firebase_token: String? = null,

                                  @SerializedName("room_id")
                             var room_id: String? = null,

                                  @SerializedName("type_notification")
                             var type_notification: String? = null

                         ) {
    companion object {
        var apiServiceFcm: ApiInterface? = null
        var instance:  FcmNotificationBuilder? = null
    }

    init {
        instance=this
        apiServiceFcm= ApiInterface.getClienNotifcation()
    }

    fun send(){
        val dataPostNotification= NotificationLocal(
                title=title,
                message = message,
                image_url = image_url,
                ui = ui,
                room_id = room_id,
                fcm_token=receiver_firebase_token,
                type_notification = type_notification,
                user_name = user_name)

        val postNotification = PostNotification(receiver_firebase_token,dataPostNotification
        )
        val call = apiServiceFcm?.postSendNotifcation("Key="+ Const_Resources.FIREBASE_TOKEN,postNotification)
        call?.enqueue(object : Callback<ResponsePostNotification> {
            override fun onResponse(call: Call<ResponsePostNotification>?, response: Response<ResponsePostNotification>?) {
                if (response != null && response.code() == 200) {
                    val value = response.body()
                    Log.e("OK  FCM", response?.message().toString())
                    //postEvent(RequestEventDetalleProducto.OK_SEND_EVENT_OFERTA, null, null,null)
                    //postEventOk(CultivoEvent.SAVE_EVENT, getCultivos(loteId), mCultivo)
                } else {
                    //postEventError(RequestEventDetalleProducto.ERROR_EVENT, "Comprueba tu conexión")
                    Log.e( "ERROR  FCM", response?.message().toString())
                }
            }
            override fun onFailure(call: Call<ResponsePostNotification>?, t: Throwable?) {
                //Log.e("error", response?.message().toString())
                Log.e("ERROR FCM",  t.toString())
            }
        })
    }
}