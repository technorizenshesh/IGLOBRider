package main.com.iglobuser.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.LinkedHashMap;
import java.util.Map;

import main.com.iglobuser.R;
import main.com.iglobuser.constant.BaseUrl;
import main.com.iglobuser.constant.MySession;


public class EmergencyActivity extends AppCompatActivity {
    private RelativeLayout exit_app_but;
    private EditText name_et, mobile_et, email_et;
    private String name_str = "", mobile_str = "",user_log_data="",user_id="", email_str = "", emergency_id = "", contact_sts = "";
    private TextView save_tv, notetv;
    ProgressBar prgressbar;
    private ImageView call;
    private LinearLayout addedcontact, deletecontact;
   private MySession mySession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_emergency);
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
        idinit();
        clickevent();
        new GetEmergency().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void clickevent() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        save_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(EmergencyActivity.this, "In working...", Toast.LENGTH_LONG).show();

                name_str = name_et.getText().toString();
                mobile_str = mobile_et.getText().toString();
                email_str = email_et.getText().toString();
                if (name_str == null || name_str.equalsIgnoreCase("")) {
                    Toast.makeText(EmergencyActivity.this, getResources().getString(R.string.allfields), Toast.LENGTH_LONG).show();
                } else if (mobile_str == null || mobile_str.equalsIgnoreCase("")) {
                    Toast.makeText(EmergencyActivity.this, getResources().getString(R.string.allfields), Toast.LENGTH_LONG).show();

                } else if (email_str == null || email_str.equalsIgnoreCase("")) {
                    Toast.makeText(EmergencyActivity.this, getResources().getString(R.string.allfields), Toast.LENGTH_LONG).show();
                } else {
                    if (emergency_id==null||emergency_id.equalsIgnoreCase("")){

                        new AddEmergencyContact().execute("add");
                    }
                    else {
                        new AddEmergencyContact().execute("update");
                    }

                }
            }
        });
        deletecontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Deletecontact().execute();
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contact_sts.equalsIgnoreCase("Deactive")) {
                    notActiveYet();
                } else {
                    if (mobile_str == null || mobile_str.equalsIgnoreCase("")) {

                    } else {


                        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobile_str));
                        startActivity(callIntent);

                    }
                }
            }
        });
    }

    private void notActiveYet() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(EmergencyActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_dialog_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        TextView oktv = (TextView) canceldialog.findViewById(R.id.oktv);
        TextView headtv = (TextView) canceldialog.findViewById(R.id.headtv);
        headtv.setText(getResources().getString(R.string.sorry));
        TextView msg = (TextView) canceldialog.findViewById(R.id.msgtv);
        msg.setText(""+getResources().getString(R.string.notactive));
        oktv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();


            }
        });
        canceldialog.show();


    }

    private void idinit() {
        addedcontact = (LinearLayout) findViewById(R.id.addedcontact);
        deletecontact = (LinearLayout) findViewById(R.id.deletecontact);
        call = (ImageView) findViewById(R.id.call);
        prgressbar = (ProgressBar) findViewById(R.id.prgressbar);
        save_tv = (TextView) findViewById(R.id.save_tv);
        notetv = (TextView) findViewById(R.id.notetv);
        email_et = (EditText) findViewById(R.id.email_et);
        name_et = (EditText) findViewById(R.id.name_et);
        mobile_et = (EditText) findViewById(R.id.mobile_et);
        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);
    }
    private class AddEmergencyContact extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgressbar.setVisibility(View.VISIBLE);
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                String postReceiverUrl = "";
                if (strings[0].equalsIgnoreCase("update")){
                    postReceiverUrl = BaseUrl.baseurl + "update_number?";
                    Log.e("Emergency", ">>>>>>>>>>>>" + "update");
                }else {
                    postReceiverUrl = BaseUrl.baseurl + "emergency_number?";
                    Log.e("Emergency", ">>>>>>>>>>>>" + "add");
                }

                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                if (strings[0].equalsIgnoreCase("update")){
                    params.put("id", emergency_id);
                }
                params.put("user_id", user_id);
                params.put("email", email_str);
                params.put("mobile", mobile_str);
                params.put("name", name_str);


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
            prgressbar.setVisibility(View.GONE);
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                addSuccessfully();
                new GetEmergency().execute();

            }


        }
    }

    private class Deletecontact extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgressbar.setVisibility(View.VISIBLE);
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://halatx.halasmart.com/hala/webservice/remove_contact?id=1
            try {
                String postReceiverUrl = BaseUrl.baseurl + "remove_contact?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("id", emergency_id);
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
                Log.e("Add Emergency", ">>>>>>>>>>>>" + response);
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
            prgressbar.setVisibility(View.GONE);
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                mobile_et.setText("");
                name_et.setText("");
                email_et.setText("");
                new GetEmergency().execute();
            }


        }
    }

    private class GetEmergency extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgressbar.setVisibility(View.VISIBLE);
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://halatx.halasmart.com/hala/webservice/get_emergency?user_id=1
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_emergency?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
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
            prgressbar.setVisibility(View.GONE);
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        addedcontact.setVisibility(View.VISIBLE);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int k = 0; k < jsonArray.length(); k++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(k);
                            emergency_id = jsonObject1.getString("id");
                            contact_sts = jsonObject1.getString("status");
                            if (jsonObject1.getString("status").equalsIgnoreCase("Deactive")) {
                                notetv.setVisibility(View.VISIBLE);
                            } else {
                                notetv.setVisibility(View.GONE);
                            }
                            mobile_str = jsonObject1.getString("mobile");
                            email_et.setText(jsonObject1.getString("email"));
                            mobile_et.setText(jsonObject1.getString("mobile"));
                            name_et.setText(jsonObject1.getString("name"));
                            save_tv.setText(getResources().getString(R.string.edit));
                        }

                    }
                    else {
                        emergency_id="";
                        addedcontact.setVisibility(View.GONE);
                        save_tv.setText(getResources().getString(R.string.save));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }
    private void addSuccessfully() {
        //   Log.e("War Msg in dialog", war_msg);
        final Dialog canceldialog = new Dialog(EmergencyActivity.this);
        canceldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        canceldialog.setCancelable(false);
        canceldialog.setContentView(R.layout.custom_dialog_lay);
        canceldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


         TextView oktv = (TextView) canceldialog.findViewById(R.id.oktv);
         TextView msg = (TextView) canceldialog.findViewById(R.id.msgtv);
        msg.setText(""+getResources().getString(R.string.emergencyadd));
        oktv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceldialog.dismiss();


            }
        });
        canceldialog.show();


    }

}
