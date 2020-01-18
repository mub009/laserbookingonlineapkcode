package com.mohtaref.clinics;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Checkout extends AppCompatActivity{
    ArrayList<HashMap<String, String>> bank_accounts_list;
    ArrayList<String> bank_accounts_ar;
    ArrayList<String> bank_accounts_en;

    ListView time_list_view;
    HashMap<String, String> selected_bank;
    Handler handler;
    String dateandtime;
    String employeeId;
    HashMap<String, String> offer;
    Calendar calendar;
    HashMap<String, String> provider_hash;
    String provider_list_en;
    String provider_list_ar;
    private static final String TAG = HttpHandlerPostToken.class.getSimpleName();

    final private String base_url = "https://laserbookingonline.com/manager/APIs/clientV2/";
    HashMap<String, String> selected_provider;
    HashMap<String, String> creditPaymentData;
    String token;
    String userid;
    String selected_payment_type;
    TextView service_checkout;
    TextView provider_checkout;
    TextView gender_checkout;
    TextView nationality_checkout;
    TextView date_checkout;
    TextView duration_checkout;
    TextView cost_checkout;
    TextView discount_checkout;
    TextView total_checkout;
    TextView terms_checkout;
    CheckedTextView cash;
    CheckedTextView transfer;
    CheckedTextView credit;
    CheckedTextView mada;
    LinearLayout transfer_form;
    LinearLayout credit_form;
    LinearLayout mada_form;
    CheckBox checkBox2;

    EditText depostDate;
    EditText depositorName;
    EditText amount;

    EditText credit_firstName;

    EditText credit_lastName;
    EditText credit_email;
    EditText mada_firstName;
    boolean alert_show = true;
    EditText mada_lastName;
    EditText mada_email;
    String firstName;
    String lastName;
    String email;
    String paid = "";
    HashMap<String, String> termsdata;
    String PaymentType;
    boolean creidt_method = false;
    Button checkout_btn;
    Timer timer;
    ProgressDialog pd;
    boolean isguest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadlocal();
        checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        checkout_btn = (Button) findViewById(R.id.checkout_btn);
        bank_accounts_list = new ArrayList<>();
        bank_accounts_ar = new ArrayList<>();
        bank_accounts_en = new ArrayList<>();
        final Calendar myCalendar = Calendar.getInstance();

        depostDate = (EditText) findViewById(R.id.depost_date);
        depositorName = (EditText) findViewById(R.id.depositorName);
        amount = (EditText) findViewById(R.id.amount);
        SharedPreferences prefg = getSharedPreferences("user", Activity.MODE_PRIVATE);
        String data = prefg.getString("data", "");
        try {
            JSONObject userdata = new JSONObject(data);
            isguest = userdata.getBoolean("isGuest");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("user is guest :", "" + isguest);
        DatePickerDialog.OnDateSetListener datepicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(myCalendar);
            }

        };

        DatePickerDialog.OnDateSetListener finalDate = datepicker;
        depostDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog date_dialog = new DatePickerDialog(Checkout.this, finalDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                //     date_dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                date_dialog.show();

            }
        });
        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkout_btn.setEnabled(true);
                } else {
                    checkout_btn.setEnabled(false);

                }
            }
        });

        checkout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isguest) {
                    alert_message_guest(getResources().getString(R.string.guestErrTitle), getResources().getString(R.string.guestErrDesc));
                    return;
                } else {
                    if (credit.isChecked() || mada.isChecked()) {
                        credit_firstName = (EditText) findViewById(R.id.first_credit);
                        credit_lastName = (EditText) findViewById(R.id.last_credit);
                        credit_email = (EditText) findViewById(R.id.email_credit);

                        mada_firstName = (EditText) findViewById(R.id.first_mada);
                        mada_lastName = (EditText) findViewById(R.id.last_mada);
                        mada_email = (EditText) findViewById(R.id.email_mada);
                        if (credit.isChecked()) {
                            firstName = credit_firstName.getText().toString();
                            lastName = credit_lastName.getText().toString();
                            email = credit_email.getText().toString();
                            selected_payment_type = "credit";
                        }
                        if (mada.isChecked()) {

                            firstName = mada_firstName.getText().toString();
                            lastName = mada_lastName.getText().toString();
                            email = mada_email.getText().toString();
                            selected_payment_type = "mada";

                        }
                        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) && !TextUtils.isEmpty(email) && isEmailValid(email)) {
                            new ReservationCredit().execute();
                        } else {
                            Log.e("invalid ", "email");
                        }             //   new Reservation().execute();
                    } else if (transfer.isChecked()) {

                    } else {

                        //  accountId,depositDate,depositorName,amount

                        // selected_payment_type="cash";
                        new Reservation().execute();
                    }
                }
            }
        });
        dateandtime = getIntent().getStringExtra("appointmentDate");
        offer = (HashMap<String, String>) getIntent().getExtras().getSerializable("offer");
        selected_provider = (HashMap<String, String>) getIntent().getExtras().getSerializable("provider");
        //    Log.e("welcome to timePicker","here is your employeeId "+employeeId);
        Log.e("welcome to timePicker", "here is your offer " + offer.toString());
        Log.e("welcome to timePicker", "here is your date " + dateandtime);
        //PaymentType=offer.get()
        termsdata = new HashMap<String, String>();
        creditPaymentData = new HashMap<String, String>();
        provider_hash = new HashMap<String, String>();
        new termstext().execute();
        new checkoutInfo().execute();
        new GetBankAccounts().execute();
    }

    public void checkout_complete(View view) {

    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null&&netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null&& mobile.isConnectedOrConnecting()) || (wifi != null&&wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }

    public void alert_message_net(String title, String body,Context context) {
        android.support.v7.app.AlertDialog.Builder alertDialog=new android.support.v7.app.AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(body);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        android.support.v7.app.AlertDialog dialog=alertDialog.create();
        dialog.show();
        dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected(context))
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();


                            finish();
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);


                        }
                    }, 1000);
                    Log.e("is connected","ture");
                }
            }
        });

    }
    private static class myCustomAlertDialog extends AlertDialog {

        protected myCustomAlertDialog(Context context) {
            super(context);
            dismiss();

        }

    }

    public void creditcall() {

        if (creditPaymentData.get("redirect_url") != null) {
            WebView wv = new WebView(Checkout.this) {
                @Override
                public boolean onCheckIsTextEditor() {
                    return true;
                }
            };

            AlertDialog.Builder alert = new AlertDialog.Builder(Checkout.this);
            final AlertDialog alert_dialog = alert.create();

            //alert.setTitle("");

            wv.setWebViewClient(new WebViewClient());
            WebSettings ws = wv.getSettings();
            wv.setWebViewClient(new WebViewClient());
            ws.setJavaScriptEnabled(true);
            Log.e("redirect to :","this "+creditPaymentData.get("redirect_url").trim());
            wv.loadUrl(creditPaymentData.get("redirect_url").trim());

            handler = new Handler();
            timer = new Timer();
            TimerTask doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                OrderPaidCheck performBackgroundTask = new OrderPaidCheck();
                                // PerformBackgroundTask this class is the class that extends AsynchTask
                                new OrderPaidCheck().execute();
                                Log.e("paid is  valid of alert", "hop = " + paid);
                                if (paid.trim().equals("valid")) {
                                    alert_show = true;
                                } else if (paid.trim().equals("paid")) {
                                    alert_show = true;
                                    timer.cancel();  // Terminates this timer, discarding any currently scheduled tasks.
                                    timer.purge();
                                    alert_message(getResources().getString(R.string.paymentSuccess), getResources().getString(R.string.paymentSuccessDesc), paid.trim());


                                } else if (paid.trim().equals("pending_payment")) {
                                    alert_show = true;
                                    timer.cancel();  // Terminates this timer, discarding any currently scheduled tasks.
                                    timer.purge();
                                    //  Log.e("paid value is =","in pending is "+paid);
                                    alert_message(getResources().getString(R.string.transactionApprovalTitle), getResources().getString(R.string.transactionApprovalDesc), paid.trim());

                                } else {
                                    alert_show = false;
                                    timer.cancel();  // Terminates this timer, discarding any currently scheduled tasks.
                                    timer.purge();
                                    paid = "error";
                                    alert_message(getResources().getString(R.string.paymentFailed), getResources().getString(R.string.pinError), paid.trim());

                                }
                                //       Log.e("paid value is =","dis "+paid);


                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                            }
                        }
                    });
                }
            };
            timer.schedule(doAsynchronousTask, 0, 5000); //execute in every 50000 ms
            alert.setView(wv);
            alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    timer.cancel();  // Terminates this timer, discarding any currently scheduled tasks.
                    timer.purge();
                    dialog.dismiss();

                }
            });
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    timer.cancel();  // Terminates this timer, discarding any currently scheduled tasks.
                    timer.purge();
                    dialogInterface.dismiss();

                }
            });

            alert.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Log.e("back pressed on alert", "yep");

                        timer.cancel();  // Terminates this timer, discarding any currently scheduled tasks.
                        timer.purge();
                        arg0.dismiss();
                        //finish();

                    }
                    return true;
                }
            });
            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    Log.e("gg", "Gg");
                    timer.cancel();  // Terminates this timer, discarding any currently scheduled tasks.
                    timer.purge();

                }
            });
            alert_dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Log.e("gg", "Gg");
                    timer.cancel();  // Terminates this timer, discarding any currently scheduled tasks.
                    timer.purge();
                }
            });
            alert_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    Log.e("gg", "Gg");
                    timer.cancel();  // Terminates this timer, discarding any currently scheduled tasks.
                    timer.purge();
                }
            });
            if (alert_show)
                alert.show();


        }


    }

    public void alert_message(String title, String body, String status) {
        AlertDialog alertDialog = new AlertDialog.Builder(Checkout.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(body);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (paid.trim().equals("paid")) {
                            Intent i = new Intent(Checkout.this, SuccessPage.class);
                            startActivity(i);
                            finishAffinity();
                        }
                        if (paid.trim().equals("pending_payment")) {
                            Intent i = new Intent(Checkout.this, HomePage.class);
                            startActivity(i);
                            finishAffinity();
                            System.exit(0);

                        }
                        if (paid.trim().equals("error")) {
                            Intent i = new Intent(Checkout.this, HomePage.class);
                            startActivity(i);
                            finishAffinity();
                            System.exit(0);

                        }


                        dialog.dismiss();
                    }

                });
        alertDialog.show();
    }

    public void alert_message_guest(String title, String body) {
        AlertDialog alertDialog = new AlertDialog.Builder(Checkout.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(body);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
        alertDialog.show();
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void searchPage(View view) {
        Intent i = new Intent(this, SearchActivity.class);
        startActivity(i);
        overridePendingTransition(0, 0);

    }

    public void home_page(View view) {
        Intent i = new Intent(this, HomePage.class);
        startActivity(i);
        finishAffinity();
        overridePendingTransition(0, 0);

    }

    public void rating_page(View view) {
        Intent i = new Intent(this, Rating.class);
        startActivity(i);
        overridePendingTransition(0, 0);

    }

    public void my_appointments(View view) {
        Intent i = new Intent(this, MyAppointments.class);
        startActivity(i);
        overridePendingTransition(0, 0);

    }

    public void profile(View view) {
        Intent i = new Intent(this, Profile.class);
        startActivity(i);
        overridePendingTransition(0, 0);

    }


    public void About_us(View view) {
        Intent i = new Intent(this, About_us.class);
        startActivity(i);
        overridePendingTransition(0, 0);

    }

    public void Privacy_policy_page(View view) {
        Uri uri = Uri.parse("https://laserbookingonline.com/Privacy_Policy/"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }

    public void Terms_page(View view) {
        Intent i = new Intent(this, Terms.class);
        startActivity(i);
        overridePendingTransition(0, 0);

    }

    public void Register_Your_Clinic(View view) {
        SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lng = pref.getString("Mylang", "");
        Log.e("language", lng);
        if (lng.equals("ar")) {
            Uri uri = Uri.parse("https://laserbookingonline.com/register.php?lang=ar"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else {
            Uri uri = Uri.parse("https://laserbookingonline.com/register.php?lang=en"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }


    }

    public void callNumPhone(View view) {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + getResources().getString(R.string.phone_number)));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
        overridePendingTransition(0, 0);

    }
    public void FaceBook(View view) {
        Uri uri = Uri.parse("https://www.facebook.com/bookinglaser/"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }
    public void Snapchat(View view) {
        Uri uri = Uri.parse("https://www.snapchat.com/add/laserbooking"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }
    public void Twitter(View view) {
        Uri uri = Uri.parse("https://twitter.com/bookinglaser"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }
    public void Instagram(View view) {
        Uri uri = Uri.parse("https://www.instagram.com/laser.booking/"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    public void setLocale(String lang) {
        // get language return 2 first letters lower case of the language //// in the other hand get display language return the name of the language
        String CurrentLang = Locale.getDefault().getDisplayLanguage();
        String Applang= Resources.getSystem().getConfiguration().locale.getDisplayLanguage();




        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        conf.setLocale(new Locale(lang));
        res.updateConfiguration(conf, dm);
        SharedPreferences.Editor editor=getSharedPreferences("Settings", Activity.MODE_PRIVATE).edit();
        editor.putString("Mylang",lang);
        editor.apply();
        if(!CurrentLang.equals(Applang)){
            recreate(); }



    }
    public void loadlocal(){
        SharedPreferences pref=getSharedPreferences("Settings",Activity.MODE_PRIVATE);
        String lng=pref.getString("Mylang","");
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        if(lng.equals("ar")){
            pd.setMessage("جاري التحميل");

        }
        else{
            pd.setMessage("Loading");

        }

        pd.show();
     //   setLocale(lng);
    }

    public void setLocale_en(View view ) {
        String lang="en";
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor=getSharedPreferences("Settings", Activity.MODE_PRIVATE).edit();
        editor.putString("Mylang",lang);
        editor.apply();
        Intent intent=getIntent();
        finishAffinity();
        startActivity(intent);
        overridePendingTransition(0, 0);

    }
    public void setLocale_ar(View view) {
        String lang="ar";
        Locale myLocale = new Locale(lang);
        //saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor=getSharedPreferences("Settings", Activity.MODE_PRIVATE).edit();
        editor.putString("Mylang",lang);
        editor.apply();
        Intent intent=getIntent();
        finishAffinity();
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    public class checkoutInfo extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // getLastLocation();
//            categories_list.clear();

            SharedPreferences pref = getSharedPreferences("user", Activity.MODE_PRIVATE);
            String data = pref.getString("data", "");
            try {
                JSONObject userdata = new JSONObject(data);
                token = userdata.getString("token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("user token is :", token);


//            "categoryId":"1", "lat": "", "lng": "","pager":"1"
//


        }

        @Override
        protected Void doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();

            // Making a request to url and getting response
            String urlget = base_url + "checkoutDetails";
            String jsonStr = null;

            // String jsonStr = sh.makeServiceCall(urlget, token, "", "");
            // String response = null;
            try {
                URL url = new URL(urlget);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Client-Auth-Token", "Bearer"+" "+token);
                // String offerid=ClinicData.get("offerId");
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("clinicId",offer.get("clinicId") );
                jsonParam.put("serviceId",offer.get("serviceId"));
                jsonParam.put("employeeId",employeeId);
                jsonParam.put("appointmentDate",dateandtime);
                jsonParam.put("totalDuration",offer.get("duration"));

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                Log.e("Before sending", "1" + jsonParam.toString());
                wr.writeBytes(jsonParam.toString());

                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                jsonStr = convertStreamToString(in);
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
            //   return response;
            Log.e("gg mynigga","yeaah");
            Log.e("is offers :", "Response from url countries: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject c = new JSONObject(jsonStr);

                    //JSONObject service=c.getJSONObject("service");
                    JSONObject provider=c.getJSONObject("provider");

                    String nationalityName_en = provider.getString("nationalityName_en");
                    String nationalityName_ar = provider.getString("nationalityName_ar");
                    String employeeId = provider.getString("employeeId");
                    String employeeSeed = provider.getString("employeeSeed");
                    String role = provider.getString("role");
                    String employeeName_en = provider.getString("employeeName_en");
                    String employeeName_ar = provider.getString("employeeName_ar");
                    String gender = provider.getString("gender");
                    String nationalityId = provider.getString("nationalityId");
                    String email = provider.getString("email");
                    String username = provider.getString("username");
                    String password = provider.getString("password");
                    String lang = provider.getString("lang");
                    String permissions = provider.getString("permissions");
                    String device_token = provider.getString("device_token");
                    String forgot_code = provider.getString("forgot_code");
                    String active = provider.getString("active");
                    String deleted = provider.getString("deleted");
                    String created_at = provider.getString("created_at");
                    String last_modify = provider.getString("last_modify");

                    // tmp hash map for single offer
                    HashMap<String, String> Categories_ob = new HashMap<>();

                    // adding each child node to HashMap key => value

                    Categories_ob.put("nationalityName_en", nationalityName_en);
                    Categories_ob.put("nationalityName_ar", nationalityName_ar);
                    Categories_ob.put("employeeId", employeeId);
                    Categories_ob.put("employeeSeed", employeeSeed);
                    Categories_ob.put("role", role);
                    Categories_ob.put("employeeName_en", employeeName_en);
                    Categories_ob.put("employeeName_ar", employeeName_ar);
                    Categories_ob.put("gender", gender);
                    Categories_ob.put("nationalityId", nationalityId);
                    Categories_ob.put("email", email);
                    Categories_ob.put("username", username);
                    Categories_ob.put("password", password);
                    Categories_ob.put("lang", lang);
                    Categories_ob.put("permissions", permissions);
                    Categories_ob.put("device_token", device_token);
                    Categories_ob.put("forgot_code", forgot_code);
                    Categories_ob.put("active", active);
                    Categories_ob.put("deleted", deleted);
                    Categories_ob.put("created_at", created_at);
                    Categories_ob.put("last_modify", last_modify);
                    provider_hash=Categories_ob;


                    Log.e("nery list is :",""+provider_hash.toString());

                } catch (final JSONException e) {
                    Log.e("", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //  formobject.buildDialog(R.style.DialogAnimation, "NO Records found","ok");


                        }
                    });

                }

            } else {
                Log.e("", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }
                });
            }


            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
            String lng = pref.getString("Mylang", "");
            Log.e("language", lng);
            cash=(CheckedTextView)findViewById(R.id.cash);
            transfer=(CheckedTextView)findViewById(R.id.transfer);
            credit=(CheckedTextView)findViewById(R.id.credit);
            mada=(CheckedTextView)findViewById(R.id.mada);

            transfer_form=(LinearLayout)findViewById(R.id.transfer_form);
            credit_form=(LinearLayout)findViewById(R.id.credit_form);
             mada_form=(LinearLayout)findViewById(R.id.mada_form);

            service_checkout=(TextView)findViewById(R.id.service_checkout);
            provider_checkout=(TextView)findViewById(R.id.provider_checkout);
            gender_checkout=(TextView)findViewById(R.id.gender_checkout);
            nationality_checkout=(TextView)findViewById(R.id.nationality_checkout);
            date_checkout=(TextView)findViewById(R.id.date_checkout);
            duration_checkout=(TextView)findViewById(R.id.duration_checkout);
            cost_checkout=(TextView)findViewById(R.id.cost_checkout);
            discount_checkout=(TextView)findViewById(R.id.discount_checkout);
            total_checkout=(TextView)findViewById(R.id.total_checkout);

            terms_checkout=(TextView)findViewById(R.id.terms_checkout);


            if(lng.equals("ar")){

                service_checkout.setText(offer.get("serviceName_ar"));
                provider_checkout.setText(provider_hash.get("employeeName_ar"));
                nationality_checkout.setText(provider_hash.get("nationalityName_ar"));

                if(provider_hash.get("gender").equals("f"))
                    gender_checkout.setText("انثه");
                else
                    gender_checkout.setText("ذكر");

                terms_checkout.setText(termsdata.get("terms_ar"));
                date_checkout.setText(dateandtime);




            }
            else{
                service_checkout.setText(offer.get("serviceName_en"));
                provider_checkout.setText(provider_hash.get("employeeName_en"));
                nationality_checkout.setText(provider_hash.get("nationalityName_en"));

                if(provider_hash.get("gender").equals("f"))
                    gender_checkout.setText("female");
                else
                    gender_checkout.setText("male");

                terms_checkout.setText(termsdata.get("terms_en"));
                date_checkout.setText(dateandtime);
            }
                duration_checkout.setText(offer.get("duration"));
                PaymentType=offer.get("paymentTypes");
                cost_checkout.setText(offer.get("cost"));
                String nocomma=offer.get("postCost");
                nocomma=  nocomma.replace(",", "");
                total_checkout.setText(nocomma);

                if(offer.get("offerId")!=null&&!offer.get("offerId").equals("null")){
                    PaymentType=offer.get("offerPaymentTypes");
                    discount_checkout.setText(offer.get("discount"));
                    Log.e("offer not null","id "+offer.get("offerId")+" payment"+PaymentType);

                }
                if(PaymentType.trim().equals("credit")){
                    transfer.setVisibility(View.GONE);
                    cash.setVisibility(View.GONE);
                    credit.setVisibility(View.VISIBLE);
                    mada.setVisibility(View.VISIBLE);

                    transfer.setChecked(false);
                    cash.setChecked(false);
                    credit.setChecked(true);
                    credit.setCheckMarkDrawable(R.drawable.ic_check_black_24dp);

                    mada.setChecked(false);

                    transfer_form.setVisibility(View.GONE);
                    credit_form.setVisibility(View.VISIBLE);
                    mada_form.setVisibility(View.GONE);


                }

                else if(PaymentType.trim().equals("cash")){
                    transfer.setVisibility(View.GONE);
                    cash.setVisibility(View.VISIBLE);
                    credit.setVisibility(View.GONE);
                    mada.setVisibility(View.GONE);

                    transfer.setChecked(false);
                    cash.setChecked(true);
                    cash.setCheckMarkDrawable(R.drawable.ic_check_black_24dp);

                    credit.setChecked(false);
                    mada.setChecked(false);

                    transfer_form.setVisibility(View.GONE);
                    credit_form.setVisibility(View.GONE);
                    mada_form.setVisibility(View.GONE);
                }

                else if (PaymentType.trim().equals("transfer")){
                    transfer.setVisibility(View.VISIBLE);
                    cash.setVisibility(View.GONE);
                    credit.setVisibility(View.GONE);
                    mada.setVisibility(View.GONE);

                    transfer.setChecked(true);
                    transfer.setCheckMarkDrawable(R.drawable.ic_check_black_24dp);

                    cash.setChecked(false);
                    credit.setChecked(false);
                    mada.setChecked(false);

                    transfer_form.setVisibility(View.VISIBLE);
                    credit_form.setVisibility(View.GONE);
                    mada_form.setVisibility(View.GONE);

                }
                else if (PaymentType.trim().equals("credit,cash")){
                    transfer.setVisibility(View.GONE);
                    cash.setVisibility(View.VISIBLE);
                    credit.setVisibility(View.VISIBLE);
                    mada.setVisibility(View.VISIBLE);

                    transfer.setChecked(false);
                    cash.setChecked(true);
                    cash.setCheckMarkDrawable(R.drawable.ic_check_black_24dp);
                    credit.setChecked(false);
                    mada.setChecked(false);

                    transfer_form.setVisibility(View.GONE);
                    credit_form.setVisibility(View.GONE);
                    mada_form.setVisibility(View.GONE);

                }
                else{
                    cash.setVisibility(View.VISIBLE);
                    transfer.setVisibility(View.VISIBLE);
                    credit.setVisibility(View.VISIBLE);
                    mada.setVisibility(View.VISIBLE);
                    cash.setChecked(true);
                    cash.setCheckMarkDrawable(R.drawable.ic_check_black_24dp);

                    transfer_form.setVisibility(View.GONE);
                    credit_form.setVisibility(View.GONE);
                    mada_form.setVisibility(View.GONE);

                }
                cash.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v)
                    {
                        ((CheckedTextView) v).toggle();

                        transfer.setChecked(false);
                        cash.setChecked(true);
                        mada.setChecked(false);
                        credit.setChecked(false);

                        credit.setCheckMarkDrawable(null);
                        cash.setCheckMarkDrawable(R.drawable.ic_check_black_24dp);
                        mada.setCheckMarkDrawable(null);
                        transfer.setCheckMarkDrawable(null);

                        transfer_form.setVisibility(View.VISIBLE);
                        credit_form.setVisibility(View.GONE);
                        mada_form.setVisibility(View.GONE);
                        transfer_form.setVisibility(View.GONE);
                        mada_form.setVisibility(View.GONE);
                        credit_form.setVisibility(View.GONE);
                    }
                });
                transfer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((CheckedTextView) view).toggle();
                        transfer.setChecked(true);
                        cash.setChecked(false);
                        mada.setChecked(false);
                        credit.setChecked(false);

                        credit.setCheckMarkDrawable(null);
                        cash.setCheckMarkDrawable(null);
                        mada.setCheckMarkDrawable(null);
                        transfer.setCheckMarkDrawable(R.drawable.ic_check_black_24dp);

                        transfer_form.setVisibility(View.VISIBLE);
                        credit_form.setVisibility(View.GONE);
                        mada_form.setVisibility(View.GONE);
                    }
                });
                credit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v)
                    {
                        ((CheckedTextView) v).toggle();
                        credit.setChecked(true);
                        credit.setCheckMarkDrawable(R.drawable.ic_check_black_24dp);
                        cash.setCheckMarkDrawable(null);
                        mada.setCheckMarkDrawable(null);
                        transfer.setCheckMarkDrawable(null);
                        cash.setChecked(false);
                        mada.setChecked(false);
                        transfer.setChecked(false);

                        credit_form.setVisibility(View.VISIBLE);
                        mada_form.setVisibility(View.GONE);
                        transfer_form.setVisibility(View.GONE);

                    }
                });
                mada.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v)
                    {
                        ((CheckedTextView) v).toggle();
                        mada.setChecked(true);
                        cash.setChecked(false);
                        credit.setChecked(false);
                        transfer.setChecked(false);

                        credit.setCheckMarkDrawable(null);
                        cash.setCheckMarkDrawable(null);
                        mada.setCheckMarkDrawable(R.drawable.ic_check_black_24dp);
                        transfer.setCheckMarkDrawable(null);

                        mada_form.setVisibility(View.VISIBLE);
                        credit_form.setVisibility(View.GONE);
                        transfer_form.setVisibility(View.GONE);

                    }
                });


        }

    }
    public class termstext extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // getLastLocation();
