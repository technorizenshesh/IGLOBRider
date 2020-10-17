package main.com.iglobuser.draweractivity;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

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

import de.hdodenhof.circleimageview.CircleImageView;
import main.com.iglobuser.Fragments.FragmentLanguage;
import main.com.iglobuser.MainActivity;
import main.com.iglobuser.R;
import main.com.iglobuser.activity.AboutUsAct;
import main.com.iglobuser.activity.EmergencyActivity;
import main.com.iglobuser.activity.FavoriteDrivers;
import main.com.iglobuser.activity.InviteEarnAct;
import main.com.iglobuser.activity.LoginAct;
import main.com.iglobuser.activity.NotificationAct;
import main.com.iglobuser.activity.PrivacyPolicyAct;
import main.com.iglobuser.activity.ProfileAct;
import main.com.iglobuser.activity.RideHistory;
import main.com.iglobuser.activity.SupportAct;
import main.com.iglobuser.activity.TermsConditions;
import main.com.iglobuser.activity.WalletAct;
import main.com.iglobuser.app.Config;
import main.com.iglobuser.constant.BaseUrl;
import main.com.iglobuser.constant.MyLanguageSession;
import main.com.iglobuser.constant.MySession;
import main.com.iglobuser.paymentclasses.MyaddedCards;
import main.com.iglobuser.paymentclasses.SaveCardDetail;
import main.com.iglobuser.service.TrackingService;


