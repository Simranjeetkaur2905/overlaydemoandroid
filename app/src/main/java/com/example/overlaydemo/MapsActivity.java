package com.example.overlaydemo;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final int REQUEST_CODE = 1;

    private Marker hoemmarker;
    private Marker destmarker;

    Polyline line;
    Polygon shape;
    private final int POLYGON_NUMBER = 7;
    List<Marker> markers = new ArrayList<>();


    LocationManager locationManager;
    LocationListener locationListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
       locationListener = new LocationListener() {
           @Override
           public void onLocationChanged(Location location) {
               //set the home loaction

               setHomeLocation(location);




           }

           @Override
           public void onStatusChanged(String provider, int status, Bundle extras) {

           }

           @Override
           public void onProviderEnabled(String provider) {

           }

           @Override
           public void onProviderDisabled(String provider) {

           }
       };

       if(!checkPermission()){
           requestPermission();

       }
       else{
           getLocation();

           // long press on map
           mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
               @Override
               public void onMapLongClick(LatLng latLng) {
                   Location location = new Location("Your destination");
                   location .setLatitude(latLng.latitude);
                   location.setLongitude(latLng.longitude);
                   //set amarker

                   setMarker(location);
               }
           });
       }



    }

    private boolean checkPermission() {

        int permissionstatus = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionstatus == PackageManager.PERMISSION_GRANTED;


    }

    private  void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);


    }

    private void setMarker(Location location) {

        //mMap.clear();
        LatLng userlatlog = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userlatlog)
                .title("your destination")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("you are going here")
                .draggable(true);

//        if (destmarker == null) {
//            destmarker = mMap.addMarker(options);
//
//            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlatlog, 15));
//
//
//        }
//        else{
//            clearMap();
//            destmarker = mMap.addMarker(options);
//
//        }
//        drawline();
        /*
        this check if there are already the same number of markers as polygon number,
        so we can clear the map
         */

        if(markers.size() == POLYGON_NUMBER){
            clearMap();
        }
        markers.add(mMap.addMarker(options));
        if (markers.size() == POLYGON_NUMBER){
            drawshape();
        }


    }


    @SuppressLint("MissingPermission")
    private void getLocation(){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,locationListener);

        Location lastknownloaction = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //setv the known location at home location
        setHomeLocation(lastknownloaction);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(REQUEST_CODE == requestCode){
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,10,locationListener);

            }
        }
    }

    private void setHomeLocation(Location location){
       // mMap.clear();
        LatLng userlocation = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userlocation)
                .title("my location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("you are here");
        hoemmarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlocation,10));



    }

    private void clearMap(){
/*
        if(destmarker != null){
            destmarker.remove();
            destmarker = null;
        }
        line.remove();

 */

   for (Marker marker:markers)
    marker.remove();
       markers.clear();
       shape.remove();
       shape = null;
    }

    private void drawline(){
        PolylineOptions options = new PolylineOptions()
                .add(hoemmarker.getPosition())
                .add(destmarker.getPosition())
                .color(Color.RED)
                .width(10);

        line = mMap.addPolyline(options);
    }

    private void drawshape(){

        PolygonOptions options = new PolygonOptions()
                .fillColor(0x330000FF)
                .strokeWidth(5)
                .strokeColor(Color.BLUE);

        //shape = mMap.addPolygon(options);
        for (int i = 0;i < POLYGON_NUMBER; i ++)
            options.add(markers.get(i).getPosition());


            shape = mMap.addPolygon(options);


    }


}
