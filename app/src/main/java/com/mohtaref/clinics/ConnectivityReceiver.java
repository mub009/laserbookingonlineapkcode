package com.mohtaref.clinics;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class ConnectivityReceiver extends BroadcastReceiver {

    public static ConnectivityReceiverListener connectivityReceiverListener;
    boolean connected=false;
    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

//        ConnectivityManager cm = (ConnectivityManager) context
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        boolean isConnected = activeNetwork != null
//                && activeNetwork.isConnectedOrConnecting();
//
//        if (connectivityReceiverListener != null) {
//            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
//        }
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            boolean noConnectivity = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false
            );
            if (noConnectivity){
           //     someMethodThatUsesActivity(getActivity(context));
             //   Toast.makeText(context, context.getResources().getString(R.string.nointernet)+" "+ context.getResources().getString(R.string.checkinternet), Toast.LENGTH_LONG).show();
                Intent i = new Intent(context, popupDialog.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

//             alert_message_net(context.getResources().getString(R.string.nointernet),context.getResources().getString(R.string.checkinternet),context,connected);
////                new Handler().postDelayed(new Runnable() {
////                    public void run() {
////                       // disableThatUsesActivity(getActivity(context));
////
////                      //  getActivity(context).finishAffinity();
////                        System.exit(0);
////                    }
////                },  1000);

            }
            else{
              //  Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
                connected=true;


            }
        }
    }
    public static Activity getActivity(Context context) {
        if (context == null) return null;
        if (context instanceof Activity) return (Activity) context;
        if (context instanceof ContextWrapper) return getActivity(((ContextWrapper)context).getBaseContext());
        return null;
    }
//
//    public static boolean isConnected() {
//        ConnectivityManager
//                cm = (ConnectivityManager) MyApplication.getInstance().getApplicationContext()
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        return activeNetwork != null
//                && activeNetwork.isConnectedOrConnecting();
//    }

    public void someMethodThatUsesActivity(Activity myActivityReference) {
        myActivityReference.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void disableThatUsesActivity(Activity myActivityReference) {
        myActivityReference.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
    public void alert_message_net(String title, String body, Context context, boolean connected) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(new ContextThemeWrapper(context,R.style.myDialog));
        alertDialog.setTitle(title);
        alertDialog.setMessage(body);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog=alertDialog.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(connected)
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 1000);
                    Log.e("is connected","ture");
                }
            }
        });

    }
}