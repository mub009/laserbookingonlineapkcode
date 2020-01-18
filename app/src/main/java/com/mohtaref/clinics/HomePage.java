package com.mohtaref.clinics;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;

import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;


import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

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
import java.util.Map;

public class HomePage extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    String token;
    String token2;
    String lat;
    String lng;
    boolean firstitemchangelang=true;
    public int Scrollofy = 0;
    public int Scrollofx = 0;
    SliderLayout sliderLayout;
    final private String base_url = "https://laserbookingonline.com/manager/APIs/clientV2/";
    boolean doubleBackToExitPressedOnce = false;
    public int numofItmesDown = 0;
    int numofItmesUP = -1;
    ArrayList<HashMap<String, String>> categories_list;
    ArrayList<HashMap<String, String>> offers_list;
    ArrayList<HashMap<String, String>> slider_offers_list;
    ArrayList<HashMap<String, String>> part_offers_list;
    public boolean pagesize = false;
    boolean downTriggerd = true;
    TextView cati1;
    TextView cati2;
    TextView cati3;
    TextView cati4;
    TextView cati5;
    TextView cati6;
    TextView cati7;
    ImageView img_cati1;
    ImageView img_cati2;
    ImageView img_cati3;
    ImageView img_cati4;
    ImageView img_cati5;
    ImageView img_cati6;
    ImageView img_cati7;
    ListView list;
    double latitude = 0.0;
    double longitude = 0.0;
    Location locationdata;
    public LocationManager lm;
    LocationManager locationManager;
    LocationManager mLocationManager;
    String locationText;
    String locationLatitude;
    String locationLongitude;
    Boolean requestingLocationUpdates = false;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    Boolean gps_enabled = false;
    Context context;
    EditText search_bar;
    ProgressDialog pd;
    Button cancel;
    Button nearby;
    boolean isguest = false;
    LinearLayout profile_side;
    LinearLayout rating_side;
    LinearLayout appointment_side;
    LinearLayout group_login_real;
    NestedScrollView homeMainLayout;
    public CustomListAdapter adapter;
    public Handler mHandler;
    public View ftView;
    public boolean isLoading = false;
    Map<String, ArrayList<HashMap<String, String>>> map = new HashMap();
    ProgressBar listprog;
    public boolean removed = false;
    public boolean refresh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gps_enabled) {
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(1 * 100)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 10); // 1 second, in milliseconds

            mGoogleApiClient = new GoogleApiClient.Builder(HomePage.this)
                    .addConnectionCallbacks(HomePage.this)
                    .addOnConnectionFailedListener(HomePage.this)
                    .addApi(LocationServices.API)
                    .build();
            Long time = mLocationRequest.getExpirationTime();
            final Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    tasks();
                }
            }, 2000);

        } else {
            tasks();
            if (longitude != 0.0 && latitude != 0.0) {
                lng = String.valueOf(longitude);
                lat = String.valueOf(latitude);
                Log.e("lat string is ", lat);
            } else
                Log.e("it'sempty", ": its empty");
        }



        super.onCreate(savedInstanceState);
        mHandler = new MyHandler();
        categories_list = new ArrayList<>();
        offers_list = new ArrayList<>();
        slider_offers_list = new ArrayList<>();
        part_offers_list = new ArrayList<>();
       // Locale.setDefault(new Locale("en"));
        setContentView(R.layout.activity_home_page);
        listprog = (ProgressBar) findViewById(R.id.listprogress);
        TextView cati1 = (TextView) findViewById(R.id.catgori1);
        TextView cati3 = (TextView) findViewById(R.id.catgori3);
        TextView cati4 = (TextView) findViewById(R.id.catgori4);
        TextView cati5 = (TextView) findViewById(R.id.catgori5);
        TextView cati6 = (TextView) findViewById(R.id.catgori6);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadlocal();

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //  ftView=li.inflate(R.layout.footer_view_loading_list,null);
        sliderLayout = findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(IndicatorAnimations.SLIDE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setSliderTransformAnimation(SliderAnimations.VERTICALFLIPTRANSFORMATION);
        sliderLayout.setScrollTimeInSec(3); //set scroll delay in seconds :
        ListView list = (ListView) findViewById(R.id.list);
        homeMainLayout = (NestedScrollView) findViewById(R.id.viewscroll);
        homeMainLayout.setNestedScrollingEnabled(false);

        SharedPreferences pref = getSharedPreferences("user", Activity.MODE_PRIVATE);
        String data = pref.getString("data", "");
        try {
            JSONObject userdata = new JSONObject(data);
            token = userdata.getString("token");
            isguest = userdata.getBoolean("isGuest");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("user token is :", token);
        Log.e("user is guest :", "" + isguest);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_burger_icon_bar);
        search_bar = (EditText) findViewById(R.id.Search);
        if (isguest) {
            profile_side = (LinearLayout) findViewById(R.id.group_login);
            rating_side = (LinearLayout) findViewById(R.id.group_Rating);
            appointment_side = (LinearLayout) findViewById(R.id.group_my_appointments);
            group_login_real = (LinearLayout) findViewById(R.id.group_login_real);
            profile_side.setVisibility(View.GONE);
            rating_side.setVisibility(View.GONE);
            appointment_side.setVisibility(View.GONE);
            group_login_real.setVisibility(View.VISIBLE);
        }


    }

    public void tasks() {

        new GetCategories().execute();

        new offers().execute();
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
    public void alert_message(String title, String body) {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(body);
        alertDialog.setCancelable(false);
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    if(isConnected(HomePage.this)){
                        finish();
                        startActivity(getIntent());
                    }

                    }

                });
        alertDialog.show();
    }

    private void handleNewLocation(Location location) {
        Log.e("ggz", location.toString());
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lng = String.valueOf(longitude);
        lat = String.valueOf(latitude);
        Log.e("lat string is ", lat);
    }

    @Override
    public void onResume() {
        super.onResume();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gps_enabled) {
            mGoogleApiClient.connect();
        } else
            return;

    }

    @Override
    protected void onPause() {
        super.onPause();

            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gps_enabled) {
                if (mGoogleApiClient.isConnected()) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                    mGoogleApiClient.disconnect();
                }
            }
            return;


    }


    private void setSliderViews(ArrayList<HashMap<String, String>> offers) {

        for (int i = 0; i < offers.size(); i++) {

            DefaultSliderView sliderView = new DefaultSliderView(this);

//            switch (i) {
//                case 0:
//                    sliderView.setImageDrawable(R.drawable.ic_launcher_background);
//                    break;
//                case 1:
//                    sliderView.setImageUrl("https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
//                    break;
//                case 2:
//                    sliderView.setImageUrl("https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260");
//                    break;
//                case 3:
//                    sliderView.setImageUrl("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
//                    break;
//
//            }
            sliderView.setImageUrl(offers.get(i).get("img").toString());


            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            //  sliderView.setDescription("setDescription " + (i + 1));
            final int finalI = i;
            sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(SliderView sliderView) {
                    HashMap<String, String> offers_slider = offers.get(finalI);
                    Intent intent = new Intent(HomePage.this, ClinicPage.class);
                    intent.putExtra("Clinic", offers_slider);
                    startActivity(intent);

                }
            });

            //at last add this view in your layout :
            sliderLayout.addSliderView(sliderView);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 1000);
        }
    }


    public void btn_nearbypage(View view) {
        Intent i = new Intent(HomePage.this, NearByActivity.class);
        i.putExtra("lat", lat);
        i.putExtra("lng", lng);
        startActivity(i);
        finishAffinity();
        System.exit(0);


    }

    public void btn7(View view) {
        Intent i = new Intent(HomePage.this, Clinics_List_Laser.class);
        i.putExtra("category", categories_list.get(4));
        startActivity(i);
    }

    public void btn6(View view) {
        Intent i = new Intent(HomePage.this, Clinics_List_Laser.class);
        i.putExtra("category", categories_list.get(3));
        startActivity(i);
    }

    public void btn5(View view) {
        Intent i = new Intent(HomePage.this, Clinics_List_Laser.class);
        i.putExtra("category", categories_list.get(2));
        startActivity(i);
    }

    public void btn4(View view) {
        Intent i = new Intent(HomePage.this, Clinics_List_Laser.class);
        i.putExtra("category", categories_list.get(1));
        startActivity(i);
    }

    public void btn3(View view) {
        Intent i = new Intent(HomePage.this, Clinics_List_Laser.class);
        i.putExtra("category", categories_list.get(0));
        startActivity(i);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.i("riiggghht", "Location services connected.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {


            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("riiggghht", "Location services Disconnectd.");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("you never know", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);

    }

    public void searchPage(View view) {
        Intent i = new Intent(this, SearchActivity.class);
        startActivity(i);
        overridePendingTransition(0, 0);

    }

    public void home_page(View view) {
        finish();
        startActivity(getIntent());

    }

    public void rating_page(View view) {
        Intent i = new Intent(HomePage.this, Rating.class);
        startActivity(i);
        overridePendingTransition(0, 0);

    }

    public void my_appointments(View view) {
        Intent i = new Intent(HomePage.this, MyAppointments.class);
        startActivity(i);
        overridePendingTransition(0, 0);

    }

    public void profile(View view) {
        Intent i = new Intent(HomePage.this, Profile.class);
        startActivity(i);
        overridePendingTransition(0, 0);

    }


    public void About_us(View view) {
        Intent i = new Intent(HomePage.this, About_us.class);
        startActivity(i);
        overridePendingTransition(0, 0);

    }

    public void Privacy_policy_page(View view) {
        Uri uri = Uri.parse("https://laserbookingonline.com/Privacy_Policy/"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }

    public void Terms_page(View view) {
        Intent i = new Intent(HomePage.this, Terms.class);
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
    public void setLocalexe(String lang) {
//        Log.e("setlocal is callled","now ");
//        SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
//        String languages = pref.getString("Mylang", "");
//        // get language return 2 first letters lower case of the language //// in the other hand get display language return the name of the language
//        String CurrentLang = Locale.getDefault().getDisplayLanguage();
//        String Applang= Resources.getSystem().getConfiguration().locale.getDisplayLanguage();
//        Log.e("CurrentLang ","is "+CurrentLang);
//        Log.e("Applang"," is "+Applang);
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
//            Log.e("setlocal  Recreate ","now ");
//            Log.e("setlocal  Recreate ","now " +Applang);
//            Log.e("setlocal  Recreate ","now "+CurrentLang);
//
//
//            recreate(); }
//        else{
//            Log.e("setlocal  Recreate ","now " +Applang);
//            Log.e("setlocal  Recreate ","now "+CurrentLang);
//            Log.e("didn't recreate","fail");
//
//        }
             Locale myLocale = new Locale(lang);
//        Resources res = getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        conf.locale = myLocale;
//        conf.setLocale(new Locale(lang));
//        res.updateConfiguration(conf, dm);

        myLocale = new Locale(lang);
     //   saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor=getSharedPreferences("Settings", Activity.MODE_PRIVATE).edit();
        editor.putString("Mylang",lang);
        editor.apply();
        recreate();



    }
    public void loadlocal(){
        SharedPreferences pref=getSharedPreferences("Settings",Activity.MODE_PRIVATE);
        String lng=pref.getString("Mylang","");
        Log.e("languge defult ","is "+Locale.getDefault().getLanguage());
        Log.e("languge saved ","is "+lng);

 //       if(!lng.equals(Locale.getDefault().getLanguage())){
        pd = new ProgressDialog(HomePage.this);
        pd.setCancelable(false);

        if(lng.equals("ar")){
            pd.setMessage("جاري التحميل");

        }
        else{
            pd.setMessage("Loading");

        }
        pd.show();

        Log.e("defult lang","is "+Locale.getDefault().getLanguage());
        Log.e("current saved lang","is "+lng);

//        if(!lng.equals(Locale.getDefault().getLanguage()))
//        setLocalexe(lng);

       // }
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
        finish();
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
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void logout(View view) {
        SharedPreferences.Editor editor=getSharedPreferences("user", Activity.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        Intent i=new Intent(this,LoginActivity.class);
        startActivity(i);
        finishAffinity();
        System.exit(0);

    }
    @Override
    protected void onStop(){
        super.onStop();
//        Glide.with(context).clear(list);
//        Glide.with(context).clear(sliderLayout);
    }



    @SuppressLint("StaticFieldLeak")
    public class GetCategories extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences pref = getSharedPreferences("user", Activity.MODE_PRIVATE);
            String data = pref.getString("data", "");
            try {
                JSONObject userdata = new JSONObject(data);
                token2 = userdata.getString("token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("user token is :", token2);
            if (longitude != 0.0 && latitude != 0.0) {
                lng = String.valueOf(longitude);
                lat = String.valueOf(latitude);
                Log.e("lat string is ", lat);
            }
        }

        @Override
        protected String doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();

            HttpHandlerGet sh = new HttpHandlerGet();

            // Making a request to url and getting response
            String urlget = base_url + "categories";
            String jsonStr = sh.makeServiceCall(urlget, token2);
            //Log.e("what is this chips", "idon't know?" + jsonStr);
           // Log.e("what to ", "doooooo");
            Log.e("is ", "Response from url countries: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONArray jsonarray = new JSONArray(jsonStr);
                 //   Log.e("Jsonarray size", "that is : " + jsonarray.length());
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject c = jsonarray.getJSONObject(i);
                        String categoryId = c.getString("categoryId");
                        String categoryName_en = c.getString("categoryName_en");
                        String categoryName_ar = c.getString("categoryName_ar");
                        String deleted = c.getString("deleted");
                        String created_at = c.getString("created_at");
                        String last_modify = c.getString("last_modify");
                        String fullimg=c.getString("fullimg");

                        // tmp hash map for single offer
                        HashMap<String, String> Categories_ob = new HashMap<>();

                        // adding each child node to HashMap key => value

                        Categories_ob.put("categoryId", categoryId);
                        Categories_ob.put("categoryName_en", categoryName_en);
                        Categories_ob.put("categoryName_ar", categoryName_ar);
                        Categories_ob.put("deleted", deleted);
                        Categories_ob.put("created_at", created_at);
                        Categories_ob.put("last_modify", last_modify);
                        Categories_ob.put("fullimg", fullimg);

                        categories_list.add(Categories_ob);
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


            return jsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(categories_list!=null&&!categories_list.isEmpty()){
            SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
            String lng = pref.getString("Mylang", "");
            Log.e("language", lng);
            cati3 = (TextView) findViewById(R.id.catgori3);
            cati4 = (TextView) findViewById(R.id.catgori4);
            cati5 = (TextView) findViewById(R.id.catgori5);
            cati6 = (TextView) findViewById(R.id.catgori6);
            cati7 = (TextView) findViewById(R.id.catgori7);

            if(lng.equals("ar")){
                String cat1 = categories_list.get(0).get("categoryName_ar");
                String cat2 = categories_list.get(1).get("categoryName_ar");
                String cat3 = categories_list.get(2).get("categoryName_ar");
                String cat4 = categories_list.get(3).get("categoryName_ar");
                String cat5 = categories_list.get(4).get("categoryName_ar");

                // String cat5 = categories_list.get(4).get("categoryName_ar");

                cati3.setText(cat1);
                cati4.setText(cat2);
                cati5.setText(cat3);
                cati6.setText(cat4);
                cati7.setText(cat5);


            }
            else {
                String cat1 = categories_list.get(0).get("categoryName_en");
                String cat2 = categories_list.get(1).get("categoryName_en");
                String cat3 = categories_list.get(2).get("categoryName_en");
                String cat4 = categories_list.get(3).get("categoryName_en");
                String cat5 = categories_list.get(4).get("categoryName_en");

                cati3.setText(cat1);
                cati4.setText(cat2);
                cati5.setText(cat3);
                cati6.setText(cat4);
                cati7.setText(cat5);

            }
            ///// set text






            ///// set icon image
            String img_cat1_string = categories_list.get(0).get("fullimg");
            String img_cat2_string = categories_list.get(1).get("fullimg");
            String img_cat3_string = categories_list.get(2).get("fullimg");
            String img_cat4_string = categories_list.get(3).get("fullimg");
            String img_cat5_string = categories_list.get(4).get("fullimg");


             img_cati3 =(ImageView)findViewById(R.id.img_btn3);
             img_cati4 =(ImageView)findViewById(R.id.img_btn4);
             img_cati5 =(ImageView)findViewById(R.id.img_btn5);
             img_cati6 =(ImageView)findViewById(R.id.img_btn6);
             img_cati7 =(ImageView)findViewById(R.id.img_btn7);

//            Picasso.get()
//                    .load(img_cat1_string)
//                    .resize(50, 50)
//                    .centerCrop()
//                    .into(img_cati3);

            Picasso.get().load(img_cat1_string).fit().into(img_cati3);
            Picasso.get().load(img_cat2_string).fit().into(img_cati4);
            Picasso.get().load(img_cat3_string).fit().into(img_cati5);
            Picasso.get().load(img_cat4_string).fit().into(img_cati6);
            Picasso.get().load(img_cat5_string).fit().into(img_cati7);

        }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class offers extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // getLastLocation();
//            categories_list.clear();

            SharedPreferences pref = getSharedPreferences("user", Activity.MODE_PRIVATE);
            String data = pref.getString("data", "");
            try {
                JSONObject userdata = new JSONObject(data);
                token2 = userdata.getString("token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("user token is :", token2);



        }

        @Override
        protected Void doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();

            HttpHandlerPostToken sh = new HttpHandlerPostToken();

            // Making a request to url and getting response
            String urlget = base_url + "offers";
            Log.e("url is this","that "+urlget);
            Log.e("url is this","that "+token2);
            Log.e("url is this","that "+lng);
            Log.e("url is this","that "+lat);

            String jsonStr = sh.makeServiceCall(urlget, token2, lng, lat);

            Log.e("is offers :", "Response from url countries: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONArray jsonarray = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject c = jsonarray.getJSONObject(i);
                        String offerId = c.getString("offerId");
                        String serviceId = c.getString("serviceId");
                        String offerTitle_en = c.getString("offerTitle_en");
                        String offerTitle_ar = c.getString("offerTitle_ar");
                        String offerDescription_en = c.getString("offerDescription_en");
                        String offerDescription_ar = c.getString("offerDescription_ar");
                        String offer_start = c.getString("offer_start");
                        String offer_end = c.getString("offer_end");
                        String priority = c.getString("priority");
                        String discount = c.getString("discount");
                        String paymentTypes = c.getString("paymentTypes");
                        String img = c.getString("img");
                        String img_m = c.getString("img_m");
                        String img_s = c.getString("img_s");
                        String sessionId = c.getString("sessionId");
                        String featured = c.getString("featured");
                        String active = c.getString("active");
                        String deleted = c.getString("deleted");
                        String created_at = c.getString("created_at");
                        String last_modify = c.getString("last_modify");
                        String serviceSeed = c.getString("serviceSeed");
                        String categoryId = c.getString("categoryId");
                        String clinicId = c.getString("clinicId");
                        String serviceName_en = c.getString("serviceName_en");
                        String serviceName_ar = c.getString("serviceName_ar");
                        String serviceDescription_en = c.getString("serviceDescription_en");
                        String serviceDescription_ar = c.getString("serviceDescription_ar");
                        String cost = c.getString("cost");
                        String duration = c.getString("duration");
                        String clinicName_en = c.getString("clinicName_en");
                        String clinicName_ar = c.getString("clinicName_ar");
                        String postCost = c.getString("postCost");


                        HashMap<String, String> offer_ob = new HashMap<>();

                        // adding each child node to HashMap key => value
                        offer_ob.put("offerId", offerId);
                        offer_ob.put("serviceId", serviceId);
                        offer_ob.put("offerTitle_en", offerTitle_en);
                        offer_ob.put("offerTitle_ar", offerTitle_ar);
                        offer_ob.put("offerDescription_en", offerDescription_en);
                        offer_ob.put("offerDescription_ar", offerDescription_ar);
                        offer_ob.put("offer_start", offer_start);
                        offer_ob.put("offer_end", offer_end);
                        offer_ob.put("priority", priority);
                        offer_ob.put("discount", discount);
                        offer_ob.put("paymentTypes", paymentTypes);
                        offer_ob.put("img", img);
                        offer_ob.put("img_m", img_m);
                        offer_ob.put("img_s", img_s);
                        offer_ob.put("sessionId", sessionId);
                        offer_ob.put("featured", featured);
                        offer_ob.put("active", active);
                        offer_ob.put("deleted", deleted);
                        offer_ob.put("created_at", created_at);
                        offer_ob.put("last_modify", last_modify);
                        offer_ob.put("serviceSeed", serviceSeed);
                        offer_ob.put("categoryId", categoryId);
                        offer_ob.put("clinicId", clinicId);
                        offer_ob.put("serviceName_en", serviceName_en);
                        offer_ob.put("serviceName_ar", serviceName_ar);
                        offer_ob.put("serviceDescription_en", serviceDescription_en);
                        offer_ob.put("serviceDescription_ar", serviceDescription_ar);
                        offer_ob.put("cost", cost);
                        offer_ob.put("duration", duration);
                        offer_ob.put("clinicName_en", clinicName_en);
                        offer_ob.put("clinicName_ar", clinicName_ar);
                        offer_ob.put("postCost", postCost);
                        if (lat != null && lng != null) {
                            String distance = c.getString("distance");
                            offer_ob.put("distance", distance);

                        }
                        offers_list.add(offer_ob);
                        if(slider_offers_list.size()<10&&featured.equals("1")){

                            slider_offers_list.add(offer_ob);
                        }
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
            Log.e("after it", "size is ; " + offers_list.size());
            if(offers_list!=null&&!offers_list.isEmpty()){
                Log.e("offers no empty","yeah full");

                for(int o=0;o<10;o++){
                    part_offers_list.add(offers_list.get(o));
                    numofItmesDown++;
                    Log.e("value of","is "+numofItmesDown);

                }
            }
            else{
                Log.e("offers is empty","yeah");
            }

            list = (ListView) findViewById(R.id.list);
            //set the adapter of CustomList Adapter
            adapter = new CustomListAdapter(
                    getApplicationContext(), R.layout.list_item, part_offers_list,lng
            );

            list.setAdapter(adapter);
//            list.setOnScrollListener(new AbsListView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(AbsListView absListView, int i) {
//
//                }
//
//                @Override
//                public void onScroll(AbsListView absListView, int firstVisibleItem, int VisibleItemCount, int totalItemCount) {
//                //check when scroll to last item listview in this tt , init data in listview= 10 items
//                    Log.e("list count","is "+list.getCount());
//                    Log.e("loaidng ","is "+isLoading);
//                    Log.e("totalItemCount","is "+totalItemCount);
//                    Log.e("lastpostion","is "+absListView.getLastVisiblePosition());
//
//                    if(absListView.getLastVisiblePosition()==totalItemCount-1&&list.getCount()>=10&& isLoading==false){
//                        Log.e("reached "," last");
//                        isLoading=true;
//                        pagesize=false;
//                        Thread thread=new ThreadGetMoreData();
//                        thread.start();
//
//                    }
          setListViewHeightBasedOnChildren(list);
//                }
//            });

//            list.setOnScrollListener(new EndlessScrollListener() {
//                @Override
//                public void onLoadMore(int page, int totalItemsCount) {
//                    for(int o=0;o<10;o++){
//                        part_offers_list.add(offers_list.get(numofItmes));
//                        numofItmes++;
//
//
//                    }
//                    adapter.notifyDataSetChanged();
//
//
//                }
//
//            });
            homeMainLayout.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if(v.getChildAt(v.getChildCount() - 1) != null) {

                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                            scrollY > oldScrollY&&isLoading==false) {
                        //code to fetch more data for endless scrolling
                        Log.e("ListActivity","LoadeMore");
                        Log.e("reached old X",""+oldScrollX);
                        Log.e("reached old Y",""+oldScrollY);
                        Log.e("reached X",""+scrollX);
                        Log.e("reached Y",""+scrollY);

                        if(removed){
                            Scrollofy=scrollY-oldScrollY;
                            Scrollofx=scrollX-oldScrollX;
                            Scrollofy=oldScrollY-9100;
                            Scrollofx=0;
                            Log.e("reomved item ","scrollofy: "+Scrollofy);
                            Log.e("reomved item"," Scrollofx " +Scrollofx);
                        }
                        isLoading=true;
                        downTriggerd=true;
                        pagesize=false;
                        Thread thread=new ThreadGetMoreData();
                        thread.start();
                        if(!removed){
                            //     homeMainLayout.scrollTo(oldScrollX,oldScrollY)
                            Scrollofy=oldScrollY;
                            Scrollofx=oldScrollX;
                            Log.e("not reomved item"," Scrollofx " +Scrollofx);
                            Log.e("not reomved item ","scrollofy: "+Scrollofy);

                        }

                     //   setListViewHeightBasedOnChildren(list);

                    }
                    if(scrollY==0&&isLoading==false){
                        Log.e("we are at", "TOP SCROLL");
                        isLoading=true;
                        downTriggerd=false;
                        pagesize=false;
                        if(removed){
                            Scrollofy=scrollY-oldScrollY;
                            Scrollofx=scrollX-oldScrollX;
                            Scrollofy=9100;
                            Scrollofx=0;
                            Log.e("reomved item ","scrollofy: "+Scrollofy);
                            Log.e("reomved item"," Scrollofx " +Scrollofx);
                        }
                        Thread thread=new ThreadGetMoreData();
                        thread.start();
                        if(!removed){
                            Scrollofx=scrollX;
                            Scrollofy=scrollY;
                        }

//                        Scrollofy=scrollY-oldScrollY;
//                        Scrollofx=scrollX-oldScrollX;
//                        Scrollofy=oldScrollY-Scrollofy;
//                        Scrollofx=oldScrollX-Scrollofx;
                        //homeMainLayout.scrollTo(oldScrollX,oldScrollY);


                    }
                }
            });

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, String> offers = (HashMap<String, String>) parent.getAdapter().getItem(position);
                    Intent intent= new Intent(HomePage.this,ClinicPage.class);
                    intent.putExtra("Clinic", offers);
                    startActivity(intent);

                    //here i want to get the items
                }
            });

        setSliderViews(slider_offers_list);
            Runnable progressRunnable = new Runnable() {

                @Override
                public void run() {
                    pd.cancel();

                }
            };

            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 2000);

        }
    }

    public class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    //add
                   // list.addFooterView(ftView);
                    listprog.setVisibility(View.VISIBLE);

                    break;

                case 1:
                    //update