//            categories_list.clear();

            SharedPreferences pref = getSharedPreferences("user", Activity.MODE_PRIVATE);
            String data = pref.getString("data", "");
            try {
                JSONObject userdata = new JSONObject(data);
                token = userdata.getString("token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("user token is :", token);





        }

        @Override
        protected Void doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();

            // Making a request to url and getting response
            String urlget = base_url + "terms";
            String jsonStr = null;

            // String jsonStr = sh.makeServiceCall(urlget, token, "", "");
            // String response = null;
            try {
                URL url = new URL(urlget);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Client-Auth-Token", "Bearer"+" "+token);
                // String offerid=ClinicData.get("offerId");
                JSONObject jsonParam = new JSONObject();

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                Log.e("Before sending", "1" + jsonParam.toString());
                wr.writeBytes(jsonParam.toString());

                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                jsonStr = convertStreamToString(in);
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
            //   return response;
            Log.e("gg mynigga","yeaah");
            Log.e("is offers :", "Response from url countries: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject c = new JSONObject(jsonStr);

                    //JSONObject service=c.getJSONObject("service");

                    String settingId = c.getString("settingId");
                    String terms_en = c.getString("terms_en");

                    String terms_ar = c.getString("terms_ar");
                    String offer_creatable = c.getString("offer_creatable");


                    // tmp hash map for single offer
                    HashMap<String, String> Categories_ob = new HashMap<>();

                    // adding each child node to HashMap key => value
                    Categories_ob.put("settingId", settingId);
                    Categories_ob.put("terms_en", terms_en);

                    Categories_ob.put("terms_ar", terms_ar);
                    Categories_ob.put("offer_creatable", offer_creatable);

                    termsdata=Categories_ob;


                    Log.e("nery list is :",""+termsdata.toString());

                } catch (final JSONException e) {
                    Log.e("", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //  formobject.buildDialog(R.style.DialogAnimation, "NO Records found","ok");


                        }
                    });

                }

            } else {
                Log.e("", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }
                });
            }


            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }

    }

    public class GetBankAccounts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // getLastLocation();
