package www.fiberathome.com.parkingapp.ui.privacyPolicy;

import android.annotation.SuppressLint;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.response.termsCondition.TermsCondition;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

import static android.content.Context.LOCATION_SERVICE;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class PrivacyPolicyFragment extends BaseFragment implements IOnBackPressListener {

    @BindView(R.id.webView)
    WebView webView;

    @BindView(R.id.recyclerViewPrivacy)
    RecyclerView recyclerViewPrivacy;

    private Unbinder unbinder;

    private PrivacyPolicyActivity context;

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
        return inflater.inflate(R.layout.fragment_privacy_policy, container, false);
    }

    private final ArrayList<TermsCondition> termsConditionsGlobal = new ArrayList<>();

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isGPSEnabled()) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, HomeFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_gps));
            }
        }
        return false;
    }

    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {
            Timber.e("isGPSEnabled else called");
        }
        return false;
    }

    private final List<List<String>> termConditionList = null;
    private final ArrayList<TermsCondition> termsConditionArrayList = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);

        context = (PrivacyPolicyActivity) getActivity();

        if (ConnectivityUtils.getInstance().checkInternet(context)) {
            fetchPrivacyPolicy();
        } else {
            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
        }
    }

    private List<List<String>> list;

    private void fetchPrivacyPolicy() {
        Timber.e("fetchPrivacyPolicy called");

        showLoading(context);

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_PRIVACY_POLICY, response -> {

            hideLoading();

            try {
                JSONObject object = new JSONObject(response);
                JSONArray jsonArray = object.getJSONArray("termsCondition");

                TermsCondition termsConditionTemp = new TermsCondition();

                if (jsonArray.length() > 0) {
                    JSONArray array2 = jsonArray.getJSONArray(1);
                    termsConditionTemp.setTitle(array2.getString(6).trim());
                    termsConditionTemp.setDescription(array2.getString(2).trim());
                }

                for (int i = 1; i < jsonArray.length(); i++) {
                    JSONArray array = jsonArray.getJSONArray(i);

                    TermsCondition termsCondition = new TermsCondition();

                    termsCondition.setTitle(array.getString(6).trim());
                    termsCondition.setDescription(array.getString(2).trim());
                    termsCondition.setDate(array.getString(4).trim());


                    if (array.getString(6).trim().equals(termsConditionTemp.getTitle()) && i != 1) {

                        termsCondition.setTitle("");
                        termsCondition.setDescription(array.getString(2).trim());

                        JSONArray array2 = jsonArray.getJSONArray(i);
                        termsConditionTemp.setTitle(array2.getString(6).trim());
                        termsConditionTemp.setDescription(array2.getString(2).trim());
                    } else {
                        termsCondition.setTitle(array.getString(6).trim());
                        termsCondition.setDescription(array.getString(2).trim());

                        JSONArray array2 = jsonArray.getJSONArray(i);
                        termsConditionTemp.setTitle(array2.getString(6).trim());
                        termsConditionTemp.setDescription(array2.getString(2).trim());
                    }
                    termsConditionsGlobal.add(termsCondition);
                    Timber.e("termsConditions -> %s", new Gson().toJson(termsConditionsGlobal));
                }
                setTermsConditions(termsConditionsGlobal);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, e -> {
            e.printStackTrace();
            hideLoading();
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
        }) {

        };

        ParkingApp.getInstance().addToRequestQueue(strReq);
    }

    /*private final ArrayList<TermsCondition> termsConditionArrayList = new ArrayList<>();
    private List<List<String>> termConditionList = null;
    private List<List<String>> list;
    private TermsConditionResponse termsConditionResponse;

    private String title = null;

    private String description = null;

    private String date = null;

    private void fetchPrivacyPolicy() {
        Timber.e("fetchPrivacyPolicy called");

        showLoading(context);

        ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);

        Call<TermsConditionResponse> call = request.getTermCondition();

        call.enqueue(new Callback<TermsConditionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TermsConditionResponse> call,
                                   @NonNull retrofit2.Response<TermsConditionResponse> response) {
                hideLoading();
                if (response.body() != null) {

                    hideLoading();

                    list = response.body().getTermsCondition();

                    Timber.e("list -> %s", new Gson().toJson(list));

                    TermsCondition termsConditionTemp = new TermsCondition();

                    if (list.size() > 0) {
                        //JSONArray array2 = jsonArray.getJSONArray(1);
                        termsConditionTemp.setTitle(list.get(6).toString());
                        termsConditionTemp.setDescription(list.get(2).toString());
                    }

                    termsConditionResponse = response.body();

                    termConditionList = termsConditionResponse.getTermsCondition();

                    if (termConditionList != null) {

                        for (List<String> baseStringList : termConditionList) {
                            for (int i = 0; i < baseStringList.size(); i++) {

                                Timber.d("onResponse: i ->  %s", i);

                                if (i == 6) {
                                    title = baseStringList.get(i).trim();
                                }

                                if (i == 2) {
                                    description = baseStringList.get(i).trim();
                                }

                                if (i == 4) {
                                    date = baseStringList.get(i).trim();
                                }
                            }
                            TermsCondition termsCondition = new TermsCondition(title, description, date);

                            termsConditionArrayList.add(termsCondition);
                        }

                        setTermsConditions(termsConditionArrayList);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TermsConditionResponse> call, @NonNull Throwable t) {
                Timber.e("onFailure -> %s", t.getMessage());
            }
        });
    }*/

    private void setTermsConditions(ArrayList<TermsCondition> termsConditions) {

        recyclerViewPrivacy.setHasFixedSize(true);
        recyclerViewPrivacy.setItemViewCacheSize(20);
        recyclerViewPrivacy.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewPrivacy.setLayoutManager(mLayoutManager);
        recyclerViewPrivacy.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        recyclerViewPrivacy.setItemAnimator(new DefaultItemAnimator());

        ViewCompat.setNestedScrollingEnabled(recyclerViewPrivacy, false);
        PrivacyPolicyAdapter privacyPolicyAdapter = new PrivacyPolicyAdapter(context, termsConditions);
        recyclerViewPrivacy.setAdapter(privacyPolicyAdapter);
    }
}
