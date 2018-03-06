package com.interedes.agriculturappv3.asistencia_tecnica.services.coords

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
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.Coords

/**
 * Created by EnuarMunoz on 5/03/18.
 */
class CoordsService constructor(context_:Context): Service(), LocationListener {


    private var context: Context?=null

    var latitud: Double = 0.toDouble()
    var longitud: Double = 0.toDouble()
   // var localizacion = Coords(latitud,longitud,""
    var serviceLocalizacionRunBool:Boolean? = false


    val location: Location?=null

    internal var gpsActivo: Boolean = false
    var locationManager: LocationManager? = null

    var texto: TextView?=null

    val ServicioUbicacion = "ServicioUbicacion"


    init {

        this.context=context_
     //   this.context = this.applicationContext
        getLocation(context as Activity)
    }




    fun getUbicacion(): Location? {
        return location
    }

    fun closeService() {
        this.stopSelf()
        if (locationManager != null) {
            locationManager!!.removeUpdates(this)
            serviceLocalizacionRunBool=false;
        }
    }


    fun setView(v: View) {
        texto = v as TextView
        texto?.text = "Coordenadas: $latitud,$longitud"
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

        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0f, this)
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0f, this)

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

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

    }

    override fun onProviderEnabled(s: String) {
        Toast.makeText(context, "GPS Activado", Toast.LENGTH_SHORT).show()
    }

    override fun onProviderDisabled(s: String) {
        closeService();
        //Toast.makeText(context,"GPS Desactivado",Toast.LENGTH_SHORT).show();
    }

    fun setLocation(loc: Location) {
        serviceLocalizacionRunBool = true
        //location = loc
        latitud = loc.latitude
        longitud = loc.longitude

       // localizacion.Latitud=loc.latitude
        //localizacion.Longitud=loc.longitude

        val retIntent = Intent("LOCATION")
        retIntent.putExtra("latitud", latitud)
        retIntent.putExtra("longitud", longitud)
        //retIntent.putExtra("initial", "" );
        context?.sendBroadcast(retIntent)
        ///   Toast.makeText(context,""+String.valueOf(loc.getLatitude())+" , "+String.valueOf(loc.getLongitude()),Toast.LENGTH_SHORT).show();
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        // texto.setText("Coordenadas: " + loc.getLatitude() + "," + loc.getLongitude());
    }

    public fun  loadLatitud(): Double {
        return latitud;
    }


    //Verificar Estado GPS
    private fun isLocationEnabled(): Boolean {
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    fun showGpsDisabledDialog(): Dialog {
        // Use the Builder class for convenient dialog construction
        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.setMessage(R.string.please_enable_gps)
                ?.setPositiveButton(R.string.confirm, DialogInterface.OnClickListener { dialog, id ->
                    val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context?.startActivity(settingsIntent)
                })
        builder?.setTitle(R.string.gps_disabled)
        builder?.setIcon(R.drawable.logo_agr_app)
        // Create the AlertDialog object and return it
        return builder!!.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        closeService()
    }

    //methods

    public fun serviceLocalizacionRun(): Location? {
        return location
    }

    fun isServiceLocalizacionRun(): Boolean? {
        return serviceLocalizacionRunBool
    }

    fun setServiceLocalizacionRun(serviceLocalizacionRunBools: Boolean?) {
        serviceLocalizacionRunBool = serviceLocalizacionRunBools
    }
}