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
public class CustomListAdapterAppointments extends ArrayAdapter<HashMap<String, String>> {

    ArrayList<HashMap<String, String>> CustomAdapter=new ArrayList<HashMap<String,String>>();
    Context context;
    int resource;
    String lng;
    public CustomListAdapterAppointments(Context context, int layoutResourceId, ArrayList<HashMap<String, String>> CustomAdapter, String lng)
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
            convertView = layoutInflater.inflate(R.layout.reservations_list_item, null, true);

        }

        hashmap_Current=CustomAdapter.get(position);

        ImageView profile_image=(ImageView) convertView.findViewById(R.id.profile_image);
        Picasso.get().load(hashmap_Current.get("logo")).into(profile_image);
        TextView status = (TextView) convertView.findViewById(R.id.status);
        String statusvalue=hashmap_Current.get("status");

        if(lng.equals("ar")){
            TextView service_name = (TextView) convertView.findViewById(R.id.service_name);
            service_name.setText(hashmap_Current.get("serviceName_ar"));
            TextView clinicName = (TextView) convertView.findViewById(R.id.clinicName);
            clinicName.setText(hashmap_Current.get("clinicName_ar"));
            status.setText(getStringResourceByName(statusvalue));

        }
        else{
            TextView service_name = (TextView) convertView.findViewById(R.id.service_name);
            service_name.setText(hashmap_Current.get("serviceName_en"));
            TextView clinicName = (TextView) convertView.findViewById(R.id.clinicName);
            clinicName.setText(hashmap_Current.get("clinicName_en"));
            status.setText(hashmap_Current.get("status"));

        }




        String[] separated = hashmap_Current.get("appointmentDate").split(" ");
        String dateofApp=separated[0];
        String time=separated[1];
        String[] separatedDate = dateofApp.split("-");
        String month_name = "";
        try {
            month_name= getMonth(hashmap_Current.get("appointmentDate"),lng);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String convertedTime ="";
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a",new Locale(lng));
            SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = parseFormat.parse(time);
            convertedTime=displayFormat.format(date);
            Log.e("time is :","convertedTime : "+convertedTime);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        TextView res_day = (TextView) convertView.findViewById(R.id.res_day);
        res_day.setText(separatedDate[2]);
        TextView res_month = (TextView) convertView.findViewById(R.id.res_month);
        res_month.setText(month_name);
        TextView res_time = (TextView) convertView.findViewById(R.id.res_time);
        res_time.setText(convertedTime);

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
    private String getStringResourceByName(String aString) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(aString, "string", packageName);
        return context.getString(resId);
    }
}
