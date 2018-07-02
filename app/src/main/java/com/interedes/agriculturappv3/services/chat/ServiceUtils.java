package com.interedes.agriculturappv3.services.chat;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver;
import com.interedes.agriculturappv3.services.resources.Chat_Resources;
import com.interedes.agriculturappv3.services.resources.Status_Chat;

public class ServiceUtils {
    private static ServiceConnection connectionServiceFriendChatForStart = null;
    private static ServiceConnection connectionServiceFriendChatForDestroy = null;

    private static String TAG = "FIREBASE UIID";

    public static boolean isServiceFriendChatRunning(Context context) {
        Class<?> serviceClass = ChatService.class;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void startServiceFriendChat(Context context) {
        if (!isServiceFriendChatRunning(context)) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                context.startForegroundService(new Intent(context, ChatService.class));
            } else {
                context.startService(new Intent(context, ChatService.class));
            }


        } else {
            if (connectionServiceFriendChatForStart != null) {
                context.unbindService(connectionServiceFriendChatForStart);
            }
            connectionServiceFriendChatForStart = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName className,
                                               IBinder service) {
                    ChatService.LocalBinder binder = (ChatService.LocalBinder) service;
                    /*for (Friend friend : binder.getService().listFriend.getListFriend()) {
                        binder.getService().mapMark.put(friend.idRoom, true);
                    }*/
                }

                @Override
                public void onServiceDisconnected(ComponentName arg0) {
                }
            };
            Intent intent = new Intent(context, ChatService.class);
            context.bindService(intent, connectionServiceFriendChatForStart, Context.BIND_NOT_FOREGROUND);
        }
    }


    public static void stopServiceFriendChat(Context context, final boolean kill) {
        if (isServiceFriendChatRunning(context)) {
            Intent intent = new Intent(context, ChatService.class);
            if (connectionServiceFriendChatForDestroy != null) {
                context.unbindService(connectionServiceFriendChatForDestroy);
            }
            connectionServiceFriendChatForDestroy = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName className,
                                               IBinder service) {
                    ChatService.LocalBinder binder = (ChatService.LocalBinder) service;
                    binder.getService().stopSelf();
                }

                @Override
                public void onServiceDisconnected(ComponentName arg0) {
                }
            };
            context.bindService(intent, connectionServiceFriendChatForDestroy, Context.BIND_NOT_FOREGROUND);
        }
    }



    public static void updateUserStatus(Context context){
        String uid = SharedPreferenceHelper.getInstance(context).getUID();
        if(checkConnection()) {
            if (!uid.equals("")) {

                Log.d(TAG, "FIREBASE SERVICE SECOND PLANE: " + uid);
                Chat_Resources.Companion.getMUserDBRef().child(uid+"/status").setValue(Status_Chat.Companion.getONLINE());
                //Chat_Resources.Companion.getMUserDBRef().child(uid+"/last_Online").setValue(ServerValue.TIMESTAMP);


                Chat_Resources.Companion.getMUserDBRef().child(uid+"/status").onDisconnect().setValue(Status_Chat.Companion.getOFFLINE());
                //Chat_Resources.Companion.getMUserDBRef().child(uid+"/last_Online").onDisconnect().setValue(ServerValue.TIMESTAMP);

                //Chat_Resources.Companion.getMUserDBRef().child(uid+"/last_Online").setValue(System.currentTimeMillis());
                //Chat_Resources.Companion.getMUserDBRef().child(uid+"/status").setValue(Status_Chat.Companion.getONLINE());

            }
        }else{
            if (!uid.equals("")) {
                Chat_Resources.Companion.getMUserDBRef().child(uid+"/status").setValue(Status_Chat.Companion.getOFFLINE());
                Chat_Resources.Companion.getMUserDBRef().child(uid+"/last_Online").setValue(ServerValue.TIMESTAMP);
            }
        }
    }


     public static boolean  checkConnection() {
        return ConnectivityReceiver.Companion.isConnected();
        //showSnack(isConnected);
    }

    public static boolean isNetworkConnected(Context context) {
        try{
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null;
        }catch (Exception e){
            return true;
        }
    }
}
