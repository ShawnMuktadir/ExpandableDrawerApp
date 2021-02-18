package www.fiberathome.com.parkingapp.ui.parking;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.response.parkingSlot.ParkingSlotResponse;
import www.fiberathome.com.parkingapp.model.response.sensors.SensorArea;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.RecyclerTouchListener;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;
import static www.fiberathome.com.parkingapp.utils.Constants.LANGUAGE_BN;
import static www.fiberathome.com.parkingapp.utils.Constants.LANGUAGE_EN;

@SuppressLint("NonConstantResourceId")
public class ParkingFragment extends BaseFragment implements IOnBackPressListener {

    private static final String TAG = ParkingFragment.class.getCanonicalName();

    @BindView(R.id.recyclerViewParking)
    RecyclerView recyclerViewParking;

    @BindView(R.id.textViewNoData)
    TextView textViewNoData;

    @BindView(R.id.linearLayoutBottom)
    LinearLayout linearLayoutBottom;

    @BindView(R.id.linearLayoutParkingFragment)
    LinearLayout linearLayoutParkingFragment;

    @BindView(R.id.editTextParking)
    EditText editTextParking;

    @BindView(R.id.ivClearSearchText)
    ImageView ivClearSearchText;

    @BindView(R.id.btnGetDirection)
    Button btnGetDirection;

    @BindView(R.id.imageViewBack)
    ImageView imageViewBack;

    @BindView(R.id.textViewParkingAreaCount)
    TextView textViewParkingAreaCount;

    @BindView(R.id.textViewParkingAreaName)
    TextView textViewParkingAreaName;

    @BindView(R.id.textViewParkingDistance)
    TextView textViewParkingDistance;

    @BindView(R.id.textViewParkingTravelTime)
    TextView textViewParkingTravelTime;