//            categories_list.clear();

            SharedPreferences pref = getSharedPreferences("user", Activity.MODE_PRIVATE);
            String data = pref.getString("data", "");
            try {
                JSONObject userdata = new JSONObject(data);
                token = userdata.getString("token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("user token is :", token);


//            "categoryId":"1", "lat": "", "lng": "","pager":"1"
//


        }

        @Override
        protected Void doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();

            // Making a request to url and getting response
            String urlget = base_url + "bankAccounts";
            String jsonStr = null;

            // String jsonStr = sh.makeServiceCall(urlget, token, "", "");
            // String response = null;
            try {
                URL url = new URL(urlget);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Client-Auth-Token", "Bearer" + " " + token);
                // String offerid=ClinicData.get("offerId");
                JSONObject jsonParam = new JSONObject();
            //    jsonParam.put("serviceId", offer.get("serviceId"));
                //     jsonParam.put("offerId",offerid);


                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                Log.e("Before sending", "1" + jsonParam.toString());
                wr.writeBytes(jsonParam.toString());

                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                jsonStr = convertStreamToString(in);
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
            //   return response;
            Log.e("gg mynigga", "yeaah");
            Log.e("is offers :", "Response from url countries: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONArray c = new JSONArray(jsonStr);


                    //     listDataHeader.add(clinic_ob);
                    for (int i = 0; i < c.length(); i++) {
                        JSONObject provider = c.getJSONObject(i);

                        String accountId = provider.getString("accountId");
                        String bankId = provider.getString("bankId");
                        String accountName = provider.getString("accountName");
                        String IBAN = provider.getString("IBAN");
                        String bankName_en = provider.getString("bankName_en");
                        String bankName_ar = provider.getString("bankName_ar");
                        String bankImg = provider.getString("bankImg");
                        String deleted = provider.getString("deleted");
                        String created_at = provider.getString("created_at");
                        String last_modify = provider.getString("last_modify");

                        // tmp hash map for single offer
                        HashMap<String, String> Categories_ob = new HashMap<>();

                        // adding each child node to HashMap key => value

                        Categories_ob.put("accountId", accountId);
                        Categories_ob.put("bankId", bankId);
                        Categories_ob.put("accountName", accountName);
                        Categories_ob.put("IBAN", IBAN);
                        Categories_ob.put("bankName_en", bankName_en);
                        Categories_ob.put("bankName_ar", bankName_ar);
                        Categories_ob.put("bankImg", bankImg);
                        Categories_ob.put("deleted", deleted);
                        Categories_ob.put("created_at", created_at);
                        Categories_ob.put("last_modify", last_modify);

                        bank_accounts_list.add(Categories_ob);
                        bank_accounts_en.add(bankName_en);
                        bank_accounts_ar.add(bankName_ar);

                    }

                } catch (final JSONException e) {
                    Log.e("", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //  formobject.buildDialog(R.style.DialogAnimation, "NO Records found","ok");


                        }
                    });

                }

            } else {
                Log.e("", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

//                        Toast.makeText(getApplicationContext(),
//                                "Couldn't get data from server. Check your internet connection or try later",
//                                Toast.LENGTH_LONG).show();

                    }
                });
            }


            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            SharedPreferences pref=getSharedPreferences("Settings",Activity.MODE_PRIVATE);
            String lng=pref.getString("Mylang","");
            Log.e("language",lng);
            Log.e("f;aj;",bank_accounts_en.toString());
            Log.e("f;aj;",bank_accounts_ar.toString());

