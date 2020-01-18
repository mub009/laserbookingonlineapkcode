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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;


import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


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

public class Clinics_List_Laser extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    TextView category_name;
    ImageView category_image;

    //  List<String> listDataHeader2;
    ArrayList<HashMap<String, String>> services_list;
    ArrayList<HashMap<String, String>> services_list_tmp;
    HashMap<String, String> category;

    HashMap<String, List<String>> listDataChild;
    ArrayList<HashMap<String, String>> listDataHeader;
    ArrayList<HashMap<String, String>> clinic_Services;
    ArrayList<ArrayList<HashMap<String, String>>> servicesOfclinic;
    private static final String TAG = HttpHandlerPostToken.class.getSimpleName();

    final private String base_url = "https://laserbookingonline.com/manager/APIs/clientV2/";

    String lat = "";
    String lng = "";
    String token;
    ProgressDialog pd;

    boolean isguest = false;
    LinearLayout profile_side;
    LinearLayout rating_side;
    LinearLayout appointment_side;
    LinearLayout group_login_real;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinics__list__laser);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadlocal();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        services_list = new ArrayList<>();
        listDataHeader = new ArrayList<>();
        clinic_Services = new ArrayList<>();
        servicesOfclinic = new ArrayList<>();
        services_list_tmp = new ArrayList<>();
        // DrawerLayout drawer = findViewById(R.id.drawer_layout);
        category = new HashMap<String, String>();

        category = (HashMap<String, String>) getIntent().getSerializableExtra("category");
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

        category_name = (TextView) findViewById(R.id.Category_name);

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
        SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lng = pref.getString("Mylang", "");
        Log.e("language", lng);
        if (lng.equals("ar"))
            category_name.setText(category.get("categoryName_ar"));
        else
            category_name.setText(category.get("categoryName_en"));

        String img_string = category.get("fullimg");

        category_image = (ImageView) findViewById(R.id.Category_image);
        Picasso.get().load(img_string).into(category_image);

        // listAdapter = new ExpandableListAdapter(this, listDataHeader, servicesOfclinic);
        new ClinicsAndServices().execute();
        Log.e("list of services:", "is " + services_list.size());


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
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
        System.exit(0);
        overridePendingTransition(0, 0);

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
    public void setLocale(String lang) {
        // get language return 2 first letters lower case of the language //// in the other hand get display language return the name of the language
        String CurrentLang = Locale.getDefault().getDisplayLanguage();
        String Applang= Resources.getSystem().getConfiguration().locale.getDisplayLanguage();




        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        conf.setLocale(new Locale(lang));
        res.updateConfiguration(conf, dm);
        SharedPreferences.Editor editor=getSharedPreferences("Settings", Activity.MODE_PRIVATE).edit();
        editor.putString("Mylang",lang);
        editor.apply();
        if(!CurrentLang.equals(Applang)){
            recreate(); }



    }
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
     //   setLocale(lng);
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

    public class ClinicsAndServices extends AsyncTask<Void, Void, Void> {
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
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Log.e("no gps","yerp");

            }else{
                SharedPreferences prefc = getSharedPreferences("loc", Activity.MODE_PRIVATE);
                lat = prefc.getString("lat", "");
                lng = prefc.getString("lng", "");

                Log.e("lat nearby",""+lat);
                Log.e("lng nearby",""+lng);
            }

        }

        @Override
        protected Void doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();

            HttpHandlerPostToken sh = new HttpHandlerPostToken();

            // Making a request to url and getting response
            String urlget = base_url + "categoryDetailsExtra";
            String jsonStr = null;

            // String jsonStr = sh.makeServiceCall(urlget, token, "", "");
            // String response = null;
            try {
                URL url = new URL(urlget);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Client-Auth-Token", "Bearer"+" "+token);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("categoryId",category.get("categoryId") );
                    jsonParam.put("lat",lat );
                    jsonParam.put("lng",lng );
               //     jsonParam.put("pager","0" );


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
            Log.e("is offers :", "Response from url countries: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONArray jsonarray = new JSONArray(jsonStr);

                    for (int i = 0; i <= jsonarray.length(); i++) {
                        JSONObject c = jsonarray.getJSONObject(i);
                        JSONObject clinic= c.getJSONObject("clinic");
                        JSONArray servicesArray= c.getJSONArray("services");
                        String clinicId = clinic.getString("clinicId");
                        String clinicName_en = clinic.getString("clinicName_en");
                        String clinicName_ar =clinic.getString("clinicName_ar");
                        String description_en = clinic.getString("description_en");
                        String description_ar = clinic.getString("description_ar");
                        String address_en =clinic.getString("address_en");
                        String address_ar = clinic.getString("address_ar");
                        String lat_ob = clinic.getString("lat");
                        String lng_ob = clinic.getString("lng");
                        String IBAN = clinic.getString("IBAN");
                        String phone = clinic.getString("phone");
                        String clinicMobile = clinic.getString("clinicMobile");
                        String clinicEmail = clinic.getString("clinicEmail");
                        String logo = clinic.getString("logo");
                        String locked_period = clinic.getString("locked_period");
                        String active = clinic.getString("active");
                        String deleted = clinic.getString("deleted");
                        String created_at = clinic.getString("created_at");
                        String last_modify = clinic.getString("last_modify");
                        String imageId = clinic.getString("imageId");
                        String img = clinic.getString("img");
                        String img_m = clinic.getString("img_m");
                        String img_s = clinic.getString("img_s");
                        String img_t = clinic.getString("img_t");
                        String uuid = clinic.getString("uuid");
                        String isDefault = clinic.getString("isDefault");
                        String cover = clinic.getString("cover");



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
                        clinic_ob.put("img", img);
                        clinic_ob.put("img_m", img_m);
                        clinic_ob.put("img_s", img_s);
                        clinic_ob.put("img_t", img_t);
                        clinic_ob.put("imageId", imageId);
                        clinic_ob.put("active", active);
                        clinic_ob.put("deleted", deleted);
                        clinic_ob.put("created_at", created_at);
                        clinic_ob.put("last_modify", last_modify);
                        clinic_ob.put("uuid", uuid);
                        clinic_ob.put("isDefault", isDefault);
                        clinic_ob.put("clinicId", clinicId);
                        clinic_ob.put("cover", cover);
                        clinic_ob.put("clinicName_en", clinicName_en);
                        clinic_ob.put("clinicName_ar", clinicName_ar);

                        if (!lat .equals("")  && !lng.equals("") ) {
                            String distance = clinic.getString("distance");
                            clinic_ob.put("distance", distance);

                        }
                        listDataHeader.add(clinic_ob);
                        for (int k = 0; k < servicesArray.length(); k++) {
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
                            String offer_end=service.getString("offer_end");
                            String discount=service.getString("discount");
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
                            service_data.put("offer_end",offer_end);
                            service_data.put("discount",discount);
                            service_data.put("postCost",postCost);
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
                        servicesOfclinic.add(new ArrayList<HashMap<String, String>>(services_list));
                        Log.e("data of service list","is "+services_list.size());
                        services_list=new ArrayList<HashMap<String, String>>();
                        //     services_list.clear();

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
//            Log.e("array of clinics", "size is ; " + services_list.toString());
//            Log.e("array of services", "size is ; " + services_list.size());
//            Log.e("array of clinics", "size is ; " + servicesOfclinic.size());
//            Log.e("array of clinics", "size is ; " + servicesOfclinic.toString());
//            Log.e("array of clinics", "size is ; " + servicesOfclinic.get(0).toString());
//            Log.e("array of service si", "size is ; " + servicesOfclinic.get(0).size());
//            Log.e("array of service si", "size is ; " + servicesOfclinic.get(2).size());


            listAdapter = new ExpandableListAdapter(Clinics_List_Laser.this, listDataHeader, servicesOfclinic,lng);

            expListView.setAdapter(listAdapter);
            for(int i=0; i < listAdapter.getGroupCount(); i++)
                expListView.expandGroup(i);


//            expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//                @Override
//                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                    Intent intent= new Intent(Clinics_List_Laser.this,ClinicPage.class);
//                    intent.putExtra("Header", listDataHeader.get(groupPosition));
//                    intent.putExtra("child", servicesOfclinic.get(groupPosition));
//                    startActivity(intent);
//
//                    return false;
//                }
//            });
            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {

                    /* You must make use of the View v, find the view by id and extract the text as below*/
                    Intent intent= new Intent(Clinics_List_Laser.this,ClinicPage.class);
                    intent.putExtra("Clinic", listDataHeader.get(groupPosition));
                    intent.putExtra("Header", listDataHeader);
                    intent.putExtra("children", servicesOfclinic.get(groupPosition));
                    startActivity(intent);
                    //listDataHeader.get(groupPosition);
                    //   servicesOfclinic.get(groupPosition);

                    return true;  // i missed this
                }
            });
            Runnable progressRunnable = new Runnable() {

                @Override
                public void run() {
                    pd.cancel();
                }
            };

            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 3500);

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



