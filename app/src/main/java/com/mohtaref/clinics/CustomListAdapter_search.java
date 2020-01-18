package com.mohtaref.clinics;
/**
 * Created by Family on 12/23/2017.
 */

import android.app.Activity;
import android.content.Context;
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
public class CustomListAdapter_search extends ArrayAdapter<HashMap<String, String>> {

    ArrayList<HashMap<String, String>> CustomAdapter=new ArrayList<HashMap<String,String>>();
    Context context;
    int resource;
    String lng;
    public CustomListAdapter_search(Context context, int layoutResourceId, ArrayList<HashMap<String, String>> CustomAdapter,String lng)
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
            convertView = layoutInflater.inflate(R.layout.list_item_search, null, true);

        }
    if(CustomAdapter.get(position)!=null){
        hashmap_Current = CustomAdapter.get(position);
        if(lng.equals("ar")){
            TextView service = (TextView) convertView.findViewById(R.id.result_data);
            service.setText(hashmap_Current.get("clinicName_ar"));
        }
        else{
            TextView service = (TextView) convertView.findViewById(R.id.result_data);
            service.setText(hashmap_Current.get("clinicName_en"));
        }
    }


    return convertView;


    }



}
