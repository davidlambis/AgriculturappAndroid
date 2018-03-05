package com.interedes.agriculturappv3.util;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.interedes.agriculturappv3.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentMap extends Fragment implements OnMapReadyCallback,LocationListener{
    private boolean is_map_moveable;
    private GoogleMap map;
    private MapView mapView;
    private List<LatLng> coordenate = new ArrayList<>();
    private int markers = 0;
    private Location location;

    public FragmentMap() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isConnected()){
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            /*if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                alertNoGps();
            }else */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    showStatusGPS("Debes otorgar permisos GPS a la aplicación");
                } else {
                    Log.i("asda", "pasó2");
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        alertNoGps();
                    }else{
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            } else {
                Log.i("asda", "pasó");
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    alertNoGps();
                }else{
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.layout_map,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try{
            MapsInitializer.initialize(getContext());
        }catch (Exception e){
            Log.i("ErrorMap",e.getMessage());
        }

        mapView.getMapAsync(this);
        showLocation(location);

        Button btn = view.findViewById(R.id.btnFinMap);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_map_moveable = !is_map_moveable;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                markers++;
                if (markers>0 && markers<5){
                    coordenate.add(latLng);
                    map.addMarker(new MarkerOptions().position(latLng));
                    if (markers==4){
                        drawPoligono();
                    }
                }
            }
        });
    }

    private void drawPoligono(){
        PolygonOptions mOptions = new PolygonOptions();
        for (int i = 0; i < coordenate.size(); i++) {
            mOptions.add(coordenate.get(i));
        }
        mOptions.strokeColor(Color.BLUE);
        mOptions.strokeWidth(7);
        mOptions.fillColor(Color.CYAN);
        Polygon line = map.addPolygon(mOptions);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
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

    private boolean isConnected(){
        ConexionInternet conexionInternet = new ConexionInternet();
        if (conexionInternet.conectadoDatos(getContext())){
            return true;
        }else if (conexionInternet.conectadoWifi(getContext())){
            return true;
        }else{
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                builder = new AlertDialog.Builder(getActivity(),
                        android.R.style.Theme_Material_Dialog_Alert);
            }else{
                builder = new AlertDialog.Builder(getActivity());
            }
            builder.setTitle(R.string.alert).setMessage(R.string.msgAlertConn)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
            return false;
        }
    }

    private void alertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.infoGPS)
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        showLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle bundle) {
        switch (status){
            case LocationProvider.AVAILABLE:
                Log.i("GPS","Disponible");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                showStatusGPS(getString(R.string.GpsOutService));
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                showStatusGPS(getString(R.string.GpsTempUnavailable));
                break;
        }
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void showLocation(Location location){
        if (location!=null){
            Log.i("LOCATION",""+location.getLatitude() +" "+ location.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                    location.getLongitude()),13));
            CameraPosition camera = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(),location.getLongitude()))
                    .zoom(17).build();
            //map.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
            map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
                    location.getLongitude()))
            .title("Mi Ubicación").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location)));
        }
    }

    private void showStatusGPS(String msg){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setMessage(msg).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alert = dialog.create();
        alert.show();
    }
}
