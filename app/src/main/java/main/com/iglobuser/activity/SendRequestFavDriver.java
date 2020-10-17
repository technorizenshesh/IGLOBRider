package main.com.iglobuser.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import com.google.firebase.messaging.FirebaseMessaging;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import cc.cloudist.acplibrary.ACProgressConstant;

import main.com.iglobuser.MainActivity;
import main.com.iglobuser.R;
import main.com.iglobuser.app.Config;
import main.com.iglobuser.constant.ACProgressCustom;
import main.com.iglobuser.constant.BaseUrl;
import main.com.iglobuser.constant.DriverDetailBean;
import main.com.iglobuser.constant.GPSTracker;
import main.com.iglobuser.constant.MultipartUtility;
import main.com.iglobuser.constant.MyCarBean;
import main.com.iglobuser.constant.MyLanguageSession;
import main.com.iglobuser.constant.MySession;
import main.com.iglobuser.draglocation.DataParser;
import main.com.iglobuser.draglocation.MyTask;
import main.com.iglobuser.draglocation.WebOperations;
import main.com.iglobuser.utils.NotificationUtils;

public class SendRequestFavDriver extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap gMap;
    GPSTracker gpsTracker;
    private double longitude = 0.0, latitude = 0.0;
    private TextView titletext, fare_rate_tv, away_minute, people_size;
    private Button confirmbooking_but;
    Dialog dialogSts, route_detail_pop;
    RippleBackground content1;
    CountDownTimer yourCountDownTimer;
    private TextView pic_location, dropoff_loc;
    private RelativeLayout exit_app_but;
    RecyclerView cartypelist;
    int initial_flag = 0;
    public boolean mylocset = true;
    private ProgressBar progressbar;
    String base_fare_str = "", per_km = "", car_charge = "", distance = "", total = "", min_way = "", request_id = "";
    String Fullpath = "";
    File mapfile;
    AutoCompleteTextView pickuplocation, dropofflocation;
    private Integer THRESHOLD = 2;
    private Integer THRESHOLD_DROP = 2;
    private int count = 0, countDrop = 0;
    boolean sts;
    ImageView gpslocator, picmap_ic, clear_pick_ic, clear_drop_ic, map_ic;
    private ProgressDialog dialog;
    Timer timerObj;

    SupportMapFragment mapFragment;
    MySession mySession;
    LatLng picklatLng, droplatlong;

    Dialog waitingdialogSts;
    int pop_sts = 0;
    String time_zone = "";
    public static Activity fa;
    TextView distance_km, estimate_fare, arriving_time, comingsoon_tv;
    public double pickup_lat_str = 0, pickup_lon_str = 0, drop_lat_str = 0, drop_lon_str = 0;

    Marker marker, myloc_marker;
    List<Marker> markerList = new ArrayList<Marker>();
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<DriverDetailBean> driverDetailBeanArrayList;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    public ArrayList<MyCarBean> myCarBeanArrayList;
    private long timeCountInMilliSeconds;
    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private TextView textViewTime, time_tv;
    private CountDownTimer countDownTimer;
    TextView cancel_tv, confirm_tv;
    long diffHours = 0, diffMinutes = 0;
    private ArrayList<String> latitudelist;
    private ArrayList<String> longitudelist;
    ACProgressCustom ac_dialog;
    private String user_log_data="",user_id="",driver_id="";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ImageView cashimg,creditimg,walletimg,pinmarimg;
    private TextView cashtv,credittv,wallettv,applycupon,discount_tv;
    private LinearLayout cashlay,creditlay,walletlay;
    private String payment_type_str="Cash";
    private double Estimate_Amount =0;
    public String  coupon_str="",date_str = "", time_str = "", ride_status = "", booktype = "Now", pickuploc_str = "", dropoffloc_str = "";
    private String language = "";
    MyLanguageSession myLanguageSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_send_request_fav_driver);
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
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            driver_id = bundle.getString("driver_id");
        }
        latitudelist = new ArrayList<>();
        longitudelist = new ArrayList<>();
        pop_sts = 0;
        fa = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mySession = new MySession(this);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        idinits();
        checkGps();
        clickevetn();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    JSONObject data = null;
                    try {
                        data = new JSONObject(message);
                        String keyMessage = data.getString("key").trim();
                        Log.e("KEY ACCEPT REJ", "" + keyMessage);
                        if (keyMessage.equalsIgnoreCase("your booking request is ACCEPT")) {
                            request_id = data.getString("request_id");
                            driver_id = data.getString("driver_id");
                            if (content1 == null) {
                            } else {
                                content1.stopRippleAnimation();
                                if (timerObj == null) {
                                } else {
                                    timerObj.cancel();
                                }

                            }
                            if (waitingdialogSts == null) {

                            } else {
                                waitingdialogSts.dismiss();
                            }
                            if (route_detail_pop == null) {
                            } else {
                                route_detail_pop.dismiss();
                            }

                            if (booktype.equalsIgnoreCase("Letter")) {
                                bookConfirm();
                            } else {
                                booking_confirmation();


                            }

                        }
                        if (keyMessage.equalsIgnoreCase("your booking request is Cancel")) {
                            request_id = data.getString("request_id");

                            if (content1 == null) {

                            } else {
                                content1.stopRippleAnimation();
                                if (timerObj == null) {

                                } else {
                                    timerObj.cancel();
                                }
                            }
                            if (waitingdialogSts == null) {

                            } else {
                                waitingdialogSts.dismiss();
                            }
                            if (route_detail_pop == null) {
                            } else {
                                route_detail_pop.dismiss();
                            }
                            requestCancel();
                        }
                        if (keyMessage.equalsIgnoreCase("your booking request is assign to new driver")) {
                            request_id = data.getString("request_id");

                            if (content1 == null) {

                            } else {
                                content1.stopRippleAnimation();
                                if (timerObj == null) {

                                } else {
                                    timerObj.cancel();
                                }
                            }
                            if (waitingdialogSts == null) {

                            } else {
                                waitingdialogSts.dismiss();
                            }
                            if (route_detail_pop == null) {
                            } else {
                                route_detail_pop.dismiss();
                            }
                            // requestReassign();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        autocompleteView();

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }
        //
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
                    clear_pick_ic.setVisibility(View.GONE);
                    loadData(pickuplocation.getText().toString());
                } else {
                    clear_pick_ic.setVisibility(View.INVISIBLE);
                }
            }
        });
        dropofflocation.setThreshold(THRESHOLD_DROP);
        dropofflocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sts = false;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {


                if (s.length() > 0) {
                    clear_drop_ic.setVisibility(View.VISIBLE);

                    loadDataDrop(dropofflocation.getText().toString());
                } else {
                    clear_drop_ic.setVisibility(View.INVISIBLE);

                }
            }
        });
    }

    private void loadData(String s) {
        try {
            if (count == 0) {
                List<String> l1 = new ArrayList<>();
                if (s == null) {

                } else {
                    l1.add(s);
                    sts = true;
                    GeoAutoCompleteAdapter ga = new GeoAutoCompleteAdapter(SendRequestFavDriver.this, l1, "" + latitude, "" + longitude);
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
                    GeoAutoCompleteAdapter ga = new GeoAutoCompleteAdapter(SendRequestFavDriver.this, l1, "" + latitude, "" + longitude);
                    dropofflocation.setAdapter(ga);

                }

            }
            countDrop++;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void clickevetn() {
        applycupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyCoupon();
            }
        });

        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickuploc_str == null || pickuploc_str.equalsIgnoreCase("")) {
                    Toast.makeText(SendRequestFavDriver.this, getResources().getString(R.string.selectpick), Toast.LENGTH_LONG).show();
                }
                else if (dropoffloc_str == null || dropoffloc_str.equalsIgnoreCase("")) {
                    Toast.makeText(SendRequestFavDriver.this, getResources().getString(R.string.selectdrop), Toast.LENGTH_LONG).show();
                }
                else {
                    Calendar c = Calendar.getInstance();
                    TimeZone tz = c.getTimeZone();
                    time_zone = tz.getID();
                    if (payment_type_str.equalsIgnoreCase("Cash")){
                        if (MainActivity.identity.equalsIgnoreCase("Verify")){
                            new SendRequestToDriver().execute();
                        }
                        else {
                            Toast.makeText(SendRequestFavDriver.this,getResources().getString(R.string.identynoti),Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        new SendRequestToDriver().execute();
                    }



                }


            }
        });
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  cancelAgenda();
            }
        });
        map_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SendRequestFavDriver.this, SetLocation.class);
                i.putExtra("setLoc", "dropoff");
                startActivity(i);

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
        picmap_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SendRequestFavDriver.this, SetLocation.class);
                i.putExtra("setLoc", "pickup");
                startActivity(i);

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

                    }

                }
            }
        });


        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cashlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cashimg.setImageResource(R.drawable.cash);
                cashtv.setTextColor(getResources().getColor(R.color.buttoncol));

                creditimg.setImageResource(R.drawable.credit_card);
                credittv.setTextColor(getResources().getColor(R.color.darkgrey));
                walletimg.setImageResource(R.drawable.wallet_unsel);
                wallettv.setTextColor(getResources().getColor(R.color.darkgrey));
                payment_type_str="Cash";
            }
        });
        creditlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  Toast.makeText(SendRequestFavDriver.this,"In working..",Toast.LENGTH_LONG).show();

                cashimg.setImageResource(R.drawable.cash_unsel);
                cashtv.setTextColor(getResources().getColor(R.color.darkgrey));

                creditimg.setImageResource(R.drawable.credit);
                credittv.setTextColor(getResources().getColor(R.color.buttoncol));
                walletimg.setImageResource(R.drawable.wallet_unsel);
                wallettv.setTextColor(getResources().getColor(R.color.darkgrey));
                payment_type_str="Card";



            }
        });
        walletlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (MainActivity.amount!=null&&!MainActivity.amount.equalsIgnoreCase("")){
                    double amount = Double.parseDouble(MainActivity.amount);
                    if (amount>Estimate_Amount){
                        cashimg.setImageResource(R.drawable.cash_unsel);
                        cashtv.setTextColor(getResources().getColor(R.color.darkgrey));

                        creditimg.setImageResource(R.drawable.credit_card);
                        credittv.setTextColor(getResources().getColor(R.color.darkgrey));
                        walletimg.setImageResource(R.drawable.wallet_type_col);
                        wallettv.setTextColor(getResources().getColor(R.color.buttoncol));
                        payment_type_str="Wallet";
                    }
                    else {
                        Toast.makeText(SendRequestFavDriver.this,getResources().getString(R.string.notenough),Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(SendRequestFavDriver.this,getResources().getString(R.string.notenough),Toast.LENGTH_LONG).show();
                }


            }
        });
    }


    private void idinits() {


        discount_tv = findViewById(R.id.discount_tv);
        applycupon = findViewById(R.id.applycupon);
        cashlay = findViewById(R.id.cashlay);
        pinmarimg = findViewById(R.id.pinmarimg);
        cashimg = findViewById(R.id.cashimg);
        cashtv = findViewById(R.id.cashtv);
        creditlay = findViewById(R.id.creditlay);
        creditimg = findViewById(R.id.creditimg);
        credittv = findViewById(R.id.credittv);
        walletlay = findViewById(R.id.walletlay);
        walletimg = findViewById(R.id.walletimg);
        wallettv = findViewById(R.id.wallettv);

        confirm_tv = (TextView) findViewById(R.id.confirm_tv);
        cancel_tv = (TextView) findViewById(R.id.cancel_tv);

        gpslocator = (ImageView) findViewById(R.id.gpslocator);
        picmap_ic = (ImageView) findViewById(R.id.picmap_ic);
        map_ic = (ImageView) findViewById(R.id.map_ic);
        clear_drop_ic = (ImageView) findViewById(R.id.clear_drop_ic);
        clear_pick_ic = (ImageView) findViewById(R.id.clear_pick_ic);
        pickuplocation = (AutoCompleteTextView) findViewById(R.id.pickuplocation);
        dropofflocation = (AutoCompleteTextView) findViewById(R.id.dropofflocation);
        people_size = (TextView) findViewById(R.id.people_size);
        away_minute = (TextView) findViewById(R.id.away_minute);
        fare_rate_tv = (TextView) findViewById(R.id.fare_rate_tv);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        cartypelist = (RecyclerView) findViewById(R.id.cartypelist);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(SendRequestFavDriver.this, LinearLayoutManager.HORIZONTAL, false);
        cartypelist.setLayoutManager(horizontalLayoutManagaer);

        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);



        titletext = (TextView) findViewById(R.id.titletext);

    }

    private void initilizeMap() {
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(SendRequestFavDriver.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SendRequestFavDriver.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        /*gMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        SendRequestFavDriver.this, R.raw.stylemap_3));*/
        gMap.setBuildingsEnabled(false);
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
        gMap.getUiSettings().setMapToolbarEnabled(false);
        gMap.getUiSettings().setZoomControlsEnabled(false);

        LatLng latLng;
        MarkerOptions marker;
        latLng = new LatLng(latitude, longitude);
        marker = new MarkerOptions().position(new LatLng(latitude, longitude)).flat(true).anchor(0.5f, 0.5f);

        gMap.addMarker(marker);
        CameraUpdate center;
        center = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 18);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(18);
        gMap.animateCamera(zoom);
        gMap.moveCamera(center);
        gMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                if (initial_flag != 0) {

                    if (mylocset) {
                        LatLng latLng = gMap.getCameraPosition().target;
                        String address_complete = loadAddress(latLng.latitude, latLng.longitude);
                        pickuplocation.setText(address_complete);

                    }
                }
                initial_flag++;

            }
        });

    }

    private void checkGps() {
        gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {

            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
           String  address_complete = loadAddress(latitude, longitude);
            pickuplocation.setText(address_complete);

        } else {
            // if gps off get lat long from network
            //   locationfromnetwork();
            gpsTracker.showSettingsAlert();
        }


    }

    private String loadAddress(double latitude, double longitude) {
        try {
            WebOperations wo = new WebOperations(SendRequestFavDriver.this.getApplicationContext());
            wo.setUrl("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=" + getResources().getString(R.string.googlekey_other));
            String str = new MyTask(wo, 3).execute().get();
            JSONObject jk = new JSONObject(str);
            JSONArray results = jk.getJSONArray("results");
            JSONObject jk1 = results.getJSONObject(0);
            String add1 = jk1.getString("formatted_address");
            return add1;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
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

    // Fetches data from url passed
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
            PolylineOptions lineOptions = null;
            ArrayList<LatLng> animation_list = null;
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                animation_list = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                latitudelist = new ArrayList<>();
                longitudelist = new ArrayList<>();

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    latitudelist.add(point.get("lat"));
                    longitudelist.add(point.get("lng"));
                    points.add(position);
                    animation_list.add(position);
                    if (j == 0) {
                        picklatLng = position;
                    }
                    if (j == (path.size() - 1)) {
                        droplatlong = position;
                    }

                }
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

            }
            if (lineOptions != null) {
                gMap.addPolyline(lineOptions);
                MarkerOptions pick = new MarkerOptions().position(picklatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pick_marker)).flat(true).anchor(0.5f, 0.5f);
                MarkerOptions drop = new MarkerOptions().position(droplatlong).icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker)).flat(true).anchor(0.5f, 0.5f);
                gMap.addMarker(pick);
                gMap.addMarker(drop);
                if (animation_list != null && !animation_list.isEmpty()) {
                    // startAnim(animation_list);
                }

            } else {

            }
        }
    }

    private void startAnim(ArrayList<LatLng> points) {
        Log.e("COME ON START ANIMATION", " True");
        if (gMap != null) {
            //      MapAnimator.getInstance().animateRoute(gMap, points);
        } else {
            Toast.makeText(getApplicationContext(), "Map not ready", Toast.LENGTH_LONG).show();
        }
    }


    public class SendRequestToDriver extends AsyncTask<String, String, String> {
        String Jsondata;
        private boolean checkdata = false;

        protected void onPreExecute() {
            try {
                super.onPreExecute();
                //  progressbar.setVisibility(View.VISIBLE);
                if (ac_dialog != null) {
                    ac_dialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                checkdata = true;
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/user_update?user_id=21&first_name=er&last_name=vijay&mobile=8889994272&email=v@gmail.com&lang=en&currency=inr&place=indore&country=india&register_id=123&ios_register_id=321&lat=22.123456&lon=75.123456
            String charset = "UTF-8";
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = simpleDateFormat.format(new Date());
            String postReceiverUrl = "";
            postReceiverUrl = BaseUrl.baseurl + "favorite_driver_request?";



            try {
                MultipartUtility multipart = new MultipartUtility(postReceiverUrl, charset);
            //    multipart.addFormField("car_type_id", selected_car_id);
                multipart.addFormField("device_type", "android");
                multipart.addFormField("user_id", user_id);
                multipart.addFormField("driver_id", driver_id);
                multipart.addFormField("picuplocation", pickuploc_str);
                multipart.addFormField("dropofflocation", dropoffloc_str);
                multipart.addFormField("picuplat", "" + pickup_lat_str);
                multipart.addFormField("pickuplon", "" + pickup_lon_str);
                multipart.addFormField("droplat", "" + drop_lat_str);
                multipart.addFormField("droplon", "" + drop_lon_str);
                multipart.addFormField("shareride_type", "no");
                multipart.addFormField("booktype", "Now");
                multipart.addFormField("passenger", "1");
                multipart.addFormField("current_time", "" + format);
                multipart.addFormField("timezone", "" + time_zone);
                multipart.addFormField("payment_type", "" + payment_type_str);
                multipart.addFormField("status", "Now");
                multipart.addFormField("apply_code", coupon_str);
                if (booktype == null || booktype.equalsIgnoreCase("") || booktype.equalsIgnoreCase("Now")) {

                } else {
                    multipart.addFormField("picklatertime", time_str);
                    multipart.addFormField("picklaterdate", date_str.trim());
                }
                if (Fullpath.equalsIgnoreCase("")) {
                } else {
                    File ImageFile = new File(Fullpath);
                    multipart.addFilePart("route_img", ImageFile);
                }
                List<String> response = multipart.finish();
                for (String line : response) {
                    Jsondata = line;
                }
                JSONObject object = new JSONObject(Jsondata);
                return Jsondata;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
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
                    if (msg.equalsIgnoreCase("success")) {


                        if (booktype.equalsIgnoreCase("Letter")) {
                            bookConfirm();
                        } else {
                            request_id = jsonObject.getString("request_id");
                            showWaitPopup();
                            //  startTimer();
                        }
                        //booking_confirmation();
                    } else {
                        noDriverFound();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    private void startTimer() {


        timerObj = new Timer();
        TimerTask timerTaskObj = new TimerTask() {
            public void run() {
                SendRequestFavDriver.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (!(SendRequestFavDriver.this).isFinishing()) {
                            if (pop_sts == 0) {
                                notfoundpopup();
                                pop_sts = 1;
                            }

                        }

                        //Do your UI operations like dialog opening or Toast here
                    }
                });
            }
        };
        timerObj.schedule(timerTaskObj, 60000, 62000);
    }

    private void notfoundpopup() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(SendRequestFavDriver.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.bookig_cancel_me_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        TextView message_tv = (TextView) canceldialog.findViewById(R.id.message_tv);
        TextView heading_tv = (TextView) canceldialog.findViewById(R.id.heading_tv);
        heading_tv.setText("" + getResources().getString(R.string.thisreqnotacc));
        message_tv.setText("" + getResources().getString(R.string.plstryagain));
        yes_tv.setText("" + getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
                Calendar c = Calendar.getInstance();
                TimeZone tz = c.getTimeZone();
                Log.e("TIME ZONE >>", tz.getDisplayName());
                Log.e("TIME ZONE ID>>", tz.getID());
                time_zone = tz.getID();

                new SendRequestToDriver().execute();

            }
        });
        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
                finish();

            }
        });
        canceldialog.show();


    }

    private void areusureCancelRide() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(SendRequestFavDriver.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.surecancelride_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                // stopCountDownTimer();



                new CancelRide().execute();
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

    @Override
    public void onResume() {
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
            if (dropoffloc_str == null || dropoffloc_str.equalsIgnoreCase("") || pickuploc_str == null || pickuploc_str.equalsIgnoreCase("")) {
            } else {

                pinmarimg.setVisibility(View.GONE);
                new GetPickRoute().execute();
            }
        }

        LocalBroadcastManager.getInstance(SendRequestFavDriver.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(SendRequestFavDriver.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(SendRequestFavDriver.this.getApplicationContext());


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onPause() {

        LocalBroadcastManager.getInstance(SendRequestFavDriver.this).unregisterReceiver(mRegistrationBroadcastReceiver);

        super.onPause();
        // timerObj.cancel();

        if (dialogSts != null && dialogSts.isShowing()) {
            dialogSts.cancel();
        }
        if (yourCountDownTimer!=null){
            yourCountDownTimer.cancel();
        }
    }


    private void bookConfirm() {
        //   Log.e("War Msg in dialog", war_msg);
        dialogSts = new Dialog(SendRequestFavDriver.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.letterbook_confirmlay);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView yes = (TextView) dialogSts.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ride_status = "complete";
                pickuploc_str = "";
                dropoffloc_str = "";
                pickup_lat_str = 0;
                pickup_lon_str = 0;
                drop_lat_str = 0;
                drop_lon_str = 0;


                finish();
            }
        });
        dialogSts.show();


    }
    private void booking_confirmation() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(SendRequestFavDriver.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.booking_success_lay);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView done_tv = (TextView) dialogSts.findViewById(R.id.done_tv);
        done_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();

                Intent i = new Intent(SendRequestFavDriver.this, DriverAcceptStatus.class);
                i.putExtra("request_id", request_id);
                i.putExtra("driver_id", driver_id);
                startActivity(i);
                finish();
            }
        });
        dialogSts.show();


    }
    private void requestCancel() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(SendRequestFavDriver.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.custom_heading_lay);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView done_tv = (TextView) dialogSts.findViewById(R.id.done_tv);
        done_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();


            }
        });
        dialogSts.show();


    }
    private void noDriverFound() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(SendRequestFavDriver.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.custom_heading_lay);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView done_tv = (TextView) dialogSts.findViewById(R.id.done_tv);
        TextView txtmsg = (TextView) dialogSts.findViewById(R.id.txtmsg);
        TextView bodymsg = (TextView) dialogSts.findViewById(R.id.bodymsg);
        txtmsg.setText(getResources().getString(R.string.drivernotfound));
        bodymsg.setText(getResources().getString(R.string.nodriverfound));
        done_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();


            }
        });
        dialogSts.show();


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
                        if (pickuploc_str == null || pickuploc_str.equalsIgnoreCase("")) {

                        } else if (dropoffloc_str == null || dropoffloc_str.equalsIgnoreCase("")) {

                        } else {

                        }
                        if (sts) {

                            pickuplocation.setText("" + l2.get(i));
                            pickuplocation.dismissDropDown();
                            dropoffloc_str = dropofflocation.getText().toString();
                            pickuploc_str = pickuplocation.getText().toString();
                            if (dropoffloc_str == null || dropoffloc_str.equalsIgnoreCase("") || pickuploc_str == null || pickuploc_str.equalsIgnoreCase("")) {
                            } else {
                                pinmarimg.setVisibility(View.GONE);
                                new GetPickRoute().execute();
                            }

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
                        wo.setUrl("https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyBXvrm0wKFaamcHvScRaQ2_Oi9lZw8if6k&input=" + constraint.toString().trim().replaceAll(" ", "+") + "&location=" + lat + "," + lon + "+&radius=20000&types=geocode&sensor=true");
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

        private List<Address> findLocations(Context context, String query_text) {

            List<Address> geo_search_results = new ArrayList<Address>();

            Geocoder geocoder = new Geocoder(context, context.getResources().getConfiguration().locale);
            List<Address> addresses = null;

            try {
                // Getting a maximum of 15 Address that matches the input text
                addresses = geocoder.getFromLocationName(query_text, 15);


            } catch (IOException e) {
                e.printStackTrace();
            }

            return addresses;
        }
    }

    private class GetPickRoute extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressbar.setVisibility(View.VISIBLE);
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
                    new SendRequestFavDriver.GetDropOffLatRoute().execute();

                } catch (JSONException e) {
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


                    //    pickup_lat_str,pickup_lon_str,drop_lat_str,drop_lon_str,
                    drop_lat_str = location.getDouble("lat");
                    drop_lon_str = location.getDouble("lng");
                    if (gMap == null) {

                    } else {
                        gMap.clear();
                        //  MarkerOptions markers = new MarkerOptions().position(new LatLng(pickup_lat_str, pickup_lon_str)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pickpoint)).flat(true).anchor(0.5f, 0.5f);
                         CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(pickup_lat_str, pickup_lon_str));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                        gMap.animateCamera(zoom);

                        gMap.moveCamera(center);

                        String url = getUrl(new LatLng(pickup_lat_str, pickup_lon_str), new LatLng(drop_lat_str, drop_lon_str));
                        FetchUrl FetchUrl = new FetchUrl();
                        FetchUrl.execute(url);
                        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                .include(new LatLng(pickup_lat_str, pickup_lon_str))
                                .include(new LatLng(drop_lat_str, drop_lon_str))
                                .build();
                        int width = getResources().getDisplayMetrics().widthPixels;
                        int padding = (int) (width * 0.10); // offset from edges of the map 12% of screen
                        gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));

                    }
                    //  new GetFareRate().execute(selected_car_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }




// progress bar code


    private void setTimerValues() {
        int time = 1;
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = time * 59 * 1000;
    }


    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d",
               /* TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
               */ TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }




    private void selectDateTime() {
        final Dialog dialogSts = new Dialog(SendRequestFavDriver.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.selectdate_newlay);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView date_tv = (TextView) dialogSts.findViewById(R.id.date_tv);
        time_tv = (TextView) dialogSts.findViewById(R.id.time_tv);
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(SendRequestFavDriver.this,
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
                                date_str = "" + year + "-" + month + "-" + daysss;
                                Calendar c = Calendar.getInstance();
                                Date date = c.getTime();
                                if (time_str == null || time_str.equalsIgnoreCase("")) {
                                    date_tv.setText(dayOfMonth + "-" + mon + "-" + year);
                                    addTime();
                                } else {
                                    String sss = date_str.trim();
                                    String dtStart = sss + " " + time_str;
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    try {
                                        Date selecteddate = format.parse(dtStart);
                                        long diff = selecteddate.getTime() - date.getTime();
                                        long diffSeconds = diff / 1000 % 60;
                                        diffMinutes = diff / (60 * 1000) % 60;
                                        diffHours = diff / (60 * 60 * 1000);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (diffHours >= 1) {
                                        date_tv.setText(dayOfMonth + "-" + mon + "-" + year);
                                        addTime();
                                    } else if (diffMinutes >= 30) {
                                        date_tv.setText(dayOfMonth + "-" + mon + "-" + year);
                                        addTime();
                                    } else {
                                        date_str = "";
                                        date_tv.setText("");
                                        Toast.makeText(SendRequestFavDriver.this, getResources().getString(R.string.cantbookreq), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (date_str == null || date_str.equalsIgnoreCase("")) {
                    Toast.makeText(SendRequestFavDriver.this, "Please Select Date", Toast.LENGTH_SHORT).show();
                } else if (time_str == null || time_str.equalsIgnoreCase("")) {
                    Toast.makeText(SendRequestFavDriver.this, "Please Select Time", Toast.LENGTH_SHORT).show();
                } else {
                    dialogSts.dismiss();
                    booktype = "Letter";

                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_str = "";
                time_str = "";
                dialogSts.dismiss();
            }
        });
        time_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(SendRequestFavDriver.this,
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
                                time_str = "" + hourOfDay + ":" + min + ":00";
                                Calendar c = Calendar.getInstance();
                                Date date = c.getTime();

                                if (date_str == null || date_str.equalsIgnoreCase("")) {
                                    time_tv.setText("" + hour + ":" + min + " " + timeSet);

                                } else {
                                    String sss = date_str.trim();
                                    String dtStart = sss + " " + time_str;
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    try {
                                        Date selecteddate = format.parse(dtStart);
                                        long diff = selecteddate.getTime() - date.getTime();
                                        long diffSeconds = diff / 1000 % 60;
                                        diffMinutes = diff / (60 * 1000) % 60;
                                        diffHours = diff / (60 * 60 * 1000);
                                        Log.e("diffHours ", " >> " + diffHours + " " + diffMinutes);

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (diffHours >= 1) {
                                        time_tv.setText("" + hour + ":" + min + " " + timeSet);
                                    } else if (diffMinutes >= 30) {
                                        time_tv.setText("" + hour + ":" + min + " " + timeSet);

                                    } else {
                                        time_str = "";
                                        time_tv.setText("");
                                        Toast.makeText(SendRequestFavDriver.this, getResources().getString(R.string.cantbookreq), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        dialogSts.show();


    }

    public void addTime() {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(SendRequestFavDriver.this,
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
                        time_str = "" + hourOfDay + ":" + min + ":00";
                        Calendar c = Calendar.getInstance();
                        Date date = c.getTime();

                        if (date_str == null || date_str.equalsIgnoreCase("")) {
                            if (time_tv != null) {
                                time_tv.setText("" + hour + ":" + min + " " + timeSet);
                            }


                        } else {
                            String sss = date_str.trim();
                            String dtStart = sss + " " + time_str;
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                Date selecteddate = format.parse(dtStart);
                                long diff = selecteddate.getTime() - date.getTime();
                                long diffSeconds = diff / 1000 % 60;
                                long diffMinutes = diff / (60 * 1000) % 60;
                                diffHours = diff / (60 * 60 * 1000);
                                Log.e("diffHours ", " >> " + diffHours + " " + diffMinutes);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (diffHours >= 1) {
                                if (time_tv != null) {
                                    time_tv.setText("" + hour + ":" + min + " " + timeSet);
                                }

                            } else if (diffMinutes >= 30) {
                                if (time_tv != null) {
                                    time_tv.setText("" + hour + ":" + min + " " + timeSet);
                                }
                            } else {
                                time_str = "";
                                if (time_tv != null) {
                                    time_tv.setText("");
                                }

                                Toast.makeText(SendRequestFavDriver.this, getResources().getString(R.string.cantbookreq), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void showWaitPopup() {

        waitingdialogSts = new Dialog(SendRequestFavDriver.this);
        waitingdialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitingdialogSts.setCancelable(false);
        waitingdialogSts.setContentView(R.layout.new_custom_waitinglay);
        waitingdialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView sendReq =  waitingdialogSts.findViewById(R.id.sendReq);
        final TextView cancel =  waitingdialogSts.findViewById(R.id.cancel);
        final TextView pick_location = waitingdialogSts.findViewById(R.id.pick_location);
        final TextView droplocation =  waitingdialogSts.findViewById(R.id.droplocation);
        pick_location.setText("" + pickuploc_str);
        droplocation.setText("" + dropoffloc_str);
        final ProgressBar progressBarCircle = (ProgressBar) waitingdialogSts.findViewById(R.id.progressBarCircle);
        textViewTime = (TextView) waitingdialogSts.findViewById(R.id.textViewTime);
        timeCountInMilliSeconds = 1 * 61000;
        timerStatus = TimerStatus.STOPPED;
        //  startStop();



        progressBarCircle.setMax((int) 60);
        progressBarCircle.setProgress(1);
        if (yourCountDownTimer != null) {
            yourCountDownTimer.cancel();
        }
        yourCountDownTimer = new CountDownTimer(61000, 1000) {
            public void onTick(long millisUntilFinished) {
                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
                Log.e("TICK 1",""+millisUntilFinished / 1000);
                if (millisUntilFinished / 1000==48){
                    Log.e("TICK",""+millisUntilFinished / 1000);
                    new GetCurrentBooking().execute();
                }
                if (millisUntilFinished / 1000==35){
                    new GetCurrentBooking().execute();
                }

            }

            public void onFinish() {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
                if (waitingdialogSts != null && waitingdialogSts.isShowing()) {
                    waitingdialogSts.dismiss();

                }
                notfoundpopup();
            }
        }.start();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerObj != null) {
                    timerObj.cancel();
                }
                areusureCancelRide();

            }
        });


        waitingdialogSts.show();


    }
    private class CancelRide extends AsyncTask<String, String, String> {
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
//http://mobileappdevelop.co/NAXCAN/webservice/cancel_ride?request_id=230
            try {
                String postReceiverUrl = BaseUrl.baseurl + "cancel_ride?";
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
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
                Toast.makeText(SendRequestFavDriver.this, "Server Error,Please Try again..", Toast.LENGTH_LONG).show();
            } else if (result.isEmpty()) {
                Toast.makeText(SendRequestFavDriver.this, "Server Error,Please Try again..", Toast.LENGTH_LONG).show();

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getString("message").equalsIgnoreCase("unsuccessfull")){
                    }
                    else {
                        if (yourCountDownTimer != null) {
                            yourCountDownTimer.cancel();
                        }
                        if (waitingdialogSts == null) {

                        } else {
                            waitingdialogSts.dismiss();
                        }
                        if (content1 != null) {
                            content1.stopRippleAnimation();
                        }
                        finish();
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

            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
//http://mobileappdevelop.co/NAXCAN/webservice/get_current_booking?user_id=1
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
            Log.e("TRIP STS RES"," >"+result);
            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {

                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    if (msg.equalsIgnoreCase("successfull")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            String status = jsonObject1.getString("status");
                            String pay_status = jsonObject1.getString("pay_status");
                            if (status.equalsIgnoreCase("Pending")) {

                            } else if (status.equalsIgnoreCase("End")) {
                                if (jsonObject1.getString("user_id").equalsIgnoreCase(user_id)) {
                                    Intent l = new Intent(SendRequestFavDriver.this, FeedbackUs.class);
                                    startActivity(l);
                                }
                            } else {
                                Intent j = new Intent(SendRequestFavDriver.this, DriverAcceptStatus.class);
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
    private void applyCoupon() {


        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(SendRequestFavDriver.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.apply_coupon);
        TextView canceltv = (TextView) dialogSts.findViewById(R.id.canceltv);
        TextView applytv = (TextView) dialogSts.findViewById(R.id.applytv);
        final EditText apply_et = (EditText) dialogSts.findViewById(R.id.apply_et);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        canceltv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
            }
        });
        applytv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String coupon_code = apply_et.getText().toString();
                if (coupon_code == null || coupon_code.equalsIgnoreCase("")) {
                    Toast.makeText(SendRequestFavDriver.this,getResources().getString(R.string.entercode),Toast.LENGTH_LONG).show();
                } else {
                    dialogSts.dismiss();
                    new ApplyCouponAsc().execute(coupon_code);
                }

            }
        });

        dialogSts.show();


    }
    private class ApplyCouponAsc extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ac_dialog != null) {
                ac_dialog.show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
//http://halatx.halasmart.com/hala/webservice/apply_code?code=JUN20
            try {
                String postReceiverUrl = BaseUrl.baseurl + "apply_code?";

                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();

                params.put("code", strings[0]);
                params.put("user_id", user_id);
                coupon_str = strings[0];

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
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
                Toast.makeText(SendRequestFavDriver.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();
            } else if (result.isEmpty()) {
                Toast.makeText(SendRequestFavDriver.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("message").equalsIgnoreCase("unsuccessfull")) {
                        coupon_str = "";
                        if (jsonObject.getString("result").equalsIgnoreCase("code already expired")){
                            discount_tv.setVisibility(View.GONE);
                            invalidCode("1");
                        }
                        else if (jsonObject.getString("result").equalsIgnoreCase("code already used")){
                            discount_tv.setVisibility(View.GONE);
                            invalidCode("2");
                        }
                        else if (jsonObject.getString("result").equalsIgnoreCase("code not exist")){
                            discount_tv.setVisibility(View.GONE);
                            invalidCode("3");
                        }


                    } else {
                        couponCodeSucc();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }
    private void invalidCode(String s) {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(SendRequestFavDriver.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_notification_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView statusmessage = (TextView) canceldialog.findViewById(R.id.statusmessage);

        if(s.equalsIgnoreCase("1")){
            statusmessage.setText(""+getResources().getString(R.string.expire));
        }
        else if(s.equalsIgnoreCase("2")){
            statusmessage.setText(""+getResources().getString(R.string.allreadyused));
        }
        else if(s.equalsIgnoreCase("3")){
            statusmessage.setText(""+getResources().getString(R.string.codeisinvalid));
        }


        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }
    private void couponCodeSucc() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(SendRequestFavDriver.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_notification_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView statusmessage = (TextView) canceldialog.findViewById(R.id.statusmessage);
        statusmessage.setText(""+getResources().getString(R.string.yourcodeappsucc));
        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discount_tv.setVisibility(View.VISIBLE);
                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }

}