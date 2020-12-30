package www.fiberathome.com.parkingapp.ui.parking;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.model.sensors.SensorArea;
import www.fiberathome.com.parkingapp.module.eventBus.GetDirectionEvent;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.RecyclerTouchListener;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;
import static www.fiberathome.com.parkingapp.utils.ApplicationUtils.distance;

@SuppressLint("NonConstantResourceId")
public class ParkingFragment extends BaseFragment implements ParkingAdapter.ParkingAdapterClickListener, IOnBackPressListener {

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

    private ProgressDialog progressDialog;

    private ArrayList<SensorArea> sensorAreas;

    private ParkingAdapter parkingAdapter;

    private HomeFragment homeFragment;

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
        context = (ParkingActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parking, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (ApplicationUtils.checkInternet(context) && isGPSEnabled()) {
            Timber.e("onCreateView if check e called");
            editTextParking.addTextChangedListener(filterTextWatcher);
        } else {
            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
        }

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
        return view;
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
        //dismissProgressDialog();
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
        //dismissProgressDialog();
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

    @Override
    public void onItemClick(int position) {
        Timber.e("parkingFragment onItemClick called");
        /*homeFragment = HomeFragment.newInstance();
        homeFragment.onParkingAdapterItemClickBottomSheetChanged(new LatLng(sensorAreas.get(position).getLat(),sensorAreas.get(position).getLng()));*/
        if (ApplicationUtils.checkInternet(context) && isGPSEnabled()) {
            Timber.e("parkingFragment onItemClick if called");
            //((MainActivity) context).onParkingAdapterItemClickBottomSheetChanged(new LatLng(sensorAreas.get(position).getLat(), sensorAreas.get(position).getLng()));

            //EventBus.getDefault().post(new GetDirectionEvent(new LatLng(sensorAreas.get(position).getLat(), sensorAreas.get(position).getLng())));
        } else {
            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {

        imageViewBack.setOnClickListener(v -> {
            layoutVisible(false, "", "", 0.0, "", null);
        });

        btnGetDirection.setOnClickListener(v -> {
            Toast.makeText(context, "Parking Fragment theke geche", Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new GetDirectionEvent(location));
        });

        ivClearSearchText.setOnClickListener(view -> {
            editTextParking.setText("");
            hideNoData();
            if (ApplicationUtils.checkInternet(context)) {
                fetchParkingSlotSensorsWithoutProgressBar();
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
                        if (SharedPreManager.getInstance(context).getLanguage().equalsIgnoreCase("English") && ApplicationUtils.textContainsBangla(contents)) {
                            setNoDataForBangla();
                            recyclerViewParking.setVisibility(View.GONE);
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
                        fetchParkingSlotSensorsWithoutProgressBar();
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
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > 0) {
                    ivClearSearchText.setVisibility(View.VISIBLE);
                } else {
                    ivClearSearchText.setVisibility(View.GONE);
                }
                if (SharedPreManager.getInstance(context).getLanguage().equalsIgnoreCase("English") && ApplicationUtils.textContainsBangla(charSequence.toString())) {
                    setNoDataForBangla();
                    recyclerViewParking.setVisibility(View.GONE);
                    editTextParking.setText("");
                } else {
                    recyclerViewParking.setVisibility(View.VISIBLE);
                    filter(charSequence.toString());
                    parkingAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    Timber.e("length 0 tcalled");
                    if (ApplicationUtils.checkInternet(context)) {
                        fetchParkingSlotSensorsWithoutProgressBar();
                    } else {
                        //TastyToastUtils.showTastyWarningToast(context, "Please connect to internet");
                    }
                }
            }
        });

        //handle special characters
        /*InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) // put your condition here
                        sb.append(c);
                    else
                        keepOriginal = false;
                }
                if (keepOriginal)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                return Character.isLetterOrDigit(c) || Character.isSpaceChar(c);
            }
        };
        editTextParking.setFilters(new InputFilter[]{filter});*/
    }

    private void setNoDataForBangla() {
        textViewNoData.setVisibility(View.VISIBLE);
        textViewNoData.setText(context.getResources().getString(R.string.no_record_found));
        ApplicationUtils.showOnlyMessageDialog(context.getResources().getString(R.string.not_available_at_bangla_search), context);
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            //if (!s.toString().equals("")) {
            //mAutoCompleteAdapter.getFilter().filter(s.toString());
            //}
            if (ApplicationUtils.checkInternet(context)) {
                parkingAdapter.notifyDataSetChanged();
            } else {
                //TastyToastUtils.showTastyWarningToast(context, "Please connect to internet");
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        //!s.toString().equals("")
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if (ApplicationUtils.checkInternet(context) && isGPSEnabled()) {
                    filter(s.toString());
                } else {
                    //TastyToastUtils.showTastyWarningToast(context, "Please connect to internet");
                }
            } else {
                if (ApplicationUtils.checkInternet(context) && isGPSEnabled()) {
                    parkingAdapter.notifyDataSetChanged();
                } else {
                    //TastyToastUtils.showTastyWarningToast(context, "Please connect to internet");
                }
            }
        }
    };

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
        textViewNoData.setText(context.getString(R.string.no_record_found));
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

    private Location onConnectedLocation = null;

    private void fetchParkingSlotSensors() {
        Timber.e("fetchParkingSlotSensors called");

        /*progressDialog = ApplicationUtils.progressDialog(getActivity(),
                "Please wait...");*/
        if (!context.isFinishing())
            showLoading(context);

        ArrayList<SensorArea> sensorAreas = new ArrayList<>();

        if (SharedData.getInstance().getOnConnectedLocation() != null) {
            onConnectedLocation = SharedData.getInstance().getOnConnectedLocation();
        }

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_SENSOR_AREA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //progressDialog.dismiss();
                hideLoading();
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("sensors");
                    Timber.e("parkingFragment response -> %s", new Gson().toJson(jsonArray));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        SensorArea sensorArea = new SensorArea();
                        JSONArray array = jsonArray.getJSONArray(i);
                        //Timber.e("Array " + i, array.getString(1));

                        try {
                            double fetchDistance = distance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                                    Double.parseDouble(array.getString(2).trim()), Double.parseDouble(array.getString(3).trim()));
                            //Timber.e("parkingFragment fetchDistance -> %s", fetchDistance);
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        }) {

        };
        ParkingApp.getInstance().addToRequestQueue(strReq);
    }

