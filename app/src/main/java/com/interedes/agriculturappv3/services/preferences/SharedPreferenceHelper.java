package com.interedes.agriculturappv3.services.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.interedes.agriculturappv3.modules.models.usuario.Usuario;

public class SharedPreferenceHelper {
    private static SharedPreferenceHelper instance = null;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private static String SHARE_USER_INFO = "userinfo";
    private static String SHARE_KEY_NAME = "name";
    private static String SHARE_KEY_EMAIL = "email";
    private static String SHARE_KEY_AVATA = "avata";
    private static String SHARE_KEY_UID = "uid";
    private static String SHARE_KEY_SERVICE_SYNC = "is_runing_service";

    private SharedPreferenceHelper() {}

    public static SharedPreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceHelper();
            preferences = context.getSharedPreferences(SHARE_USER_INFO, Context.MODE_PRIVATE);
            editor = preferences.edit();
        }
        return instance;
    }

    public void saveUserInfo(Usuario usuario) {
        editor.putString(SHARE_KEY_NAME, usuario.getNombre());
        editor.putString(SHARE_KEY_EMAIL, usuario.getEmail());
        //editor.putString(SHARE_KEY_AVATA, usuario.foto);
        editor.putString(SHARE_KEY_UID, usuario.getIdFirebase());
        editor.apply();
    }

    public void savePostSyncData(String syncStatus) {
        editor.putString(SHARE_KEY_SERVICE_SYNC, syncStatus);
        editor.apply();
    }

    public Usuario getUserInfo(){
        String userName = preferences.getString(SHARE_KEY_NAME, "");
        String email = preferences.getString(SHARE_KEY_EMAIL, "");
        String avatar = preferences.getString(SHARE_KEY_AVATA, "default");

        Usuario user = new Usuario();
        user.setNombre(userName);
        user.setEmail(email);
        //user.avata = avatar;
        return user;
    }

    public String getUID(){
        return preferences.getString(SHARE_KEY_UID, "");
    }

    public String getRuningService(){
        return preferences.getString(SHARE_KEY_SERVICE_SYNC, "");
    }

}
