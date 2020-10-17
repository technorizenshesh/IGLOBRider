package main.com.iglobuser.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

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
import java.util.LinkedHashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import main.com.iglobuser.MainActivity;
import main.com.iglobuser.R;
import main.com.iglobuser.app.Config;
import main.com.iglobuser.constant.ACProgressCustom;
import main.com.iglobuser.constant.BaseUrl;
import main.com.iglobuser.constant.MyLanguageSession;
import main.com.iglobuser.constant.MySession;
import main.com.iglobuser.restapi.ApiClient;
import main.com.iglobuser.utils.NotificationUtils;

public class DriverDetailAct extends AppCompatActivity {
    private String language = "";
    MyLanguageSession myLanguageSession;

    private RelativeLayout exit_app_but;
    private CircleImageView driver_img;
    private TextView drivername, cardetail, pickuplocation, droplocation,cancel_booking;
    private RatingBar rating;
    private ImageView addfavourite, drivercarimg,share_img,calllay,message_lay;
    private String driver_img_str="",driver_name_str="",car_detail_str="",request_id="",fav_status = "", user_log_data = "", user_id = "", driver_detail_str = "", mobile_str = "", driver_id = "", dropoff_str = "", pickup_str = "";
    ACProgressCustom ac_dialog;
    private MySession mySession;
    BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_driver_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (myLanguageSession.getLanguage().equalsIgnoreCase("ar")) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

        }

        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
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
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && !bundle.isEmpty()) {
            driver_detail_str = bundle.getString("driver_detail_str");
            dropoff_str = bundle.getString("dropoff_str");
            pickup_str = bundle.getString("pickup_str");
            driver_id = bundle.getString("driver_id");
            fav_status = bundle.getString("fav_status");
            request_id = bundle.getString("request_id");

        }
        idinti();
        clickevent();
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
                        //(keyMessage.equalsIgnoreCase("new shared user"))
                        if (keyMessage.equalsIgnoreCase("your booking request is Cancel")) {
                            request_id = data.getString("request_id");
                            requestCancel();
                        }
                        else if (keyMessage.equalsIgnoreCase("your booking request is Arrived")) {
                            // status_map = "Arrived";
                            request_id = data.getString("request_id");
                            driverisArrived();
                        }
                        else if (keyMessage.equalsIgnoreCase("your booking request is Start")) {
                            // status_map = "Start";
                            request_id = data.getString("request_id");
                            //titletext.setText("" + "Trip is Started");
                            tripStarted();
                        } else if (keyMessage.equalsIgnoreCase("your booking request is End")) {
                            Intent i = new Intent(DriverDetailAct.this, FeedbackUs.class);
                            startActivity(i);
                            finish();
                        } else if (keyMessage.equalsIgnoreCase("your ride is Finish")) {
                            tripFinish();
                        } else if (keyMessage.equalsIgnoreCase("your booking request is assign to new driver")) {
                            request_id = data.getString("request_id");
                            // requestReassign();
                        }

                        else if (keyMessage.equalsIgnoreCase("arriving latter booking request")) {
                            if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                                NotificationUtils.r.stop();
                            }
                            String picklaterdate = data.getString("picklaterdate");
                            String picklatertime = data.getString("picklatertime");

                            //  bookedRequestAlert(picklaterdate,picklatertime);


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
        LocalBroadcastManager.getInstance(DriverDetailAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(DriverDetailAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(DriverDetailAct.this.getApplicationContext());
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
        addfavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("driver_id>>",">> "+driver_id);
                Log.e("user_id>>",">> "+user_id);
                callapi(user_id, driver_id);
            }
        });
        cancel_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCancelRide();
            }
        });
        share_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        getResources().getString(R.string.app_name));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        calllay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobile_str == null || mobile_str.equalsIgnoreCase("")) {

                } else {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobile_str));
                    startActivity(callIntent);

                }
            }
        });
        message_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //messagePop();
                Intent i = new Intent(DriverDetailAct.this, ChatingAct.class);
                i.putExtra("receiver_id", driver_id);
                i.putExtra("request_id", request_id);
                i.putExtra("receiver_img", driver_img_str);
                i.putExtra("receiver_name", driver_name_str);
                i.putExtra("block_status", "");
                startActivity(i);
            }
        });
    }
    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(DriverDetailAct.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();

    }
    private void messagePop() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(DriverDetailAct.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.custom_click_sendmsg);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView reply_tv = (TextView) dialogSts.findViewById(R.id.reply_tv);
        TextView driver_name = (TextView) dialogSts.findViewById(R.id.driver_name);
        TextView car_detial = (TextView) dialogSts.findViewById(R.id.car_detial);
        TextView close_tv = (TextView) dialogSts.findViewById(R.id.close_tv);
        CircleImageView driver_img = (CircleImageView) dialogSts.findViewById(R.id.driver_img);
        driver_name.setText(""+driver_name_str);
        car_detial.setText(""+car_detail_str);
        if (driver_img_str!=null&&!driver_img_str.equalsIgnoreCase("")){
            Picasso.with(DriverDetailAct.this).load(driver_img_str).into(driver_img);

        }

        reply_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
            }
        });close_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
            }
        });
        dialogSts.show();


    }

    private void idinti() {
        message_lay = findViewById(R.id.message_lay);
        calllay = findViewById(R.id.calllay);
        cancel_booking = findViewById(R.id.cancel_booking);
        share_img = findViewById(R.id.share_img);
        exit_app_but = findViewById(R.id.exit_app_but);
        drivercarimg = findViewById(R.id.drivercarimg);
        addfavourite = findViewById(R.id.addfavourite);
        cardetail = findViewById(R.id.cardetail);
        droplocation = findViewById(R.id.droplocation);
        pickuplocation = findViewById(R.id.pickuplocation);
        driver_img = findViewById(R.id.driver_img);
        drivername = findViewById(R.id.drivername);
        rating = findViewById(R.id.rating);
        if (DriverAcceptStatus.fav_status.equalsIgnoreCase("favorite")) {
//no_favorite
            addfavourite.setImageResource(R.drawable.fav_add);
        } else {
            addfavourite.setImageResource(R.drawable.wishlist);
        }

        pickuplocation.setText("" + pickup_str);
        droplocation.setText("" + dropoff_str);
        if (driver_detail_str != null && !driver_detail_str.equalsIgnoreCase("")) {
            Log.e("driver_detail_str", "> " + driver_detail_str);
            try {
                JSONArray jsonArray1 = new JSONArray(driver_detail_str);
                for (int k = 0; k < jsonArray1.length(); k++) {
                    JSONObject jsonObject2 = jsonArray1.getJSONObject(k);
                    driver_name_str=jsonObject2.getString("first_name") + " " + jsonObject2.getString("last_name");
                    car_detail_str= jsonObject2.getString("vehicle_name") + "\n" + jsonObject2.getString("car_number").trim() + " , " + jsonObject2.getString("car_color");

                    drivername.setText("" + jsonObject2.getString("first_name") + " " + jsonObject2.getString("last_name"));
                    mobile_str = jsonObject2.getString("mobile");
                   String driver_id = jsonObject2.getString("id");

                    String data ="" + jsonObject2.getString("car_color")+" "+jsonObject2.getString("vehicle_make")+" "+jsonObject2.getString("vehicle_name");
                    String return_val =  capText(data);
                    cardetail.setText(return_val);
                    if (jsonObject2.getString("rating") != null && !jsonObject2.getString("rating").equalsIgnoreCase("")) {
                        rating.setRating(Float.parseFloat(jsonObject2.getString("rating")));
                    }

                    //"http://mobileappdevelop.co/NAXCAN/uploads/images/"
                    if (jsonObject2.getString("profile_image") == null || jsonObject2.getString("profile_image").equalsIgnoreCase("") || jsonObject2.getString("profile_image").equalsIgnoreCase(BaseUrl.image_baseurl)) {

                    } else {
                        driver_img_str= jsonObject2.getString("profile_image");

                        Picasso.with(DriverDetailAct.this).load(jsonObject2.getString("profile_image")).into(driver_img);

                    }
                    if (jsonObject2.getString("car_image") == null || jsonObject2.getString("car_image").equalsIgnoreCase("") || jsonObject2.getString("car_image").equalsIgnoreCase(BaseUrl.image_baseurl)) {

                    } else {
                        Picasso.with(DriverDetailAct.this).load(BaseUrl.image_baseurl + jsonObject2.getString("car_image")).into(drivercarimg);

                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void callapi(String user_id, String driver_id) {
//http://testing.bigclicki.com/webservice/loginapp?email=0&password=0
        Log.e("loginCall >", " > FIRST");
        if (ac_dialog != null) {
            ac_dialog.show();
        }

        Call<ResponseBody> call = ApiClient.getApiInterface().addRemFavorite(user_id, driver_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (ac_dialog != null) {
                    ac_dialog.dismiss();
                }

                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject object = new JSONObject(responseData);
                        Log.e("loginCall >", " >" + responseData);
                        if (object.getString("status").equals("1")) {
                            if (object.getString("result").equalsIgnoreCase("favorite drivert successfully")) {
                                addfavourite.setImageResource(R.drawable.fav_add);
                                DriverAcceptStatus.fav_status = "favorite";
                                Toast.makeText(DriverDetailAct.this, getResources().getString(R.string.driverasfav), Toast.LENGTH_LONG).show();
                            } else {
                                DriverAcceptStatus.fav_status = "no_favorite";
                                addfavourite.setImageResource(R.drawable.wishlist);
                                Toast.makeText(DriverDetailAct.this, getResources().getString(R.string.unfav), Toast.LENGTH_LONG).show();

                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                t.printStackTrace();
                if (ac_dialog != null) {
                    ac_dialog.dismiss();
                }

                Log.e("TAG", t.toString());
            }
        });
    }
    private void toCancelRide() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(DriverDetailAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.sure_to_cancle);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        final TextView message_tv = (TextView) canceldialog.findViewById(R.id.message_tv);
        message_tv.setText(getResources().getString(R.string.cancelconfirmmsg));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ResponseToRequest().execute();
                canceldialog.dismiss();

            }
        });
        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
            }
        });
        canceldialog.show();


    }

    private class ResponseToRequest extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