    private void fetchParkingSlotSensorsWithoutProgressBar() {

        ArrayList<SensorArea> sensorAreas = new ArrayList<>();

        if (SharedData.getInstance().getOnConnectedLocation() != null) {
            onConnectedLocation = SharedData.getInstance().getOnConnectedLocation();
        }

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_SENSOR_AREA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("sensors");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        SensorArea sensorArea = new SensorArea();
                        JSONArray array = jsonArray.getJSONArray(i);

                        double fetchDistance = distance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
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
                    }
                    setFragmentControls(sensorAreas);
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

    private void setFragmentControls(ArrayList<SensorArea> sensorAreas) {
        this.sensorAreas = sensorAreas;
        recyclerViewParking.setHasFixedSize(true);
        recyclerViewParking.setItemViewCacheSize(20);
        recyclerViewParking.setNestedScrollingEnabled(false);
        recyclerViewParking.setMotionEventSplittingEnabled(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerViewParking.setLayoutManager(mLayoutManager);
        recyclerViewParking.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        recyclerViewParking.setItemAnimator(new DefaultItemAnimator());
        recyclerViewParking.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerViewParking, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(context, position + " is selected!", Toast.LENGTH_SHORT).show();
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
        parkingAdapter = new ParkingAdapter(context, this, homeFragment, sensorAreas, onConnectedLocation, this);
        //parkingAdapter.setClickListener(this);
        recyclerViewParking.setAdapter(parkingAdapter);
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

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
