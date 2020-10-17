package main.com.iglobuser.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.NetworkOnMainThreadException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
import main.com.iglobuser.draweractivity.BaseActivity;
import main.com.iglobuser.paymentclasses.SaveCardDetail;
import main.com.iglobuser.utils.NotificationUtils;
import www.develpoeramit.mapicall.ApiCallBuilder;

public class TripStatusAct extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap gMap;
    GPSTracker gpsTracker;
    private double longitude = 0.0, latitude = 0.0;
    private TextView fare_rate_tv, away_minute, people_size;
    Dialog dialogSts, route_detail_pop;
    RippleBackground content1;
    CountDownTimer yourCountDownTimer;
    private RelativeLayout exit_app_but;
    RecyclerView cartypelist;
    CarHoriZontalLay carHoriZontalLay;
    private String selected_car_id = "";
    String request_id = "";
    String Fullpath = "";
    AutoCompleteTextView pickuplocation, dropofflocation;
    private Integer THRESHOLD = 2;
    private int count = 0, countDrop = 0;
    boolean sts;
    ImageView gpslocator, picmap_ic, clear_pick_ic, clear_drop_ic, map_ic;
    private ProgressDialog dialog;
    Timer timerObj;
    ScheduledExecutorService scheduleTaskExecutor;
    SupportMapFragment mapFragment;
    MySession mySession;
    LatLng picklatLng, droplatlong;
    Dialog waitingdialogSts;
    int pop_sts = 0;
    String time_zone = "";
    public static Activity fa;
    Marker marker;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<DriverDetailBean> driverDetailBeanArrayList = new ArrayList<>();
    public ArrayList<MyCarBean> myCarBeanArrayList =new ArrayList<>();
    public ArrayList<MyCarBean>  myCarBeanArrayList_check;
    private long timeCountInMilliSeconds;
    private TextView textViewTime, time_tv;
    TextView cancel_tv, confirm_tv;
    long diffHours = 0, diffMinutes = 0;
    ACProgressCustom ac_dialog;
    private String user_log_data = "", user_id = "", driver_id = "", min_away = "";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ImageView cashimg, creditimg, walletimg;
    private TextView cashtv, credittv, wallettv, applycupon, discount_tv, tv_ride_time;
    private LinearLayout cashlay, creditlay, walletlay;
    private String payment_type_str = "Cash";
    private double Estimate_Amount = 0;
    private String diff_second = "1", coupon_str = "";
    TimerTask timerTask2;
    private boolean isVisible = false;
    private String language = "";
    MyLanguageSession myLanguageSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_trip_status);
        idinits();
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
        if (user_log_data != null) {
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

        pop_sts = 0;
        fa = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        selected_car_id = MainActivity.selected_car_id;
        myCarBeanArrayList_check = new ArrayList<>();
        mySession = new MySession(this);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

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

                            if (yourCountDownTimer != null) {
                                yourCountDownTimer.cancel();
                            }

                            if (waitingdialogSts == null) {

                            } else {
                                waitingdialogSts.dismiss();
                            }
                            if (route_detail_pop == null) {
                            } else {
                                route_detail_pop.dismiss();
                            }

                            if (MainActivity.booktype.equalsIgnoreCase("Letter")) {
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
                            if (yourCountDownTimer != null) {
                                yourCountDownTimer.cancel();
                            }

                            requestCancel();
                        }
                        if (keyMessage.equalsIgnoreCase("no driver available")) {
                            request_id = data.getString("request_id");

                            if (content1 == null) {

                            } else {
                                content1.stopRippleAnimation();
                                if (timerObj == null) {

                                } else {
                                    timerObj.cancel();
                                }
                            }
                            if (yourCountDownTimer != null) {
                                yourCountDownTimer.cancel();
                            }

                            if (waitingdialogSts == null) {

                            } else {
                                waitingdialogSts.dismiss();
                            }
                            if (route_detail_pop == null) {
                            } else {
                                route_detail_pop.dismiss();
                            }
                            noDriverFound();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        autocompleteView();
        checkGps();
        try {
            initilizeMap();

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
                    clear_pick_ic.setVisibility(View.GONE);
                    loadData(pickuplocation.getText().toString());
                } else {
                    clear_pick_ic.setVisibility(View.INVISIBLE);
                }
            }
        });
        dropofflocation.setThreshold(THRESHOLD);
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
                    map_ic.setVisibility(View.GONE);
                    loadDataDrop(dropofflocation.getText().toString());
                } else {
                    clear_drop_ic.setVisibility(View.INVISIBLE);
                    map_ic.setVisibility(View.VISIBLE);
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
                    GeoAutoCompleteAdapter ga = new GeoAutoCompleteAdapter(TripStatusAct.this, l1, "" + latitude, "" + longitude);
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
                    GeoAutoCompleteAdapter ga = new GeoAutoCompleteAdapter(TripStatusAct.this, l1, "" + latitude, "" + longitude);
                    dropofflocation.setAdapter(ga);

                }

            }
            countDrop++;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void clickevetn() {
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected_car_id == null || selected_car_id.equalsIgnoreCase("")) {
                    Toast.makeText(TripStatusAct.this, getResources().getString(R.string.selcartype), Toast.LENGTH_LONG).show();
                } else {
                    if (away_minute.getText().toString().equalsIgnoreCase("No Drivers")) {
                        Toast.makeText(TripStatusAct.this, getResources().getString(R.string.nodrivers), Toast.LENGTH_LONG).show();
                    } else {
                        Calendar c = Calendar.getInstance();
                        TimeZone tz = c.getTimeZone();
                        time_zone = tz.getID();
                        if (payment_type_str == null || payment_type_str.equalsIgnoreCase("")) {
                            Toast.makeText(TripStatusAct.this, getResources().getString(R.string.selectpaymenttype), Toast.LENGTH_LONG).show();
                        } else {
                            new SendRequestToDriver().execute();
                        }

                    }

                }


            }
        });
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancelAgenda();
            }
        });
        map_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TripStatusAct.this, SetLocation.class);
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
                Intent i = new Intent(TripStatusAct.this, SetLocation.class);
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
                cashimg.setImageResource(R.drawable.ic_cash_tool);
                cashtv.setTextColor(getResources().getColor(R.color.buttoncol));

                creditimg.setImageResource(R.drawable.ic_credit_card);
                credittv.setTextColor(getResources().getColor(R.color.black));
                walletimg.setImageResource(R.drawable.ic_wallet_black);
                wallettv.setTextColor(getResources().getColor(R.color.black));
                payment_type_str = "Cash";
            }
        });
        creditlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toast.makeText(TripStatusAct.this,"In working..",Toast.LENGTH_LONG).show();

                if (BaseActivity.card_base_id == null || BaseActivity.card_base_id.equalsIgnoreCase("")) {
                    cardStatus();
                } else if (!BaseActivity.card_base_id.equalsIgnoreCase("")) {
                    cashimg.setImageResource(R.drawable.ic_cash);
                    cashtv.setTextColor(getResources().getColor(R.color.black));

                    creditimg.setImageResource(R.drawable.ic_credit_card_toolcol);
                    credittv.setTextColor(getResources().getColor(R.color.buttoncol));
                    walletimg.setImageResource(R.drawable.ic_wallet_black);
                    wallettv.setTextColor(getResources().getColor(R.color.black));
                    payment_type_str = "Card";
                } else {
                    cardStatus();
                }


            }
        });
        walletlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (MainActivity.amount != null && !MainActivity.amount.equalsIgnoreCase("")) {
                    double amount = Double.parseDouble(MainActivity.amount);
                    if (amount > Estimate_Amount) {
                        cashimg.setImageResource(R.drawable.ic_cash);
                        cashtv.setTextColor(getResources().getColor(R.color.black));

                        creditimg.setImageResource(R.drawable.ic_credit_card);
                        credittv.setTextColor(getResources().getColor(R.color.black));
                        walletimg.setImageResource(R.drawable.ic_wallet_toolcol);
                        wallettv.setTextColor(getResources().getColor(R.color.buttoncol));
                        payment_type_str = "Wallet";
                    } else {
                        Toast.makeText(TripStatusAct.this, getResources().getString(R.string.notenough), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(TripStatusAct.this, getResources().getString(R.string.notenough), Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    private void cardStatus() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.confirmlogoutother_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        final TextView body_tv = (TextView) canceldialog.findViewById(R.id.body_tv);
        body_tv.setText("" + getResources().getString(R.string.pleaseaddcarddetail));
        no_tv.setText("" + getResources().getString(R.string.cancel));
        yes_tv.setText("" + getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                Intent i = new Intent(TripStatusAct.this, SaveCardDetail.class);
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

    private void idinits() {
        discount_tv = findViewById(R.id.discount_tv);
        applycupon = findViewById(R.id.applycupon);
        cashlay = findViewById(R.id.cashlay);
        cashimg = findViewById(R.id.cashimg);
        cashtv = findViewById(R.id.cashtv);
        creditlay = findViewById(R.id.creditlay);
        creditimg = findViewById(R.id.creditimg);
        credittv = findViewById(R.id.credittv);
        walletlay = findViewById(R.id.walletlay);
        walletimg = findViewById(R.id.walletimg);
        wallettv = findViewById(R.id.wallettv);
        tv_ride_time = findViewById(R.id.tv_ride_time);

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
        cartypelist = (RecyclerView) findViewById(R.id.cartypelist);
        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);
        pickuplocation.setText("" + MainActivity.pickuploc_str);
        dropofflocation.setText("" + MainActivity.dropoffloc_str);
        TextView titletext = (TextView) findViewById(R.id.titletext);
        carHoriZontalLay = new CarHoriZontalLay();
        cartypelist.setAdapter(carHoriZontalLay);
        applycupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyCoupon();
            }
        });

    }

    private void initilizeMap() {
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(TripStatusAct.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TripStatusAct.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        gMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        TripStatusAct.this, R.raw.stylemap_3));
        gMap.setBuildingsEnabled(false);
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
        gMap.getUiSettings().setMapToolbarEnabled(false);
        gMap.getUiSettings().setZoomControlsEnabled(false);
        CameraUpdate center;
        if (latitude == 0.0) {
            center = CameraUpdateFactory.newLatLngZoom(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str), 14);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            gMap.animateCamera(zoom);

        } else {
            center = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            gMap.animateCamera(zoom);

        }
        gMap.moveCamera(center);

        String url = getUrl(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str), new LatLng(MainActivity.drop_lat_str, MainActivity.drop_lon_str));
        FetchUrl FetchUrl = new FetchUrl();
        FetchUrl.execute(url);
        final LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str))
                .include(new LatLng(MainActivity.drop_lat_str, MainActivity.drop_lon_str))
                .build();
        int width = getResources().getDisplayMetrics().widthPixels;
        final int padding = (int) (width * 0.10); // offset from edges of the map 12% of screen

        gMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));
            }
        });

    }

    private void checkGps() {
        gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    class CarHoriZontalLay extends RecyclerView.Adapter<CarHoriZontalLay.MyViewHolder> {
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

        public CarHoriZontalLay() {

        }

        @Override
        public CarHoriZontalLay.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_car_lay, parent, false);
            CarHoriZontalLay.MyViewHolder myViewHolder = new CarHoriZontalLay.MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final CarHoriZontalLay.MyViewHolder holder,  int listPosition) {
            if (listPosition == myCarBeanArrayList.size() - 1) {
                holder.viewLine.setVisibility(View.GONE);
            }
            holder.carname.setText("" + myCarBeanArrayList.get(listPosition).getCarname());
            if (myCarBeanArrayList.get(listPosition).isSelected()) {
                if (myCarBeanArrayList.get(listPosition).getTotal() != null && !myCarBeanArrayList.get(listPosition).getTotal().equalsIgnoreCase("")) {
                    Estimate_Amount = Double.parseDouble(myCarBeanArrayList.get(listPosition).getTotal());
                }
                fare_rate_tv.setText("$" + myCarBeanArrayList.get(listPosition).getTotal());
                if (myCarBeanArrayList.get(listPosition).getCab_find().equalsIgnoreCase("no_cab")) {
                    away_minute.setText(getResources().getString(R.string.nodrivers_s));

                } else {
                    away_minute.setText("" + myCarBeanArrayList.get(listPosition).getCab_find() + " min");

                }
                people_size.setText("" + myCarBeanArrayList.get(listPosition).getNo_of_seats() + " people");

                holder.backview.setBackgroundResource(R.drawable.selected_round_back);
            } else {
                holder.backview.setBackgroundResource(R.drawable.unselectedback);
            }
            String car_url = myCarBeanArrayList.get(listPosition).getCar_image();
            if (!car_url.equalsIgnoreCase("") && car_url.equalsIgnoreCase(BaseUrl.baseurl)) {
                Picasso.with(TripStatusAct.this).load(myCarBeanArrayList.get(listPosition).getCar_image()).into(holder.carimage);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("CarClicked","====>"+listPosition);
                    for (int k = 0; k < myCarBeanArrayList.size(); k++) {
                        if (listPosition == k) {
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
                    cartypelist.setAdapter(carHoriZontalLay);
                    carHoriZontalLay.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return myCarBeanArrayList == null ? 0 : myCarBeanArrayList.size();
        }
    }

    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=" + getResources().getString(R.string.googlekey_other);
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.e("Url", "----->" + url);
        return url;
    }

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
                tv_ride_time.setText("Estimate ride time: " + parser.getTime(jObject));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
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
            }
        }
    }


    public class SendRequestToDriver extends AsyncTask<String, String, String> {
        String Jsondata;
        private boolean checkdata = false;

        protected void onPreExecute() {
            try {
                super.onPreExecute();
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
            String charset = "UTF-8";
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = simpleDateFormat.format(new Date());
            String postReceiverUrl = "";
            if (MainActivity.booktype.equalsIgnoreCase("Letter")) {
                postReceiverUrl = BaseUrl.baseurl + "latter_booking_request?";
            } else {
                postReceiverUrl = BaseUrl.baseurl + "booking_request?";
            }
            Log.e("BOOKING_URL", "====>" + postReceiverUrl + "car_type_id=" + selected_car_id + "&user_id=" + user_id + "&picuplocation=" + MainActivity.pickuploc_str + "&dropofflocation=" + MainActivity.dropoffloc_str + "&picuplat=" + MainActivity.pickup_lat_str + "&pickuplon=" + MainActivity.pickup_lon_str + "&droplat=" + MainActivity.drop_lat_str + "&droplon=" + MainActivity.drop_lon_str + "&shareride_type=no&booktype=" + MainActivity.booktype + "&passenger=1&current_time=" + date + "&timezone=" + time_zone + "&status=" + MainActivity.booktype+"&apply_code="+coupon_str+"&vehical_type="+(mySession.isVIP() ? "VIP" : "Reqular")+"&picklatertime="+ MainActivity.time_str+"&picklaterdate="+MainActivity.date_str.trim());
            try {
                MultipartUtility multipart = new MultipartUtility(postReceiverUrl, charset);
                multipart.addFormField("car_type_id", selected_car_id);
                multipart.addFormField("device_type", "android");
                multipart.addFormField("user_id", user_id);
                multipart.addFormField("picuplocation", MainActivity.pickuploc_str);
                multipart.addFormField("dropofflocation", MainActivity.dropoffloc_str);
                multipart.addFormField("picuplat", "" + MainActivity.pickup_lat_str);
                multipart.addFormField("pickuplon", "" + MainActivity.pickup_lon_str);
                multipart.addFormField("droplat", "" + MainActivity.drop_lat_str);
                multipart.addFormField("droplon", "" + MainActivity.drop_lon_str);
                multipart.addFormField("shareride_type", "no");
                multipart.addFormField("booktype", MainActivity.booktype);
                multipart.addFormField("passenger", "1");
                multipart.addFormField("current_time", "" + format);
                multipart.addFormField("timezone", "" + time_zone);
                multipart.addFormField("payment_type", "" + payment_type_str);
                multipart.addFormField("status", "" + MainActivity.booktype);
                multipart.addFormField("apply_code", coupon_str);
                multipart.addFormField("vehical_type", mySession.isVIP() ? "VIP" : "Reqular");
                if (MainActivity.booktype == null || MainActivity.booktype.equalsIgnoreCase("") || MainActivity.booktype.equalsIgnoreCase("Now")) {
                    multipart.addFormField("picklatertime", "");
                    multipart.addFormField("picklaterdate", "");

                } else {
                    multipart.addFormField("picklatertime", MainActivity.time_str);
                    multipart.addFormField("picklaterdate", MainActivity.date_str.trim());
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
                return Jsondata;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("BookingRequest", "===========>" + result);
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
                        if (MainActivity.booktype.equalsIgnoreCase("Letter")) {
                            bookConfirm();
                        } else {
                            request_id = jsonObject.getString("request_id");
                            showWaitPopup();
                        }
                    } else {
                        noDriverFound();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void areusureCancelRide() {
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
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
                if (timerObj != null) {
                    timerObj.cancel();
                }
                if (yourCountDownTimer != null) {
                    yourCountDownTimer.cancel();
                }
                CancelRide();
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
        isVisible = true;
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }
        LocalBroadcastManager.getInstance(TripStatusAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(TripStatusAct.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(TripStatusAct.this.getApplicationContext());
        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (selected_car_id == null || selected_car_id.equalsIgnoreCase("")) {
                    new GetNearestDriverAll().execute();
                } else {
                    new GetNearestDriver().execute();
                }
                new GetCarLists().execute();
            }
        }, 0, 6, TimeUnit.SECONDS);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (scheduleTaskExecutor != null) {
            scheduleTaskExecutor.shutdown();
        }
    }

    @Override
    public void onPause() {
        isVisible = false;
        LocalBroadcastManager.getInstance(TripStatusAct.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        if (timerTask2 != null) {
            timerTask2.cancel();
        }
        super.onPause();
        if (scheduleTaskExecutor != null) {
            scheduleTaskExecutor.shutdown();
        }
        if (dialogSts != null && dialogSts.isShowing()) {
            dialogSts.cancel();
        }
    }


    private void bookConfirm() {
        //   Log.e("War Msg in dialog", war_msg);
        dialogSts = new Dialog(TripStatusAct.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.letterbook_confirmlay);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView yes = (TextView) dialogSts.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mylocset = true;
                MainActivity.ride_status = "complete";
                MainActivity.pickuploc_str = "";
                MainActivity.dropoffloc_str = "";
                MainActivity.pickup_lat_str = 0;
                MainActivity.pickup_lon_str = 0;
                MainActivity.drop_lat_str = 0;
                MainActivity.drop_lon_str = 0;
                finish();
            }
        });
        dialogSts.show();
    }

    private void booking_confirmation() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(TripStatusAct.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.booking_success_lay);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView done_tv = (TextView) dialogSts.findViewById(R.id.done_tv);
        done_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
                Intent i = new Intent(TripStatusAct.this, DriverAcceptStatus.class);
                i.putExtra("request_id", request_id);
                i.putExtra("driver_id", driver_id);
                startActivity(i);
                finish();
            }
        });
        dialogSts.show();
    }

    private void requestCancel() {
        final Dialog dialogSts = new Dialog(TripStatusAct.this);
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
        final Dialog dialogSts = new Dialog(TripStatusAct.this);
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
                        if (sts) {
                            pickuplocation.setText("" + l2.get(i));
                            pickuplocation.dismissDropDown();
                            MainActivity.dropoffloc_str = dropofflocation.getText().toString();
                            MainActivity.pickuploc_str = pickuplocation.getText().toString();
                            if (MainActivity.dropoffloc_str == null || MainActivity.dropoffloc_str.equalsIgnoreCase("") || MainActivity.pickuploc_str == null || MainActivity.pickuploc_str.equalsIgnoreCase("")) {
                            } else {
                                new GetPickRoute().execute();
                            }

                        } else {
                            dropofflocation.setText("" + l2.get(i));
                            dropofflocation.dismissDropDown();
                            MainActivity.dropoffloc_str = dropofflocation.getText().toString();
                            MainActivity.pickuploc_str = pickuplocation.getText().toString();
                            if (MainActivity.dropoffloc_str == null || MainActivity.dropoffloc_str.equalsIgnoreCase("") || MainActivity.pickuploc_str == null || MainActivity.pickuploc_str.equalsIgnoreCase("")) {
                            } else {
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
                        wo.setUrl("https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyBXvrm0wKFaamcHvScRaQ2_Oi9lZw8if6k&input=" + constraint.toString().trim().replaceAll(" ", "+") + "&location=" + lat + "," + lon + "+&radius=20000&types=establishment&sensor=true");
                        String result = null;
                        try {
                            result = new MyTask(wo, 3).execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        parseJson(result);
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
            String address1 = MainActivity.pickuploc_str.trim().replaceAll(" ", "+");
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
                    MainActivity.pickup_lat_str = location.getDouble("lat");
                    MainActivity.pickup_lon_str = location.getDouble("lng");
                    new GetDropOffLatRoute().execute();

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
            String address1 = MainActivity.dropoffloc_str.trim().replaceAll(" ", "+");
            String address = address1.trim().replaceAll(",", "+");
            String postReceiverUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + getResources().getString(R.string.googlekey_other);

            try {
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


                    MainActivity.drop_lat_str = location.getDouble("lat");
                    MainActivity.drop_lon_str = location.getDouble("lng");
                    if (gMap == null) {

                    } else {
                        gMap.clear();
                        //  MarkerOptions markers = new MarkerOptions().position(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pickpoint)).flat(true).anchor(0.5f, 0.5f);
                        double distance_difference = distFrom(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str, MainActivity.drop_lat_str, MainActivity.drop_lon_str);
                        double distance_km = 0;
                        if (distance_difference > 0) {
                            distance_km = distance_difference * 1.5;
                        } else {
                            distance_km = 1;
                        }
                        String distan_str = String.format("%.1f", new BigDecimal(distance_km));
                        String distan_str_km = String.format("%.1f", new BigDecimal(distance_difference));
                        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                        gMap.animateCamera(zoom);
                        gMap.moveCamera(center);

                        String url = getUrl(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str), new LatLng(MainActivity.drop_lat_str, MainActivity.drop_lon_str));
                        FetchUrl FetchUrl = new FetchUrl();
                        FetchUrl.execute(url);
                        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                .include(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str))
                                .include(new LatLng(MainActivity.drop_lat_str, MainActivity.drop_lon_str))
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


    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        return dist;
    }


    private String hmsTimeFormatter(long milliSeconds) {
        String hms = String.format("%02d",
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }


    private class GetCarLists extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myCarBeanArrayList.clear();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_car_type_list?";

                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();

                params.put("picuplat", MainActivity.pickup_lat_str);
                params.put("pickuplon", MainActivity.pickup_lon_str);
                params.put("droplat", MainActivity.drop_lat_str);
                params.put("droplon", MainActivity.drop_lon_str);
                params.put("user_id", user_id);

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                String urlParameters = postData.toString();
                Log.e("CARIST","===>"+urlParameters);
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
            Log.e("CAR TYPE TRIP", " > >" + result);
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            MyCarBean myCarBean = new MyCarBean();
                            myCarBean.setId(jsonObject1.getString("id"));
                            myCarBean.setCarname(jsonObject1.getString("car_name"));
                            myCarBean.setCar_image(jsonObject1.getString("car_image"));
                            //myCarBean.setDistance(jsonObject1.getString("distance"));
                            myCarBean.setDistance(jsonObject1.getString("miles"));
                            myCarBean.setTotal(jsonObject1.getString("total"));
                            myCarBean.setCab_find(jsonObject1.getString("cab_find"));
                            myCarBean.setNo_of_seats(jsonObject1.getString("no_of_seats"));
                            if (selected_car_id != null) {
                                myCarBean.setSelected(selected_car_id.equalsIgnoreCase(jsonObject1.getString("id")));
                            } else {
                                myCarBean.setSelected(false);
                            }
                            myCarBeanArrayList.add(myCarBean);
                        }
                        carHoriZontalLay = new CarHoriZontalLay();
                        cartypelist.setAdapter(carHoriZontalLay);
                        carHoriZontalLay.notifyDataSetChanged();
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
                    params.put("latitude", MainActivity.pickup_lat_str);
                    params.put("longitude", MainActivity.pickup_lon_str);
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
            Log.e("ALL DRIVER", ">>" + result);
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

                        for (DriverDetailBean point : driverDetailBeanArrayList) {
                            LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
                            options.position(latLng);
                            options.title("" + point.getFirst_name() + " " + point.getEstimatetime() + " min away");
                            marker = gMap.addMarker(options);
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.carfromabove));
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    private class GetNearestDriver extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String postReceiverUrl = BaseUrl.baseurl + "available_car_driver?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                if (latitude == 0.0) {
                    params.put("latitude", ""+MainActivity.pickup_lat_str);
                    params.put("longitude", ""+MainActivity.pickup_lon_str);
                } else {
                    params.put("latitude", ""+latitude);
                    params.put("longitude", ""+longitude);
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
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.e("GetNearestDriver","======>"+response);
            try {
                JSONObject jsonobj = new JSONObject(response);
                String msg = jsonobj.getString("message");
                if (msg.equalsIgnoreCase("success")) {
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
                            driverDetailBeanArrayList.add(driverDetailBean);
                        }
                    }
                    for (DriverDetailBean point : driverDetailBeanArrayList) {
                        LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
                        options.position(latLng);
                        options.title("" + point.getFirst_name() + " " + point.getEstimatetime() + " min away");
                        marker = gMap.addMarker(options);
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.carfromabove));

                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showWaitPopup() {
        waitingdialogSts = new Dialog(TripStatusAct.this);
        waitingdialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitingdialogSts.setCancelable(false);
        waitingdialogSts.setContentView(R.layout.new_custom_waitinglay_ripple);
        waitingdialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView sendReq = waitingdialogSts.findViewById(R.id.sendReq);
        final TextView cancel = waitingdialogSts.findViewById(R.id.cancel);
        final TextView pick_location = waitingdialogSts.findViewById(R.id.pick_location);
        final TextView droplocation = waitingdialogSts.findViewById(R.id.droplocation);
        pick_location.setText("" + MainActivity.pickuploc_str);
        droplocation.setText("" + MainActivity.dropoffloc_str);
        final ProgressBar progressBarCircle = (ProgressBar) waitingdialogSts.findViewById(R.id.progressBarCircle);
        textViewTime = (TextView) waitingdialogSts.findViewById(R.id.textViewTime);
        content1 = (RippleBackground) waitingdialogSts.findViewById(R.id.content1);
        content1.startRippleAnimation();
        int sec = 60;
        int mili = 1000;
        int newsec = 1;
        Log.e("diff_second ?", "POPUP" + diff_second);
        if (diff_second == null || diff_second.equalsIgnoreCase("")) {
        } else {
            int difernce = Integer.parseInt(diff_second);
            newsec = sec - difernce;
        }
        Log.e("newsec >>", "dd " + newsec);
        timeCountInMilliSeconds = 1 * newsec * mili;
        Log.e("Count Timer", "gg " + timeCountInMilliSeconds);
        progressBarCircle.setMax((int) 60);
        if (yourCountDownTimer != null) {
            yourCountDownTimer.cancel();
        }
        yourCountDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            public void onTick(long millisUntilFinished) {
                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
                Log.e("TICK 1", "" + millisUntilFinished / 1000);
                if (millisUntilFinished / 1000 == 42) {
                    Log.e("TICK", "" + millisUntilFinished / 1000);
                    new GetCurrentBooking().execute();
                }
                if (millisUntilFinished / 1000 == 22) {
                    Log.e("TICK", "" + millisUntilFinished / 1000);
                    new GetCurrentBooking().execute();
                }
                if (millisUntilFinished / 1000 == 32) {
                    Log.e("TICK", "" + millisUntilFinished / 1000);
                    new GetCurrentBooking().execute();
                }

            }

            public void onFinish() {
                if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
                    NotificationUtils.r.stop();
                }
                if (isVisible) {
                    if (waitingdialogSts != null && waitingdialogSts.isShowing()) {
                        waitingdialogSts.dismiss();
                    }
                    if (content1 != null) {
                        content1.stopRippleAnimation();

                    }
                    noDriverFound();
                }

            }
        }.start();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitingdialogSts.dismiss();
