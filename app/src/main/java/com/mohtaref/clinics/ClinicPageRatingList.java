package com.mohtaref.clinics;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class ClinicPageRatingList extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private AppBarConfiguration mAppBarConfiguration;
    ListView ratingListPage;
    ArrayList<HashMap<String,String>>ratinglistdata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_clinic_page_rating_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ratingListPage=(ListView)findViewById(R.id.ratinglist_of_rating_clinic);
        SharedPreferences pref=getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lng=pref.getString("Mylang","");
        ratinglistdata=new ArrayList<>();
        ratinglistdata=(ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("ratinglist");
        CustomListAdapter_clinicPage_ratings adapter_rating = new CustomListAdapter_clinicPage_ratings(
                getApplicationContext(), R.layout.list_item_rating_clinic_page, ratinglistdata,lng
        );
        ratingListPage.setAdapter(adapter_rating);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
    @Override
    public void onBackPressed() {
        overridePendingTransition(0, 0);
        finish();
    }
    public void Goback(View view){
        onBackPressed();
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected){
            alert_message_net(getResources().getString(R.string.nointernet),getResources().getString(R.string.checkinternet),this);

        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        MyApplication.getInstance().setConnectivityListener(this);
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


    public void alert_message_net(String title, String body, Context context) {
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