//            final TextInputLayout city=(TextInputLayout)findViewById(R.id.city_input);

//            city.setVisibility(View.VISIBLE);
            if(lng.equals("ar")){

                final Spinner provider_spinner = (Spinner) findViewById(R.id.bank_accounts);
                final ArrayAdapter adapter = new ArrayAdapter<>(
                        Checkout.this,
                        R.layout.simple_spinner_dropdown_item,
                        bank_accounts_ar
                );
            //    adapter.insert("اي مقدم خدمة",0);
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

                provider_spinner.setAdapter(adapter);
                //  adapter.notifyDataSetChanged();
                //    int spinnerPosition = adapter.getPosition("المدينة");
                provider_spinner.setSelection(0);
                // spinner_region.setBackgroundResource(arrow_left);



            }

            else{

                final Spinner provider_spinner = (Spinner) findViewById(R.id.bank_accounts);
                final ArrayAdapter adapter = new ArrayAdapter<>(
                        Checkout.this,
                        R.layout.simple_spinner_dropdown_item,
                        bank_accounts_en
                );
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

                //adapter.insert("Any Provider",0);


                provider_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {

//                        if (pos ==0) {
//
//                            //     To reset a spinner to default value:
//
//                            selected_bank=null;
//                        } else{
                            selected_bank=bank_accounts_list.get(pos);

                     //   }


                        Log.e("selected is :","dat "+selected_bank);

                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                        Log.e("noting :","dat ");

                    }

                });
                provider_spinner.setAdapter(adapter);
                //  adapter.notifyDataSetChanged();
                //int spinnerPosition = adapter.getPosition("City");
                provider_spinner.setSelection(0);
            }
            Runnable progressRunnable = new Runnable() {

                @Override
                public void run() {
                    pd.cancel();
                }
            };

            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 3500);
        }


    }

    public class Reservation extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // getLastLocation();
