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

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.databinding.FragmentPrivacyPolicyBinding;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.response.termsCondition.TermsCondition;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
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
        if (ApplicationUtils.isGPSEnabled(context)) {
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

    private List<List<String>> list;

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
