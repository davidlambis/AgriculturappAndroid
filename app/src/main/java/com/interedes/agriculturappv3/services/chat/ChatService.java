package com.interedes.agriculturappv3.services.chat;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.interedes.agriculturappv3.R;
import com.interedes.agriculturappv3.services.resources.Const_Resources;

public class ChatService extends Service {


    private static String TAG = "FriendChatService";
    // Binder given to clients
    public final IBinder mBinder = new LocalBinder();
    public CountDownTimer updateOnline;

    public ChatService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //startForeground(1,new Notification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "OnStartService");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(this, "")
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("")
                    .setAutoCancel(true);

            Notification notification = builder.build();
            startForeground(1, notification);

        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Notification notification = builder.build();
            startForeground(1, notification);
        }

        updateOnline = new CountDownTimer(System.currentTimeMillis(), Const_Resources.Companion.getTIME_TO_REFRESH()) {
            @Override
            public void onTick(long l) {
                ServiceUtils.updateUserStatus(getApplicationContext());
            }

            @Override
            public void onFinish() {

            }
        };
        updateOnline.start();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "OnBindService");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateOnline.cancel();
        Log.d(TAG, "OnDestroyService");
    }



    public class LocalBinder extends Binder {
        public ChatService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ChatService.this;
        }
    }
}
