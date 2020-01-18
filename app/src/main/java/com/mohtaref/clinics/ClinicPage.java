package com.mohtaref.clinics;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;


import androidx.navigation.ui.AppBarConfiguration;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class ClinicPage extends AppCompatActivity implements OnMapReadyCallback {
    BottomSheetBehavior bottomSheetBehavior;
    LinearLayout footer;
    TextView chosen_service;
    Button book_now;
    private AppBarConfiguration mAppBarConfiguration;
    ExpandableListAdapterClinicPage listAdapter;
    ExpandableListView expListView;
    ListView offers_list_view;
    ListView working_list_view;
    ListView rating_list_view;

    //  List<String> listDataHeader2;
    ArrayList<HashMap<String, String>> services_list;
    ArrayList<HashMap<String, String>> services_list_tmp;
    ArrayList<HashMap<String, String>> workingTimeList;
    ArrayList<HashMap<String, String>> ClinicRatingList;
    ArrayList<HashMap<String, String>> partClinicRatingList;

    ListView list;
    HashMap<String, List<String>> listDataChild;
    HashMap<String, String> ClinicData;
    // ArrayList<HashMap<String, String>> Headrs;
    ArrayList<String> exphead;
    ArrayList<HashMap<String, String>> offersList;
    private View mSelectedView; // the view has been selected
    private View mSelectedViewexp; // the view has been selected

    ArrayList<HashMap<String, String>> clinic_Services;
    ArrayList<HashMap<String, String>> Services_Laser;
    ArrayList<HashMap<String, String>> Services_Filler;
    ArrayList<HashMap<String, String>> Services_Botox;
    ArrayList<HashMap<String, String>> Services_Dental;
    ArrayList<HashMap<String, String>> Services_Skin;

    Intent intent_offer;
    private GoogleMap mMaps;

    ArrayList<ArrayList<HashMap<String, String>>> servicesOfclinic;
    private static final String TAG = HttpHandlerPostToken.class.getSimpleName();

    final private String base_url = "https://laserbookingonline.com/manager/APIs/clientV2/";

    String lat = "";
    String lng = "";
    String token;
    ProgressDialog pd;
    Double clinicLat = 0.0;
    Double clinicLng = 0.0;
    boolean isguest = false;
    LinearLayout profile_side;
    LinearLayout rating_side;
    LinearLayout appointment_side;
    LinearLayout group_login_real;
    ScrollView mScrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadlocal();
        bottomSheetBehavior = new BottomSheetBehavior();
        footer = (LinearLayout) findViewById(R.id.bottom_sheet);
        book_now = (Button) findViewById(R.id.book_now);
        chosen_service = (TextView) findViewById(R.id.chosen_service);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // list=(ListView)findViewById(R.id.offers_list);
        expListView = (ExpandableListView) findViewById(R.id.exp_services);
        offers_list_view = (ListView) findViewById(R.id.offers_list);
        working_list_view = (ListView) findViewById(R.id.working_view_list);
        rating_list_view = (ListView) findViewById(R.id.rating_view_list);
        services_list = new ArrayList<>();
        ClinicData = new HashMap<String, String>();
        clinic_Services = new ArrayList<>();
        workingTimeList = new ArrayList<>();
        ClinicRatingList = new ArrayList<>();
        partClinicRatingList = new ArrayList<>();
        //   Headrs=new ArrayList<>();
        offersList = new ArrayList<>();

        servicesOfclinic = new ArrayList<ArrayList<HashMap<String, String>>>();
        exphead = new ArrayList<>();
        Services_Laser = new ArrayList<>();
        Services_Filler = new ArrayList<>();
        Services_Botox = new ArrayList<>();
        Services_Dental = new ArrayList<>();
        Services_Skin = new ArrayList<>();
        // DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ClinicData = (HashMap<String, String>) getIntent().getExtras().getSerializable("Clinic");


        //    Headrs= (ArrayList<HashMap<String, String>>) getIntent().getExtras().getSerializable("Header");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //    String k= getIntent().getStringExtra("lat");
        //  Log.e("k= ","is "+k);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_burger_icon_bar);
        // preparing list data


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

        new ClinicDetails().execute();


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

    public void bookService(View view) {
        Log.e("before activity","this"+intent_offer.getSerializableExtra("offer").toString());

        startActivity(intent_offer);
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
        String lng=pref.getString("Mylang","");
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        if(lng.equals("ar")){
            pd.setMessage("جاري التحميل");

        }
        else{
            pd.setMessage("Loading");

        }

        pd.show();
      //  setLocale(lng);
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

    public void logout(View view) {
        SharedPreferences.Editor editor=getSharedPreferences("user", Activity.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        Intent i=new Intent(this,LoginActivity.class);
        startActivity(i);
        finishAffinity();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

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

    public class ClinicDetails extends AsyncTask<Void, Void, Void> {
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


//            "categoryId":"1", "lat": "", "lng": "","pager":"1"
//


        }

        @Override
        protected Void doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();

            HttpHandlerPostToken sh = new HttpHandlerPostToken();

            // Making a request to url and getting response
            String urlget = base_url + "clinicDetailsV2";
            String jsonStr = null;

            // String jsonStr = sh.makeServiceCall(urlget, token, "", "");
            // String response = null;
            try {
                URL url = new URL(urlget);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Client-Auth-Token", "Bearer"+" "+token);
                String offerid="";
                if(ClinicData.get("offerId")!=null)
                    offerid  =ClinicData.get("offerId");

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("clinicId",ClinicData.get("clinicId") );
                    jsonParam.put("offerId","");


                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    Log.e("Before sending", "1" + jsonParam.toString());
                    wr.writeBytes(jsonParam.toString());

                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                jsonStr = convertStreamToString(in);
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
            //   return response;
            Log.e("gg mynigga","yeaah");
            Log.e("is offers :", "Response from url countries: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject c = new JSONObject(jsonStr);

                    Log.e("gg mynigga","inside try now");
                    String clinicId = c.getString("clinicId");
                    String clinicName_en = c.getString("clinicName_en");
                    String clinicName_ar =c.getString("clinicName_ar");
                    String description_en = c.getString("description_en");
                    String description_ar = c.getString("description_ar");
                    String address_en =c.getString("address_en");
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
//                    String imageId = c.getString("imageId");
//                    String img = c.getString("img");
//                    String img_m = c.getString("img_m");
//                    String img_s = c.getString("img_s");
//                    String img_t = c.getString("img_t");
//                    String uuid = c.getString("uuid");
//                    String isDefault = c.getString("isDefault");
                    String cover = c.getString("cover");



                    HashMap<String, String> clinic_ob = new HashMap<>();

// adding each child node to HashMap key => value
                    clinic_ob.put("description_en", description_en);
                    clinic_ob.put("description_ar", description_ar);
                    clinic_ob.put("address_en", address_en);
                    clinic_ob.put("address_ar", address_ar);
                    clinic_ob.put("lat", lat_ob);
                    clinic_ob.put("lng", lng_ob);
                    clinic_ob.put("IBAN", IBAN);
                    clinic_ob.put("phone", phone);
                    clinic_ob.put("clinicMobile", clinicMobile);
                    clinic_ob.put("clinicEmail", clinicEmail);
                    clinic_ob.put("logo", logo);
                    clinic_ob.put("locked_period", locked_period);
//                    clinic_ob.put("img", img);
//                    clinic_ob.put("img_m", img_m);
//                    clinic_ob.put("img_s", img_s);
//                    clinic_ob.put("img_t", img_t);
//                    clinic_ob.put("imageId", imageId);
                    clinic_ob.put("active", active);
                    clinic_ob.put("deleted", deleted);
                    clinic_ob.put("created_at", created_at);
                    clinic_ob.put("last_modify", last_modify);
//                    clinic_ob.put("uuid", uuid);
//                    clinic_ob.put("isDefault", isDefault);
                    clinic_ob.put("clinicId", clinicId);
                    clinic_ob.put("cover", cover);
                    clinic_ob.put("clinicName_en", clinicName_en);
                    clinic_ob.put("clinicName_ar", clinicName_ar);

                    ClinicData=clinic_ob;

                        JSONArray servicesArray= c.getJSONArray("services");

                        Log.e("servicesArray length","is : "+servicesArray.length());

                   //     listDataHeader.add(clinic_ob);
                        for (int k = 0; k < servicesArray.length(); k++) {
                            Log.e("servicesArray length","is : "+servicesArray.length());
                            JSONObject service = servicesArray.getJSONObject(k);
                            String serviceId=service.getString("serviceId");
                            String serviceSeed=service.getString("serviceSeed");
                            String categoryId=service.getString("categoryId");
                            String clinicId_service=service.getString("clinicId");
                            String serviceName_en=service.getString("serviceName_en");
                            String serviceName_ar=service.getString("serviceName_ar");
                            String serviceDescription_en=service.getString("serviceDescription_en");
                            String serviceDescription_ar=service.getString("serviceDescription_ar");
                            String cost=service.getString("cost");
                            String duration=service.getString("duration");
                            String paymentTypes=service.getString("paymentTypes");
                            String active_service=service.getString("active");
                            String deleted_service=service.getString("deleted");
                            String created_at_service=service.getString("created_at");
                            String last_modify_service=service.getString("last_modify");
                            String categoryName_en=service.getString("categoryName_en");
                            String categoryName_ar=service.getString("categoryName_ar");
                           String offerId=service.getString("offerId");

                            String postCost=service.getString("postCost");


                            HashMap<String, String> service_data = new HashMap<>();

                            service_data.put("serviceId",serviceId);
                            service_data.put("serviceSeed",serviceSeed);
                            service_data.put("categoryId",categoryId);
                            service_data.put("clinicId",clinicId_service);
                            service_data.put("serviceName_en",serviceName_en);
                            service_data.put("serviceName_ar",serviceName_ar);
                            service_data.put("serviceDescription_en",serviceDescription_en);
                            service_data.put("serviceDescription_ar",serviceDescription_ar);
                            service_data.put("cost",cost);
                            service_data.put("duration",duration);
                            service_data.put("paymentTypes",paymentTypes);
                            service_data.put("active",active_service);
                            service_data.put("deleted",deleted_service);
                            service_data.put("created_at",created_at_service);
                            service_data.put("last_modify",last_modify_service);
                            service_data.put("categoryName_en",categoryName_en);
                            service_data.put("categoryName_ar",categoryName_ar);
                            service_data.put("offerId",offerId);
                            Log.e("offerId value","is  "+offerId);
                            if(offerId!=null&&!offerId.equals("null")&&!offerId.equals("")){
                                String sessionId=service.getString("sessionId");
                                String offer_end=service.getString("offer_end");
                                String offer_start=service.getString("offer_start");
                                String offerPaymentTypes=service.getString("offerPaymentTypes");
                                String discount=service.getString("discount");
                                service_data.put("sessionId",sessionId);
                                service_data.put("offer_end",offer_end);
                                service_data.put("offer_start",offer_start);
                                service_data.put("offerPaymentTypes",offerPaymentTypes);
                                service_data.put("discount",discount);
                            }
                            service_data.put("postCost",postCost);
                            if(offerId!=null&&!offerId.equals("null")&&!offerId.equals("")){
                                offersList.add(service_data);
                            }
                            else
                                services_list.add(service_data);

//                            if(i+1== servicesArray.length()-1){
//                                servicesOfclinic.add(services_list);
//                                services_list.clear();
//                            }
//                            if( k==servicesArray.length()-1){
//                                //   services_list_tmp.clear();
//                                //     services_list_tmp=services_list;
//                                servicesOfclinic.add(new ArrayList<HashMap<String, String>>(services_list));
//                                Log.e("worked","worked here");
//
//
//
//                            }

                        }
                    JSONArray workingtimes= c.getJSONArray("workingtimes");

                    Log.e("workingtimes length","is : "+workingtimes.toString());

                    //     listDataHeader.add(clinic_ob);
                    for (int w = 0; w < workingtimes.length(); w++) {
                        JSONObject workTime = workingtimes.getJSONObject(w);
                        JSONObject  times_en= workTime.getJSONObject("times_en");
                        String day_en=times_en.getString("day");
                        String time_en=times_en.getString("time");

                        JSONObject  times_ar= workTime.getJSONObject("times_ar");
                        String day_ar=times_ar.getString("day");
                        String time_ar=times_ar.getString("time");



                        HashMap<String, String> workinTimeData = new HashMap<>();

                        workinTimeData.put("day_en",day_en);
                        workinTimeData.put("time_en",time_en);
                        workinTimeData.put("day_ar",day_ar);
                        workinTimeData.put("time_ar",time_ar);

                            workingTimeList.add(workinTimeData);


                    }
                    JSONArray feedbacks= c.getJSONArray("feedbacks");

                    Log.e("workingtimes length","is : "+workingtimes.toString());

                    //     listDataHeader.add(clinic_ob);
                    for (int w = 0; w < workingtimes.length(); w++) {
                        JSONObject feedback = feedbacks.getJSONObject(w);
                        String  feedbackId= feedback.getString("feedbackId");
                        String clientName=feedback.getString("clientName");
                        String mobile=feedback.getString("mobile");
                        String  score= feedback.getString("score");
                        String comment=feedback.getString("comment");
                        String hasComment=feedback.getString("hasComment");




                        HashMap<String, String> feedbackData = new HashMap<>();

                        feedbackData.put("feedbackId",feedbackId);
                        feedbackData.put("clientName",clientName);
                        feedbackData.put("mobile",mobile);
                        feedbackData.put("score",score);
                        feedbackData.put("comment",comment);
                        feedbackData.put("hasComment",hasComment);

                        ClinicRatingList.add(feedbackData);
                        if(w<3)
                        partClinicRatingList.add(feedbackData);


                    }
                    //c.get("overallScore");

                    //    servicesOfclinic.add(new ArrayList<HashMap<String, String>>(services_list));
                   //     Log.e("data of service list","is "+services_list.size());
                 //       services_list=new ArrayList<HashMap<String, String>>();
                        //     services_list.clear();

                        //         Log.e("nery list is :",""+Nearby_list.toString());

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
            if(ClinicData!=null&&!ClinicData.isEmpty()){
            SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
            String lng = pref.getString("Mylang", "");
            Log.e("language", lng);
            Log.e("workingtimes length","is : "+workingTimeList.toString());
            Log.e("feed back lsit is","this "+ClinicRatingList.toString());
            Log.e("array of services","size is "+services_list.size());
            Log.e("array of services","size is "+services_list.toString());
            ImageView img=(ImageView)findViewById(R.id.imageView2);
            TextView decribtion=(TextView) findViewById(R.id.decribtion);
            Log.e("cover url","is "+ClinicData.get("cover"));
            Picasso.get().load(ClinicData.get("cover")).fit().into(img);
            if(ClinicData.get("lat")!=null){
            clinicLat=Double.parseDouble(ClinicData.get("lat"));
            clinicLng= Double.parseDouble(ClinicData.get("lng"));
            }
            if (mMaps == null) {
                SupportMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap)
                    {
                        mMaps = googleMap;
                        mMaps.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mMaps.getUiSettings().setZoomControlsEnabled(true);
                        // Add a marker in Sydney and move the camera
                        LatLng sydney = new LatLng(clinicLat, clinicLng);
                        SharedPreferences pref=getSharedPreferences("Settings",Activity.MODE_PRIVATE);
                        String lng=pref.getString("Mylang","");
                        mMaps.addMarker(new MarkerOptions().position(sydney).title(ClinicData.get("clinicName_"+lng))).showInfoWindow();
                        mMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,12.0f ));

                      //  mMaps.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        mScrollView = findViewById(R.id.activity_expandable_scroll_view); //parent scrollview in xml, give your scrollview id value
                        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2))
                                .setListener(new WorkaroundMapFragment.OnTouchListener() {
                                    @Override
                                    public void onTouch()
                                    {
                                        mScrollView.requestDisallowInterceptTouchEvent(true);
                                    }
                                });
                    }
                });
            }
            if(lng.equals("ar"))
                decribtion.setText(ClinicData.get("description_ar"));

            else
            decribtion.setText(ClinicData.get("description_en"));
