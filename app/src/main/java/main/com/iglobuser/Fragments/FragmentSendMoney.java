package main.com.iglobuser.Fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import main.com.iglobuser.R;
import main.com.iglobuser.constant.BaseUrl;
import main.com.iglobuser.constant.MySession;
import main.com.iglobuser.databinding.FragmentSendMoneyBinding;
import www.develpoeramit.mapicall.ApiCallBuilder;

public class FragmentSendMoney extends Fragment {
    private FragmentSendMoneyBinding binding;
    private boolean isVerify=false;
    private String ToUserID;
    private MySession mySession;
    private String user_log_data;
    private String user_id;

    public FragmentSendMoney() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_send_money, container, false);
        mySession = new MySession(getContext());
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
        bindView();
        return binding.getRoot();
    }

    private void bindView() {
       binding.btnVerify.setOnClickListener(v->{
           if (isVerify){
               if (binding.etAmount.getText().toString().isEmpty()){
                   binding.etAmount.setError(getString(R.string.required));
                   binding.etAmount.requestFocus();
                   return;
               }
               SendAmount();
             /*  int amount=Integer.parseInt(binding.etAmount.getText().toString());
               if (Integer.parseInt(MainActivity.amount)>amount){
               }else {
                   binding.etAmount.setError("You have not sufficient balance");
                   binding.etAmount.requestFocus();
               }*/
           }else {
               if (binding.etMobile.getText().toString().isEmpty()){
                   binding.etMobile.setError(getString(R.string.required));
                   binding.etMobile.requestFocus();
                   return;
               }
               getVerify();
           }
       });
    }

    private void SendAmount() {
        HashMap<String, String> param=new HashMap<>();
        param.put("user_id",user_id);
        param.put("touser_id",ToUserID);
        param.put("amount",binding.etAmount.getText().toString());
        ApiCallBuilder.build(getContext()).setUrl(BaseUrl.get().sendWalletAmount())
                .isShowProgressBar(true)
                .setParam(param).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                try {
                    JSONObject object=new JSONObject(response);
                    boolean status=object.getString("status").contains("1");
                    Toast.makeText(getActivity(), ""+object.getString("message"), Toast.LENGTH_SHORT).show();
                    if (status){
                        getActivity().finish();
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

    private void getVerify() {
        HashMap<String, String> param=new HashMap<>();
        param.put("mobile",binding.etMobile.getText().toString());
        ApiCallBuilder.build(getContext()).setUrl(BaseUrl.get().getVerify())
                .isShowProgressBar(true)
                .setParam(param).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                Log.e("getVerify","=====>"+response);
                try {
                    JSONObject object=new JSONObject(response);
                    isVerify=object.getString("status").contains("1");
                    if (isVerify){
                        binding.cardProfile.setVisibility(View.VISIBLE);
                        JSONObject result=object.getJSONObject("result");
                        ToUserID = result.getString("id");
                        String first_name=result.getString("first_name")+" "+result.getString("last_name");
                        String mobile=result.getString("mobile");
                        String image=result.getString("image");
                        binding.tvName.setText(first_name);
                        binding.tvMobile.setText(mobile);
                        Picasso.with(getActivity()).load(image).placeholder(R.drawable.user).into(binding.imageUser);
                        binding.btnVerify.setText("Send Amount");
                    }else {
                        binding.etMobile.setError(object.getString("result"));
                        binding.etMobile.requestFocus();
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
}
