package main.com.iglobuser.paymentclasses;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

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

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.iglobuser.R;
import main.com.iglobuser.constant.ACProgressCustom;
import main.com.iglobuser.constant.BaseUrl;
import main.com.iglobuser.constant.MyLanguageSession;
import main.com.iglobuser.constant.MySession;
import main.com.iglobuser.draweractivity.BaseActivity;

public class SaveCardDetail extends AppCompatActivity {
    EditText namecard, edt_cardnumber, expiry_date, year, security_code, postalcode;
    private Button sending;
    String strnamecard = "", cardnumber = "", strexpiry_date = "", cvv_number = "", stryear = "";
    ACProgressCustom ac_dialog;
    private RelativeLayout exit_app_but;
    private MySession mySession;
    private String user_log_data = "", user_id = "", card_id = "", token_str = "";
    private TextView removecard;
    private CreditCardFormatTextWatcher tv;
    boolean cardnumber_bool = false;
    boolean cvv_bool = false;
    boolean expmonth_bool = false;
    boolean expyear_bool = false;
    private String language = "",customer_id="";
    MyLanguageSession myLanguageSession;
    private ProgressBar progress_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());


        setContentView(R.layout.activity_save_card_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (myLanguageSession.getLanguage().equalsIgnoreCase("ar")) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

        }
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
                    Log.e("user_id >>>>", "" + user_id);
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

        idinti();
        clickevent();
      //  new GetCardDetail().execute();
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
        new GetUserProfile().execute();
    }

    private void clickevent() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        removecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RemooveCardDetail().execute();
            }
        });
        sending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strnamecard = namecard.getText().toString().trim();
                cardnumber = edt_cardnumber.getText().toString().trim();
                strexpiry_date = expiry_date.getText().toString().trim();
                stryear = year.getText().toString().trim();
                cvv_number = security_code.getText().toString().trim();
                //Validate();


                if (strnamecard == null || strnamecard.equals("")) {
                    namecard.setError(getResources().getString(R.string.cardnameempty));
                }
                if (cardnumber == null || cardnumber.equals("")) {
                    edt_cardnumber.setError(getResources().getString(R.string.cardnumnotempty));
                }
                if (strexpiry_date == null || strexpiry_date.equals("")) {
                    expiry_date.setError(getResources().getString(R.string.expdatenotempty));
                }
                if (stryear == null || stryear.equals("")) {
                    year.setError(getResources().getString(R.string.yearnotempty));
                }
                if (cvv_number == null || cvv_number.equals("")) {
                    security_code.setError(getResources().getString(R.string.securitynotempety));
                } else {

                    int month = Integer.parseInt(strexpiry_date);
                    int year_int = Integer.parseInt(stryear);

                    onClickSomething(cardnumber, month, year_int, cvv_number);

                    if (cardnumber_bool && cvv_bool && expmonth_bool && expyear_bool) {
                        //Toast.makeText(SaveCardDetail.this,"In working...", Toast.LENGTH_LONG).show();
                        progress_bar.setVisibility(View.VISIBLE);
                          Card card = new Card(cardnumber, month, year_int, cvv_number);  // pk_test_2khGozRubEhBZxFXj3TnxrkO
                        card.setCurrency("usd");
                        card.setName(strnamecard);
                        Stripe stripe = new Stripe(SaveCardDetail.this, BaseUrl.stripe_publish);
                        //Stripe stripe = new Stripe(SaveCardDetail.this, "pk_test_tuLF7lx5bPZpsfwM4OzqE0HJ");  //pk_test_DpAdEXE4slfMy2FR7vsSj0ya

                        stripe.createToken(
                                card,
                                new TokenCallback() {
                                    public void onSuccess(Token token) {
                                        // Send token to your server
                                        Log.e("Token >>", ">> " + token.getId());
                                        token_str = token.getId();
                                        progress_bar.setVisibility(View.GONE);
                                        if (customer_id == null || customer_id.equalsIgnoreCase("") || customer_id.equalsIgnoreCase("null")) {
                                            new AddCardDetail().execute();
                                        } else {
                                            new SavedCardAsc().execute();
                                        }


                                    }

                                    public void onError(Exception error) {
                                        // Show localized error message
                                        progress_bar.setVisibility(View.GONE);
                                        Toast.makeText(SaveCardDetail.this, "\n" + "The expiration year or the security code of your card is not valid",
                                                Toast.LENGTH_LONG
                                        ).show();
                                        Log.e("WRONG CARD ERROR", " >> " + error.toString());
                                        System.out.println("Eeeeeeeeeeeeeeerrrrr" + error.toString());

                                    }
                                });
                    } else if (!cardnumber_bool) {
                        Toast.makeText(SaveCardDetail.this, getResources().getString(R.string.cardnumberwrong), Toast.LENGTH_LONG).show();
                    } else if (!cvv_bool) {
                        Toast.makeText(SaveCardDetail.this, getResources().getString(R.string.cvvwrong), Toast.LENGTH_LONG).show();

                    } else if (!expyear_bool) {
                        Toast.makeText(SaveCardDetail.this, getResources().getString(R.string.wrongexpyear), Toast.LENGTH_LONG).show();

                    } else if (!expmonth_bool) {
                        Toast.makeText(SaveCardDetail.this, getResources().getString(R.string.wrongexpmonth), Toast.LENGTH_LONG).show();

                    }


                }


            }
        });
    }

    private class SavedCardAsc extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  progressBar.setVisibility(View.VISIBLE);
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
//http://technorizen.com/transport/webservice/add_card?cus_id=&source=
                String postReceiverUrl = BaseUrl.baseurl + "add_card?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("cus_id", customer_id);
                params.put("source", token_str);

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
            Log.e("Saved Card Res", ">>>>>>>>>>>>" + result);
            // progressBar.setVisibility(View.GONE);
            if (ac_dialog != null) {
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {

                        Log.e("Saved Card Data >> ", " >> " + result);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        BaseActivity.card_base_id = jsonObject1.getString("id");
                        finish();

                        Toast.makeText(SaveCardDetail.this, getResources().getString(R.string.cardaddedsuc), Toast.LENGTH_LONG).show();


                    } else {
                        Toast.makeText(SaveCardDetail.this, getResources().getString(R.string.somethingwrong), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        }
    }


    private class GetUserProfile extends AsyncTask<String, String, String> {
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

                        customer_id = jsonObject1.getString("cust_id");


                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    public void onClickSomething(String cardNumber, int cardExpMonth, int cardExpYear, String cardCVC) {
        Card card = new Card(cardNumber, cardExpMonth, cardExpYear, cardCVC);
        card.validateNumber();
        card.validateCVC();
        card.validateExpMonth();
        card.validateExpYear();
        if (card.validateNumber()) {
            cardnumber_bool = true;
        } else {
            cardnumber_bool = false;
        }
        if (card.validateCVC()) {
            cvv_bool = true;
        } else {
            cvv_bool = false;
        }
        if (card.validateExpYear()) {
            expyear_bool = true;
        } else {
            expyear_bool = false;
        }
        if (card.validateExpMonth()) {
            expmonth_bool = true;
        } else {
            expmonth_bool = false;
        }
    }

    private void idinti() {
        progress_bar = findViewById(R.id.progress_bar);
        removecard = findViewById(R.id.removecard);
        exit_app_but = findViewById(R.id.exit_app_but);
        namecard = (EditText) findViewById(R.id.namecard);
        edt_cardnumber = (EditText) findViewById(R.id.edt_cardnumber);
        expiry_date = (EditText) findViewById(R.id.expiry_date);
        year = (EditText) findViewById(R.id.year);
        security_code = (EditText) findViewById(R.id.security_code);
        postalcode = (EditText) findViewById(R.id.postalcode);

        sending = (Button) findViewById(R.id.sending_payment);
        tv = new CreditCardFormatTextWatcher(edt_cardnumber);
        edt_cardnumber.addTextChangedListener(tv);

    }


    private class AddCardDetail extends AsyncTask<String, String, String> {
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
            //http://hitchride.net/webservice/save_card?user_id=1&holder_name=shyam&card_number=1236545212545633&expiry_month=04&expiry_year=%202019
            try {
                Log.e("token_str ", " >>" + token_str);
                String postReceiverUrl = BaseUrl.baseurl + "save_card?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("user_id", user_id);
                params.put("source", token_str);
                params.put("description", "");
                params.put("email", "" + "");


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
                Log.e("Add Card", ">>>>>>>>>>>>" + response);
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
                    if (jsonObject.getString("status").equalsIgnoreCase("1")){
                        Toast.makeText(SaveCardDetail.this, getResources().getString(R.string.yourcarddetailsaved), Toast.LENGTH_LONG).show();

                        JSONObject jsonObject2 = jsonObject.getJSONObject("result");
                        BaseActivity.card_base_id = jsonObject2.getString("default_source");
                        finish();
                    }
                    else {
                        Toast.makeText(SaveCardDetail.this, getResources().getString(R.string.somethingwrong), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        }
    }





    private class RemooveCardDetail extends AsyncTask<String, String, String> {
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
//http://hitchride.net/webservice/delete_card?card_id=1
            try {
                String postReceiverUrl = BaseUrl.baseurl + "delete_card?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("cus_id", strings[0]);
                params.put("card_id", card_id);

                Log.e("Customer Card", ">>>>>>>>>>>>" + strings[0]);
                Log.e("Customer card_id", ">>>>>>>>>>>>" + strings[1]);

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
                Log.e("Delete Card", ">>>>>>>>>>>>" + response);
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
                BaseActivity.Card_Added_Sts = "";
                Toast.makeText(SaveCardDetail.this, getResources().getString(R.string.cardremoved), Toast.LENGTH_LONG).show();

                finish();
            }


        }
    }


}
