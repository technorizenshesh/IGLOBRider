package main.com.iglobuser;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.iglobuser.activity.DriverAcceptStatus;
import main.com.iglobuser.activity.FeedbackUs;
import main.com.iglobuser.activity.ProfileAct;
import main.com.iglobuser.activity.SetLocation;
import main.com.iglobuser.activity.SplashActivity;
import main.com.iglobuser.activity.TripStatusAct;
import main.com.iglobuser.constant.ACProgressCustom;
import main.com.iglobuser.constant.BaseUrl;
import main.com.iglobuser.constant.DriverDetailBean;
import main.com.iglobuser.constant.GPSTracker;
import main.com.iglobuser.constant.MyCarBean;
import main.com.iglobuser.constant.MyLanguageSession;
import main.com.iglobuser.constant.MySession;
import main.com.iglobuser.draglocation.DataParser;
import main.com.iglobuser.draglocation.MyTask;
import main.com.iglobuser.draglocation.WebOperations;
import main.com.iglobuser.draweractivity.BaseActivity;

public class MainActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult> {
    private Integer THRESHOLD = 2;
    private int count = 0, countDrop = 0;
    private FrameLayout contentFrameLayout;
    private GoogleMap gMap;
    GPSTracker gpsTracker;
    private AutoCompleteTextView pickuplocation, dropofflocation;
    private MySession mySession;
    public static double longitude = 0.0, latitude = 0.0, pickup_lat_str = 0, pickup_lon_str = 0, drop_lat_str = 0, drop_lon_str = 0;
    public static String time_zone = "";
    int initial_flag = 0;
    public static boolean mylocset = true;
    String address_complete = "";
    Marker googlemarker_pos, my_job_location_marker;
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 0; // in Milliseconds
    Location location;
    Location location_ar;
    LocationManager locationManager;
    private GoogleApiClient googleApiClient;
    boolean sts;
    ImageView gpslocator, clear_pick_ic, clear_drop_ic, map_ic, pinmarimg;
    ProgressBar progressbar;
    private TextView booknow, bookletter;
    private RecyclerView cartypelist;
    CarHoriZontalLay carHoriZontalLay;
    ArrayList<MyCarBean> myCarBeanArrayList;
    public static String selected_car_id = "";
    public static String user_id = "", amount = "",state_str="", identity = "", date_str = "", time_str = "", ride_status = "", booktype = "", pickuploc_str = "", dropoffloc_str = "";
    ACProgressCustom ac_dialog;
    LatLng picklatLng, droplatlong;
    Marker marker, myloc_marker;
    List<Marker> markerList = new ArrayList<Marker>();
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<DriverDetailBean> driverDetailBeanArrayList;
    private String user_log_data = "";
    ScheduledExecutorService scheduleTaskExecutor;
    String currentVersion;
    private String language = "";
    MyLanguageSession myLanguageSession;
    PolylineOptions lineOptions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        contentFrameLayout = (FrameLayout) findViewById(R.id.contentFrame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_main, contentFrameLayout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (myLanguageSession.getLanguage().equalsIgnoreCase("ar")) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();

        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone();
        time_zone = tz.getID();
        gpsTracker = new GPSTracker(MainActivity.this);
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
                    state_str = jsonObject1.getString("state");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        checklocation();
        idinits();
        if (mySession.getAppUpdate().equalsIgnoreCase("cancel")){
            try {
                currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                Log.e("OnCreate", "Current version " + currentVersion);


            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Log.e("OnCreate EXC", "Current version " + currentVersion);
            }
            Log.e("OnCreate OUT", "Current version " + currentVersion);
        }

        getCurrentLocation();
        autocompleteView();
        clickevetn();
        try {
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("TAG", "Country: " + gpsTracker.getCountry());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        scheduleTaskExecutor.shutdown();
    }

    @Override
    public void onPause() {
        super.onPause();
        // timerObj.cancel();
        scheduleTaskExecutor.shutdown();
    }

    private void initilizeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void idinits() {
        pinmarimg = (ImageView) findViewById(R.id.pinmarimg);
        map_ic = (ImageView) findViewById(R.id.map_ic);
        gpslocator = (ImageView) findViewById(R.id.gpslocator);
        clear_pick_ic = (ImageView) findViewById(R.id.clear_pick_ic);
        clear_drop_ic = (ImageView) findViewById(R.id.clear_drop_ic);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        booknow = (TextView) findViewById(R.id.booknow);
        bookletter = (TextView) findViewById(R.id.bookletter);
        pickuplocation = (AutoCompleteTextView) findViewById(R.id.pickuplocation);
        dropofflocation = (AutoCompleteTextView) findViewById(R.id.dropofflocation);
        cartypelist = (RecyclerView) findViewById(R.id.cartypelist);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        cartypelist.setLayoutManager(horizontalLayoutManagaer);
        new GetCarLists().execute();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;

        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        MainActivity.this, R.raw.stylemap_3));

