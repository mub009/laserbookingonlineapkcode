package com.mohtaref.clinics;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ExpandableListAdapterClinicPage extends BaseExpandableListAdapter {

    private Context _context;
    private ArrayList<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private ArrayList<ArrayList<HashMap<String,String>>>  _listDataChild;
    private String lng;

    public ExpandableListAdapterClinicPage(Context context, ArrayList<String>listDataHeader,
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
            convertView = infalInflater.inflate(R.layout.list_item_clinic_page_services_child_exp, null);
        }
        if(lng.equals("ar")){
            TextView txtListChildService = (TextView) convertView
                    .findViewById(R.id.service);

            txtListChildService.setText(childText.get("serviceName_ar"));
            TextView txtListChildPrice = (TextView) convertView
                    .findViewById(R.id.price2);
            txtListChildPrice.setText(childText.get("postCost")+"ريال ");
            View line = convertView.findViewById(R.id.viewLine);

            if(isLastChild){
                line.setVisibility(View.GONE);
            } else {
                line.setVisibility(View.VISIBLE);
            }
        }
        else{
            TextView txtListChildService = (TextView) convertView
                    .findViewById(R.id.service);

            txtListChildService.setText(childText.get("serviceName_en"));
            TextView txtListChildPrice = (TextView) convertView
                    .findViewById(R.id.price2);
            txtListChildPrice.setText(childText.get("postCost")+" Riayl");
            View line = convertView.findViewById(R.id.viewLine);

            if(isLastChild){
                line.setVisibility(View.GONE);
            } else {
                line.setVisibility(View.VISIBLE);
            }
        }


//        if(isLastChild){
//            convertView = Inflater.inflate(R.layout.list_item_with_button, null);
//        } else {
//            convertView = Inflater.inflate(R.layout.list_item, null);
//        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //Log.e("child count","is "+_listDataChild.get(groupPosition).size());
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
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.exp_list_clinic_page, null);
        }

        TextView ClinicName = (TextView) convertView
                .findViewById(R.id.serviceName);
      //  lblListHeader.setTypeface(null, Typeface.BOLD);
        ClinicName.setText(headerTitle);


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