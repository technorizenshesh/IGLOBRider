package main.com.iglobuser.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import main.com.iglobuser.R;
import main.com.iglobuser.constant.BaseUrl;

public class TermsConditions extends AppCompatActivity {

    private RelativeLayout exit_app_but;
    WebView aboutusdata;
    private ProgressBar progressbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);

        idinit();
        clickevetn();
        aboutusdata.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                progressbar.setVisibility(View.VISIBLE);
                Log.e("DDDD","lll");
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                progressbar.setVisibility(View.VISIBLE);
                Log.e("DDDD","ssss");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressbar.setVisibility(View.GONE);
                Log.e("DDDD","eee");

            }
        });

    }

    private void clickevetn() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void idinit() {
        aboutusdata= (WebView) findViewById(R.id.aboutusdata);
        exit_app_but = (RelativeLayout) findViewById(R.id.exit_app_but);
        progressbar = findViewById(R.id.progressbar);
        aboutusdata.getSettings().setJavaScriptEnabled(true);
        aboutusdata.getSettings().setPluginState(WebSettings.PluginState.ON);
        aboutusdata.setWebViewClient(new Callback());
        String pdfURL = BaseUrl.termsconditions;
        aboutusdata.loadUrl(pdfURL);

    }
    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            return(false);
        }
    }

}