public class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private DrawerLayout drawer_layout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationview;
    boolean exit = false;
    MySession mySession;
    private LinearLayout aboutuslay, privacylay, termslay, invitelay, emergencycontact, notificationlay, supportlay, add_card_lay, mywallet, ridehistory, logout, adddriver, myprofile;
    private TextView mywalletmoney;
    public static String Card_Added_Sts = "", promo_code = "", card_base_id = "";

    private GoogleApiClient mGoogleApiClient;
    private String user_log_data = "", user_id = "";
    private TextView user_name;
    private CircleImageView user_imguser_img;
    private String language = "", cust_id = "";
    MyLanguageSession myLanguageSession;
    private LinearLayout language_lay;
    private Switch swt_vip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, TrackingService.class));
        } else {
            startService(new Intent(this, TrackingService.class));
        }
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_base);
        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        if (user_log_data == null) {

        } else {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_name = findViewById(R.id.user_name);
                    user_id = jsonObject1.getString("id");
                    cust_id = jsonObject1.getString("cust_id");
                    Log.e("COME TRUE ", " >." + jsonObject1.getString("first_name"));
                    user_name.setText("" + jsonObject1.getString("first_name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        idinit();
        adddrawer();
        clickevents();

    }

    private void clickevents() {
        mywallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, WalletAct.class);
                startActivity(i);
            }
        });
        ridehistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, RideHistory.class);
                startActivity(i);
            }
        });
        adddriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, FavoriteDrivers.class);
                startActivity(i);
            }
        });
        myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, ProfileAct.class);
                startActivity(i);
            }
        });
        add_card_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cust_id != null && !cust_id.equalsIgnoreCase("")) {
                    Intent i = new Intent(BaseActivity.this, MyaddedCards.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(BaseActivity.this, SaveCardDetail.class);
                    startActivity(i);
                }

            }
        });
        supportlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, SupportAct.class);
                startActivity(i);
            }
        });
        notificationlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, NotificationAct.class);
                startActivity(i);
            }
        });
        emergencycontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, EmergencyActivity.class);
                startActivity(i);
            }
        });
        invitelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, InviteEarnAct.class);
                startActivity(i);
            }
        });
        termslay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, TermsConditions.class);
                startActivity(i);
            }
        });
        privacylay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, PrivacyPolicyAct.class);
                startActivity(i);
            }
        });
        aboutuslay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseActivity.this, AboutUsAct.class);
                startActivity(i);
            }
        });
        language_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new FragmentLanguage().show(getSupportFragmentManager(),"");
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySession.logoutUser();
                Intent i = new Intent(BaseActivity.this, LoginAct.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });

    }

    private void idinit() {

        aboutuslay = findViewById(R.id.aboutuslay);
        swt_vip = findViewById(R.id.swt_vip);
        privacylay = findViewById(R.id.privacylay);
        termslay = findViewById(R.id.termslay);
        invitelay = findViewById(R.id.invitelay);
        emergencycontact = findViewById(R.id.emergencycontact);
        user_imguser_img = findViewById(R.id.user_imguser_img);
        notificationlay = findViewById(R.id.notificationlay);
        supportlay = findViewById(R.id.supportlay);
        add_card_lay = findViewById(R.id.add_card_lay);
        mywalletmoney = findViewById(R.id.mywalletmoney);
        myprofile = findViewById(R.id.myprofile);
        adddriver = findViewById(R.id.adddriver);
        logout = findViewById(R.id.logout);
        mywallet = findViewById(R.id.mywallet);
        ridehistory = findViewById(R.id.ridehistory);
        language_lay = findViewById(R.id.language_lay);
        swt_vip.setChecked(mySession.isVIP());
        swt_vip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mySession.setVIP(isChecked);
            }
        });
    }


    private void adddrawer() {

        setSupportActionBar(toolbar);
        navigationview = (NavigationView) findViewById(R.id.navigationview);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        drawer_layout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
// toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.MULTIPLY);

     /*   View header = View.inflate(context, R.layout.headerlayout, null);
        navigationview.addHeaderView(header);*/
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
        new GetUserProfile().execute();
    }
    public void onRefreshLanguage(){
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, getResources().getString(R.string.pressagain),
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private class GetUserProfile extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(String... strings) {
//http://technorizen.com/WORKSPACE1/shipper/webservice/get_driver?driver_id=36
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_profile?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("type", "USER");
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
            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {
                    Log.e("DATAUSER ", " >> " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                        String firebase_regid = pref.getString("regId", null);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        MainActivity.amount = jsonObject1.getString("amount");
                        card_base_id = jsonObject1.getString("card_id");
                        Log.e("card_base_id >> ", " >> " + card_base_id);
                        cust_id = jsonObject1.getString("cust_id");
                        promo_code = jsonObject1.getString("promo_code");
                        MainActivity.identity = jsonObject1.getString("identity");
                        String image_url = jsonObject1.getString("image");
                        if (image_url == null || image_url.equalsIgnoreCase("") || image_url.equalsIgnoreCase(BaseUrl.image_baseurl)) {
                        } else {
                            Glide.with(BaseActivity.this)
                                    .load(image_url).placeholder(R.drawable.user)
                                    .into(user_imguser_img);

                        }
                        mywalletmoney.setText("Wallet Balance $" + MainActivity.amount);
                        if (jsonObject1.getString("register_id") == null || jsonObject1.getString("register_id").equalsIgnoreCase("") || jsonObject1.getString("register_id").equalsIgnoreCase("0") || jsonObject1.getString("register_id").equalsIgnoreCase("null")) {

                            firebase_regid = pref.getString("regId", null);
                            if (firebase_regid != null) {
                                new UpdateRegistrationid().execute(firebase_regid);
                            }
                        } else if (firebase_regid != null && !firebase_regid.equalsIgnoreCase("")) {
                            if (jsonObject1.getString("register_id").equalsIgnoreCase(firebase_regid)) {

                            } else {
                                someThingWrong();
                            }
                        }
                        /*JSONArray jsonArray = jsonObject1.getJSONArray("card_detail");
                        if (jsonArray==null||jsonArray.length()==0){
                            Card_Added_Sts ="Not Added";
                        }
                        else {
                            Card_Added_Sts ="Added";
                        }
                        if (jsonObject1.getString("state")==null||jsonObject1.getString("state").equalsIgnoreCase("")||jsonObject1.getString("state").equalsIgnoreCase("0")){
                            profileUpdate();
                        }*/

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    private void profileUpdate() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(BaseActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.confirmlogoutother_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        final TextView body_tv = (TextView) canceldialog.findViewById(R.id.body_tv);
        body_tv.setText("" + getResources().getString(R.string.updateprofile));
        no_tv.setText("" + getResources().getString(R.string.later));
        yes_tv.setText("" + getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                Intent i = new Intent(BaseActivity.this, ProfileAct.class);
                startActivity(i);
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

    private void someThingWrong() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(BaseActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_heading_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView done_tv = (TextView) canceldialog.findViewById(R.id.done_tv);
        final TextView txtmsg = (TextView) canceldialog.findViewById(R.id.txtmsg);
        final TextView bodymsg = (TextView) canceldialog.findViewById(R.id.bodymsg);
        txtmsg.setText("" + getResources().getString(R.string.somethingwrong));
        bodymsg.setText("" + getResources().getString(R.string.loginagain));
        done_tv.setText("" + getResources().getString(R.string.ok));
        done_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                mySession.logoutUser();
                Intent i = new Intent(BaseActivity.this, LoginAct.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });
        canceldialog.show();
    }

    private class UpdateRegistrationid extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            //http://mobileappdevelop.co/NAXCAN/webservice/update_register_id?user_id=31&register_id=1234
            try {
                String postReceiverUrl = BaseUrl.baseurl + "update_register_id?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("register_id", strings[0]);

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
                Log.e("Update_Register_id ", ">>>>>>>>>>>>" + response);
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
            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
            }
        }
    }


}



/*
Fast Ez ===
Please check this apk ====
Now when user cancel the request when driver is not accept the ride in this case not send notification to driver
Now add heat view in driver current position in driver dashboard screen
Now show the license plat number in driver home screen in place of vin number
Now add the code in every input field that starts with first letter capital
Some time slow at the time of accept ==this is internet or server issue some time
Please provide your sound file for driver notification so we add this in driver app

Please check with this driver  ==
Username == 6194105000
Password == 135800



TaxiMania ===
Please check this apk ==
Now show the order screen dynamic in driver app which show canceled booking , completed booking and on going booking list dynamically with user detail
Make order screen dynamic in user app which show canceled booking , completed booking and on going booking list dynamically with user detail

*/