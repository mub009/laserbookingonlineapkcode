package com.mohtaref.clinics;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


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
import java.util.Locale;

public class Profile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    String region_string = "";
    String city_string = "";
    String area_string = "";
    TextView errorTextN;
    public ArrayList<HashMap> registerData;
    public ArrayList<HashMap> CityData;
    public ArrayList<HashMap> DistrictsData;
    public ArrayList<String> Regions_en;
    public ArrayList<String> Regions_ar;
    public ArrayList<String> Cities_en;
    public ArrayList<String> Cities_ar;
    public ArrayList<String> Districts_en;
    public ArrayList<String> Districts_ar;
    final private String url_base = "https://laserbookingonline.com/manager/APIs/clientV2/";
    private Register mAuthTask = null;
    public String region = "";
    public String city = "";
    public String area = "";
    Button Save_btn;
    TextView mobile;
    EditText name;
    EditText Password;
    EditText RePassword;
    boolean name_check = false;
    boolean pass_check = false;
    boolean repass_check = false;
    boolean mobile_check = false;
    boolean Reigon_check = false;
    boolean distract_check = true;
    boolean city_check = false;
    String district_id = "0";
    String city_id = "0";
    int city_pos;
    String user_mobile;
    String user_name;
    String token;
    boolean second_time = false;
    HashMap<String, String> usergeo;
    Button g;
    String clientName;
    public ProgressDialog pd;

    boolean isguest = false;
    LinearLayout profile_side;
    LinearLayout rating_side;
    LinearLayout appointment_side;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usergeo = new HashMap<String, String>();
        registerData = new ArrayList<HashMap>();
        CityData = new ArrayList<HashMap>();
        DistrictsData = new ArrayList<HashMap>();
        Regions_en = new ArrayList<String>();
        Regions_ar = new ArrayList<String>();
        Cities_en = new ArrayList<String>();
        Cities_ar = new ArrayList<String>();
        Districts_en = new ArrayList<String>();
        Districts_ar = new ArrayList<String>();
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mobile = (TextView) findViewById(R.id.phone_prof);
        name = (EditText) findViewById(R.id.profile_name);
        Save_btn = (Button) findViewById(R.id.save_btn);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_burger_icon_bar);
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        loadlocal();
        SharedPreferences prefg = getSharedPreferences("user", Activity.MODE_PRIVATE);
        String data = prefg.getString("data", "");
        try {
            JSONObject userdata = new JSONObject(data);
            isguest = userdata.getBoolean("isGuest");
            user_name = userdata.getString("clientName");
            user_mobile = userdata.getString("mobile");

            //  second_time=false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("user is guest :", "" + isguest);


        if (isguest) {
            profile_side = (LinearLayout) findViewById(R.id.group_login);
            rating_side = (LinearLayout) findViewById(R.id.group_Rating);
            appointment_side = (LinearLayout) findViewById(R.id.group_my_appointments);
            profile_side.setVisibility(View.GONE);
            rating_side.setVisibility(View.GONE);
            appointment_side.setVisibility(View.GONE);
            Spinner regspen = (Spinner) findViewById(R.id.region_prof);
            Spinner cspen = (Spinner) findViewById(R.id.city_prof);
            Spinner aspen = (Spinner) findViewById(R.id.area_prof);
            View v4 = (View) findViewById(R.id.view4);
            View v5 = (View) findViewById(R.id.view5);
            View v6 = (View) findViewById(R.id.view6);
            View v7 = (View) findViewById(R.id.view7);
            Save_btn.setVisibility(View.GONE);
            regspen.setVisibility(View.GONE);
            cspen.setVisibility(View.GONE);
            aspen.setVisibility(View.GONE);
            v4.setVisibility(View.GONE);
            v5.setVisibility(View.GONE);
            v6.setVisibility(View.GONE);
            v7.setVisibility(View.GONE);
            name.setText(user_name);
            mobile.setText(user_mobile);
            name.setEnabled(false);
            name.setKeyListener(null);
        } else {
//        Save_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//          //    Register_btn_disabler();
//                Save_btn.setEnabled(true);
//                Log.e("button clicked"," yep"+usergeo.toString());
//              //
//                //  Save_btn.setEnabled(false);
//            }
//        });
            name.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    Register_btn_enabler();
                }

            });

            SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
            String lng = pref.getString("Mylang", "");
            Log.e("language", lng);
            if (lng.equals("ar")) {
                final Spinner spinner_region = (Spinner) findViewById(R.id.region_prof);

                final ArrayAdapter<String> adapter_region = new ArrayAdapter<>(
                        getBaseContext(),
                        R.layout.spinner_ar,
                        Regions_ar
                );
                adapter_region.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);


                final Spinner spinner_city = (Spinner) findViewById(R.id.city_prof);
                final ArrayAdapter<String> adapter_city = new ArrayAdapter<>(
                        getBaseContext(),
                        R.layout.spinner_ar,
                        Cities_ar
                );
                adapter_city.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

                final Spinner spinner_area = (Spinner) findViewById(R.id.area_prof);
                final ArrayAdapter<String> adapter_area = new ArrayAdapter<>(
                        getBaseContext(),
                        R.layout.spinner_ar,
                        Districts_ar
                );
                adapter_area.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                adapter_area.insert("اختر حي", 0);
                spinner_area.setAdapter(adapter_area);


                spinner_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {


                        if (pos == 0) {
                            Reigon_check = false;
                            final Spinner spinner_area = (Spinner) findViewById(R.id.area_prof);
                            spinner_area.setSelection(0);
                            final Spinner spinner_city = (Spinner) findViewById(R.id.city_prof);
                            spinner_city.setSelection(0);
                            //     To reset a spinner to default value:


                        } else {


                            final Spinner spinner_area = (Spinner) findViewById(R.id.area_prof);
                            Register_btn_disabler();
                            final ArrayAdapter<String> adapter_area = new ArrayAdapter<>(
                                    Profile.this,
                                    R.layout.spinner_ar,
                                    Districts_ar
                            );
                            adapter_area.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                            adapter_area.clear();
                            adapter_area.insert("اختر المدينة", 0);
                            spinner_area.setAdapter(adapter_area);
                            distract_check = false;
                            new PostCities().execute();

                            // Either gone or invisible
                        }


                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        Reigon_check = true;
                        region_string = "اختر المنطقة";
                        errorTextN = (TextView) parent.getSelectedView();

                    }

                });


                //City Item listener
                spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {

                        if (pos == 0) {

                            //     To reset a spinner to default value:
                            city_check = false;
                            spinner_city.setSelection(0);
                            final Spinner spinner_area = (Spinner) findViewById(R.id.area_prof);
                            spinner_area.setSelection(0);
                            spinner_area.setVisibility(View.GONE);
                            // area.setVisibility(View.GONE);

                        } else {
                            // Either gone or invisible

                            final Spinner spinner_area = (Spinner) findViewById(R.id.area_prof);
                            spinner_area.setSelection(0);
                            spinner_area.setVisibility(View.GONE);

                            Register_btn_disabler();
                            //   area.setVisibility(View.GONE);
                            city_pos = pos;
                            //  errorTextN = (TextView) parent.getSelectedView();
                            city_check = true;
                            distract_check = false;
                            adapter_area.clear();

                            new PostDistricts().execute();

                        }


                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        city_check = false;
                        region_string = "اختر المدينة";
                        errorTextN = (TextView) parent.getSelectedView();

                    }

                });

                //area Item listener
                spinner_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {


                        region_string = parent.getItemAtPosition(pos).toString();

                        Log.e("the id is heree boyyyy", district_id);
                        errorTextN = (TextView) parent.getSelectedView();
                        if (pos == 0) {
                            distract_check = false;
                            district_id = "0";
                            Log.e("out of index", "erroooooor");
                        } else {
                            district_id = DistrictsData.get(pos - 1).get("districtId").toString();
                            city_id = DistrictsData.get(pos - 1).get("cityId").toString();
                            Log.e("here", " dat is :" + district_id);
                            Reigon_check = true;
                            city_check = true;
                            distract_check = true;
                            Register_btn_enabler();

                        }

                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        distract_check = false;
                        region_string = "اختر حي";
                        errorTextN = (TextView) parent.getSelectedView();

                    }

                });

            } else {
                final Spinner spinner_region = (Spinner) findViewById(R.id.region_prof);

                final ArrayAdapter<String> adapter_region = new ArrayAdapter<>(
                        getBaseContext(),
                        R.layout.simple_spinner_item_fix,
                        Regions_en
                );
                adapter_region.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);


                final Spinner spinner_city = (Spinner) findViewById(R.id.city_prof);
                final ArrayAdapter<String> adapter_city = new ArrayAdapter<>(
                        getBaseContext(),
                        R.layout.simple_spinner_item_fix,
                        Cities_en
                );
                adapter_city.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

                final Spinner spinner_area = (Spinner) findViewById(R.id.area_prof);
                final ArrayAdapter<String> adapter_area = new ArrayAdapter<>(
                        getBaseContext(),
                        R.layout.simple_spinner_item_fix,
                        Districts_en
                );
                adapter_area.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                adapter_area.insert("Select a district", 0);
                spinner_area.setAdapter(adapter_area);


                spinner_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {


                        if (pos == 0) {
                            Reigon_check = false;
                            final Spinner spinner_area = (Spinner) findViewById(R.id.area_prof);
                            spinner_area.setSelection(0);
                            final Spinner spinner_city = (Spinner) findViewById(R.id.city_prof);
                            spinner_city.setSelection(0);
                            //     To reset a spinner to default value:


                        } else {


                            final Spinner spinner_area = (Spinner) findViewById(R.id.area_prof);
                            Register_btn_disabler();
                            final ArrayAdapter<String> adapter_area = new ArrayAdapter<>(
                                    Profile.this,
                                    R.layout.simple_spinner_item_fix,
                                    Districts_en
                            );
                            adapter_area.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                            adapter_area.clear();
                            adapter_area.insert("Select a city", 0);
                            spinner_area.setAdapter(adapter_area);
                            distract_check = false;
                            new PostCities().execute();

                            // Either gone or invisible
                        }


                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        Reigon_check = true;
                        region_string = "اختر المنطقة";
                        errorTextN = (TextView) parent.getSelectedView();

                    }

                });


                //City Item listener
                spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {

                        if (pos == 0) {

                            //     To reset a spinner to default value:
                            city_check = false;
                            spinner_city.setSelection(0);
                            final Spinner spinner_area = (Spinner) findViewById(R.id.area_prof);
                            spinner_area.setSelection(0);
                            spinner_area.setVisibility(View.GONE);
                            // area.setVisibility(View.GONE);

                        } else {
                            // Either gone or invisible

                            final Spinner spinner_area = (Spinner) findViewById(R.id.area_prof);
                            spinner_area.setSelection(0);
                            spinner_area.setVisibility(View.GONE);

                            Register_btn_disabler();
                            //   area.setVisibility(View.GONE);
                            city_pos = pos;
                            //  errorTextN = (TextView) parent.getSelectedView();
                            city_check = true;
                            distract_check = false;
                            adapter_area.clear();

                            new PostDistricts().execute();

                        }


                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        city_check = false;
                        region_string = "Select a city";
                        errorTextN = (TextView) parent.getSelectedView();

                    }

                });

                //area Item listener
                spinner_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {


                        region_string = parent.getItemAtPosition(pos).toString();

                        Log.e("the id is heree boyyyy", district_id);
                        errorTextN = (TextView) parent.getSelectedView();
                        if (pos == 0) {
                            distract_check = false;
                            district_id = "0";
                            Log.e("out of index", "erroooooor");
                        } else {
                            district_id = DistrictsData.get(pos - 1).get("districtId").toString();
                            city_id = DistrictsData.get(pos - 1).get("cityId").toString();
                            Log.e("here", " dat is :" + district_id);
                            Reigon_check = true;
                            city_check = true;
                            distract_check = true;
                            Register_btn_enabler();

                        }

                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        distract_check = false;
                        region_string = "Select a distract";
                        errorTextN = (TextView) parent.getSelectedView();

                    }

                });
            }

            Save_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new savedata().execute();
                    Save_btn.setEnabled(false);
                }
            });


            new GetGeo().execute();
            //  new PostCities().execute();
            // new PostDistricts().execute();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void profiles(View view) {
        finish();
        startActivity(getIntent());

    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
    }

    public void Register_btn_enabler() {
        if (distract_check && second_time) {
            Save_btn.setEnabled(true);

        }
        second_time = true;


    }

    public void Register_btn_disabler() {
        Save_btn.setEnabled(false);


    }


    public void savedat_btn(View view) {
        Log.e("new Button clicked", " yep");

        g.setEnabled(false);
        g.setEnabled(true);
        Log.e("new Button clicked", " worked fine");

    }

    public void logout(View view) {
        SharedPreferences.Editor editor = getSharedPreferences("user", Activity.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        Intent i = new Intent(Profile.this, LoginActivity.class);
        startActivity(i);
        finishAffinity();
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
        finish();
        startActivity(getIntent());

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



    //get Regions request

    public class PostRegions extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            final Spinner spinner_region = (Spinner) findViewById(R.id.region_prof);
            final ArrayAdapter<String> adapter_region = new ArrayAdapter<>(
                    Profile.this,
                    R.layout.simple_spinner_item_fix,
                    Regions_en
            );
            adapter_region.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            adapter_region.clear();
            final Spinner spinner_city = (Spinner) findViewById(R.id.city_prof);

            final ArrayAdapter<String> adapter_city = new ArrayAdapter<>(
                    Profile.this,
                    R.layout.simple_spinner_item_fix,
                    Cities_en
            );
            adapter_city.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            adapter_city.clear();
            adapter_city.insert("Select a city",0);


            spinner_city.setAdapter(adapter_city);
            //  adapter.notifyDataSetChanged();
            //int spinnerPosition = adapter.getPosition("City");
          //  spinner_city.setSelection(0);

            final Spinner spinner_area = (Spinner) findViewById(R.id.area_prof);

            final ArrayAdapter<String> adapter_area = new ArrayAdapter<>(
                    Profile.this,
                    R.layout.simple_spinner_item_fix,
                    Districts_en
            );
            adapter_area.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            adapter_area.clear();
            adapter_area.insert("Select a district",0);


            spinner_city.setAdapter(adapter_area);


        }


        @Override
        protected Void doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();

            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String urlget = url_base+"regions";
            String jsonStr = sh.makeServiceCall(urlget);
            if(registerData.size()!=0)
                Regions_en.clear();
            if(registerData.size()!=0)
                Regions_ar.clear();
            if(registerData.size()!=0)
            registerData.clear();
            Log.e("", "Response from url countries: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONArray jsonarray = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject c = jsonarray.getJSONObject(i);
                        //  String id = c.getString("id");
                        String regionId=c.getString("regionId");
                        String regionName_en = c.getString("regionName_en");
                        String regionName_ar = c.getString("regionName_ar");
                        String deleted = c.getString("deleted");


                        HashMap<String, String> reigon = new HashMap<>();

                        // adding each child node to HashMap key => value
                        reigon.put("regionId", regionId);
                        reigon.put("regionName_en", regionName_en);
                        reigon.put("regionName_ar", regionName_ar);
                        reigon.put("deleted", deleted);

                        Regions_en.add(regionName_en);
                        Regions_ar.add(regionName_ar);
                        registerData.add(reigon);
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
            SharedPreferences pref=getSharedPreferences("Settings",Activity.MODE_PRIVATE);
            String lng=pref.getString("Mylang","");
            Log.e("language",lng);
            Log.e("f;aj;",Regions_en.toString());
            Log.e("f;aj;",Regions_ar.toString());

            if(lng.equals("ar")){

                final Spinner spinner_region = (Spinner) findViewById(R.id.region_prof);
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        Profile.this,
                        R.layout.simple_spinner_item_fix,
                        Regions_ar
                );
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                // adapter.clear();

                adapter.insert("اختر المنطقة",0);


                spinner_region.setAdapter(adapter);
                //  adapter.notifyDataSetChanged();
                //   int spinnerPosition = adapter.getPosition("region");
                spinner_region.setSelection(Regions_ar.indexOf(usergeo.get("regionName_ar")));
                // spinner_region.setBackgroundResource(arrow_left);



            }
            else{

                final Spinner spinner_region = (Spinner) findViewById(R.id.region_prof);
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        Profile.this,
                        R.layout.simple_spinner_item_fix,
                        Regions_en
                );
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
               // adapter.clear();

                adapter.insert("Select a region",0);


                spinner_region.setAdapter(adapter);
                //  adapter.notifyDataSetChanged();
                //   int spinnerPosition = adapter.getPosition("region");
                spinner_region.setSelection(Regions_en.indexOf(usergeo.get("regionName_en")));
            }
        }


    }
    //////////////////////////////////////////////////////////////////////////////////////
    public class PostCities extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Cities_en.clear();
            Cities_ar.clear();
            CityData.clear();
            final Spinner spinner_city = (Spinner) findViewById(R.id.city_prof);
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    Profile.this,
                    R.layout.simple_spinner_item_fix,
                    Cities_en
            );
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            adapter.clear();

            Log.e("cities","in now ");

        }

        @Override
        protected Void doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();
            final Spinner spinner_region = (Spinner) findViewById(R.id.region_prof);
            @SuppressLint("WrongThread") int reigon= spinner_region.getSelectedItemPosition();
            if(registerData.size()!=0)
            Log.e("regionsds"," is "+registerData);
            String id_region=registerData.get(reigon-1).get("regionId").toString();
            Log.e("check Thaat check dat: ",id_region);
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String urlget = url_base+"cities";
            URL url= null;

            String response = null;
            String jsonStr = null;
            try {
                url = new URL(urlget);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setDoInput(true);// here you are setting the `Content-Type` for the data you are sending which is `application/json`
                httpURLConnection.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("regionId",id_region);


                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                Log.e("Before sending", "1" + jsonParam.toString());
                wr.writeBytes(jsonParam.toString());
                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                response = convertStreamToString(in);
                jsonStr = response;


                wr.flush();
                wr.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.e("", "Response from url countries: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONArray jsonarray = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject c = jsonarray.getJSONObject(i);
                        //  String id = c.getString("id");
                        String cityId=c.getString("cityId");
                        String regionId=c.getString("regionId");
                        String cityName_en = c.getString("cityName_en");
                        String cityName_ar = c.getString("cityName_ar");
                        String deleted = c.getString("deleted");


                        HashMap<String, String> city = new HashMap<>();

                        // adding each child node to HashMap key => value
                        city.put("cityId", cityId);
                        city.put("regionId", regionId);
                        city.put("regionName_en", cityName_en);
                        city.put("regionName_ar", cityName_en);
                        city.put("deleted", deleted);

                        Cities_en.add(cityName_en);
                        Cities_ar.add(cityName_ar);
                        CityData.add(city);
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
            SharedPreferences pref=getSharedPreferences("Settings",Activity.MODE_PRIVATE);
            String lng=pref.getString("Mylang","");
            Log.e("language",lng);
            Log.e("f;aj;",Cities_en.toString());
            Log.e("f;aj;",Cities_ar.toString());


            if(lng.equals("ar")){

                final Spinner spinner_city = (Spinner) findViewById(R.id.city_prof);
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        Profile.this,
                        R.layout.simple_spinner_item_fix,
                        Cities_ar
                );
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                adapter.insert("اختر المدينة",0);


                spinner_city.setAdapter(adapter);
                //  adapter.notifyDataSetChanged();
                //int spinnerPosition = adapter.getPosition("City");
                spinner_city.setSelection(Cities_ar.indexOf(usergeo.get("cityName_ar")));



            }
            else{

                final Spinner spinner_city = (Spinner) findViewById(R.id.city_prof);
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        Profile.this,
                        R.layout.simple_spinner_item_fix,
                        Cities_en
                );
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                adapter.insert("Select a city",0);


                spinner_city.setAdapter(adapter);
                //  adapter.notifyDataSetChanged();
                //int spinnerPosition = adapter.getPosition("City");
                spinner_city.setSelection(Cities_en.indexOf(usergeo.get("cityName_en")));

            }
        }


    }

    //////////////////////////////////////////////////////////////////////////////////////////// PostDistricts
    public class PostDistricts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            Districts_en.clear();
            Districts_ar.clear();
            DistrictsData.clear();

            district_id="0";
            final Spinner spinner_area = (Spinner) findViewById(R.id.area_prof);
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    Profile.this,
                    R.layout.simple_spinner_item_fix,
                    Districts_en
            );
            adapter.clear();
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        }

        @Override
        protected Void doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();
        //    Log.e("before ","spinner");
           // final Spinner spinner_city = (Spinner) findViewById(R.id.city_prof);
            //int city_pos= spinner_city.getSelectedItemPosition();
          //  Log.e("after ","spinner");

            Log.e("check city pos dat: ","gg this :"+city_pos);

            String id_city=CityData.get(city_pos-1).get("cityId").toString();
            Log.e("check Thaat city dat: ",id_city);
       //     HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String urlget = url_base+"districts";
            URL url= null;

            String response = null;
            String jsonStr = null;
            try {
                url = new URL(urlget);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setDoInput(true);// here you are setting the `Content-Type` for the data you are sending which is `application/json`
                httpURLConnection.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("cityId",id_city );


                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                Log.e("Before sending", "1" + jsonParam.toString());
                wr.writeBytes(jsonParam.toString());
                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                response = convertStreamToString(in);
                jsonStr = response;


                wr.flush();
                wr.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.e("", "Response from url countries: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONArray jsonarray = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject c = jsonarray.getJSONObject(i);
                        //  String id = c.getString("id");
                        String districtId=c.getString("districtId");
                        String cityId=c.getString("cityId");
                        String districtName_en = c.getString("districtName_en");
                        String districtName_ar = c.getString("districtName_ar");
                        String deleted = c.getString("deleted");


                        HashMap<String, String> dis = new HashMap<>();

                        // adding each child node to HashMap key => value
                        dis.put("districtId", districtId);
                        dis.put("cityId", cityId);
                        dis.put("districtName_en", districtName_en);
                        dis.put("districtName_ar", districtName_ar);
                        dis.put("deleted", deleted);

                        Districts_en.add(districtName_en);
                        Districts_ar.add(districtName_ar);
                        DistrictsData.add(dis);
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

            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            SharedPreferences pref=getSharedPreferences("Settings",Activity.MODE_PRIVATE);
            String lng=pref.getString("Mylang","");
            Log.e("language",lng);
            Log.e("Districts_en: ",Districts_en.toString());
            Log.e("Districts_ar: ",Districts_ar.toString());
            if(lng.equals("ar")){

                final Spinner spinner_area = (Spinner) findViewById(R.id.area_prof);
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        Profile.this,
                        R.layout.simple_spinner_item_fix,
                        Districts_ar
                );
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                adapter.insert("اختر حي",0);


                spinner_area.setAdapter(adapter);
                //  adapter.notifyDataSetChanged();
                //int spinnerPosition = adapter.getPosition("City");
                Spinner spinne_cc=(Spinner)findViewById(R.id.city_prof);

                spinner_area.setSelection(Districts_ar.indexOf(usergeo.get("districtName_ar")));

                spinner_area.setVisibility(View.VISIBLE);


            }
            else{

                final Spinner spinner_area = (Spinner) findViewById(R.id.area_prof);
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        Profile.this,
                        R.layout.simple_spinner_item_fix,
                        Districts_en
                );
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                adapter.insert("Select a district",0);


                spinner_area.setAdapter(adapter);
                //  adapter.notifyDataSetChanged();
                //int spinnerPosition = adapter.getPosition("City");
                spinner_area.setSelection(Districts_en.indexOf(usergeo.get("districtName_en")));
                spinner_area.setVisibility(View.VISIBLE);

            }
            distract_check=false;

            Register_btn_disabler();
            pd.dismiss();

        }


    }


