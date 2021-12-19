package www.fiberathome.com.parkingapp.ui.privacyPolicy;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.databinding.FragmentPrivacyPolicyBinding;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.response.termsCondition.TermsCondition;
import www.fiberathome.com.parkingapp.model.response.termsCondition.TermsConditionResponse;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class PrivacyPolicyFragment extends BaseFragment implements IOnBackPressListener {
    private PrivacyPolicyActivity context;
    FragmentPrivacyPolicyBinding binding;

    private final ArrayList<TermsCondition> termsConditionsGlobal = new ArrayList<>();

    public PrivacyPolicyFragment() {
        // Required empty public constructor
    }

    public static PrivacyPolicyFragment newInstance() {
        return new PrivacyPolicyFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPrivacyPolicyBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public boolean onBackPressed() {
        if (ConnectivityUtils.getInstance().isGPSEnabled(context)) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, HomeFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            } else {
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_gps));
            }
        }
        return false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = (PrivacyPolicyActivity) getActivity();
        if (ConnectivityUtils.getInstance().checkInternet(context)) {
            fetchPrivacyPolicy();
        } else {
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet));
        }
    }

   /* private List<List<String>> list;

    private void fetchPrivacyPolicy() {
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
        }, (VolleyError e) -> {
            e.printStackTrace();
            hideLoading();
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
        }) {

        };
        ParkingApp.getInstance().addToRequestQueue(strReq);
    }*/

    private final ArrayList<TermsCondition> termsConditionArrayList = new ArrayList<>();
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


                    termsConditionResponse = response.body();

                    termConditionList = termsConditionResponse.getTermsCondition();


                    if (termConditionList != null) {
                        TermsCondition termsCondition = null;
                        for (int i = 0; i < termConditionList.size(); i++) {
                            title = termConditionList.get(i).get(6).trim();
                            description = termConditionList.get(i).get(2).trim();
                            date = termConditionList.get(i).get(4).trim();
                            if (termsCondition != null) {
                                if (termsCondition.getTitle().equalsIgnoreCase(title)) {
                                    String tempDescription = termsCondition.getDescription() + " " + description;
                                    termsCondition.setDescription(tempDescription);
                                    termsConditionArrayList.remove(termsConditionArrayList.size() - 1);
                                } else {
                                    termsCondition = new TermsCondition(title, description, date);
                                }
                            } else {
                                termsCondition = new TermsCondition(title, description, date);
                            }
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
    }

    private void setTermsConditions(ArrayList<TermsCondition> termsConditions) {
        binding.recyclerViewPrivacy.setHasFixedSize(true);
        binding.recyclerViewPrivacy.setItemViewCacheSize(20);
        binding.recyclerViewPrivacy.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerViewPrivacy.setLayoutManager(mLayoutManager);
        binding.recyclerViewPrivacy.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        binding.recyclerViewPrivacy.setItemAnimator(new DefaultItemAnimator());

        ViewCompat.setNestedScrollingEnabled(binding.recyclerViewPrivacy, false);
        PrivacyPolicyAdapter privacyPolicyAdapter = new PrivacyPolicyAdapter(context, termsConditions);
        binding.recyclerViewPrivacy.setAdapter(privacyPolicyAdapter);
    }
}
