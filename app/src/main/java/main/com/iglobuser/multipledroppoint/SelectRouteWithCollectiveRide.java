package main.com.iglobuser.multipledroppoint;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.iglobuser.R;
import main.com.iglobuser.activity.DriverAcceptStatus;
import main.com.iglobuser.app.Config;
import main.com.iglobuser.constant.ACProgressCustom;
import main.com.iglobuser.constant.BaseUrl;
import main.com.iglobuser.constant.GPSTracker;
import main.com.iglobuser.constant.MultipartUtility;
import main.com.iglobuser.constant.MySession;
import main.com.iglobuser.draglocation.DataParser;
import main.com.iglobuser.draglocation.MyTask;
import main.com.iglobuser.draglocation.WebOperations;
import main.com.iglobuser.utils.NotificationUtils;

public class SelectRouteWithCollectiveRide extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap gMap;
    int sts_main_route = 0;
    GPSTracker gpsTracker;
    private double longitude = 0.0, latitude = 0.0;
    public static LatLng firstlatlong, lastlatlong;
    RelativeLayout exit_app_but;
    int clickstatus = 0;
    int colstatus = 0;
    String point_str = "pickpoint";
    ProgressBar prgressbar;
    private String address_complete = "", pickuploc_str = "", dropoffloc_str = "";
    boolean sts;
    public static double pickup_lat_str = 0.0, pickup_lon_str = 0.0, drop_lat_str = 0.0, drop_lon_str = 0.0;
    public static double route_pick_lat = 0.0, route_pick_lon = 0.0, route_drop_lat = 0.0, route_drop_lon = 0.0;
    MySession mySession;
    double tolerance = 8; // meters
    int radious = 8; // meters
    ArrayList<LatLng> points;
    ArrayList<LatLng> mainroutelist = new ArrayList<>();
    Marker marker_pick;
    Marker marker_drop;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    double distancefrompickup = 0, distancefromdropoff = 0;
    String time_zone = "";
    TextView continue_tv, dropofflocation;
    String request_id = "", user_log_data = "", user_id = "";
    ACProgressCustom ac_dialog, ac_dialog_m;
    Polyline polyline_main, polyline_second;
    boolean request_send_val = false;
    List<HashMap<String, String>> direction_arrow;
    public static int add_drop_sts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_route_with_collective_ride);
        ac_dialog = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();
        ac_dialog_m = new ACProgressCustom.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getResources().getString(R.string.pleasewait))
                .textSize(20).textMarginTop(5)
                .fadeColor(Color.DKGRAY).build();


        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
            NotificationUtils.r.stop();
        }

        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        if (user_log_data == null) {

        } else {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("message");
                if (message.equalsIgnoreCase("successfull")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle == null || bundle.isEmpty()) {

        } else {
            request_id = bundle.getString("request_id");
        }
        idinits();
        clickevetn();
        checkGps();

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }
        clcikepopup(point_str);


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


                            // requestCancel();
                        }
                        if (keyMessage.equalsIgnoreCase("your booking request is assign to new driver")) {
                            // requestReassign();
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
        LocalBroadcastManager.getInstance(SelectRouteWithCollectiveRide.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(SelectRouteWithCollectiveRide.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(SelectRouteWithCollectiveRide.this.getApplicationContext());

    }


    private void clickevetn() {


        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        continue_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dropoffloc_str = dropofflocation.getText().toString();

                if (dropoffloc_str == null || dropoffloc_str.equalsIgnoreCase("")) {
                    Toast.makeText(SelectRouteWithCollectiveRide.this, getResources().getString(R.string.seldroppoint), Toast.LENGTH_LONG).show();
                    clcikepopup("else");
                } else {
                    confirmPop();
                }

            }
        });
    }


    private void idinits() {
        dropofflocation = findViewById(R.id.dropofflocation);
        continue_tv = findViewById(R.id.continue_tv);


        prgressbar = (ProgressBar) findViewById(R.id.prgressbar);

        dropofflocation = findViewById(R.id.dropofflocation);
        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);
    }

    private void initilizeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(SelectRouteWithCollectiveRide.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SelectRouteWithCollectiveRide.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        gMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        SelectRouteWithCollectiveRide.this, R.raw.stylemap_3));

        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMapToolbarEnabled(false);
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).flat(true).anchor(0.5f, 0.5f);
        gMap.addMarker(marker);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
        gMap.animateCamera(cameraUpdate);
        gMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                String url = getUrl(new LatLng(DriverAcceptStatus.pic_lat, DriverAcceptStatus.pick_lon), new LatLng(DriverAcceptStatus.drop_lat, DriverAcceptStatus.drop_lon));
                FetchUrl FetchUrl = new FetchUrl();
                FetchUrl.execute(url, "first");
                LatLngBounds latLngBounds = new LatLngBounds.Builder()
                        .include(new LatLng(DriverAcceptStatus.pic_lat, DriverAcceptStatus.pick_lon))
                        .include(new LatLng(DriverAcceptStatus.drop_lat, DriverAcceptStatus.drop_lon))
                        .build();
                int width = getResources().getDisplayMetrics().widthPixels;
                int padding = (int) (width * 0); // offset from edges of the map 12% of screen
                gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));

            }
        });
        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng pointa) {
                LatLng point = null;
                if (clickstatus == 0) {

                    boolean isLocationOnPath = PolyUtil.isLocationOnPath(pointa, mainroutelist, true, tolerance);
                    Log.e("point DRAG>>", "" + point);
                    Log.e("CHECK NEAR LAT ", "FIRST > " + GeoPointDistanceAlgorithm.GeoPointDistanceAlgorithm(mainroutelist, pointa, radious));
                    Log.e("CHECK NEAR LAT ", "DISTANCE > " + GeoPointDistanceAlgorithm.GeoPointDistanceAlgorithma(mainroutelist, pointa, radious));
                    Log.e("CHECK NEAR LAT ", "LATLONG > " + GeoPointDistanceAlgorithm.GeoPointDistanceAlgorithmLat(mainroutelist, pointa, radious));

                    if (isLocationOnPath) {
                        point = GeoPointDistanceAlgorithm.GeoPointDistanceAlgorithmLat(mainroutelist, pointa, radious);
                        address_complete = loadAddress(point.latitude, point.longitude);
                        dropofflocation.setText(address_complete);

                        clickstatus = 1;
                        MarkerOptions marker2 = new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)).flat(true).anchor(0.5f, 0.5f).snippet("pickup").visible(true);
                        marker_pick = gMap.addMarker(marker2);
                        marker_pick.setDraggable(true);
                        firstlatlong = point;
                        Log.e("Point Type >>>", "" + isLocationOnPath);
                        pickup_lat_str = firstlatlong.latitude;
                        pickup_lon_str = firstlatlong.longitude;


                        //clcikepopup("else");

                    }
                    else {
                        longclickonpath();
                    }
                }
            }
        });

        gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                arg0.getId();
                Log.e("" + arg0 + "System out", "onMarkerDragStart..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);
            }

            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                Log.e("System out", "onMarkerDragEnd..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);
                String ponit_str = "";
                LatLng point = null;
                LatLng pointa = new LatLng(arg0.getPosition().latitude, arg0.getPosition().longitude);
                boolean isLocationOnPaths = PolyUtil.isLocationOnPath(pointa, mainroutelist, true, tolerance);


                if (isLocationOnPaths) {
                    point = GeoPointDistanceAlgorithm.GeoPointDistanceAlgorithmLat(mainroutelist, pointa, radious);
                    firstlatlong = point;
                    if (arg0.getSnippet().equalsIgnoreCase("pickup")) {

                        address_complete = loadAddress(point.latitude, point.longitude);
                        dropofflocation.setText(address_complete);
                        if (arg0.getSnippet().equalsIgnoreCase("pickup")) {
                            marker_pick.setPosition(firstlatlong);
                        } else {
                            marker_drop.setPosition(lastlatlong);
                        }


                    } else {
                        double distancefromdropoffs = distFrom(point.latitude, point.longitude, route_pick_lat, route_pick_lon);

                        if (distancefromdropoffs > distancefrompickup) {
                            distancefromdropoff = distancefromdropoffs;
                            lastlatlong = point;
                            drop_lat_str = lastlatlong.latitude;
                            drop_lon_str = lastlatlong.longitude;

                            address_complete = loadAddress(point.latitude, point.longitude);
                            dropofflocation.setText(address_complete);


                        } else {
                            if (arg0.getSnippet().equalsIgnoreCase("pickup")) {
                                marker_pick.setPosition(firstlatlong);
                            } else {
                                marker_drop.setPosition(lastlatlong);
                            }
                        }


                    }


                } else {
                    ponit_str = arg0.getSnippet();
                    if (arg0.getSnippet().equalsIgnoreCase("pickup")) {
                        marker_pick.setPosition(firstlatlong);
                    } else {
                        marker_drop.setPosition(lastlatlong);
                    }
                    pointOnPath(ponit_str);
                }

            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                Log.e("System out", "onMarkerDrag...");
            }
        });

