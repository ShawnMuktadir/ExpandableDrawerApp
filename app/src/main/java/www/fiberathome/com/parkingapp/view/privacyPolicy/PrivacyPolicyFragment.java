package www.fiberathome.com.parkingapp.view.privacyPolicy;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.model.termsCondition.TermsCondition;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

public class PrivacyPolicyFragment extends Fragment {

    @BindView(R.id.webView)
    WebView webView;

    @BindView(R.id.recyclerViewPrivacy)
    RecyclerView recyclerViewPrivacy;

    private Context context;
    private Unbinder unbinder;

    private ProgressDialog progressDialog;

    public PrivacyPolicyFragment() {
        // Required empty public constructor
    }

    public static PrivacyPolicyFragment newInstance() {
        return new PrivacyPolicyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_privacy_policy, container, false);
        unbinder = ButterKnife.bind(this, view);
        context = getActivity();

        if (ApplicationUtils.checkInternet(requireActivity())) {
            fetchPrivacyPolicy();
        } else {
            TastyToastUtils.showTastyWarningToast(context, "Please connect to internet...");
        }

//        openPrivacyPolicy();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private ArrayList<TermsCondition> termsConditionsGlobal = new ArrayList<>();
    private PrivacyPolicyAdapter privacyPolicyAdapter;

    private void fetchPrivacyPolicy() {
        Timber.e("fetchPrivacyPolicy called");
        progressDialog = ApplicationUtils.progressDialog(getActivity(),
                "Please wait...");

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_PRIVACY_POLICY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("termsCondition");

                    TermsCondition termsConditionTemp = new TermsCondition();

                    if (jsonArray.length() > 0) {
                        JSONArray array2 = jsonArray.getJSONArray(1);
                        termsConditionTemp.setTermsConditionDomain(array2.getString(6).trim());
                        termsConditionTemp.setTermsConditionBody(array2.getString(2).trim());
                    }

                    for (int i = 1; i < jsonArray.length(); i++) {
                        JSONArray array = jsonArray.getJSONArray(i);

                        TermsCondition termsCondition = new TermsCondition();

                        termsCondition.setTermsConditionId(array.getString(0).trim());
                        termsCondition.setTermsConditionSerialNo(array.getString(1).trim());
                        termsCondition.setTermsConditionRemarks(array.getString(3).trim());
                        termsCondition.setTermsConditionDate(array.getString(4).trim());
                        termsCondition.setTermsConditionUser(array.getString(5).trim());
                        termsCondition.setTermsConditionDomain(array.getString(6).trim());


                        if(array.getString(6).trim().equals(termsConditionTemp.getTermsConditionDomain()) && i!= 1) {

                            termsCondition.setTermsConditionDomain("");
                            termsCondition.setTermsConditionBody(array.getString(2).trim());

                            JSONArray array2 = jsonArray.getJSONArray(i);
                            termsConditionTemp.setTermsConditionDomain(array2.getString(6).trim());
                            termsConditionTemp.setTermsConditionBody(array2.getString(2).trim());
                        } else {

                            termsCondition.setTermsConditionDomain(array.getString(6).trim());
                            termsCondition.setTermsConditionBody(array.getString(2).trim());

                            JSONArray array2 = jsonArray.getJSONArray(i);
                            termsConditionTemp.setTermsConditionDomain(array2.getString(6).trim());
                            termsConditionTemp.setTermsConditionBody(array2.getString(2).trim());
                        }

                        termsConditionsGlobal.add(termsCondition);
                        Timber.e("termsConditions -> %s", new Gson().toJson(termsConditionsGlobal));
                    }

                    setTermsConditions(termsConditionsGlobal);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        }) {

        };

        ParkingApp.getInstance().addToRequestQueue(strReq);
    }

    private void setTermsConditions(ArrayList<TermsCondition> termsConditions) {
        this.termsConditionsGlobal = termsConditions;
        recyclerViewPrivacy.setHasFixedSize(true);
        recyclerViewPrivacy.setItemViewCacheSize(20);
        recyclerViewPrivacy.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewPrivacy.setLayoutManager(mLayoutManager);
        recyclerViewPrivacy.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        recyclerViewPrivacy.setItemAnimator(new DefaultItemAnimator());

        ViewCompat.setNestedScrollingEnabled(recyclerViewPrivacy, false);
        privacyPolicyAdapter = new PrivacyPolicyAdapter(context, termsConditionsGlobal);
        recyclerViewPrivacy.setAdapter(privacyPolicyAdapter);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void openPrivacyPolicy() {
        progressDialog = ApplicationUtils.progressDialog(context, "Please wait...");

        if (ApplicationUtils.checkInternet(context)) {
            webView.setWebViewClient(new WebViewClient() {
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
                    Toast.makeText(context, "Error:" + description, Toast.LENGTH_SHORT).show();

                }
            });
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.setInitialScale(1);
            webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            webView.setScrollbarFadingEnabled(false);
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            webView.loadUrl("https://docs.google.com/document/d/e/2PACX-1vQ5ikmuSBW8iYYSUXutxZkPGEZ_HhFlxxWKC5m0v9MpPvezmBvyb4WLZjSuuJnXl6xs6f7gc7UwQ223/pub");
        } else {
            TastyToastUtils.showTastyWarningToast(context, "Please Connect to internet!");
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
//            view.loadUrl("javascript:(function() { " +
//                    "document.getElementsByClassName('ndfHFb-c4YZDc-GSQQnc-LgbsSe ndfHFb-c4YZDc-to915-LgbsSe VIpgJd-TzA9Ye-eEGnhe ndfHFb-c4YZDc-LgbsSe')[0].style.display='none'; })()");

        }
    }
}
