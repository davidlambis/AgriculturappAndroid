package com.interedes.agriculturappv3.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConexionInternet {

    public ConexionInternet() {}

    public boolean conectadoWifi(Context context){
        ConnectivityManager conexion = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conexion != null) {
            NetworkInfo netInfo = conexion.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (netInfo != null) {
                if (netInfo.isConnected()){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean conectadoDatos(Context context) {
        ConnectivityManager conexion = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conexion != null) {
            NetworkInfo netInfo = conexion.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (netInfo != null) {
                if (netInfo.isConnected()){
                    return true;
                }
            }
        }
        return false;
    }
}