//            categories_list.clear();

            SharedPreferences pref = getSharedPreferences("user", Activity.MODE_PRIVATE);
            String data = pref.getString("data", "");
            try {
                JSONObject userdata = new JSONObject(data);
                Log.e("data is ","dat "+userdata);
                token = userdata.getString("token");
                userid=userdata.getString("clientId");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("user token is :", token);

            Log.e("user token is :", userid);

//            "categoryId":"1", "lat": "", "lng": "","pager":"1"
//



        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();

            // Making a request to url and getting response
            String urlget = base_url + "createReservation";
            String jsonStr = null;

            // String jsonStr = sh.makeServiceCall(urlget, token, "", "");
            // String response = null;
            try {
                URL url = new URL(urlget);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Client-Auth-Token", "Bearer" + " " + token);
                // String offerid=ClinicData.get("offerId");
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("clinicId", offer.get("clinicId"));
                jsonParam.put("serviceId", offer.get("serviceId"));
                jsonParam.put("clientId",userid );
                jsonParam.put("employeeId", provider_hash.get("employeeId"));
                jsonParam.put("appointmentDate",dateandtime);
                jsonParam.put("totalDuration", offer.get("duration"));
                jsonParam.put("cost", offer.get("cost"));
                if(offer.get("discount")!=null) {
                    jsonParam.put("offerId", offer.get("offerId"));
                    jsonParam.put("discount", offer.get("discount"));
                }
                else {
                    jsonParam.put("discount","0.0");
                    jsonParam.put("offerId", null);

                }
                String nocomma=offer.get("postCost");
                nocomma=  nocomma.replace(",", "");
                jsonParam.put("totalCost",nocomma );
                if(transfer.isChecked()){
                    jsonParam.put("paymentType","cash");
                    String account= selected_bank.get("accountId");
                    String dateof=  depostDate.getText().toString();
                    String amountm=   amount.getText().toString();
                    String depostName=   depositorName.getText().toString();
                    jsonParam.put("accountId",account);
                    jsonParam.put("depositDate",dateof);
                    jsonParam.put("depositorName",amountm);
                    jsonParam.put("amount",depostName);
                }
                else
                    jsonParam.put("paymentType","cash");


//                {
//                    "clinicId": 2,
//                        "serviceId": 61,
//                        "clientId": 14875,
//                        "offerId": 269,
//                        "employeeId": 46,
//                        "appointmentDate": "2019-11-20 09:30:00",
//                        "totalDuration": 90,
//                        "cost":"800.00",
//                        "discount": "37.50",
//                        "totalCost": "500.00"
//
//                }
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                Log.e("Before sending", "1" + jsonParam.toString());
                wr.writeBytes(jsonParam.toString());

                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                jsonStr = convertStreamToString(in);
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
            //   return response;
            Log.e("gg mynigga", "yeaah");
            Log.e("is offers :", "Response from url countries: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONArray c = new JSONArray(jsonStr);


//                    //     listDataHeader.add(clinic_ob);
//                    for (int i = 0; i < c.length(); i++) {
//                        JSONObject provider = c.getJSONObject(i);
//
//                        String accountId = provider.getString("accountId");
//                        String bankId = provider.getString("bankId");
//                        String accountName = provider.getString("accountName");
//                        String IBAN = provider.getString("IBAN");
//                        String bankName_en = provider.getString("bankName_en");
//                        String bankName_ar = provider.getString("bankName_ar");
//                        String bankImg = provider.getString("bankImg");
//                        String deleted = provider.getString("deleted");
//                        String created_at = provider.getString("created_at");
//                        String last_modify = provider.getString("last_modify");
//
//                        // tmp hash map for single offer
//                        HashMap<String, String> Categories_ob = new HashMap<>();
//
//                        // adding each child node to HashMap key => value
//
//                        Categories_ob.put("accountId", accountId);
//                        Categories_ob.put("bankId", bankId);
//                        Categories_ob.put("accountName", accountName);
//                        Categories_ob.put("IBAN", IBAN);
//                        Categories_ob.put("bankName_en", bankName_en);
//                        Categories_ob.put("bankName_ar", bankName_ar);
//                        Categories_ob.put("bankImg", bankImg);
//                        Categories_ob.put("deleted", deleted);
//                        Categories_ob.put("created_at", created_at);
//                        Categories_ob.put("last_modify", last_modify);
//
//                        bank_accounts_list.add(Categories_ob);
//                        bank_accounts_en.add(bankName_en);
//                        bank_accounts_ar.add(bankName_ar);
//
//                    }

                } catch (final JSONException e) {
                    Log.e("", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //  formobject.buildDialog(R.style.DialogAnimation, "NO Records found","ok");


                        }
                    });

                }

            } else {
                Log.e("", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

//                        Toast.makeText(getApplicationContext(),
//                                "Couldn't get data from server. Check your internet connection or try later",
//                                Toast.LENGTH_LONG).show();

                    }
                });
            }


            return jsonStr;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            SharedPreferences pref=getSharedPreferences("Settings",Activity.MODE_PRIVATE);
            String lng=pref.getString("Mylang","");
            Log.e("language",lng);
            Log.e("f;aj;","is "+result);
            if(result.trim().equals("false")){
                Log.e("not"," available false ");
            }
            else if(result.trim().equals("null")||result==null){
                Log.e("not"," available");

            }

            else
                Log.e("none conditon","worked");
                Intent intent=new Intent(Checkout.this,SuccessPage.class);
                startActivity(intent);
                finishAffinity();

        }



    }

    public class ReservationCredit extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // getLastLocation();
