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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.response.sensors.SensorArea;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.RecyclerTouchListener;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;
import static www.fiberathome.com.parkingapp.utils.ApplicationUtils.calculateDistance;

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
            HomeFragment homeFragment = new HomeFragment();
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, homeFragment)
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

        imageViewBack.setOnClickListener(v -> {
            layoutVisible(false, "", "", 0.0, "", null);
        });

        btnGetDirection.setOnClickListener(v -> {
            Toast.makeText(context, "Parking Fragment theke geche", Toast.LENGTH_SHORT).show();
            //EventBus.getDefault().post(new GetDirectionEvent(location));
        });

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
                        if (Locale.getDefault().getLanguage().equals("en") && ApplicationUtils.textContainsBangla(contents)) {
                            setNoDataForBangla();
                            recyclerViewParking.setVisibility(View.GONE);
                            editTextParking.setText("");
                        } else if (Locale.getDefault().getLanguage().equals("bn") && ApplicationUtils.isEnglish(contents)) {
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
                if (Locale.getDefault().getLanguage().equals("en") && ApplicationUtils.textContainsBangla(s.toString())) {
                    setNoDataForBangla();
                    recyclerViewParking.setVisibility(View.GONE);
                    editTextParking.setText("");
                } else if (Locale.getDefault().getLanguage().equals("bn") && ApplicationUtils.isEnglish(s.toString())) {
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

    private void filter(String text) {
        ArrayList<SensorArea> filteredList = new ArrayList<>();
        if (ApplicationUtils.checkInternet(context) && isGPSEnabled()) {

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
            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
        }
    }

    private void setNoData() {
        textViewNoData.setVisibility(View.VISIBLE);
        textViewNoData.setText(context.getString(R.string.no_nearest_parking_area_found));
    }

    private void hideNoData() {
        textViewNoData.setVisibility(View.GONE);
    }

    private void layoutVisible(boolean isVisible, String name, String count, double distance, String duration, LatLng location) {
        this.name = name;
        this.count = count;
        this.location = location;
        this.distance = distance;
        this.duration = duration;

        if (isVisible) {
            linearLayoutBottom.setVisibility(View.VISIBLE);
            textViewParkingAreaCount.setText(count);
            textViewParkingAreaName.setText(name);
            textViewParkingDistance.setText(new DecimalFormat("##.##").format(distance) + " km");
            textViewParkingTravelTime.setText(duration);
        } else {
            linearLayoutBottom.setVisibility(View.GONE);
        }
    }

    private void fetchParkingSlotSensors() {
        Timber.e("fetchParkingSlotSensors called");

        if (!context.isFinishing())
            showLoading(context);

        ArrayList<SensorArea> sensorAreas = new ArrayList<>();

        if (SharedData.getInstance().getOnConnectedLocation() != null) {
            onConnectedLocation = SharedData.getInstance().getOnConnectedLocation();
        }

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_SENSOR_AREA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideLoading();

                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("sensors");
                    Timber.e("parkingFragment response -> %s", new Gson().toJson(jsonArray));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        SensorArea sensorArea = new SensorArea();

                        JSONArray array = jsonArray.getJSONArray(i);

                        try {
                            double fetchDistance = calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                                    Double.parseDouble(array.getString(2).trim()), Double.parseDouble(array.getString(3).trim()));

                            sensorArea.setParkingArea(array.getString(1).trim());
                            sensorArea.setLat(Double.parseDouble(array.getString(2).trim()));
                            sensorArea.setLng(Double.parseDouble(array.getString(3).trim()));
                            sensorArea.setCount(array.getString(4).trim());
                            sensorArea.setDistance(fetchDistance);

                            sensorAreas.add(sensorArea);
                            //sorting distance in ascending way
                            Collections.sort(sensorAreas, new Comparator<SensorArea>() {
                                @Override
                                public int compare(SensorArea c1, SensorArea c2) {
                                    return Double.compare(c1.getDistance(), c2.getDistance());
                                }
                            });
                        } catch (Exception e) {
                            e.getCause();
                        }
                    }
                    setFragmentControls(sensorAreas);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, e -> e.printStackTrace()) {
        };
        ParkingApp.getInstance().addToRequestQueue(strReq);
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

        setAdapter();
    }

    private long mLastClickTime = System.currentTimeMillis();

    private static final long CLICK_TIME_INTERVAL = 300;

    private void setAdapter() {
        parkingAdapter = new ParkingAdapter(context, sensorAreas, onConnectedLocation, new ParkingAdapter.ParkingAdapterClickListener() {
            @Override
            public void onItemClick(int position, double lat, double lng, String parkingAreaName, String count) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();

                long now = System.currentTimeMillis();

                if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                    return;
                }

                mLastClickTime = now;

                if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
                    try {
                        Timber.e("try called");
                        SharedData.getInstance().setSensorArea(sensorAreas.get(position));
                    } catch (Exception e) {
                        Timber.e("try catch called -> %s", e.getMessage());
                    }
                    Bundle bundle = new Bundle();
                    bundle.putDouble("lat", lat);
                    Timber.e("lat -> %s", lat);
                    bundle.putDouble("lng", lng);
                    Timber.e("lng -> %s", lng);
                    bundle.putString("areaName",parkingAreaName);
                    bundle.putString("count",count);
                    context.startActivity(HomeActivity.class, bundle);
                } else {
                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
                }
            }
        });
        recyclerViewParking.setAdapter(parkingAdapter);
    }

    private void updateAdapter() {
        if (parkingAdapter != null) {
            parkingAdapter = null;
        }
        setAdapter();
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
}