    private Unbinder unbinder;

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
    private List<List<String>> list;
    private ParkingSlotResponse parkingSlotResponse;
    private String parkingArea = null;
    private String placeId = null;
    private double endLat = 0.0;
    private double endLng = 0.0;
    private double fetchDistance = 0.0;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);

        context = (ParkingActivity) getActivity();

        setListeners();

        if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
            Timber.e("check internet method called");
            fetchParkingSlotSensors();
        } else {
            ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet_gps), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                Timber.e("Positive Button clicked");
                if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
                    fetchParkingSlotSensors();
                } else {
                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
                }
            }, (dialog, which) -> {
                Timber.e("Negative Button Clicked");
                dialog.dismiss();
                if (context != null) {
                    context.finish();
                    TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                }
            });
        }
    }

    @Override
    public void onStart() {
        Timber.e("onStart called");
        super.onStart();
    }

    @Override
    public void onResume() {
        Timber.e("onResume called");
        super.onResume();
    }

    @Override
    public void onPause() {
        Timber.e("onPause called");
        super.onPause();
    }

    @Override
    public void onStop() {
        Timber.e("onStop called");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Timber.e("onDestroy called");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
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

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {

        ivClearSearchText.setOnClickListener(view -> {
            editTextParking.setText("");
            hideNoData();
            if (ApplicationUtils.checkInternet(context)) {
                updateAdapter();
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
            }
        });

        editTextParking.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String contents = editTextParking.getText().toString().trim();
                if (contents.length() > 0) {
                    //do search
                    if (ApplicationUtils.checkInternet(context) && isGPSEnabled()) {
                        if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_EN) && ApplicationUtils.textContainsBangla(contents)) {
                            setNoDataForBangla();
                            recyclerViewParking.setVisibility(View.GONE);
                            editTextParking.setText("");
                        } else if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_BN) && ApplicationUtils.isEnglish(contents)) {
                            setNoDataForEnglish();
                            recyclerViewParking.setVisibility(View.VISIBLE);
                            editTextParking.setText("");
                        } else {
                            filter(contents);
                            parkingAdapter.notifyDataSetChanged();
                            ApplicationUtils.hideKeyboard(context, editTextParking);
                        }
                    } else {
                        TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
                    }
                } else {
                    //if something to do for empty edittext
                    if (ApplicationUtils.checkInternet(context) && isGPSEnabled()) {
                        updateAdapter();
                        ApplicationUtils.hideKeyboard(context, editTextParking);
                    } else {
                        TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
                    }
                    return true;
                }
            }
            return false;
        });

        editTextParking.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ivClearSearchText.setVisibility(View.VISIBLE);
                } else {
                    ivClearSearchText.setVisibility(View.GONE);
                }
                if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_EN) && ApplicationUtils.textContainsBangla(s.toString())) {
                    setNoDataForBangla();
                    recyclerViewParking.setVisibility(View.GONE);
                    editTextParking.setText("");
                } else if (Preferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_BN) && ApplicationUtils.isEnglish(s.toString())) {
                    setNoDataForEnglish();
                    recyclerViewParking.setVisibility(View.VISIBLE);
                    editTextParking.setText("");
                } else {
                    recyclerViewParking.setVisibility(View.VISIBLE);
                    filter(s.toString());
                    parkingAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    Timber.e("length 0 called");
                    if (ApplicationUtils.checkInternet(context)) {
                        updateAdapter();
                    } else {
                        //TastyToastUtils.showTastyWarningToast(context, "Please connect to internet");
                    }
                }
            }
        });
    }

    private void fetchParkingSlotSensors() {
        Timber.e("fetchParkingSlotSensors called");

        showLoading(context);

        if (SharedData.getInstance().getOnConnectedLocation() != null) {
            onConnectedLocation = SharedData.getInstance().getOnConnectedLocation();
            Timber.e("onConnectedLocation -> %s", onConnectedLocation);
        }

        ApiService request = ApiClient.getRetrofitInstance(AppConfig.URL_FETCH_SENSOR_AREA).create(ApiService.class);
        Call<ParkingSlotResponse> call = request.getParkingSlots();
        call.enqueue(new Callback<ParkingSlotResponse>() {
            @Override
            public void onResponse(@NonNull Call<ParkingSlotResponse> call, @NonNull retrofit2.Response<ParkingSlotResponse> response) {
                hideLoading();
                if (response.body() != null) {
                    list = response.body().getSensors();
                    Timber.e("list -> %s", new Gson().toJson(list));

                    parkingSlotResponse = response.body();

                    parkingSlotList = parkingSlotResponse.getSensors();

                    if (parkingSlotList != null) {
                        for (List<String> baseStringList : parkingSlotList) {
                            for (int i = 0; i < baseStringList.size(); i++) {

                                Timber.d("onResponse: i ->  %s", i);

                                if (i == 1) {
                                    parkingArea = baseStringList.get(i);
                                }

                                if (i == 0) {
                                    placeId = baseStringList.get(i);
                                }

                                if (i == 2) {
                                    endLat = Double.parseDouble(baseStringList.get(i).trim());
                                    Timber.e("endLat -> %s",endLat);
                                }

                                if (i == 3) {
                                    endLng = Double.parseDouble(baseStringList.get(i).trim());
                                    Timber.e("endLng -> %s",endLng);
                                }

                                if (i == 4) {
                                    count = baseStringList.get(i);
                                }

                                fetchDistance = ApplicationUtils.calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                                        endLat, endLng);
                                if (fetchDistance > 1.9) {
                                    fetchDistance = fetchDistance + 2;
                                  //  Timber.e("kim 1st if -> %s", kim);
                                } else if (fetchDistance < 1.9 && fetchDistance > 1) {
                                    fetchDistance = fetchDistance + 1;
                                  //  Timber.e("kim 2nd if-> %s", kim);
                                } else {
                                    fetchDistance = fetchDistance + 0.5;
                                  //  Timber.e("kim else-> %s", kim);
                                }
                            }
                            SensorArea sensorArea = new SensorArea(parkingArea, placeId, endLat, endLng, count, fetchDistance);

                            sensorArea.setDistance(fetchDistance);

                            sensorAreaArrayList.add(sensorArea);
                        }

                        Collections.sort(sensorAreaArrayList, new Comparator<SensorArea>() {
                            @Override
                            public int compare(SensorArea c1, SensorArea c2) {
                                return Double.compare(c1.getDistance(), c2.getDistance());
                            }
                        });

                        setFragmentControls(sensorAreaArrayList);
                    }
                }
            }

            @Override
            public void onFailure(Call<ParkingSlotResponse> call, Throwable t) {
                Timber.e("onFailure -> %s", t.getMessage());
                ApplicationUtils.showMessageDialog("Something went wrong...Please try later!", context);
            }
        });
    }

    private void setFragmentControls(ArrayList<SensorArea> sensorAreas) {
        this.sensorAreas = sensorAreas;
        recyclerViewParking.setHasFixedSize(true);
        recyclerViewParking.setItemViewCacheSize(20);
        recyclerViewParking.setNestedScrollingEnabled(false);
        recyclerViewParking.setMotionEventSplittingEnabled(false);

        recyclerViewParking.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerViewParking.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        recyclerViewParking.setItemAnimator(new DefaultItemAnimator());
        recyclerViewParking.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerViewParking, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                if (context != null) {

                    context.navigationView.getMenu().getItem(1).setChecked(false);

                    ApplicationUtils.hideKeyboard(context, editTextParking);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        ViewCompat.setNestedScrollingEnabled(recyclerViewParking, false);

        setAdapter(sensorAreas);
    }

    private long mLastClickTime = System.currentTimeMillis();

    private static final long CLICK_TIME_INTERVAL = 300;

    private void setAdapter(ArrayList<SensorArea> sensorAreas) {
        this.sensorAreas = sensorAreas;

        parkingAdapter = new ParkingAdapter(context, sensorAreas, onConnectedLocation, (position, lat, lng, parkingAreaName, count) -> {
            //Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();

            long now = System.currentTimeMillis();

            if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                return;
            }

            mLastClickTime = now;

            if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
                try {
                    Timber.e("try called");
                    SharedData.getInstance().setSensorArea(this.sensorAreas.get(position));
                } catch (Exception e) {
                    Timber.e("try catch called -> %s", e.getMessage());
                }
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", lat);
                Timber.e("lat -> %s", lat);
                bundle.putDouble("lng", lng);
                Timber.e("lng -> %s", lng);
                bundle.putString("areaName", parkingAreaName);
                bundle.putString("count", count);

                context.startActivityWithFinishBundle(HomeActivity.class, bundle);
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        });

        recyclerViewParking.setAdapter(parkingAdapter);

        /*bubbleSortArrayList(sensorAreas);

        parkingAdapter.setDataList(sensorAreas);*/
    }

    private void setNoDataForEnglish() {
        textViewNoData.setVisibility(View.VISIBLE);
        textViewNoData.setText(context.getResources().getString(R.string.no_nearest_parking_area_found));
        ApplicationUtils.showOnlyMessageDialog(context.getResources().getString(R.string.change_app_language_to_english), context);
    }

    private void setNoDataForBangla() {
        textViewNoData.setVisibility(View.VISIBLE);
        textViewNoData.setText(context.getResources().getString(R.string.no_nearest_parking_area_found));
        ApplicationUtils.showOnlyMessageDialog(context.getResources().getString(R.string.not_available_at_bangla_search), context);
    }

    ArrayList<SensorArea> filteredList = new ArrayList<>();

    private void filter(String text) {
        ArrayList<SensorArea> filteredList = new ArrayList<>();
        if (ApplicationUtils.checkInternet(context) && isGPSEnabled()) {

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
            }
        } else {
            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
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

    private void setNoData() {
        textViewNoData.setVisibility(View.VISIBLE);
        textViewNoData.setText(context.getString(R.string.no_nearest_parking_area_found));
    }

    private void hideNoData() {
        textViewNoData.setVisibility(View.GONE);
    }
}
