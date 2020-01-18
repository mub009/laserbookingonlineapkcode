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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by quocnguyen on 03/08/2016.
 */
public class CustomListAdapter_clinicPage_worktimes extends ArrayAdapter<HashMap<String, String>> {

    ArrayList<HashMap<String, String>> CustomAdapter=new ArrayList<HashMap<String,String>>();
    Context context;
    int resource;
    String lng;
    public CustomListAdapter_clinicPage_worktimes(Context context, int layoutResourceId, ArrayList<HashMap<String, String>> CustomAdapter, String lng)
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
            convertView = layoutInflater.inflate(R.layout.clinic_page_working_time, null, true);

        }

    hashmap_Current = CustomAdapter.get(position);
    if(lng.equals("ar")){

        TextView Day_work = (TextView) convertView.findViewById(R.id.day_work);
        Day_work.setText(hashmap_Current.get("day_ar"));
        TextView Time_work=(TextView)convertView.findViewById(R.id.time_work);
        Time_work.setText(hashmap_Current.get("time_ar"));
    }

    else{
        TextView Day_work = (TextView) convertView.findViewById(R.id.day_work);
        Day_work.setText(hashmap_Current.get("day_en"));
        TextView Time_work=(TextView)convertView.findViewById(R.id.time_work);
        Time_work.setText(hashmap_Current.get("time_en"));}


        Log.e("view returned"," condtion worked");
    return convertView;


    }

    private void strikeThroughText(TextView previos_price){
        previos_price.setPaintFlags(previos_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

}
