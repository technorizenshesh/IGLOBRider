package main.com.iglobuser.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.iglobuser.R;
import main.com.iglobuser.app.Config;
import main.com.iglobuser.constant.ACProgressCustom;
import main.com.iglobuser.constant.BaseUrl;
import main.com.iglobuser.constant.MyLanguageSession;
import main.com.iglobuser.constant.MySession;
import main.com.iglobuser.utils.NotificationUtils;

public class InvoiceActivity extends AppCompatActivity {
    private TextView time_tv,timefare,distancefare,distance,carcharge,tipamount;

    private TextView servicetax,nightcharge,date_tv, totalamount, pickuplocation, droplocation, drivername;
    private ProgressBar progressbar;
    private ImageView driver_img,remove_tips;
    private RelativeLayout exit_app_but;
    private RatingBar ratingbar;
    private EditText comment_et;
    private Button submit;
    private String req_datetime = "",driver_id="",car_charge_str="",amount_str_main="", amount_str = "", request_id = "", comment_str = "", user_log_data = "", user_id = "";
    private float rating = 0;
    MySession mySession;
    RadioGroup paymentGroup;
    String payment_type_str = "Cash";
    float mywalletamount, rideamount;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    Dialog waitDialog;
    String time_zone="";
    ACProgressCustom ac_dialog;
    private TextView paymet_type,apply_tv,tipsamt_tv,discount_type;
    private CheckBox tips_check;
    private LinearLayout tipslay,tipsshowlay,tips_check_lay;
    String tips_amount_str = "0";
    private EditText tips_amount;
    private String language = "";
    MyLanguageSession myLanguageSession;

    //paypal code

    //end paypal code
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_invoice);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (myLanguageSession.getLanguage().equalsIgnoreCase("ar")) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

        }
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
            NotificationUtils.r.stop();
        }

        if (user_log_data == null) {

        } else {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        idinit();
        clickevent();
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null&&!bundle.isEmpty()){
            request_id = bundle.getString("request_id");
            Log.e("request_id >> "," >"+request_id);
            new GetPayment().execute();
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Log.e("Push notification: ", "" + message);
                    JSONObject data = null;
                    try {
                        data = new JSONObject(message);
                        String keyMessage = data.getString("key").trim();
                        Log.e("KEY ACCEPT REJ", "" + keyMessage);
                        if (keyMessage.equalsIgnoreCase("your booking request is Finish")) {
                            if (waitDialog != null && waitDialog.isShowing()) {
                                waitDialog.dismiss();


                            }
                        }
                        if (keyMessage.equalsIgnoreCase("your payment is denied")) {
                            if (waitDialog != null && waitDialog.isShowing()) {
                                waitDialog.dismiss();
                                //  driverDeniedPayment();

                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };



    }


    @Override
    protected void onResume() {
        super.onResume();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }
    }

    private void clickevent() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void idinit() {
        //   paymentGroup = (RadioGroup) findViewById(R.id.paymentGroup);
        carcharge = findViewById(R.id.carcharge);
        nightcharge =findViewById(R.id.nightcharge);
        servicetax =findViewById(R.id.servicetax);
        distancefare = findViewById(R.id.distancefare);
        distance = findViewById(R.id.distance);
        time_tv = findViewById(R.id.time_tv);
        timefare = findViewById(R.id.timefare);
        tipamount = findViewById(R.id.tipamount);

        discount_type = findViewById(R.id.discount_type);
        tipsshowlay = findViewById(R.id.tipsshowlay);
        tips_amount = findViewById(R.id.tips_amount);
        tipsamt_tv = findViewById(R.id.tipsamt_tv);
        tips_check_lay = findViewById(R.id.tips_check_lay);
        apply_tv = findViewById(R.id.apply_tv);
        remove_tips = findViewById(R.id.remove_tips);
        tips_check = findViewById(R.id.tips_check);
        tipslay = findViewById(R.id.tipslay);
        submit = (Button) findViewById(R.id.submit);
        paymet_type = (TextView) findViewById(R.id.paymet_type);
        comment_et = (EditText) findViewById(R.id.comment_et);
        ratingbar = (RatingBar) findViewById(R.id.ratingbar);
        driver_img = (ImageView) findViewById(R.id.driver_img);
        date_tv = (TextView) findViewById(R.id.date_tv);
        droplocation = (TextView) findViewById(R.id.droplocation);
        drivername = (TextView) findViewById(R.id.drivername);
        pickuplocation = (TextView) findViewById(R.id.pickuplocation);
        totalamount = (TextView) findViewById(R.id.totalamount);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);
        tips_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tipslay.setVisibility(View.VISIBLE);
                } else {
                    tipslay.setVisibility(View.GONE);
                }
            }
        });
        remove_tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tips_amount_str = "0";
                tipsshowlay.setVisibility(View.GONE);
                tips_check.setChecked(false);
                tips_check_lay.setVisibility(View.VISIBLE);
                tipslay.setVisibility(View.GONE);
                amount_str=amount_str_main;
            }
        });
        apply_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tips_amount_str = tips_amount.getText().toString();
                if (tips_amount_str == null || tips_amount_str.equalsIgnoreCase("")) {

                } else {
                    tipsamt_tv.setText("$" + tips_amount_str);
                    tipsshowlay.setVisibility(View.VISIBLE);
                    tips_check_lay.setVisibility(View.GONE);

                }
            }
        });
    }

    private class GetPayment extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressbar.setVisibility(View.VISIBLE);
            if(ac_dialog!=null){
                ac_dialog.show();
            }

            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
