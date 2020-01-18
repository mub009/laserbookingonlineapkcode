package com.mohtaref.clinics;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.regex.Pattern;


public class Register extends AppCompatActivity{
String region_string="";
    String city_string="";
    String area_string="";
    TextView errorTextN;
    public ArrayList<HashMap> RegionsData;
    public ArrayList<HashMap>CityData;
    public ArrayList<HashMap>DistrictsData;
    public ArrayList<String> Regions_en;
    public ArrayList<String> Regions_ar;
    public ArrayList<String> Cities_en;
    public ArrayList<String> Cities_ar;
    public ArrayList<String> Districts_en;
    public ArrayList<String> Districts_ar;
    private String url_base="https://laserbookingonline.com/manager/APIs/clientV2/";
    private NewRegister mAuthTask = null;
    public String region="";
    public String city="";
    public String area="";
    Button  Register_btn;
    EditText mobile;
    EditText name;
    EditText Password;
    EditText RePassword;
    boolean name_check=false;
    boolean pass_check=false;
    boolean repass_check=false;
    boolean mobile_check=false;
    boolean Reigon_check=false;
    boolean distract_check=false;
    boolean city_check=false;
    String district_id="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

          Register_btn=(Button)findViewById(R.id.Register_btn);
          mobile=(EditText)findViewById(R.id.phone);
          name=(EditText)findViewById(R.id.names1);
          Password=(EditText)findViewById(R.id.password);
          RePassword=(EditText)findViewById(R.id.repassword);

        mobile.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String k=mobile.getText().toString();
                if( Pattern.matches("^(\\+966|00966|966){0,1}0?5[0-9]{8}$", k)){
                    mobile_check=true;
           }
                else{
                    mobile_check=false;

                }
                Register_btn_enabler();
            }

        });
        name.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String fullname=name.getText().toString();
                if(!TextUtils.isEmpty(fullname)){
                     name_check=true;

                }
                else{
                     name_check=false;
                }
                Register_btn_enabler();
            }

        });
        Password.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String pass=Password.getText().toString();
                String repass=RePassword.getText().toString();
                if(!TextUtils.isEmpty(pass)&& pass.equals(repass)){
                    repass_check=true;
                    pass_check=true;
                }
                else{
                    pass_check=false;
                    repass_check=false;


                }
                Register_btn_enabler();

            }

        });
        RePassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String repass=RePassword.getText().toString();
                String pass=Password.getText().toString();
                if(!TextUtils.isEmpty(repass)&&repass.equals(pass)){
                repass_check=true;
                    pass_check=true;

                }
            else{
                repass_check=false;
                    pass_check=false;

                }
                Register_btn_enabler();

            }

        });
        loadlocal();
        Register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        RegionsData=new ArrayList<HashMap>();
        CityData=new ArrayList<HashMap>();
        DistrictsData=new ArrayList<HashMap>();
        Regions_en=new ArrayList<String>();
        Regions_ar=new ArrayList<String>();
        Cities_en=new ArrayList<String>();
        Cities_ar=new ArrayList<String>();
        Districts_en=new ArrayList<String>();
        Districts_ar=new ArrayList<String>();
        new PostRegions().execute();
        final TextInputLayout region=(TextInputLayout)findViewById(R.id.region_input);
        final TextInputLayout city=(TextInputLayout)findViewById(R.id.city_input);
        final TextInputLayout area=(TextInputLayout)findViewById(R.id.area_input);
        final Spinner spinner_region = (Spinner) findViewById(R.id.region);
        final ArrayAdapter<String> adapter_region = new ArrayAdapter<>(
                getBaseContext(),
                android.R.layout.simple_spinner_item,
                Regions_en
        );
        adapter_region.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

      //  spinner_region.setAdapter(adapter_region);
      //  int regionPosition = adapter_region.getPosition("region");
        //spinner_region.setSelection(regionPosition);


        final Spinner spinner_city = (Spinner) findViewById(R.id.city);
        ArrayAdapter<CharSequence> adapter_city = ArrayAdapter.createFromResource(this,
                R.array.countries_array, R.layout.simple_spinner_item_fix);
        adapter_city.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if(area.getVisibility()==View.VISIBLE){
                    distract_check=false;
                    Register_btn_enabler();

                }

                if (pos ==0) {

                    //     To reset a spinner to default value:
                    city_check=false;
                    final Spinner spinner_area = (Spinner) findViewById(R.id.city);
                    spinner_area.setSelection(0);
                    area.setVisibility(View.GONE);

                } else{
                    // Either gone or invisible

                    final Spinner spinner_area = (Spinner) findViewById(R.id.area);
                    spinner_area.setSelection(0);
                    area.setVisibility(View.GONE);
                    region_string = parent.getItemAtPosition(pos).toString();
                    errorTextN = (TextView) parent.getSelectedView();
                    city_check=true;
                    new PostDistricts().execute();

                }




            }

            public void onNothingSelected(AdapterView<?> parent) {
                city_check=false;
                region_string = "Select a city";
                errorTextN = (TextView) parent.getSelectedView();

            }

        });
  //      spinner_city.setAdapter(adapter_city);
  //      int cityPosition = adapter_city.getPosition("Nationality");
    //    spinner_city.setSelection(cityPosition);

        final Spinner spinner_area = (Spinner) findViewById(R.id.area);
        ArrayAdapter<CharSequence> adapter_area = ArrayAdapter.createFromResource(this,
                R.array.countries_array, R.layout.simple_spinner_item_fix);
        adapter_area.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
       // spinner_area.setAdapter(adapter_area);
       // int areaPosition = adapter_area.getPosition("Nationality");
     //   spinner_area.setSelection(cityPosition);


        //Region Item listener
        spinner_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

