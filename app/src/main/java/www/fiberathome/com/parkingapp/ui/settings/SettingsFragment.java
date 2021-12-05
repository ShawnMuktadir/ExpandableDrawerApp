package www.fiberathome.com.parkingapp.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.databinding.FragmentSettingsBinding;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.ui.settings.adapter.SettingAdapter;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class SettingsFragment extends BaseFragment implements SettingAdapter.OnItemClickListener, IOnBackPressListener {

    FragmentSettingsBinding binding;
    private SettingsActivity context;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = (SettingsActivity) getActivity();

        initView(view);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.e("onCreate called");
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

    private void initView(View view) {

        String[] nameArray = new String[]{
                context.getResources().getString(R.string.language)

        };

        Integer[] drawableArray = new Integer[]{
                R.drawable.ic_language
        };

        SettingAdapter settingAdapter = new SettingAdapter(
                populateSettingsItem(nameArray, drawableArray, new ArrayList<>()), this);

        binding.recyclerViewSettings.setLayoutManager(new LinearLayoutManager(context));
        binding.recyclerViewSettings.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerViewSettings.setAdapter(settingAdapter);
    }

    private List<www.fiberathome.com.parkingapp.model.Settings> populateSettingsItem(String[] nameArray, Integer[] drawableArray, List<www.fiberathome.com.parkingapp.model.Settings> settings) {
        for (int i = 0; i < nameArray.length; i++) {
            settings.add(new www.fiberathome.com.parkingapp.model.Settings(
                    nameArray[i],
                    drawableArray[i]
            ));
        }

        return settings;
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case 0:
                context.startActivity(LanguageSettingActivity.class);
                break;

            case 1:
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.not_implemented_yet));
                break;

            //ToDo for more settings
            default:
                throw new IllegalStateException("Unexpected value: " + position);
        }
    }
}
