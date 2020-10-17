package main.com.iglobuser.Fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import main.com.iglobuser.R;
import main.com.iglobuser.databinding.FragmentAddAmountBinding;
import main.com.iglobuser.paymentclasses.MyCardsPayment;

public class FragmentAddAmount extends Fragment {
    private FragmentAddAmountBinding binding;
    private String amount_str="0";

    public FragmentAddAmount() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_add_amount, container, false);
        bindView();
        return binding.getRoot();
    }

    private void bindView() {
        binding.addmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount_str = binding.amountEt.getText().toString();
                if (amount_str.equalsIgnoreCase("")){
                    Toast.makeText(getActivity(),getResources().getString(R.string.enteramount), Toast.LENGTH_LONG).show();
                }
                else {
                    Intent i = new Intent(getActivity(), MyCardsPayment.class);
                    i.putExtra("amount_str",amount_str);
                    startActivity(i);

                }
            }
        });
        binding.fiftyBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.amountEt.setText("50");
                binding.fiftyBut.setBackgroundResource(R.drawable.border_yellowrounddrab);
                binding.hundredBut.setBackgroundResource(R.drawable.border_grey_rec);
                binding.onefiftyBut.setBackgroundResource(R.drawable.border_grey_rec);

            }
        });
        binding.hundredBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.amountEt.setText("100");
                binding.hundredBut.setBackgroundResource(R.drawable.border_yellowrounddrab);
                binding.onefiftyBut.setBackgroundResource(R.drawable.border_grey_rec);
                binding.fiftyBut.setBackgroundResource(R.drawable.border_grey_rec);
            }
        });

        binding.onefiftyBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.amountEt.setText("150");
                binding.onefiftyBut.setBackgroundResource(R.drawable.border_yellowrounddrab);
                binding.hundredBut.setBackgroundResource(R.drawable.border_grey_rec);
                binding.fiftyBut.setBackgroundResource(R.drawable.border_grey_rec);
            }
        });

    }
}