//            categories_list.clear();

            SharedPreferences pref = getSharedPreferences("user", Activity.MODE_PRIVATE);
            String data = pref.getString("data", "");
            try {
                JSONObject userdata = new JSONObject(data);
                Log.e("data is ","dat "+userdata);
                token = userdata.getString("token");
                userid=userdata.getString("clientId");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("user token is :", token);

            Log.e("user token is :", userid);

//            "categoryId":"1", "lat": "", "lng": "","pager":"1"



        }

        @Override
        protected String doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();

            // Making a request to url and getting response
            String urlget = base_url + "createUrlAndOrderV2";
            String jsonStr = null;

            // String jsonStr = sh.makeServiceCall(urlget, token, "", "");
            // String response = null;
            try {
                URL url = new URL(urlget);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Client-Auth-Token", "Bearer" + " " + token);
                // String offerid=ClinicData.get("offerId");
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("clinicId", offer.get("clinicId"));
                jsonParam.put("serviceId", offer.get("serviceId"));
                jsonParam.put("clientId",userid );
                jsonParam.put("employeeId", provider_hash.get("employeeId"));
                jsonParam.put("appointmentDate",dateandtime);
                jsonParam.put("totalDuration", offer.get("duration"));
                jsonParam.put("cost", offer.get("cost"));
                if(offer.get("discount")!=null) {
                    jsonParam.put("offerId", offer.get("offerId"));
                    jsonParam.put("discount", offer.get("discount"));
                }
                else {
                    jsonParam.put("discount","0.0");
                    jsonParam.put("offerId", null);

                }
                String nocomma=offer.get("postCost");
                nocomma=  nocomma.replace(",", "");
                jsonParam.put("totalCost",nocomma );
                jsonParam.put("paymentType",selected_payment_type);
                jsonParam.put("cc_firstName",firstName);
                jsonParam.put("cc_lastName",lastName);
                jsonParam.put("email",email);

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                Log.e("Before sending", "1" + jsonParam.toString());
                wr.writeBytes(jsonParam.toString());

                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                jsonStr = convertStreamToString(in);
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
            //   return response;
            Log.e("gg mynigga", "yeaah");
            Log.e("is offers :", "Response from url countries: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject c = new JSONObject(jsonStr);
                        String response_code = c.getString("response_code");
                    String payment_url = c.getString("payment_url");
                    String redirect_url = c.getString("redirect_url");
                    String transaction_id = c.getString("transaction_id");
                    String p_id = c.getString("p_id");

                    creditPaymentData.put("response_code",response_code);
                    creditPaymentData.put("payment_url",payment_url);
                    creditPaymentData.put("transaction_id",transaction_id);
                    creditPaymentData.put("redirect_url",redirect_url);
                    creditPaymentData.put("p_id",p_id);

                } catch (final JSONException e) {
                    Log.e("", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //  formobject.buildDialog(R.style.DialogAnimation, "NO Records found","ok");


                        }
                    });

                }

            } else {
                Log.e("", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

//                        Toast.makeText(getApplicationContext(),
//                                "Couldn't get data from server. Check your internet connection or try later",
//                                Toast.LENGTH_LONG).show();

                    }
                });
            }


            return jsonStr;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            SharedPreferences pref=getSharedPreferences("Settings",Activity.MODE_PRIVATE);
            String lng=pref.getString("Mylang","");
            Log.e("language",lng);
            Log.e("f;aj;","is "+result);