//http://mobileappdevelop.co/NAXCAN/webservice/get_payment?request_id=1
                String postReceiverUrl = BaseUrl.baseurl + "get_payment?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("request_id", request_id);
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                String urlParameters = postData.toString();
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(urlParameters);
                writer.flush();
                String response = "";
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
                writer.close();
                reader.close();
                return response;
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // progressbar.setVisibility(View.GONE);
            if(ac_dialog!=null){
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    if (msg.equalsIgnoreCase("successful")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            amount_str = jsonObject1.getString("total");
                            amount_str_main = jsonObject1.getString("total");

                            tipamount.setText("$" + jsonObject1.getString("per_miles_charge"));
                            distancefare.setText("$" + jsonObject1.getString("per_miles_charge"));
                            timefare.setText("$" + jsonObject1.getString("per_min_charge"));
                            // basefare.setText("" + "$ " + jsonObject1.getInt("base_fare"));
                            carcharge.setText("" + "$" + jsonObject1.getInt("car_charge"));
                            distance.setText("Distance(" + jsonObject1.getString("miles") + " km)");
                            time_tv.setText("Time(" + jsonObject1.getString("perMin") + " min)");
                            nightcharge.setText("$" + jsonObject1.getString("night_charge_amount"));
                            servicetax.setText("$" + jsonObject1.getString("service_tax_amount"));


                            car_charge_str = String.valueOf(jsonObject1.getString("car_charge"));

                            JSONObject jsonObject2 = jsonObject1.getJSONObject("booking_detail");
                            pickuplocation.setText("" + jsonObject2.getString("picuplocation"));
                            droplocation.setText("" + jsonObject2.getString("dropofflocation"));
                            paymet_type.setText("Payment Type : "+jsonObject2.getString("payment_type"));
                           String tip_amount_str = jsonObject2.getString("tip_amount");
                            if (tip_amount_str == null || tip_amount_str.equalsIgnoreCase("") || tip_amount_str.equalsIgnoreCase("0")) {
                                tip_amount_str = "0";
                                tipamount.setText("$0.00");
                                totalamount.setText("Total :" + "$" + jsonObject1.getString("total"));

                            } else {
                                tipamount.setText("$" + tip_amount_str);
                                double tipamt = Double.parseDouble(tip_amount_str);
                                totalamount.setText("Total :" + "$" + jsonObject1.getString("total"));

                                if (jsonObject1.getString("total") != null && !jsonObject1.getString("total").equalsIgnoreCase("")) {
                                    double totalrideamt = Double.parseDouble(jsonObject1.getString("total"));
                                    double total = tipamt + totalrideamt;
                                    totalamount.setText("Total :" + "$" + total);

                                }

                            }
                            try {
                                Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject2.getString("req_datetime"));
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm");
                                String strDate = formatter.format(date1);
                                date_tv.setText(""+strDate);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                           // totalamount.setText("Total :" + "$ " + jsonObject1.getString("total"));
                            discount_type.setText(getResources().getString(R.string.discountapplied) + "$ " + jsonObject1.getString("discount"));

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }












}