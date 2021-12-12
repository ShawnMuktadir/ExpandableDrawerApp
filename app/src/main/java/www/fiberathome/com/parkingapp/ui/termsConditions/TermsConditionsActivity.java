package www.fiberathome.com.parkingapp.ui.termsConditions;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.databinding.ActivityTermsConditionsBinding;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
public class TermsConditionsActivity extends AppCompatActivity {

    protected ActivityTermsConditionsBinding binding;
    private ProgressDialog progressDialog;
    protected Context context;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTermsConditionsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = this;

        progressDialog = DialogUtils.getInstance().progressDialog(context, context.getResources().getString(R.string.please_wait));

        if (ConnectivityUtils.getInstance().checkInternet(context)) {
            binding.webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(TermsConditionsActivity.this, "Error:" + description, Toast.LENGTH_SHORT).show();

                }
            });
            binding.webView.getSettings().setJavaScriptEnabled(true);
            binding.webView.getSettings().setUseWideViewPort(true);
            binding.webView.getSettings().setLoadWithOverviewMode(true);
            binding.webView.getSettings().setBuiltInZoomControls(true);
            binding.webView.setInitialScale(1);
            binding.webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            binding.webView.setScrollbarFadingEnabled(false);
            binding.webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            binding.webView.loadUrl("https://docs.google.com/document/d/e/2PACX-1vQ5ikmuSBW8iYYSUXutxZkPGEZ_HhFlxxWKC5m0v9MpPvezmBvyb4WLZjSuuJnXl6xs6f7gc7UwQ223/pub");
        } else {
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet));
        }

    }

    public static class AppWebViewClients extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            //view.loadUrl("javascript:(function() { " +
            //"document.getElementsByClassName('ndfHFb-c4YZDc-GSQQnc-LgbsSe ndfHFb-c4YZDc-to915-LgbsSe VIpgJd-TzA9Ye-eEGnhe ndfHFb-c4YZDc-LgbsSe')[0].style.display='none'; })()");
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.getCause();
        }

        return false;
    }
}
