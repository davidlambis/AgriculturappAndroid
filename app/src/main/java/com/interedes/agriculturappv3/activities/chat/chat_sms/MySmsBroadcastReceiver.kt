package com.interedes.agriculturappv3.activities.chat.chat_sms

import android.app.*
import android.content.BroadcastReceiver
import android.widget.Toast
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.ContactsContract
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.telephony.SmsMessage
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.services.Const
import java.util.*


class MySmsBroadcastReceiver: BroadcastReceiver() {

    val SMS_BUNDLE = "pdus"
    var mChannel: NotificationChannel? = null
    var notifManager: NotificationManager? = null

    private val ADMIN_CHANNEL_ID = "admin_channel"

    /**
     * onReceive
     * Purpose:
     * Listens for SMS messages and adds them to the main List.
     * @param context
     * @param intent
     */
    override fun onReceive(context: Context, intent: Intent) {
        val intentExtras = intent.extras
        if (intentExtras != null) {
            val sms = intentExtras.get(SMS_BUNDLE) as Array<Any>
            var smsMessageStr = ""
            var smsBody = ""
            var smsAddress = ""
            for (i in sms.indices) {
                val smsMessage = SmsMessage.createFromPdu(sms[i] as ByteArray)

                smsBody = smsMessage.getMessageBody()
                smsAddress = smsMessage.getOriginatingAddress()

                smsMessageStr += "SMS From: $smsAddress\n"
                smsMessageStr += smsBody + "\n"
            }

           // if(smsMessageStr.contains(context.getString(R.string.idenfication_sms_app))){

                //Get the user's settings
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                //Get the value for notifications
                val notificationsOn = sharedPreferences.getBoolean(SettingsActivity.NOTIFICATIONS_KEY, true)
                //boolean vibratorOn = sharedPreferences.getBoolean(SettingsActivity.VIBRATION_KEY, true);

                if (notificationsOn) {

                    var messageContent= smsBody.replace(context.getString(R.string.idenfication_send_sms_app),"")
                    var messageAdress=getContactDisplayNameByNumber(smsAddress,context)
                    if(messageAdress.equals("")){
                        messageAdress=smsAddress
                    }



                    displayCustomNotificationForOrders(messageAdress, messageContent, context,smsAddress,messageAdress)
                    val retIntent = Intent(Const.SERVICE_RECYVE_MESSAGE)
                    retIntent.putExtra("new_message", smsMessageStr)
                    context.sendBroadcast(retIntent)

                    //Build the notification:
                }

           // }
        }
    }

    fun getContactDisplayNameByNumber(number: String?, context:Context?): String {
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        var name = ""
        val contentResolver = context?.contentResolver
        val contactLookup = contentResolver?.query(uri, arrayOf(BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME), null, null, null)
        try {
            if (contactLookup != null && contactLookup.count > 0) {
                contactLookup.moveToNext()
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME))
                //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            }
        } finally {
            contactLookup?.close()
        }

        return name
    }


    private fun displayCustomNotificationForOrders(title: String, description: String, context: Context,smsAddress:String,messageAdress:String) {
        if (notifManager == null) {
            notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        val notificationId = Random().nextInt(60000)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            val builder: NotificationCompat.Builder
            val intent = Intent(context, Chat_Sms_Activity::class.java)

            val TAG = "SMSCHATAPP"
            val TAG_USER_NAME = "USER_NAME"

            intent.putExtra(TAG,smsAddress)
            intent.putExtra(TAG_USER_NAME,messageAdress)



            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent: PendingIntent
            setupChannels(context)
            /*
            val importance = NotificationManager.IMPORTANCE_HIGH
            if (mChannel == null) {
                mChannel = NotificationChannel("0", title, importance)
                mChannel?.setDescription(description)
                mChannel?.enableVibration(true)
                notifManager?.createNotificationChannel(mChannel)
            }*/


            builder = NotificationCompat.Builder(context, ADMIN_CHANNEL_ID)

            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            pendingIntent = PendingIntent.getActivity(context, notificationId+1, intent, PendingIntent.FLAG_ONE_SHOT)
            builder.setContentTitle(title)
                    .setSmallIcon(getNotificationIcon()) // required
                    .setContentText(description)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_notification))
                    .setBadgeIconType(R.mipmap.ic_launcher_notification)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setShowWhen(true)
                    .setWhen(Calendar.getInstance().getTimeInMillis())
            val notification = builder.build()
            notifManager?.notify(notificationId, notification)
        } else {

            val intent = Intent(context, Chat_Sms_Activity::class.java)
            val TAG = "SMSCHATAPP"
            val TAG_USER_NAME = "USER_NAME"
            intent.putExtra(TAG,smsAddress)
            intent.putExtra(TAG_USER_NAME,messageAdress)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            var pendingIntent: PendingIntent? = null

            pendingIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_ONE_SHOT)

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setAutoCancel(true)
                    ///.setColor(context.getColor(R.color.colorPrimary))
                    .setSound(defaultSoundUri)
                    .setSmallIcon(getNotificationIcon())
                    .setContentIntent(pendingIntent)
                    .setStyle(NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(description))

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }

    private fun getNotificationIcon(): Int {
        val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) R.mipmap.ic_launcher else R.mipmap.ic_launcher
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(context:Context) {
        val adminChannelName = context.getString(R.string.notifications_admin_channel_name)
        val adminChannelDescription = context.getString(R.string.notifications_admin_channel_description)

        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        if (notifManager != null) {
            notifManager!!.createNotificationChannel(adminChannel)
        }
    }


}