//http://mobileappdevelop.co/NAXCAN/webservice/users_Cancel_request?request_id=1&status=Cancel
            try {
                String postReceiverUrl = BaseUrl.baseurl + "users_Cancel_request?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("request_id", request_id);
                params.put("status", "Cancel");
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
                Log.e("Json Cnacel By user", ">>>>>>>>>>>>" + response);
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
            if(ac_dialog!=null){
                ac_dialog.dismiss();
            }
            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                //finish();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getString("message").equalsIgnoreCase("unsuccessfull")){

                    }
                    else {
                        Intent i = new Intent(DriverDetailAct.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(i);                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private void requestCancel() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(DriverDetailAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_heading_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView done_tv = (TextView) canceldialog.findViewById(R.id.done_tv);
        done_tv.setText(""+getResources().getString(R.string.ok));
        done_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                if (NotificationUtils.r!=null&&NotificationUtils.r.isPlaying()){
                    NotificationUtils.r.stop();
                }
                Intent i = new Intent(DriverDetailAct.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });
        canceldialog.show();


    }
    private void tripStarted() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(DriverDetailAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_heading_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.done_tv);

        TextView txtmsg = (TextView) canceldialog.findViewById(R.id.txtmsg);
        TextView bodymsg = (TextView) canceldialog.findViewById(R.id.bodymsg);
        txtmsg.setText(""+getResources().getString(R.string.tripstart));
        bodymsg.setText(""+getResources().getString(R.string.tripstarted));
        yes_tv.setText(""+getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                if (NotificationUtils.r!=null&&NotificationUtils.r.isPlaying()){
                    NotificationUtils.r.stop();
                }


            }
        });
        canceldialog.show();


    }
    private void tripFinish() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(DriverDetailAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_heading_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.done_tv);

        TextView txtmsg = (TextView) canceldialog.findViewById(R.id.txtmsg);
        TextView bodymsg = (TextView) canceldialog.findViewById(R.id.bodymsg);
        txtmsg.setText(""+getResources().getString(R.string.ridecomp));
        bodymsg.setText(""+getResources().getString(R.string.yourridefinish));
        yes_tv.setText(""+getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                if (NotificationUtils.r!=null&&NotificationUtils.r.isPlaying()){
                    NotificationUtils.r.stop();
                }
                Intent i = new Intent(DriverDetailAct.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);

            }
        });
        canceldialog.show();


    }

    private void driverisArrived() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(DriverDetailAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_heading_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.done_tv);

        TextView txtmsg = (TextView) canceldialog.findViewById(R.id.txtmsg);
        TextView bodymsg = (TextView) canceldialog.findViewById(R.id.bodymsg);
        txtmsg.setText(""+getResources().getString(R.string.driverarrived));
        bodymsg.setText(""+getResources().getString(R.string.yourdriverarr));
        yes_tv.setText(""+getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                if (NotificationUtils.r!=null&&NotificationUtils.r.isPlaying()){
                    NotificationUtils.r.stop();
                }


            }
        });
        canceldialog.show();


    }
    public String capText(String data){
        StringBuilder sb = new StringBuilder(data);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }
}
