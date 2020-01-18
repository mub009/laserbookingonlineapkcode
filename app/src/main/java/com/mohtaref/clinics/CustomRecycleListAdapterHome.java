package com.mohtaref.clinics;
/**
 * Created by Family on 12/23/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by quocnguyen on 03/08/2016.
 */
public class CustomRecycleListAdapterHome extends ArrayAdapter<HashMap<String, String>> {

    ArrayList<HashMap<String, String>> CustomAdapter=new ArrayList<HashMap<String,String>>();
    Context context;
    int resource;
    String lng;
public void addListItemToAdapter(ArrayList<HashMap<String, String>> Noffers){
    CustomAdapter.addAll(Noffers);
    this.notifyDataSetChanged();

}
    public CustomRecycleListAdapterHome(Context context, int layoutResourceId, ArrayList<HashMap<String, String>> CustomAdapter, String lng)
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
            convertView = layoutInflater.inflate(R.layout.list_item, null, true);

        }

        hashmap_Current=CustomAdapter.get(position);
        ImageView img = (ImageView) convertView.findViewById(R.id.imageView2);
        Picasso.get().load(hashmap_Current.get("img"))  .fit()
                .centerCrop().into(img);
                //.into(img);

        if(lng.equals("ar")){
            TextView service = (TextView) convertView.findViewById(R.id.service);
            service.setText(hashmap_Current.get("offerTitle_ar"));
            TextView persentage = (TextView) convertView.findViewById(R.id.persentage);
            persentage.setText("(%"+hashmap_Current.get("discount")+")");
            TextView distance = (TextView) convertView.findViewById(R.id.distance);
            if(hashmap_Current.get("distance")!=null){
                distance.setVisibility(View.VISIBLE);
                distance.setText(hashmap_Current.get("distance")+"كم ");

            }
            else{
                distance.setVisibility(View.GONE);
            }
            TextView address = (TextView) convertView.findViewById(R.id.address);
            address.setText(hashmap_Current.get("clinicName_ar"));
            TextView previos_price = (TextView) convertView.findViewById(R.id.previos_price);
            Double costd=Double.parseDouble(hashmap_Current.get("cost"));
            int value = costd.intValue();
            String cost=String.valueOf(value);
            previos_price.setText(cost+"ريال ");
            TextView price = (TextView) convertView.findViewById(R.id.price);
            price.setText(hashmap_Current.get("postCost")+"ريال ");
            strikeThroughText(previos_price);

        }
        else{
            TextView service = (TextView) convertView.findViewById(R.id.service);
            service.setText(hashmap_Current.get("offerTitle_en"));
            TextView persentage = (TextView) convertView.findViewById(R.id.persentage);
            persentage.setText("(%"+hashmap_Current.get("discount")+")");
            TextView distance = (TextView) convertView.findViewById(R.id.distance);
            if(hashmap_Current.get("distance")!=null){
                distance.setVisibility(View.VISIBLE);
                distance.setText(hashmap_Current.get("distance")+" Km");

            }
            else{
                distance.setVisibility(View.GONE);
            }
            TextView address = (TextView) convertView.findViewById(R.id.address);
            address.setText(hashmap_Current.get("clinicName_en"));
            TextView previos_price = (TextView) convertView.findViewById(R.id.previos_price);
            Double costd=Double.parseDouble(hashmap_Current.get("cost"));
            int value = costd.intValue();
            String cost=String.valueOf(value);
            previos_price.setText(cost+" Riyal");
            TextView price = (TextView) convertView.findViewById(R.id.price);
            price.setText(hashmap_Current.get("postCost")+" Riyal");
            strikeThroughText(previos_price);
        }



        return convertView;
    }

    private void strikeThroughText(TextView previos_price){
        previos_price.setPaintFlags(previos_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }
//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        ImageView bmImage;
//
//        public DownloadImageTask(ImageView bmImage) {
//            this.bmImage = bmImage;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//        }
//    }
}