        gMap.setMyLocationEnabled(true);
        gMap.setBuildingsEnabled(false);
        // gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
        gMap.getUiSettings().setMapToolbarEnabled(false);
        gMap.getUiSettings().setZoomControlsEnabled(false);

        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).flat(true).anchor(0.5f, 0.5f);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        gMap.animateCamera(cameraUpdate);

        gMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                if (googlemarker_pos != null) {
                    googlemarker_pos.setPosition(latLng1);
                }
            }
        });

        gMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                if (initial_flag != 0) {

                    if (mylocset) {
                        LatLng latLng = gMap.getCameraPosition().target;
                        address_complete = loadAddress(latLng.latitude, latLng.longitude);
                        pickuplocation.setText(address_complete);

                    }
                }
                initial_flag++;

            }
        });


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
        if (SetLocation.pickuplocation_str != null && !SetLocation.pickuplocation_str.equalsIgnoreCase("")) {
            dropofflocation.setText("" + SetLocation.pickuplocation_str);
            SetLocation.pickuplocation_str = "";
            dropoffloc_str = dropofflocation.getText().toString();
            Log.e("COME 2", "22" + dropoffloc_str);
            pickuploc_str = pickuplocation.getText().toString();
            if (dropoffloc_str == null || dropoffloc_str.equalsIgnoreCase("") || pickuploc_str.equalsIgnoreCase("")) {
            } else {
                pinmarimg.setVisibility(View.GONE);
                new GetPickRoute().execute();
            }
        }

        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (selected_car_id == null || selected_car_id.equalsIgnoreCase(""))
                {
                    new GetNearestDriverAll().execute();
                } else {
                    new GetNearestDriver().execute();
                }

            }
        }, 0, 8, TimeUnit.SECONDS);

        new GetCurrentBooking().execute();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

    }

    @Override
    public void onLocationChanged(Location location) {


    }

    private void checklocation() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location_ar = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (location_ar != null) {
            //Getting longitude and latitude
            longitude = location_ar.getLongitude();
            latitude = location_ar.getLatitude();
            if (latitude == 0.0) {
                latitude = SplashActivity.latitude;
                longitude = SplashActivity.longitude;

            }
            address_complete = loadAddress(latitude, longitude);
            pickuplocation.setText(address_complete);


        } else {
            System.out.println("----------------geting Location from GPS----------------");
            GPSTracker tracker = new GPSTracker(this);
            location_ar = tracker.getLocation();
            if (location_ar == null) {
                latitude = SplashActivity.latitude;
                longitude = SplashActivity.longitude;
                address_complete = loadAddress(latitude, longitude);
                pickuplocation.setText(address_complete);


            } else {
                longitude = location_ar.getLongitude();
                latitude = location_ar.getLatitude();

                if (latitude == 0.0) {
                    latitude = SplashActivity.latitude;
                    longitude = SplashActivity.longitude;

                }
                address_complete = loadAddress(latitude, longitude);
                pickuplocation.setText(address_complete);


            }
        }
    }

    private class GetCarLists extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myCarBeanArrayList = new ArrayList<>();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_car_list?";
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
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    Log.e("CAR RESPONSE >>", "" + result);
                    if (status.equalsIgnoreCase("1")) {
                        Log.e("CAR RESPONSE 2>>", "" + result);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            MyCarBean myCarBean = new MyCarBean();
                            myCarBean.setId(jsonObject1.getString("id"));
                            myCarBean.setCarname(jsonObject1.getString("car_name"));
                            myCarBean.setCar_image(jsonObject1.getString("car_image"));
                            myCarBean.setDistance("0");
                            myCarBean.setTotal("0");
                            myCarBean.setCab_find("no_cab");
                            myCarBean.setSelected(false);
                            myCarBeanArrayList.add(myCarBean);

                        }

                    }
                    carHoriZontalLay = new CarHoriZontalLay(myCarBeanArrayList);
                    cartypelist.setAdapter(carHoriZontalLay);
                    //cartypelist.scrollToPosition(listPosition);
                    carHoriZontalLay.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }


    private void clickevetn() {
        map_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SetLocation.class);
                i.putExtra("setLoc", "dropoff");
                startActivity(i);
            }
        });
        bookletter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickuploc_str = pickuplocation.getText().toString();
                dropoffloc_str = dropofflocation.getText().toString();
                if (pickuploc_str == null || pickuploc_str.equalsIgnoreCase("")) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.plsselpicklocation), Toast.LENGTH_LONG).show();

                } else if (dropoffloc_str == null || dropoffloc_str.equalsIgnoreCase("")) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.seldroploc), Toast.LENGTH_LONG).show();
                } else if (selected_car_id == null || selected_car_id.equalsIgnoreCase("")) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.selcartype), Toast.LENGTH_LONG).show();
                } else {
                    booktype = "Letter";
                    selectDateTime();

                }

            }
        });

        booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickuploc_str = pickuplocation.getText().toString();
                dropoffloc_str = dropofflocation.getText().toString();
                if (pickuploc_str == null || pickuploc_str.equalsIgnoreCase("")) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.plsselpicklocation), Toast.LENGTH_LONG).show();

                } else if (dropoffloc_str == null || dropoffloc_str.equalsIgnoreCase("")) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.seldroploc), Toast.LENGTH_LONG).show();
                } else if (selected_car_id == null || selected_car_id.equalsIgnoreCase("")) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.selcartype), Toast.LENGTH_LONG).show();
                } else {
                    booktype = "Now";
                    new GetPickUp().execute();
                }


            }
        });


        clear_pick_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickuplocation.setText("");
            }
        });
        clear_drop_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dropofflocation.setText("");
            }
        });
        gpslocator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gMap == null) {

                } else {
                    Location loc = gMap.getMyLocation();
                    if (loc != null) {
                        LatLng latLang = new LatLng(loc.getLatitude(), loc
                                .getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLang, 17);
                        gMap.animateCamera(cameraUpdate);
                        address_complete = loadAddress(latitude, longitude);
                        pickuplocation.setText(address_complete);

                    }

                }
            }
        });
    }

    private String loadAddress(double latitude, double longitude) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current", strReturnedAddress.toString());
            } else {
                Log.w("My Current", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current", "Canont get Address!");
        }
        return strAdd;
    }

    private void loadData(String s) {
        try {
            if (count == 0) {
                List<String> l1 = new ArrayList<>();
                if (s == null) {

                } else {
                    l1.add(s);
                    sts = true;
                    GeoAutoCompleteAdapter ga = new GeoAutoCompleteAdapter(MainActivity.this, l1, "" + latitude, "" + longitude);
                    pickuplocation.setAdapter(ga);

                }

            }
            count++;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDataDrop(String s) {
        try {
            if (countDrop == 0) {
                List<String> l1 = new ArrayList<>();
                if (s == null) {

                } else {
                    l1.add(s);
                    sts = false;
                    GeoAutoCompleteAdapter ga = new GeoAutoCompleteAdapter(MainActivity.this, l1, "" + latitude, "" + longitude);
                    dropofflocation.setAdapter(ga);

                }

            }
            countDrop++;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void autocompleteView() {
        pickuplocation.setThreshold(THRESHOLD);
        pickuplocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sts = true;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0) {
                    //  clear_pick_ic.setVisibility(View.VISIBLE);
                    //  picmap_ic.setVisibility(View.GONE);
                    loadData(pickuplocation.getText().toString());
                } else {
                    //clear_pick_ic.setVisibility(View.INVISIBLE);
                    //  picmap_ic.setVisibility(View.VISIBLE);

                }
            }
        });
        dropofflocation.setThreshold(THRESHOLD);
        dropofflocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sts = false;
                mylocset = false;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {


                if (s.length() > 0) {
                    // clear_drop_ic.setVisibility(View.VISIBLE);
                    //map_ic.setVisibility(View.GONE);
                    loadDataDrop(dropofflocation.getText().toString());
                } else {
                    //clear_drop_ic.setVisibility(View.INVISIBLE);
                    //map_ic.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    class GeoAutoCompleteAdapter extends BaseAdapter implements Filterable {

        private Activity context;
        private List<String> l2 = new ArrayList<>();
        private LayoutInflater layoutInflater;
        private WebOperations wo;
        private String lat, lon;

        public GeoAutoCompleteAdapter(Activity context, List<String> l2, String lat, String lon) {
            this.context = context;
            this.l2 = l2;
            this.lat = lat;
            this.lon = lon;
            layoutInflater = LayoutInflater.from(context);
            wo = new WebOperations(context);
        }

        @Override
        public int getCount() {

            return l2.size();
        }

        @Override
        public Object getItem(int i) {
            return l2.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            view = layoutInflater.inflate(R.layout.geo_search_result, viewGroup, false);
            TextView geo_search_result_text = (TextView) view.findViewById(R.id.geo_search_result_text);
            try {
                geo_search_result_text.setText(l2.get(i));
                geo_search_result_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        InputMethodManager inputManager = (InputMethodManager)
                                getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

                        if (sts) {
                            if (l2 == null || l2.isEmpty()) {

                            } else {
                                pickuplocation.setText("" + l2.get(i));
                                pickuplocation.dismissDropDown();
                                dropoffloc_str = dropofflocation.getText().toString();
                                pickuploc_str = pickuplocation.getText().toString();
                                if (dropoffloc_str == null || dropoffloc_str.equalsIgnoreCase("") || pickuploc_str == null || pickuploc_str.equalsIgnoreCase("")) {
                                } else {
                                    pinmarimg.setVisibility(View.GONE);

                                    new GetPickRoute().execute();
                                }
                            }


                        } else {
                            if (l2 == null || l2.isEmpty()) {

                            } else {
                                dropofflocation.setText("" + l2.get(i));
                                dropofflocation.dismissDropDown();
                                dropoffloc_str = dropofflocation.getText().toString();
                                pickuploc_str = pickuplocation.getText().toString();
                                if (dropoffloc_str == null || dropoffloc_str.equalsIgnoreCase("") || pickuploc_str == null || pickuploc_str.equalsIgnoreCase("")) {
                                } else {
                                    pinmarimg.setVisibility(View.GONE);

                                    new GetPickRoute().execute();
                                }

                            }

                        }

                    }
                });

            } catch (Exception e) {

            }

            return view;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        wo.setUrl("https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyDQhXBxYiOPm-aGspwuKueT3CfBOIY3SJs&input=" + constraint.toString().trim().replaceAll(" ", "+") + "&location=" + lat + "," + lon + "+&radius=20000&types=establishment&sensor=true");
                        String result = null;
                        try {
                            result = new MyTask(wo, 3).execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        parseJson(result);


                        // Assign the data to the FilterResults
                        filterResults.values = l2;
                        filterResults.count = l2.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    if (results != null && results.count != 0) {
                        l2 = (List) results.values;
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }

        private void parseJson(String result) {
            try {
                l2 = new ArrayList<>();
                JSONObject jk = new JSONObject(result);

                JSONArray predictions = jk.getJSONArray("predictions");
                for (int i = 0; i < predictions.length(); i++) {
                    JSONObject js = predictions.getJSONObject(i);
                    l2.add(js.getString("description"));
                }
            } catch (Exception e) {
                e.printStackTrace();
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

    class CarHoriZontalLay extends RecyclerView.Adapter<CarHoriZontalLay.MyViewHolder> {
        ArrayList<MyCarBean> myCarBeanArrayList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView carname, total_dis, total_amt, eta_min;
            ImageView carimage;
            View viewLine, small_verview;
            RelativeLayout backview;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.viewLine = (View) itemView.findViewById(R.id.viewLine);
                this.carname = (TextView) itemView.findViewById(R.id.carname);
                this.backview = itemView.findViewById(R.id.backview);
                this.carimage = itemView.findViewById(R.id.carimage);

            }
        }

        public CarHoriZontalLay(ArrayList<MyCarBean> myCarBeanArrayList) {
            this.myCarBeanArrayList = myCarBeanArrayList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_car_lay, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
            if (listPosition == myCarBeanArrayList.size() - 1) {
                holder.viewLine.setVisibility(View.GONE);
            }
            holder.carname.setText("" + myCarBeanArrayList.get(listPosition).getCarname());
            if (myCarBeanArrayList.get(listPosition).isSelected()) {
                holder.backview.setBackgroundResource(R.drawable.selected_round_back);
            } else {
                holder.backview.setBackgroundResource(R.drawable.unselectedback);
            }
            String car_url = myCarBeanArrayList.get(listPosition).getCar_image();
            if (car_url == null || car_url.equalsIgnoreCase("") || car_url.equalsIgnoreCase(BaseUrl.baseurl)) {
            } else {
                Picasso.with(MainActivity.this).load(myCarBeanArrayList.get(listPosition).getCar_image()).into(holder.carimage);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = listPosition;
                    for (int k = 0; k < myCarBeanArrayList.size(); k++) {
                        if (pos == k) {
                            if (myCarBeanArrayList.get(k).isSelected()) {
                                myCarBeanArrayList.get(k).setSelected(false);
                                selected_car_id = "";
                            } else {
                                myCarBeanArrayList.get(k).setSelected(true);
                                selected_car_id = myCarBeanArrayList.get(k).getId();
                            }
                        } else {
                            myCarBeanArrayList.get(k).setSelected(false);
                        }
                    }
                    carHoriZontalLay = new CarHoriZontalLay(myCarBeanArrayList);
                    cartypelist.setAdapter(carHoriZontalLay);
                    cartypelist.scrollToPosition(listPosition);
                    carHoriZontalLay.notifyDataSetChanged();
                    //  selected_service_fare.setVisibility(View.VISIBLE);

                }
            });


        }

        @Override
        public int getItemCount() {
            // return 4;
            return myCarBeanArrayList == null ? 0 : myCarBeanArrayList.size();
        }
    }

    private class GetPickUp extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  progressbar.setVisibility(View.VISIBLE);
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
            String address1 = pickuploc_str.trim().replaceAll(" ", "+");
            String address = address1.trim().replaceAll(",", "+");
            String postReceiverUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + getResources().getString(R.string.googlekey_other);

            try {
                //  String postReceiverUrl = "https://api.ctlf.co.uk/";
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
            Log.e("GetPickUp","=======>"+result);
            if (result == null) {
            } else if (result.equalsIgnoreCase("")) {
            } else {
                JSONObject location = null;
                try {
                    location = new JSONObject(result).getJSONArray("results")
                            .getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location");
                    pickup_lat_str = location.getDouble("lat");
                    pickup_lon_str = location.getDouble("lng");
                    new GetDropOffLat().execute();
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }
    }

    private class GetDropOffLat extends AsyncTask<String, String, String> {
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
            String address1 = dropoffloc_str.trim().replaceAll(" ", "+");
            String address = address1.trim().replaceAll(",", "+");
            String postReceiverUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + getResources().getString(R.string.googlekey_other);

            try {
                //  String postReceiverUrl = "https://api.ctlf.co.uk/";
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
            //   progressbar.setVisibility(View.GONE);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {

            } else if (result.equalsIgnoreCase("")) {

            } else {
                JSONObject location = null;
                try {
                    location = new JSONObject(result).getJSONArray("results")
                            .getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location");


                    //    pickup_lat_str,pickup_lon_str,drop_lat_str,drop_lon_str,
                    drop_lat_str = location.getDouble("lat");
                    drop_lon_str = location.getDouble("lng");
                    Intent i = new Intent(MainActivity.this, TripStatusAct.class);
                    startActivity(i);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }


    private class GetPickRoute extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressbar.setVisibility(View.VISIBLE);

            try {
                if (ac_dialog != null) {
                    ac_dialog.show();
                }

                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String address1 = pickuploc_str.trim().replaceAll(" ", "+");
            String address = address1.trim().replaceAll(",", "+");
            String postReceiverUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + getResources().getString(R.string.googlekey_other);

            try {
                //  String postReceiverUrl = "https://api.ctlf.co.uk/";
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

            } else if (result.equalsIgnoreCase("")) {

            } else {
                JSONObject location = null;
                try {
                    location = new JSONObject(result).getJSONArray("results")
                            .getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location");


                    //    pickup_lat_str,pickup_lon_str,drop_lat_str,drop_lon_str,
                    pickup_lat_str = location.getDouble("lat");
                    pickup_lon_str = location.getDouble("lng");
                    new GetDropOffLatRoute().execute();

                } catch (JSONException e) {
                    if (ac_dialog != null) {
                        ac_dialog.dismiss();
                    }
                    e.printStackTrace();

                }

            }

        }
    }

    private class GetDropOffLatRoute extends AsyncTask<String, String, String> {
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
            String address1 = dropoffloc_str.trim().replaceAll(" ", "+");
            String address = address1.trim().replaceAll(",", "+");
            String postReceiverUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + getResources().getString(R.string.googlekey_other);

            try {
                //  String postReceiverUrl = "https://api.ctlf.co.uk/";
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
            //progressbar.setVisibility(View.GONE);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {

            } else if (result.equalsIgnoreCase("")) {

            } else {
                JSONObject location = null;
                try {
                    location = new JSONObject(result).getJSONArray("results")
                            .getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location");
                    drop_lat_str = location.getDouble("lat");
                    drop_lon_str = location.getDouble("lng");
                    if (gMap == null) {
                    } else {
                        gMap.clear();
                        if (latitude != 0) {
                            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).flat(true).anchor(0.5f, 0.5f);
                        }
                        //  MarkerOptions myloc = new MarkerOptions().position(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str)).icon(BitmapDescriptorFactory.fromBitmap(myLocationWindow("" + distan_str, pickuploc_str, distan_str_km)));
                        //  MarkerOptions myjob = new MarkerOptions().position(new LatLng(MainActivity.drop_lat_str, MainActivity.drop_lon_str)).icon(BitmapDescriptorFactory.fromBitmap(myJobWindow(Favourites.droplocfav_type, dropoffloc_str))).snippet("myjobloc");

                        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(pickup_lat_str, pickup_lon_str));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                        gMap.animateCamera(zoom);
                        //gMap.addMarker(myloc);
                        //  my_job_location_marker = gMap.addMarker(myjob);
                        gMap.moveCamera(center);
                        String url = getUrl(new LatLng(pickup_lat_str, pickup_lon_str), new LatLng(drop_lat_str, drop_lon_str));
                        FetchUrl FetchUrl = new FetchUrl();
                        FetchUrl.execute(url);
                        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                .include(new LatLng(pickup_lat_str, pickup_lon_str))
                                .include(new LatLng(drop_lat_str, drop_lon_str))
                                .build();
                        int width = getResources().getDisplayMetrics().widthPixels;
                        int padding = (int) (width * 0.20); // offset from edges of the map 12% of screen
                        gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=" + getResources().getString(R.string.googlekey_other);
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            ArrayList<LatLng> animation_list = null;
            if (result == null) {


            } else {

            }

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                animation_list = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    animation_list.add(position);
                    points.add(position);
                    if (j == 0) {
                        picklatLng = position;
                    }
                    if (j == (path.size() - 1)) {
                        droplatlong = position;
                    }

                }
                Log.e("SIZE POINT", " True >> " + points.size());
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
            }
         AddLine();
        }
    }
    private void AddLine(){
        if (lineOptions != null) {
            gMap.addPolyline(lineOptions);
            MarkerOptions pick = new MarkerOptions().position(picklatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pick_marker)).flat(true).anchor(0.5f, 0.5f);
            MarkerOptions drop = new MarkerOptions().position(droplatlong).icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker)).flat(true).anchor(0.5f, 0.5f);
            gMap.addMarker(pick);
            gMap.addMarker(drop);
        }
    }

    private class GetNearestDriver extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            driverDetailBeanArrayList = new ArrayList<>();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String postReceiverUrl = BaseUrl.baseurl + "available_car_driver?";
                String CheckUrl = BaseUrl.baseurl + "available_car_driver?car_type_id=" + selected_car_id + "&latitude=" + latitude + "&longitude=" + longitude + "&user_id=" + MainActivity.user_id;

                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();


                if (latitude == 0.0) {
                    params.put("latitude", pickup_lat_str);
                    params.put("longitude", pickup_lon_str);
                } else {
                    params.put("latitude", latitude);
                    params.put("longitude", longitude);
                }
                params.put("car_type_id", selected_car_id);
                params.put("user_id", user_id);
                params.put("timezone", time_zone);

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
            Log.e("AvailableCars","=====>"+result);
            super.onPostExecute(result);
            if (result == null) {
                if (gMap == null) {

                } else {
                    gMap.clear();
                }
            } else if (result.equalsIgnoreCase("null")) {
                if (gMap == null) {

                } else {
                    gMap.clear();
                }

            } else if (result.isEmpty()) {
                if (gMap == null) {

                } else {
                    gMap.clear();
                }
            } else {
                try {
                    JSONObject jsonobj = new JSONObject(result);
                    String msg = jsonobj.getString("message");
                    if (msg.equalsIgnoreCase("success")) {
                        if (gMap != null) {
                            gMap.clear();
                            if (marker != null) {
                                markerList.clear();
                            }
                            AddLine();
                        }
                        JSONArray jsonArray = jsonobj.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String results = jsonObject.getString("result");
                            if (results.equalsIgnoreCase("successful")) {
                                JSONObject esti = jsonArray.getJSONObject(0);
                                DriverDetailBean driverDetailBean = new DriverDetailBean();
                                driverDetailBean.setId(jsonObject.getString("id"));
                                if (jsonObject.getString("lat") == null || jsonObject.getString("lat").equalsIgnoreCase("")) {
                                    driverDetailBean.setLatitude(Double.parseDouble("0.0"));
                                    driverDetailBean.setLongitude(Double.parseDouble("0.0"));

                                } else {
                                    driverDetailBean.setLatitude(Double.parseDouble(jsonObject.getString("lat")));
                                    driverDetailBean.setLongitude(Double.parseDouble(jsonObject.getString("lon")));

                                }
                                driverDetailBean.setFirst_name(jsonObject.getString("first_name"));
                                driverDetailBean.setCartypeid(jsonObject.getString("car_type_id"));
                                driverDetailBean.setEstimatetime(jsonObject.getInt("estimate_time"));
                                driverDetailBean.setBearing(jsonObject.getInt("bearing"));
                                driverDetailBeanArrayList.add(driverDetailBean);

                            }
                        }

                        if (driverDetailBeanArrayList != null &&!driverDetailBeanArrayList.isEmpty()) {
                            for (DriverDetailBean point : driverDetailBeanArrayList) {
                                LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
                                options.position(latLng);
                                options.title("" + point.getFirst_name() + " " + point.getEstimatetime() + " min away");
                                options.rotation(point.getBearing());
                                marker = gMap.addMarker(options);
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.carfromabove));
                                markerList.add(marker);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }


    }

    private class GetNearestDriverAll extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            driverDetailBeanArrayList = new ArrayList<>();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_available_driver?";
                String CheckUrl = BaseUrl.baseurl + "get_available_driver?latitude=" + latitude + "&longitude=" + longitude + "&user_id=" + MainActivity.user_id;
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();

                if (latitude == 0.0) {
                    params.put("latitude", pickup_lat_str);
                    params.put("longitude", pickup_lon_str);
                } else {
                    params.put("latitude", latitude);
                    params.put("longitude", longitude);
                }
                params.put("user_id", MainActivity.user_id);
                params.put("timezone", MainActivity.time_zone);
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
                if (gMap == null) {

                } else {
                    gMap.clear();
                }
            } else if (result.equalsIgnoreCase("null")) {
                if (gMap == null) {

                } else {
                    gMap.clear();
                }

            } else if (result.isEmpty()) {
                if (gMap == null) {

                } else {
                    gMap.clear();
                }
            } else {
                try {
                    JSONObject jsonobj = new JSONObject(result);
                    String msg = jsonobj.getString("message");
                    if (msg.equalsIgnoreCase("success")) {
                        if (gMap != null) {
                            gMap.clear();
                            if (markerList != null) {
                                for (int i = 0; i < markerList.size(); i++) {
                                    markerList.get(i).remove();
                                }
                            }
                            AddLine();
                        }
                        JSONArray jsonArray = jsonobj.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String results = jsonObject.getString("result");
                            if (results.equalsIgnoreCase("successful")) {
                                JSONObject esti = jsonArray.getJSONObject(0);
                                DriverDetailBean driverDetailBean = new DriverDetailBean();
                                driverDetailBean.setId(jsonObject.getString("id"));
                                if (jsonObject.getString("lat") == null || jsonObject.getString("lat").equalsIgnoreCase("")) {
                                    driverDetailBean.setLatitude(Double.parseDouble("0.0"));
                                    driverDetailBean.setLongitude(Double.parseDouble("0.0"));

                                } else {
                                    driverDetailBean.setLatitude(Double.parseDouble(jsonObject.getString("lat")));
                                    driverDetailBean.setLongitude(Double.parseDouble(jsonObject.getString("lon")));

                                }
                                driverDetailBean.setFirst_name(jsonObject.getString("first_name"));
                                driverDetailBean.setEstimatetime(jsonObject.getInt("estimate_time"));
                                driverDetailBean.setCartypeid(jsonObject.getString("car_type_id"));
                                driverDetailBeanArrayList.add(driverDetailBean);
                            }


                        }

                        if (driverDetailBeanArrayList == null || driverDetailBeanArrayList.isEmpty()) {

                        } else {
                            if (driverDetailBeanArrayList.size() > 0) {

                                for (DriverDetailBean point : driverDetailBeanArrayList) {
                                    LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
                                    options.position(latLng);
                                    options.title("" + point.getFirst_name() + " " + point.getEstimatetime() + " min away");
                                    marker = gMap.addMarker(options);
                                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.carfromabove));
                                    markerList.add(marker);

                                }
                            }

                        }


                    } else {
                        if (gMap == null) {

                        } else {
                            if (marker != null) {
                                for (int i = 0; i < markerList.size(); i++) {
                                    markerList.get(i).remove();
                                }

                            }

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private class GetCurrentBooking extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  progressbar.setVisibility(View.VISIBLE);
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
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_current_booking?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("type", "USER");
                params.put("timezone", time_zone);
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
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }
            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    Log.e("Current Booking Main", ">>" + result);
                    if (msg.equalsIgnoreCase("successfull")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            String status = jsonObject1.getString("status");
                            Log.e("Current Booking status", ">> " + status);
                            String pay_status = jsonObject1.getString("pay_status");
                            if (status.equalsIgnoreCase("Pending")) {
                                if (jsonObject1.getString("user_id").equalsIgnoreCase(user_id)) {
                                    pickuploc_str = jsonObject1.getString("picuplocation");
                                    dropoffloc_str = jsonObject1.getString("dropofflocation");
                                    selected_car_id = jsonObject1.getString("car_type_id");
                                    booktype = jsonObject1.getString("booktype");
                                    date_str = jsonObject1.getString("picklaterdate");
                                    time_str = jsonObject1.getString("picklatertime");
                                    pickup_lat_str = Double.parseDouble(jsonObject1.getString("picuplat"));
                                    pickup_lon_str = Double.parseDouble(jsonObject1.getString("pickuplon"));
                                    drop_lat_str = Double.parseDouble(jsonObject1.getString("droplat"));
                                    drop_lon_str = Double.parseDouble(jsonObject1.getString("droplon"));
                                    /*Intent k = new Intent(MainActivity.this, TripStatusAct.class);
                                    startActivity(k);*/
                                }
                            } else if (status.equalsIgnoreCase("End")) {
                                if (jsonObject1.getString("user_id").equalsIgnoreCase(user_id)) {
                                    if (jsonObject1.getString("user_rating_status").equalsIgnoreCase("Yes")){

                                    }
                                    else {
                                        Intent l = new Intent(MainActivity.this, FeedbackUs.class);
                                        startActivity(l);
                                    }


                                }
                            }
                            else if (status.equalsIgnoreCase("Finish")) {
                                if (jsonObject1.getString("user_id").equalsIgnoreCase(user_id)) {
                                    Intent l = new Intent(MainActivity.this, FeedbackUs.class);
                                    startActivity(l);
                                }
                            }
                            else {
                                Log.e("Current Booking status", ">TRUE> " + status);
                                Intent j = new Intent(MainActivity.this, DriverAcceptStatus.class);
                                startActivity(j);
                            }


                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void selectDateTime() {
        final Dialog dialogSts = new Dialog(MainActivity.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.selectdate_newlay);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView date_tv = (TextView) dialogSts.findViewById(R.id.date_tv);
        final TextView time_tv = (TextView) dialogSts.findViewById(R.id.time_tv);
        TextView cancel = (TextView) dialogSts.findViewById(R.id.cancel);
        TextView ok = (TextView) dialogSts.findViewById(R.id.ok);
        LinearLayout time_lay = (LinearLayout) dialogSts.findViewById(R.id.time_lay);
        LinearLayout date_lay = (LinearLayout) dialogSts.findViewById(R.id.date_lay);
        date_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                                String mon = MONTHS[monthOfYear];
                                int mot = monthOfYear + 1;
                                String month = "";
                                if (mot >= 10) {
                                    month = String.valueOf(mot);
                                } else {
                                    month = "0" + String.valueOf(mot);
                                }
                                String daysss = "";
                                if (dayOfMonth >= 10) {
                                    daysss = String.valueOf(dayOfMonth);
                                } else {
                                    daysss = "0" + String.valueOf(dayOfMonth);
                                }
                                date_tv.setText(dayOfMonth + "-" + mon + "-" + year);
                                date_str = "" + year + "-" + month + "-" + daysss;

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (date_str == null || date_str.equalsIgnoreCase("")) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.seldate), Toast.LENGTH_SHORT).show();
                } else if (time_str == null || time_str.equalsIgnoreCase("")) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.seltime), Toast.LENGTH_SHORT).show();
                } else {
                    dialogSts.dismiss();
                    booktype = "Letter";
                    new GetPickUp().execute();
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
            }
        });
        time_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                int hour = hourOfDay;
                                int fullhour = hourOfDay;
                                int minutes = minute;
                                String timeSet = "";
                                if (hour > 12) {
                                    hour -= 12;
                                    timeSet = "PM";
                                } else if (hour == 0) {
                                    hour += 12;
                                    timeSet = "AM";
                                } else if (hour == 12) {
                                    timeSet = "PM";
                                } else {
                                    timeSet = "AM";
                                }

                                String min = "";
                                if (minutes < 10)
                                    min = "0" + minutes;
                                else
                                    min = String.valueOf(minutes);
                                time_str = "" + hourOfDay + " : " + min + " " + timeSet;
                                time_tv.setText("" + time_str);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();


            }
        });
        dialogSts.show();
    }

    private void appUpdate() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MainActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.confirmlogoutother_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        final TextView body_tv = (TextView) canceldialog.findViewById(R.id.body_tv);
        body_tv.setText(""+getResources().getString(R.string.appupdateneed));
        no_tv.setText(""+getResources().getString(R.string.remindlater));
        yes_tv.setText(""+getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                final String appPackageName = BuildConfig.APPLICATION_ID; // package name of the app
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySession.setAppUpdate("later");
                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }
    private void profileUpdate() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(MainActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.confirmlogoutother_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        final TextView body_tv = (TextView) canceldialog.findViewById(R.id.body_tv);
        body_tv.setText(""+getResources().getString(R.string.updateprofile));
        no_tv.setText(""+getResources().getString(R.string.later));
        yes_tv.setText(""+getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                Intent i = new Intent(MainActivity.this, ProfileAct.class);
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

}