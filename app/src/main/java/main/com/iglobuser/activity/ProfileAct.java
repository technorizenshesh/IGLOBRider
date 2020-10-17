package main.com.iglobuser.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import de.hdodenhof.circleimageview.CircleImageView;
import main.com.iglobuser.R;
import main.com.iglobuser.app.Config;
import main.com.iglobuser.constant.ACProgressCustom;
import main.com.iglobuser.constant.BaseUrl;
import main.com.iglobuser.constant.CountryBean;
import main.com.iglobuser.constant.GPSTracker;
import main.com.iglobuser.constant.MultipartUtility;
import main.com.iglobuser.constant.MyLanguageSession;
import main.com.iglobuser.constant.MySession;
import main.com.iglobuser.utils.NotificationUtils;

public class ProfileAct extends AppCompatActivity {
    private EditText state_et,city_et,firstname_et, lastname_et, email_et, mobile_et;
    private String firstname_str,state_str="",city_str="", lastname_str, email_str, mobile_str, language_str = "", currency_str, place_str;
    private CircleImageView user_img;
    private ImageView identy_img;
    private RelativeLayout exit_app_but, image_lay;
    ProgressBar prgressbar;
    private String ImagePath = "",IdentiImagePath="";
    MySession mySession;
    private TextView save_profile,status_tv;
    private String image_url = "", user_log_data = "",click_sts="";
    String user_id = "", country_str = "", password_str = "", firebase_regid = "";
    GPSTracker gpsTracker;
    private double longitude = 0.0, latitude = 0.0;
    Spinner country_spn, language_spn;
    ArrayList<String> language_list;
    ACProgressCustom ac_dialog;
    Toolbar toolbar;
    ArrayList<CountryBean> countryBeanArrayList,statelistbean,citylistbean;
    CountryListAdapter countryListAdapter;
    private String language = "";
    MyLanguageSession myLanguageSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_profile);
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

        if (NotificationUtils.r != null && NotificationUtils.r.isPlaying()) {
            NotificationUtils.r.stop();
        }

        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        language_list = new ArrayList<>();
        language_list.add(getResources().getString(R.string.language));
        language_list.add("English");
        language_list.add("Other");

        Log.e("user_log_data", "" + user_log_data);

        if (user_log_data == null) {

        } else {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");

                    Log.e("user_id >>>>", "" + user_id);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            idinit();
            checkGps();
            clickevetn();
            new GetCountryList().execute();
            new GetUserProfile().execute();
        }
    }



    private void checkGps() {
        gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {

            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            Log.e("Login Latitude", "" + latitude);
            Log.e("Login longitude", "" + longitude);
        } else {
            // if gps off get lat long from network
            //   locationfromnetwork();
            gpsTracker.showSettingsAlert();
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
    }

    private void clickevetn() {

        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();


            }
        });
        identy_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_sts ="identify";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), 3);
            }
        });
        image_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                click_sts ="profile";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), 1);

            }
        });
        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstname_str = firstname_et.getText().toString();
                lastname_str = lastname_et.getText().toString();
                email_str = email_et.getText().toString();
                mobile_str = mobile_et.getText().toString();
                state_str = state_et.getText().toString();
                city_str = city_et.getText().toString();


                if (firstname_str.length() == 0) {
                    firstname_et.setError(getResources().getString(R.string.enterfirstname));
                    firstname_et.requestFocus();
                } else if (lastname_str.length() == 0) {
                    lastname_et.setError(getResources().getString(R.string.enterlastname));
                    lastname_et.requestFocus();
                } else if (email_str.length() == 0) {
                    email_et.setError(getResources().getString(R.string.emteremail));
                    email_et.requestFocus();
                } else if (mobile_str.length() == 0) {
                    mobile_et.setError(getResources().getString(R.string.entermobile));
                    mobile_et.requestFocus();
                } else if (country_str==null||country_str.equalsIgnoreCase("")) {
                    Toast.makeText(ProfileAct.this,getResources().getString(R.string.select_country),Toast.LENGTH_LONG).show();
                }
                else if (state_str==null||state_str.equalsIgnoreCase("")) {
                    state_et.setError(getResources().getString(R.string.enter_state));
                    state_et.requestFocus();
                }else if (city_str==null||city_str.equalsIgnoreCase("")) {
                    city_et.setError(getResources().getString(R.string.entercity));
                    city_et.requestFocus();
                }
                else {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                    firebase_regid = pref.getString("regId", null);
                    Log.e("Login Activity >>", "Firebase reg id: " + firebase_regid);
                    new JsonUpdateProfile().execute();


                }
            }
        });
    }

    private void idinit() {
        country_spn = findViewById(R.id.country_spn);
        status_tv = (TextView) findViewById(R.id.status_tv);
        save_profile = (TextView) findViewById(R.id.save_profile);
        state_et = findViewById(R.id.state_et);
        city_et = findViewById(R.id.city_et);
        prgressbar = (ProgressBar) findViewById(R.id.prgressbar);
        user_img = (CircleImageView) findViewById(R.id.user_img);
        identy_img = (ImageView) findViewById(R.id.identy_img);
        firstname_et = (EditText) findViewById(R.id.firstname_et);
        lastname_et = (EditText) findViewById(R.id.lastname_et);
        email_et =  findViewById(R.id.email_et);
        mobile_et =  findViewById(R.id.mobile_et);

        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);
        image_lay = (RelativeLayout) findViewById(R.id.image_lay);


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


    public class JsonUpdateProfile extends AsyncTask<String, String, String> {

        String Jsondata;
        private boolean checkdata = false;

        protected void onPreExecute() {
            try {
                super.onPreExecute();
                //  prgressbar.setVisibility(View.VISIBLE);
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
            String requestURL = BaseUrl.baseurl + "update_profile?";
            try {
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addFormField("user_id", user_id);
                multipart.addFormField("first_name", firstname_str);
                multipart.addFormField("last_name", lastname_str);
                multipart.addFormField("email", email_str);
                multipart.addFormField("mobile", mobile_str);
                multipart.addFormField("place", place_str);
                multipart.addFormField("lang", "en");
                multipart.addFormField("currency", currency_str);
                multipart.addFormField("country", country_str);
                multipart.addFormField("state", state_str);
                multipart.addFormField("city", city_str);
                multipart.addFormField("lat", "" + latitude);
                multipart.addFormField("lon", "" + longitude);
                multipart.addFormField("register_id", firebase_regid);
                multipart.addFormField("type", "USER");

                if (ImagePath.equalsIgnoreCase("")) {

                } else {
                    File ImageFile = new File(ImagePath);
                    multipart.addFilePart("image", ImageFile);
                } if (IdentiImagePath.equalsIgnoreCase("")) {

                } else {
                    File ImageFile = new File(IdentiImagePath);
                    multipart.addFilePart("document", ImageFile);
                }

                List<String> response = multipart.finish();

                for (String line : response) {


                    Jsondata = line;
                    Log.e("Update Response ====", Jsondata);

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
            //   prgressbar.setVisibility(View.GONE);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                mySession.setlogindata(result);
                mySession.signinusers(true);
                finish();

            }

        }


    }

    private class GetUserProfile extends AsyncTask<String, String, String> {


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
//http://technorizen.com/WORKSPACE1/shipper/webservice/get_user?user_id=61
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_profile?";
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
                Log.e("Json Login Response", ">>>>>>>>>>>>" + response);
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
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        firstname_et.setText("" + jsonObject1.getString("first_name"));
                        lastname_et.setText("" + jsonObject1.getString("last_name"));
                        email_et.setText("" + jsonObject1.getString("email"));


                        email_str = jsonObject1.getString("email");
                        password_str = jsonObject1.getString("password");
                        image_url = jsonObject1.getString("image");
                        mobile_et.setText("" + jsonObject1.getString("mobile"));
                        country_str = jsonObject1.getString("country");
                        state_et.setText("" + jsonObject1.getString("state"));
                        city_et.setText("" + jsonObject1.getString("city"));

                        /*phone_et.setText(""+jsonObject.getString("image"));*/
                        /*phone_et.setText(""+jsonObject.getString("id"));*/
                        if (countryBeanArrayList!=null&&!countryBeanArrayList.isEmpty()){
                            for (int i=0;i<countryBeanArrayList.size();i++){
                                if (country_str!=null&&!country_str.equalsIgnoreCase("")){
                                    if (country_str.equalsIgnoreCase(countryBeanArrayList.get(i).getCountry())){
                                        country_spn.setSelection(i);
                                    }
                                }
                            }

                        }


                        if (jsonObject1.getString("identity").equalsIgnoreCase("Verify")){
                            status_tv.setText(getResources().getString(R.string.identi));
                        }else {
                            if (jsonObject1.getString("document")==null||jsonObject1.getString("document").equalsIgnoreCase("")||jsonObject1.getString("document").equalsIgnoreCase(BaseUrl.image_baseurl))
                            {

                            }
                            else {
                                status_tv.setText(getResources().getString(R.string.identynotide));
                            }
                        }
                        if (jsonObject1.getString("document")==null||jsonObject1.getString("document").equalsIgnoreCase("")||jsonObject1.getString("document").equalsIgnoreCase(BaseUrl.image_baseurl))
                        {

                        }
                        else {
                            Picasso.with(ProfileAct.this).load(jsonObject1.getString("document")).into(identy_img);
                        }
                        if (image_url == null) {

                        } else if (image_url.equalsIgnoreCase(BaseUrl.image_baseurl)) {

                        } else if (image_url.equalsIgnoreCase("")) {

                        } else {

                            Picasso.with(ProfileAct.this).load(image_url).placeholder(R.drawable.profile_ic).into(user_img);
/*
                            Glide.with(ProfileAct.this)
                                    .load(image_url)
                                    .thumbnail(0.5f)
                                    .placeholder(R.drawable.profile_ic)
                                    .override(200, 200)
                                    .centerCrop()
                                    .crossFade()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .listener(new RequestListener<String, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                                            return false;
                                        }
                                    })
                                    .into(user_img);
*/

                        }


                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {


            switch (requestCode) {
                case 1:
                    Uri selectedImage = data.getData();
                    getPath(selectedImage);
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String FinalPath = cursor.getString(columnIndex);
                    cursor.close();
                    String ImagePath = getPath(selectedImage);
                    Log.e("PATH From Gallery", "" + FinalPath);
                    Log.e("PATH Get Gallery", "" + getPath(selectedImage));
                    decodeFile(ImagePath);
                    break;
                case 2:

                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    // File file = new File(photo);
                    //  save(file.getAbsolutePath());
                    ImagePath = saveToInternalStorage(photo);
                    Log.e("PATH Camera", "" + ImagePath);

                    //  avt_imag.setImageBitmap(photo);
                    break;

                case 3:
                    Uri selectedImage3 = data.getData();
                    getPath(selectedImage3);
                    String[] filePathColumn3 = {MediaStore.Images.Media.DATA};
                    Cursor cursor3 = getContentResolver().query(selectedImage3, filePathColumn3, null, null, null);
                    cursor3.moveToFirst();
                    int columnIndex3 = cursor3.getColumnIndex(filePathColumn3[0]);
                    String FinalPath3 = cursor3.getString(columnIndex3);
                    cursor3.close();
                    String ImagePath3 = getPath(selectedImage3);
                    Log.e("PATH From Gallery", "" + FinalPath3);
                    Log.e("PATH Get Gallery", "" + getPath(selectedImage3));
                    decodeFiles(ImagePath3);
                    break;
            }
        }
    }

    public String getPath(Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        if (cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            //  Log.e("image_path.===..", "" + path);
        }
        cursor.close();
        return path;
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        String dateToStr = format.format(today);
        ContextWrapper cw = new ContextWrapper(ProfileAct.this);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, "profile_" + dateToStr + ".JPEG");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }


    public void decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);
        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;
        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, o2);
        ImagePath = saveToInternalStorage(bitmap);
        Log.e("DECODE PATH PROF", "ff " + ImagePath);
        user_img.setImageBitmap(bitmap);



    }

    public void decodeFiles(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);
        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;
        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, o2);
        IdentiImagePath = saveToInternalStorage(bitmap);
        Log.e("DECODE PATH IDT", "ff " + IdentiImagePath);
        identy_img.setImageBitmap(bitmap);

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
            //http://www.masar.taxi/webservice/countrys
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

                        countryListAdapter = new CountryListAdapter(ProfileAct.this, countryBeanArrayList);
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
            view = inflter.inflate(R.layout.spinner_lay_profile, null);

            TextView names = (TextView) view.findViewById(R.id.pname);
            //  TextView countryname = (TextView) view.findViewById(R.id.countryname);


            names.setText(values.get(i).getCountry());


            return view;
        }
    }


}