if(refresh){Handler handler=new Handler();
    handler.postDelayed(new Runnable() {
        @Override
        public void run() {
            listprog.setVisibility(View.GONE);

            //Do something after 100ms
            adapter.addListItemToAdapter(map,downTriggerd);

            // list.removeFooterView(ftView);
            //Remove loading view
            if(removed){
                homeMainLayout.scrollTo(Scrollofx,Scrollofy);

            }
            setListViewHeightBasedOnChildren(list);

//                            if(!removed){
//                                homeMainLayout.scrollTo(Scrollofx,Scrollofy);
//
//                            }

            isLoading=false;
        }
    }, 2500);
}
else{

}

                    default:
                        break;
            }
        }
    }
    private ArrayList<HashMap<String,String>> getMoreData(){

        ArrayList<HashMap<String,String>>offeraddList=new ArrayList<>();
        ArrayList<HashMap<String,String>>offeraddListRemove=new ArrayList<>();

        for(int o=0;o<10;o++){
if(numofItmesDown!=offers_list.size()-1&&numofItmesUP!=offers_list.size()-1){
    //down scroll
if(downTriggerd){
    if(numofItmesDown>=50){
        offeraddList.add(offers_list.get(numofItmesDown));
        numofItmesDown++;
        numofItmesUP++;
        offeraddListRemove.add(offers_list.get(numofItmesUP));
         removed=true;
        refresh=true;


    }
    else{
        offeraddList.add(offers_list.get(numofItmesDown));
        numofItmesDown++;
        removed=false;
        refresh=true;

    }

}
//up scroll
else{
    if(numofItmesUP>=0){
        if(numofItmesDown>=50){
            Log.e("up counter"," "+numofItmesUP);
            Log.e("down counter"," "+numofItmesUP);

            offeraddList.add(offers_list.get(numofItmesUP));
            offeraddListRemove.add(offers_list.get(numofItmesDown));
             removed=true;
            refresh=true;

            numofItmesDown--;
            numofItmesUP--;

        }
        else{
            if(numofItmesUP!=0){
                offeraddList.add(offers_list.get(numofItmesUP));
                numofItmesDown--;
                numofItmesUP--;
            }
         else if(numofItmesUP==0) {
                offeraddList.add(offers_list.get(numofItmesUP));
            }
//
            map.put("offeraddList",offeraddList);
            map.put("offeraddListRemove",offeraddListRemove);
            return offeraddList;
        }
    }
    else {
      // refresh=false;
        map.put("offeraddList",offeraddList);
        map.put("offeraddListRemove",offeraddListRemove);
        return offeraddList;

    }


}

}

else if(downTriggerd&&numofItmesDown==offers_list.size()-1) {
    refresh=false;
Log.e("End of it","no more offers");
    map.put("offeraddList",offeraddList);
    map.put("offeraddListRemove",offeraddListRemove);
    return offeraddList;
}
else if (!downTriggerd&&numofItmesDown!=offers_list.size()-1){
    refresh=true;
    offeraddList.add(offers_list.get(numofItmesUP));
    offeraddListRemove.add(offers_list.get(numofItmesDown));
    removed=true;

    numofItmesDown--;
    numofItmesUP++;

}
else if(!downTriggerd&&numofItmesUP==offers_list.size()-1){
    refresh=false;
    Log.e("End of it","no more offers");
    map.put("offeraddList",offeraddList);
    map.put("offeraddListRemove",offeraddListRemove);
    return offeraddList;
}
else if(downTriggerd&&numofItmesUP==offers_list.size()-1){
    refresh=true;
    offeraddList.add(offers_list.get(numofItmesUP));
    offeraddListRemove.add(offers_list.get(numofItmesDown));
    removed=true;

    numofItmesDown++;
    numofItmesUP--;
}
        }
        map.put("offeraddList",offeraddList);
        map.put("offeraddListRemove",offeraddListRemove);
        return offeraddList;
    }
    //        if(numofItmesUP>0){
