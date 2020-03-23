package www.fiberathome.com.parkingapp.ui.parking;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.AppConfig;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionEvent;
import www.fiberathome.com.parkingapp.model.SensorArea;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParkingFragment extends Fragment {

    private static final String TAG = "ParkingFRagment";

    @BindView(R.id.recyclerViewParking)
    RecyclerView recyclerViewParking;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.textViewNoData)
    TextView textViewNoData;
    @BindView(R.id.linearLayoutBottom)
    LinearLayout linearLayoutBottom;
    @BindView(R.id.editTextParking)
    EditText editTextParking;
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

    private Context context;
    private View view;
    private Handler handler;
    private ArrayList<SensorArea> sensorAreas;
    private ParkingAdapter parkingAdapter;
    public String name, count;
    public LatLng location;
    public double distance;
    public String duration;
    private ProgressDialog progressDialog;

    public ParkingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        context = getActivity();

        ParkingFragment fragment = new ParkingFragment(); //Your Fragment
        SensorArea sensorArea = new SensorArea(); // Your Object
        Bundle bundleParcelable = new Bundle();
        bundleParcelable.putParcelable("sensor", sensorArea); // Key, value
        fragment.setArguments(bundleParcelable);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_parking, container, false);
        ButterKnife.bind(this, view);
        initUI();
        setListeners();
        fetchAreas();
        return view;
    }

    private void initUI() {

    }

    private void setListeners() {
        imageViewBack.setOnClickListener(v -> {
            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
            navBar.setVisibility(View.VISIBLE);
            layoutVisible(false, "", "", 0.0, null);
        });

        btnGetDirection.setOnClickListener(v -> {
            Toast.makeText(context, "Parking Fragment theke geche", Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new GetDirectionEvent(location));
        });

        editTextParking.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    filter(s.toString());
                }
            }
        });
    }

    private void setFragmentControls(ArrayList<SensorArea> sensorAreas) {
        this.sensorAreas = sensorAreas;
        recyclerViewParking.setHasFixedSize(true);
        recyclerViewParking.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        parkingAdapter = new ParkingAdapter(context, this, sensorAreas);
        recyclerViewParking.setAdapter(parkingAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshing.run();
            }
        });
        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_dark, android.R.color.holo_blue_dark, android.R.color.holo_orange_light, android.R.color.holo_red_light);
    }

    private final Runnable refreshing = new Runnable() {
        public void run() {
            try {
                if (isRefreshing()) {
                    fetchAreas();
                    swipeRefreshStatus(false);
                    handler.postDelayed(this, 5000);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void filter(String text) {
        ArrayList<SensorArea> filteredList = new ArrayList<>();

        for (SensorArea item : sensorAreas) {
            if (item.getParkingArea().toLowerCase().contains(text.toLowerCase()) || item.getCount().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        parkingAdapter.filterList(filteredList);
    }

    private boolean isRefreshing() {
        return swipeRefreshLayout.isRefreshing();
    }


    public void setNoData() {
        textViewNoData.setVisibility(View.VISIBLE);
        textViewNoData.setText(context.getString(R.string.no_record_found));
    }

    public void swipeRefreshStatus(boolean status) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(status);
        }
    }

    public void hideNoData() {
        textViewNoData.setVisibility(View.GONE);
    }

    public void layoutVisible(boolean isVisible, String name, String count, double distance, LatLng location) {
        this.name = name;
        this.count = count;
        this.location = location;
        this.distance = distance;
        this.duration = duration;

        Timber.e("name -> %s", name);
        Timber.e("count -> %s", count);
        Timber.e("location -> %s", location);
        Timber.e("distance -> %s", distance);
        Timber.e("isVisible -> %s", isVisible);

//        ParkingFragment fragment = new ParkingFragment(); //Your Fragment
//        SensorArea car = new SensorArea(); // Your Object
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("carInfo", car); // Key, value
//        fragment.setArguments(bundle);

        if (isVisible) {
            Timber.e("isVisible True");
            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
            navBar.setVisibility(View.GONE);
            linearLayoutBottom.setVisibility(View.VISIBLE);
            textViewParkingAreaCount.setText(count);
            textViewParkingAreaName.setText(name);
            textViewParkingDistance.setText(new DecimalFormat("##.##").format(distance) + " km");
//            textViewParkingTravelTime.setText(duration[0]);
        } else {
            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
            navBar.setVisibility(View.VISIBLE);
            linearLayoutBottom.setVisibility(View.GONE);
        }
    }

    private void fetchAreas() {

        //initialize the progress dialog and show it
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching The Parking Slots....");
        progressDialog.show();
        ArrayList<SensorArea> sensorAreas = new ArrayList<>();

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_SENSOR_AREA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Timber.e(ParkingFragment.class.getCanonicalName(), "" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("sensors");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        SensorArea sensorArea = new SensorArea();
                        JSONArray array = jsonArray.getJSONArray(i);
                        Timber.e("Array " + i, array.getString(1));
                        sensorArea.setParkingArea(array.getString(1).trim());
                        sensorArea.setLat(Double.parseDouble(array.getString(2).trim()));
                        sensorArea.setLng(Double.parseDouble(array.getString(3).trim()));
                        sensorArea.setCount(array.getString(4).trim());

                        sensorAreas.add(sensorArea);
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
}
