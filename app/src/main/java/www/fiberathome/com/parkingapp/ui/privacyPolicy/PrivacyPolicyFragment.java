package www.fiberathome.com.parkingapp.ui.privacyPolicy;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.data.model.response.termsCondition.TermsCondition;
import www.fiberathome.com.parkingapp.data.model.response.termsCondition.TermsConditionResponse;
import www.fiberathome.com.parkingapp.databinding.FragmentPrivacyPolicyBinding;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class PrivacyPolicyFragment extends BaseFragment implements IOnBackPressListener {
    private final ArrayList<TermsCondition> termsConditionArrayList = new ArrayList<>();
    private List<List<String>> termConditionList = null;
    private List<List<String>> list;
    private TermsConditionResponse termsConditionResponse;
    private String title, description, date = null;

    private PrivacyPolicyActivity context;
    private PrivacyPolicyViewModel privacyPolicyViewModel;
    FragmentPrivacyPolicyBinding binding;

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
        privacyPolicyViewModel = new ViewModelProvider(this).get(PrivacyPolicyViewModel.class);
        if (ConnectivityUtils.getInstance().checkInternet(context)) {
            fetchPrivacyPolicy();
        } else {
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet));
        }
    }

    private void fetchPrivacyPolicy() {
        Timber.e("fetchPrivacyPolicy called");
        showLoading(context);

        privacyPolicyViewModel.getTermCondition();
        privacyPolicyViewModel.getTermConditionMutableData().observe(context, (TermsConditionResponse termsConditionResponse) -> {
            hideLoading();
            if (termsConditionResponse != null) {
                hideLoading();
                list = termsConditionResponse.getTermsCondition();
                Timber.e("list -> %s", new Gson().toJson(list));

                termConditionList = termsConditionResponse.getTermsCondition();

                if (termConditionList != null) {
                    TermsCondition termsCondition = null;
                    for (int i = 0; i < termConditionList.size(); i++) {
                        title = termConditionList.get(i).get(6).trim();
                        description = termConditionList.get(i).get(2).trim();
                        date = termConditionList.get(i).get(4).trim();
                        if (termsCondition != null) {
                            if (termsCondition.getTitle().equalsIgnoreCase(title)) {
                                String tempDescription = termsCondition.getDescription() + " \n" + description;
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