//            offeraddList.remove(numofItmesUP);
//
//            numofItmesUP--;
//        }
//        else{
//
//
//        }
    public class ThreadGetMoreData extends Thread{
        @Override
        public void run() {
            //add footer view afte get data
            if(refresh)
            mHandler.sendEmptyMessage(0);
            //search more data
           // ArrayList<HashMap<String,String>> listResult=
                    getMoreData();
            //delay time to show loading footer when debug, remove it when relese
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            //send result to handle
            Message msg= mHandler.obtainMessage(1,map);
            mHandler.sendMessage(msg);

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

    public void buildDialog(int animationSource, String type, String button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // builder.setTitle("");
        builder.setMessage(type);
        builder.setNegativeButton(button, null);
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = animationSource;
        dialog.show();

    }
//    public Location getloco(){
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//
//        }
//
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        // Got last known location. In some rare situations this can be null.
//                        Log.e("location is : "," "+location);
//                        if (location != null) {
//                            // Logic to handle location object
//                            locationdata=location;
//                            Log.e("locationdata is :"," "+locationdata);
//                            longitude=locationdata.getLongitude();
//                            latitude=location.getLatitude();
//                            Log.e("longitude is :"," this"+longitude);
//                            Log.e("latitude is :","this "+latitude);
//
//                        }
//
//                    }
//                });
//        Log.e("locationdata return:"," "+locationdata);
//
//    }+

    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
//        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationClient.getLastLocation()
//                .addOnSuccessListener(new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        // GPS location can be null if GPS is switched off
//                        if (location != null) {
//                            onLocationChanged(location);
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
//                        e.printStackTrace();
//                    }
//                });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        LocationListener listener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                if (location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
//                    // Do something with the recent location fix
//                    //  otherwise wait for the update below
//                } else {
//                    if (ActivityCompat.checkSelfPermission(HomePage.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomePage.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        // TODO: Consider calling
//                        //    ActivityCompat#requestPermissions
//                        // here to request the missing permissions, and then overriding
//                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                        //                                          int[] grantResults)
//                        // to handle the case where the user grants the permission. See the documentation
//                        // for ActivityCompat#requestPermissions for more details.
//                        return;
//                    }
//                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//                }
//                if (location != null) {
//                    Log.v("Location Changed", location.getLatitude() + " and " + location.getLongitude());
//                    mLocationManager.removeUpdates(this);
//                }
//            }
//
//
//            //TODO -- Barney implements the other three interface methods
//            // Required functions
//            public void onProviderDisabled(String arg0) {}
//            public void onProviderEnabled(String arg0) {}
//            public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
//
//  };


    }

    private void _getLocation() {
        // Get the location manager
        LocationManager locationManager = (LocationManager)
                getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        LocationListener loc_listener = new LocationListener() {

            public void onLocationChanged(Location l) {
            }

            public void onProviderEnabled(String p) {
            }

            public void onProviderDisabled(String p) {
            }

            public void onStatusChanged(String p, int status, Bundle extras) {
            }
        };
        locationManager
                .requestLocationUpdates(bestProvider, 0, 0, loc_listener);
        location = locationManager.getLastKnownLocation(bestProvider);
        try {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.e("longtuide is :", "" + longitude);
            Log.e("latitude is :", "" + latitude);
        } catch (NullPointerException e) {
            latitude = -1.0;
            longitude = -1.0;
        }
    }
    public Location getLocation() {

        Location location = null;
        final long MIN_DISTANCE_CHANGE_FOR_UPDATES =0; // 10 meters
        final long MIN_TIME_BW_UPDATES = 0; // 1 minute
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            try {
                LocationManager locationManager = (LocationManager) getApplicationContext()
                        .getSystemService(Context.LOCATION_SERVICE);

                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
//updating when location is changed
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                        }
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

                boolean isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                boolean isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
                Log.e("gps is what:","is :"+isGPSEnabled);
                if (!isNetworkEnabled && !isGPSEnabled) {
//call getLocation again in onResume method in this case
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                } else {

//                    if (isNetworkEnabled) {
//
//                        locationManager.requestLocationUpdates(
//                                LocationManager.NETWORK_PROVIDER,
//                                MIN_TIME_BW_UPDATES,
//                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
//                        if (locationManager != null) {
//                            location = locationManager
//                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                        }
//
//                    } else {

                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        Log.e("GPS", "GPS Enabled");
                    Log.e("GPS", "GPS Enabled"+locationManager);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            Log.e("GPS", "GPS Enabled"+location);


                        }

                 //   }


                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

//Request for the ACCESS FINE LOCATION
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//call the getLocation funtion again on permission granted callback
        }
        return location;
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
        pagesize=true;


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
//                            Intent i=new Intent(HomePage.this,HomePage.class);
//
//                            finish();
//                            startActivity(i);
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


