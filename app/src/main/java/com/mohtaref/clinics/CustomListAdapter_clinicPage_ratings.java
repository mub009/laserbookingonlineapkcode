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
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by quocnguyen on 03/08/2016.
 */
public class CustomListAdapter_clinicPage_ratings extends ArrayAdapter<HashMap<String, String>> {

    ArrayList<HashMap<String, String>> CustomAdapter=new ArrayList<HashMap<String,String>>();
    Context context;
    int resource;
    String lng;
    public CustomListAdapter_clinicPage_ratings(Context context, int layoutResourceId, ArrayList<HashMap<String, String>> CustomAdapter, String lng)
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
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item_rating_clinic_page, null, true);

        }

        hashmap_Current = CustomAdapter.get(position);


        TextView clientName = (TextView) convertView.findViewById(R.id.raterName);
        clientName.setText(hashmap_Current.get("clientName"));
        TextView comment = (TextView) convertView.findViewById(R.id.rating_comment);
        comment.setText(hashmap_Current.get("comment"));
        RatingBar ratingScore=(RatingBar) convertView.findViewById(R.id.ratingScore);
        ratingScore.setRating(Float.parseFloat(hashmap_Current.get("score")));

        return convertView;

    }


    private void strikeThroughText(TextView previos_price){
        previos_price.setPaintFlags(previos_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

}
