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
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.resources.NotificationTypeResources
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import android.media.AudioAttributes
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.main_menu.ui.events.RequestEventMainMenu
import com.interedes.agriculturappv3.modules.notification.events.RequestEventsNotification
import com.interedes.agriculturappv3.services.notifications.repository.FirebaseInstanceRepository
import com.interedes.agriculturappv3.services.notifications.repository.IMainFirebaseInstance
import com.interedes.agriculturappv3.services.resources.TagNavigationResources

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val ADMIN_CHANNEL_ID = "admin_channel"
    private var notificationManager: NotificationManager? = null

    private var notificationSum= 1

    var repository: IMainFirebaseInstance.Repository? = null
    var eventBus: EventBus? = null
    init {
        repository = FirebaseInstanceRepository()
        eventBus = GreenRobotEventBus()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        val fcmNotificationBuilder= com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal()
        fcmNotificationBuilder.title=remoteMessage!!.data["title"]
        fcmNotificationBuilder.message= remoteMessage!!.data["message"]
        fcmNotificationBuilder.user_name=remoteMessage!!.data["user_name"]
        fcmNotificationBuilder.ui=remoteMessage!!.data["ui"]
        fcmNotificationBuilder.fcm_token=remoteMessage!!.data["fcm_token"]
        fcmNotificationBuilder.room_id=remoteMessage!!.data["room_id"]
        fcmNotificationBuilder.type_notification= remoteMessage!!.data["type_notification"]
        fcmNotificationBuilder.image_url= remoteMessage!!.data["image_url"]

        if (!fcmNotificationBuilder.type_notification.equals(NotificationTypeResources.NOTIFICATION_TYPE_MESSAGE_ONLINE)) {
            repository?.saveNotification(fcmNotificationBuilder)
            postEventMenu(RequestEventMainMenu.UPDATE_BADGE_NOTIIFCATIONS,null,null,null)
            postEventNotifications(RequestEventsNotification.RELOAD_LIST_NOTIFICATION,null,null,null)
        }

        displayCustomNotificationBigTex(fcmNotificationBuilder,this)
        /*
        //validate,change for splash activity
        val notificationIntent = Intent(this, MenuMainActivity::class.java)
        //if (MenuMainActivity.instance?.isAppRunning!!) {
            //Some action
       // } else {
            //Show notification as usual
       // }
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

       // val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

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
                        .bigPicture(bitmap))/*NotificationLocal with Image*/
                .setContentText(remoteMessage.data["message"])
                .setAutoCancel(true)
               // .setSound(defaultSoundUri)
                .addAction(R.drawable.ic_local_offer,
                        getString(R.string.notification_add_to_cart_button), likePendingIntent)
                .setContentIntent(pendingIntent)
        notificationBuilder.setOnlyAlertOnce(true)
        notificationBuilder.setDefaults(NotificationLocal.DEFAULT_SOUND)
        notificationBuilder.setDefaults(NotificationLocal.DEFAULT_VIBRATE)
        notificationManager!!.notify(notificationId, notificationBuilder.build())
        */
    }

    private fun postEventNotifications(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventsNotification(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    private fun postEventMenu(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventMainMenu(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
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


    private fun displayCustomNotificationBigTex(fcmNotificationLocalBuilder: com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal, context: Context) {
        if (notificationManager == null) {
            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
           // notificationManager = NotificationManagerCompat.from(this)
        }

        var bitmap:Bitmap?=null
        if( fcmNotificationLocalBuilder.image_url!=null){
            bitmap = fcmNotificationLocalBuilder.image_url.let { getBitmapfromUrl(it!!) }!!
        }


        //var intent:Intent?=null
        val intent = Intent(context, MenuMainActivity::class.java)

        if(fcmNotificationLocalBuilder.type_notification.equals(NotificationTypeResources.NOTIFICATION_TYPE_OFERTA) ||
                fcmNotificationLocalBuilder.type_notification.equals(NotificationTypeResources.NOTIFICATION_TYPE_CONFIRM_OFERTA) ||
                fcmNotificationLocalBuilder.type_notification.equals(NotificationTypeResources.NOTIFICATION_TYPE_REFUSED_OFERTA)){
               intent.putExtra(TagNavigationResources.TAG_NAVIGATE_OFERTAS,TagNavigationResources.NAVIGATE_OFERTAS)

        }else if(fcmNotificationLocalBuilder.type_notification.equals(NotificationTypeResources.NOTIFICATION_TYPE_MESSAGE_ONLINE)){


            intent.putExtra(TagNavigationResources.TAG_NAVIGATE_CHAT_ONLINE,TagNavigationResources.NAVIGATE_CHAT_ONLINE)
            intent.putExtra(TagNavigationResources.TAG_NOTIFICATION,fcmNotificationLocalBuilder)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val ticker = "Ver"
            val notificationId = Random().nextInt(60000)

            val builder: NotificationCompat.Builder
            //intent.putExtra(TagSmsResources.PHONE_NUMBER,smsAddress)
            //intent.putExtra(TagSmsResources.CONTACT_NAME,messageAdress)
            val pendingIntent: PendingIntent
            setupChannels(context)
            builder = NotificationCompat.Builder(context, ADMIN_CHANNEL_ID)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    or Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            pendingIntent = PendingIntent.getActivity(context, notificationId+1, intent, PendingIntent.FLAG_ONE_SHOT)
            builder.setContentTitle(fcmNotificationLocalBuilder.title)
                    .setSmallIcon(getNotificationIcon()) // required
                    .setContentText(fcmNotificationLocalBuilder.message)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setTicker(ticker)
                    //API Level min 16 is required
                    .setLargeIcon( if (bitmap!=null)bitmap  else BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_notification) )
                    //.setGroup(smsAddress)
                    .setBadgeIconType(R.mipmap.ic_launcher_notification)
                    .setContentIntent(pendingIntent)
                    ///.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setShowWhen(true)
                    .setWhen(Calendar.getInstance().getTimeInMillis())
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentInfo(fcmNotificationLocalBuilder.message)

            builder.setVibrate( longArrayOf(0))

            if (fcmNotificationLocalBuilder.type_notification.equals(NotificationTypeResources.NOTIFICATION_TYPE_MESSAGE_ONLINE)) {
                builder.setStyle( NotificationCompat.BigTextStyle()
                        .bigText(fcmNotificationLocalBuilder.message))
            }else if(fcmNotificationLocalBuilder.type_notification.equals(NotificationTypeResources.NOTIFICATION_TYPE_OFERTA) ||
                    fcmNotificationLocalBuilder.type_notification.equals(NotificationTypeResources.NOTIFICATION_TYPE_CONFIRM_OFERTA) ||
                            fcmNotificationLocalBuilder.type_notification.equals(NotificationTypeResources.NOTIFICATION_TYPE_REFUSED_OFERTA)){
                builder.setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap).setSummaryText(fcmNotificationLocalBuilder.message))
            }

            //builder.setDefaults(NotificationLocal.DEFAULT_VIBRATE)
            val notification = builder.build()
            notificationManager!!.notify(notificationId, notification)

        } else {
            val ticker = "Ver"
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val notificationId = 237
            notificationSum=notificationSum+1

            //intent.putExtra(TagSmsResources.PHONE_NUMBER,smsAddress)
            //intent.putExtra(TagSmsResources.CONTACT_NAME,messageAdress)
            var pendingIntent: PendingIntent? = null
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    or Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            pendingIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_ONE_SHOT)
            val notificationBuilder = NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(fcmNotificationLocalBuilder.title)
                    .setContentText(fcmNotificationLocalBuilder.message)
                    .setDefaults(Notification.DEFAULT_ALL) // must requires VIBRATE permission
                    .setPriority(NotificationCompat.PRIORITY_HIGH) //must give priority to High, Max which will considered as heads-up notification
                    .addAction(R.mipmap.ic_launcher_notification,
                            ticker, pendingIntent)
                    .setAutoCancel(true)
                    .setTicker(ticker)
                    .setShowWhen(true)
                    ///.setColor(context.getColor(R.color.colorPrimary))
                    .setSound(defaultSoundUri)
                    .setSmallIcon(getNotificationIcon())
                    .setContentIntent(pendingIntent)
                    .setLargeIcon( if (bitmap!=null)bitmap  else BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_notification) )
                    //.setGroupSummary(true)
                   // .setNumber(notificationSum)
                    .setWhen(System.currentTimeMillis())
                    .setContentInfo(fcmNotificationLocalBuilder.message)
                    //.setFullScreenIntent(pendingIntent, true)
                    //API Level min 16 is required
                    //.setStyle(NotificationCompat.BigTextStyle().setBigContentTitle(fcmNotificationLocalBuilder.title).bigText(fcmNotificationLocalBuilder.message))

            if (android.os.Build.VERSION.SDK_INT >= 21) {
                notificationBuilder.setColor(context.getResources().getColor(R.color.green_900))
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                notificationBuilder.setVibrate( longArrayOf(0))
            }

            if (fcmNotificationLocalBuilder.type_notification.equals(NotificationTypeResources.NOTIFICATION_TYPE_MESSAGE_ONLINE)) {
                notificationBuilder.setStyle( NotificationCompat.BigTextStyle()
                        .bigText(fcmNotificationLocalBuilder.message))
            }else if(fcmNotificationLocalBuilder.type_notification.equals(NotificationTypeResources.NOTIFICATION_TYPE_OFERTA)){
                notificationBuilder.setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap).setSummaryText(fcmNotificationLocalBuilder.message))
            }

             val notification:Notification = notificationBuilder.build();
            //val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager!!.notify(notificationId,notification)

        }
    }

    private fun getNotificationIcon(): Int {
        val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) R.mipmap.ic_launcher else R.mipmap.ic_launcher
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(context:Context) {
        val notificationSoundUri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val adminChannelName = context.getString(R.string.notifications_admin_channel_name)
        val adminChannelDescription = context.getString(R.string.notifications_admin_channel_description)

        val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.BLUE
        adminChannel.enableVibration(true)
        adminChannel.vibrationPattern= longArrayOf(100, 200, 300, 400, 500, 400, 500, 200, 500)
        adminChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        adminChannel.setShowBadge(true)
       // adminChannel.sound
        adminChannel.setSound(notificationSoundUri, audioAttributes)
        if (notificationManager != null) {
            notificationManager!!.createNotificationChannel(adminChannel)
        }
    }
}