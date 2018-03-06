package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;
import com.interedes.agriculturappv3.R;
import com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu.MenuMainActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LotesFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMapClickListener ,View.OnClickListener{

    private GoogleMap mMap;
    MapView mapView;
    Marker LastMarkerName;
    Polygon polygon;

    FloatingActionButton fabAddLote;
    View v;

    private LatLngBounds bounds;
    private LatLngBounds.Builder builder = new LatLngBounds.Builder();


    //List<Location> locationsGlobals= new ArrayList<>();
    public LotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lotes, container, false);

        fabAddLote= v.findViewById(R.id.fabAddLote);
        fabAddLote.setOnClickListener(this);

        mapView = v.findViewById(R.id.mapLotes);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try{
            MapsInitializer.initialize(getContext());
        }catch (Exception e){
            Log.i("ErrorMap",e.getMessage());
        }
        mapView.getMapAsync(this);
        return v;
    }

    //call this method in your onCreateMethod
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);

            ArrayList<LatLng> locations = new ArrayList();
            locations.add(new LatLng(37.35, -122.0));
            locations.add(new LatLng(37.45, -122.0));
            locations.add(new LatLng(37.45, -122.2));
            locations.add(new LatLng(37.35, -122.2));
            locations.add(new LatLng(37.35, -122.0));

            double area = SphericalUtil.computeArea(locations);
            PolygonOptions rectOptions = new PolygonOptions();
            rectOptions.addAll(locations);
            rectOptions.strokeColor(ContextCompat.getColor(getActivity(),android.R.color.holo_orange_dark));
            rectOptions.fillColor(ContextCompat.getColor(getActivity(),android.R.color.holo_orange_light));

            // Get back the mutable Polygon
            polygon = mMap.addPolygon(rectOptions);
            getPolygonCenterPoint(locations);
            //LatLng positionInitial = getPolygonCenterPoint(locations);
            /// mMap.addMarker(new MarkerOptions().position(positionInitial).title("Ecuador"));
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionInitial, 12));

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.animateCamera(cu);
            // Setting a click event handler for the map
            mMap.setOnMapClickListener(this);

          // double area= calculateAreaOfGPSPolygonOnEarthInSquareMeters(locationsGlobals);
           Toast.makeText(getActivity(),"AREA: "+String.format("%.0f mts", area),Toast.LENGTH_LONG).show();
        }
    }






    private LatLng getPolygonCenterPoint(ArrayList<LatLng> polygonPointsList){
        LatLng centerLatLng = null;
        for(int i = 0 ; i < polygonPointsList.size() ; i++)
        {
            builder.include(polygonPointsList.get(i));
            /*Location locationadd= new Location("");
            locationadd.setLatitude(polygonPointsList.get(i).latitude);
            locationadd.setLatitude(polygonPointsList.get(i).latitude);
            locationsGlobals.add(locationadd);*/
        }
        bounds = builder.build();
        centerLatLng =  bounds.getCenter();
        return centerLatLng;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(LastMarkerName!=null){
            LastMarkerName.remove();
        }
        // Creating a marker
        LastMarkerName= drawMarker(latLng,latLng.latitude + " : " + latLng.longitude,"Location",BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
    }

    //Dibujar Marker
    private Marker drawMarker(LatLng latLng, String title, String snippet, BitmapDescriptor bitmapDescriptor) {
        // Instantiating CircleOptions to draw a circle around the marker
        MarkerOptions markerOption = new MarkerOptions();
        //mMap.clear();
        // Specifying the psition of the marker
        markerOption.position(latLng);
        // Title marker
        markerOption.title(title);
        // Fill color of the marker
        markerOption.icon(bitmapDescriptor);
        //Snip
        markerOption.snippet(snippet);
        markerOption.draggable(true);

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        // Adding the circle to the GoogleMap
        return mMap.addMarker(markerOption);
    }










    /*ADD LOTE*/
    /*------------------------------------------------------------------------------------------------------*/
    /*------------------------------------------------------------------------------------------------------*/
    public Dialog AddLote() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        v=inflater.inflate(R.layout.dialog_lote, null);

        TextView coordsLote= v.findViewById(R.id.coordenadas_lote);

        if(LastMarkerName!=null){
            coordsLote.setText(String.valueOf(LastMarkerName.getPosition().latitude+" / "+LastMarkerName.getPosition().longitude));

        }


        builder.setView(v);
        builder.setTitle(getString(R.string.add_lote));
        builder.setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Snackbar.make(v, "No se realizaron cambios", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        builder.setMessage(getString(R.string.message_add_lote));
        builder.setIcon(R.drawable.ic_lote);
        return builder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAddLote:
                AddLote();
                break;

        }
    }

    //region MAPA
    /*------------------------------------------------------------------------------------------------------------*/

}
