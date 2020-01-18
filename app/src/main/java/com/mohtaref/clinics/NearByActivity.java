package com.mohtaref.clinics;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class NearByActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,ConnectivityReceiver.ConnectivityReceiverListener {
    String token;
    String lat;
    String lng;
    ArrayList<HashMap<String, String>> Nearby_list;
    ArrayList<HashMap<String, String>> NearbyAllClinics_list;

    ListView list;
    final private String base_url = "https://laserbookingonline.com/manager/APIs/clientV2/";
    Button showMap;
    private AppBarConfiguration mAppBarConfiguration;
    ProgressDialog pd;


    boolean isguest = false;
    LinearLayout profile_side;
    LinearLayout rating_side;
    LinearLayout appointment_side;
    LinearLayout group_login_real;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by);
//        SharedPreferences prefd=getSharedPreferences("loc",Activity.MODE_PRIVATE);
//        lat = prefd.getString("lat", "");
//        lng = prefd.getString("lng", "");
        Intent intent = getIntent();
        SharedPreferences prefcd = getSharedPreferences("loc", Activity.MODE_PRIVATE);
        SharedPreferences prefcds = getSharedPreferences("user", Activity.MODE_PRIVATE);

        if(intent.getStringExtra("lat")!=null&&!intent.getStringExtra("lat").equals("null")&&!intent.getStringExtra("lat").equals("")){
            lat = intent.getStringExtra("lat");
            lng = intent.getStringExtra("lng");

            Log.e("lat nearby", "is " + lat);
            Log.e("lng nearby", "is " + lng);
        }
         else if(prefcd.getString("lat","")!=null&&!prefcd.getString("lat","").equals("")){
            lat = prefcd.getString("lat", "");
            lng = prefcd.getString("lng", "");
            Log.e("lat nearby loc", "is " + lat);
            Log.e("lng nearby loc", "is " + lng);
        }
         else{
            String data = prefcds.getString("data", "");
            try {
                JSONObject userdata = new JSONObject(data);
                lat=userdata.getString("lat");
                lng=userdata.getString("lng");
                Log.e("lat nearby user", "is " + lat);
                Log.e("lng nearby user", "is " + lng);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }




        loadlocal();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout_nearby);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_burger_icon_bar);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        Nearby_list = new ArrayList<>();
        NearbyAllClinics_list = new ArrayList<>();
        showMap = (Button) findViewById(R.id.show_in_map);
        showMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e("clikced right here", "working");
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.e("condetion fulfiled", "yerp");
                    showGPSDisabledAlertToUser();

                } else {
                    Intent i = new Intent(NearByActivity.this, MapActivity.class);
                    Log.e("size b efor map", "is " + Nearby_list.size());
                    i.putExtra("nearList", Nearby_list);
                    i.putExtra("nearAllList", NearbyAllClinics_list);

                    startActivity(i);
                }
            }
        });

        SharedPreferences prefg = getSharedPreferences("user", Activity.MODE_PRIVATE);
        String data = prefg.getString("data", "");
        try {
            JSONObject userdata = new JSONObject(data);
            isguest = userdata.getBoolean("isGuest");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("user is guest :", "" + isguest);

        if (isguest) {
            profile_side = (LinearLayout) findViewById(R.id.group_login);
            rating_side = (LinearLayout) findViewById(R.id.group_Rating);
            appointment_side = (LinearLayout) findViewById(R.id.group_my_appointments);
            group_login_real = (LinearLayout) findViewById(R.id.group_login_real);

            group_login_real.setVisibility(View.VISIBLE);
            profile_side.setVisibility(View.GONE);
            rating_side.setVisibility(View.GONE);
            appointment_side.setVisibility(View.GONE);
        }
      //  new NearByClinics().execute();
        new NearByAllClinics().execute();
    }

    @Override
    public void onBackPressed() {
    Intent intent=new Intent(NearByActivity.this,HomePage.class);
    startActivity(intent);
        overridePendingTransition(0, 0);
        finishAffinity();
        System.exit(0);

    }

    @Override
    public void onPause() {
        super.onPause();

        overridePendingTransition(0, 0);
    }


    public void logout(View view) {
        SharedPreferences.Editor editor = getSharedPreferences("user", Activity.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finishAffinity();
        System.exit(0);

    }

    public void searchPage(View view) {
        Intent i = new Intent(this, SearchActivity.class);
        startActivity(i);
        overridePendingTransition(0, 0);

    }

    public void home_page(View view) {
        Intent i = new Intent(this, HomePage.class);
        startActivity(i);
        finishAffinity();
        overridePendingTransition(0, 0);
        System.exit(0);
    }

    public void rating_page(View view) {
        Intent i = new Intent(this, Rating.class);
        startActivity(i);
        overridePendingTransition(0, 0);

    }

    public void my_appointments(View view) {
        Intent i = new Intent(this, MyAppointments.class);
        startActivity(i);
        overridePendingTransition(0, 0);

    }

    public void profile(View view) {
        Intent i = new Intent(this, Profile.class);
        startActivity(i);
        overridePendingTransition(0, 0);

    }


    public void About_us(View view) {
        Intent i = new Intent(this, About_us.class);
        startActivity(i);
        overridePendingTransition(0, 0);

    }

    public void Privacy_policy_page(View view) {
        Uri uri = Uri.parse("https://laserbookingonline.com/Privacy_Policy/"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }

    public void Terms_page(View view) {
        Intent i = new Intent(this, Terms.class);
        startActivity(i);
        overridePendingTransition(0, 0);

    }

    public void Register_Your_Clinic(View view) {
        SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lng = pref.getString("Mylang", "");
        Log.e("language", lng);
        if (lng.equals("ar")) {
            Uri uri = Uri.parse("https://laserbookingonline.com/register.php?lang=ar"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else {
            Uri uri = Uri.parse("https://laserbookingonline.com/register.php?lang=en"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }


    }

    public void callNumPhone(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL); //use ACTION_CALL class
        callIntent.setData(Uri.parse("tel:"+getResources().getString(R.string.phone_number)));    //this is the phone number calling
        //check permission
        //If the device is running Android 6.0 (API level 23) and the app's targetSdkVersion is 23 or higher,
        //the system asks the user to grant approval.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //request permission from user if the app hasn't got the required permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                    10);
            return;
        }else {     //have got permission
            try{
                startActivity(callIntent);  //call activity and make phone call
            }
            catch (android.content.ActivityNotFoundException ex){
            }
        }
        overridePendingTransition(0, 0);

    }
    public void FaceBook(View view) {
        Uri uri = Uri.parse("https://www.facebook.com/bookinglaser/"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }
    public void Snapchat(View view) {
        Uri uri = Uri.parse("https://www.snapchat.com/add/laserbooking"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }
    public void Twitter(View view) {
        Uri uri = Uri.parse("https://twitter.com/bookinglaser"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }
    public void Instagram(View view) {
        Uri uri = Uri.parse("https://www.instagram.com/laser.booking/"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
//    public void setLocale(String lang) {
//        // get language return 2 first letters lower case of the language //// in the other hand get display language return the name of the language
//        String CurrentLang = Locale.getDefault().getDisplayLanguage();
//        String Applang= Resources.getSystem().getConfiguration().locale.getDisplayLanguage();
//
//
//
//
//        Locale myLocale = new Locale(lang);
//        Resources res = getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        conf.locale = myLocale;
//        conf.setLocale(new Locale(lang));
//        res.updateConfiguration(conf, dm);
//        SharedPreferences.Editor editor=getSharedPreferences("Settings", Activity.MODE_PRIVATE).edit();
//        editor.putString("Mylang",lang);
//        editor.apply();
//        if(!CurrentLang.equals(Applang)){
//            recreate(); }
//
//
//
//    }
    public void loadlocal(){
        SharedPreferences pref=getSharedPreferences("Settings",Activity.MODE_PRIVATE);
        String lngs=pref.getString("Mylang","");
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        if(lngs.equals("ar")){
            pd.setMessage("جاري التحميل");

        }
        else{
            pd.setMessage("Loading");

        }

        pd.show();
       // setLocale(lng);
    }

    public void setLocale_en(View view ) {
        String lang="en";
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor=getSharedPreferences("Settings", Activity.MODE_PRIVATE).edit();
        editor.putString("Mylang",lang);
        editor.apply();
        Intent intent=getIntent();
        finishAffinity();
        startActivity(intent);
        overridePendingTransition(0, 0);

    }
    public void setLocale_ar(View view) {
        String lang="ar";
        Locale myLocale = new Locale(lang);
        //saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor=getSharedPreferences("Settings", Activity.MODE_PRIVATE).edit();
        editor.putString("Mylang",lang);
        editor.apply();
        Intent intent=getIntent();
        finishAffinity();
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. to access this page GPS is required please enable your GPS")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
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

    public class NearByClinics extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // getLastLocation();
//            categories_list.clear();
            Log.e("eneterd Nearby","yes");

            SharedPreferences pref = getSharedPreferences("user", Activity.MODE_PRIVATE);
            String data = pref.getString("data", "");
            try {
                JSONObject userdata = new JSONObject(data);
                token = userdata.getString("token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("user token is :", token);




        }

        @Override
        protected Void doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();

            HttpHandlerPostToken sh = new HttpHandlerPostToken();

            // Making a request to url and getting response
            String urlget = base_url + "nearbyClinicsInit";

            String jsonStr = sh.makeServiceCall(urlget, token, lng, lat);

            Log.e("is offers :", "Response from url countries: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONArray jsonarray = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject c = jsonarray.getJSONObject(i);
                        String avgScore = c.getString("avgScore");
                        String clinicId = c.getString("clinicId");
                        String clinicName_en = c.getString("clinicName_en");
                        String clinicName_ar = c.getString("clinicName_ar");
                        String description_en = c.getString("description_en");
                        String description_ar = c.getString("description_ar");
                        String address_en = c.getString("address_en");
                        String address_ar = c.getString("address_ar");
                        String lat_ob = c.getString("lat");
                        String lng_ob = c.getString("lng");
                        String IBAN = c.getString("IBAN");
                        String phone = c.getString("phone");
                        String clinicMobile = c.getString("clinicMobile");
                        String clinicEmail = c.getString("clinicEmail");
                        String logo = c.getString("logo");
                        String locked_period = c.getString("locked_period");
                        String active = c.getString("active");
                        String deleted = c.getString("deleted");
                        String created_at = c.getString("created_at");
                        String last_modify = c.getString("last_modify");
                        String imageId = c.getString("imageId");
                        String img = c.getString("img");
                        String img_m = c.getString("img_m");
                        String img_s = c.getString("img_s");
                        String img_t = c.getString("img_t");
                        String uuid = c.getString("uuid");
                        String isDefault = c.getString("isDefault");
                        String cover = c.getString("cover");



                        HashMap<String, String> offer_ob = new HashMap<>();

                        // adding each child node to HashMap key => value
                        offer_ob.put("avgScore", avgScore);
                        offer_ob.put("description_en", description_en);
                        offer_ob.put("description_ar", description_ar);
                        offer_ob.put("address_en", address_en);
                        offer_ob.put("address_ar", address_ar);
                        offer_ob.put("lat", lat_ob);
                        offer_ob.put("lng", lng_ob);
                        offer_ob.put("IBAN", IBAN);
                        offer_ob.put("phone", phone);
                        offer_ob.put("clinicMobile", clinicMobile);
                        offer_ob.put("clinicEmail", clinicEmail);
                        offer_ob.put("logo", logo);
                        offer_ob.put("locked_period", locked_period);
                        offer_ob.put("img", img);
                        offer_ob.put("img_m", img_m);
                        offer_ob.put("img_s", img_s);
                        offer_ob.put("img_t", img_t);
                        offer_ob.put("imageId", imageId);
                        offer_ob.put("active", active);
                        offer_ob.put("deleted", deleted);
                        offer_ob.put("created_at", created_at);
                        offer_ob.put("last_modify", last_modify);
                        offer_ob.put("uuid", uuid);
                        offer_ob.put("isDefault", isDefault);
                        offer_ob.put("clinicId", clinicId);
                        offer_ob.put("cover", cover);
                        offer_ob.put("clinicName_en", clinicName_en);
                        offer_ob.put("clinicName_ar", clinicName_ar);

                        if (lat != null && lng != null) {
                            String distance = c.getString("distance");
                            offer_ob.put("distance", distance);

                        }
                        Nearby_list.add(offer_ob);
               //         Log.e("nery list is :",""+Nearby_list.toString());
                    }
                } catch (final JSONException e) {
                    Log.e("", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //  formobject.buildDialog(R.style.DialogAnimation, "NO Records found","ok");


                        }
                    });

                }

            } else {
                Log.e("", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

//                        Toast.makeText(getApplicationContext(),
//                                "Couldn't get data from server. Check your internet connection or try later",
//                                Toast.LENGTH_LONG).show();

                    }
                });
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
            String lng = pref.getString("Mylang", "");
            Log.e("language", lng);
            Log.e("after it", "size is ; " + Nearby_list.size());

            list = (ListView) findViewById(R.id.list);

            //set the adapter of CustomList Adapter
            CustomListAdapter_NearBy adapter = new CustomListAdapter_NearBy(
                    getApplicationContext(), R.layout.list_item_clinics_nearby, Nearby_list,lng
            );

            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, String> offers = (HashMap<String, String>) parent.getAdapter().getItem(position);
//                    Intent intent = new Intent(Statistics_Activity.this, Stat_month.class);
//                    intent.putExtra("id", userid);
//                    intent.putExtra("year", year_forms_this);
//                    startActivity(intent);
                    Intent intent= new Intent(NearByActivity.this,ClinicPage.class);
                    intent.putExtra("Clinic", offers);
                    startActivity(intent);
                    //here i want to get the items
                }
            });




        }
    }
    public class NearByAllClinics extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // getLastLocation();
