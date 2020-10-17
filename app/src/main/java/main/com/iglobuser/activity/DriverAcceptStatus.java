package main.com.iglobuser.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceActivity;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.maps.android.PolyUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cc.cloudist.acplibrary.ACProgressConstant;
import de.hdodenhof.circleimageview.CircleImageView;
import main.com.iglobuser.Fragments.FragmentShareTrip;
import main.com.iglobuser.MainActivity;
import main.com.iglobuser.R;
import main.com.iglobuser.app.Config;
import main.com.iglobuser.constant.ACProgressCustom;
import main.com.iglobuser.constant.BaseUrl;
import main.com.iglobuser.constant.GPSTracker;
import main.com.iglobuser.constant.MyLanguageSession;
import main.com.iglobuser.constant.MySession;
import main.com.iglobuser.draglocation.DataParser;
import main.com.iglobuser.multipledroppoint.SelectRouteWithCollectiveRide;
import main.com.iglobuser.utils.NotificationUtils;
import www.develpoeramit.mapicall.ApiCallBuilder;

public class DriverAcceptStatus extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap gMap;
    GPSTracker gpsTracker;
    private double longitude = 0.0, latitude = 0.0;
    SupportMapFragment mapFragment;
    private RelativeLayout exit_app_but,updatedestinaton;
    private LinearLayout driver_det_lay;
    private ImageView message_lay;
    private TextView pickuplocation,dropofflocation,drivername,carnumber,carname,carbrand;
    private ImageView carimage;
    private CircleImageView driver_imag;
    LatLng picklatLng, droplatlong;
    ACProgressCustom ac_dialog;
    MySession mySession;
    String user_log_data="",user_id="",request_id="",car_type_id="",mobile="",driver_id="";
    public static double pic_lat = 0.0, pick_lon = 0.0, drop_lat = 0.0, drop_lon = 0.0, driver_lat = 0.0, driver_lon = 0.0;
    LatLng  driverlatlng;
    Marker driver_marker;
    final Timer timer = new Timer();
    private TextView addpoint,driver_sts,driver_status_sec,timeaway,rating_tv,cardetail;
    private String driver_detail_str="",pickup_str="",dropoff_str="",driver_img_str="",driver_name_str="",car_detail_str="";
    public static String fav_status="";
    BroadcastReceiver mRegistrationBroadcastReceiver;
    MarkerOptions pickmarkerpoint;
    private ImageView share_img,calllay;
    private MarkerOptions options = new MarkerOptions();
    Marker marker;
    private String language = "";
    MyLanguageSession myLanguageSession;
    private String map_root;
    private String EmergecyContact="";
    private boolean IsTripStart=false;
    private Dialog dialog;
    ArrayList<LatLng> points=new ArrayList<>();
    private LatLng newLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_driver_accept_status);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (myLanguageSession.getLanguage().equalsIgnoreCase("ar")) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

        }
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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
        idint();
        clickevent();
        checkGps();
        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
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
                        //(keyMessage.equalsIgnoreCase("new shared user"))
                        if (keyMessage.equalsIgnoreCase("your booking request is Cancel")) {
                            request_id = data.getString("request_id");
                            requestCancel();
                        }
                        else if (keyMessage.equalsIgnoreCase("your booking request is Arrived")) {
                           // status_map = "Arrived";
                            request_id = data.getString("request_id");
                            driver_status_sec.setText("" + getResources().getString(R.string.yourdriverarr));
                            driver_sts.setText("" + getResources().getString(R.string.driverarrived));
                            driverisArrived();

                        }
                        else if (keyMessage.equalsIgnoreCase("your booking request is Start")) {
                           IsTripStart=true;
                           if (marker!=null)marker.remove();
                            request_id = data.getString("request_id");
                            //titletext.setText("" + "Trip is Started");
                            driver_status_sec.setText("" + getResources().getString(R.string.enjoyride));
                            driver_sts.setText("" + getResources().getString(R.string.onride));
                            tripStarted();
                        } else if (keyMessage.equalsIgnoreCase("your booking request is End")) {
                            Intent i = new Intent(DriverAcceptStatus.this, FeedbackUs.class);
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



                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        new GetCurrentBooking().execute();
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
        if (UpdateLocation.update_sts==1){
            new GetCurrentBooking().execute();
            UpdateLocation.update_sts=0;
        }
        if (SelectRouteWithCollectiveRide.add_drop_sts==1){
            new GetCurrentBooking().execute();
            SelectRouteWithCollectiveRide.add_drop_sts=0;
        }
        LocalBroadcastManager.getInstance(DriverAcceptStatus.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(DriverAcceptStatus.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(DriverAcceptStatus.this.getApplicationContext());
        new GetEmergency().execute();

    }
    private void tripStarted() {
        DismissDialog();
         dialog = new Dialog(DriverAcceptStatus.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_heading_lay);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) dialog.findViewById(R.id.done_tv);

        TextView txtmsg = (TextView) dialog.findViewById(R.id.txtmsg);
        TextView bodymsg = (TextView) dialog.findViewById(R.id.bodymsg);
        txtmsg.setText(""+getResources().getString(R.string.tripstart));
        bodymsg.setText(""+getResources().getString(R.string.tripstarted));
        yes_tv.setText(""+getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (NotificationUtils.r!=null&&NotificationUtils.r.isPlaying()){
                    NotificationUtils.r.stop();
                }


            }
        });
        dialog.show();


    }
    private void tripFinish() {
        DismissDialog();
         dialog = new Dialog(DriverAcceptStatus.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_heading_lay);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) dialog.findViewById(R.id.done_tv);

        TextView txtmsg = (TextView) dialog.findViewById(R.id.txtmsg);
        TextView bodymsg = (TextView) dialog.findViewById(R.id.bodymsg);
        txtmsg.setText(""+getResources().getString(R.string.ridecomp));
        bodymsg.setText(""+getResources().getString(R.string.yourridefinish));
        yes_tv.setText(""+getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (NotificationUtils.r!=null&&NotificationUtils.r.isPlaying()){
                    NotificationUtils.r.stop();
                }
                Intent i = new Intent(DriverAcceptStatus.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);

            }
        });
        dialog.show();


    }

    private void driverisArrived() {
        DismissDialog();
        dialog = new Dialog(DriverAcceptStatus.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_heading_lay);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView yes_tv = (TextView) dialog.findViewById(R.id.done_tv);
        TextView txtmsg = (TextView) dialog.findViewById(R.id.txtmsg);
        TextView bodymsg = (TextView) dialog.findViewById(R.id.bodymsg);
        txtmsg.setText(""+getResources().getString(R.string.driverarrived));
        bodymsg.setText(""+getResources().getString(R.string.yourdriverarr));
        yes_tv.setText(""+getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (NotificationUtils.r!=null&&NotificationUtils.r.isPlaying()){
                    NotificationUtils.r.stop();
                }
            }
        });
        dialog.show();
    }


    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(DriverAcceptStatus.this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();

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
    private void initilizeMap() {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(DriverAcceptStatus.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DriverAcceptStatus.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        gMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        DriverAcceptStatus.this, R.raw.stylemap_3));
        gMap.setBuildingsEnabled(false);
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
        gMap.getUiSettings().setMapToolbarEnabled(false);
        gMap.getUiSettings().setZoomControlsEnabled(false);

        MarkerOptions marker;
        if (latitude == 0.0) {
            marker = new MarkerOptions().position(new LatLng(MainActivity.pickup_lat_str, MainActivity.pickup_lon_str)).flat(true).anchor(0.5f, 0.5f);

        } else {
            marker = new MarkerOptions().position(new LatLng(latitude, longitude)).flat(true).anchor(0.5f, 0.5f);

        }
        gMap.addMarker(marker);
        MarkerOptions myjob = new MarkerOptions().position(new LatLng(MainActivity.drop_lat_str, MainActivity.drop_lon_str)).flat(true).anchor(0.5f, 0.5f);
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
        gMap.addMarker(myjob);
        gMap.moveCamera(center);
        gMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                if (IsTripStart){
                    driver_marker.setPosition(newLatLng);
                    rotateMarker(driver_marker,location.getBearing());
                }
            }
        });


    }
    private void clickevent() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
               // toCancelRide();
            }
        });driver_det_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DriverAcceptStatus.this,DriverDetailAct.class);
                i.putExtra("driver_detail_str",driver_detail_str);
                i.putExtra("driver_id",driver_id);
                i.putExtra("pickup_str",pickup_str);
                i.putExtra("dropoff_str",dropoff_str);
                i.putExtra("fav_status",fav_status);
                i.putExtra("request_id",request_id);
                startActivity(i);
            }
        });message_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DriverAcceptStatus.this, ChatingAct.class);
                i.putExtra("receiver_id", driver_id);
                i.putExtra("request_id", request_id);
                i.putExtra("receiver_img", driver_img_str);
                i.putExtra("receiver_name", driver_name_str);
                i.putExtra("block_status", "");
                startActivity(i);
            }
        });
        updatedestinaton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DriverAcceptStatus.this, UpdateLocation.class);
                i.putExtra("request_id", request_id);
                startActivity(i);
            }
        });
        addpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DriverAcceptStatus.this, SelectRouteWithCollectiveRide.class);
                i.putExtra("request_id", request_id);
                startActivity(i);
            }
        });
        share_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FragmentShareTrip().setTripID(map_root).show(getSupportFragmentManager(),"");
            }
        });
        calllay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("MOBILE NO>>"," >> "+mobile);
                if (mobile == null || mobile.equalsIgnoreCase("")) {

                } else {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobile));
                    startActivity(callIntent);

                }
            }
        });
    }

    private void idint() {
        TextView emergency_call = findViewById(R.id.emergency_call);
        cardetail = findViewById(R.id.cardetail);
        addpoint = findViewById(R.id.addpoint);
        calllay = findViewById(R.id.calllay);
        share_img = findViewById(R.id.share_img);
        updatedestinaton = findViewById(R.id.updatedestinaton);
        rating_tv = findViewById(R.id.rating_tv);
        driver_status_sec = findViewById(R.id.driver_status_sec);
        driver_sts = findViewById(R.id.driver_sts);
        driver_imag = findViewById(R.id.driver_imag);
        timeaway = findViewById(R.id.timeaway);
        carbrand = findViewById(R.id.carbrand);
        carname = findViewById(R.id.carname);
        carimage = findViewById(R.id.carimage);
        carnumber = findViewById(R.id.carnumber);
        drivername = findViewById(R.id.drivername);
        dropofflocation = findViewById(R.id.dropofflocation);
        pickuplocation = findViewById(R.id.pickuplocation);
        exit_app_but = findViewById(R.id.exit_app_but);
        message_lay = findViewById(R.id.message_lay);
        driver_det_lay = findViewById(R.id.driver_det_lay);
        emergency_call.setOnClickListener(v->{
            if(EmergecyContact.equals("")){
                Toast.makeText(this, "No IGLOB branch found near you.", Toast.LENGTH_SHORT).show();
            }else {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + EmergecyContact));
                startActivity(callIntent);
            }
        });
    }

    private class GetCurrentBooking extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  progressbar.setVisibility(View.VISIBLE);
            if(ac_dialog!=null){
                ac_dialog.show();
            }

            if (gMap!=null){
                gMap.clear();
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
            if(ac_dialog!=null){
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {
                    Log.e("Resposne in my Booking", "" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                     map_root = jsonObject.getString("map");
                    if (msg.equalsIgnoreCase("successfull")) {
                        fav_status = jsonObject.getString("fav_status");
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            request_id = jsonObject1.getString("id");
                            car_type_id = jsonObject1.getString("car_type_id");
                            pickuplocation.setText(""+jsonObject1.getString("picuplocation"));
                            dropofflocation.setText(""+jsonObject1.getString("dropofflocation"));
                            carname.setText("" + jsonObject1.getString("type_name"));
                            pickup_str = jsonObject1.getString("picuplocation");
                            dropoff_str = jsonObject1.getString("dropofflocation");
                            String Status = jsonObject1.getString("status");
                            if (Status == null || Status.equalsIgnoreCase("")) {
                            } else {
                            }
                            if (Status.equalsIgnoreCase("Accept")) {
                            } else if (Status.equalsIgnoreCase("Arrived")) {
                                driver_status_sec.setText("" + getResources().getString(R.string.ddriverhasarr));
                                driver_sts.setText("" + getResources().getString(R.string.drivarr));                                // driverisArrived();
                            } else if (Status.equalsIgnoreCase("Start")) {
                                IsTripStart=true;
                                driver_status_sec.setText("" + getResources().getString(R.string.enjoyride));
                                driver_sts.setText("" + getResources().getString(R.string.onride));                                //  tripStarted();
                            } else if (Status.equalsIgnoreCase("End")) {
                                Intent l = new Intent(DriverAcceptStatus.this, FeedbackUs.class);
                                startActivity(l);
                                finish();
                            }

                            JSONArray jsonArray1 = jsonObject1.getJSONArray("driver_details");
                            if(jsonArray1!=null||jsonArray1.length()!=0){
                                driver_detail_str  =jsonArray1.toString();
                            }
                            for (int k = 0; k < jsonArray1.length(); k++) {
                                JSONObject jsonObject2 = jsonArray1.getJSONObject(k);

                                driver_name_str=jsonObject2.getString("first_name") + " " + jsonObject2.getString("last_name");
                                car_detail_str= jsonObject2.getString("vehicle_name") + "\n" + jsonObject2.getString("car_number").trim() + " , " + jsonObject2.getString("car_color");

                                drivername.setText("" + jsonObject2.getString("first_name") + " " + jsonObject2.getString("last_name"));
                                mobile = jsonObject2.getString("mobile");
                                driver_id = jsonObject2.getString("id");
                                //carnumber.setText("" + jsonObject2.getString("car_number"));
                                carnumber.setText("" + jsonObject2.getString("license_plate"));
                                String data ="" + jsonObject2.getString("car_color")+" "+jsonObject2.getString("vehicle_make")+" "+jsonObject2.getString("vehicle_name");
                                       String return_val =  capText(data);
                                        cardetail.setText(return_val);

                                rating_tv.setText("" + jsonObject2.getString("rating")+ "%");

                                if (jsonObject2.getString("lat") == null || jsonObject2.getString("lat").equalsIgnoreCase("")) {

                                } else {
                                    driver_lat = Double.parseDouble(jsonObject2.getString("lat"));
                                    driver_lon = Double.parseDouble(jsonObject2.getString("lon"));
                                    driverlatlng = new LatLng(driver_lat, driver_lon);
                                }
                                //"http://mobileappdevelop.co/NAXCAN/uploads/images/"
                                if (jsonObject2.getString("profile_image") == null || jsonObject2.getString("profile_image").equalsIgnoreCase("") || jsonObject2.getString("profile_image").equalsIgnoreCase(BaseUrl.image_baseurl)) {

                                } else {
                                   driver_img_str= jsonObject2.getString("profile_image");
                                    Picasso.with(DriverAcceptStatus.this).load(jsonObject2.getString("profile_image")).into(driver_imag);

                                }
                                if (jsonObject2.getString("car_image") == null || jsonObject2.getString("car_image").equalsIgnoreCase("") || jsonObject2.getString("car_image").equalsIgnoreCase(BaseUrl.image_baseurl)) {

                                } else {
                                    Picasso.with(DriverAcceptStatus.this).load(jsonObject2.getString("car_image")).into(carimage);

                                }

                            }
                            if (jsonObject1.getString("picuplat") == null || jsonObject1.getString("picuplat").equalsIgnoreCase("")) {
                            } else {

                                pic_lat = Double.parseDouble(jsonObject1.getString("picuplat"));
                                pick_lon = Double.parseDouble(jsonObject1.getString("pickuplon"));
                                drop_lat = Double.parseDouble(jsonObject1.getString("droplat"));
                                drop_lon = Double.parseDouble(jsonObject1.getString("droplon"));
                                if (gMap == null) {
                                    Log.e("Come Map Null", "");
                                } else {
                                    JSONArray jsonArray2 = jsonObject.getJSONArray("booking_dropoff");
                                    for (int ii =0;ii<jsonArray2.length();ii++){
                                        JSONObject jsonObject2 = jsonArray2.getJSONObject(ii);
                                        if (!IsTripStart&&jsonObject2.getString("droplon")!=null&&!jsonObject2.getString("droplon").equalsIgnoreCase("")){
                                            double droppoint_lat = Double.parseDouble(jsonObject2.getString("droplat"));
                                            double droppoint_lon = Double.parseDouble(jsonObject2.getString("droplon"));
                                            LatLng latLng = new LatLng(droppoint_lat, droppoint_lon);
                                            options.position(latLng);
                                            options.title("" + jsonObject2.getString("dropofflocation"));
                                            marker = gMap.addMarker(options);
                                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker));
                                        }
                                    }
                                    MarkerOptions markers = new MarkerOptions().position(new LatLng(pic_lat, pick_lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pick_marker)).flat(true).anchor(0.5f, 0.5f);
                                    MarkerOptions marker2 = new MarkerOptions().position(new LatLng(drop_lat, drop_lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker)).flat(true).anchor(0.5f, 0.5f);
                                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
                                    MarkerOptions markers1 = new MarkerOptions().position(new LatLng(driver_lat, driver_lon)).flat(true).anchor(0.5f, 0.5f);
                                    driver_marker = gMap.addMarker(markers1);
                                    driver_marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.carfromabove));


                                    gMap.addMarker(markers);
                                    gMap.addMarker(marker2);

                                    gMap.moveCamera(center);
                                    timer.schedule(new TimerTask() {
                                        public void run() {
                                           if (IsTripStart) {
                                               String url = getUrl(newLatLng, new LatLng(drop_lat, drop_lon));
                                               FetchUrl FetchUrl = new FetchUrl();
                                               FetchUrl.execute(url, "second");
                                           }else {
                                               getDriverLocation();
                                           }
                                        }
                                    }, 2000, 15000);


                                    String url = getUrl(new LatLng(pic_lat, pick_lon), new LatLng(drop_lat, drop_lon));
                                    FetchUrl FetchUrl = new FetchUrl();
                                    FetchUrl.execute(url, "first");
                                    LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                            .include(new LatLng(pic_lat, pick_lon))
                                            .include(new LatLng(drop_lat, drop_lon))
                                            .build();
                                    int width = getResources().getDisplayMetrics().widthPixels;
                                    int padding = (int) (width * 0); // offset from edges of the map 12% of screen
                                    gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));
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
            String type="";
            String data = "";
            try {
                data = downloadUrl(url[0]);
                type=url[0];

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
                if (IsTripStart)
                timeaway.setText("Estimate ride time: " + parser.getTime(jObject));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            if (points.size()>0){
                if (PolyUtil.isLocationOnPath(newLatLng, points, true, 100)) {
                    Log.e("isLocationOnPath","====>"+true);
                   return;
                }
            }

            PolylineOptions lineOptions = null;
            ArrayList<LatLng> animation_list = null;
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
                gMap.clear();
                gMap.addPolyline(lineOptions);
                MarkerOptions pick = new MarkerOptions().position(picklatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.pick_marker)).flat(true).anchor(0.5f, 0.5f);
                MarkerOptions drop = new MarkerOptions().position(droplatlong).icon(BitmapDescriptorFactory.fromResource(R.drawable.drop_marker)).flat(true).anchor(0.5f, 0.5f);
                gMap.addMarker(pick);
                gMap.addMarker(drop);
                MarkerOptions markers1 = new MarkerOptions().position(new LatLng(driver_lat, driver_lon)).flat(true).anchor(0.5f, 0.5f);
                driver_marker = gMap.addMarker(markers1);
                driver_marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.carfromabove));


            }
        }
    }


    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

    public void getDriverLocation() {
        System.out.println("driver_id ::: " + user_id);
        System.out.println("request_id ::: " + request_id);
        HashMap<String,String>parma=new HashMap<>();
        parma.put("user_id", user_id);
        parma.put("request_id", request_id);
        ApiCallBuilder.build(this).setUrl(BaseUrl.baseurl + "get_distance_time")
                .setParam(parma)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String res) {
                        try {
                            JSONObject response=new JSONObject(res);
                            if (response.getString("status").equals("1")) {
                                if (response.getString("driver_lat")!=null&&response.getString("driver_lat")!="null"&&response.getString("driver_lon")!="null"&&!response.getString("driver_lon").equalsIgnoreCase("")){
                                    Double lat = Double.parseDouble(response.getString("driver_lat"));
                                    Double lng = Double.parseDouble(response.getString("driver_lon"));
                                    driverlatlng = new LatLng(lat, lng);
                                    driver_marker.setPosition(driverlatlng);
                                    CameraUpdate center = CameraUpdateFactory.newLatLngZoom(driverlatlng, 15);
                                    gMap.animateCamera(center);
                                }
                                JSONArray jsonArray = response.getJSONArray("result");
                                Log.e("COME IN LOOP DOWN","");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Log.e("COME IN LOOP","");
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String my_booking = jsonObject1.getString("my_booking");
                                    if (my_booking.equalsIgnoreCase("yes")) {
                                        String distance = jsonObject1.getString("distance");
                                        String minaway = String.valueOf(jsonObject1.getInt("min_way"));
                                        Log.e("COME IN distance",""+distance);
                                        if (jsonObject1.getString("picuplat") != null && jsonObject1.getString("droplat") != null) {
                                            double plat = Double.parseDouble(jsonObject1.getString("picuplat"));
                                            double plon = Double.parseDouble(jsonObject1.getString("pickuplon"));
                                            double dlat = Double.parseDouble(jsonObject1.getString("droplat"));
                                            double dlon = Double.parseDouble(jsonObject1.getString("droplon"));
                                            LatLng latLng = new LatLng(plat, plon);
                                            LatLng Drop_latLng = new LatLng(dlat, dlon);
                                            if (gMap != null) {
                                                if (!IsTripStart) {
                                                    timeaway.setText("" + minaway + getResources().getString(R.string.minaway));
                                                    pickmarkerpoint = new MarkerOptions().position(latLng).flat(true).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromBitmap(distanceMarker(minaway + "\n" + "min")));
                                                    gMap.addMarker(pickmarkerpoint);
                                                }
//                                                driver_marker.setRotation(bearingBetweenLocations(driverlatlng,IsTripStart?Drop_latLng:latLng));
                                                rotateMarker(driver_marker,bearingBetweenLocations(driverlatlng,IsTripStart?Drop_latLng:latLng));
                                            }
                                        }
                                        break;
                                    }
                                }
                                System.out.println("----------latlong---------------- " + driverlatlng);
                            } else {
                                Log.e("CANCEL COMPLETION : ", response.toString());
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
    private float bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {
        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return (float) brng;
    }
    void DismissDialog(){
        if (dialog!=null){
            if (dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }
    private void requestCancel() {
        DismissDialog();
        dialog= new Dialog(DriverAcceptStatus.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_heading_lay);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView done_tv = (TextView) dialog.findViewById(R.id.done_tv);
        done_tv.setText(""+getResources().getString(R.string.ok));
        done_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (NotificationUtils.r!=null&&NotificationUtils.r.isPlaying()){
                    NotificationUtils.r.stop();
                }
                finish();
            }
        });
        dialog.show();


    }
    private Bitmap distanceMarker(String s) {
        View markerLayout = getLayoutInflater().inflate(R.layout.distance_marker, null);

        ImageView markerImage = (ImageView) markerLayout.findViewById(R.id.marker_image);
        TextView markerRating = (TextView) markerLayout.findViewById(R.id.marker_text);
        markerImage.setImageResource(R.drawable.distancemarker);
        markerRating.setText(s);

        markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

        final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerLayout.draw(canvas);
        return bitmap;
    }
    public String capText(String data){
    StringBuilder sb = new StringBuilder(data);
    sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
    return sb.toString();
}
    private class GetEmergency extends AsyncTask<String, String, String> {
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
                String postReceiverUrl = BaseUrl.get().getNearBranch();
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("country_name", gpsTracker.getCountry());
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
                Log.e("Get Emergency", ">>>>>>>>>>>>" + response);
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
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int k = 0; k < jsonArray.length(); k++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(k);
                            EmergecyContact=jsonObject1.getString("mobile");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }
    private String getEsimateTime(Location location){
        Location location2 = new Location("");
        location2.setLatitude(drop_lat);
        location2.setLongitude(drop_lon);
        float distanceInMeters = location.distanceTo(location2);
        float distanceInKM=distanceInMeters/1000;
        Log.e("Distance","=====>"+distanceInMeters);
        Log.e("Speed","=====>"+location.getSpeed());
        float speedIsMetersPerMinute = location.getSpeed()>40?location.getSpeed():40;
        float totalMinutes = distanceInKM / speedIsMetersPerMinute;
        String minutes = String.valueOf(totalMinutes % 60);
        minutes = minutes.length() == 1 ? "0" + minutes : minutes;
        return (totalMinutes / 60) + "hrs :" + minutes+" Min";
    }
    private void rotateMarker(final Marker marker, final float toRotation) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {

                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;

                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });

    }
}