package www.fiberathome.com.parkingapp.view.parking;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.AppConfig;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionEvent;
import www.fiberathome.com.parkingapp.eventBus.SetMarkerEvent;
import www.fiberathome.com.parkingapp.model.SensorArea;
import www.fiberathome.com.parkingapp.utils.OnEditTextRightDrawableTouchListener;
import www.fiberathome.com.parkingapp.utils.RecyclerTouchListener;
import www.fiberathome.com.parkingapp.data.preference.SharedData;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.view.activity.main.MainActivity;
import www.fiberathome.com.parkingapp.view.fragments.HomeFragment;

import static www.fiberathome.com.parkingapp.utils.ApplicationUtils.distance;

public class ParkingFragment extends Fragment implements ParkingAdapter.onItemClickListener {

    private static final String TAG = "ParkingFragment";

    @BindView(R.id.recyclerViewParking)
    RecyclerView recyclerViewParking;
    //    @BindView(R.id.swipeRefreshLayout)
//    SwipeRefreshLayout swipeRefreshLayout;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_parking, container, false);
        ButterKnife.bind(this, view);
        //set on text change listener for edittext
//        editTextParking.addTextChangedListener(textWatcher());
        initUI();
        setListeners();
        fetchAreas();
        return view;
    }

    private void initUI() {

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {

        imageViewBack.setOnClickListener(v -> {
//            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//            navBar.setVisibility(View.VISIBLE);
            layoutVisible(false, "", "", 0.0, "", null);
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
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//                if (charSequence.length() > 0) {
//                    ivClearSearchText.setVisibility(View.VISIBLE);
//                } else {
//                    ivClearSearchText.setVisibility(View.GONE);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    filter(s.toString());
                }

                //drawing cross button if text appears programmatically
                if (s.length() > 0) {
                    editTextParking.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0);
                } else {
                    editTextParking.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });
        //handle drawable cross button click listener programmatically
        editTextParking.setOnTouchListener(
                new OnEditTextRightDrawableTouchListener(editTextParking) {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void OnDrawableClick() {
                        // The right drawable was clicked. Your action goes here.
                        editTextParking.setText("");
                        fetchAreas();
                    }
                });

//        btn_clear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                editTextParking.setText("");
//            }
//        });
    }

    private void filter(String text) {
        ArrayList<SensorArea> filteredList = new ArrayList<>();

        for (SensorArea item : sensorAreas) {
            if (item.getParkingArea().toLowerCase().contains(text.toLowerCase()) || item.getCount().toLowerCase().contains(text.toLowerCase())) {
                hideNoData();
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
//            Toast.makeText(context, "No data", Toast.LENGTH_LONG).show();
            TastyToastUtils.showTastyErrorToast(context, "No data");
//            setNoData();
        }

        parkingAdapter.filterList(filteredList);
    }

    private void setNoData() {
        textViewNoData.setVisibility(View.VISIBLE);
//        textViewNoData.setText(context.getString(R.string.no_record_found));
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

//        Timber.e("name -> %s", name);
//        Timber.e("count -> %s", count);
//        Timber.e("location -> %s", location);
//        Timber.e("distance -> %s", distance);
//        Timber.e("isVisible -> %s", isVisible);
//        Timber.e("duration -> %s", duration);

        if (isVisible) {
//            Timber.e("isVisible True");
//            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//            navBar.setVisibility(View.GONE);
            linearLayoutBottom.setVisibility(View.VISIBLE);
            textViewParkingAreaCount.setText(count);
            textViewParkingAreaName.setText(name);
            textViewParkingDistance.setText(new DecimalFormat("##.##").format(distance) + " km");
            textViewParkingTravelTime.setText(duration);
        } else {
//            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//            navBar.setVisibility(View.VISIBLE);
            linearLayoutBottom.setVisibility(View.GONE);
        }
    }

    private Location onConnectedLocation = null;

    private void fetchAreas() {
        //initialize the progress dialog and show it
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching The Parking Slots....");
        progressDialog.show();
        ArrayList<SensorArea> sensorAreas = new ArrayList<>();

        if (SharedData.getInstance().getOnConnectedLocation() != null) {
            onConnectedLocation = SharedData.getInstance().getOnConnectedLocation();
        }

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_SENSOR_AREA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
//                Timber.e(ParkingFragment.class.getCanonicalName(), "" + response);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("sensors");
//                    Timber.e("jsonArray length parkingFragment-> %s", jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        SensorArea sensorArea = new SensorArea();
                        JSONArray array = jsonArray.getJSONArray(i);
//                        Timber.e("Array " + i, array.getString(1));

                        double fetchDistance = distance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                                Double.parseDouble(array.getString(2).trim()), Double.parseDouble(array.getString(3).trim()));
//                        Timber.e("parkingFragment fetchDistance -> %s", fetchDistance);
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

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewParking.setLayoutManager(mLayoutManager);
        recyclerViewParking.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        recyclerViewParking.setItemAnimator(new DefaultItemAnimator());
        recyclerViewParking.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerViewParking, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Movie movie = movieList.get(position);
//                Toast.makeText(context, position + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        ViewCompat.setNestedScrollingEnabled(recyclerViewParking, false);
        HomeFragment homeFragment = new HomeFragment();
        parkingAdapter = new ParkingAdapter(context, this, homeFragment, sensorAreas, onConnectedLocation);
        parkingAdapter.setClickListener(this);
        recyclerViewParking.setAdapter(parkingAdapter);
    }

    @Override
    public void onClick() {
        EventBus.getDefault().post(new GetDirectionEvent(HomeFragment.location));
        MainActivity parentActivity = (MainActivity) context;
        parentActivity.replaceFragment();
    }
}
