package com.interedes.agriculturappv3.util;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.interedes.agriculturappv3.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentMap extends Fragment implements OnMapReadyCallback{
    private boolean is_map_moveable;
    private GoogleMap map;
    private MapView mapView;
    private List<LatLng> coordenate = new ArrayList<>();
    private int markers = 0;

    public FragmentMap() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}
