package com.mohtaref.clinics;

import android.util.Log;

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

public class HttpHandlerPostToken {

    private static final String TAG = HttpHandlerPostToken.class.getSimpleName();

    public HttpHandlerPostToken() {
    }

    public String makeServiceCall(String reqUrl,String tok,String lng,String lat) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Client-Auth-Token", "Bearer"+" "+tok);

            if(lng!=null&&lat!=null){
              JSONObject jsonParam = new JSONObject();
              jsonParam.put("lng",lng );
              jsonParam.put("lat",lat );


              DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
              Log.e("Before sending", "1" + jsonParam.toString());
              wr.writeBytes(jsonParam.toString());
          }
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
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