//            categories_list.clear();

            SharedPreferences pref = getSharedPreferences("user", Activity.MODE_PRIVATE);
            String data = pref.getString("data", "");
            try {
                JSONObject userdata = new JSONObject(data);
                token = userdata.getString("token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("user token is :", token);

//            SharedPreferences prefc = getSharedPreferences("loc", Activity.MODE_PRIVATE);
//            lat = prefc.getString("lat", "");
//            lng = prefc.getString("lng", "");
//
//            Log.e("lat nearby",""+lat);
//            Log.e("lng nearby",""+lng);


        }

        @Override
        protected Void doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();

            HttpHandlerPostToken sh = new HttpHandlerPostToken();

            // Making a request to url and getting response
            String urlget = base_url + "nearbyClinics";
            Log.e("lat","value is "+lat);
            String jsonStr = sh.makeServiceCall(urlget, token, lng, lat);

            Log.e("is offers :", "Response from url countries: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONArray jsonarray = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject c = jsonarray.getJSONObject(i);
                        String avgScore = c.getString("avgScore");
                        String clinicId = c.getString("clinicId");
                        String clinicName_en = c.getString("clinicName_en");
                        String clinicName_ar = c.getString("clinicName_ar");
                        String description_en = c.getString("description_en");
                        String description_ar = c.getString("description_ar");
                        String address_en = c.getString("address_en");
                        String address_ar = c.getString("address_ar");
                        String lat_ob = c.getString("lat");
                        String lng_ob = c.getString("lng");
                        String IBAN = c.getString("IBAN");
                        String phone = c.getString("phone");
                        String clinicMobile = c.getString("clinicMobile");
                        String clinicEmail = c.getString("clinicEmail");
                        String logo = c.getString("logo");
                        String locked_period = c.getString("locked_period");
                        String active = c.getString("active");
                        String deleted = c.getString("deleted");
                        String created_at = c.getString("created_at");
                        String last_modify = c.getString("last_modify");
                        String imageId = c.getString("imageId");
                        String img = c.getString("img");
                        String img_m = c.getString("img_m");
                        String img_s = c.getString("img_s");
                        String img_t = c.getString("img_t");
                        String uuid = c.getString("uuid");
                        String isDefault = c.getString("isDefault");
                        String cover = c.getString("cover");



                        HashMap<String, String> offer_ob = new HashMap<>();

                        // adding each child node to HashMap key => value
                        offer_ob.put("avgScore", avgScore);
                        offer_ob.put("description_en", description_en);
                        offer_ob.put("description_ar", description_ar);
                        offer_ob.put("address_en", address_en);
                        offer_ob.put("address_ar", address_ar);
                        offer_ob.put("lat", lat_ob);
                        offer_ob.put("lng", lng_ob);
                        offer_ob.put("IBAN", IBAN);
                        offer_ob.put("phone", phone);
                        offer_ob.put("clinicMobile", clinicMobile);
                        offer_ob.put("clinicEmail", clinicEmail);
                        offer_ob.put("logo", logo);
                        offer_ob.put("locked_period", locked_period);
                        offer_ob.put("img", img);
                        offer_ob.put("img_m", img_m);
                        offer_ob.put("img_s", img_s);
                        offer_ob.put("img_t", img_t);
                        offer_ob.put("imageId", imageId);
                        offer_ob.put("active", active);
                        offer_ob.put("deleted", deleted);
                        offer_ob.put("created_at", created_at);
                        offer_ob.put("last_modify", last_modify);
                        offer_ob.put("uuid", uuid);
                        offer_ob.put("isDefault", isDefault);
                        offer_ob.put("clinicId", clinicId);
                        offer_ob.put("cover", cover);
                        offer_ob.put("clinicName_en", clinicName_en);
                        offer_ob.put("clinicName_ar", clinicName_ar);

                        if (lat != null && lng != null) {
                            String distance = c.getString("distance");
                            offer_ob.put("distance", distance);

                        }
                        NearbyAllClinics_list.add(offer_ob);
                        //         Log.e("nery list is :",""+Nearby_list.toString());
                    }
                } catch (final JSONException e) {
                    Log.e("", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //  formobject.buildDialog(R.style.DialogAnimation, "NO Records found","ok");


                        }
                    });

                }

            } else {
                Log.e("", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

//                        Toast.makeText(getApplicationContext(),
//                                "Couldn't get data from server. Check your internet connection or try later",
//                                Toast.LENGTH_LONG).show();

                    }
                });
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
            String lng = pref.getString("Mylang", "");
            Log.e("language", lng);
            Log.e("after it", "size is ; " + NearbyAllClinics_list.size());

            list = (ListView) findViewById(R.id.list);

            //set the adapter of CustomList Adapter
            CustomListAdapter_NearBy adapter = new CustomListAdapter_NearBy(
                    getApplicationContext(), R.layout.list_item_clinics_nearby, NearbyAllClinics_list,lng
            );

            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, String> offers = (HashMap<String, String>) parent.getAdapter().getItem(position);
//                    Intent intent = new Intent(Statistics_Activity.this, Stat_month.class);
//                    intent.putExtra("id", userid);
//                    intent.putExtra("year", year_forms_this);
//                    startActivity(intent);
                    Intent intent= new Intent(NearByActivity.this,ClinicPage.class);
                    intent.putExtra("Clinic", offers);
                    startActivity(intent);
                    //here i want to get the items
                }
            });

            Runnable progressRunnable = new Runnable() {

                @Override
                public void run() {
                    pd.cancel();
                }
            };

            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 2500);

        }
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
