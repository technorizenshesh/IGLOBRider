package main.com.iglobuser.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import main.com.iglobuser.R;
import main.com.iglobuser.constant.ACProgressCustom;
import main.com.iglobuser.constant.BaseUrl;
import main.com.iglobuser.constant.MyLanguageSession;
import main.com.iglobuser.constant.MySession;
import main.com.iglobuser.constant.RideBean;
import main.com.iglobuser.restapi.ApiClient;

public class RideHistory extends AppCompatActivity {
private RideHisAdp rideHisAdp;
private ListView historylist;
private RelativeLayout exit_app_but;
private ArrayList<RideBean> rideBeanArrayList;
private RideHisAdp ridehisadp;
private MySession mySession;
private String user_log_data="",user_id="";
    ACProgressCustom ac_dialog;
    private String language = "";
    MyLanguageSession myLanguageSession;
    private TextView noride_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());



        setContentView(R.layout.activity_ride_history);
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

        idint();
        clickevt();
        new RideHistoryJson().execute();
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

    private void clickevt() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void idint() {
        noride_tv = findViewById(R.id.noride_tv);
        exit_app_but = findViewById(R.id.exit_app_but);
        historylist = findViewById(R.id.historylist);
        rideHisAdp = new RideHisAdp(RideHistory.this, rideBeanArrayList);
        historylist.setAdapter(rideHisAdp);
    }

    public class RideHisAdp extends BaseAdapter {
        String[] result;
        Context context;
        ArrayList<RideBean> rideBeanArrayList;
        private LayoutInflater inflater = null;


        public RideHisAdp(Activity activity, ArrayList<RideBean> rideBeanArrayList) {
            this.context = activity;
            this.rideBeanArrayList = rideBeanArrayList;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
          //  return 4;        }
           return rideBeanArrayList == null ? 0 : rideBeanArrayList.size();        }

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

            rowView = inflater.inflate(R.layout.custom_ridehistory_lay, null);
            TextView pickuplocation = rowView.findViewById(R.id.pickuplocation);
            TextView dropofflocation = rowView.findViewById(R.id.dropofflocation);
            TextView date_tv = rowView.findViewById(R.id.date_tv);
            TextView paymenttype = rowView.findViewById(R.id.paymenttype);
            TextView drivername = rowView.findViewById(R.id.drivername);
            TextView cardetail = rowView.findViewById(R.id.cardetail);
            TextView status_tv = rowView.findViewById(R.id.status_tv);
            TextView invoice_tv = rowView.findViewById(R.id.invoice_tv);
            ImageView addfavourite = rowView.findViewById(R.id.addfavourite);
            CircleImageView driver_img = rowView.findViewById(R.id.driver_img);

            pickuplocation.setText(""+rideBeanArrayList.get(position).getPicuplocation());
            dropofflocation.setText(""+rideBeanArrayList.get(position).getDropofflocation());
            date_tv.setText(""+rideBeanArrayList.get(position).getReq_datetime());
            paymenttype.setText(""+rideBeanArrayList.get(position).getPayment_type());
            drivername.setText(""+rideBeanArrayList.get(position).getDrivername());
            cardetail.setText(""+rideBeanArrayList.get(position).getDrivercardetail());
            String fav_status=rideBeanArrayList.get(position).getFav_status();
            //no_favorite
            if (fav_status.equalsIgnoreCase("favorite")){
                addfavourite.setImageResource(R.drawable.fav_add);
            }
            else {
                addfavourite.setImageResource(R.drawable.blackwishlist);
            }
            String driver_img_str=rideBeanArrayList.get(position).getDriverimage();
            if (driver_img_str!=null&&!driver_img_str.equalsIgnoreCase("")){
                Picasso.with(RideHistory.this).load(driver_img_str).into(driver_img);
            }
            if (rideBeanArrayList.get(position).getStatus().equalsIgnoreCase("Finish")){
                status_tv.setText(getResources().getString(R.string.completed));
                status_tv.setBackgroundResource(R.color.green);
                invoice_tv.setVisibility(View.VISIBLE);
            }
            else if (rideBeanArrayList.get(position).getStatus().equalsIgnoreCase("Cancel")||rideBeanArrayList.get(position).getStatus().equalsIgnoreCase("Cancel_by_driver")||rideBeanArrayList.get(position).getStatus().equalsIgnoreCase("Cancel_by_user"))
            {
                status_tv.setText(getResources().getString(R.string.canceled));
                status_tv.setBackgroundResource(R.color.red);
                invoice_tv.setVisibility(View.GONE);
            }
            addfavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callapi(user_id, rideBeanArrayList.get(position).getDriver_id(),position);
                }
            });
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(RideHistory.this,RideDetailAct.class);
                    i.putExtra("rideid",rideBeanArrayList.get(position).getId());
                    startActivity(i);
                }
            });
            invoice_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(RideHistory.this,InvoiceActivity.class);
                    i.putExtra("request_id",rideBeanArrayList.get(position).getId());
                    startActivity(i);
                }
            });

            return rowView;
        }

    }


    private class RideHistoryJson extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //prgressbar.setVisibility(View.VISIBLE);
            if(ac_dialog!=null){
                ac_dialog.show();
            }
            rideBeanArrayList = new ArrayList<>();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/get_user_history?user_id=22&type=DRIVER
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_user_history?";
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
                Log.e("Json History", ">>>>>>>>>>>>" + response);
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
                    Log.e("Resposne in my Booking", "" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    if (msg.equalsIgnoreCase("successfull")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            RideBean ridebean = new RideBean();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            ridebean.setId(jsonObject1.getString("id"));
                            ridebean.setPicuplocation(jsonObject1.getString("picuplocation"));
                            ridebean.setDropofflocation(jsonObject1.getString("dropofflocation"));
                            ridebean.setStatus(jsonObject1.getString("status"));
                            ridebean.setBooktype(jsonObject1.getString("booktype"));
                            ridebean.setPayment_type(jsonObject1.getString("payment_type"));
                            ridebean.setFav_status(jsonObject1.getString("fav_status"));

                            try {
                                Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject1.getString("req_datetime"));
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm");
                                String strDate = formatter.format(date1);
                                ridebean.setReq_datetime(strDate);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            JSONArray jsonArray1 = jsonObject1.getJSONArray("driver_details");
                            for (int k = 0; k < jsonArray1.length(); k++) {
                                JSONObject jsonObject2 = jsonArray1.getJSONObject(k);

                                String driver_name_str=jsonObject2.getString("first_name") + " " + jsonObject2.getString("last_name");
                                String car_detail_str= jsonObject2.getString("vehicle_name") + "\n" + jsonObject2.getString("car_number").trim() + " , " + jsonObject2.getString("car_color");
                                String mobile = jsonObject2.getString("mobile");
                                String  driver_id = jsonObject2.getString("id");
                                String  profileimage = jsonObject2.getString("profile_image");

                                ridebean.setDrivername(driver_name_str);
                                ridebean.setDrivercardetail(car_detail_str);
                                ridebean.setDrivermobile(mobile);
                                ridebean.setDriverimage(profileimage);
                                ridebean.setDriver_id(driver_id);
                                //"http://mobileappdevelop.co/NAXCAN/uploads/images/"

                            }


                            rideBeanArrayList.add(ridebean);



                        }

                        ridehisadp = new RideHisAdp(RideHistory.this,rideBeanArrayList);
                        historylist.setAdapter(ridehisadp);
                        ridehisadp.notifyDataSetChanged();

                    }

                    if (rideBeanArrayList==null||rideBeanArrayList.isEmpty()){
                        noride_tv.setVisibility(View.VISIBLE);
                    }
                    else {
                        noride_tv.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private void callapi(String user_id, String driver_id, final int position) {
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
                        Log.e("addFavDriver >", " >" + responseData);
                        if (object.getString("status").equals("1")) {
                            new RideHistoryJson().execute();
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

}
