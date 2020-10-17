package main.com.iglobuser.paymentclasses;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.iglobuser.R;
import main.com.iglobuser.constant.ACProgressCustom;
import main.com.iglobuser.constant.BaseUrl;
import main.com.iglobuser.constant.MySession;

public class MyCardsPayment extends AppCompatActivity {
    private ArrayList<CardBean> cardBeanArrayList;
    private CustomCardAdp customCardAdp;
    private ListView addedcardlist;
    private MySession mySession;
    private String user_log_data="",user_id="",cust_id="",amount_str="",type="",card_id="",time_zone="";
    private RelativeLayout addcard,backlay;
    private ACProgressCustom ac_dialog;
    private TextView addamount_tv,amount_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_cards);
        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone();
        time_zone = tz.getID();
        if (user_log_data == null) {

        } else {
            try {
                Log.e("user_log_data >>"," <<"+user_log_data);
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");
                    cust_id = jsonObject1.getString("cust_id");

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
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null&&!bundle.isEmpty()){
            amount_str = bundle.getString("amount_str");
        }
        idinit();
        clickevent();
    }

    private void clickevent() {
        addcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyCardsPayment.this,SaveCardDetail.class);
                startActivity(i);
            }
        });
        addamount_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (card_id==null||card_id.equalsIgnoreCase("")){
Toast.makeText(MyCardsPayment.this,getResources().getString(R.string.selectcard),Toast.LENGTH_LONG).show();
               }
               else {
                   if (amount_str==null||amount_str.equalsIgnoreCase("")){

                   }
                   else {
                       paymentwithcard();
                   }

               }
            }
        });
        backlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              finish();
            }
        });
    }
    private void paymentwithcard() {
        // Tag used to cancel the request
        if (Utils.isConnected(getApplicationContext())) {
            //new CreateCardCustomer().execute();
            new AddAmountWallet().execute();

        } else {
            Toast.makeText(getApplicationContext(), "Please Cheeck network conection..", Toast.LENGTH_SHORT).show();
        }
    }
    private class AddAmountWallet extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  progressBar.setVisibility(View.VISIBLE);
            if (ac_dialog!=null){
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
//http://technorizen.com/transport/webservice/strips_payment?transaction_type=&payment_type=&amount=&user_id=&request_id=&tip=&time_zone=&car_charge=&token=&currency=

                String postReceiverUrl = BaseUrl.baseurl + "strips_payment?";
                Log.e("postReceiverUrl >>"," >>"+postReceiverUrl+"transaction_type=add_to_wallet&amount="+amount_str+"&payment_type=Card&user_id="+user_id+"&time_zone="+time_zone+"&token="+card_id+"&currency=usd&customer="+cust_id);

                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("transaction_type", "add_to_wallet");
                params.put("amount", amount_str);
                params.put("payment_type", "Card");
                params.put("user_id", user_id);
                params.put("time_zone", time_zone);
                params.put("token", card_id);
                params.put("currency", "usd");
                params.put("customer", cust_id);
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
            Log.e("Add Wallet Amt", ">>>>>>>>>>>>" + result);
            // progressBar.setVisibility(View.GONE);
            if (ac_dialog!=null){
                ac_dialog.dismiss();
            }

            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                       // JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        //  WalletActivity.cust_id =  jsonObject1.getString("id");
                        // Log.e("customer_id >> ", " >> "+WalletActivity.cust_id);
                        Toast.makeText(MyCardsPayment.this, getResources().getString(R.string.amountaddedinyourwallet), Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else {
                        Toast.makeText(MyCardsPayment.this, getResources().getString(R.string.somethingwrong), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        }
    }

    private void idinit() {
        amount_tv = findViewById(R.id.amount_tv);
        amount_tv.setText("Amount : $"+amount_str);
        addamount_tv = findViewById(R.id.addamount_tv);
        backlay = findViewById(R.id.backlay);
        addedcardlist = findViewById(R.id.addedcardlist);
        addcard = findViewById(R.id.addcard);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new GetAddedCard().execute();
    }

    public class CustomCardAdp extends BaseAdapter {
        Context context;
        private LayoutInflater inflater = null;
        ArrayList<CardBean> cardBeanArrayList;

        public CustomCardAdp(Context contexts, ArrayList<CardBean> cardBeanArrayList) {
            this.context = contexts;
            this.cardBeanArrayList = cardBeanArrayList;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return cardBeanArrayList == null ? 0 : cardBeanArrayList.size();
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

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final Holder holder;
            holder = new Holder();
            View rowView;

            rowView = inflater.inflate(R.layout.custom_manual_card_lay, null);
            RadioButton creditcard_rbut = rowView.findViewById(R.id.creditcard_rbut);
            if (cardBeanArrayList.get(position).isDefaultCard()) {
                creditcard_rbut.setChecked(true);
                card_id = cardBeanArrayList.get(position).getId();


            } else {
                creditcard_rbut.setChecked(false);
            }
            TextView cardnumber = rowView.findViewById(R.id.cardnumber);

            TextView cardholdername = rowView.findViewById(R.id.cardholdername);
            TextView expiresdate = rowView.findViewById(R.id.expiresdate);

            String cardbrand = cardBeanArrayList.get(position).getBrand();
            String carnum = cardBeanArrayList.get(position).getLast4();
            cardholdername.setText("" + cardBeanArrayList.get(position).getCard_name());
            if (cardbrand.length() > 4) {
                cardbrand = cardbrand.substring(0, 4);
            }
            String stars = "**** ****";
            cardnumber.setText("" + cardbrand + " " + stars + " " + carnum);
            expiresdate.setText(getResources().getString(R.string.validtill) + " " + cardBeanArrayList.get(position).getSetfullexpyearmonth());

            creditcard_rbut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = position;
                    if (!cardBeanArrayList.get(position).isDefaultCard()) {
                        for (int k = 0; k < cardBeanArrayList.size(); k++) {
                            if (pos == k) {
                                if (cardBeanArrayList.get(k).isDefaultCard()) {
                                    cardBeanArrayList.get(k).setDefaultCard(false);
                                    card_id = "";

                                } else {
                                    card_id = cardBeanArrayList.get(position).getId();
                                   cardBeanArrayList.get(k).setDefaultCard(true);

                                }
                            } else {

                                cardBeanArrayList.get(k).setDefaultCard(false);
                            }
                        }
                        customCardAdp = new CustomCardAdp(MyCardsPayment.this, cardBeanArrayList);
                        addedcardlist.setAdapter(customCardAdp);
                        addedcardlist.setSelection(position);
                        customCardAdp.notifyDataSetChanged();
                    } else {
                        card_id = cardBeanArrayList.get(position).getId();
                        cardBeanArrayList.get(pos).setDefaultCard(true);
                        customCardAdp = new CustomCardAdp(MyCardsPayment.this, cardBeanArrayList);
                        addedcardlist.setAdapter(customCardAdp);
                        addedcardlist.setSelection(position);
                        customCardAdp.notifyDataSetChanged();
                    }


                    //  selected_service_fare.setVisibility(View.VISIBLE);

                }
            });


            // cardnumber.setText(""+getLastfour(cardBeanArrayList.get(position).getCard_number()));
            return rowView;
        }

    }


    private void sureDelete(final String customer, final String id) {
        final Dialog dialogSts = new Dialog(MyCardsPayment.this, R.style.DialogSlideAnim);
        dialogSts.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSts.setCancelable(false);
        dialogSts.setContentView(R.layout.custom_popup);
        dialogSts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView no_tv =  dialogSts.findViewById(R.id.no_tv);
        TextView yes_tv =  dialogSts.findViewById(R.id.yes_tv);

        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();
                new DeleteCardAsc().execute(customer,id);

            }
        });
        no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSts.dismiss();


            }
        });
        dialogSts.show();
    }

    private class DeleteCardAsc extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (ac_dialog!=null){
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
//http://technorizen.com/transport/webservice/delete_card?cus_id=&card_id=
            try {
                String postReceiverUrl = BaseUrl.baseurl + "delete_card?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                Log.e("Customer Card", ">>>>>>>>>>>>" + strings[0]);
                Log.e("Customer card_id", ">>>>>>>>>>>>" + strings[1]);
                params.put("cus_id", strings[0]);
                params.put("card_id", strings[1]);

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
            if (ac_dialog!=null){
                ac_dialog.dismiss();
            }
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        new GetAddedCard().execute();


                        //  new TransferAmount().execute(customer_id);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }
    private class GetAddedCard extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
if (ac_dialog!=null){
    ac_dialog.show();
}
            cardBeanArrayList = new ArrayList<>();
            try {
                super.onPreExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
//http://technorizen.com/transport/webservice/get_card?cus_id=cus_EGZqOg9nnZW25f
            try {
                String postReceiverUrl = BaseUrl.baseurl + "get_user_card?";
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
                Log.e("Get Added Cards", ">>>>>>>>>>>>" + response);
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
            if (ac_dialog!=null){
                ac_dialog.dismiss();
            }
            if (result == null) {
            } else if (result.isEmpty()) {

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                        JSONObject jsonObject2 = jsonObject1.getJSONObject("sources");
                        cust_id = jsonObject1.getString("id");
                        Log.e("customer_id >> ", " >> " + cust_id);

                        JSONArray jsonArray = jsonObject2.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject3 = jsonArray.getJSONObject(i);
                            CardBean cardBean = new CardBean();
                            cardBean.setId(jsonObject3.getString("id"));
                            cardBean.setLast4(jsonObject3.getString("last4"));
                            cardBean.setExp_month(jsonObject3.getString("exp_month"));
                            cardBean.setExp_year(jsonObject3.getString("exp_year"));
                            cardBean.setBrand(jsonObject3.getString("brand"));
                            cardBean.setFunding(jsonObject3.getString("funding"));
                            cardBean.setCustomer(jsonObject3.getString("customer"));
                            cardBean.setCard_name(jsonObject3.getString("name"));
                            String star = "************";
                            String cardlastfour = jsonObject3.getString("last4");

                            cardBean.setSetfullcardnumber(star + cardlastfour);
                            cardBean.setSetfullexpyearmonth(jsonObject3.getString("exp_month") + "/" + jsonObject3.getString("exp_year"));

                            cardBeanArrayList.add(cardBean);
                            addcard.setVisibility(View.GONE);
                        }
                        customCardAdp = new CustomCardAdp(MyCardsPayment.this,cardBeanArrayList);
                        addedcardlist.setAdapter(customCardAdp);
                        customCardAdp.notifyDataSetChanged();

                        //  new TransferAmount().execute(customer_id);


                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }


}
