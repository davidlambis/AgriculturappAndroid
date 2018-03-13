package com.interedes.agriculturappv3.services.coords;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.interedes.agriculturappv3.R;

/**
 * Created by usuario on 13/03/2018.
 */

public class CoordsServiceJava extends Service implements LocationListener {

    private final Context context;
    public static double latitud;
    public static double longitud;

    //
    public static boolean serviceLocalizacionRun=false;
    public static Location location;
    boolean gpsActivo;
    public LocationManager locationManager;

    TextView texto;


    public CoordsServiceJava(Context context) {
        super();
        this.context = context;
        getLocation((Activity) context);
    }

    public void closeService() {
        this.stopSelf();
        if(locationManager!=null){
            locationManager.removeUpdates(this);
        }
    }


    public void setView(View v) {
        texto = (TextView) v;
        texto.setText("Coordenadas: " + latitud + "," + longitud);
    }

    public void getLocation(Activity activity) {
        try {
            locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
            gpsActivo = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        } catch (Exception ex) {

        }

        if (!isLocationEnabled()) {
            showGpsDisabledDialog();
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, this);

        /*
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER
                    , 1000
                    , 1
                    , this);  //cada minuto y 1 segundo se actualiza el GPS
                       //Traer ultima ubicacion conocida
        location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        latitud = location.getLatitude();
        longitud = location.getLongitude();

                    */
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onLocationChanged(Location location) {
        setLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(context,"GPS Activado",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        //Toast.makeText(context,"GPS Desactivado",Toast.LENGTH_SHORT).show();
    }

    public void setLocation(Location loc) {
        serviceLocalizacionRun= true;
        location= loc;
        latitud = loc.getLatitude();
        longitud = loc.getLongitude();


        Intent retIntent = new Intent("LOCATION");
        retIntent.putExtra("latitud", latitud);
        retIntent.putExtra("longitud", longitud);
        //retIntent.putExtra("initial", "" );
        context.sendBroadcast(retIntent);
        ///   Toast.makeText(context,""+String.valueOf(loc.getLatitude())+" , "+String.valueOf(loc.getLongitude()),Toast.LENGTH_SHORT).show();
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        // texto.setText("Coordenadas: " + loc.getLatitude() + "," + loc.getLongitude());
    }


    //Verificar Estado GPS
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public Dialog showGpsDisabledDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.please_enable_gps)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(settingsIntent);
                    }
                });
        builder.setTitle(R.string.gps_disabled);
        builder.setIcon(R.drawable.logo_agr_app);
        // Create the AlertDialog object and return it
        return builder.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeService();
    }

    //methods

    public static Location serviceLocalizacionRun() {
        return location;
    }

    public static boolean isServiceLocalizacionRun() {
        return serviceLocalizacionRun;
    }

    public static void setServiceLocalizacionRun(boolean serviceLocalizacionRun) {
        CoordsServiceJava.serviceLocalizacionRun = serviceLocalizacionRun;
    }
}
