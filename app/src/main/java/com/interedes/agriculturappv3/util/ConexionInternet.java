package com.interedes.agriculturappv3.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

class ConexionInternet {

    ConexionInternet() {}

    boolean conectadoWifi(Context context){
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

    boolean conectadoDatos(Context context) {
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
