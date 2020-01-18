package com.mohtaref.clinics;
/**
 * Created by Family on 12/23/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by quocnguyen on 03/08/2016.
 */
public class CustomListAdapter_time extends ArrayAdapter<String> {

    ArrayList<String> CustomAdapter=new ArrayList<String>();
    Context context;
    int resource;

    public CustomListAdapter_time(Context context, int layoutResourceId, ArrayList<String> CustomAdapter)
    {
        super(context, layoutResourceId, CustomAdapter);
        this.CustomAdapter=CustomAdapter;
        this.context=context;
        this.resource=layoutResourceId;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String g=CustomAdapter.get(position);
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item_time, null, true);

        }
        TextView Timeview = (TextView) convertView.findViewById(R.id.time);
        Log.e("setText","is "+CustomAdapter.get(position));
        Timeview.setText(g);




    return convertView;


    }



}