//            Log.e("array of services","size is "+services_list.toString());
//            Log.e("array of services 4","size is "+services_list.get(4).toString());
//            for(int g=0;g<listDataHeader.size();g++) {
//              String id=  listDataHeader.get(g).get("clinicName_en");
//          Log.e("data is :","is : "+id);
//          for(int z=0;z<services_list.size();z++){
//
//              clinic_Services.add(services_list.get(g));
//          }
//                    //found it!
//
//
//            }
            //    Log.e("array of services of 1","is :"+clinic_Services.size());
//            list = (ListView) findViewById(R.id.list);
//
//            //set the adapter of CustomList Adapter
//            CustomListAdapter_NearBy adapter = new CustomListAdapter_NearBy(
//                    getApplicationContext(), R.layout.list_item_clinics_nearby, Nearby_list
//            );
//
//            list.setAdapter(adapter);
//
//            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    HashMap<String, String> offers = (HashMap<String, String>) parent.getAdapter().getItem(position);
////                    Intent intent = new Intent(Statistics_Activity.this, Stat_month.class);
////                    intent.putExtra("id", userid);
////                    intent.putExtra("year", year_forms_this);
////                    startActivity(intent);
//
//                    //here i want to get the items
//                }
//            });

       //     listAdapter = new ExpandableListAdapter(ClinicPage.this, listDataHeader, servicesOfclinic);

            CustomListAdapter_clinicPage_offerlist adapter = new CustomListAdapter_clinicPage_offerlist(
                    getApplicationContext(), R.layout.clinic_page_item_list_offers, offersList,lng
            );

            offers_list_view.setAdapter(adapter);
            int L=0,F=0,B=0,D=0,S=0;
            offers_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                  //  expListView.setSelector(android.R.color.transparent);
                  //  offers_list_view.setSelector(R.drawable.rounded_list_item_clinic_page);

                    HashMap<String, String> offers = (HashMap<String, String>) parent.getAdapter().getItem(position);
                    intent_offer = new Intent(ClinicPage.this, DatePickerPage.class);
                    Log.e("offer is :","this"+offers.toString());
                    intent_offer.putExtra("offer", offers);
                    intent_offer.putExtra("clinic",ClinicData);
                    //   RelativeLayout footer=(RelativeLayout)findViewById(R.id.footer);
                   // footer.setVisibility(View.VISIBLE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    if(lng.equals("ar"))
                        chosen_service.setText(offers.get("postCost")+"ريال ");

                    else
                    chosen_service.setText(offers.get("postCost")+" Riyal");
                    footer.setVisibility(View.GONE);
                    footer.setVisibility(View.VISIBLE);

                //    startActivity(intent);
                    Log.e("offer postion ","is: "+position);

                    Log.e("data is ","that offer: "+offers.toString());
                    //here i want to get the items
                }
            });
            setListViewHeightBasedOnChildren(offers_list_view);


            CustomListAdapter_clinicPage_worktimes adapter_workng = new CustomListAdapter_clinicPage_worktimes(
                    getApplicationContext(), R.layout.clinic_page_working_time, workingTimeList,lng
            );

            working_list_view.setAdapter(adapter_workng);
            setListViewHeightBasedOnChildren(working_list_view);
            Log.e("list entering adapter","is this "+partClinicRatingList.toString());
            CustomListAdapter_clinicPage_ratings adapter_rating = new CustomListAdapter_clinicPage_ratings(
                    getApplicationContext(), R.layout.list_item_rating_clinic_page, partClinicRatingList,lng
            );
            rating_list_view.setAdapter(adapter_rating);
            setListViewHeightBasedOnChildren(rating_list_view);

            if(lng.equals("ar")){
                for (int j=0;j<services_list.size();j++){
                    String category=services_list.get(j).get("categoryName_ar");
                    if( category.equals("ليزر")){
                        Services_Laser.add(services_list.get(j));
                        L++;
                    }
                    if( category.equals("فيلر")){
                        Services_Filler.add(services_list.get(j));
                        F++;
                    }
                    if( category.equals("بوتوكس")){
                        Services_Botox.add(services_list.get(j));
                        B++;
                    }
                    if( category.equals("اسنان")){
                        Services_Dental.add(services_list.get(j));
                        D++;
                    }
                    if( category.equals("بشرة")){
                        Services_Skin.add(services_list.get(j));
                        S++;

                    }

                }
                if(L>0)
                    exphead.add("ليزر");
                if(F>0)
                    exphead.add("فيلر");
                if(B>0)
                    exphead.add("بوتوكس");
                if(D>0)
                    exphead.add("اسنان");
                if(S>0)
                    exphead.add("بشرة");
            }
            else
            {
                for (int j=0;j<services_list.size();j++){
                    String category=services_list.get(j).get("categoryName_en");
                    if( category.equals("LASER")){
                        Services_Laser.add(services_list.get(j));
                        L++;
                    }
                    if( category.equals("Filler")){
                        Services_Filler.add(services_list.get(j));
                        F++;
                    }
                    if( category.equals("Botox")){
                        Services_Botox.add(services_list.get(j));
                        B++;
                    }
                    if( category.equals("Dental")){
                        Services_Dental.add(services_list.get(j));
                        D++;
                    }
                    if( category.equals("Skin")){
                        Services_Skin.add(services_list.get(j));
                        S++;
                    }
                }
                if(L>0)
                    exphead.add("LASER");
                if(F>0)
                    exphead.add("Filler");
                if(B>0)
                    exphead.add("Botox");
                if(D>0)
                    exphead.add("Dental");
                if(S>0)
                    exphead.add("Skin");

            }



            servicesOfclinic.add(Services_Laser);
            servicesOfclinic.add(Services_Filler);
            servicesOfclinic.add(Services_Botox);
            servicesOfclinic.add(Services_Dental);
            servicesOfclinic.add(Services_Skin);

        listAdapter = new ExpandableListAdapterClinicPage(ClinicPage.this, exphead, servicesOfclinic,lng);
            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {
//                    offers_list_view.setSelector(android.R.color.transparent);



//                    expListView.setSelector(R.drawable.rounded_list_item_clinic_page);
                    /* You must make use of the View v, find the view by id and extract the text as below*/
                    intent_offer = new Intent(ClinicPage.this, DatePickerPage.class);
                    intent_offer.putExtra("offer", servicesOfclinic.get(groupPosition).get(childPosition));
                    intent_offer.putExtra("clinic",ClinicData);
                    //   RelativeLayout footer=(RelativeLayout)findViewById(R.id.footer);
                    // footer.setVisibility(View.VISIBLE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    if(lng.equals("ar"))
                        chosen_service.setText(servicesOfclinic.get(groupPosition).get(childPosition).get("postCost")+"ريال ");

                    else
                        chosen_service.setText(servicesOfclinic.get(groupPosition).get(childPosition).get("postCost")+" Riyal");
                    footer.setVisibility(View.GONE);
                    footer.setVisibility(View.VISIBLE);
//                    Intent intents= new Intent(ClinicPage.this,DatePickerPage.class);
//                    intents.putExtra("Clinic", ClinicData);
                    //listDataHeader.get(groupPosition);
                    //   servicesOfclinic.get(groupPosition);
                    Log.e("clicked","here "+groupPosition+" "+childPosition);
                    Log.e("clicked","here "+servicesOfclinic.get(groupPosition).get(childPosition).toString());


                    return true;  // i missed this
                }
            });
            expListView.setAdapter(listAdapter);
            for(int i=0; i < listAdapter.getGroupCount(); i++)
                expListView.expandGroup(i);
            }
            else
                Log.e("empty "," result");

            pd.dismiss();
        }

    }
    public  void  allRatingsPage(View view){
        Intent intent=new Intent(ClinicPage.this,ClinicPageRatingList.class);
        intent.putExtra("ratinglist",ClinicRatingList);
        startActivity(intent);

    }
    public void show_Direction(View view){
        SharedPreferences prefc = getSharedPreferences("loc", Activity.MODE_PRIVATE);
        lat = prefc.getString("lat", "");
        lng = prefc.getString("lng", "");
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr="+clinicLat+","+clinicLng));
        startActivity(intent);
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
    public static void setListViewHeightBasedOnChildren(ListView listView) {
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
    }
}



