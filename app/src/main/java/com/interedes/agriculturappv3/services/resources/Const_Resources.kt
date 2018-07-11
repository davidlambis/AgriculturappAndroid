package com.interedes.agriculturappv3.services.resources

/**
 * Created by usuario on 21/03/2018.
 */
class Const_Resources {

    companion object {

        var PAKAGE="com.interedes.agriculturappv3.services"

        var SERVICE_LOCATION="com.interedes.agriculturappv3.action.LOCATION"
        var SERVICE_CONECTIVITY="com.interedes.agriculturappv3.action.CONECTIVITY"


        //recive
        val SERVICE_RECYVE_MESSAGE="com.interedes.agriculturappv3.action.RECIVE_MESSAGE"

        //SMS
        val SERVICE_SMS_SENT="com.interedes.agriculturappv3.action.SERVICE_SMS_SENT"
        val SERVICE_SMS_DELIVERED="com.interedes.agriculturappv3.action.SERVICE_SMS_DELIVERED"



        //Service
        val ACTION_RUN_ISERVICE = "com.interedes.agriculturappv3.action.RUN_INTENT_SERVICE"
        val ACTION_PROGRESS_EXIT = "com.interedes.agriculturappv3.action.PROGRESS_EXIT"
        val EXTRA_PROGRESS = "com.interedes.agriculturappv3.action.PROGRESS"

        //Firebase
        val FIREBASE_TOKEN = "AAAA-D1BmYg:APA91bFT75lhEfyY15HblsNlm9fWtYa_pZRnIN0vV68XWbtLunslfO0T2LhM18s7o9OmQbmisOfx5FXaowTXHDs64UIjE5L99zwbgYtQZ-CU7wAfmS2qmB_XAJHi7TOEtOdC2MhALuWyw3SuPiSP7OM25vFXSbKgdw"



        //
        var REQUEST_CODE_REGISTER = 2000
        var STR_EXTRA_ACTION_LOGIN = "login"
        var STR_EXTRA_ACTION_RESET = "resetpass"
        var STR_EXTRA_ACTION = "action"
        var STR_EXTRA_USERNAME = "username"
        var STR_EXTRA_PASSWORD = "password"
        var STR_DEFAULT_BASE64 = "default"
        var UID = ""
        //TODO only use this UID for debug mode
        //    public static String UID = "6kU0SbJPF5QJKZTfvW1BqKolrx22";
        var INTENT_KEY_CHAT_FRIEND = "friendname"
        var INTENT_KEY_CHAT_AVATA = "friendavata"
        var INTENT_KEY_CHAT_ID = "friendid"
        var INTENT_KEY_CHAT_ROOM_ID = "roomid"
        var TIME_TO_REFRESH = (10 * 1000).toLong()
        var TIME_TO_OFFLINE = (2 * 60 * 1000).toLong()

    }


}