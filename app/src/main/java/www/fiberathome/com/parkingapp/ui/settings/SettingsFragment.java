package www.fiberathome.com.parkingapp.ui.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.ui.settings.adapter.SettingAdapter;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings("unused")
public class SettingsFragment extends BaseFragment implements SettingAdapter.OnItemClickListener, IOnBackPressListener {

    @BindView(R.id.recyclerView_settings)
    RecyclerView recyclerViewSettings;

    private Unbinder unbinder;

    private SettingsActivity context;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
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
    public void onDestroyView() {
        super.onDestroyView();

        Timber.e("onDestroyView called ");

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

    public AlertDialog alertDialog;

    private void initView(View view) {
        unbinder = ButterKnife.bind(this, view);

        String[] nameArray = new String[]{
                context.getResources().getString(R.string.language)

        };

        Integer[] drawableArray = new Integer[]{
                R.drawable.ic_language
        };

        SettingAdapter settingAdapter = new SettingAdapter(
                populateSettingsItem(nameArray, drawableArray, new ArrayList<>()), this);

        recyclerViewSettings.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewSettings.setItemAnimator(new DefaultItemAnimator());
        recyclerViewSettings.setAdapter(settingAdapter);
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

    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {
            alertDialog = new AlertDialog.Builder(context)
                    .setTitle("GPS Permissions")
                    .setMessage("GPS is required for this app to work. Please enable GPS.")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();
        }
        return false;
    }
}
