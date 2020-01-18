package com.mohtaref.clinics;
import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener , com.google.android.gms.location.LocationListener {
    ConnectivityReceiver connectivityReceiver=new ConnectivityReceiver();
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    public HashMap<String, String> User_data = new HashMap<>();
    public String language;
    private static final String ACTION_USB_PERMISSION = "worked wonder";
    ;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    Context context;
    LocationManager locationManager;
    Boolean gps_enabled=false;

    private login mAuthTask = null;
    private boolean correctPass=true;
    public boolean unVerfied=false;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    public boolean device_connected=false;
    final private String base_url = "https://laserbookingonline.com/manager/APIs/clientV2/";
    public String User_register_Id="";
    ProgressDialog pd;
    String token;
    String notification_token;
    String prefered_lang;
    HashMap<String,String> verfiychek;
    String version_name;
    int version_code;
    HashMap<String,String> version_app;
    //gradlew :app:dependencies
    /**
     * permissions request code
     */
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadlocal();
        mContext=this;
        setContentView(R.layout.activity_login);
        SharedPreferences prefg=getSharedPreferences("Settings",Activity.MODE_PRIVATE);
        String lng=prefg.getString("Mylang","");
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        if(lng.equals("ar")){
            pd.setMessage("جاري التحميل");

        }
        else{
            pd.setMessage("Loading");

        }
        pd.show();
        version_name= BuildConfig.VERSION_NAME;
        version_code=BuildConfig.VERSION_CODE;
//        Tovuti.from(LoginActivity.this).monitor(new Monitor.ConnectivityListener(){
//            @Override
//            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast){
//               if(isConnected){
//                Log.e("coonnected"," yep");
//                   new Handler().postDelayed(new Runnable() {
//                       @Override
//                       public void run() {
//
//                       }
//                   }, 500);
//
//
//               }
//               else{
//                   Log.e("not","conntedc yep");
//
//                      // alert_message_net(getResources().getString(R.string.nointernet),getResources().getString(R.string.checkinternet),LoginActivity.this);
//               }
//
//
//            }
//        });
        version_app=new HashMap<>();
                          new CheckVersion().execute();


            verfiychek=new HashMap<>();
        SharedPreferences pref = getSharedPreferences("user", Activity.MODE_PRIVATE);
        FirebaseApp.initializeApp(LoginActivity.this);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e("error", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.e("notification token","is this : "+token);
                        SharedPreferences.Editor editor=getSharedPreferences("notification_laser", Activity.MODE_PRIVATE).edit();
                        editor.putString("token_notify",token);
                        editor.apply();
//                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.e("error", msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


       // pd.dismiss();
        pd.cancel();
        checkPermissions();
      //  ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
        language= Locale.getDefault().getDisplayLanguage();
        Log.e("currunt Language: ",language);
       // loadlocal();
        //pd.dismiss();

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.phone);
//        populateAutoComplete();
        //   getDetail();
        mPasswordView = (EditText) findViewById(R.id.password);
//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setEnabled(false);
        mEmailView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String k=mEmailView.getText().toString();
                if( Pattern.matches("^[0,5]\\d{8,9}$", k)){
                    Log.e("worked ","wooooooooow");
                    mEmailSignInButton.setEnabled(true);}
                else{
                    mEmailSignInButton.setEnabled(false);
                    Log.e("failed","failed");

                }
            }

        });
        mPasswordView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String k=mPasswordView.getText().toString();
                if( !TextUtils.isEmpty(k)){
                    mEmailSignInButton.setEnabled(true);}
                else{
                    mEmailSignInButton.setEnabled(false);

                }
            }

        });
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.email_login_form);
        mProgressView = findViewById(R.id.login_progress);
        context = getApplicationContext();
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
     //   if(gps_enabled) {
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(1 * 100)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 10); // 1 second, in milliseconds

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
       // }

    }

    @Override
    public void onResume() {
        super.onResume();
      //  if(isConnected(this)){
            locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(gps_enabled) {
                mGoogleApiClient.connect();
            }
            else
                return;
     //   }
//        else
//            alert_message_net("no","net");


    }
    @Override
    protected void onPause() {
        super.onPause();
    //    if(isConnected(this)){
            locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(gps_enabled) {
                if (mGoogleApiClient.isConnected()) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                    mGoogleApiClient.disconnect();
                }
            }
            return;
        //}