//Don't forget to Set draggable(true) to marker, if this not set marker does not drag.

        // gMap.addMarker(new MarkerOptions().position(crntLocationLatLng).icon(BitmapDescriptorFactory .fromResource(R.drawable.marker_logo_f)).draggable(true));


    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(SelectRouteWithCollectiveRide.this).unregisterReceiver(mRegistrationBroadcastReceiver);

    }

    private String loadAddress(double latitude, double longitude) {
        try {
            WebOperations wo = new WebOperations(SelectRouteWithCollectiveRide.this.getApplicationContext());
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

    private void checkGps() {
        gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {

            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

            Log.e("FindTruck Latitude", "" + latitude);
            Log.e("FindTruck longitude", "" + longitude);
        } else {
            // if gps off get lat long from network
            //   locationfromnetwork();
            gpsTracker.showSettingsAlert();
        }


    }

    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=" + getResources().getString(R.string.googlekey_other);
        ;
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
            points = new ArrayList<>();
            PolylineOptions lineOptions = null;
            ArrayList<LatLng> animation_list = null;
            if (result != null) {
                for (int i = 0; i < result.size(); i++) {
                    animation_list = new ArrayList<>();
                    lineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = result.get(i);
                    if (colstatus == 0) {
                        direction_arrow = result.get(i);
                    }

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                        animation_list.add(position);
                    }
                    lineOptions.addAll(points);
                    if (sts_main_route == 0) {
                        mainroutelist.addAll(points);
                        sts_main_route = 1;
                    }
                    lineOptions.width(10);
                    if (colstatus == 0) {
                        lineOptions.color(Color.BLACK);
                    } else {
                        lineOptions.color(Color.GREEN);
                    }
                }

            }
            if (lineOptions != null) {
                if (colstatus == 0) {
                    polyline_main = gMap.addPolyline(lineOptions);

                } else {
                    if (polyline_second != null) {
                        polyline_second.remove();
                    }
                    polyline_second = gMap.addPolyline(lineOptions);
                }

            } else {
            }
        }
    }

    private void clcikepopup(String point_str) {


        //   Log.e("War Msg in dialog", war_msg);
        final Dialog dialogSts = new Dialog(SelectRouteWithCollectiveRide.this);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.clcik_path_popup);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView click_tv = dialogSts.findViewById(R.id.click_tv);
        if (point_str.equalsIgnoreCase("pickpoint")) {
            click_tv.setText(getResources().getString(R.string.click_getin));
        } else {
            click_tv.setText(getResources().getString(R.string.click_getout));
        }
        TextView ok = dialogSts.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
            }
        });


        if (dialogSts != null && dialogSts.isShowing()) {

        } else {
            dialogSts.show();
        }


    }


    private void confirmPop() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(SelectRouteWithCollectiveRide.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.bookig_cancel_me_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView no_tv = (TextView) canceldialog.findViewById(R.id.no_tv);
        TextView message_tv = (TextView) canceldialog.findViewById(R.id.message_tv);
        TextView heading_tv = (TextView) canceldialog.findViewById(R.id.heading_tv);
        heading_tv.setText("" + getResources().getString(R.string.confirm));
        message_tv.setText("" + getResources().getString(R.string.areucon));

        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (canceldialog != null) {
                    canceldialog.cancel();
                    canceldialog.dismiss();
                }

                Calendar c = Calendar.getInstance();
                TimeZone tz = c.getTimeZone();
                Log.e("TIME ZONE >>", tz.getDisplayName());
                Log.e("TIME ZONE ID>>", tz.getID());
                time_zone = tz.getID();

                if (!request_send_val) {
                    new SendShareRequest().execute();
                } else {
                    Toast.makeText(SelectRouteWithCollectiveRide.this, getResources().getString(R.string.pleasewait), Toast.LENGTH_LONG).show();

                }

            }
        });
        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
                dragandDrop();
            }
        });
        canceldialog.show();


    }

    private void dragandDrop() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(SelectRouteWithCollectiveRide.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.draganddrop_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);


        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }
    private void longclickonpath() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(SelectRouteWithCollectiveRide.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.draganddrop_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.yes_tv);
        final TextView message_tv = (TextView) canceldialog.findViewById(R.id.message_tv);
        message_tv.setText(""+getResources().getString(R.string.selectpointonnearpath));

        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();

            }
        });
        canceldialog.show();


    }


    public class SendShareRequest extends AsyncTask<String, String, String> {
        String Jsondata;

        protected void onPreExecute() {
            try {
                super.onPreExecute();
                request_send_val = true;
                // prgressbar.setVisibility(View.VISIBLE);
                if (ac_dialog_m != null) {
                    ac_dialog_m.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://hitchride.net/webservice/add_booking_dropoff?request_id=186&dropofflocation=bhawar kua&droplat=22.123&droplon=75.123
            String charset = "UTF-8";
            String postReceiverUrl = BaseUrl.baseurl + "add_booking_dropoff?";
            Log.e("requestURL >>", postReceiverUrl);

            try {
                MultipartUtility multipart = new MultipartUtility(postReceiverUrl, charset);
                multipart.addFormField("request_id", request_id);

                multipart.addFormField("user_id", user_id);

                multipart.addFormField("dropofflocation", dropoffloc_str);
                multipart.addFormField("droplat", ""+firstlatlong.latitude);
                multipart.addFormField("droplon", ""+firstlatlong.longitude);
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
            request_send_val = false;

            //  prgressbar.setVisibility(View.GONE);
            if (ac_dialog_m != null) {
                ac_dialog_m.dismiss();
            }
            Log.e("Add Drop Point", " > " + result);
            if (result == null) {
            } else if (result.isEmpty()) {
            } else {
                try {
                    add_drop_sts = 1;
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("status");
                    if (msg.equalsIgnoreCase("1")) {
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    private void pointOnPath(String pickup) {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(SelectRouteWithCollectiveRide.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_heading_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView yes_tv = (TextView) canceldialog.findViewById(R.id.done_tv);

        TextView message_tv = (TextView) canceldialog.findViewById(R.id.bodymsg);
        TextView heading_tv = (TextView) canceldialog.findViewById(R.id.txtmsg);
        heading_tv.setText("" + getResources().getString(R.string.wrongpoint));
        if (pickup.equalsIgnoreCase("pickup")) {
            message_tv.setText("" + getResources().getString(R.string.wrongpick));
        } else {
            message_tv.setText("" + getResources().getString(R.string.wrongdrop));
        }


        yes_tv.setText("" + getResources().getString(R.string.ok));
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();
            }
        });
        canceldialog.show();


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

        return dist * 1.60934;
    }


}