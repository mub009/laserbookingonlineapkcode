package com.mohtaref.clinics;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    String lat;
    String lng;
    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ArrayList<HashMap<String, String>> Near_list = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("nearList");
        ArrayList<HashMap<String, String>> Near_All_list = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("nearAllList");

        Log.e("array","here :"+Near_list.toString());
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
        SharedPreferences prefc = getSharedPreferences("loc", Activity.MODE_PRIVATE);
        lat = prefc.getString("lat", "");
        lng = prefc.getString("lng", "");

        Log.e("lat nearby",""+lat);
        Log.e("lng nearby",""+lng);
        double longtuid=Double.parseDouble(lng);
        double lattudie=Double.parseDouble(lat);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lattudie, longtuid);
        mMap.addMarker(new MarkerOptions().position(sydney).title("My Location"));

      //  mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lattudie, longtuid), 12.0f));
        ArrayList<HashMap<String, String>> Near_list = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("nearList");
        ArrayList<HashMap<String, String>> Near_All_list = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("nearAllList");

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                int pos = mHashMap.get(marker);
                Intent intent = new Intent(MapActivity.this, ClinicPage.class);
                intent.putExtra("Clinic",Near_All_list.get(pos));
                startActivity(intent);
                overridePendingTransition(0, 0);

            }
        });
        for(int i = 0 ; i < Near_All_list.size() ; i++) {

            createMarker(Double.parseDouble(Near_All_list.get(i).get("lat")),Double.parseDouble(Near_All_list.get(i).get("lng")), Near_All_list.get(i).get("clinicName_en"),i);
        }

    }



    protected Marker createMarker(double latitude, double longitude, String title,Integer num) {
        Marker marker =  mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(title)
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_place_black_34dp)));
        mHashMap.put(marker,num);

        return marker;


    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }




    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null&&netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null&& mobile.isConnectedOrConnecting()) || (wifi != null&&wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }


    public void alert_message_net(String title, String body,Context context) {
        android.support.v7.app.AlertDialog.Builder alertDialog=new android.support.v7.app.AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(body);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        android.support.v7.app.AlertDialog dialog=alertDialog.create();
        dialog.show();
        dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected(context))
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();


                            finish();
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);


                        }
                    }, 1000);
                    Log.e("is connected","ture");
                }
            }
        });

    }
}
