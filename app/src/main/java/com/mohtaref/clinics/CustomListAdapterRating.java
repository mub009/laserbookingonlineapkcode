package com.mohtaref.clinics;
/**
 * Created by Family on 12/23/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by quocnguyen on 03/08/2016.
 */
public class CustomListAdapterRating extends ArrayAdapter<HashMap<String, String>> {

    ArrayList<HashMap<String, String>> CustomAdapter=new ArrayList<HashMap<String,String>>();
    Context context;
    int resource;
    String lng;
    public CustomListAdapterRating(Context context, int layoutResourceId, ArrayList<HashMap<String, String>> CustomAdapter, String lng)
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
            convertView = layoutInflater.inflate(R.layout.rating_list_item, null, true);

        }

        hashmap_Current=CustomAdapter.get(position);
        ImageView profile_image=(ImageView) convertView.findViewById(R.id.profile_image);
        Picasso.get().load(hashmap_Current.get("logo")).into(profile_image);

        if(lng.equals("ar")){
            TextView reservastionId = (TextView) convertView.findViewById(R.id.reservastionId);
            reservastionId.setText(hashmap_Current.get("reservationId")+"الحجز# ");
            TextView service_name = (TextView) convertView.findViewById(R.id.service_name);
            service_name.setText(hashmap_Current.get("serviceName_ar"));
            TextView clinicName = (TextView) convertView.findViewById(R.id.clinicName);
            clinicName.setText(hashmap_Current.get("clinicName_ar"));
        }
        else{
            TextView reservastionId = (TextView) convertView.findViewById(R.id.reservastionId);
            reservastionId.setText("Reservation#"+hashmap_Current.get("reservationId"));
            TextView service_name = (TextView) convertView.findViewById(R.id.service_name);
            service_name.setText(hashmap_Current.get("serviceName_en"));
            TextView clinicName = (TextView) convertView.findViewById(R.id.clinicName);
            clinicName.setText(hashmap_Current.get("clinicName_en"));
        }






        String[] separated = hashmap_Current.get("appointmentDate").split(" ");
        String dateofApp=separated[0];
        String time=separated[1];
        String[] separatedDate = dateofApp.split("-");
//        String month_name = "";
//        try {
//            month_name= getMonth(hashmap_Current.get("appointmentDate"));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        String convertedTime ="";
//        try {
//            SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
//            SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm:ss");
//            Date date = parseFormat.parse(time);
//            convertedTime=displayFormat.format(date);
//            Log.e("time is :","convertedTime : "+convertedTime);
//        } catch (final ParseException e) {
//            e.printStackTrace();
//        }
        TextView app_date = (TextView) convertView.findViewById(R.id.app_date);
        try {
            String dateString = hashmap_Current.get("appointmentDate");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = sdf.parse(dateString);
            Log.e("date is","dis "+date);
            long startDate = date.getTime();

            PrettyTime prettyTime = new PrettyTime(new Locale(lng));
            String ago = prettyTime.format(new Date(startDate));
            Log.e("ago is","dis "+ago);

            app_date.setText(ago);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return convertView;
    }

    private void strikeThroughText(TextView previos_price){
        previos_price.setPaintFlags(previos_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }
    private static String getMonth(String date,String lng) throws ParseException{
        Date d = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMMM",new Locale(lng)).format(cal.getTime());
        Log.e("month name is;","this ;"+monthName);
        return monthName;
    }
}