//            if(Integer.parseInt(result)>0){
                Log.e("sucess","complete new page");
         //   }
          //  else
                Log.e("not"," available");
                if(result==null){


                    alert_message(getResources().getString(R.string.notavailable),getResources().getString(R.string.reservation_taken),"ok");

                }
                else if(creditPaymentData.get("response_code").equals("4012")){
                    Log.e("first tiem worked","without problemt");
                    new OrderPaidCheck().execute();
                    creditcall();
                }
                else
                {
                alert_message(getResources().getString(R.string.error),getResources().getString(R.string.enterValidData),"ok");

                }
        }


    }
    public class OrderPaidCheck extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // getLastLocation();
//            categories_list.clear();

            SharedPreferences pref = getSharedPreferences("user", Activity.MODE_PRIVATE);
            String data = pref.getString("data", "");
            try {
                JSONObject userdata = new JSONObject(data);
                Log.e("data is ","dat "+userdata);
                token = userdata.getString("token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("user token is :", token);


//            "categoryId":"1", "lat": "", "lng": "","pager":"1"



        }

        @Override
        protected String doInBackground(Void... arg0) {
//            final FormActivity formobject=new FormActivity();

            // Making a request to url and getting response
            String urlget = base_url + "orderPaidByTransactionId";
            String jsonStr = null;

            // String jsonStr = sh.makeServiceCall(urlget, token, "", "");
            // String response = null;
            try {
                URL url = new URL(urlget);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Client-Auth-Token", "Bearer" + " " + token);
                // String offerid=ClinicData.get("offerId");
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("transactionId", creditPaymentData.get("transaction_id"));


                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                Log.e("Before sending", "1" + jsonParam.toString());
                wr.writeBytes(jsonParam.toString());

                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                jsonStr = convertStreamToString(in);
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
            //   return response;
            Log.e("gg mynigga", "yeaah");
            Log.e("is offers :", "Response from url countries: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject c = new JSONObject(jsonStr);


                } catch (final JSONException e) {
                    Log.e("", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //  formobject.buildDialog(R.style.DialogAnimation, "NO Records found","ok");


                        }
                    });

                }

            } else {
                Log.e("", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

//                        Toast.makeText(getApplicationContext(),
//                                "Couldn't get data from server. Check your internet connection or try later",
//                                Toast.LENGTH_LONG).show();

                    }
                });
            }


            return jsonStr;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            SharedPreferences pref=getSharedPreferences("Settings",Activity.MODE_PRIVATE);
            String lng=pref.getString("Mylang","");
            Log.e("language",lng);
            if (result!=null){
                Log.e("f;aj;","is "+result);
                String res=result.replaceAll("\"","");
                res=res.replaceAll(" ","");
                Log.e("new string is ","this "+res);


            if(res.trim().equals("valid")){
                paid=res;

                Log.e("paid value is =","in valid is "+paid);

            }
            else if(res.trim().equals("paid")){
                paid=res;
                Log.e("paid value is =","in paid is "+paid);

//                alert(title, body);
//                title=>'paymentSuccess', 'paymentSuccessDesc';
//                then go to success page
            }
            else if(res.trim().equals("pending_payment")){

                paid=res;
                Log.e("paid value is =","in pending is "+paid);

//                alert(title, body);
//                ('transactionApprovalTitle', 'transactionApprovalDesc');
//                then go to home page
            }
            else {
                paid="error";
//                alert(title, body);
//                ('paymentFailed', 'pinError');
//                $state.go('app.home');
            }
            }
            else{
                paid="error";
            }
            Log.e("paid value is =","dis "+paid);


//            if(Integer.parseInt(result)>0){
            Log.e("sucess","complete new page");
            //   }
            //  else

        }


    }

    private void updateLabel(Calendar myCalendar) {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        depostDate.setText(sdf.format(myCalendar.getTime()));
    }
    public void callAsynchronousTask() {
        handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            termstext performBackgroundTask = new termstext();
                            // PerformBackgroundTask this class is the class that extends AsynchTask
                            new termstext().execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000); //execute in every 50000 ms
    }
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

}
