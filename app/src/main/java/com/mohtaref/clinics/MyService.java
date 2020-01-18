package com.mohtaref.clinics;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

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

public class MyService extends FirebaseMessagingService {

    String notification_token;
    String lang;
    final private String base_url = "https://laserbookingonline.com/manager/APIs/clientV2/";
    public MyService() {
    }
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d("new Token", "Refreshed token: " + token);
         lang = Locale.getDefault().getDisplayLanguage();
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        SharedPreferences.Editor editor=getSharedPreferences("notification_laser", Activity.MODE_PRIVATE).edit();
        editor.putString("token_notify",token);
        editor.apply();
        SharedPreferences pref = getSharedPreferences("user", Activity.MODE_PRIVATE);
        if(pref.getString("data", "")!=null&&!pref.getString("data", "").equals(""))
        new newNotrifcationToken(token,lang).execute();
    }
    public class newNotrifcationToken extends AsyncTask<Void, Void, String> {
        final String token;
        final String lang;

        //0531042553/123456  phone + pass

        public newNotrifcationToken(String notify_tok, String current_lang) {
            token = notify_tok;
            lang = current_lang;

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
                URL url = new URL(base_url+"updateClientTokenLang"); //Enter URL here
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setDoInput(true);// here you are setting the `Content-Type` for the data you are sending which is `application/json`
                httpURLConnection.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("token",token );
                Log.e("lang","is langs: "+lang);
                jsonParam.put("lang", lang);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                Log.e("Before sending", "" + jsonParam.toString());
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
//            JSONObject jsob= null;
//            if(jsonStr!=null){
//                try {
//                    jsob = new JSONObject(jsonStr);
//                    User_register_Id= jsob.getString("clientId");
//                if(jsob.getString("token")!=null&& !jsob.getString("token").equals("")){
//                    verify_check=true;
//                }
//                else
//                    verify_check=false;
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }


            Log.e("right here ", "Response from url here:" + jsonStr);





            return jsonStr;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);






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

}
