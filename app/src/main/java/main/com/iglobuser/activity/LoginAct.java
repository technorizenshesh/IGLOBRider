package main.com.iglobuser.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import main.com.iglobuser.MainActivity;
import main.com.iglobuser.R;
import main.com.iglobuser.app.Config;
import main.com.iglobuser.constant.ACProgressCustom;
import main.com.iglobuser.constant.BaseUrl;
import main.com.iglobuser.constant.GPSTracker;
import main.com.iglobuser.constant.MyLanguageSession;
import main.com.iglobuser.constant.MySession;
import main.com.iglobuser.restapi.ApiClient;

public class LoginAct extends AppCompatActivity {
    private Button loginbut;
    private RelativeLayout backbut, facebooklay;
    private TextView forgot_tv;
    private EditText phone_et, password_et;
    private String phone_str = "", password_str = "";
    ACProgressCustom ac_dialog;
    MySession mySession;

    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 0; // in Milliseconds
    LocationManager locationManager;
    Location location;
    private double latitude = 0, longitude = 0;
    GPSTracker gpsTracker;

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private RelativeLayout facebook_button;
    String firebase_regid = "", login_sts = "yes", facebook_name = "", facebook_email = "", facebook_id = "", facebook_image = "", face_gender, face_locale, facebook_lastname = "", face_username;
    public static boolean social_login = false;
    private String language = "";
    MyLanguageSession myLanguageSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (myLanguageSession.getLanguage().equalsIgnoreCase("ar")) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

        }


        callbackManager = CallbackManager.Factory.create();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(LoginAct.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LoginAct.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        mySession = new MySession(this);
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();
        checkGps();
        idinti();
        clickevent();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


    }

    private void checkGps() {
        gpsTracker = new GPSTracker(LoginAct.this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            if (latitude == 0.0) {
                latitude = SplashActivity.latitude;
                longitude = SplashActivity.longitude;

            }
        } else {

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

            } else {
                latitude = SplashActivity.latitude;
                longitude = SplashActivity.longitude;
                Log.e("LAT", "" + latitude);
                Log.e("LON", "" + longitude);

            }
        }


    }

    private void clickevent() {

        facebooklay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                firebase_regid = pref.getString("regId", null);

                if (gpsTracker.canGetLocation()) {
                    latitude = gpsTracker.getLatitude();
                    longitude = gpsTracker.getLongitude();
                    if (latitude == 0.0) {
                        latitude = SplashActivity.latitude;
                        longitude = SplashActivity.longitude;

                    }

                } else {
                    latitude = SplashActivity.latitude;
                    longitude = SplashActivity.longitude;

                }

                loginButton.performClick();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookData();
            }
        });
        backbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loginbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                String firebase_regid = pref.getString("regId", null);
                phone_str = phone_et.getText().toString();
                password_str = password_et.getText().toString();
                if (phone_str == null || phone_str.equalsIgnoreCase("")) {
                    Toast.makeText(LoginAct.this, getResources().getString(R.string.entermobile), Toast.LENGTH_LONG).show();
                } else if (password_str == null || password_str.equalsIgnoreCase("")) {
                    Toast.makeText(LoginAct.this, getResources().getString(R.string.enterpass), Toast.LENGTH_LONG).show();
                } else {
                    callapi(phone_str, password_str, "" + firebase_regid, "" + latitude, "" + longitude, "USER");
                }

            }
        });
        forgot_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginAct.this, ForgotPassword.class);
                startActivity(i);
            }
        });
    }

    private void idinti() {
        loginButton = findViewById(R.id.login_button_fb);
        phone_et = findViewById(R.id.phone_et);
        password_et = findViewById(R.id.password_et);
        forgot_tv = findViewById(R.id.forgot_tv);
        backbut = findViewById(R.id.backbut);
        loginbut = findViewById(R.id.loginbut);
        facebooklay = findViewById(R.id.facebooklay);
    }

    private void callapi(String input_mobile, String input_pass_str, String firebase_regid, String lat, String lon, String user) {
//http://testing.bigclicki.com/webservice/loginapp?email=0&password=0
        Log.e("loginCall >", " > FIRST");
        if (ac_dialog != null) {
            ac_dialog.show();
        }

        Call<ResponseBody> call = ApiClient.getApiInterface().loginCall(input_mobile, input_pass_str, firebase_regid, lat, lon, user, login_sts);
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

                            mySession.setlogindata(responseData);
                            mySession.signinusers(true);
                            JSONObject jsonObject1 = object.getJSONObject("result");
                            Intent i = new Intent(LoginAct.this, MainActivity.class);
                            startActivity(i);
                            finish();



                        } else {
                            if (object.getString("result").equalsIgnoreCase("user already logged in")) {
                                alreadyLogin();
                            } else {
                                login_sts = "no";
                                Toast.makeText(LoginAct.this, getResources().getString(R.string.invalidcredential), Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (IOException e) {
                        login_sts = "no";
                        e.printStackTrace();
                    } catch (JSONException e) {
                        login_sts = "no";
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
                login_sts = "no";
                Log.e("TAG", t.toString());
            }
        });
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    private void facebookData() {
        Log.e("hello >>>>>", "call method");

        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("public_profile email");

        if (AccessToken.getCurrentAccessToken() != null) {
            RequestData();
            Log.e("hello >>>>>", "if");
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AccessToken.getCurrentAccessToken() != null) {

                }
            }
        });

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    Log.e("hello >>>>>", "registerCallback");
                    RequestData();
                }
            }

            @Override
            public void onCancel() {
                Log.e("helll", "cancel");
            }

            @Override
            public void onError(FacebookException exception) {

                Log.e("helll", "error>>" + exception.getMessage());

            }
        });
    }

    public void RequestData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                JSONObject json = response.getJSONObject();
                try {
                    if (json != null) {
                        Log.e("Access facebook >>>>", "" + AccessToken.getCurrentAccessToken());
                        Log.e("json facebook >>>>", "" + json);

                        facebook_id = json.getString("id");
                        Log.e("json >>>>", "" + json);
                        if (json.has("email")) {
                            facebook_email = json.getString("email");

                        } else {

                            facebook_email = "";
                        }

                        facebook_image = "http://graph.facebook.com/" + facebook_id + "/picture?type=large&height=320&width=420";
                        Log.e("facebook_image >>", facebook_image);

                        facebook_name = json.getString("first_name");
                        facebook_lastname = json.getString("last_name");


                        face_username = json.getString("name");
                        // face_gender = json.getString("gender");

                        Log.e("facebook_email >>>", "" + facebook_email);
                        Log.e("facebook_name >>>", "" + facebook_name);
                        Log.e("facebook_id >>>", "" + facebook_id);
                        Log.e("facebook_image >>>", "" + facebook_image);
                        Log.e("facebook_name >>>", "" + facebook_name);
                        Log.e("facebook_id >>>", "" + facebook_id);


                        String value_fb = "1";
                        if (facebook_name.length() > 0) {

                            new SocialLogin().execute();
                            // new SocialLogin2().execute();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,name,link,email,picture,gender,locale");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private class SocialLogin extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ac_dialog != null) {
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
// http://mobileappdevelop.co/NAXCAN/webservice/user_social_login?username=vgurjar&social_id=123456&register_id=123&ios_register_id=321&lat=22.123456&lon=75.123456&image=http://technorizen.com/WORKSPACEVIJAY/SNIFF/uploads/images/logo.png
            try {
                String postReceiverUrl = BaseUrl.baseurl + "social_login";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("social_id", facebook_id);
                params.put("first_name", face_username);
                params.put("last_name", "");
                params.put("image", facebook_image);
                params.put("email", facebook_email);
                params.put("register_id", firebase_regid);
                params.put("lat", latitude);
                params.put("lon", longitude);
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
                Log.e("Json Social Response", ">>>>>>>>>>>>" + response);
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
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("message");
                    if (status.equalsIgnoreCase("successfull")) {
                        mySession.setlogindata(result);
                        mySession.signinusers(true);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        String email = jsonObject1.getString("email");
                        String mobile = jsonObject1.getString("mobile");

                        Intent i = new Intent(LoginAct.this, MainActivity.class);
                        startActivity(i);

                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private void alreadyLogin() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(LoginAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.confirmlogoutother_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);

        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_sts = "yes";
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                String firebase_regid = pref.getString("regId", null);
                canceldialog.dismiss();
                callapi(phone_str, password_str, "" + firebase_regid, "" + latitude, "" + longitude, "USER");

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

}
/*
                                app:ccp_customMasterCountries="US,CA,DE,FR,AU,ES,CH,IE,GB,NL,NZ,IT"

*/