//                if (area.getVisibility() == View.VISIBLE) {
//                        area.setVisibility(View.GONE);
//                    final Spinner spinner_area = (Spinner) findViewById(R.id.area);
//                    spinner_area.setSelection(0);
//                    final Spinner spinner_city = (Spinner) findViewById(R.id.city);
//                    spinner_area.setSelection(0);
//               //     To reset a spinner to default value:
//
//                    //final Spinner spinner_city = (Spinner) findViewById(R.id.city);
//                      //  spinner_city.setSelection(0);
//
//                } else {
//                    // Either gone or invisible
//                }

                if(area.getVisibility()==View.VISIBLE || city.getVisibility()==View.VISIBLE){
                    distract_check=false;
                    city_check=false;
                    Register_btn_enabler();

                }
                if (pos ==0) {
                    Reigon_check=false;
                    final Spinner spinner_area = (Spinner) findViewById(R.id.area);
                    spinner_area.setSelection(0);
                    final Spinner spinner_city = (Spinner) findViewById(R.id.city);
                    spinner_city.setSelection(0);
                    area.setVisibility(View.GONE);
                    city.setVisibility(View.GONE);
                    //     To reset a spinner to default value:


                } else {

                    final Spinner spinner_city = (Spinner) findViewById(R.id.city);
                    spinner_city.setSelection(0);
                    final Spinner spinner_area = (Spinner) findViewById(R.id.area);
                    spinner_area.setSelection(0);
                    area.setVisibility(View.GONE);
                    city.setVisibility(View.GONE);
                    region_string = parent.getItemAtPosition(pos).toString();
                    errorTextN = (TextView) parent.getSelectedView();
                    Reigon_check=true;
                    new  PostCities().execute();

                    // Either gone or invisible
                }







            }

            public void onNothingSelected(AdapterView<?> parent) {
                Reigon_check=true;
                region_string = "Select a region";
                errorTextN = (TextView) parent.getSelectedView();

            }

        });



        //City Item listener
        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if(area.getVisibility()==View.VISIBLE){
                    distract_check=false;
                    Register_btn_enabler();

                }

                if (pos ==0) {

                    //     To reset a spinner to default value:
                    city_check=false;
                    final Spinner spinner_area = (Spinner) findViewById(R.id.city);
                     spinner_area.setSelection(0);
                    area.setVisibility(View.GONE);

                } else{
                    // Either gone or invisible

                    final Spinner spinner_area = (Spinner) findViewById(R.id.area);
                    spinner_area.setSelection(0);
                    area.setVisibility(View.GONE);
                    region_string = parent.getItemAtPosition(pos).toString();
                    errorTextN = (TextView) parent.getSelectedView();
                    city_check=true;
                    new PostDistricts().execute();

                }




            }

            public void onNothingSelected(AdapterView<?> parent) {
                city_check=false;
                region_string = "Select a city";
                errorTextN = (TextView) parent.getSelectedView();

            }

        });

        //area Item listener
        spinner_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {


                region_string = parent.getItemAtPosition(pos).toString();

                Log.e("the id is heree boyyyy",district_id);
                errorTextN = (TextView) parent.getSelectedView();
                if(pos==0){
                    distract_check=false;
                    Log.e("out of index","erroooooor");
                }
                else{
                    district_id=DistrictsData.get(pos-1).get("districtId").toString();
                    Reigon_check=true;
                    city_check=true;
                    distract_check=true;
                    Register_btn_enabler();
                }

            }

            public void onNothingSelected(AdapterView<?> parent) {
                distract_check=false;
                region_string = "Select a distract";
                errorTextN = (TextView) parent.getSelectedView();

            }

        });

    }
    public void Register_btn_enabler(){

       if(mobile_check&&name_check&&pass_check&&repass_check&&Reigon_check&&distract_check&&city_check){
           Register_btn.setEnabled(true);

       }
       else{
           Register_btn.setEnabled(false);
       }
    }
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mobile.setError(null);
        name.setError(null);
        // Store values at the time of the login attempt.
        String mobilenum = mobile.getText().toString();
        String fullname= name.getText().toString();
        String password= Password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
