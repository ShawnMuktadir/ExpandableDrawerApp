package www.fiberathome.com.parkingapp.ui.navigation.parking;

import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.data.model.data.Constants.LANGUAGE_BN;
import static www.fiberathome.com.parkingapp.data.model.data.Constants.LANGUAGE_EN;
import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.data.model.data.preference.LanguagePreferences;
import www.fiberathome.com.parkingapp.data.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.data.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.data.model.response.sensors.SensorArea;
import www.fiberathome.com.parkingapp.databinding.FragmentParkingBinding;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.ui.home.HomeViewModel;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.KeyboardUtils;
import www.fiberathome.com.parkingapp.utils.MathUtils;
import www.fiberathome.com.parkingapp.utils.RecyclerTouchListener;
import www.fiberathome.com.parkingapp.utils.TextUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class ParkingFragment extends BaseFragment implements IOnBackPressListener {

    private ParkingActivity context;

    private ArrayList<SensorArea> sensorAreas;

    private ParkingAdapter parkingAdapter;

    private Location onConnectedLocation = null;

    public String name, count;
    public LatLng location;
    public double distance;
    public String duration;

    private final ArrayList<SensorArea> sensorAreaArrayList = new ArrayList<>();
    private List<List<String>> parkingSlotList = null;
    private String parkingArea = null;
    private String placeId = null;
    private double endLat = 0.0;
    private double endLng = 0.0;
    private double fetchDistance = 0.0;

    private HomeViewModel homeViewModel;
    FragmentParkingBinding binding;

    public ParkingFragment() {
        // Required empty public constructor
    }

    public static ParkingFragment newInstance() {
        return new ParkingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentParkingBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = (ParkingActivity) getActivity();
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        setListeners();

        if (isGPSEnabled() && ConnectivityUtils.getInstance().checkInternet(context)) {
            fetchParkingSlotSensors();
        } else {
            DialogUtils.getInstance().alertDialog(context,
                    context,
                    context.getResources().getString(R.string.connect_to_internet_gps),
                    context.getResources().getString(R.string.retry),
                    context.getResources().getString(R.string.close_app),
                    new DialogUtils.DialogClickListener() {
                        @Override
                        public void onPositiveClick() {
                            Timber.e("Positive Button clicked");
                            if (isGPSEnabled() && ConnectivityUtils.getInstance().checkInternet(context)) {
                                fetchParkingSlotSensors();
                            } else {
                                ToastUtils.getInstance().showToastMessage(context,
                                        context.getResources().getString(R.string.connect_to_internet_gps));
                            }
                        }

                        @Override
                        public void onNegativeClick() {
                            Timber.e("Negative Button Clicked");
                            if (context != null) {
                                context.finish();
                                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.thanks_message));
                            }
                        }
                    }).show();
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
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_gps));
            }
        }
        return false;
    }

    @SuppressLint({"ClickableViewAccessibility", "NotifyDataSetChanged"})
    private void setListeners() {

        binding.ivClearSearchText.setOnClickListener(view -> {
            binding.editTextParking.setText("");
            hideNoData();
            if (ConnectivityUtils.getInstance().checkInternet(context)) {
                updateAdapter();
            } else {
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet));
            }
        });

        binding.editTextParking.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    binding.ivClearSearchText.setVisibility(View.VISIBLE);
                } else {
                    binding.ivClearSearchText.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_EN) &&
                        TextUtils.getInstance().textContainsBangla(s.toString())) {
                    setNoDataForBangla();
                    binding.recyclerViewParking.setVisibility(View.GONE);
                    binding.editTextParking.setText("");
                } else if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_BN) &&
                        TextUtils.getInstance().textContainsEnglish(s.toString())) {
                    setNoDataForEnglish();
                    binding.recyclerViewParking.setVisibility(View.VISIBLE);
                    binding.editTextParking.setText("");
                } else {
                    binding.recyclerViewParking.setVisibility(View.VISIBLE);
                    filter(s.toString().trim());
                    parkingAdapter.notifyDataSetChanged();
                }

                if (s.length() == 0) {
                    Timber.e("length 0 called");
                    if (ConnectivityUtils.getInstance().checkInternet(context)) {
                        updateAdapter();
                    } else {
                        Timber.e("else length 0 called");
                        //ToastUtils.getInstance().showToastMessage(context, "Please connect to internet");
                    }
                }
            }
        });

        binding.editTextParking.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String contents = binding.editTextParking.getText().toString().trim();
                if (contents.length() > 0) {
                    //do search
                    if (ConnectivityUtils.getInstance().checkInternet(context) && isGPSEnabled()) {
                        if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_EN) &&
                                TextUtils.getInstance().textContainsBangla(contents)) {
                            setNoDataForBangla();
                            binding.recyclerViewParking.setVisibility(View.GONE);
                            binding.editTextParking.setText("");
                        } else if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_BN) &&
                                TextUtils.getInstance().textContainsEnglish(contents)) {
                            setNoDataForEnglish();
                            binding.recyclerViewParking.setVisibility(View.VISIBLE);
                            binding.editTextParking.setText("");
                        } else {
                            filter(contents);
                            parkingAdapter.notifyDataSetChanged();
                            KeyboardUtils.getInstance().hideKeyboard(context, binding.editTextParking);
                        }
                    } else {
                        ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet_gps));
                    }
                } else {
                    //if something to do for empty edittext
                    if (ConnectivityUtils.getInstance().checkInternet(context) && isGPSEnabled()) {
                        updateAdapter();
                        KeyboardUtils.getInstance().hideKeyboard(context, binding.editTextParking);
                    } else {
                        ToastUtils.getInstance().showToastMessage(context,
                                context.getResources().getString(R.string.connect_to_internet_gps));
                    }
                    return true;
                }
            }
            return false;
        });
    }

    private void fetchParkingSlotSensors() {
        showLoading(context);

        if (SharedData.getInstance().getOnConnectedLocation() != null) {
            onConnectedLocation = SharedData.getInstance().getOnConnectedLocation();
            Timber.e("onConnectedLocation -> %s", onConnectedLocation);
        }

        homeViewModel.initFetchParkingSlotSensors();
        homeViewModel.getParkingSlotResponseMutableLiveData().observe(context, parkingSlotResponse -> {
            hideLoading();
            if (parkingSlotResponse != null) {
                parkingSlotList = parkingSlotResponse.getSensors();
                if (parkingSlotList != null) {
                    for (List<String> baseStringList : parkingSlotList) {
                        for (int i = 0; i < baseStringList.size(); i++) {

                            if (!LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase("bn")) {
                                if (i == 1) {
                                    parkingArea = baseStringList.get(i);
                                }
                            } else {
                                if (i == 11) {
                                    parkingArea = baseStringList.get(i);
                                }
                            }

                            if (i == 0) {
                                placeId = baseStringList.get(i);
                            }

                            if (i == 2) {
                                endLat = Double.parseDouble(baseStringList.get(i).trim());
                            }

                            if (i == 3) {
                                endLng = Double.parseDouble(baseStringList.get(i).trim());
                            }

                            if (i == 4) {
                                count = baseStringList.get(i);
                            }

                            fetchDistance = MathUtils.getInstance().calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                                    endLat, endLng);

                            if (fetchDistance > 1.9) {
                                fetchDistance = fetchDistance + 2;
                            } else if (fetchDistance < 1.9 && fetchDistance > 1) {
                                fetchDistance = fetchDistance + 1;
                            } else {
                                fetchDistance = fetchDistance + 0.5;
                            }
                        }

                        SensorArea sensorArea = new SensorArea(parkingArea, placeId, endLat, endLng,
                                count, null, fetchDistance);
                        sensorAreaArrayList.add(sensorArea);
                    }
                    Collections.sort(sensorAreaArrayList, (c1, c2) -> Double.compare(c1.getDistance(), c2.getDistance()));
                    setFragmentControls(sensorAreaArrayList);
                }
            }
        });
    }

    private void setFragmentControls(ArrayList<SensorArea> sensorAreas) {
        this.sensorAreas = sensorAreas;
        binding.recyclerViewParking.setHasFixedSize(false);
        binding.recyclerViewParking.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        binding.recyclerViewParking.setLayoutManager(mLayoutManager);
        binding.recyclerViewParking.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        binding.recyclerViewParking.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerViewParking.addOnItemTouchListener(new RecyclerTouchListener(context, binding.recyclerViewParking, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (context != null) {
                    KeyboardUtils.getInstance().hideKeyboard(context, binding.editTextParking);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        ViewCompat.setNestedScrollingEnabled(binding.recyclerViewParking, false);
        setAdapter(sensorAreas);
    }

    private long mLastClickTime = System.currentTimeMillis();

    private static final long CLICK_TIME_INTERVAL = 300;

    private void setAdapter(ArrayList<SensorArea> sensorAreas) {
        this.sensorAreas = sensorAreas;
        parkingAdapter = new ParkingAdapter(context, sensorAreas, onConnectedLocation,
                (position, lat, lng, parkingAreaName, count, placeId) -> {
                    long now = System.currentTimeMillis();
                    if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                        return;
                    }
                    mLastClickTime = now;
                    if (isGPSEnabled() && ConnectivityUtils.getInstance().checkInternet(context)) {
                        try {
                            Timber.e("try called");
                            Bundle bundle = new Bundle();
                            bundle.putDouble("lat", lat);
                            bundle.putDouble("lng", lng);
                            bundle.putString("areaName", parkingAreaName);
                            bundle.putString("count", count);
                            bundle.putString("placeId", placeId);
                            context.startActivityWithFinishBundle(HomeActivity.class, bundle);
                        } catch (Exception e) {
                            Timber.e("try catch called -> %s", e.getMessage());
                        }
                    } else {
                        ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet_gps));
                    }
                });
        binding.recyclerViewParking.setAdapter(parkingAdapter);
    }

    private void setNoDataForEnglish() {
        binding.textViewNoData.setVisibility(View.VISIBLE);
        binding.textViewNoData.setText(context.getResources().getString(R.string.no_parking_spot_found));
        DialogUtils.getInstance().showOnlyMessageDialog(context.getResources().getString(R.string.change_app_language_to_english), context);
    }

    private void setNoDataForBangla() {
        binding.textViewNoData.setVisibility(View.VISIBLE);
        binding.textViewNoData.setText(context.getResources().getString(R.string.no_parking_spot_found));
        DialogUtils.getInstance().showOnlyMessageDialog(context.getResources().getString(R.string.change_app_language_to_bangla), context);
    }

    private void filter(String text) {
        ArrayList<SensorArea> filteredList = new ArrayList<>();
        if (ConnectivityUtils.getInstance().checkInternet(context) && isGPSEnabled()) {

            if (!sensorAreas.isEmpty()) {
                for (SensorArea item : sensorAreas) {
                    if (item.getParkingArea().toLowerCase().contains(text.toLowerCase()) || item.getCount().toLowerCase().contains(text.toLowerCase())) {
                        hideNoData();
                        filteredList.add(item);
                    }
                }

                if (filteredList.isEmpty()) {
                    setNoData();
                } else {
                    hideNoData();
                }
                parkingAdapter.filterList(filteredList);
            } else {
                Timber.e("sensorAreas is empty");
            }
        } else {
            ToastUtils.getInstance().showToastMessage(context,
                    context.getResources().getString(R.string.connect_to_internet_gps));
        }
    }

    private void updateAdapter() {
        if (parkingAdapter != null) {
            parkingAdapter = null;
        }
        setAdapter(sensorAreas);
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (providerEnabled) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle(context.getResources().getString(R.string.gps_permission))
                    .setMessage(context.getResources().getString(R.string.gps_required))
                    .setPositiveButton(context.getResources().getString(R.string.yes), (dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    })
                    .setCancelable(false)
                    .show();
        }
        return false;
    }

    private void setNoData() {
        binding.textViewNoData.setVisibility(View.VISIBLE);
        binding.textViewNoData.setText(context.getResources().getString(R.string.no_parking_spot_found));
    }

    private void hideNoData() {
        binding.textViewNoData.setVisibility(View.GONE);
    }
}
