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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class TimePickerPage extends AppCompatActivity {
    ArrayList<String> available_time_list;
    ListView time_list_view;
    String month_name;
    Button repickdate;
    TextView tvw;
    String date_picked;
    String employeeId;
    HashMap<String, String> offer;
    Calendar calendar;
    ArrayList<HashMap<String, String>> providers_list;
    public ArrayList<String> provider_list_en;
    public ArrayList<String> provider_list_ar;
    private static final String TAG = HttpHandlerPostToken.class.getSimpleName();

    final private String base_url = "https://laserbookingonline.com/manager/APIs/clientV2/";
    HashMap<String, String> selected_provider;
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
        setContentView(R.layout.activity_time_picker_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadlocal();
        available_time_list = new ArrayList();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_burger_icon_bar);
        SharedPreferences pref = getSharedPreferences("user", Activity.MODE_PRIVATE);
        String data = pref.getString("data", "");
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
        selected_provider = (HashMap<String, String>) getIntent().getExtras().getSerializable("provider");
        date_picked = getIntent().getStringExtra("appointmentDate");
        offer = (HashMap<String, String>) getIntent().getExtras().getSerializable("offer");

        provider_list_ar = (ArrayList<String>) getIntent().getExtras().getSerializable("providers_ar");
        provider_list_en = (ArrayList<String>) getIntent().getExtras().getSerializable("providers_en");
        providers_list = (ArrayList<HashMap<String, String>>) getIntent().getExtras().getSerializable("providers_list");
        if (selected_provider != null)
            employeeId = selected_provider.get("employeeId");
        Log.e("welcome to timePicker", "here is your employeeId " + employeeId);
        Log.e("welcome to timePicker", "here is your offer " + offer.toString());
        Log.e("welcome to timePicker", "here is your date " + date_picked);
        time_list_view = (ListView) findViewById(R.id.time_list_view);
        repickdate = (Button) findViewById(R.id.repick_date);
        try {
            month_name = getMonth(date_picked);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] separated = date_picked.split("-");
        repickdate.setText(separated[2] + " " + month_name + " " + separated[0]);
        repickdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        new AvailableTime().execute();
    }

    private static String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
        Log.e("month name is;", "this ;" + monthName);
        return monthName;
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
    public class AvailableTime extends AsyncTask<Void, Void, Void> {
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

            // Making a request to url and getting response
            String urlget = base_url + "getAvailableTimes";
            String jsonStr = null;

            // String jsonStr = sh.makeServiceCall(urlget, token, "", "");
            // String response = null;
            try {
                URL url = new URL(urlget);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Client-Auth-Token", "Bearer" + " " + token);
                // String offerid=ClinicData.get("offerId");

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("clinicId", offer.get("clinicId"));

                if(offer.get("offerId")!=null&&!offer.get("offerId").equals("null"))
                jsonParam.put("offerId", offer.get("offerId"));

                jsonParam.put("serviceId", offer.get("serviceId"));
                jsonParam.put("employeeId", employeeId);
                jsonParam.put("selDate", date_picked);
                jsonParam.put("totalDuration", offer.get("duration"));

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
            Log.e("gg mynigga", "yeaah");
            Log.e("is offers :", "Response from url countries: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONArray c = new JSONArray(jsonStr);


                    //     listDataHeader.add(clinic_ob);
                    SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
                    String lng = pref.getString("Mylang", "");
                    if (lng.equals("ar")) {
                        Locale locale = new Locale("en");
                        Locale.setDefault(locale);
                        for (int i = 0; i < c.length(); i++) {
                            String time = (String) c.get(i) + 1;
                            String convertedTime = "";
                            try {
                                SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
                                SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm:ss");
                                Date date = parseFormat.parse(time);
                                convertedTime = displayFormat.format(date);
                                Log.e("time is :", "convertedTime : " + convertedTime);
                            } catch (final ParseException e) {
                                e.printStackTrace();
                            }
                            available_time_list.add(convertedTime);
                        }
                        Locale locales = new Locale("ar");
                        Locale.setDefault(locales);
                    } else {
                        for (int i = 0; i < c.length(); i++) {
                            String time = (String) c.get(i) + 1;
                            String convertedTime = "";
                            try {
                                SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
                                SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm:ss");
                                Date date = parseFormat.parse(time);
                                convertedTime = displayFormat.format(date);
                                Log.e("time is :", "convertedTime : " + convertedTime);
                            } catch (final ParseException e) {
                                e.printStackTrace();
                            }
                            available_time_list.add(convertedTime);
                        }


                    }
                    Log.e("nery list is :", "" + available_time_list.toString());

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
            time_list_view = (ListView) findViewById(R.id.time_list_view);

            CustomListAdapter_time adapter_list = new CustomListAdapter_time(
                    getApplicationContext(), R.layout.list_item_time, available_time_list
            );
            time_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (lng.equals("ar")) {
                        Locale locale = new Locale("en");
                        Locale.setDefault(locale);
                        Log.e("clicked is", "this " + available_time_list.get(position));
                        Intent intent = new Intent(TimePickerPage.this, Checkout.class);
                        String timeam = available_time_list.get(position);
                        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm:ss");
                        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
                        Date date = null;
                        try {
                            date = parseFormat.parse(timeam);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String time = displayFormat.format(date);
                        Log.e("time is ", "in 24 : " + time);
                        intent.putExtra("provider", selected_provider);
                        intent.putExtra("offer", offer);
                        intent.putExtra("appointmentDate", date_picked + " " + time);

                        Locale localed = new Locale("ar");
                        Locale.setDefault(localed);
                        startActivity(intent);
                    } else {
                        Log.e("clicked is", "this " + available_time_list.get(position));
                        Intent intent = new Intent(TimePickerPage.this, Checkout.class);
                        String timeam = available_time_list.get(position);
                        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm:ss");
                        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
                        Date date = null;
                        try {
                            date = parseFormat.parse(timeam);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String time = displayFormat.format(date);
                        Log.e("time is ", "in 24 : " + time);
                        intent.putExtra("provider", selected_provider);
                        intent.putExtra("offer", offer);
                        intent.putExtra("appointmentDate", date_picked + " " + time);


                        startActivity(intent);
                    }


                    //here i want to get the items
                }
            });
            time_list_view.setAdapter(adapter_list);

            adapter_list.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(time_list_view);

//            if(lng.equals("ar")){
//
//                final Spinner provider_spinner = (Spinner) findViewById(R.id.service_provider);
//                final ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                        TimePickerPage.this,
//                        R.layout.spinner_ar,
//                        provider_list_ar
//                );
//                //adapter.insert("اي مقدم خدمة",0);
//                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
//
//                provider_spinner.setAdapter(adapter);
//                //  adapter.notifyDataSetChanged();
//                //    int spinnerPosition = adapter.getPosition("المدينة");
//                provider_spinner.setSelection(0);
//                // spinner_region.setBackgroundResource(arrow_left);
//
//
//
//            }
//            else{
//
//                final Spinner provider_spinner = (Spinner) findViewById(R.id.service_provider);
//                final ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                        TimePickerPage.this,
//                        R.layout.simple_spinner_dropdown_item,
//                        provider_list_en
//                );
//                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
//
//               // adapter.insert("Any Provider",0);
//
//
//                provider_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    public void onItemSelected(AdapterView<?> parent, View view,
//                                               int pos, long id) {
//
//                        if (pos ==0) {
//
//                            //     To reset a spinner to default value:
//
//                            selected_provider=null;
//                        } else{
//                            selected_provider=providers_list.get(pos-1);
//
//                        }
//
//
//                        Log.e("selected is :","dat "+selected_provider);
//
//                    }
//
//                    public void onNothingSelected(AdapterView<?> parent) {
//
//                        Log.e("noting :","dat ");
//
//                    }
//
//                });
//                provider_spinner.setAdapter(adapter);
//                //  adapter.notifyDataSetChanged();
//                //int spinnerPosition = adapter.getPosition("City");
//                provider_spinner.setSelection(0);
//            }
            pd.dismiss();
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

    public void logout(View view) {
        SharedPreferences.Editor editor = getSharedPreferences("user", Activity.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finishAffinity();
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
        //setLocale(lng);
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
}
