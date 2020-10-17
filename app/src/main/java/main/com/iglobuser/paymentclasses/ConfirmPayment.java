package main.com.iglobuser.paymentclasses;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.model.Card;
import com.stripe.android.view.CardInputWidget;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.iglobuser.MainActivity;
import main.com.iglobuser.R;
import main.com.iglobuser.constant.ACProgressCustom;
import main.com.iglobuser.constant.BaseUrl;
import main.com.iglobuser.constant.MyLanguageSession;
import main.com.iglobuser.constant.MySession;

public class ConfirmPayment extends AppCompatActivity {
    Button sending,package_money;
    ProgressDialog progressDialog;
    String str_token="",pack_price="",pack_name="",pack_id="",user_log_data="",user_id="";
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    TextView demoplus_name;
    CardInputWidget mCardInputWidget;
    Dialog dialog;

    int month, year_int;
    private String token_id;
    private MySession mySession;
    private RelativeLayout exit_app_but;
    String request_id="",transaction_type="",car_charge_str="",rating_str="",tips_amount_str="0",time_zone="",comment_str="";
    private CreditCardFormatTextWatcher tv;
    /**/
    ACProgressCustom ac_dialog;
    private String language = "";
    MyLanguageSession myLanguageSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());


        setContentView(R.layout.activity_confirm_payment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (myLanguageSession.getLanguage().equalsIgnoreCase("ar")) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

        }


        progressDialog = new ProgressDialog(this);
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
      Bundle  b = getIntent().getExtras();
        if (b != null&&!b.isEmpty()) {
            pack_price = b.getString("amount_str");
            request_id = b.getString("request_id");
            car_charge_str = b.getString("car_charge_str");
            rating_str = b.getString("rating");
            tips_amount_str = b.getString("tips_amount_str");
            time_zone = b.getString("time_zone");
            comment_str = b.getString("comment_str");
            transaction_type = b.getString("transaction_type");

            Log.d("payment", pack_price);
            System.out.println("payment" + pack_price);
        }else
        {
            Toast.makeText(this,"payment is null",Toast.LENGTH_SHORT).show();
        }

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();
        //   mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);

        package_money = (Button) findViewById(R.id.package_money);

        demoplus_name = (TextView) findViewById(R.id.demoplus_name);
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sending = (Button) findViewById(R.id.sending_payment);

        dialog = new Dialog(ConfirmPayment.this);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        sending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                  /*  showDialog();
                    month = Integer.parseInt(strexpiry_date);
                    year_int = Integer.parseInt(stryear);

                    onClickSomething(cardnumber, month, year_int, cvv_number);
                    mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
                    Card card = new Card(cardnumber, month, year_int, cvv_number);  // pk_test_2khGozRubEhBZxFXj3TnxrkO
                    Stripe stripe = new Stripe(ConfirmPayment.this, BaseUrl.stripe_publish);  //pk_test_DpAdEXE4slfMy2FR7vsSj0ya
                   // Stripe stripe = new Stripe(ConfirmPayment.this, "pk_test_tuLF7lx5bPZpsfwM4OzqE0HJ");  //pk_test_DpAdEXE4slfMy2FR7vsSj0ya
                    stripe.createToken(
                            card,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your server
                                    System.out.println("----------------Token" + token.getId());
                                    hideDialog();
                                    token_id = token.getId();
                                    paymentwithcard();

                             }
                                public void onError(Exception error) {
                                    // Show localized error message
                                    Toast.makeText(ConfirmPayment.this, "\n" + "The expiration year or the security code of your card is not valid",
                                            Toast.LENGTH_LONG
                                    ).show();
                                    System.out.println("Eeeeeeeeeeeeeeerrrrr" + error.toString());
                                    hideDialog();
                                }
                            });*/


            }
        });

        demoplus_name.setText(pack_name);

    }

    private void paymentwithcard() {
        // Tag used to cancel the request
        if(Utils.isConnected(getApplicationContext())){
            Paymentjsontask task = new Paymentjsontask();
            task.execute();

        }

        else{
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.checknetworkconnection), Toast.LENGTH_SHORT).show();
        }
    }
    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();

    }
    public void onClickSomething(String cardNumber, int cardExpMonth, int cardExpYear, String cardCVC) {
        Card card = new Card(cardNumber, cardExpMonth, cardExpYear, cardCVC);
        card.validateNumber();
        card.validateCVC();
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

    public class Paymentjsontask extends AsyncTask<String, Void, String> {
        boolean iserror = false;
        String result = "";
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            String cancel_req_tag = "paymentin";
        progressDialog.setMessage("Payment you in...");
        showDialog();
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(String... params) {
            //HttpClient client = new DefaultHttpClient();


            HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            DefaultHttpClient client = new DefaultHttpClient();
            SchemeRegistry registry = new SchemeRegistry();
            SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
            socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
            registry.register(new Scheme("https", socketFactory, 443));
            SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
            DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
            HttpPost post = new HttpPost(BaseUrl.baseurl+"strips_payment?transaction_type="+transaction_type+"&payment_type=Card&currency=USD&amount="+pack_price+"&user_id="+user_id+"&token="+token_id+"&request_id="+request_id+"&rating="+rating_str+"&review="+comment_str+"&car_charge="+car_charge_str+"&tip="+tips_amount_str+"&time_zone="+time_zone);
Log.e("STRIPE URL >> "," >> "+BaseUrl.baseurl+"strips_payment?transaction_type="+transaction_type+"&payment_type=Card&currency=USD&amount="+pack_price+"&user_id="+user_id+"&token="+token_id+"&request_id="+request_id+"&rating="+rating_str+"&review="+comment_str+"&car_charge="+car_charge_str+"&tip="+tips_amount_str);
            //http://hitchride.net/webservice/strips_payment?transaction_type=ride_payment&payment_type=Card&amount=200&user_id=117&request_id=164&tip=5&car_charge=12&token=123&currency=USD&rating=4&review=nice%20job
            try {
                HttpResponse response = client.execute(post);
                String object = EntityUtils.toString(response.getEntity());
                System.out.println("#####object=" + object);
                //JSONArray js = new JSONArray(object);
                JSONObject jobect1 = new JSONObject(object);
                result = jobect1.getString("message");
                if(result.equalsIgnoreCase("payment successfull")){
                    String details = jobect1.getString("result");

                }else{

                }
            }

            catch (Exception e) {
                Log.v("22", "22" + e.getMessage());
                e.printStackTrace();
                iserror = true;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result1) {
            // TODO Auto-generated method stub
            super.onPostExecute(result1);
            hideDialog();
            if(iserror== false){
                if (result.equalsIgnoreCase("payment successfull")){

                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.paymentaddsucc),Toast.LENGTH_SHORT).show();
                    if (transaction_type.equalsIgnoreCase("ride_payment")){
                        Intent i = new Intent(ConfirmPayment.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(i);
                    }
                    else {
                        finish();
                    }



                } else {
                    Toast.makeText(ConfirmPayment.this,getResources().getString(R.string.cardinfowrong),Toast.LENGTH_SHORT).show();

                }
            }else{
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.chkserver),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }



}
