package com.mohtaref.clinics;

import android.Manifest;
import android.app.Activity;
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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AppointmentDetails extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    String token;
    String lat;
    String lng;
    HashMap<String, String> Appointment_data;
    private static final String TAG = HttpHandlerPostToken.class.getSimpleName();
    final private String base_url = "https://laserbookingonline.com/manager/APIs/clientV2/";
    LinearLayout cancel_app_lay;
    LinearLayout cancel_reason_lay;
    LinearLayout reject_reason_lay;

    TextView reservationId;
    TextView serviceName;
    TextView totalCost;
    TextView employeeName_en;
    TextView appointmentDate;
    TextView time_Of_appointment;
    TextView status;
    TextView cancel_res_text;
    TextView reject_res_text;

    TextView paymentType;
    TextView paid;
    TextView clinicName;
    TextView address;
    TextView phone;
    Button location_btn;
    Button cancel_btn_app;
    String paid_app;
    String reason_text;

    boolean isguest = false;
    LinearLayout profile_side;
    LinearLayout rating_side;
    LinearLayout appointment_side;
    LinearLayout group_login_real;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadlocal();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_burger_icon_bar);

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
            profile_side.setVisibility(View.GONE);
            rating_side.setVisibility(View.GONE);
            appointment_side.setVisibility(View.GONE);
            group_login_real.setVisibility(View.VISIBLE);
        }
        Appointment_data = (HashMap<String, String>) getIntent().getSerializableExtra("appointment");


        cancel_app_lay = (LinearLayout) findViewById(R.id.cancel_app_lay);

        cancel_reason_lay = (LinearLayout) findViewById(R.id.cancel_lay);
        reject_reason_lay = (LinearLayout) findViewById(R.id.reject_lay);


        loadlocal();


        reservationId = (TextView) findViewById(R.id.reservationId_app);
        serviceName = (TextView) findViewById(R.id.serviceName_app);
        totalCost = (TextView) findViewById(R.id.totalCost_app);
        employeeName_en = (TextView) findViewById(R.id.employeeName_app);
        appointmentDate = (TextView) findViewById(R.id.appointmentDate_app);
        time_Of_appointment = (TextView) findViewById(R.id.time_Of_appointment_app);
        status = (TextView) findViewById(R.id.status_app);
        cancel_res_text = (TextView) findViewById(R.id.cancel_reason);
        reject_res_text = (TextView) findViewById(R.id.reject_reason);
        paymentType = (TextView) findViewById(R.id.paymentType_app);
        paid = (TextView) findViewById(R.id.paid_app);
        clinicName = (TextView) findViewById(R.id.clinicName_app);
        address = (TextView) findViewById(R.id.address_app);
        phone = (TextView) findViewById(R.id.phone_app);
        location_btn = (Button) findViewById(R.id.location_btn_app);

        if (Appointment_data.get("status").equals("approved") || Appointment_data.get("status").equals("pending"))
            cancel_app_lay.setVisibility(View.VISIBLE);

        if (Appointment_data.get("status").equals("cancelled")) {
            cancel_reason_lay.setVisibility(View.VISIBLE);
            cancel_res_text.setText(Appointment_data.get("cancelReason"));
        }

        if (Appointment_data.get("status").equals("reject") && Appointment_data.get("rejectReason") != null && !Appointment_data.get("rejectReason").equals("")) {
            reject_reason_lay.setVisibility(View.VISIBLE);
            reject_res_text.setText(Appointment_data.get("rejectReason"));

        }

        String[] separated = Appointment_data.get("appointmentDate").split(" ");
        String dateofApp = separated[0];
        String time = separated[1];

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
        SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lng = pref.getString("Mylang", "");
        if (lng != null && lng.equals("ar")) {
            serviceName.setText(Appointment_data.get("serviceName_ar"));
            employeeName_en.setText(Appointment_data.get("employeeName_ar"));
            clinicName.setText(Appointment_data.get("clinicName_ar"));
            address.setText(Appointment_data.get("address_ar"));
        } else {
            reservationId.setText(Appointment_data.get("reservationId"));
            serviceName.setText(Appointment_data.get("serviceName_en"));
            employeeName_en.setText(Appointment_data.get("employeeName_en"));
            clinicName.setText(Appointment_data.get("clinicName_en"));
            address.setText(Appointment_data.get("address_en"));
        }

        reservationId.setText(Appointment_data.get("reservationId"));

        totalCost.setText(Appointment_data.get("totalCost"));
        appointmentDate.setText(dateofApp);
        time_Of_appointment.setText(convertedTime);
        String statusvalue = Appointment_data.get("status");
        String paymentTypevalue = Appointment_data.get("paymentType");

        status.setText(getStringResourceByName(statusvalue));
        paymentType.setText(getStringResourceByName(paymentTypevalue));
        if (lng.equals("ar")) {
            if (Appointment_data.get("paid").equals(0))
                paid_app = "نعم";
            else
                paid_app = "لا";
            paid.setText(paid_app);
        } else {
            if (Appointment_data.get("paid").equals(0))
                paid_app = "Yes";
            else
                paid_app = "No";
            paid.setText(paid_app);
        }


        phone.setText(Appointment_data.get("phone"));

        location_btn = (Button) findViewById(R.id.location_btn_app);
        location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Appointment_data.get("lat") + "," + Appointment_data.get("lng"));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
        cancel_btn_app = (Button) findViewById(R.id.cancel_btn_app);
        cancel_btn_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert_message(getResources().getString(R.string.reason), getResources().getString(R.string.writereason), getResources().getString(R.string.send), getResources().getString(R.string.ac_cancel));
            }
        });

    }

    private String getStringResourceByName(String aString) {
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(aString, "string", packageName);
        return getString(resId);
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
                     //       Intent i=new Intent(HomePage.this,HomePage.class);

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
    public class CancelAppointments extends AsyncTask<Void, Void, Void> {
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
            String urlget = base_url + "cancelReservation";
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
                jsonParam.put("reservationId", Appointment_data.get("reservationId"));
                jsonParam.put("reason", reason_text);

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


            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
            String lng = pref.getString("Mylang", "");
            Log.e("language", lng);
            Intent intent = new Intent(AppointmentDetails.this, MyAppointments.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);

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

    public void alert_message(String title, String body, String send_btn, String cancel_btn) {
        final EditText input = new EditText(AppointmentDetails.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setSingleLine();
        input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        input.setLayoutParams(lp);
        AlertDialog alertDialog = new AlertDialog.Builder(AppointmentDetails.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(body);
        alertDialog.setView(input);
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, send_btn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        if(paid.trim().equals("paid")){
//                            Intent i=new Intent(AppointmentDetails.this,SuccessPage.class);
//                            startActivity(i);
//                            finishAffinity();
//                        }
//                        if(paid.trim().equals("pending_payment")){
//                            Intent i=new Intent(AppointmentDetails.this,HomePage.class);
//                            startActivity(i);
//                            finishAffinity();
//                        }
//                        if(paid.trim().equals("error")){
//                            Intent i=new Intent(AppointmentDetails.this,HomePage.class);
//                            startActivity(i);
//                            finishAffinity();
//                        }
                        reason_text = input.getText().toString();
                        if (!TextUtils.isEmpty(reason_text)) {
                            new CancelAppointments().execute();
                            dialog.dismiss();
                        }

                    }

                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, cancel_btn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }

                });
        alertDialog.show();
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
    }  public void setLocale(String lang) {
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
}
