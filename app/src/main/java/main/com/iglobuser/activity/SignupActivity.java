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
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;

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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import main.com.iglobuser.constant.CountryBean;
import main.com.iglobuser.constant.GPSTracker;
import main.com.iglobuser.constant.MyLanguageSession;
import main.com.iglobuser.constant.MySession;
import main.com.iglobuser.restapi.ApiClient;

public class SignupActivity extends AppCompatActivity {
    private Button signupbut;
    private RelativeLayout backbut;
    private EditText city_et,state_et,first_name, last_name,  password_et, email_et;
    private String first_name_str = "", last_name_str = "", phone_et_str = "", password_str = "", email_str = "";
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 0; // in Milliseconds
    LocationManager locationManager;
    Location location;
    private double latitude=0,longitude=0;
    GPSTracker gpsTracker;
    MySession mySession;
    ACProgressCustom ac_dialog;
    private TextView phone_et;
    private String otp_str="",country_code_str="",mobile_str_t="",mobile_str="",country_str="",city_str="",state_str="";
    CountryCodePicker ccp;
    EditText otp_edt;
    private Dialog confirmdialog;
    CountDownTimer yourCountDownTimer;
    ArrayList<CountryBean> countryBeanArrayList,statelistbean,citylistbean;
    CountryListAdapter countryListAdapter;
    private Spinner country_spn,state_spn,city_spn;
    private String language = "";
    MyLanguageSession myLanguageSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_signup);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (myLanguageSession.getLanguage().equalsIgnoreCase("ar")) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(SignupActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SignupActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        idint();
        clickevent();
        new GetCountryList().execute();
    }

    private void clickevent() {
        backbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        signupbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                String firebase_regid = pref.getString("regId", null);

                state_str = state_et.getText().toString();
                city_str = city_et.getText().toString();
                first_name_str = first_name.getText().toString();
                last_name_str = last_name.getText().toString();
                phone_et_str = phone_et.getText().toString();
                password_str = password_et.getText().toString();
                email_str = email_et.getText().toString();

                if (first_name_str == null || first_name_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this,getResources().getString(R.string.plsenfrst),Toast.LENGTH_LONG).show();

                } else if (last_name_str == null || last_name_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this,getResources().getString(R.string.plsenlst),Toast.LENGTH_LONG).show();

                } else if (phone_et_str == null || phone_et_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this,getResources().getString(R.string.plsenterphone),Toast.LENGTH_LONG).show();

                }
                else if (email_str == null || email_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this,getResources().getString(R.string.plsemail),Toast.LENGTH_LONG).show();

                }else if (password_str == null || password_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this,getResources().getString(R.string.plsenpass),Toast.LENGTH_LONG).show();

                }else if (country_str == null || country_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this,getResources().getString(R.string.plsselecountry),Toast.LENGTH_LONG).show();

                }else if (state_str == null || state_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this,getResources().getString(R.string.enter_state),Toast.LENGTH_LONG).show();

                }else if (city_str == null || city_str.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this,getResources().getString(R.string.entercity),Toast.LENGTH_LONG).show();

                }
                else {
                    callSignupapi(first_name_str,last_name_str,phone_et_str,email_str, password_str,firebase_regid,""+latitude,""+longitude,"USER",country_str,state_str,city_str);

                }

            }
        });
    }

    private void idint() {
        country_spn = findViewById(R.id.country_spn);
        state_spn = findViewById(R.id.state_spn);
        city_spn = findViewById(R.id.city_spn);
        password_et = findViewById(R.id.password_et);
        state_et = findViewById(R.id.state_et);
        city_et = findViewById(R.id.city_et);
        phone_et = findViewById(R.id.phone_et);
        email_et = findViewById(R.id.email_et);
        last_name = findViewById(R.id.last_name);
        first_name = findViewById(R.id.first_name);
        backbut = findViewById(R.id.backbut);
        signupbut = findViewById(R.id.signupbut);
/*
        phone_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobVeriPopup();
            }
        });
*/


        country_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (countryBeanArrayList != null && !countryBeanArrayList.isEmpty()) {
                }
                if (countryBeanArrayList.get(position).getId() == null || countryBeanArrayList.get(position).getId().equalsIgnoreCase("0")) {

                } else {
                    country_str = countryBeanArrayList.get(position).getCountry();


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
/*
    private void mobVeriPopup() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(SignupActivity.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.custom_verilay);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView no_tv = (TextView) dialogSts.findViewById(R.id.no_tv);
        final TextView yes_tv = (TextView) dialogSts.findViewById(R.id.yes_tv);
        final EditText enter_number = (EditText) dialogSts.findViewById(R.id.enter_number);
        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
                finish();
            }
        });
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
                // mobile_str = enter_number.getText().toString();



                Intent i = new Intent(SignupActivity.this, MobileVerificationActivity.class);
                startActivity(i);
            }
        });

        dialogSts.show();


    }
*/


    private void mobVeriPopup() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(SignupActivity.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.custom_twillo_verilay);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView no_tv = (TextView) dialogSts.findViewById(R.id.no_tv);
        final TextView yes_tv = (TextView) dialogSts.findViewById(R.id.yes_tv);
        final EditText enter_number = (EditText) dialogSts.findViewById(R.id.enter_number);
        ccp = dialogSts.findViewById(R.id.ccp);
        country_code_str = ccp.getSelectedCountryCode();
        Log.e("Country Code"," >"+country_code_str);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
//                Toast.makeText(SignupActivity.this, "Updated " + selectedCountry.getPhoneCode(), Toast.LENGTH_SHORT).show();
                System.out.println("----selectedCountry 1--- " + selectedCountry.getName());
                System.out.println("----selectedCountry 1--- " + selectedCountry.getIso());
                System.out.println("----selectedCountry 1--- " + selectedCountry.getPhoneCode());
                country_code_str = selectedCountry.getPhoneCode();

                Log.e("Country Change Code"," >"+country_code_str);
            }
        });

        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
                finish();
            }
        });
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
                // mobile_str = enter_number.getText().toString();
                String mobile_s = enter_number.getText().toString();
                mobile_str = mobile_s;
                mobile_str_t = country_code_str+mobile_s;
                if (mobile_str_t == null || mobile_str_t.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.entermobile), Toast.LENGTH_LONG).show();
                } else {
                    new SendOtp().execute();
                }


               /* Intent i = new Intent(SignupAct.this, MobileVerificationActivity.class);
                startActivity(i);*/
            }
        });

        dialogSts.show();


    }

    private class SendOtp extends AsyncTask<String, String, String> {
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
//http://halatx.halasmart.com/hala/webservice/mobile_verify?mobile=8889994272
            try {
                String postReceiverUrl = BaseUrl.baseurl + "mobile_verify?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("mobile", mobile_str_t);

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
                Log.e("Send Otp Response", ">>>>>>>>>>>>" + response);
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
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        otp_str = jsonObject.getString("verify_code");
                        enerOtpLay();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }


    private void enerOtpLay() {


        //   Log.e("War Msg in dialog", war_msg);
        confirmdialog = new Dialog(SignupActivity.this);
        confirmdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmdialog.setCancelable(false);
        confirmdialog.setContentView(R.layout.custom_confirmotplay);
        confirmdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        otp_edt = (EditText) confirmdialog.findViewById(R.id.otp_edt);
        final TextView remainingtime = (TextView) confirmdialog.findViewById(R.id.remainingtime);
        TextView confirm = (TextView) confirmdialog.findViewById(R.id.confirm);
        TextView cancel = (TextView) confirmdialog.findViewById(R.id.cancel);
        TextView resendotp = (TextView) confirmdialog.findViewById(R.id.resendotp);
        yourCountDownTimer = new CountDownTimer(150000, 1000) {

            public void onTick(long millisUntilFinished) {
                String text = String.format(Locale.getDefault(), "Remaining %02d min: %02d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                remainingtime.setText(text);
                // remainingtime.setText(""+ millisUntilFinished / 1000);
            }

            public void onFinish() {


                //   notfoundpopup();

            }
        }.start();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = otp_edt.getText().toString();
                if (otp == null || otp.equalsIgnoreCase("")) {
                    Toast.makeText(SignupActivity.this,getResources().getString(R.string.enterotp),Toast.LENGTH_LONG).show();
                } else {
                    if (otp_str.equalsIgnoreCase(otp)) {
                        confirmdialog.dismiss();
                        phone_et.setText("" + mobile_str);

                    }
                    else {
                        phone_et.setText("");
                        Toast.makeText(SignupActivity.this,getResources().getString(R.string.otpnotmatch),Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmdialog.dismiss();
            }
        });

        resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmdialog.dismiss();
                new SendOtp().execute();
            }
        });
        confirmdialog.show();


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
        if (MobileVerificationActivity.phoneNumberString!=null&&!MobileVerificationActivity.phoneNumberString.equalsIgnoreCase("")){
            phone_et.setText("" + MobileVerificationActivity.phoneNumberString);
            MobileVerificationActivity.phoneNumberString="";
        }
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
    private void checkGps() {
        gpsTracker = new GPSTracker(SignupActivity.this);
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
    private void callSignupapi(String first_name_str, String last_name_str, String phone_et_str, String email_str, String password_str, String firebase_regid, String lat, String lon, String user, String country_str, String state_str, String city_str) {
//http://testing.bigclicki.com/webservice/loginapp?email=0&password=0

        if (ac_dialog != null) {
            ac_dialog.show();
        }

        Call<ResponseBody> call = ApiClient.getApiInterface().SignupCall(first_name_str, last_name_str,phone_et_str,email_str,password_str,firebase_regid,"",lat,lon,user,country_str,state_str,city_str,"","","");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject object = new JSONObject(responseData);
                        Log.e("SignupCall >", " >" + responseData);
                        if (object.getString("status").equals("1"))
                        {

                            mySession.setlogindata(responseData);
                            mySession.signinusers(true);
                            if (ac_dialog != null) {
                                ac_dialog.dismiss();
                            }

                            Intent i = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();

                        }
                        else if (object.getString("result").trim().equalsIgnoreCase("email already exist")){
                            Toast.makeText(SignupActivity.this,getResources().getString(R.string.emailalreadyexist),Toast.LENGTH_LONG).show();
                            if (ac_dialog != null) {
                                ac_dialog.dismiss();
                            }

                        }
                        else if (object.getString("result").trim().equalsIgnoreCase("mobile already exist")){
                            Toast.makeText(SignupActivity.this,getResources().getString(R.string.mobilealreadyexist),Toast.LENGTH_LONG).show();
                            if (ac_dialog != null) {
                                ac_dialog.dismiss();
                            }

                        }

                        else {
                            if (ac_dialog != null) {
                                ac_dialog.dismiss();
                            }

                            Toast.makeText(SignupActivity.this,getResources().getString(R.string.invalidcredential),Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        if (ac_dialog != null) {
                            ac_dialog.dismiss();
                        }

                        e.printStackTrace();
                    } catch (JSONException e) {
                        if (ac_dialog != null) {
                            ac_dialog.dismiss();
                        }

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



    private class GetCountryList extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // prgressbar.setVisibility(View.VISIBLE);
            if(ac_dialog!=null){
                ac_dialog.show();
            }

            countryBeanArrayList = new ArrayList<>();


            CountryBean countryListBean = new CountryBean();
            countryListBean.setId("0");
            countryListBean.setCountry("Country");
            countryListBean.setCurrency("");
            countryBeanArrayList.add(countryListBean);

            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/country_list
            try {
                String postReceiverUrl = BaseUrl.baseurl + "countrys";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();

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
                Log.e("Json Country Response", ">>>>>>>>>>>>" + response);
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
            // prgressbar.setVisibility(View.GONE);
            if(ac_dialog!=null){
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("status");
                    if (message.equalsIgnoreCase("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            CountryBean countryBean = new CountryBean();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            countryBean.setId(jsonObject1.getString("id"));
                            countryBean.setCountry(jsonObject1.getString("name"));
                            countryBean.setCurrency("");
                            countryBeanArrayList.add(countryBean);
                        }

                        countryListAdapter = new CountryListAdapter(SignupActivity.this, countryBeanArrayList);
                        country_spn.setAdapter(countryListAdapter);

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public class CountryListAdapter extends BaseAdapter {
        Context context;

        LayoutInflater inflter;
        private ArrayList<CountryBean> values;

        public CountryListAdapter(Context applicationContext, ArrayList<CountryBean> values) {
            this.context = applicationContext;
            this.values = values;

            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {

            return values == null ? 0 : values.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.spinner_lay, null);

            TextView names = (TextView) view.findViewById(R.id.pname);
            //  TextView countryname = (TextView) view.findViewById(R.id.countryname);


            names.setText(values.get(i).getCountry());


            return view;
        }
    }


}
