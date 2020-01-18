package com.mohtaref.clinics;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;



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
import java.net.URL;
import java.util.Locale;


public class Verify extends AppCompatActivity{
    EditText verify_code;
    Button verify_btn;
    public  String user_register_id;
    private String url_base="https://laserbookingonline.com/manager/APIs/clientV2/";
    public Boolean code_check;
    private User_verify mAuthTask = null;
    private Resend_verify_code resend_task=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        loadlocal();
        Intent intent = getIntent();
        if(intent.getExtras().getString("user_register_id")!=null){
            user_register_id=intent.getExtras().getString("user_register_id");
            Log.e("the string is ","this "+user_register_id);

        }
        verify_code=(EditText)findViewById(R.id.Verify_code);
        verify_btn=(Button)findViewById(R.id.verify_btn);
        verify_code.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                String code=verify_code.getText().toString();
                if(!TextUtils.isEmpty(code)){
                    verify_btn.setEnabled(true);

                }
                else{
                    verify_btn.setEnabled(false);
                }

            }


        });
        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        verify_code.setError(null);

        // Store values at the time of the login attempt.
        String code = verify_code.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
//         if (!isMobileValid(mobilenum)) {
//            mobile.setError(getString(R.string.error_invalid_email));
//            focusView = mobile;
//            cancel = true;
//        }
        if (TextUtils.isEmpty(code)) {

        }
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
            mAuthTask = new User_verify(code);
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
    public void Resend_verify_code(View view) {
        resend_task=new Resend_verify_code(user_register_id);
        resend_task.execute((Void) null);
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




    public class User_verify extends AsyncTask<Void, Void, String> {
        final String verfiy_code;

        //0531042553/123456  phone + pass

        public User_verify(String code) {
            verfiy_code = code;
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
                URL url = new URL(url_base+"verify"); //Enter URL here
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("clientId",user_register_id);
                jsonParam.put("code",verfiy_code );




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

            resend_task=null;
            mAuthTask = null;
            //   showProgress(false);
            if (result!=null) {
                Log.e("send: ",result);

                if(result.trim().equalsIgnoreCase("true")){
                    Log.e("SmS ","SmmmmmmmmmmmmmmmmmmS");
                    Log.e("data is : ",result);
                    alert_message_verified(getResources().getString(R.string.verified_title),getResources().getString(R.string.verified_desc));


                }
               else{
                    Log.e("worng code",result);
                    //pop up wrong code
                    alert_message(getResources().getString(R.string.invalid_verify_title),getResources().getString(R.string.invalid_verify_desc));

                }


            } else {
                Log.e("null"," null");
                Log.e("fail","fail");
                Log.e("fail","is "+result);
                alert_message(getResources().getString(R.string.invalid_verify_title),getResources().getString(R.string.invalid_verify_desc));
                //pop up
//                Toast.makeText(Forget_password.this,"check your internet Connection or user, password are incorrect",Toast.LENGTH_LONG).show();
//                mEmailView.setError(getString(R.string.error_invalid_email));
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
                //   mPasswordView.requestFocus();
            }


        }

    }

    public class Resend_verify_code extends AsyncTask<Void, Void, String> {
        final String client_id;

        //0531042553/123456  phone + pass

        public Resend_verify_code(String id) {
            client_id=id;

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
                URL url = new URL(url_base+"sendAgain"); //Enter URL here
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setDoInput(true);// here you are setting the `Content-Type` for the data you are sending which is `application/json`
                httpURLConnection.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("clientId",client_id );


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


            resend_task = null;
            //   showProgress(false);
            if (result!=null) {
                Log.e("resend: ",result);
                if(result.trim().equalsIgnoreCase("true")){
                    Log.e("SmS ","SmmmmmmmmmmmmmmmmmmS");
                    Log.e("data is : ",result);
                    alert_message(getResources().getString(R.string.verify_code),getResources().getString(R.string.new_sms_sent));

                }
                else {
                    Log.e("worng code",result);
                    //pop up wrong code

                }


            } else {
                Log.e("fail","fail");
                //pop up
//                Toast.makeText(Forget_password.this,"check your internet Connection or user, password are incorrect",Toast.LENGTH_LONG).show();
//                mEmailView.setError(getString(R.string.error_invalid_email));
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
                //   mPasswordView.requestFocus();
            }


        }

    }
    public void alert_message_verified(String title,String body){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(body);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i=new Intent(Verify.this,LoginActivity.class);
                        startActivity(i);
                        finishAffinity();


                        dialog.dismiss();
                    }

                });
        alertDialog.show();
    }
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
