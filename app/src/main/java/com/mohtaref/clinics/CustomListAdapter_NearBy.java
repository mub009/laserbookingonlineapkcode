package com.mohtaref.clinics;
/**
 * Created by Family on 12/23/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by quocnguyen on 03/08/2016.
 */
public class CustomListAdapter_NearBy extends ArrayAdapter<HashMap<String, String>> {

    ArrayList<HashMap<String, String>> CustomAdapter=new ArrayList<HashMap<String,String>>();
    Context context;
    int resource;
    String lng;
    public CustomListAdapter_NearBy(Context context, int layoutResourceId, ArrayList<HashMap<String, String>> CustomAdapter,String lng)
    {
        super(context, layoutResourceId, CustomAdapter);
        this.CustomAdapter=CustomAdapter;
        this.context=context;
        this.resource=layoutResourceId;
        this.lng=lng;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HashMap<String, String> hashmap_Current;
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item_clinics_nearby, null, false);

        }

        hashmap_Current=CustomAdapter.get(position);
        ImageView img = (ImageView) convertView.findViewById(R.id.imageView2);
// it's slow because the size of image is big use image like img-m or img-s
        Picasso.get().load(hashmap_Current.get("cover")).fit().into(img);

        if(lng.equals("ar")) {

            TextView clinic_name = (TextView) convertView.findViewById(R.id.clinic_name);
            clinic_name.setText(hashmap_Current.get("clinicName_ar"));

            TextView distance = (TextView) convertView.findViewById(R.id.distance);
            TextView distancetext = (TextView) convertView.findViewById(R.id.textView10);
            if (hashmap_Current.get("distance") != null) {
                distance.setVisibility(View.VISIBLE);
                distance.setText(hashmap_Current.get("distance") + "كم ");
                distancetext.setVisibility(View.VISIBLE);
            } else {
                distance.setVisibility(View.GONE);
                distancetext.setVisibility(View.GONE);
            }
            TextView address = (TextView) convertView.findViewById(R.id.clinic_address);

            if (hashmap_Current.get("address_ar") != null) {

            address.setText("المدينة: "+hashmap_Current.get("address_ar"));}
            else
                address.setText(""+"المدينة: ");
        }
        else{
            TextView clinic_name = (TextView) convertView.findViewById(R.id.clinic_name);
            clinic_name.setText(hashmap_Current.get("clinicName_en"));

            TextView distance = (TextView) convertView.findViewById(R.id.distance);
            TextView distancetext = (TextView) convertView.findViewById(R.id.textView10);
            if (hashmap_Current.get("distance") != null) {
                distance.setVisibility(View.VISIBLE);
                distance.setText(hashmap_Current.get("distance") + " Km");
                distancetext.setVisibility(View.VISIBLE);
            } else {
                distance.setVisibility(View.GONE);
                distancetext.setVisibility(View.GONE);
            }
            TextView address = (TextView) convertView.findViewById(R.id.clinic_address);

            if (hashmap_Current.get("address_en") != null) {

            address.setText("City: " + hashmap_Current.get("address_en"));}
            else
                address.setText("City: ");
        }

        return convertView;
    }

    private void strikeThroughText(TextView previos_price){
        previos_price.setPaintFlags(previos_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