//        else
//            alert_message_net("no","net");

    }

    @Override
    protected  void onStop(){
        super.onStop();
      //  Tovuti.from(this).stop();


    }


    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
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
//    public void alert_message_net(String title, String body,Context context) {
//        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
//        alertDialog.setTitle(title);
//        alertDialog.setMessage(body);
//        alertDialog.setCancelable(false);
//        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//        AlertDialog dialog=alertDialog.create();
//        dialog.show();
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(isConnected(context))
//                {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            dialog.dismiss();
//                        }
//                    }, 1000);
//                    Log.e("is connected","ture");
//                }
//            }
//        });
//
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
              //  initialize();
                break;
        }
    }
//    private void populateAutoComplete() {
//        if (!mayRequestContacts()) {
//            return;
//        }
//
//    }
//
//    private boolean mayRequestContacts() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
//            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
//                    .setAction(android.R.string.ok, new View.OnClickListener() {
//                        @Override
//                        @TargetApi(Build.VERSION_CODES.M)
//                        public void onClick(View v) {
//                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//                        }
//                    });
//        } else {
//            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//        }
//        return false;
//    }

    /**
     * Callback received when a permissions request has been completed.
     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_READ_CONTACTS) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                populateAutoComplete();
//            }
//        }
//    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
        //    mPasswordView.setError(getString(R.string.));
           // focusView = mPasswordView;
            cancel = true;
        }

//        if (!isPasswordValid(password)) {
//            //mPasswordView.setError(getString(R.string.error_incorrect_password));
//          //  focusView = mPasswordView;
//            cancel = true;
//        }
//        // Check for a valid email address.
//        else if (!isEmailValid(email)) {
//            //mEmailView.setError(getString(R.string.error_invalid_email));
//           // focusView = mEmailView;
//            cancel = true;
//        }
        if (TextUtils.isEmpty(email)) {
//            mEmailView.setError(getString(R.string.error_field_required));
//            focusView = mEmailView;
            cancel = true;

        }
     /* if(device_connected==false){
            Toast.makeText(LoginActivity.this,"Connect finger print device",Toast.LENGTH_LONG).show();
            cancel = true;

        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            //focusView.requestFocus();
            alert_message(getResources().getString(R.string.loginfailed),getResources().getString(R.string.checkcreds));

        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
      //      showProgress(true);
            pd.show();
            mAuthTask = new login(email, password);
            mAuthTask.execute((Void) null);
           // new SendPostRequest().execute();
//            Intent refresh = new Intent(this, HomePage.class);
//            startActivity(refresh);
//            finish();
        }
    }

    private boolean isEmailValid(String email) {
        if (android.util.Patterns.PHONE.matcher(email).matches()) {
            return true;
        } else
            return false;
    }

    private boolean isPasswordValid(String password) {
        if (password.length()>5) {
            return true;
        } else
            return false;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public void alert_message(String title,String body){
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

        Locale myLocale = new Locale(lng);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());


       // pd.show();
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
        recreate();
    }
    public void setLocale_ar(View view) {
        String lang="ar";
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor=getSharedPreferences("Settings", Activity.MODE_PRIVATE).edit();
        editor.putString("Mylang",lang);
        editor.apply();
        recreate();
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


            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,  this);
        }
        else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
Log.e("locatioin is",""+location.toString());
    }


    private void handleNewLocation(Location location) {
        Log.e("ggz", location.toString());
      double  latitude = location.getLatitude();
     double   longitude = location.getLongitude();
    String   lng = String.valueOf(longitude);
   String   lat = String.valueOf(latitude);

        SharedPreferences.Editor editor=getSharedPreferences("loc", Activity.MODE_PRIVATE).edit();
        editor.putString("lat",lat);
        editor.putString("lng",lng);
        editor.apply();
        Log.e("lat string is ", lat);
    }
    public class login extends AsyncTask<Void, Void, String> {
        boolean state = false;
        final String phone;
        final String pass;

        //0531042553/123456  phone + pass

        public login(String email, String password) {
            phone = email;
            pass = password;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPreferences pref=getSharedPreferences("notification_laser",Activity.MODE_PRIVATE);
            if(pref.getString("token_notify","")!=null&&!pref.getString("token_notify","").equals(""))
             notification_token=pref.getString("token_notify","");

            prefered_lang=Locale.getDefault().getLanguage();




        }


        @Override
        protected String doInBackground(Void... params) {
            User_register_Id="";
            String response = null;
            String jsonStr = null;
            try {
                URL url = new URL(base_url+"login"); //Enter URL here
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setDoInput(true);// here you are setting the `Content-Type` for the data you are sending which is `application/json`
                httpURLConnection.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("mobile",phone );
                jsonParam.put("password", pass);
                if(notification_token!=null&&!notification_token.equals("")){
                    jsonParam.put("deviceToken",notification_token);
                    jsonParam.put("lang",prefered_lang);
                }


                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                Log.e("Before sending", "1" + jsonParam.toString());
                wr.writeBytes(jsonParam.toString());
                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                response = convertStreamToString(in);
                jsonStr = response;


                wr.flush();
                wr.close();

                JSONObject logindata= new JSONObject(jsonStr);
                if(logindata.getString("error")!=null){
                   String error= logindata.getString("error");
                   String clientId= logindata.getString("clientId");
                    verfiychek.put("error",error);
                    verfiychek.put("clientId",clientId);
                    User_register_Id=clientId;
                    unVerfied=true;

                }


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


    if (!result.trim().equals("false")&&result!=null) {
        Log.e("inside result= false","here");
                if(unVerfied){
                    Log.e("inside verify_check","here");
                    Intent i=new Intent(LoginActivity.this,Verify.class);
                    i.putExtra("user_register_id",User_register_Id);
                    pd.cancel();
                    startActivity(i);
                    finish();

                }

else {

                    Log.e("inside verify_check","here");
                    Log.e("inside else","here");
                    SharedPreferences.Editor editor=getSharedPreferences("user", Activity.MODE_PRIVATE).edit();
                    editor.putString("data",result);
                    editor.apply();
                    pd.cancel();
                    Intent intent = new Intent(LoginActivity.this, HomePage.class);
                    startActivity(intent);
                    finish();
                }




    } else {
        mAuthTask=null;
        pd.cancel();
    //    showProgress(false);
        Log.e("pop up is needed pass", result);
        alert_message(getResources().getString(R.string.loginfailed),getResources().getString(R.string.checkcreds));


        //   mPasswordView.requestFocus();
    }









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


    public void Register_page(View view) {
        Intent refresh = new Intent(this, Register.class);
        startActivity(refresh);
        finish();
    }
    public void forget_password_page(View view) {
        Intent refresh = new Intent(this, Forget_password.class);
        startActivity(refresh);
        finish();
    }
    public void home_page(View view) {
       // showProgress(true);

        pd.show();
        new guestLogin().execute();

    }
    public class guestLogin extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }


        @Override
        protected String doInBackground(Void... params) {

            String response = null;
            String jsonStr = null;
            try {
                URL url = new URL(base_url+"guestLogin"); //Enter URL here
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setDoInput(true);// here you are setting the `Content-Type` for the data you are sending which is `application/json`
                httpURLConnection.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("mobile","" );


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
        Log.e("yep :;:",result);
            if (result!=null) {
                SharedPreferences.Editor editor=getSharedPreferences("user", Activity.MODE_PRIVATE).edit();
                editor.putString("data",result);
                editor.apply();
                Intent intent = new Intent(LoginActivity.this, HomePage.class);
                startActivity(intent);
                finish();
            } else {
                alert_message(getResources().getString(R.string.nointernet),getResources().getString(R.string.checkinternet));

                //   mPasswordView.requestFocus();
            }

        }

    }
    @SuppressLint("StaticFieldLeak")
    public class CheckVersion extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();

            // Making a request to url and getting response
            String urlget = base_url + "version";
            String jsonStr = null;

            // String jsonStr = sh.makeServiceCall(urlget, token, "", "");
            // String response = null;
            try {
                URL url = new URL(urlget);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                // String offerid=ClinicData.get("offerId");


                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                jsonStr = convertStreamToString(in);
            } catch (MalformedURLException e) {
                Log.e("", "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e("", "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e("", "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e("", "Exception: " + e.getMessage());
            }
            //   return response;
            Log.e("gg mynigga","yeaah");
            Log.e("is offers :", "Response from url countries: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject c = new JSONObject(jsonStr);
                    String versionId = c.getString("versionId");

                    String android = c.getString("android");



                    HashMap<String, String> offer_ob = new HashMap<>();

                    // adding each child node to HashMap key => value
                    offer_ob.put("versionId", versionId);
                    offer_ob.put("android", android);



                    version_app=offer_ob;







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
            Log.e("version local is:"," "+BuildConfig.VERSION_NAME);
            SharedPreferences pref = getSharedPreferences("user", Activity.MODE_PRIVATE);
            Log.e("version from server is:"," "+version_app.get("android"));
            String v_s=version_app.get("android");
            int version_value= 5;
            if (v_s != null) {
                version_value = compareVersionNames(BuildConfig.VERSION_NAME,v_s);
            }

            if(version_value==-1){
                pd.cancel();
                alert_message_guest(getResources().getString(R.string.UPD_TITLE),getResources().getString(R.string.UPD_BODY),getResources().getString(R.string.UPD_OK));

            }
            else if(version_value==0){
                if(pref.getString("data", "")!=null&&!pref.getString("data", "").equals("")){
                    Log.e("version from server is:"," case 0 not null");

                       //  pd.show();
                    String data = pref.getString("data", "");
                    try {
                        JSONObject userdata = new JSONObject(data);
                        token = userdata.getString("token");
//                prefered_lang=userdata.getString("");
//                SharedPreferences.Editor editor=getSharedPreferences("Settings", Activity.MODE_PRIVATE).edit();
//                editor.putString("Mylang",prefered_lang);
//                editor.apply();
                        Intent i=new Intent(LoginActivity.this,HomePage.class);
                        pd.cancel();

                        startActivity(i);
                        finishAffinity();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                pd.cancel();

            }
            else if(version_value==1){
                if(pref.getString("data", "")!=null&&!pref.getString("data", "").equals("")){
                  //  pd.show();
                    Log.e("version from server is:"," case 1 not null");

                    String data = pref.getString("data", "");
                    try {
                        JSONObject userdata = new JSONObject(data);
                        token = userdata.getString("token");
//                prefered_lang=userdata.getString("");
//                SharedPreferences.Editor editor=getSharedPreferences("Settings", Activity.MODE_PRIVATE).edit();
//                editor.putString("Mylang",prefered_lang);
//                editor.apply();
                        Intent i=new Intent(LoginActivity.this,HomePage.class);
                        pd.cancel();

                        startActivity(i);
                        finishAffinity();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    pd.cancel();

                }
                pd.cancel();
            }
            else{
                Log.e("returned version is ","null");
            }


            pd.cancel();

        }
    }
    public void alert_message_guest(String title,String body,String Button){
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(body)
                .setCancelable(false)
                .setPositiveButton(Button, null) //Set to null. We override the onclick
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                android.widget.Button button = ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        //Dismiss once everything is OK.
                        //  alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    public int compareVersionNames(String oldVersionName, String newVersionName) {
        int res = 0;

        String[] oldNumbers = oldVersionName.split("\\.");
        String[] newNumbers = newVersionName.split("\\.");

        // To avoid IndexOutOfBounds
        int maxIndex = Math.min(oldNumbers.length, newNumbers.length);

        for (int i = 0; i < maxIndex; i ++) {
            int oldVersionPart = Integer.valueOf(oldNumbers[i]);
            int newVersionPart = Integer.valueOf(newNumbers[i]);

            if (oldVersionPart < newVersionPart) {
                res = -1;
                break;
            } else if (oldVersionPart > newVersionPart) {
                res = 1;
                break;
            }
        }

        // If versions are the same so far, but they have different length...
        if (res == 0 && oldNumbers.length != newNumbers.length) {
            res = (oldNumbers.length > newNumbers.length)?1:-1;
        }

        return res;
    }


}
