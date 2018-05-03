package com.interedes.agriculturappv3.activities.chat.chat_sms

import android.content.BroadcastReceiver
import android.widget.Toast
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.telephony.SmsMessage
import com.interedes.agriculturappv3.R


class MySmsBroadcastReceiver: BroadcastReceiver() {


    val SMS_BUNDLE = "pdus"

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

            //Get the user's settings
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            //Get the value for notifications
            val notificationsOn = sharedPreferences.getBoolean(SettingsActivity.NOTIFICATIONS_KEY, true)
            //boolean vibratorOn = sharedPreferences.getBoolean(SettingsActivity.VIBRATION_KEY, true);

            if (notificationsOn) {
                //Build the notification:
                val builder = NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_clientes)
                        .setContentTitle("Message from $smsAddress")
                        .setContentText(smsBody)
                builder.setAutoCancel(true)

                val notificationIntent = Intent(context, Chat_Sms_Activity::class.java)
                // The stack builder object will contain an artificial back stack for the started Activity.
                // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
                val stackBuilder = TaskStackBuilder.create(context)
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(Chat_Sms_Activity::class.java)
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(notificationIntent)
                val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

                builder.setContentIntent(pendingIntent)
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                //                int id = 123; //TODO: needs to be an integer unique to this application to be able to update the notification later on after it's created. (or could create multiple notifications?)
                notificationManager.notify(Chat_Sms_Activity.NOTIFICATION_ID, builder.build())
            }

            /*if (vibratorOn) {
                //Create the vibration
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

                if (vibrator.hasVibrator()) {
                    //vibrator.vibrate(long[] pattern, int repeat, AudioAttributes attributes)
                    //vibrator.vibrate(long[] pattern, int repeat)
                    //vibrator.vibrate(long milliseconds, AudioAttributes attributes)
                    //test
                    vibrator.vibrate(5);
                }
            }*/

            Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show()

            //This updates the UI with message
            val inst = Chat_Sms_Activity.instance
            inst.updateList(smsMessageStr)
        }
    }
}