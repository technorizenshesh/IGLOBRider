package main.com.iglobuser.activity;

/**
 * Created by technorizen on 26/10/17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import main.com.iglobuser.R;


public class MobileVerificationActivity extends AppCompatActivity {
    private static final String PACKAGE = "com.techno.users.plikit";
    private static final int APP_REQUEST_CODE = 99;
public  static  String phoneNumberString="";
    String userid="";
    String classstatus="",login="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification);

        initAccountKitSmsFlow();
    }


    public void initAccountKitSmsFlow() {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        configurationBuilder.setReadPhoneStateEnabled(true);
        configurationBuilder.setReceiveSMS(true);

        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage = "";
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                finish();
            } else if (loginResult.wasCancelled()) {
                toastMessage = "varificaion Cancelled";
                finish();
            } else {
                if (loginResult.getAccessToken() != null) {
                  //  toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                    getAccount();
                }
            }
            // Surface the result to your user in an appropriate way.
        }
    }


    private void getAccount() {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                // Get Account Kit ID
                String accountKitId = account.getId();

                // Get phone number
                Log.e("accountKitId >>","account> "+account);
                Log.e("accountKitId >>","account> "+accountKitId);
                String phoneNumber = account.getPhoneNumber().getPhoneNumber();
              /*  Log.e("Code >>",""+account.getPhoneNumber().getCountryCode());
                Log.e("Code iso >>",""+account.getPhoneNumber().getCountryCodeIso());
                Log.e("Code Num>>",""+account.getPhoneNumber().getPhoneNumber());
                Log.e("Code R Num>>",""+account.getPhoneNumber().getRawPhoneNumber());
*/
                phoneNumberString = ""+phoneNumber.toString();
finish();
            }

            @Override
            public void onError(final AccountKitError error) {
                Log.e("AccountKit", error.toString());
                // Handle Error
            }
        });
    }
}
