package com.mohtaref.clinics;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private ArrayList<HashMap<String, String>> _listDataHeader; // header titles
    // child data in format of header title, child title
    private ArrayList<ArrayList<HashMap<String,String>>>  _listDataChild;
    private String lng;
    public ExpandableListAdapter(Context context,   ArrayList<HashMap<String, String>>  listDataHeader,
                                 ArrayList<ArrayList<HashMap<String,String>>> listChildData,String lng) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.lng=lng;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
       return _listDataChild.get(groupPosition).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final HashMap<String,String> childText = (HashMap<String,String>) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_clinics_services, null);
        }
        if(lng.equals("ar")){
            TextView txtListChildService = (TextView) convertView
                    .findViewById(R.id.service);

            txtListChildService.setText(childText.get("serviceName_ar"));
            TextView txtListChildPrice = (TextView) convertView
                    .findViewById(R.id.price2);
            txtListChildPrice.setText(childText.get("postCost")+"ريال ");
            View line = convertView.findViewById(R.id.viewLine);
            View line2 = convertView.findViewById(R.id.viewLine2);

            View v = convertView.findViewById(R.id.more);
            if(isLastChild){
                v.setVisibility(View.VISIBLE);
            } else {
                v.setVisibility(View.GONE);
            }
            if(isLastChild){
                line.setVisibility(View.GONE);
            } else {
                line.setVisibility(View.VISIBLE);
            }
            if(isLastChild){
                line2.setVisibility(View.VISIBLE);
            } else {
                line2.setVisibility(View.GONE);
            }
//        if(isLastChild){
//            convertView = Inflater.inflate(R.layout.list_item_with_button, null);
//        } else {
//            convertView = Inflater.inflate(R.layout.list_item, null);
//        }
        }
        else{
            TextView txtListChildService = (TextView) convertView
                    .findViewById(R.id.service);

            txtListChildService.setText(childText.get("serviceName_en"));
            TextView txtListChildPrice = (TextView) convertView
                    .findViewById(R.id.price2);
            txtListChildPrice.setText(childText.get("postCost")+" Riayl");
            View line = convertView.findViewById(R.id.viewLine);
            View line2 = convertView.findViewById(R.id.viewLine2);

            View v = convertView.findViewById(R.id.more);
            if(isLastChild){
                v.setVisibility(View.VISIBLE);
            } else {
                v.setVisibility(View.GONE);
            }
            if(isLastChild){
                line.setVisibility(View.GONE);
            } else {
                line.setVisibility(View.VISIBLE);
            }
            if(isLastChild){
                line2.setVisibility(View.VISIBLE);
            } else {
                line2.setVisibility(View.GONE);
            }
//        if(isLastChild){
//            convertView = Inflater.inflate(R.layout.list_item_with_button, null);
//        } else {
//            convertView = Inflater.inflate(R.layout.list_item, null);
//        }
        }


        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Log.e("child count","is "+_listDataChild.get(groupPosition).size());
        return _listDataChild.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        HashMap<String,String> headerTitle = (HashMap<String,String>) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_clinics, null);
        }
        ImageView img = (ImageView) convertView.findViewById(R.id.profile_image);
// it's slow because the size of image is big use image like img-m or img-s
        Picasso.get().load(headerTitle.get("cover")).fit().into(img);
if(lng.equals("ar")){
    TextView ClinicName = (TextView) convertView
            .findViewById(R.id.clinicName);
    //  lblListHeader.setTypeface(null, Typeface.BOLD);
    ClinicName.setText(headerTitle.get("clinicName_ar"));
    TextView address = (TextView) convertView
            .findViewById(R.id.clinicAddress);
    address.setText(headerTitle.get("address_ar"));
    TextView distance = (TextView) convertView.findViewById(R.id.distance);

    if(headerTitle.get("distance")!=null){
        distance.setVisibility(View.VISIBLE);
        distance.setText(headerTitle.get("distance")+"كم ");
    }
    else{
        distance.setVisibility(View.GONE);
    }
}
    else{
    TextView ClinicName = (TextView) convertView
            .findViewById(R.id.clinicName);
    //  lblListHeader.setTypeface(null, Typeface.BOLD);
    ClinicName.setText(headerTitle.get("clinicName_en"));
    TextView address = (TextView) convertView
            .findViewById(R.id.clinicAddress);
    address.setText(headerTitle.get("address_en"));
    TextView distance = (TextView) convertView.findViewById(R.id.distance);

    if(headerTitle.get("distance")!=null){
        distance.setVisibility(View.VISIBLE);
        distance.setText(headerTitle.get("distance")+" Km");
    }
    else{
        distance.setVisibility(View.GONE);
    }
}

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}