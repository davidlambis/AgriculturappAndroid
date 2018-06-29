package com.interedes.agriculturappv3.services.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.view.Menu
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.R.style.SplashTheme
import com.interedes.agriculturappv3.activities.splash.SplashActivity
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val NOTIFICATION_ID_EXTRA = "notificationId"
    private val IMAGE_URL_EXTRA = "imageUrl"
    private val ADMIN_CHANNEL_ID = "admin_channel"
    private var notificationManager: NotificationManager? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        //validate,change for splash activity
        val notificationIntent = Intent(this, MenuMainActivity::class.java)
        /*if (MenuMainActivity.instance?.isAppRunning!!) {
            //Some action
        } else {
            //Show notification as usual
        }*/
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,
                0 /* Request code */, notificationIntent,
                PendingIntent.FLAG_ONE_SHOT)

        //You should use an actual ID instead
        val notificationId = Random().nextInt(60000)


        val bitmap = remoteMessage!!.data["image_url"]?.let { getBitmapfromUrl(it) }

        val likeIntent = Intent(this, LikeService::class.java)
        likeIntent.putExtra(NOTIFICATION_ID_EXTRA, notificationId)
        likeIntent.putExtra(IMAGE_URL_EXTRA, remoteMessage!!.data["image_url"])
        val likePendingIntent = PendingIntent.getService(this,
                notificationId + 1, likeIntent, PendingIntent.FLAG_ONE_SHOT)


        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels()
        }

        val notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setLargeIcon(bitmap)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.data["title"])
                .setStyle(NotificationCompat.BigPictureStyle()
                        .setSummaryText(remoteMessage.data["message"])
                        .bigPicture(bitmap))/*Notification with Image*/
                .setContentText(remoteMessage.data["message"])
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .addAction(R.drawable.ic_local_offer,
                        getString(R.string.notification_add_to_cart_button), likePendingIntent)
                .setContentIntent(pendingIntent)


        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND)
        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE)

        notificationManager!!.notify(notificationId, notificationBuilder.build())

    }

    fun getBitmapfromUrl(imageUrl: String): Bitmap? {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels() {
        val adminChannelName = getString(R.string.notifications_admin_channel_name)
        val adminChannelDescription = getString(R.string.notifications_admin_channel_description)

        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        if (notificationManager != null) {
            notificationManager!!.createNotificationChannel(adminChannel)
        }
    }
}