//         if (!isMobileValid(mobilenum)) {
//            mobile.setError(getString(R.string.error_invalid_email));
//            focusView = mobile;
//            cancel = true;
//        }
//        if (TextUtils.isEmpty(mobilenum)) {
//            mobile.setError(getString(R.string.error_field_required));
//            focusView = mobile;
//            cancel = true;
//        }
//        if (TextUtils.isEmpty(fullname)) {
//            name.setError(getString(R.string.error_field_required));
//            focusView = name;
//            cancel = true;
//        }
     /* if(device_connected==false){
            Toast.makeText(LoginActivity.this,"Connect finger print device",Toast.LENGTH_LONG).show();
            cancel = true;

        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            //focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);
            SharedPreferences pref=getSharedPreferences("Settings",Activity.MODE_PRIVATE);
            String lng=pref.getString("Mylang","");
            mAuthTask = new NewRegister(mobilenum,fullname,password,district_id,lng);
            mAuthTask.execute((Void) null);
            // new SendPostRequest().execute();
//            Intent refresh = new Intent(this, HomePage.class);
//            startActivity(refresh);
//            finish();
        }
    }

    public void sign_in_page(View view) {

        Intent refresh = new Intent(this, LoginActivity.class);
        startActivity(refresh);
        finish();
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

        setLocale(lng);
    }

    public void setLocale_en(View view ) {
        String lang="en";
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
        recreate();
    }
    public void setLocale_ar(View view) {
        String lang="ar";
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
        recreate();
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



        }

        @Override
        protected Void doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();

            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String urlget = url_base+"regions";
            String jsonStr = sh.makeServiceCall(urlget);
            Regions_en.clear();
            Regions_ar.clear();
            RegionsData.clear();
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
                        RegionsData.add(reigon);
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

            final Spinner spinner_region = (Spinner) findViewById(R.id.region);
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    Register.this,
                    R.layout.simple_spinner_item_fix,
                    Regions_ar
            );
            adapter.insert("اختر المنطقة",0);
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

            spinner_region.setAdapter(adapter);
            //  adapter.notifyDataSetChanged();
         //   int spinnerPosition = adapter.getPosition("المنطقة");
            spinner_region.setSelection(0);
           // spinner_region.setBackgroundResource(arrow_left);



        }
        else{

    final Spinner spinner_region = (Spinner) findViewById(R.id.region);
    final ArrayAdapter<String> adapter = new ArrayAdapter<>(
           Register.this,
            R.layout.simple_spinner_item_fix,
            Regions_en
    );
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

    adapter.insert("Select a region",0);


    spinner_region.setAdapter(adapter);
    //  adapter.notifyDataSetChanged();
 //   int spinnerPosition = adapter.getPosition("region");
    spinner_region.setSelection(0);
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

        }

        @Override
        protected Void doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();
            final Spinner spinner_region = (Spinner) findViewById(R.id.region);
            @SuppressLint("WrongThread") int reigon= spinner_region.getSelectedItemPosition();
           String id_region=RegionsData.get(reigon-1).get("regionId").toString();
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

            final TextInputLayout city=(TextInputLayout)findViewById(R.id.city_input);

            city.setVisibility(View.VISIBLE);
            if(lng.equals("ar")){

                final Spinner spinner_city = (Spinner) findViewById(R.id.city);
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        Register.this,
                        R.layout.simple_spinner_item_fix,
                        Cities_ar
                );
                adapter.insert("اختر المدينة",0);
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

                spinner_city.setAdapter(adapter);
                //  adapter.notifyDataSetChanged();
            //    int spinnerPosition = adapter.getPosition("المدينة");
                spinner_city.setSelection(0);
                // spinner_region.setBackgroundResource(arrow_left);



            }
            else{

                final Spinner spinner_city = (Spinner) findViewById(R.id.city);
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        Register.this,
                        R.layout.simple_spinner_item_fix,
                        Cities_en
                );
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

                adapter.insert("Select a city",0);


                spinner_city.setAdapter(adapter);
                //  adapter.notifyDataSetChanged();
                //int spinnerPosition = adapter.getPosition("City");
                spinner_city.setSelection(0);
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
    //////////////////////////////////////////////////////////////////////////////////////////// PostDistricts
    public class PostDistricts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Districts_en.clear();
            Districts_ar.clear();
            DistrictsData.clear();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();
            final Spinner spinner_city = (Spinner) findViewById(R.id.city);
            @SuppressLint("WrongThread") int city_pos= spinner_city.getSelectedItemPosition();
            String id_city=CityData.get(city_pos-1).get("cityId").toString();
            Log.e("check Thaat check dat: ",id_city);
            HttpHandler sh = new HttpHandler();

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
            final TextInputLayout area=(TextInputLayout)findViewById(R.id.area_input);
            area.setVisibility(View.VISIBLE);
            if(lng.equals("ar")){

                final Spinner spinner_area = (Spinner) findViewById(R.id.area);
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        Register.this,
                        R.layout.simple_spinner_item_fix,
                        Districts_ar
                );

                adapter.insert("اختر الحي",0);
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

                spinner_area.setAdapter(adapter);
                //  adapter.notifyDataSetChanged();
                //    int spinnerPosition = adapter.getPosition("المدينة");
                spinner_area.setSelection(0);
                // spinner_region.setBackgroundResource(arrow_left);



            }
            else{

                final Spinner spinner_area = (Spinner) findViewById(R.id.area);
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        Register.this,
                        R.layout.simple_spinner_item_fix,
                        Districts_en
                );
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

                adapter.insert("Select a district",0);


                spinner_area.setAdapter(adapter);
                //  adapter.notifyDataSetChanged();
                //int spinnerPosition = adapter.getPosition("City");
                spinner_area.setSelection(0);

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
    ////////////////////////////////////////////////////////////////////// New Registeration

    public class NewRegister extends AsyncTask<Void, Void, String> {
        boolean state = false;
        final String phone;
        final String fullname;
        final String pass;
        final String districtId;
        final String lang;


        //0531042553/123456  phone + pass

        public NewRegister(String mobile,String name,String password ,String district_Id, String language) {
            phone = mobile;
            fullname=name;
            pass=password;
            districtId=district_Id;
            lang=language;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }


        @Override
        protected String doInBackground(Void... params) {

            String response = null;
            String jsonStr = null;
            try {
                URL url = new URL(url_base+"register"); //Enter URL here
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setDoInput(true);// here you are setting the `Content-Type` for the data you are sending which is `application/json`
                httpURLConnection.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("mobile",phone );
                jsonParam.put("fullname",fullname );
                jsonParam.put("password",pass );
                jsonParam.put("districtId",districtId );
                jsonParam.put("lang",lang );




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
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("right here ", "Response from url here:" + jsonStr);


            return jsonStr;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String clinetId=result.trim().replaceAll("\"","");
            Log.e("result is ","this "+clinetId);

            mAuthTask = null;
            //   showProgress(false);
            if (result!=null&&!result.equals("false")) {
                Intent i=new Intent(Register.this,Verify.class);
                i.putExtra("user_register_id",clinetId);
                startActivity(i);
                finishAffinity();
            } else {
                Log.e("fail","fail");
                alert_message_fail(getResources().getString(R.string.mobile_unavailable_title),getResources().getString(R.string.mobile_unavailable_desc));
                //pop up message here

//                Toast.makeText(Forget_password.this,"check your internet Connection or user, password are incorrect",Toast.LENGTH_LONG).show();
//                mEmailView.setError(getString(R.string.error_invalid_email));
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
                //   mPasswordView.requestFocus();
            }


        }

    }
    public void alert_message_fail(String title,String body){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(body);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {



                        dialog.dismiss();
                    }

                });
        alertDialog.show();
    }
    public void alert_message(String title,String body,String result){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(body);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i=new Intent(Register.this,Verify.class);
                        i.putExtra("user_register_id",result);
                        startActivity(i);
                        finish();


                        dialog.dismiss();
                    }

                });
        alertDialog.show();
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
