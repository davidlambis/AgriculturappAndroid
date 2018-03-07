package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote


import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil

import com.interedes.agriculturappv3.R
import kotlinx.android.synthetic.main.fragment_lote.*
import kotlinx.android.synthetic.main.dialog_lote.*
import kotlinx.android.synthetic.main.dialog_lote.view.*
import java.util.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class LoteFragment : Fragment(), OnMapReadyCallback,GoogleMap.OnMapClickListener ,View.OnClickListener {

    //Mapa
    private var mMap: GoogleMap? = null
    private var LastMarkerName: Marker? = null
    private var polygon: Polygon?=null
    private var bounds: LatLngBounds? = null
    private val builder = LatLngBounds.Builder()

    var viewDialog:View?= null;

    //Dialog
    private var dialog: AlertDialog.Builder?=null
    private var _dialog: AlertDialog? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lote, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabAddLote.setOnClickListener(this)

        mapViewLotes.onCreate(savedInstanceState)
        mapViewLotes.onResume()

        try {
            MapsInitializer.initialize(context!!)
        } catch (e: Exception) {
            Log.i("ErrorMap", e.message)
        }
        mapViewLotes.getMapAsync(this)
    }

    //region MAPA

    //On Map Reding
    override fun onMapReady(map: GoogleMap?) {
        mMap = map
        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
        } else {

            mMap?.setMyLocationEnabled(true)
            mMap?.getUiSettings()?.isZoomControlsEnabled = true
            mMap?.getUiSettings()?.isZoomGesturesEnabled = true

            val locations = ArrayList<LatLng>()
            locations.add(LatLng(37.35, -122.0))
            locations.add(LatLng(37.45, -122.0))
            locations.add(LatLng(37.45, -122.2))
            locations.add(LatLng(37.35, -122.2))
            locations.add(LatLng(37.35, -122.0))

            val area = SphericalUtil.computeArea(locations)
            val rectOptions = PolygonOptions()
            rectOptions.addAll(locations)
            rectOptions.strokeColor(ContextCompat.getColor(activity!!, android.R.color.holo_orange_dark))
            rectOptions.fillColor(ContextCompat.getColor(activity!!, android.R.color.holo_orange_light))

            // Get back the mutable Polygon
            polygon = mMap?.addPolygon(rectOptions)
            getPolygonCenterPoint(locations)
            //LatLng positionInitial = getPolygonCenterPoint(locations);
            /// mMap.addMarker(new MarkerOptions().position(positionInitial).title("Ecuador"));
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionInitial, 12));

            val width = resources.displayMetrics.widthPixels
            val height = resources.displayMetrics.heightPixels
            val padding = (width * 0.10).toInt() // offset from edges of the map 10% of screen
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
            mMap?.animateCamera(cu)
            // Setting a click event handler for the map
            mMap?.setOnMapClickListener(this)

            // double area= calculateAreaOfGPSPolygonOnEarthInSquareMeters(locationsGlobals);
            Toast.makeText(activity, "AREA: " + String.format("%.0f mts", area), Toast.LENGTH_LONG).show()
        }
    }

    ///Convert Coords Poligon to LatLng Unique
    private fun getPolygonCenterPoint(polygonPointsList: ArrayList<LatLng>): LatLng? {
        var centerLatLng: LatLng? = null
        for (i in polygonPointsList.indices) {
            builder.include(polygonPointsList[i])
            /*Location locationadd= new Location("");
            locationadd.setLatitude(polygonPointsList.get(i).latitude);
            locationadd.setLatitude(polygonPointsList.get(i).latitude);
            locationsGlobals.add(locationadd);*/
        }
        bounds = builder.build()
        centerLatLng = bounds?.getCenter()
        return centerLatLng
    }

    override fun onMapClick(latLng: LatLng) {
        if (LastMarkerName != null) {
            LastMarkerName?.remove()
        }
        // Creating a marker
        LastMarkerName = drawMarker(latLng, latLng.latitude.toString() + " : " + latLng.longitude, "Location", BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
    }


    //Properties and Drawing Marker
    private fun drawMarker(latLng: LatLng, title: String, snippet: String, bitmapDescriptor: BitmapDescriptor): Marker? {
        // Instantiating CircleOptions to draw a circle around the marker
        val markerOption = MarkerOptions()
        //mMap.clear();
        // Specifying the psition of the marker
        markerOption.position(latLng)
        // Title marker
        markerOption.title(title)
        // Fill color of the marker
        markerOption.icon(bitmapDescriptor)
        //Snip
        markerOption.snippet(snippet)
        markerOption.draggable(true)

        mMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))

        // Adding the circle to the GoogleMap
        return mMap?.addMarker(markerOption)
    }

    //endregion

    //region Events
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fabAddLote -> AddLote()
            R.id.ivClosetDialogLote->_dialog?.dismiss()
            R.id.btnCancelLote->_dialog?.dismiss()
            // R.id.fabAddLote -> AddLote()
        }
    }

    //endregion

    //region UI ELements
    /*------------------------------------------------------------------------------------------------------*/
    /*------------------------------------------------------------------------------------------------------*/
    //Dilog
    fun AddLote(): AlertDialog? {
            dialog = AlertDialog.Builder(activity!!)
            val inflater = this.layoutInflater
            viewDialog = inflater.inflate(R.layout.dialog_lote, null)
            val coordsLote = viewDialog?.coordenadas_lote


            val btnClosetDialogLote = viewDialog?.ivClosetDialogLote
            val btnCancelLote = viewDialog?.btnCancelLote

            btnCancelLote?.setOnClickListener(this)
            btnClosetDialogLote?.setOnClickListener(this)

            if (LastMarkerName != null) {
                coordsLote?.setText(LastMarkerName!!.getPosition().latitude.toString() + " / " + LastMarkerName!!.getPosition().longitude)
            }


            dialog?.setView(viewDialog)
            dialog?.setTitle(getString(R.string.add_lote))
            dialog?.setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->
                Snackbar.make(viewDialog?.coordenadas_lote!!, "No se realizaron cambios", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            })
            dialog?.setMessage(getString(R.string.message_add_lote))
            dialog?.setIcon(R.drawable.ic_lote)
            _dialog = dialog?.show()
             return _dialog
        }

    //endregion

    //region Overrides Methods
    //call this method in your onCreateMethod
    override fun onResume() {
        mapViewLotes.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapViewLotes.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapViewLotes.onLowMemory()
    }

    //endregion
}