//                areusureCancelRide();
                if (timerObj != null) {
                    timerObj.cancel();
                }
                if (yourCountDownTimer != null) {
                    yourCountDownTimer.cancel();
                }
                CancelRide();
            }
        });

        waitingdialogSts.show();


    }
    private void CancelRide(){
        HashMap<String,String>parmas=new HashMap<>();
        parmas.put("request_id", request_id);
        ApiCallBuilder.build(this).setUrl(BaseUrl.get().cancelRide())
                .isShowProgressBar(true)
                .setParam(parmas).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                try {
                    JSONObject object=new JSONObject(response);
                    boolean status=object.getString("status").contains("1");
                    Toast.makeText(TripStatusAct.this, ""+object.getString("message"), Toast.LENGTH_SHORT).show();
                    if (status){
                        if (ac_dialog != null) {
                            ac_dialog.dismiss();
                        }
                        if (yourCountDownTimer != null) {
                            yourCountDownTimer.cancel();
                        }
                        if (waitingdialogSts != null) {
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

            @Override
            public void Failed(String error) {

            }
        });
    }


    private class GetCurrentBooking extends AsyncTask<String, String, String> {
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
            Log.e("TRIP STS RES", " >" + result);
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
                                request_id = jsonObject1.getString("id");
                                showWaitPopup();
                            } else if (status.equalsIgnoreCase("Accept") || status.equalsIgnoreCase("Start") || status.equalsIgnoreCase("Arrived")) {
                                if (isVisible) {
                                    waitingdialogSts.dismiss();
                                    if (jsonObject1.getString("user_id").equalsIgnoreCase(user_id)) {
                                        Intent l = new Intent(TripStatusAct.this, DriverAcceptStatus.class);
                                        startActivity(l);
                                    }

                                }
                            } else if (status.equalsIgnoreCase("End")) {
                                if (isVisible) {
                                    waitingdialogSts.dismiss();
                                    if (jsonObject1.getString("user_id").equalsIgnoreCase(user_id)) {
                                        Intent l = new Intent(TripStatusAct.this, FeedbackUs.class);
                                        startActivity(l);
                                    }

                                }

                            } else {
                                if (isVisible) {
                                    waitingdialogSts.dismiss();
                                    Intent j = new Intent(TripStatusAct.this, DriverAcceptStatus.class);
                                    startActivity(j);
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

    private void applyCoupon() {
        final Dialog dialogSts = new Dialog(TripStatusAct.this);
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
                    Toast.makeText(TripStatusAct.this, getResources().getString(R.string.entercode), Toast.LENGTH_LONG).show();
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
                Toast.makeText(TripStatusAct.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();
            } else if (result.isEmpty()) {
                Toast.makeText(TripStatusAct.this, getResources().getString(R.string.servererror), Toast.LENGTH_LONG).show();

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("message").equalsIgnoreCase("unsuccessfull")) {
                        coupon_str = "";
                        if (jsonObject.getString("result").equalsIgnoreCase("code already expired")) {
                            discount_tv.setVisibility(View.GONE);
                            invalidCode("1");
                        } else if (jsonObject.getString("result").equalsIgnoreCase("code already used")) {
                            discount_tv.setVisibility(View.GONE);
                            invalidCode("2");
                        } else if (jsonObject.getString("result").equalsIgnoreCase("code not exist")) {
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

    private void couponCodeSucc() {
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_notification_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView statusmessage = (TextView) canceldialog.findViewById(R.id.statusmessage);
        statusmessage.setText("" + getResources().getString(R.string.yourcodeappsucc));
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

    private void invalidCode(String s) {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(TripStatusAct.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_notification_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView statusmessage = (TextView) canceldialog.findViewById(R.id.statusmessage);

        if (s.equalsIgnoreCase("1")) {
            statusmessage.setText("" + getResources().getString(R.string.expire));
        } else if (s.equalsIgnoreCase("2")) {
            statusmessage.setText("" + getResources().getString(R.string.allreadyused));
        } else if (s.equalsIgnoreCase("3")) {
            statusmessage.setText("" + getResources().getString(R.string.codeisinvalid));
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
}
