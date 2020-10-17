package main.com.iglobuser.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import de.hdodenhof.circleimageview.CircleImageView;
import main.com.iglobuser.R;
import main.com.iglobuser.constant.ACProgressCustom;
import main.com.iglobuser.constant.BaseUrl;
import main.com.iglobuser.constant.DriverDetailBean;
import main.com.iglobuser.constant.GPSTracker;
import main.com.iglobuser.constant.MyLanguageSession;
import main.com.iglobuser.constant.MySession;

public class FavoriteDrivers extends AppCompatActivity {
    private ListView favdrilist;
    private RelativeLayout exit_app_but;
    private FavdriverAdp favdriverAdp;
    ACProgressCustom ac_dialog;
    MySession mySession;
    private String user_log_data = "", user_id = "";


    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 0; // in Milliseconds
    LocationManager locationManager;
    Location location;
    private double latitude = 0, longitude = 0;
    GPSTracker gpsTracker;
    ArrayList<DriverDetailBean> driverDetailBeanArrayList,maindriverDetailBeanArrayList,favdriverlist,favdriverlist1;
    private EditText search_et;
    private ImageView clear_text;
    private String language = "";
    MyLanguageSession myLanguageSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());


        setContentView(R.layout.activity_favorite_drivers);
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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(FavoriteDrivers.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FavoriteDrivers.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


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
        idinti();
        clcikevent();
        checkGps();
        addTextListener();
        if (android.os.Build.MANUFACTURER == "samsung") {
            search_et.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View arg0) {
                    return true;    // return true to say it was handled
                }
            });
        }
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
        new GetFavDrvierAsc().execute();
    }

    private void checkGps() {
        gpsTracker = new GPSTracker(FavoriteDrivers.this);
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
                Log.e("search_etLAT", "" + latitude);
                Log.e("LON", "" + longitude);

            }
        }


    }

    private void clcikevent() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void idinti() {
        clear_text = findViewById(R.id.clear_text_b);
        clear_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_et.setText("");
                if (favdriverAdp==null){

                }
                else {
                    favdriverAdp.filter("");
                }
            }
        });

        search_et = findViewById(R.id.search_et);
        favdrilist = findViewById(R.id.favdrilist);
        exit_app_but = findViewById(R.id.exit_app_but);
        search_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchstr = search_et.getText().toString();
                    if (searchstr==null||searchstr.equalsIgnoreCase("")){

                    }
                    else {

                        InputMethodManager inputManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        if (searchstr.toString().length() > 0) {
                            clear_text.setVisibility(View.VISIBLE);
                            if (favdriverAdp==null){

                            }
                            else {
                                favdriverAdp.filter(searchstr.toString());
                            }

                        } else {
                            clear_text.setVisibility(View.GONE);
                            if (favdriverAdp==null){

                            }
                            else {
                                favdriverAdp.filter("");
                            }

                        }
                    }
                    return true;
                }
                return false;
            }
        });

    }

    public class FavdriverAdp extends BaseAdapter {
        String[] result;
        Context context;
        private LayoutInflater inflater = null;
        ArrayList<DriverDetailBean> driverDetailBeanArrayList;
        ArrayList<DriverDetailBean> searchdriverDetailBeanArrayList;
        public FavdriverAdp(Activity activity, ArrayList<DriverDetailBean> driverDetailBeanArrayList) {
            this.context = activity;
            this.driverDetailBeanArrayList = driverDetailBeanArrayList;

            this.searchdriverDetailBeanArrayList = new ArrayList<>();
            searchdriverDetailBeanArrayList.addAll(driverDetailBeanArrayList);
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return driverDetailBeanArrayList == null ? 0 : driverDetailBeanArrayList.size();
        }
        // return driverDetailBeanArrayList == null ? 0 : driverDetailBeanArrayList.size();        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder {

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final Holder holder;
            holder = new Holder();
            View rowView;

            rowView = inflater.inflate(R.layout.custom_fav_lay, null);
            final TextView status_tv = rowView.findViewById(R.id.status_tv);
            TextView cardetail = rowView.findViewById(R.id.cardetail);
            TextView driver_name = rowView.findViewById(R.id.driver_name);
            TextView send_booking = rowView.findViewById(R.id.send_booking);
            TextView distance_tv = rowView.findViewById(R.id.distance_tv);
            CircleImageView driver_img = rowView.findViewById(R.id.driver_img);
            if (driverDetailBeanArrayList.get(position).getStatus().equalsIgnoreCase("Active")){
                status_tv.setTextColor(getResources().getColor(R.color.green));
                status_tv.setText(driverDetailBeanArrayList.get(position).getStatus());
            }
            else if (driverDetailBeanArrayList.get(position).getStatus().equalsIgnoreCase("Busy")){
                status_tv.setTextColor(getResources().getColor(R.color.red));
                status_tv.setText(driverDetailBeanArrayList.get(position).getStatus());
            }
            if (driverDetailBeanArrayList.get(position).getOnline_status().equalsIgnoreCase("ONLINE")){

            }
            else {
                status_tv.setTextColor(getResources().getColor(R.color.darkgrey));
                status_tv.setText(driverDetailBeanArrayList.get(position).getOnline_status());
            }
            if (driverDetailBeanArrayList.get(position).getStatus().equalsIgnoreCase("Deactive")){
                status_tv.setTextColor(getResources().getColor(R.color.darkgrey));
                status_tv.setText(driverDetailBeanArrayList.get(position).getStatus());
            }


            distance_tv.setText(driverDetailBeanArrayList.get(position).getDistance()+" km");
            driver_name.setText(driverDetailBeanArrayList.get(position).getFirst_name()+" "+driverDetailBeanArrayList.get(position).getLast_name());
            cardetail.setText("" + driverDetailBeanArrayList.get(position).getCar_model() + "\n" + driverDetailBeanArrayList.get(position).getCar_number().trim() + " , " + driverDetailBeanArrayList.get(position).getCar_color());
            String driver_img_str=driverDetailBeanArrayList.get(position).getImage();

            if (driver_img_str == null || driver_img_str.equalsIgnoreCase("") || driver_img_str.equalsIgnoreCase(BaseUrl.image_baseurl)) {

            } else {
                Picasso.with(FavoriteDrivers.this).load(BaseUrl.image_baseurl + driver_img_str).placeholder(R.drawable.user).into(driver_img);

            }
            send_booking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (driverDetailBeanArrayList.get(position).getStatus().equalsIgnoreCase("Active")&&driverDetailBeanArrayList.get(position).getOnline_status().equalsIgnoreCase("ONLINE")){
                        Intent i = new Intent(FavoriteDrivers.this,SendRequestFavDriver.class);
                        i.putExtra("driver_id",driverDetailBeanArrayList.get(position).getId());
                        startActivity(i);
                    }
                    else {
                        Toast.makeText(FavoriteDrivers.this,getResources().getString(R.string.drivernotavb),Toast.LENGTH_LONG).show();
                    }
                }
            });
            return rowView;
        }
        public void filter(String charText) {
            //charText = charText.toLowerCase(Locale.getDefault());
            if (charText==null){

            }
            else {
                charText = charText.toString().toLowerCase();
                driverDetailBeanArrayList.clear();
                if (charText.length() == 0) {
                    if (favdriverlist1!=null){
                        driverDetailBeanArrayList.addAll(favdriverlist1);
                    }

                } else {
                    for (DriverDetailBean wp : maindriverDetailBeanArrayList) {
                        if (wp.getFirst_name().toLowerCase().startsWith(charText)||wp.getLast_name().toLowerCase().startsWith(charText)||wp.getCar_number().toLowerCase().startsWith(charText)||wp.getMobile().toLowerCase().startsWith(charText))//.toLowerCase(Locale.getDefault())
                        {
                            driverDetailBeanArrayList.add(wp);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        }

    }

    private class GetFavDrvierAsc extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  progressbar.setVisibility(View.VISIBLE);
            driverDetailBeanArrayList = new ArrayList<>();
            maindriverDetailBeanArrayList = new ArrayList<>();
            favdriverlist = new ArrayList<>();
            favdriverlist1 = new ArrayList<>();
            if (ac_dialog != null) {
                ac_dialog.show();
            }
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_favorite_driver?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("lat", latitude);
                params.put("lon", longitude);
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
            Log.e("Fav Driver Response","> "+result);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);


                            DriverDetailBean driverDetailBean = new DriverDetailBean();

                            driverDetailBean.setId(jsonObject1.getString("id"));
                            driverDetailBean.setFirst_name(jsonObject1.getString("first_name"));
                            driverDetailBean.setLast_name(jsonObject1.getString("last_name"));
                            if (jsonObject1.getString("mobile")==null){
                                driverDetailBean.setMobile("");
                            }
                            else {
                                driverDetailBean.setMobile(jsonObject1.getString("mobile"));
                            }

                            if (jsonObject1.getString("car_number")==null){
                                driverDetailBean.setCar_number("");
                            }
                            else {
                                driverDetailBean.setCar_number(jsonObject1.getString("car_number"));
                            }

                            if (jsonObject1.getString("last_name")==null){
                                driverDetailBean.setLast_name("");
                            }
                            else {
                                driverDetailBean.setLast_name(jsonObject1.getString("last_name"));
                            }
                            driverDetailBean.setEmail(jsonObject1.getString("email"));
                            driverDetailBean.setImage(jsonObject1.getString("image"));
                            driverDetailBean.setDistance(jsonObject1.getString("distance"));
                            driverDetailBean.setStatus(jsonObject1.getString("status"));
                            driverDetailBean.setOnline_status(jsonObject1.getString("online_status"));
                            driverDetailBean.setCar_color(jsonObject1.getString("car_color"));
                            driverDetailBean.setCar_image(jsonObject1.getString("car_image"));
                            driverDetailBean.setCar_model(jsonObject1.getString("vehicle_name"));
                            driverDetailBean.setYear_of_manufacture(jsonObject1.getString("year_of_manufacture"));
                            driverDetailBean.setFav_status(jsonObject1.getString("fav_status"));
                            if (!jsonObject1.getString("fav_status").equalsIgnoreCase("No")){
                                favdriverlist.add(driverDetailBean);
                                favdriverlist1.add(driverDetailBean);
                            }

                            maindriverDetailBeanArrayList.add(driverDetailBean);


                        }
                        if (maindriverDetailBeanArrayList!=null&&!maindriverDetailBeanArrayList.isEmpty()) {
                            driverDetailBeanArrayList.addAll(maindriverDetailBeanArrayList);
                           }

                        favdriverAdp = new FavdriverAdp(FavoriteDrivers.this,favdriverlist);
                        favdrilist.setAdapter(favdriverAdp);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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
    public void addTextListener() {
        search_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s==null){

                }
                else {

                    if (s.toString().length() > 0) {
                        clear_text.setVisibility(View.VISIBLE);
                        if (favdriverAdp==null){

                        }
                        else {
                            favdriverAdp.filter(s.toString());
                        }

                    }/**/ else {
                        clear_text.setVisibility(View.GONE);
                        if (favdriverAdp==null){

                        }
                        else {
                            favdriverAdp.filter("");
                        }
                    }




                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

}