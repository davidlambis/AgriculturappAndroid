package com.interedes.agriculturappv3.services.coords

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.Service
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.Coords
import com.interedes.agriculturappv3.asistencia_tecnica.services.coords.CoordsService

/**
 * Created by usuario on 13/03/2018.
 */
class CoordsServiceKotlin(var context_: Context): Service(), LocationListener {

    private var context: Context? = null
    var latitud: Double = 0.toDouble()
    var longitud: Double = 0.toDouble()
    var texto: TextView?=null
    //


    var serviceLocalizacionRun:Boolean? = false
    val location: Location?=null
    internal var gpsActivo: Boolean = false
    var locationManager: LocationManager? = null

    companion object {
        var instance:  CoordsServiceKotlin? = null
    }


    init {
        instance=this
        this.context = context_
        getLocation(context as Activity)
    }

    fun closeService() {
        this.stopSelf()
        if (locationManager != null) {
            locationManager!!.removeUpdates(this)
        }
    }

    fun setView(v: View) {
        texto = v as TextView
        texto!!.text = "Coordenadas: $latitud,$longitud"
    }

    fun getLocation(activity: Activity) {
        try {
            locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            gpsActivo = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

        } catch (ex: Exception) {

        }

        if (!isLocationEnabled()) {
            showGpsDisabledDialog()
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            return
        }


        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0f, this)
        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0f, this)

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

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location) {
        setLocation(location)
    }

    override fun onStatusChanged(s: String, status: Int, bundle: Bundle) {
        when (status) {
            LocationProvider.AVAILABLE ->  Log.i("GPS", "Disponible")
            LocationProvider.OUT_OF_SERVICE -> showStatusGPS("Proveedor fuera de servicio de localización")
            LocationProvider.TEMPORARILY_UNAVAILABLE -> {
                //showStatusGPS("Temporalmente no disponible el servicio de GPS")
            }
        }
    }

    override fun onProviderEnabled(s: String) {
        Toast.makeText(context, "GPS Activado", Toast.LENGTH_SHORT).show()
    }

    override fun onProviderDisabled(s: String) {
        //Toast.makeText(context,"GPS Desactivado",Toast.LENGTH_SHORT).show();
    }

    fun setLocation(loc: Location) {
        serviceLocalizacionRun = true
       // location = loc
        latitud = loc.latitude
        longitud = loc.longitude

        val retIntent = Intent("LOCATION")
        retIntent.putExtra("latitud", latitud)
        retIntent.putExtra("longitud", longitud)
        //retIntent.putExtra("initial", "" );
        context?.sendBroadcast(retIntent)
        ///   Toast.makeText(context,""+String.valueOf(loc.getLatitude())+" , "+String.valueOf(loc.getLongitude()),Toast.LENGTH_SHORT).show();
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        // texto.setText("Coordenadas: " + loc.getLatitude() + "," + loc.getLongitude());
    }


    //Verificar Estado GPS
    fun isLocationEnabled(): Boolean {
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun showGpsDisabledDialog(): Dialog {
        // Use the Builder class for convenient dialog construction
        val builder = AlertDialog.Builder(context!!)
        builder.setMessage(R.string.please_enable_gps)
                .setPositiveButton(R.string.confirm) { dialog, id ->
                    val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context!!.startActivity(settingsIntent)
                }
        builder.setTitle(R.string.gps_disabled)
        builder.setIcon(R.drawable.logo_agr_app)
        // Create the AlertDialog object and return it
        return builder.show()
    }

    private fun showStatusGPS(msg: String) {
        val dialog = android.app.AlertDialog.Builder(context)
        dialog.setMessage(msg).setCancelable(false)
                .setPositiveButton("OK") { dialogInterface, i -> dialogInterface.dismiss() }
        val alert = dialog.create()
        alert.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        closeService()
    }

    //methods

    fun serviceLocalizacionRun(): Location? {
        return location
    }

    fun isServiceLocalizacionRun(): Boolean? {
        return serviceLocalizacionRun
    }

    fun setServiceLocalizacionRun(serviceLocalizacionRun: Boolean) {
        CoordsServiceJava.serviceLocalizacionRun = serviceLocalizacionRun
    }

    fun getLocationCoords(): Coords {
        val coords= Coords(latitud, longitud,"");
        return coords;
    }
}