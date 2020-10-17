package main.com.iglobuser.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cc.cloudist.acplibrary.ACProgressConstant;
import de.hdodenhof.circleimageview.CircleImageView;
import main.com.iglobuser.R;
import main.com.iglobuser.app.Config;
import main.com.iglobuser.constant.ACProgressCustom;
import main.com.iglobuser.constant.BaseUrl;
import main.com.iglobuser.constant.ConverSession;
import main.com.iglobuser.constant.MultipartUtility;
import main.com.iglobuser.constant.MyLanguageSession;
import main.com.iglobuser.constant.MySession;
import main.com.iglobuser.utils.NotificationUtils;

public class SupportAct extends AppCompatActivity {
    private RelativeLayout exit_app_but;
    private ListView chatlist;
    private TextView send_tv, titletext;
    private ArrayList<ConverSession> converSessionArrayList;
    MySession mySession;
    String messagetext = "", ImagePath = "";
    EditText message_et;
    ScheduledExecutorService scheduleTaskExecutor;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ConversessionAdapter conversessionAdapter;
    public String image_url = "", block_status = "", firstname_str = "", lastname_str = "", date_time = "", user_log_data = "", time_zone = "", request_id = "", user_id = "", receiver_id = "", receiver_img = "", receiver_name = "";
    int beforelength = 0;
    ImageView camera_img;
    private boolean prosts = false;
    ProgressBar prgressbar;
    public static boolean isInFront = false;
    private RelativeLayout bottumlay, optionmenu;
    private CircleImageView chatuser_img;
    private Toolbar toolbar;
    ACProgressCustom ac_dialog;
    private String language = "";
    MyLanguageSession myLanguageSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());


        setContentView(R.layout.activity_support);
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
        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone();
        time_zone = tz.getID();
        user_log_data = mySession.getKeyAlldata();
        if (user_log_data == null) {
        } else {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");
                    image_url = jsonObject1.getString("image");
                    firstname_str = jsonObject1.getString("first_name");
                    lastname_str = jsonObject1.getString("last_name");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            receiver_id = bundle.getString("receiver_id");
            receiver_name = bundle.getString("receiver_name");
            receiver_img = bundle.getString("receiver_img");
            request_id = bundle.getString("request_id");
            block_status = bundle.getString("block_status");
            Log.e("block_status chat>>"," >"+block_status);

        }
        idinit();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null) {
            throw new Error("Can't find tool bar, did you forget to add it in Activity layout file?");
        }

        setSupportActionBar(toolbar);
        clickevent();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Log.e("Push Chat: ", "" + message);
                    JSONObject data = null;
                    try {
                        data = new JSONObject(message);
                        String keyMessage = data.getString("key").trim();
                        if (keyMessage.equalsIgnoreCase("You have a new message")) {
                            Log.e("Push Chat Come: ", "True");

                            new MyConverSession().execute();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        // new MyConverSession().execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        scheduleTaskExecutor.shutdown();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);

        scheduleTaskExecutor.shutdown();
        isInFront = false;
    }

    @Override
    public void onResume() {
        isInFront = true;
        super.onResume();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());

        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                new MyConverSession().execute();
            }
        }, 0, 7, TimeUnit.SECONDS);
    }

    private void clickevent() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        send_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                messagetext = message_et.getText().toString();
                if (messagetext == null || messagetext.equalsIgnoreCase("")) {
                    Toast.makeText(SupportAct.this,getResources().getString(R.string.typemessage),Toast.LENGTH_LONG).show();

                } else {
                    Date today = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    date_time = format.format(today);
                    System.out.println("CURRENT " + date_time);
                    prosts = false;
                    new SendMessage().execute();
                }

            }
        });
        camera_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagetext = message_et.getText().toString();
                if (messagetext == null || messagetext.equalsIgnoreCase("")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
                } else {
                    // Toast.makeText(ChatingAct.this, getResources().getString(R.string.pleassend), Toast.LENGTH_LONG).show();
                }

            }
        });

    }




    private void idinit() {

        bottumlay = (RelativeLayout) findViewById(R.id.bottumlay);

        chatuser_img = (CircleImageView) findViewById(R.id.chatuser_img);
        prgressbar = (ProgressBar) findViewById(R.id.prgressbar);
        camera_img = (ImageView) findViewById(R.id.camera_img);
        message_et = (EditText) findViewById(R.id.message_et);
        send_tv = (TextView) findViewById(R.id.send_tv);
        titletext = (TextView) findViewById(R.id.titletext);
        chatlist = (ListView) findViewById(R.id.chatlist);
        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);
      //  titletext.setText("" + receiver_name);
        if (receiver_img != null && !receiver_img.equalsIgnoreCase("")) {
            Picasso.with(SupportAct.this).load(receiver_img).into(chatuser_img);
        }

    }

    public class ConversessionAdapter extends BaseAdapter {
        String[] result;
        Context context;
        private ArrayList<ConverSession> converSessionArrayList;
        private LayoutInflater inflater = null;

        public ConversessionAdapter(SupportAct chatActivity, ArrayList<ConverSession> converSessionArrayList) {
            // TODO Auto-generated constructor stub
            context = chatActivity;
            this.converSessionArrayList = converSessionArrayList;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return converSessionArrayList.size();
        }

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
            TextView receivermessage, sendermessage;
            ImageView prof_mess_img;
            LinearLayout layout1, layout2;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
           View rowView = inflater.inflate(R.layout.chat_item_newlay, null);
            ConverSession message = converSessionArrayList.get(position);
            RelativeLayout layout_left=rowView.findViewById(R.id.layout_left);
            RelativeLayout layout_right=rowView.findViewById(R.id.layout_right);
            CircleImageView img_user_left=rowView.findViewById(R.id.img_user_left);
            CircleImageView img_user_right=rowView.findViewById(R.id.img_user_right);
            TextView tv_left_msg=rowView.findViewById(R.id.tv_left_msg);
            TextView tv_right_msg=rowView.findViewById(R.id.tv_right_msg);
            TextView tv_left_date=rowView.findViewById(R.id.tv_left_date);
            TextView tv_right_date=rowView.findViewById(R.id.tv_right_date);

            if (user_id.equalsIgnoreCase(message.getSenderid())) {
                layout_left.setVisibility(View.GONE);
                layout_right.setVisibility(View.VISIBLE);
                Picasso.with(context).load(message.getUserimg()).placeholder(R.drawable.user).into(img_user_right);
                tv_right_msg.setText(message.getMessage());
                tv_right_date.setText(message.getDatetime());
            }else {
                layout_left.setVisibility(View.VISIBLE);
                layout_right.setVisibility(View.GONE);
                Picasso.with(context).load(message.getSenderimg()).placeholder(R.drawable.user).into(img_user_left);
                tv_left_msg.setText(message.getMessage());
                tv_left_date.setText(message.getDatetime());
            }
            return rowView;
        }

    }

    public class SendMessage extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (prosts) {
                prgressbar.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/insert_chat?sender_id=1&receiver_id=2&chat_message=hello
            String charset = "UTF-8";
            String requestURL = BaseUrl.baseurl + "insert_chat";
            Log.e("requestURL >>", requestURL);

            try {
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                Log.e("SENDER ", "ID" + user_id);

                multipart.addFormField("sender_id", user_id);
                multipart.addFormField("receiver_id", "483");
                multipart.addFormField("chat_message", messagetext);
                multipart.addFormField("request_id", "");
                multipart.addFormField("timezone", time_zone);
                multipart.addFormField("date_time", date_time);
                multipart.addFormField("type", "Support");
                Log.e("request_id >>", "  ll ss" + request_id);
                if (ImagePath.equalsIgnoreCase("")) {

                } else {
                    File ImageFile = new File(ImagePath);
                    multipart.addFilePart("chat_image", ImageFile);
                }

                List<String> response = multipart.finish();


                String Jsondata = "";

                for (String line : response) {


                    Jsondata = line;
                    Log.e("Send msg Response ====", Jsondata);

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
        protected void onPostExecute(String lenghtOfFile) {

            if (lenghtOfFile==null){
                messagetext = "";
                message_et.setText("");
                ImagePath = "";
                prgressbar.setVisibility(View.GONE);
                new MyConverSession().execute();
            }
            else if (lenghtOfFile.equalsIgnoreCase("")){
                messagetext = "";
                message_et.setText("");
                ImagePath = "";
                prgressbar.setVisibility(View.GONE);
                new MyConverSession().execute();
            }
            else {
                try {
                    JSONObject jsonObject = new JSONObject(lenghtOfFile);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")){
                        messagetext = "";
                        message_et.setText("");
                        ImagePath = "";
                        prgressbar.setVisibility(View.GONE);
                        new MyConverSession().execute();
                    }
                    else if (jsonObject.getString("status").equalsIgnoreCase("0")){
                        if (jsonObject.getString("result").equalsIgnoreCase("blocked user unblock first")){
                            // Toast.makeText(ChatingAct.this,getResources().getString(R.string.donotmessagesend),Toast.LENGTH_LONG).show();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }


    }

    private class MyConverSession extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            converSessionArrayList = new ArrayList<>();
            super.onPreExecute();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://mobileappdevelop.co/NAXCAN/webservice/get_chat_detail?sender_id=1&receiver_id=2&chat_message=hello%20mil%20gyi%20ws
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_chat_detail?";
                Log.e("GET CHAT DETAIL",">>"+postReceiverUrl+"sender_id="+receiver_id+"&receiver_id="+user_id+"&request_id="+request_id);
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                Log.e("request_id >>", "  ll " + request_id);
                params.put("sender_id", "483");
                params.put("receiver_id", user_id);
                params.put("request_id", "");
                params.put("type", "Support");
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
            Log.e("Chat MEssages >>", "" + result);
            if (result == null) {

            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int jsonlenth = 0;
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        jsonlenth = jsonArray.length();
                        converSessionArrayList = new ArrayList<>();
                        if (beforelength < jsonlenth || beforelength > jsonlenth) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                String results = jsonObject2.getString("result");

                                if (results.equalsIgnoreCase("successful")) {
                                    ConverSession conversession = new ConverSession();
                                    conversession.setId(jsonObject2.getString("id"));
                                    conversession.setSenderid(jsonObject2.getString("sender_id"));
                                    conversession.setMessage(jsonObject2.getString("chat_message"));
                                    conversession.setChat_image(jsonObject2.getString("chat_image"));
                                    // conversession.setDatetime(jsonObject2.getString("date_time"));

                                    Date date1 = null;
                                    try {
                                        date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(jsonObject2.getString("date_time"));
                                        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy hh:mm aa", Locale.ENGLISH);
                                        String strDate = formatter.format(date1);
                                        conversession.setDatetime(strDate);

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    // conversession.setTime(jsonObject2.getString("time"));
                                    converSessionArrayList.add(conversession);

                                }
                            }

                        }

                    }
                    if (converSessionArrayList != null || !converSessionArrayList.isEmpty()) {
                        if (jsonlenth > beforelength) {
                            // Collections.reverse(converSessionArrayList);
                            conversessionAdapter = new ConversessionAdapter(SupportAct.this, converSessionArrayList);
                            chatlist.setAdapter(conversessionAdapter);
                            chatlist.setSelection(converSessionArrayList.size() - 1);
                            conversessionAdapter.notifyDataSetChanged();
                            beforelength = jsonlenth;
                        }

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
        ContextWrapper cw = new ContextWrapper(SupportAct.this);
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
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        date_time = format.format(today);
        System.out.println("CURRENT " + date_time);
        prosts = true;
        new SendMessage().execute();


    }

    private class UnblockUser extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // prgressbar.setVisibility(View.VISIBLE);
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
//http://mobileappdevelop.co/NAXCAN/webservice/block_user?user_id=1&block_id=2&block_reasion=
            try {
                String postReceiverUrl = BaseUrl.baseurl + "block_user?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                Log.e("block_id >"," > "+receiver_id);
                Log.e("user_id >"," > "+user_id);
                params.put("user_id", user_id);
                params.put("block_id", receiver_id);
                params.put("block_reasion", "");

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
                Log.e("Block Response", ">>>>>>>>>>>>" + response);
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
            Log.e("Block User Res", " >" + result);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }
            block_status ="not_block";
            invalidateOptionsMenu();

            //finish();


        }
    }

}
