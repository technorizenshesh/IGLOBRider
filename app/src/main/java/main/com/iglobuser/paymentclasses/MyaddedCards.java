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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import java.util.LinkedHashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import main.com.iglobuser.R;
import main.com.iglobuser.constant.ACProgressCustom;
import main.com.iglobuser.constant.BaseUrl;
import main.com.iglobuser.constant.MySession;

public class MyaddedCards extends AppCompatActivity {
    private ArrayList<CardBean> cardBeanArrayList;
    private CustomCardAdp customCardAdp;
    private ListView addedcardlist;
    private MySession mySession;
    private String user_log_data="",user_id="",cust_id="",come_from="";
    private RelativeLayout addcard,backlay;
    private ACProgressCustom ac_dialog;
    public static int comests=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_myadded_cards);
        mySession = new MySession(this);
        user_log_data = mySession.getKeyAlldata();
        comests=0;
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
            come_from= bundle.getString("come_from");
        }
        idinit();
        clickevent();
    }

    private void clickevent() {
        addcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyaddedCards.this,SaveCardDetail.class);
                startActivity(i);
            }
        });
        backlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              finish();
            }
        });
    }

    private void idinit() {
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

            rowView = inflater.inflate(R.layout.custom_card_itemlay, null);
            TextView savedcardnumber = rowView.findViewById(R.id.savedcardnumber);
            TextView savedcardtv = rowView.findViewById(R.id.savedcardtv);
            TextView validdate = rowView.findViewById(R.id.validdate);
            TextView cardbrand = rowView.findViewById(R.id.cardbrand);
            TextView cardtype = rowView.findViewById(R.id.cardtype);
            ImageView delete_card = rowView.findViewById(R.id.delete_card);
            ImageView update_card = rowView.findViewById(R.id.update_card);

            if (position==0){
                savedcardtv.setVisibility(View.VISIBLE);
            }
            else {
                savedcardtv.setVisibility(View.GONE);
            }

            String cardbrandstr = cardBeanArrayList.get(position).getBrand();
            String carnum = cardBeanArrayList.get(position).getLast4();
            if (cardbrandstr.length() > 4)
            {
                cardbrandstr = cardbrandstr.substring(0, 4);
            }
            String stars = "**** ****";
            savedcardnumber.setText(""+cardbrandstr+" "+stars+" "+carnum);

            // savedcardnumber.setText(""+cardBeanArrayList.get(position).getSetfullcardnumber());


            validdate.setText("" + cardBeanArrayList.get(position).getSetfullexpyearmonth());
            cardbrand.setText("" + cardBeanArrayList.get(position).getBrand());
            cardtype.setText("" + cardBeanArrayList.get(position).getFunding());
            cardtype.setAllCaps(true);
            delete_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sureDelete(cardBeanArrayList.get(position).getCustomer(),cardBeanArrayList.get(position).getId());

                }
            });
            update_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(MyaddedCards.this, UpdateCard.class);
                    i.putExtra("cardnumber_str",cardBeanArrayList.get(position).getSetfullcardnumber());
                    i.putExtra("cardholder_name",cardBeanArrayList.get(position).getCard_name());
                    i.putExtra("expmonth",cardBeanArrayList.get(position).getExp_month());
                    i.putExtra("expyear",cardBeanArrayList.get(position).getExp_year());
                    i.putExtra("card_id",cardBeanArrayList.get(position).getId());
                    i.putExtra("customer_id",cardBeanArrayList.get(position).getCustomer());
                    startActivity(i);
                }
            });
            // cardnumber.setText(""+getLastfour(cardBeanArrayList.get(position).getCard_number()));
            return rowView;
        }

    }


    private void sureDelete(final String customer, final String id) {
        final Dialog dialogSts = new Dialog(MyaddedCards.this, R.style.DialogSlideAnim);
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
                       // new GetAddedCard().execute();
                        finish();


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
               /* String postReceiverUrl = BaseUrl.baseurl + "get_card?";
                URL url = new URL(postReceiverUrl);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("cus_id", cust_id);*/
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
                        String customer_id = jsonObject1.getString("id");
                        Log.e("customer_id >> ", " >> " + customer_id);

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

                        //  new TransferAmount().execute(customer_id);


                    }

                    customCardAdp = new CustomCardAdp(MyaddedCards.this,cardBeanArrayList);
                    addedcardlist.setAdapter(customCardAdp);
                    customCardAdp.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }


}
