package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.interedes.agriculturappv3.R;
import com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu.MenuMainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class LotesFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    MapView mapView;
    LocationManager mlocManager;
    Marker marcador;
    Circle circle;



    public LotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lotes, container, false);

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
       // mapView.onResume();
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

/*
            PolygonOptions rectOptions = new PolygonOptions()
                    .add(new LatLng(37.35, -122.0),
                            new LatLng(37.45, -122.0),
                            new LatLng(37.45, -122.2),
                            new LatLng(37.35, -122.2),
                            new LatLng(37.35, -122.0));

// Get back the mutable Polygon
            Polygon polygon = mMap.addPolygon(rectOptions);*/

            LatLng positionInitial = new LatLng(4.565473550710278, -74.058837890625);
            /// mMap.addMarker(new MarkerOptions().position(positionInitial).title("Ecuador"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionInitial, 6));


        }
    }
    //region MAPA
    /*------------------------------------------------------------------------------------------------------------*/

}