public class GetGeo extends AsyncTask<Void, Void, String> {
    boolean state = false;


    //0531042553/123456  phone + pass

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        SharedPreferences pref = getSharedPreferences("user", Activity.MODE_PRIVATE);
        String data = pref.getString("data", "");
        try {
            JSONObject userdata = new JSONObject(data);
            token = userdata.getString("token");
            user_name=userdata.getString("clientName");
            user_mobile=userdata.getString("mobile");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("user token is :", token);
        name.setText(user_name);
        mobile.setText(user_mobile);
        second_time=false;

    }


    @Override
    protected String doInBackground(Void... params) {

        String response = null;
        String jsonStr = null;
        try {
            URL url = new URL(url_base+"clientGeos"); //Enter URL here
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Client-Auth-Token", "Bearer"+" "+token);
            httpURLConnection.setDoInput(true);// here you are setting the `Content-Type` for the data you are sending which is `application/json`
            httpURLConnection.connect();

            JSONObject jsonParam = new JSONObject();



            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            Log.e("Before sending", "1" + jsonParam.toString());
            wr.writeBytes(jsonParam.toString());
            InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
            response = convertStreamToString(in);
            jsonStr = response;


            wr.flush();
            wr.close();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("right here ", "Response from url here:" + jsonStr);


        if (jsonStr != null) {
            try {
           JSONObject jsonob= new JSONObject(jsonStr);
           String districtId=jsonob.getString("districtId");
                String cityId=jsonob.getString("cityId");
                String districtName_en=jsonob.getString("districtName_en");
                String districtName_ar=jsonob.getString("districtName_ar");
                String deleted=jsonob.getString("deleted");
                String regionId=jsonob.getString("regionId");
                String cityName_en=jsonob.getString("cityName_en");
                String cityName_ar=jsonob.getString("cityName_ar");
                String regionName_en=jsonob.getString("regionName_en");
                String regionName_ar=jsonob.getString("regionName_ar");
            HashMap<String,String>geo=new HashMap<>();
                geo.put("districtId",districtId);
                geo.put("cityId",cityId);
                geo.put("districtName_en",districtName_en);
                geo.put("districtName_ar",districtName_ar);
                geo.put("deleted",deleted);

                geo.put("regionId",regionId);
                geo.put("cityName_en",cityName_en);
                geo.put("cityName_ar",cityName_ar);
                geo.put("regionName_en",regionName_en);
                geo.put("regionName_ar",regionName_ar);
                usergeo=geo;

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


        return response;
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.e("data is here","data: "+result);
        String k=usergeo.get("districtName_ar").toString();
        String a= usergeo.toString();
        Log.e("data is here","data k: "+k);
        Log.e("data is here","data: "+a);
        second_time=false;
        new PostRegions().execute();

    }


}

    public class savedata extends AsyncTask<Void, Void, String> {
        boolean state = false;


        //0531042553/123456  phone + pass

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPreferences pref = getSharedPreferences("user", Activity.MODE_PRIVATE);
            String data = pref.getString("data", "");
            try {
                JSONObject userdata = new JSONObject(data);
                token = userdata.getString("token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("user token is :", token);

            clientName= name.getText().toString();

        }


        @Override
        protected String doInBackground(Void... params) {

            String response = null;
            String jsonStr = null;
            try {
                URL url = new URL(url_base+"updateProfile"); //Enter URL here
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Client-Auth-Token", "Bearer"+" "+token);
                httpURLConnection.setDoInput(true);// here you are setting the `Content-Type` for the data you are sending which is `application/json`
                httpURLConnection.connect();


                    JSONObject jsonParam = new JSONObject();
                try {
                    jsonParam.put("clientName",clientName );
                    jsonParam.put("districtId",district_id );

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    Log.e("Before sending", "1" + jsonParam.toString());
                    wr.writeBytes(jsonParam.toString());

                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                response = convertStreamToString(in);
                jsonStr = response;


                wr.flush();
                wr.close();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("right here ", "Response from url here:" + jsonStr);

            return response;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("data is here","dataUpdated: "+result);

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
