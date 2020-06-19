package www.fiberathome.com.parkingapp.view.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.GoogleMapWebServiceNDistance.DirectionsParser;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.AppConfig;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.data.retrofit.Common;
import www.fiberathome.com.parkingapp.data.retrofit.IGoogleApi;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionAfterButtonClickEvent;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionBottomSheetEvent;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionForMarkerEvent;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionForSearchEvent;
import www.fiberathome.com.parkingapp.eventBus.SetMarkerEvent;
import www.fiberathome.com.parkingapp.model.BookingSensors;

import www.fiberathome.com.parkingapp.model.SelcectedPlace;
import www.fiberathome.com.parkingapp.view.activity.search.SearchActivity;

import www.fiberathome.com.parkingapp.model.SensorArea;

import www.fiberathome.com.parkingapp.view.booking.ScheduleFragment;
import www.fiberathome.com.parkingapp.view.booking.adapter.BookingSensorAdapter;
import www.fiberathome.com.parkingapp.view.booking.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.view.bottomSheet.BottomSheetAdapter;
import www.fiberathome.com.parkingapp.preference.AppConstants;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.GpsUtils;
import www.fiberathome.com.parkingapp.utils.RecyclerTouchListener;

import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import static www.fiberathome.com.parkingapp.utils.AppConstants.FIRST_TIME_INSTALLED;
import static www.fiberathome.com.parkingapp.utils.AppConstants.NEW_PLACE_SELECTED;
import static www.fiberathome.com.parkingapp.utils.AppConstants.NEW_SEARCH_ACTIVITY_REQUEST_CODE;


import www.fiberathome.com.parkingapp.data.preference.SharedData;

/**
 * A simple {@link Fragment} subclass.
 */

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, GoogleMap.OnMarkerClickListener {

//        GoogleMap.OnCameraIdleListener,
//        GoogleMap.OnCameraMoveCanceledListener,
//        GoogleMap.OnCameraMoveListener,
//        GoogleMap.OnCameraMoveStartedListener

//    GoogleMap.OnInfoWindowClickListener,

    private final String TAG = getClass().getSimpleName();

    //from parking adapter
    @BindView(R.id.btnGetDirection)
    Button btnGetDirection;
    @BindView(R.id.linearLayoutParkingAdapterBackBottom)
    LinearLayout linearLayoutParkingAdapterBackBottom;
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
    @BindView(R.id.linearLayoutBottom)
    LinearLayout linearLayoutBottom;
    @BindView(R.id.linearLayoutNameCount)
    LinearLayout linearLayoutNameCount;

    //from search
    @BindView(R.id.btnSearchGetDirection)
    Button btnSearchGetDirection;
    @BindView(R.id.imageViewSearchBack)
    ImageView imageViewSearchBack;
    @BindView(R.id.textViewSearchParkingAreaCount)
    TextView textViewSearchParkingAreaCount;
    @BindView(R.id.textViewSearchParkingAreaName)
    TextView textViewSearchParkingAreaName;
    @BindView(R.id.textViewSearchParkingDistance)
    TextView textViewSearchParkingDistance;
    @BindView(R.id.textViewSearchParkingTravelTime)
    TextView textViewSearchParkingTravelTime;
    @BindView(R.id.linearLayoutSearchBottom)
    public LinearLayout linearLayoutSearchBottom;
    @BindView(R.id.linearLayoutSearchBottomButton)
    public LinearLayout linearLayoutSearchBottomButton;
    @BindView(R.id.linearLayoutSearchNameCount)
    LinearLayout linearLayoutSearchNameCount;

    //from marker
    @BindView(R.id.btnMarkerGetDirection)
    Button btnMarkerGetDirection;
    @BindView(R.id.imageViewMarkerBack)
    ImageView imageViewMarkerBack;
    @BindView(R.id.textViewMarkerParkingAreaCount)
    TextView textViewMarkerParkingAreaCount;
    @BindView(R.id.textViewMarkerParkingAreaName)
    TextView textViewMarkerParkingAreaName;
    @BindView(R.id.textViewMarkerParkingDistance)
    TextView textViewMarkerParkingDistance;
    @BindView(R.id.textViewMarkerParkingTravelTime)
    TextView textViewMarkerParkingTravelTime;
    @BindView(R.id.linearLayoutMarkerBottom)
    LinearLayout linearLayoutMarkerBottom;
    @BindView(R.id.linearLayoutMarkerNameCount)
    LinearLayout linearLayoutMarkerNameCount;

    //from bottomSheet
    @BindView(R.id.btnBottomSheetGetDirection)
    Button btnBottomSheetGetDirection;
    @BindView(R.id.imageViewBottomSheetBack)
    ImageView imageViewBottomSheetBack;
    @BindView(R.id.textViewBottomSheetParkingAreaCount)
    TextView textViewBottomSheetParkingAreaCount;
    @BindView(R.id.textViewBottomSheetParkingAreaName)
    TextView textViewBottomSheetParkingAreaName;
    @BindView(R.id.textViewBottomSheetParkingDistance)
    public TextView textViewBottomSheetParkingDistance;
    @BindView(R.id.textViewBottomSheetParkingTravelTime)
    public TextView textViewBottomSheetParkingTravelTime;
    @BindView(R.id.linearLayoutBottomSheetBottom)
    LinearLayout linearLayoutBottomSheetBottom;
    @BindView(R.id.linearLayoutBottomSheetNameCount)
    LinearLayout linearLayoutBottomSheetNameCount;
    @BindView(R.id.linearLayoutBottomSheetGetDirection)
    LinearLayout linearLayoutBottomSheetGetDirection;
    @BindView(R.id.view)
    View view;

    private LinearLayout bottomSheet;
    private FragmentChangeListener listener;
    private long arrived, departure;
    private TextView arrivedtimeTV, departuretimeTV, timeDifferenceTV, countDownTV, textViewTermsCondition;
    private long difference;
    private Button moreBtn, btnLiveParking;
    private LinearLayout bookedLayout;
    //    private boolean fromScheduleFragment=false;
    private Button departureBtn;

    private RecyclerView bottomSheetRecyclerView;
    //private BottomSheetSensorAdapter bottomSheetSensorAdapter;
    private BottomSheetAdapter bottomSheetAdapter;

    private int LOCATION_PERMISSION_REQUEST_CODE = 100;
    public BottomSheetBehavior bottomSheetBehavior;
    public LatLng searchPlaceLatLng;
    public int bottomSheetSearch;
    public static Location currentLocation;
    private Marker currentLocationMarker;
    public LatLng bottomSheetPlaceLatLng;
    public static LatLng location;
    public LatLng markerPlaceLatLng;
    private ArrayList<LatLng> coordList = new ArrayList<LatLng>();
    //private GoogleMap googleMap;

    private String nearByDuration;
    private String fetchDuration;
    private SupportMapFragment supportMapFragment;
    private double nearByDistance;

    private Context context;
    private String name, count = "";
    private String distance;
    private String duration;
    public String address, city, state, country, subAdminArea, test, knownName, postalCode = "";
    private boolean isGPS;
    public GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    //private Location mlastlocation;
    private LocationRequest locationRequest;

    //used in fetchSensor()
    public double nDistance = 132116456;
    public double nLatitude;
    public double nLongitude;
    private String sensorStatus = "Occupied";

    private SearchView searchView;
    private Button buttonSearch;

    //flags
    private int getDirectionButtonClicked = 0;
    private int getDirectionSearchButtonClicked = 0;
    private int getDirectionMarkerButtonClicked = 0;
    private int getDirectionBottomSheetButtonClicked = 0;
    private ProgressDialog progressDialog;
    private ProgressDialog bottomSheetProgressDialog;
    private int markerAlreadyClicked = 0;
    private int fromMarkerRouteDrawn = 0;
    //route flag
    private int flag = 0;

    //polyline animate
    private List<LatLng> polyLineList;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, grayPolyline;

    private IGoogleApi mService;
    private float v;
    private double lat, lng;
    private Handler handler;
    private LatLng startPosition, endPosition;
    private int index, next;


    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Timber.e("onCreateView called");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        ButterKnife.bind(this, view);
        initUI(view);
        setListeners();

        bottomSheet = view.findViewById(R.id.layout_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(400);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
//                        layoutBottomSheetVisible(false, "", "", "", "", null);
//                        btn.setText("Close Sheet");
                    case BottomSheetBehavior.STATE_COLLAPSED:
//                        if (mMap != null) {
//                            mMap.clear();
//                            animateCamera(onConnectedLocation);
//                            fetchSensors(onConnectedLocation);
//                            bookingSensorsArrayListGlobal.clear();
//                            bookingSensorsArrayList.clear();
//                            bookingSensorsMarkerArrayList.clear();
//                            fetchBottomSheetSensors(onConnectedLocation);
//                        }
//                        btn.setText("Expand Sheet");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
//                        layoutBottomSheetVisible(false, "", "", "", "", null);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
//                        layoutBottomSheetVisible(false, "", "", "", "", null);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float slideOffset) {
                if (isAdded()) {
//                    bottomSheetAdapter.isItemClicked = false;
                }
            }
        });

        if (getArguments() != null) {
            if (getArguments().getBoolean("s")) {
                bookedLayout.setVisibility(View.VISIBLE);
                arrived = getArguments().getLong("arrived", 0);
                departure = getArguments().getLong("departure", 0);
                difference = departure - arrived;

                setTimer(difference);

                Log.d(TAG, "onCreateView: " + arrived + "    " + departure);
                Log.d(TAG, "onCreateView: difference:" + difference);

            }
        }

        listener = (FragmentChangeListener) getActivity();

        polyLineList = new ArrayList<>();
        mService = Common.getGoogleApi();

        return view;
    }

    private void initUI(View view) {
        view.findViewById(R.id.currentLocationImageButton).setOnClickListener(clickListener);
        buttonSearch = view.findViewById(R.id.input_search);
        bottomSheetRecyclerView = view.findViewById(R.id.bottomsheet_recyclerview);
        //for booking
        arrivedtimeTV = view.findViewById(R.id.arrivedtimeTV);
        departuretimeTV = view.findViewById(R.id.departureTimeTV);
        timeDifferenceTV = view.findViewById(R.id.timeDifferenceTV);
        countDownTV = view.findViewById(R.id.countDownTV);
        moreBtn = view.findViewById(R.id.moreBtn);
        btnLiveParking = view.findViewById(R.id.btnLiveParking);
        textViewTermsCondition = view.findViewById(R.id.textViewTermsCondition);
        bookedLayout = view.findViewById(R.id.bookedLayout);
        departureBtn = view.findViewById(R.id.departureBtn);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        supportMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.map, supportMapFragment);
        ft.commit();

        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setMessage("Initializing....");

        new GpsUtils(getContext()).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        if ((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            /*ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission
                    .ACCESS_COARSE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);*/
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            Log.d(TAG, "onViewCreated: in if");
        } else {
            supportMapFragment.getMapAsync(this);
            Log.d(TAG, "onViewCreated: in else");
        }

        arrivedtimeTV.setText("Arrived " + getDate(arrived));
        departuretimeTV.setText("Departure " + getDate(departure));
        timeDifferenceTV.setText(getTimeDiffrence(difference) + " min");
        //dekhte hobee eta koi boshbe
        if (getDirectionButtonClicked == 0) {
            linearLayoutParkingAdapterBackBottom.setOnClickListener(v -> {
                ApplicationUtils.showMessageDialog("Once reach your destination you can reserve your booking spot!!!", context);
            });
        } else {
            linearLayoutParkingAdapterBackBottom.setOnClickListener(v -> {
                ApplicationUtils.showMessageDialog("Hey Shawn!!!", context);
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: on Map Ready");

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            Log.d(TAG, "onMapReady: if cond");
        }

        //changing map style
//        try {
//            boolean isSuccess = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.uber_map_style20));
//
//            if (!isSuccess) {
//                Timber.e("Error...Map Style load failed!!!");
//            }
//        } catch (Resources.NotFoundException e) {
//            e.printStackTrace();
//        }

        mMap = googleMap;
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.setMyLocationEnabled(false);//if false it remove the blue dot over icon
        mMap.setBuildingsEnabled(false);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(false);
        buildGoogleApiClient();
//        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);
//        mMap.setOnCameraIdleListener(this);
//        mMap.setOnCameraMoveStartedListener(this);
//        mMap.setOnCameraMoveListener(this);
//        mMap.setOnCameraMoveCanceledListener(this);
    }

//    @Override
//    public void onCameraMoveStarted(int reason) {
//
//        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
//            Toast.makeText(context, "The user gestured on the map.",
//                    Toast.LENGTH_SHORT).show();
//        } else if (reason == GoogleMap.OnCameraMoveStartedListener
//                .REASON_API_ANIMATION) {
//            Toast.makeText(context, "The user tapped something on the map.",
//                    Toast.LENGTH_SHORT).show();
//        } else if (reason == GoogleMap.OnCameraMoveStartedListener
//                .REASON_DEVELOPER_ANIMATION) {
//            Toast.makeText(context, "The app moved the camera.",
//                    Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onCameraMove() {
//        Toast.makeText(context, "The camera is moving.",
//                Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onCameraMoveCanceled() {
//        Toast.makeText(context, "Camera movement canceled.",
//                Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onCameraIdle() {
//        Toast.makeText(context, "The camera has stopped moving.",
//                Toast.LENGTH_SHORT).show();
//    }

    //    @Override
//    public void onInfoWindowClick(Marker marker) {
//        bottomSheetBehavior.setPeekHeight(400);
//        Timber.e("onInfoWindowClick -> %s", marker.getTitle());
//
////        markerAlreadyClicked = 1;
//        if (markerAlreadyClicked == 1) {
//            if (markerAlreadyClicked == 1 && fromMarkerRouteDrawn == 0) {
//                bottomSheetBehavior.setPeekHeight(400);
//                ApplicationUtils.showMessageDialog("Please try again!!!", context);
//            }
//            if (mMap != null)
//                mMap.clear();
//            fetchSensors(onConnectedLocation);
//            bookingSensorsArrayListGlobal.clear();
//            fetchBottomSheetSensors(onConnectedLocation);
//            if (getActivity() != null) {
//                BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//                navBar.setVisibility(View.VISIBLE);
//            }
//            linearLayoutMarkerBottom.setVisibility(View.GONE);
//            linearLayoutBottom.setVisibility(View.GONE);
//            linearLayoutSearchBottom.setVisibility(View.GONE);
//            btnMarkerGetDirection.setText("Get Direction");
//            btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
//            getDirectionMarkerButtonClicked = 0;
//            if (fromMarkerRouteDrawn == 1) {
////                && markerAlreadyClicked == 1
//                ApplicationUtils.showMessageDialog("You have already selected a parking slot! \nPlease try again!", context);
//                fetchSensors(onConnectedLocation);
//                bottomSheetBehavior.setPeekHeight(400);
//            } else {
//                fromMarkerRouteDrawn = 0;
//                fetchSensors(onConnectedLocation);
//            }
//            markerAlreadyClicked = 0;
//            Timber.e("onInfoWindowClick markerAlreadyClicked -> %s", markerAlreadyClicked);
//        } else {
//            Timber.e("onInfoWindowClick else markerAlreadyClicked -> %s", markerAlreadyClicked);
//            if (mMap != null)
//                mMap.clear();
//            fetchSensors(onConnectedLocation);
//            bookingSensorsArrayListGlobal.clear();
//            fetchBottomSheetSensors(onConnectedLocation);
//            String spotstatus = marker.getSnippet();
//            String spotid = marker.getTitle();
//
//            //calculate Duration
//            markerPlaceLatLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
//            getDestinationInfoForDuration(markerPlaceLatLng);
//
//            for (int i = 0; i < clickEventJsonArray.length(); i++) {
//                JSONObject jsonObject;
//                try {
//                    jsonObject = clickEventJsonArray.getJSONObject(i);
//                    String latitude1 = jsonObject.get("latitude").toString();
//                    String longitude1 = jsonObject.get("longitude").toString();
//                    String uid = jsonObject.get("uid").toString();
//
////                    if(spotid.equalsIgnoreCase(uid)){
////                        String parkingNumberOfIndividualMarker = jsonObject.get("no_of_parking").toString();
////                        textViewMarkerParkingAreaCount.setText(parkingNumberOfIndividualMarker);
////                        Timber.e("parkingNumberOfIndividualMarker -> %s", parkingNumberOfIndividualMarker);
////                    }
//                    double distanceForCount = calculateDistance(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude, ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));
//                    Timber.e("DistanceForCount -> %s", distanceForCount);
//                    if (distanceForCount < 0.1) {
//                        String parkingNumberOfIndividualMarker = jsonObject.get("no_of_parking").toString();
//                        textViewMarkerParkingAreaCount.setText(parkingNumberOfIndividualMarker);
//                        Timber.e("parkingNumberOfIndividualMarker -> %s", parkingNumberOfIndividualMarker);
//                        break;
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            getAddress(getActivity(), markerPlaceLatLng.latitude, markerPlaceLatLng.longitude);
//            String searchPlaceName = address;
//            TaskParser taskParser = new TaskParser();
//            double markerDistance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
//                    new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
////            layoutMarkerVisible(true, searchPlaceName, "1", ApplicationUtils.distance(currentLocation.getLatitude(), currentLocation.getLongitude(), marker.getPosition().latitude, marker.getPosition().longitude), marker.getPosition());
//            layoutMarkerVisible(true, searchPlaceName, textViewMarkerParkingAreaCount.getText().toString(),
//                    String.valueOf(markerDistance), marker.getPosition());
//            Timber.e("layoutMarkerVisible textViewMarkerParkingAreaCount -> %s", textViewMarkerParkingAreaCount.getText().toString());
//            markerAlreadyClicked++;
//            Timber.e("onInfoWindowClick else last markerAlreadyClicked -> %s", markerAlreadyClicked);
////            markerAlreadyClicked = 0;
////        }
////        markerAlreadyClicked = 0;
////            if (spotstatus.equalsIgnoreCase("Empty") || spotstatus.equalsIgnoreCase("Occupied.")) {
////                //Toast.makeTextHome(getContext(),"Sensor details will be shown here..",Toast.LENGTH_SHORT);
////
////                selectedSensor = marker.getTitle();
////                Timber.e("openDialog");
////                openDialog(selectedSensor);
////
////                //R.id.nearest:
////                // get the nearest sensor information
////                selectedSensor = marker.getTitle();
////
////                //parkingReqSpot.setText(selectedSensor.toString());
////
////                selectedSensorStatus = "Empty";
////
////                nearest.setText("Reverse Spot");
////                Toast.makeText(getContext(), marker.getTitle() + " Is Selected For Reservation!", Toast.LENGTH_SHORT).show();
////
////                Timber.e("Sensor details will be shown here..");
////            } else {
////                nearest.setText("Find Nearest");
////                selectedSensorStatus = "Occupied";
////                Toast.makeText(getContext(), marker.getTitle() + " Is already occupied, please an empty parking space.", Toast.LENGTH_LONG).show();
////            }
//        }
//    }

    private String parkingNumberOfIndividualMarker = "";
    private BookingSensors bookingSensorsMarker;
    private ArrayList<BookingSensors> bookingSensorsMarkerArrayList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onMarkerClick(Marker marker) {
        linearLayoutMarkerBottom.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setPeekHeight(400);
        //Timber.e("onMarkerClick -> %s", marker.getTitle());

        if (markerAlreadyClicked == 1) {
            if (fromMarkerRouteDrawn == 0) {
                bottomSheetBehavior.setPeekHeight(400);
                ApplicationUtils.showMessageDialog("Please try again!!!", context);
            }
            if (mMap != null)
                mMap.clear();
            fetchSensors(onConnectedLocation);
            bookingSensorsArrayListGlobal.clear();
            fetchBottomSheetSensors(onConnectedLocation);
//            if (getActivity() != null) {
//                BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//                navBar.setVisibility(View.VISIBLE);
//            }
            linearLayoutMarkerBottom.setVisibility(View.GONE);
            linearLayoutBottom.setVisibility(View.GONE);
            linearLayoutSearchBottom.setVisibility(View.GONE);
            btnMarkerGetDirection.setText("Get Direction");
            btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
            getDirectionMarkerButtonClicked = 0;
            if (fromMarkerRouteDrawn == 1) {
                ApplicationUtils.showMessageDialog("You have already selected a parking slot! \nPlease try again!", context);
                fetchSensors(onConnectedLocation);
                bottomSheetBehavior.setPeekHeight(400);
            } else {
                fromMarkerRouteDrawn = 0;
                fetchSensors(onConnectedLocation);
            }
            markerAlreadyClicked = 0;
            //Timber.e("onInfoWindowClick markerAlreadyClicked -> %s", markerAlreadyClicked);
        } else {
            //Timber.e("onInfoWindowClick else markerAlreadyClicked -> %s", markerAlreadyClicked);
            if (mMap != null)
                mMap.clear();
            fetchSensors(onConnectedLocation);
            bookingSensorsArrayListGlobal.clear();
//            fetchBottomSheetSensors(onConnectedLocation);
            String spotstatus = marker.getSnippet();
            String spotid = marker.getTitle();

            //calculate Duration
            markerPlaceLatLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            getDestinationInfoForDuration(markerPlaceLatLng);

            for (int i = 0; i < clickEventJsonArray.length(); i++) {
                JSONObject jsonObject;
                try {
                    jsonObject = clickEventJsonArray.getJSONObject(i);
                    String latitude1 = jsonObject.get("latitude").toString();
                    String longitude1 = jsonObject.get("longitude").toString();
                    String uid = jsonObject.get("uid").toString();

//                    if(spotid.equalsIgnoreCase(uid)){
//                        String parkingNumberOfIndividualMarker = jsonObject.get("no_of_parking").toString();
//                        textViewMarkerParkingAreaCount.setText(parkingNumberOfIndividualMarker);
//                        Timber.e("parkingNumberOfIndividualMarker -> %s", parkingNumberOfIndividualMarker);
//                    }
                    double distanceForCount = calculateDistance(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude, ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));
                    //Timber.e("DistanceForCount -> %s", distanceForCount);
                    if (distanceForCount < 0.1) {
                        parkingNumberOfIndividualMarker = jsonObject.get("no_of_parking").toString();
//                        textViewMarkerParkingAreaCount.setText(parkingNumberOfIndividualMarker);
//                        Timber.e("parkingNumberOfIndividualMarker -> %s", parkingNumberOfIndividualMarker);
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            getAddress(getActivity(), markerPlaceLatLng.latitude, markerPlaceLatLng.longitude);
            String markerPlaceName = address;
            TaskParser taskParser = new TaskParser();
            double markerDistance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                    new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
            layoutMarkerVisible(false, markerPlaceName, parkingNumberOfIndividualMarker,
                    String.valueOf(markerDistance), marker.getPosition());
            //Timber.e("layoutMarkerVisible textViewMarkerParkingAreaCount -> %s", parkingNumberOfIndividualMarker);
            markerAlreadyClicked++;
            //Timber.e("onInfoWindowClick else last markerAlreadyClicked -> %s", markerAlreadyClicked);
//            markerAlreadyClicked = 0;
//        }
//        markerAlreadyClicked = 0;
//            if (spotstatus.equalsIgnoreCase("Empty") || spotstatus.equalsIgnoreCase("Occupied.")) {
//                //Toast.makeTextHome(getContext(),"Sensor details will be shown here..",Toast.LENGTH_SHORT);
//
//                selectedSensor = marker.getTitle();
//                Timber.e("openDialog");
//                openDialog(selectedSensor);
//
//                //R.id.nearest:
//                // get the nearest sensor information
//                selectedSensor = marker.getTitle();
//
//                //parkingReqSpot.setText(selectedSensor.toString());
//
//                selectedSensorStatus = "Empty";
//
//                nearest.setText("Reverse Spot");
//                Toast.makeText(getContext(), marker.getTitle() + " Is Selected For Reservation!", Toast.LENGTH_SHORT).show();
//
//                Timber.e("Sensor details will be shown here..");
//            } else {
//                nearest.setText("Find Nearest");
//                selectedSensorStatus = "Occupied";
//                Toast.makeText(getContext(), marker.getTitle() + " Is already occupied, please an empty parking space.", Toast.LENGTH_LONG).show();
//            }
        }

        if (markerPlaceLatLng != null) {
            if (mMap != null)
                mMap.clear();
            fetchSensors(onConnectedLocation);
//            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//            navBar.setVisibility(View.VISIBLE);
//            bottomSheetSearch = 0;
            //for getting the location name
            getAddress(getActivity(), markerPlaceLatLng.latitude, markerPlaceLatLng.longitude);
            String markerPlaceName = address;
            //   Timber.e("searchPlaceName -> %s", markerPlaceName);

            TaskParser taskParser = new TaskParser();
            double markerDistance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                    new LatLng(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude));
//            Timber.e("searchDistance -> %s", markerDistance);
//            layoutMarkerVisible(true, markerPlaceName, parkingNumberOfIndividualMarker,
//                    String.valueOf(markerDistance), marker.getPosition());

            layoutMarkerVisible(true, "", "",
                    "", marker.getPosition());

            bottomSheetBehavior.setPeekHeight(400);

            if (markerDistance < 3000) {
                adjustValue = 1;
            }

            double kim = (markerDistance / 1000) + adjustValue;
//            Timber.e("adjustValue first -> %s", adjustValue);
            double markerDoubleDuration = Double.parseDouble(new DecimalFormat("##.##").format(markerDistance * 2.43));
            String markerStringDuration = markerDoubleDuration + " mins";

            bookingSensorsMarker = new BookingSensors(markerPlaceName, markerPlaceLatLng.latitude, markerPlaceLatLng.longitude,
                    markerDistance,
                    parkingNumberOfIndividualMarker, markerStringDuration);
//            Timber.e("bookingSensorsMarker first -> %s", new Gson().toJson(bookingSensorsMarker));
//                        bookingSensorsArrayList.add(bookingSensors);
            bookingSensorsMarkerArrayList.add(new BookingSensors(markerPlaceName, markerPlaceLatLng.latitude, markerPlaceLatLng.longitude,
                    markerDistance, parkingNumberOfIndividualMarker, markerStringDuration,
                    context.getResources().getString(R.string.nearest_parking_from_your_destination),
                    BookingSensors.TEXT_INFO_TYPE, 0));

            for (int i = 0; i < clickEventJsonArray.length(); i++) {
                JSONObject jsonObject;
                try {
                    jsonObject = clickEventJsonArray.getJSONObject(i);
                    String latitude1 = jsonObject.get("latitude").toString();
                    String longitude1 = jsonObject.get("longitude").toString();

                    double distanceForNearbyLoc = calculateDistance(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude,
                            ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));
                    Timber.e("DistanceForNearbyLoc -> %s", distanceForNearbyLoc);

                    if (distanceForNearbyLoc < 3) {
//                        bottomSheetSearch = 1;
                        origin = new LatLng(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude);
                        getAddress(getActivity(), ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));
                        String nearbyAreaName = address;
                        String parkingNumberOfNearbyDistanceLoc = jsonObject.get("no_of_parking").toString();
                        Timber.e("nearbyDistance nearByDuration -> %s -> %s", nearByDistance, nearByDuration);
                        bookingSensorsMarker = new BookingSensors(nearbyAreaName, ApplicationUtils.convertToDouble(latitude1),
                                ApplicationUtils.convertToDouble(longitude1), nearByDistance, parkingNumberOfNearbyDistanceLoc);
                        Timber.e("bookingSensors nearest marker location -> %s", new Gson().toJson(bookingSensorsMarker));

                        int adjsutNearbyValue = 2;
                        if (distanceForNearbyLoc < 1000) {
                            adjsutNearbyValue = 1;
                        }

                        double km = (distanceForNearbyLoc / 1000) + adjsutNearbyValue;
                        double nearbySearchDoubleDuration = Double.parseDouble(new DecimalFormat("##.##").format(km * 2.43));
                        String nearbySearchStringDuration = nearbySearchDoubleDuration + " mins";

                        bookingSensorsMarkerArrayList.add(new BookingSensors(nearbyAreaName, ApplicationUtils.convertToDouble(latitude1),
                                ApplicationUtils.convertToDouble(longitude1), distanceForNearbyLoc, parkingNumberOfNearbyDistanceLoc,
                                nearbySearchStringDuration,
                                BookingSensors.INFO_TYPE, 1));

                        bubbleSortArrayList(bookingSensorsMarkerArrayList);
                        bottomSheetBehavior.setPeekHeight(400);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            Timber.e("bookingSensorsMarkerArrayList latest -> %s", new Gson().toJson(bookingSensorsMarkerArrayList));
            //bottomSheetSensorAdapter.updateData(bookingSensorsMarkerArrayList);
            bottomSheetAdapter.updateData(bookingSensorsMarkerArrayList);
            setBottomSheetRecyclerViewAdapter(bookingSensorsMarkerArrayList);
//            Timber.e("setBottomSheetRecyclerViewAdapter(bookingSensorsMarkerArrayList) call hoiche for loop");
        }

        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();

    }

    public Location onConnectedLocation;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        Log.d(TAG, "onConnected: ");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        onConnectedLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (onConnectedLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
        } else {
            //handleNewLocation(location);
            LatLng latLng = new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13.5f));
            animateCamera(onConnectedLocation);
            fetchSensors(onConnectedLocation);
            fetchBottomSheetSensors(onConnectedLocation);
            SharedData.getInstance().setOnConnectedLocation(onConnectedLocation);
        }

        //value getting from parking adapter
        if (SharedData.getInstance().getSensorArea() != null) {
            bottomSheetBehavior.setPeekHeight(400);
            SensorArea sensorArea = SharedData.getInstance().getSensorArea();
            //   Timber.e("Sensor Area from SharedData -> %s", new Gson().toJson(sensorArea));
            textViewParkingAreaName.setText(ApplicationUtils.capitalize(sensorArea.getParkingArea()));
            textViewParkingAreaCount.setText(sensorArea.getCount());
            String distance = new DecimalFormat("##.##").format(sensorArea.getDistance()) + " km";
            textViewParkingDistance.setText(distance);
//            textViewParkingTravelTime.setText(sensorArea.getDuration());
            getDestinationInfoForDuration(new LatLng(sensorArea.getLat(), sensorArea.getLng()));
        } else {
//            Timber.e("Genjam");
        }
    }

    private final View.OnClickListener clickListener = view -> {
        if (view.getId() == R.id.currentLocationImageButton && mMap != null && onConnectedLocation != null)
            animateCamera(onConnectedLocation);
    };

    public void animateCamera(@NonNull Location location) {
//        Timber.e("animateCamera call hoiche");
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)));
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
//        Timber.e("getCameraPositionWithBearing call hoiche");
        return new CameraPosition.Builder().target(latLng).zoom(13.5f).build();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //Log.d(TAG, "onLocationChanged: ");
        currentLocation = location;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        /*mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));*/

        //Log.d(TAG, "onLocationChanged: "+location);


        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }

//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Position");
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_small));
//        currentLocationMarker = mMap.addMarker(markerOptions);

        // for making currentLocationMarker maintain bearing in real-time
        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_small))
                .rotation(location.getBearing()).flat(true).anchor(0.5f, 0.5f)
                .alpha((float) 0.91));

    }

    @Override
    public void onStart() {
//        Timber.e("onStart called");
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
//        Timber.e("onResume called");
        super.onResume();
//        nearest.setOnClickListener(this);
    }

    @Override
    public void onPause() {
//        Timber.e("onPause called");
        super.onPause();
        dismissProgressDialog();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onStop() {
//        Timber.e("onStop called");
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
//        Timber.e("onDestroy called");
        super.onDestroy();
//        mMap = null;
    }

    private JSONArray clickEventJsonArray;
    private JSONArray searchPlaceEventJsonArray;
    private JSONArray bottomSheetPlaceEventJsonArray;

    public void fetchSensors(Location location) {
        this.onConnectedLocation = location;
//        progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage("Fetching The Parking Sensors....");
//        progressDialog.setCancelable(false);
//        progressDialog.show();

//        Timber.d("fetchSensors: " + mMap);
        if (mMap != null) {
            //Toast.makeText(context, "ye huppey", Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "fetchSensors: yeaaaaaaaa");
        }

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_SENSORS, response -> {
//                progressDialog.dismiss();
//            Timber.e(" Sensor Response -> %s", response);
            try {
                JSONObject object = new JSONObject(response);
                JSONArray jsonArray = object.getJSONArray("sensors");
                clickEventJsonArray = object.getJSONArray("sensors");
//                Timber.e("clickEventJsonArray length -> %s", clickEventJsonArray.length());
                searchPlaceEventJsonArray = object.getJSONArray("sensors");
                bottomSheetPlaceEventJsonArray = object.getJSONArray("sensors");
//                Timber.e("searchPlaceEventJsonArray length -> %s", searchPlaceEventJsonArray.length());
//                Timber.e(" Sensor JSONArray -> %s", new Gson().toJson(jsonArray));

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    double latitude = ApplicationUtils.convertToDouble(jsonObject.get("latitude").toString());
                    double longitude = ApplicationUtils.convertToDouble(jsonObject.get("longitude").toString());


                    double tDistance = calculateDistance(latitude, longitude, location.getLatitude(), location.getLongitude());
//                    Timber.e("tDistance: -> %s", tDistance);
                    if (tDistance < nDistance) {
                        nDistance = tDistance;
                        nLatitude = latitude;
                        nLongitude = longitude;
                    }

                    if (jsonObject.get("s_status").toString().equalsIgnoreCase("1")) {
                        if (jsonObject.get("reserve_status").toString().equalsIgnoreCase("1")) {
                            sensorStatus = "Occupied";
                            double lat = latitude;
                            double lon = longitude;
                            if (mMap != null) {
//                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Booked And Parked").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
//                                Timber.e("Booked position -> %s", new LatLng(lat, lon));
                                mMap.addMarker(marker);
                            }
                        } else {
                            sensorStatus = "Empty";
                            double lat = latitude;
                            double lon = longitude;

                            if (mMap != null) {
//                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Occupied.").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
//                                Timber.e("Occupied position -> %s", new LatLng(lat, lon));
                                mMap.addMarker(marker);
                            }
                        }
                    } else {
                        if (jsonObject.get("reserve_status").toString().equalsIgnoreCase("1")) {
                            sensorStatus = "Occupied";
                            double lat = latitude;
                            double lon = longitude;
                            if (mMap != null) {
//                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Booked but No Vehicle").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
//                                Timber.e("No Vehicle position -> %s", new LatLng(lat, lon));
                                mMap.addMarker(marker);
                            }

                        } else {
                            sensorStatus = "Empty";
                            double lat = latitude;
                            double lon = longitude;
                            if (mMap != null) {
//                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Empty").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                mMap.addMarker(marker);
//                                Timber.e("Empty position -> %s", new LatLng(lat, lon));
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                //System.out.println(e.getMessage());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }) {

        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ParkingApp.getInstance().addToRequestQueue(strReq);
    }

    //called from fetchSensor()
    private double calculateDistance(Double latitude, Double longitude, double e, double f) {
        double d2r = Math.PI / 180;

        double dlong = (longitude - f) * d2r;
        double dlat = (latitude - e) * d2r;
        double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(e * d2r) * Math.cos(latitude * d2r) * Math.pow(Math.sin(dlong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = 6367 * c;
        return d;
    }

    private ArrayList<BookingSensors> bookingSensorsArrayListGlobal = new ArrayList<>();
    private BookingSensors bookingSensorsGlobal;
    private double adjustValue = 2;
    private LatLng origin;

    //fetch bottom sheet sensors
    public void fetchBottomSheetSensors(Location location) {
        //initialize the progress dialog and show it
//        bottomSheetProgressDialog = new ProgressDialog(context);
//        bottomSheetProgressDialog.setMessage("Fetching The Parking Sensors....");
//        bottomSheetProgressDialog.setCancelable(false);
//        bottomSheetProgressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_SENSORS, response -> {
//            bottomSheetProgressDialog.dismiss();
//            Timber.e(ParkingApp.class.getCanonicalName(), "" + response);

            try {
                JSONObject object = new JSONObject(response);
                JSONArray jsonArray = object.getJSONArray("sensors");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String areaName = jsonObject.get("parking_area").toString();
                    double latitude = ApplicationUtils.convertToDouble(jsonObject.get("latitude").toString());
                    double longitude = ApplicationUtils.convertToDouble(jsonObject.get("longitude").toString());
//                    Timber.e("api lat -> %s lon -> %s", latitude, longitude);
                    String count = jsonObject.get("no_of_parking").toString();

                    TaskParser taskParser = new TaskParser();
                    double fetchDistance = taskParser.showDistance(new LatLng(location.getLatitude(), location.getLongitude()),
                            new LatLng(latitude, longitude));

                    double kim = (fetchDistance / 1000) + adjustValue;
                    double doubleDuration = Double.parseDouble(new DecimalFormat("##.##").format(kim * 2.43));

                    String initialNearestDuration = doubleDuration + " mins";

                    if (fetchDistance < 3) {
                        //Toast.makeText(requireActivity(), "olaaaaaaaaaaaaaaaaaaa", Toast.LENGTH_SHORT).show();
                        origin = new LatLng(location.getLatitude(), location.getLongitude());
                        getAddress(context, latitude, longitude);
                        String nearestCurrentAreaName = address;
//                        Timber.e("fetchDistance -> %s", fetchDistance);
                        bookingSensorsGlobal = new BookingSensors(nearestCurrentAreaName, latitude,
                                longitude, fetchDistance, count);
//                        Timber.e("bookingSensorsGlobal -> %s", new Gson().toJson(bookingSensorsGlobal));
//                        bookingSensorsArrayListGlobal.add(bookingSensorsGlobal);
                        bookingSensorsArrayListGlobal.add(new BookingSensors(nearestCurrentAreaName, latitude, longitude,
                                fetchDistance, count, initialNearestDuration,
                                BookingSensors.INFO_TYPE, 1));
                        //fetch distance in ascending order
                        Collections.sort(bookingSensorsArrayListGlobal, (c1, c2) -> Double.compare(c1.getDistance(), c2.getDistance()));
//                        Timber.e("bookingSensorsArrayListGlobal new -> %s", new Gson().toJson(bookingSensorsArrayListGlobal));
                        bottomSheetBehavior.setPeekHeight(400);
//                        Log.d(TAG, "fetchBottomSheetSensors: inside if" + bookingSensorsArrayListGlobal);
                    }
                }
//                Log.d(TAG, "fetchBottomSheetSensors: " + bookingSensorsArrayListGlobal);
                setBottomSheetFragmentControls(bookingSensorsArrayListGlobal);
                //Timber.e("test bookingSensorsList -> %s", new Gson().toJson(bookingSensorsArrayListGlobal));
//                Collections.sort(bookingSensorsArrayListGlobal, BookingSensors.BY_NAME_ASCENDING_ORDER);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        }) {

        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ParkingApp.getInstance().addToRequestQueue(strReq);
    }

    private void getAddress(Context context, double LATITUDE, double LONGITUDE) {
        //Set Address
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);

            if (addresses != null && addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                test = addresses.get(0).getAddressLine(1); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                subAdminArea = addresses.get(0).getSubAdminArea();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
//                Timber.e("getAddress:  address -> %s", address);
//                Timber.e("getAddress:  city -> %s", city);
//                Timber.e("getAddress:  country -> %s", country);
//                Timber.e("getAddress:  test -> %s", test);
//                Timber.e("getAddress:  premises -> %s", subAdminArea);
//                Timber.e("getAddress:  state -> %s", state);
//                Timber.e("getAddress:  postalCode -> %s", postalCode);
//                Timber.e("getAddress:  knownName -> %s", knownName);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setBottomSheetFragmentControls(ArrayList<BookingSensors> sensors) {
        bottomSheetRecyclerView.setHasFixedSize(true);
        bottomSheetRecyclerView.setItemViewCacheSize(20);
        bottomSheetRecyclerView.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        bottomSheetRecyclerView.setLayoutManager(mLayoutManager);
        bottomSheetRecyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        bottomSheetRecyclerView.setItemAnimator(new DefaultItemAnimator());
        bottomSheetRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), bottomSheetRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Movie movie = movieList.get(position);
//                Toast.makeText(getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        ViewCompat.setNestedScrollingEnabled(bottomSheetRecyclerView, false);
//        Timber.e("bensorSensorArrayList check -> %s", new Gson().toJson(sensors));
        setBottomSheetRecyclerViewAdapter(sensors);

//        Timber.e("booking sensor recyclerView -> %s", bottomSheetRecyclerView.getAdapter().getItemCount());
    }

    private void setBottomSheetRecyclerViewAdapter(ArrayList<BookingSensors> bookingSensors) {
//        Log.d(TAG, "setBottomSheetRecyclerViewAdapter: " + bookingSensors.size());
        /*for(BookingSensors sensors:bookingSensors)
        {
            Log.d(TAG, "setBottomSheetRecyclerViewAdapter: "+sensors.text);
        }*/
//        Timber.e("setBottomSheetRecyclerViewAdapter bookingSensors -> %s", new Gson().toJson(bookingSensors));
        //bottomSheetSensorAdapter = new BottomSheetSensorAdapter(context, this, bookingSensors, onConnectedLocation);
        bottomSheetAdapter = new BottomSheetAdapter(context, this, bookingSensors, onConnectedLocation);
        bottomSheetRecyclerView.setAdapter(bottomSheetAdapter);
    }

    private ArrayList<BookingSensors> bookingSensorsArrayListBottomSheet = new ArrayList<>();

    public void bottomSheetPlaceLatLngNearestLocations() {
        if (bottomSheetPlaceLatLng != null) {
            //for getting the location name
            getAddress(getActivity(), bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude);
            String bottomSheetPlaceName = address;
//            Timber.e("searchPlaceName -> %s", bottomSheetPlaceName);

            TaskParser taskParser = new TaskParser();
            double bottomSheetDistance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                    new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));

            if (bottomSheetDistance < 3000) {
                adjustValue = 1;
            }

            double kim = (bottomSheetDistance / 1000) + adjustValue;
//            Timber.e("adjustValue first -> %s", adjustValue);
            double bottomSheetDoubleDuration = Double.parseDouble(new DecimalFormat("##.##").format(bottomSheetDistance * 2.43));
            String bottomSheetStringDuration = bottomSheetDoubleDuration + " mins";

            bookingSensorsArrayListBottomSheet.add(new BookingSensors(bottomSheetPlaceName, bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude,
                    bottomSheetDistance, textViewBottomSheetParkingAreaCount.getText().toString(), bottomSheetStringDuration,
                    context.getResources().getString(R.string.nearest_parking_from_your_destination),
                    BookingSensors.TEXT_INFO_TYPE, 0));

            for (int i = 0; i < bottomSheetPlaceEventJsonArray.length(); i++) {
                JSONObject jsonObject;
                try {
                    jsonObject = bottomSheetPlaceEventJsonArray.getJSONObject(i);
                    String latitude1 = jsonObject.get("latitude").toString();
                    String longitude1 = jsonObject.get("longitude").toString();

                    double distanceForNearbyLoc = calculateDistance(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude,
                            ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));

                    if (distanceForNearbyLoc < 5) {
                        origin = new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude);
                        getAddress(getActivity(), ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));
                        String nearbyAreaName = address;
                        String parkingNumberOfNearbyDistanceLoc = jsonObject.get("no_of_parking").toString();

                        int adjsutNearbyValue = 2;
                        if (distanceForNearbyLoc < 1000) {
                            adjsutNearbyValue = 1;
                        }

                        double km = (distanceForNearbyLoc / 1000) + adjsutNearbyValue;
                        double nearbySearchDoubleDuration = Double.parseDouble(new DecimalFormat("##.##").format(km * 2.43));
                        String nearbySearchStringDuration = nearbySearchDoubleDuration + " mins";
                        bookingSensorsArrayListBottomSheet.add(new BookingSensors(nearbyAreaName, ApplicationUtils.convertToDouble(latitude1),
                                ApplicationUtils.convertToDouble(longitude1), distanceForNearbyLoc, parkingNumberOfNearbyDistanceLoc,
                                nearbySearchStringDuration,
                                BookingSensors.INFO_TYPE, 1));

                        bubbleSortArrayList(bookingSensorsArrayListBottomSheet);
                        bottomSheetBehavior.setPeekHeight(400);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            bottomSheetAdapter.updateData(bookingSensorsArrayListBottomSheet);
            setBottomSheetRecyclerViewAdapter(bookingSensorsArrayListBottomSheet);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            responseString = requestDirection(strings[0]);
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    private String requestDirection(String reqUrl) {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    @SuppressLint("StaticFieldLeak")
    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;
            PolylineOptions polylineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if (lists.size() < 1) {
                Toast.makeText(getActivity(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();
//                for (int j = 0; j < path.size(); j++) {
                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));
//                        if (j == 0) {    // Get distance from the list
//                            distance = (String) point.get("distance");
//                            continue;
//                        } else if (j == 1) { // Get duration from the list
//                            duration = (String) point.get("duration");
//                            continue;
//                        }

//                    Timber.e("duration -> %s", duration);

                    points.add(new LatLng(lat, lon));
                }
            }
            polylineOptions.addAll(points);
            polylineOptions.width(5);
            if (flag == 1) {
                polylineOptions.color(Color.BLACK);
                polylineOptions.width(5);
            }
//                else if (flag == 2) {
////                    if (googleMap != null)
////                        googleMap.clear();
//                    polylineOptions.color(Color.BLACK);
//                    polylineOptions.width(5);
//                }
            flag++;

            polylineOptions.geodesic(true);
            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getActivity(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }
        }

        public double showDistance(LatLng from, LatLng to) {

            int Radius = 6371;// radius of earth in Km
            double lat1 = from.latitude;
            double lat2 = to.latitude;
            double lon1 = from.longitude;
            double lon2 = to.longitude;
            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(lat1))
                    * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                    * Math.sin(dLon / 2);
            double c = 2 * Math.asin(Math.sqrt(a));
            double valueResult = Radius * c;

            double km = valueResult / 1;
            DecimalFormat newFormat = new DecimalFormat("####");
            int kmInDec = Integer.parseInt(newFormat.format(km));
            double meter = valueResult % 1000;
            int meterInDec = Integer.parseInt(newFormat.format(meter));
//            Timber.i("showDistance" + valueResult + "   KM  " + kmInDec
//                    + " Meter   " + meterInDec);

            return (Radius * c);
        }
    }

    private BookingSensors bookingSensors;
    private BookingSensors bookingSensorsBottomSheet;
    private ArrayList<BookingSensors> bookingSensorsArrayList = new ArrayList<>();
//    double adjustValue = 2;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        Timber.e("onActivityResult HomeFragment called");
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d("onMapReady: onActivityResult");

        if (requestCode == AppConstants.GPS_REQUEST && resultCode == RESULT_OK) {
            isGPS = true; // flag maintain before get location
        }

        if (requestCode == NEW_SEARCH_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            Log.d(TAG, "onActivityResult: blaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            //progressDialog.show();
//            Toast.makeText(requireActivity(), "place received", Toast.LENGTH_SHORT).show();
            SelcectedPlace selcectedPlace = (SelcectedPlace) data.getSerializableExtra(NEW_PLACE_SELECTED);//This line may produce null point exception
            if (selcectedPlace != null) {
                buttonSearch.setVisibility(View.GONE);
                linearLayoutSearchBottom.setVisibility(View.VISIBLE);
                linearLayoutSearchBottomButton.setVisibility(View.VISIBLE);
                btnSearchGetDirection.setVisibility(View.VISIBLE);
                btnSearchGetDirection.setEnabled(true);
                imageViewSearchBack.setVisibility(View.VISIBLE);
//                buttonSearch.setText(selcectedPlace.getAreaName());
//                Timber.e("onActivityResult: lat: -> %s", selcectedPlace.getLatitude());
//                Timber.e("onActivityResult: lon: -> %s", selcectedPlace.getLongitude());
                searchPlaceLatLng = new LatLng(selcectedPlace.getLatitude(), selcectedPlace.getLongitude());
//                Timber.e(String.valueOf(searchPlaceLatLng));

                if (mMap != null)
                    mMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(searchPlaceLatLng);
                markerOptions.title(name);
//                    markerOptions.draggable(true);
                coordList.add(new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                mMap.addMarker(markerOptions).setFlat(true);
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 13.5f));
                fetchSensors(onConnectedLocation);
                bottomSheetSearch = 0;
                btnSearchGetDirection.setText("Unavailable Parking Spot");
                btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                btnSearchGetDirection.setEnabled(false);
                btnSearchGetDirection.setFocusable(false);
                //for getting the location name
                getAddress(getActivity(), searchPlaceLatLng.latitude, searchPlaceLatLng.longitude);
                String searchPlaceName = address;
//                    Timber.e("searchPlaceName -> %s", searchPlaceName);

                TaskParser taskParser = new TaskParser();
                double searchDistance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                        new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
//                    Timber.e("searchDistance -> %s", searchDistance);
                layoutSearchVisible(true, searchPlaceName, "0", textViewSearchParkingDistance.getText().toString(), searchPlaceLatLng);
                bottomSheetBehavior.setPeekHeight(400);

                if (searchDistance < 3000) {
                    adjustValue = 1;
                }

                double kim = (searchDistance / 1000) + adjustValue;
//                    Timber.e("adjustValue first -> %s", adjustValue);
                double searchDoubleDuration = Double.parseDouble(new DecimalFormat("##.##").format(searchDistance * 2.43));
                String searchStringDuration = searchDoubleDuration + " mins";

                bookingSensors = new BookingSensors(searchPlaceName, searchPlaceLatLng.latitude, searchPlaceLatLng.longitude,
                        searchDistance,
                        "0", searchStringDuration);
//                    Timber.e("bookingSensors only Search-> %s", new Gson().toJson(bookingSensors));
//                        bookingSensorsArrayList.add(bookingSensors);
                bookingSensorsArrayList.add(new BookingSensors(searchPlaceName, searchPlaceLatLng.latitude, searchPlaceLatLng.longitude,
                        searchDistance, "0", searchStringDuration,
                        context.getResources().getString(R.string.nearest_parking_from_your_destination),
                        BookingSensors.TEXT_INFO_TYPE, 0));

                for (int i = 0; i < searchPlaceEventJsonArray.length(); i++) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = searchPlaceEventJsonArray.getJSONObject(i);
                        String latitude1 = jsonObject.get("latitude").toString();
                        String longitude1 = jsonObject.get("longitude").toString();

                        double distanceForNearbyLoc = calculateDistance(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude,
                                ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));
//                            Timber.e("DistanceForNearbyLoc -> %s", distanceForNearbyLoc);

                        if (distanceForNearbyLoc < 5) {
                            bottomSheetSearch = 1;
                            origin = new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude);
                            getAddress(getActivity(), ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));
                            String nearbyAreaName = address;
                            String parkingNumberOfNearbyDistanceLoc = jsonObject.get("no_of_parking").toString();
//                                Timber.e("nearbyDistance nearByDuration -> %s -> %s", nearByDistance, nearByDuration);
                            bookingSensors = new BookingSensors(nearbyAreaName, ApplicationUtils.convertToDouble(latitude1),
                                    ApplicationUtils.convertToDouble(longitude1), nearByDistance, parkingNumberOfNearbyDistanceLoc);
//                                Timber.e("bookingSensors nearest search location -> %s", new Gson().toJson(bookingSensors));

                            int adjsutNearbyValue = 2;
                            if (distanceForNearbyLoc < 1000) {
                                adjsutNearbyValue = 1;
                            }

                            double km = (distanceForNearbyLoc / 1000) + adjsutNearbyValue;
                            double nearbySearchDoubleDuration = Double.parseDouble(new DecimalFormat("##.##").format(km * 2.43));
                            String nearbySearchStringDuration = nearbySearchDoubleDuration + " mins";

//                                    bookingSensorsArrayList.add(bookingSensors);
                            bookingSensorsArrayList.add(new BookingSensors(nearbyAreaName, ApplicationUtils.convertToDouble(latitude1),
                                    ApplicationUtils.convertToDouble(longitude1), distanceForNearbyLoc, parkingNumberOfNearbyDistanceLoc,
                                    nearbySearchStringDuration,
                                    BookingSensors.INFO_TYPE, 1));

                            bubbleSortArrayList(bookingSensorsArrayList);
                            bottomSheetBehavior.setPeekHeight(400);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//                    Timber.e("bookingSensors latest -> %s", new Gson().toJson(bookingSensorsArrayList));
//                        bookingSensorsArrayListGlobal.clear();
                //bottomSheetSensorAdapter.updateData(bookingSensorsArrayList);
                bottomSheetAdapter.updateData(bookingSensorsArrayList);
                setBottomSheetRecyclerViewAdapter(bookingSensorsArrayList);
//                    Timber.e("setBottomSheetRecyclerViewAdapter(bookingSensorsArrayList) call hoiche for loop");
            }
        } else {
            if (SharedData.getInstance().getOnConnectedLocation() != null) {
                fetchSensors(SharedData.getInstance().getOnConnectedLocation());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Timber.d("onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: on requestPermission");
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            Log.d(TAG, "onRequestPermissionsResult: First time evoked");

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Log.d(TAG, "onRequestPermissionResult: on requestPermission if-if");
                progressDialog.show();
                supportMapFragment.getMapAsync(this);
//                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();

            } else if (grantResults.length == FIRST_TIME_INSTALLED && getActivity() != null) {
                if ((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    Log.d(TAG, "onViewCreated: in if");
                    Toast.makeText(context, "Ok Clicked", Toast.LENGTH_SHORT).show();
                }

            }

        }

    }

    //bubble sort for nearest parking spot from searched area
    private void bubbleSortArrayList(ArrayList<BookingSensors> list) {
        BookingSensors temp;
        boolean sorted = false;

        while (!sorted) {
            sorted = true;
            for (int i = 1; i < list.size() - 1; i++) {
                if (list.get(i).compareTo(list.get(i + 1)) > 0) {
                    temp = list.get(i);
                    list.set(i, list.get(i + 1));
                    list.set(i + 1, temp);
                    sorted = false;
                }
            }
        }
    }

    private void getDestinationInfoForDuration(LatLng latLngDestination) {
        this.origin = origin;
//        Timber.e(" -> %s", origin);

        String serverKey = context.getResources().getString(R.string.google_maps_key); // Api Key For Google Direction API \\
//        Timber.e("condition getDestinationInfoForDuration");
        origin = new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude());
        LatLng destination = latLngDestination;

        //-------------Using AK Exorcist Google Direction Library---------------\\
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {

                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
//                        dismissDialog();
                        String status = direction.getStatus();
                        if (status.equals(RequestResult.OK)) {
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            Info distanceInfo = leg.getDistance();
                            Info durationInfo = leg.getDuration();
                            String distance = distanceInfo.getText();
                            String duration = durationInfo.getText();
                            textViewParkingTravelTime.setText(duration);
//                            if (bottomSheetSearch == 0)
//                            textViewMarkerParkingTravelTime.setText(duration);
                            textViewSearchParkingDistance.setText(distance);
                            textViewSearchParkingTravelTime.setText(duration);
//                            Timber.e("search distance duration-> %s %s",
//                                    textViewSearchParkingDistance.getText().toString(),
//                                    textViewSearchParkingTravelTime.getText().toString());
                            textViewBottomSheetParkingTravelTime.setText(duration);
//                            Timber.e("textViewBottomSheetParkingTravelTime duration-> %s", textViewBottomSheetParkingTravelTime.getText().toString());
                            nearByDuration = duration;
//                            nearByDistance = distance;
                            fetchDuration = duration;
//                            Timber.e("fetchDuration -> %s", fetchDuration);
//                            Timber.e("inmethod nearByDuration nearByDistance -> %s %s", nearByDuration, nearByDistance);
                            //------------Displaying Distance and Time-----------------\\
//                            showingDistanceTime(distance, duration); // Showing distance and time to the user in the UI \\
                            String message = "Total Distance is " + distance + " and Estimated Time is " + duration;
//                            Timber.e("duration message -> %s", message);

                        } else if (status.equals(RequestResult.NOT_FOUND)) {
                            Toast.makeText(context, "No routes exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something here
                    }
                });
        //-------------------------------------------------------------------------------\\

    }

    //getting the direction url
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Key
//        String key = "key=AIzaSyDMWfYh5kjSQTALbZb-C0lSNACpcH5RDU4";
        String key = "key=AIzaSyCsEedODXq-mkA1JYedp-Y-QARH0x4h0kI";
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + key;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

//        Timber.e("Directions URL: -> %s", url);

        mService.getDataFromGoogleApi(url)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, retrofit2.Response<String> response) {
//                        Timber.e("retrofit onResponse call hoiche");
                        try {
                            JSONObject jsonObject =
                                    new JSONObject(response.body().toString());
                            JSONArray jsonArray = jsonObject.getJSONArray("routes");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject route = jsonArray.getJSONObject(i);
                                JSONObject poly = route.getJSONObject("overview_polyline");
                                String polyLine = poly.getString("points");
                                polyLineList = decodePoly(polyLine);

                            }
                            //Adjusting Bounds
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (LatLng latLng : polyLineList)
                                builder.include(latLng);
                            LatLngBounds bounds = builder.build();
                            CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                            mMap.animateCamera(mCameraUpdate);

                            polylineOptions = new PolylineOptions();
                            polylineOptions.color(Color.GRAY);
                            polylineOptions.width(5f);
                            polylineOptions.geodesic(true);
                            polylineOptions.startCap(new SquareCap());
                            polylineOptions.endCap(new SquareCap());
                            polylineOptions.jointType(JointType.ROUND);
                            polylineOptions.addAll(polyLineList);
                            grayPolyline = mMap.addPolyline(polylineOptions);

                            blackPolylineOptions = new PolylineOptions();
                            blackPolylineOptions.color(Color.BLACK);
                            blackPolylineOptions.width(5f);
                            blackPolylineOptions.zIndex(5f);
                            blackPolylineOptions.geodesic(true);
                            blackPolylineOptions.startCap(new SquareCap());
                            blackPolylineOptions.endCap(new SquareCap());
                            blackPolylineOptions.jointType(JointType.ROUND);
                            blackPolylineOptions.addAll(polyLineList);
                            blackPolyline = mMap.addPolyline(blackPolylineOptions);

//                            mMap.addMarker(new MarkerOptions().
//                                    position(polyLineList.get(polyLineList.size() - 1)));

                            //Animator
                            final ValueAnimator polyLineAnimator = ValueAnimator.ofInt(0, 100);
                            polyLineAnimator.setDuration(2000);
                            polyLineAnimator.setRepeatCount(ValueAnimator.INFINITE);
                            polyLineAnimator.setRepeatMode(ValueAnimator.RESTART);
                            polyLineAnimator.setInterpolator(new LinearInterpolator());
                            polyLineAnimator.addUpdateListener(valueAnimator -> {
                                List<LatLng> points = grayPolyline.getPoints();
                                int percentValue = (int) valueAnimator.getAnimatedValue();
                                int size = points.size();
                                int newPoints = (int) (size * (percentValue / 100.0f));
                                List<LatLng> p = points.subList(0, newPoints);
                                blackPolyline.setPoints(p);
                            });

                            polyLineAnimator.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    List<LatLng> greyLatLng = grayPolyline.getPoints();
                                    if (greyLatLng != null) {
                                        greyLatLng.clear();
                                    }
                                    polyLineAnimator.start();

                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    polyLineAnimator.cancel();
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                            polyLineAnimator.start();
                            zoomRoute(mMap, polyLineList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(mMap.getCameraPosition().target)
                .zoom(13.5f)
                .bearing(30)
                .tilt(30)
                .build()));
        return url;
    }

    /**
     * Zooms a Route (given a List of LalLng) at the greatest possible zoom level.
     *
     * @param googleMap:      instance of GoogleMap
     * @param lstLatLngRoute: list of LatLng forming Route
     */
    private void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {

        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 200;
        int left = 50;
        int right = 50;
        int top = 20;
        int bottom = 100;

        LatLngBounds latLngBounds = boundsBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
        googleMap.setPadding(left, top, right, bottom);
    }

    //for uber like car bearing
    public static float getBearing(LatLng startPosition, LatLng newPos) {

        //Source
        double lat1 = startPosition.latitude;
        double lng1 = startPosition.longitude;

        // destination
        double lat2 = newPos.latitude;
        double lng2 = newPos.longitude;

        double fLat = degreeToRadians(lat1);
        double fLong = degreeToRadians(lng1);
        double tLat = degreeToRadians(lat2);
        double tLong = degreeToRadians(lng2);

        double dLon = (tLong - fLong);

        float degree = (float) (radiansToDegree(Math.atan2(Math.sin(dLon) * Math.cos(tLat),
                Math.cos(fLat) * Math.sin(tLat) - Math.sin(fLat) * Math.cos(tLat) * Math.cos(dLon))));

        if (degree >= 0) {
            return degree;
        } else {
            return 360 + degree;
        }
    }

    private static double degreeToRadians(double latLong) {
        return (Math.PI * latLong / 180.0);
    }

    private static double radiansToDegree(double latLong) {
        return (latLong * 180.0 / Math.PI);
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetMarkerEvent event) {
//        Toast.makeText(getActivity(), "Geche", Toast.LENGTH_SHORT).show();
        layoutVisible(true, ApplicationUtils.capitalize(name), count, distance, event.location);
//        Timber.e("Zoom call hoiche");
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(event.location);
        markerOptions.title(name);
//        markerOptions.draggable(true);
        coordList.add(new LatLng(event.location.latitude, event.location.longitude));
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
        mMap.addMarker(markerOptions);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(event.location, 13.5f));
//        final Handler handler = new Handler();
////        handler.postDelayed(new Runnable() {
////            @Override
////            public void run() {
////                EventBus.getDefault().post(new GetDirectionAfterButtonClickEvent(event.location));
////            }
////        }, 500);

//        String url = getDirectionsUrl(new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude), event.location);
//        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
//        taskRequestDirections.execute(url);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCustomDirectionEvent(GetDirectionAfterButtonClickEvent event) {
//        Toast.makeText(getActivity(), "Adapter Click e Geche", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //animateCamera(currentLocation);
                location = event.location;
                EventBus.getDefault().post(new SetMarkerEvent(location));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(location);
                markerOptions.title(name);
//                markerOptions.draggable(true);
                coordList.add(new LatLng(location.latitude, location.longitude));
//                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                mMap.addMarker(markerOptions);
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13.5f));
                btnGetDirection.setVisibility(View.VISIBLE);
//                linearLayoutNameCount.setVisibility(View.VISIBLE);
                linearLayoutBottom.setVisibility(View.VISIBLE);
                imageViewBack.setVisibility(View.VISIBLE);
                String url = getDirectionsUrl(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()), event.location);
//                String url = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), event.location);
//                Timber.e("url -> %s", url);
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);

            }
        }, 1000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBottomSheetDirectionEvent(GetDirectionBottomSheetEvent event) {
//        Toast.makeText(getActivity(), "BottomSheet Click e Geche", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //animateCamera(currentLocation);
                bottomSheetPlaceLatLng = event.location;
                EventBus.getDefault().post(new SetMarkerEvent(bottomSheetPlaceLatLng));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(bottomSheetPlaceLatLng);
                markerOptions.title(name);
//                markerOptions.draggable(true);
                coordList.add(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
//                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                mMap.addMarker(markerOptions);
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13.5f));
                btnBottomSheetGetDirection.setVisibility(View.VISIBLE);
//                linearLayoutNameCount.setVisibility(View.VISIBLE);
                linearLayoutBottomSheetBottom.setVisibility(View.VISIBLE);
                imageViewBottomSheetBack.setVisibility(View.VISIBLE);
                String url = getDirectionsUrl(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()), event.location);
//                String url = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), event.location);
//                Timber.e("url -> %s", url);
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);

            }
        }, 1000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchDirectionEvent(GetDirectionForSearchEvent event) {
//        Toast.makeText(getActivity(), "Search Click e Geche", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //animateCamera(currentLocation);
                searchPlaceLatLng = event.location;
                EventBus.getDefault().post(new SetMarkerEvent(searchPlaceLatLng));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(searchPlaceLatLng);
                markerOptions.title(name);
//                markerOptions.draggable(true);
                coordList.add(new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
//                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                mMap.addMarker(markerOptions);
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 13.5f));
                btnSearchGetDirection.setVisibility(View.VISIBLE);
//                linearLayoutNameCount.setVisibility(View.VISIBLE);
//                linearLayoutSearchBottom.setVisibility(View.VISIBLE);
                linearLayoutSearchBottomButton.setVisibility(View.VISIBLE);
                imageViewSearchBack.setVisibility(View.VISIBLE);
                linearLayoutBottom.setVisibility(View.GONE);
                linearLayoutMarkerBottom.setVisibility(View.GONE);
                String url = getDirectionsUrl(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()), searchPlaceLatLng);
//                String url = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), searchPlaceLatLng);
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);
            }
        }, 1000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMarkerDirectionEvent(GetDirectionForMarkerEvent event) {
//        Toast.makeText(getActivity(), "Marker Click e Geche", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //animateCamera(currentLocation);
                markerPlaceLatLng = event.location;
                EventBus.getDefault().post(new SetMarkerEvent(markerPlaceLatLng));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(markerPlaceLatLng);
                markerOptions.title(name);
//                markerOptions.draggable(true);
                coordList.add(new LatLng(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude));
//                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                mMap.addMarker(markerOptions);
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPlaceLatLng, 13.5f));
                btnMarkerGetDirection.setVisibility(View.VISIBLE);
                String url = getDirectionsUrl(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()), markerPlaceLatLng);
//                String url = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), markerPlaceLatLng);
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);
                fromMarkerRouteDrawn = 1;

            }
        }, 1000);
    }

    @SuppressLint("SetTextI18n")
    public void layoutVisible(boolean isVisible, String name, String count,
                              String distance, LatLng location) {
        this.name = name;
        this.count = count;
        this.distance = distance;
        this.location = location;
        this.duration = duration;

        if (isVisible) {
            linearLayoutBottom.setVisibility(View.VISIBLE);
            Location temp = new Location(LocationManager.GPS_PROVIDER);
            temp.setLatitude(location.latitude);
            temp.setLongitude(location.longitude);
            animateCamera(temp);
//            textViewParkingAreaCount.setText(count);
//            textViewParkingAreaName.setText(name);
//            textViewParkingDistance.setText(new DecimalFormat("##.##").format(distance) + " km");
//            textViewParkingTravelTime.setText(duration);
        } else {
//            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//            navBar.setVisibility(View.VISIBLE);
            linearLayoutBottom.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void layoutSearchVisible(boolean isVisible, String name, String count,
                                     String distance, LatLng location) {
        this.name = name;
        this.count = count;
        this.searchPlaceLatLng = location;
        this.distance = distance;
        this.duration = duration;

        if (isVisible) {
//            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//            navBar.setVisibility(View.VISIBLE);
            linearLayoutSearchBottom.setVisibility(View.VISIBLE);
            textViewSearchParkingAreaCount.setText(count);
            textViewSearchParkingAreaName.setText(ApplicationUtils.capitalize(name));
        } else {
//            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//            navBar.setVisibility(View.VISIBLE);
            linearLayoutSearchBottom.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void layoutMarkerVisible(boolean isVisible, String name, String count,
                                     String distance, LatLng location) {
        this.name = name;
        this.count = count;
//        Timber.e("layoutMarkerVisible count -> %s", count);
        this.distance = distance;
        this.markerPlaceLatLng = location;
        this.duration = duration;

        if (isVisible) {
//            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//            navBar.setVisibility(View.VISIBLE);
            linearLayoutMarkerNameCount.setVisibility(View.GONE);
            linearLayoutMarkerBottom.setVisibility(View.VISIBLE);
//            textViewMarkerParkingAreaCount.setText(count);
//            textViewMarkerParkingAreaName.setText(ApplicationUtils.capitalize(name));
        } else {
//            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//            navBar.setVisibility(View.VISIBLE);
            linearLayoutMarkerBottom.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    public void layoutBottomSheetVisible(boolean isVisible, String name, String count,
                                         String distance, String duration, LatLng location) {
        this.name = name;
        this.count = count;
//        Timber.e("layoutBottomSheetVisible count -> %s", count);
        this.distance = distance;
        this.bottomSheetPlaceLatLng = location;
        this.duration = duration;

        if (isVisible) {
            if (bottomSheetPlaceLatLng != null) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(bottomSheetPlaceLatLng);
                markerOptions.title(name);
                coordList.add(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
//                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                mMap.addMarker(markerOptions).setFlat(true);
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bottomSheetPlaceLatLng, 13.5f));
            }
            linearLayoutBottomSheetBottom.setVisibility(View.VISIBLE);
            textViewBottomSheetParkingAreaCount.setText(count);
            textViewBottomSheetParkingAreaName.setText(ApplicationUtils.capitalize(name));
//            textViewBottomSheetParkingDistance.setText(new DecimalFormat("##.##").format(distance) + " km");
            textViewBottomSheetParkingDistance.setText(distance.substring(0, 3) + " km");
//            textViewMarkerParkingTravelTime.setText(duration);
            getDestinationInfoForDuration(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));

            if (count.equals("0")){
                btnBottomSheetGetDirection.setText("Unavailable Parking Spot");
                btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                btnBottomSheetGetDirection.setEnabled(false);
                btnBottomSheetGetDirection.setFocusable(false);
            }else {
                btnBottomSheetGetDirection.setText("Get Direction");
                btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                btnBottomSheetGetDirection.setEnabled(true);
                btnBottomSheetGetDirection.setFocusable(true);
            }
        } else {
//            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//            navBar.setVisibility(View.VISIBLE);
            linearLayoutBottomSheetBottom.setVisibility(View.GONE);
            bottomSheetAdapter.isItemClicked = false;
        }
    }

    private void setListeners() {

        buttonSearch.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), SearchActivity.class);
            //startActivity(intent);
            startActivityForResult(intent, NEW_SEARCH_ACTIVITY_REQUEST_CODE);
            if (searchPlaceLatLng != null)
                bookingSensorsArrayList.clear();
            if (mMap != null)
                mMap.clear();
            bookingSensorsArrayListGlobal.clear();
            bookingSensorsArrayList.clear();
            bookingSensorsMarkerArrayList.clear();
            if (onConnectedLocation != null) {
                fetchBottomSheetSensors(onConnectedLocation);
//                fetchSensors(onConnectedLocation);
            }
            buttonSearch.setText(null);
            linearLayoutBottom.setVisibility(View.GONE);
            linearLayoutSearchBottom.setVisibility(View.GONE);
            linearLayoutMarkerBottom.setVisibility(View.GONE);
            linearLayoutBottomSheetBottom.setVisibility(View.GONE);
            markerAlreadyClicked = 0;
        });

        imageViewBack.setOnClickListener(v -> {
            if (mMap != null) {
                mMap.clear();
                fetchSensors(onConnectedLocation);
                bookingSensorsArrayListGlobal.clear();
                bookingSensorsArrayList.clear();
                bookingSensorsMarkerArrayList.clear();
                fetchBottomSheetSensors(onConnectedLocation);
                animateCamera(onConnectedLocation);
                buttonSearch.setText(null);
                buttonSearch.setVisibility(View.VISIBLE);
//                BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//                navBar.setVisibility(View.VISIBLE);
                layoutVisible(false, "", "", " ", null);
                linearLayoutBottom.setVisibility(View.GONE);
                linearLayoutSearchBottom.setVisibility(View.GONE);
                linearLayoutMarkerBottom.setVisibility(View.GONE);
                SharedData.getInstance().setSensorArea(null);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                bottomSheetBehavior.setPeekHeight(400);

                btnGetDirection.setText("Get Direction");
                btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                btnGetDirection.setEnabled(true);
                btnGetDirection.setFocusable(true);
            }
        });

        imageViewSearchBack.setOnClickListener(v -> {
            if (mMap != null) {
//                Timber.e("imageViewSearchBack call hoiche");
//                imageViewSearchBackClicked = true;
                btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                bottomSheetBehavior.setPeekHeight(400);
                animateCamera(onConnectedLocation);
                mMap.clear();
                if (getDirectionSearchButtonClicked == 1) {
                    btnSearchGetDirection.setText("Get Direction");
                    btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                    getDirectionSearchButtonClicked--;
                }
                fetchSensors(onConnectedLocation);
                fetchBottomSheetSensors(onConnectedLocation);
                buttonSearch.setText(null);
                buttonSearch.setVisibility(View.VISIBLE);
                bookingSensorsArrayList.clear();
                bookingSensorsArrayListGlobal.clear();
                bookingSensorsMarkerArrayList.clear();
                LatLng latLng = new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude());
//                BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//                navBar.setVisibility(View.VISIBLE);
                layoutSearchVisible(false, "", "", "", null);
                linearLayoutBottom.setVisibility(View.GONE);
                linearLayoutSearchBottom.setVisibility(View.GONE);
                linearLayoutMarkerBottom.setVisibility(View.GONE);
                SharedData.getInstance().setBookingSensors(null);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                bottomSheetBehavior.setPeekHeight(400);

                btnSearchGetDirection.setText("Get Direction");
                btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                btnSearchGetDirection.setEnabled(true);
                btnSearchGetDirection.setFocusable(true);
            }
        });

        imageViewMarkerBack.setOnClickListener(v -> {
            if (mMap != null) {
                bottomSheetBehavior.setPeekHeight(400);
                mMap.clear();
                if (getDirectionMarkerButtonClicked == 1) {
                    btnMarkerGetDirection.setText("Get Direction");
                    btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                    getDirectionMarkerButtonClicked--;
                }
                fetchSensors(onConnectedLocation);
                bookingSensorsArrayListGlobal.clear();
                bookingSensorsArrayList.clear();
                bookingSensorsMarkerArrayList.clear();
                fetchBottomSheetSensors(onConnectedLocation);
                animateCamera(onConnectedLocation);
                buttonSearch.setText(null);
                buttonSearch.setVisibility(View.VISIBLE);
//                BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//                navBar.setVisibility(View.VISIBLE);
                layoutMarkerVisible(false, "", "", "", null);
                linearLayoutBottom.setVisibility(View.GONE);
                linearLayoutSearchBottom.setVisibility(View.GONE);
                linearLayoutMarkerBottom.setVisibility(View.GONE);
                markerAlreadyClicked = 0;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                bottomSheetBehavior.setPeekHeight(400);

                btnMarkerGetDirection.setText("Get Direction");
                btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                btnMarkerGetDirection.setEnabled(true);
                btnMarkerGetDirection.setFocusable(true);
            }
        });

        imageViewBottomSheetBack.setOnClickListener(v -> {
            if (mMap != null) {
                mMap.clear();
                buttonSearch.setText(null);
                buttonSearch.setVisibility(View.VISIBLE);
                fetchSensors(onConnectedLocation);
//                if (bottomSheetPlaceLatLng != null) {
//                    Timber.e("bottomSheetPlaceLatLng if  dhukche");
//                    getActivity().getSupportFragmentManager().beginTransaction().replace(this.getId(),
//                            new HomeFragment()).commit();
//                } else {
//                    Timber.e("bottomSheetPlaceLatLng else  dhukche");
//                    getActivity().getSupportFragmentManager().beginTransaction().replace(this.getId(),
//                            new HomeFragment()).commit();
//                }
                bookingSensorsArrayListGlobal.clear();
                bookingSensorsArrayList.clear();
                bookingSensorsMarkerArrayList.clear();
                fetchBottomSheetSensors(onConnectedLocation);
                animateCamera(onConnectedLocation);
                if (getDirectionBottomSheetButtonClicked == 1) {
                    btnBottomSheetGetDirection.setText("Get Direction");
                    btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                    getDirectionBottomSheetButtonClicked--;
                }
//                BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//                navBar.setVisibility(View.VISIBLE);
                layoutBottomSheetVisible(false, "", "", "", "", null);
//                bottomSheetAdapter.isItemClicked = false;
                ApplicationUtils.reLoadFragment(getParentFragmentManager(), this);
                linearLayoutBottom.setVisibility(View.GONE);
                linearLayoutSearchBottom.setVisibility(View.GONE);
                linearLayoutMarkerBottom.setVisibility(View.GONE);
                markerAlreadyClicked = 0;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                bottomSheetBehavior.setPeekHeight(400);

                btnBottomSheetGetDirection.setText("Get Direction");
                btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                btnBottomSheetGetDirection.setEnabled(true);
                btnBottomSheetGetDirection.setFocusable(true);
            }
        });

        btnGetDirection.setOnClickListener(v -> {
//            Toast.makeText(context, "adapter", Toast.LENGTH_SHORT).show();
            if (getDirectionButtonClicked == 0) {
                getDirectionButtonClicked++;
                if (location != null) {
                    EventBus.getDefault().post(new GetDirectionAfterButtonClickEvent(location));
                    fetchSensors(onConnectedLocation);
                    buttonSearch.setVisibility(View.GONE);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(location);
//                    markerOptions.title(name);
                    coordList.add(new LatLng(location.latitude, location.longitude));
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                    mMap.addMarker(markerOptions).setFlat(true);
//              move map camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13.5f));
//                    linearLayoutNameCount.setVisibility(View.VISIBLE);
                    linearLayoutBottom.setVisibility(View.VISIBLE);
                    linearLayoutSearchBottom.setVisibility(View.GONE);
                    linearLayoutMarkerBottom.setVisibility(View.GONE);
                    imageViewBack.setVisibility(View.VISIBLE);
                    btnGetDirection.setText("Confirm Booking");
                    btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                    btnGetDirection.setEnabled(false);
                    btnGetDirection.setFocusable(false);

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight(400);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ApplicationUtils.showMessageDialog("Once reach your destination, \nConfirm Booking Button will be enabled &" +
                                    " you can reserve your booking spot!!!", context);
                        }
                    }, 1000);
                }
            } else if (getDirectionButtonClicked == 1) {
//                ApplicationUtils.showMessageDialog("Once reach your destination you can reserve your booking spot!!!", context);
//                getDirectionButtonClicked--;
                if (mMap != null) {
//                    mMap.clear();
//                    fetchSensors(onConnectedLocation);
//                    buttonSearch.setVisibility(View.VISIBLE);
//                    bookingSensorsArrayListGlobal.clear();
//                    bookingSensorsArrayList.clear();
//                    bookingSensorsMarkerArrayList.clear();
//                    animateCamera(onConnectedLocation);
//                    fetchBottomSheetSensors(onConnectedLocation);
//                    layoutVisible(false, "", "", "", null);
//                    linearLayoutBottom.setVisibility(View.GONE);
//                    linearLayoutSearchBottom.setVisibility(View.GONE);
//                    linearLayoutMarkerBottom.setVisibility(View.GONE);
//                    linearLayoutNameCount.setVisibility(View.GONE);
//                    btnGetDirection.setText("Get Direction");
//                    btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
//                    BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//                    navBar.setVisibility(View.VISIBLE);
                    TaskParser taskParser = new TaskParser();
                    double distance = taskParser.showDistance(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                            new LatLng(location.latitude, location.longitude));
                    if (distance < 0.1) {
                        btnGetDirection.setText("Confirm Booking");
                        btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                        btnGetDirection.setEnabled(true);
                        btnGetDirection.setFocusable(true);
                        bookedLayout.setVisibility(View.VISIBLE);

                        Bundle bundle = new Bundle();
                        bundle.putBoolean("m", false); //m for more button check
                        ScheduleFragment scheduleFragment = new ScheduleFragment();
                        scheduleFragment.setArguments(bundle);
                        listener.fragmentChange(scheduleFragment);
                        bottomSheet.setVisibility(View.GONE);
                    }
                    SharedData.getInstance().setSensorArea(null);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight(400);
                }
            }
        });

        btnSearchGetDirection.setOnClickListener(v -> {
//            Toast.makeText(context, "search", Toast.LENGTH_SHORT).show();
            if (getDirectionSearchButtonClicked == 0) {
//                Timber.e("1st click getDirectionSearchButtonClicked before increase-> %s", getDirectionButtonClicked);
                getDirectionSearchButtonClicked++;
//                Timber.e("1st click getDirectionSearchButtonClicked after increase-> %s", getDirectionButtonClicked);
                if (searchPlaceLatLng != null) {
                    EventBus.getDefault().post(new GetDirectionForSearchEvent(searchPlaceLatLng));
                    fetchSensors(onConnectedLocation);
                    buttonSearch.setVisibility(View.GONE);
                    MarkerOptions markerDestinationPositionOptions = new MarkerOptions();
                    markerDestinationPositionOptions.position(searchPlaceLatLng);
//                    markerDestinationPositionOptions.title(name);
//                    markerDestinationPositionOptions.draggable(true);
                    coordList.add(new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
//                    markerDestinationPositionOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//                    markerDestinationPositionOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                    mMap.addMarker(markerDestinationPositionOptions).setFlat(true);
//              move map camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 13.5f));
                    linearLayoutSearchBottomButton.setVisibility(View.VISIBLE);
                    linearLayoutBottom.setVisibility(View.GONE);
                    linearLayoutMarkerBottom.setVisibility(View.GONE);
                    imageViewSearchBack.setVisibility(View.VISIBLE);

                    btnSearchGetDirection.setText("Confirm Booking");
                    btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                    btnSearchGetDirection.setEnabled(false);
                    btnSearchGetDirection.setFocusable(false);

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight(400);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ApplicationUtils.showMessageDialog("Once reach your destination, \nConfirm Booking Button will be enabled &" +
                                    " you can reserve your booking spot!!!", context);
                        }
                    }, 1000);
                }
            } else if (getDirectionSearchButtonClicked == 1) {
//                ApplicationUtils.showMessageDialog("Once reach your destination you can reserve your booking spot!!!", context);
//                Timber.e("2nd click getDirectionSearchButtonClicked before decrease -> %s", getDirectionButtonClicked);
//                getDirectionSearchButtonClicked--;
//                Timber.e("2nd click getDirectionSearchButtonClicked after decrease-> %s", getDirectionButtonClicked);
                if (mMap != null) {
//                    mMap.clear();
//                    animateCamera(onConnectedLocation);
//                    bottomSheetBehavior.setPeekHeight(400);
//                    fetchSensors(onConnectedLocation);
//                    buttonSearch.setVisibility(View.VISIBLE);
//                    fetchBottomSheetSensors(onConnectedLocation);
//                    buttonSearch.setText(null);
//                    bookingSensorsArrayList.clear();
//                    bookingSensorsArrayListGlobal.clear();
//                    bookingSensorsMarkerArrayList.clear();
//                    layoutSearchVisible(false, "", "", "", null);
//                    btnSearchGetDirection.setText("Get Direction");
//                    btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
//                    linearLayoutBottom.setVisibility(View.GONE);
//                    linearLayoutSearchBottom.setVisibility(View.GONE);
//                    linearLayoutMarkerBottom.setVisibility(View.GONE);
//                    linearLayoutSearchNameCount.setVisibility(View.GONE);
//                    BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//                    navBar.setVisibility(View.VISIBLE);
                    TaskParser taskParser = new TaskParser();
                    double distance = taskParser.showDistance(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                            new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
                    if (distance < 0.1) {
                        btnSearchGetDirection.setText("Confirm Booking");
                        btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                        btnSearchGetDirection.setEnabled(true);
                        btnSearchGetDirection.setFocusable(true);
                        bookedLayout.setVisibility(View.VISIBLE);

                        Bundle bundle = new Bundle();
                        bundle.putBoolean("m", false); //m for more
                        ScheduleFragment scheduleFragment = new ScheduleFragment();
                        scheduleFragment.setArguments(bundle);
                        listener.fragmentChange(scheduleFragment);
                        bottomSheet.setVisibility(View.GONE);
                    }
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight(400);
                }
            }
        });

        btnMarkerGetDirection.setOnClickListener(v -> {
//            Toast.makeText(context, "marker", Toast.LENGTH_SHORT).show();
            if (getDirectionMarkerButtonClicked == 0) {
                getDirectionMarkerButtonClicked++;
                if (markerPlaceLatLng != null) {
                    EventBus.getDefault().post(new GetDirectionForMarkerEvent(markerPlaceLatLng));
                    linearLayoutMarkerNameCount.setVisibility(View.GONE);
                    fetchSensors(onConnectedLocation);
                    buttonSearch.setVisibility(View.GONE);
                    bookingSensorsArrayListGlobal.clear();
                    bookingSensorsArrayList.clear();
//                    fetchBottomSheetSensors(onConnectedLocation);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(markerPlaceLatLng);
//                    markerOptions.title(name);
//                    markerOptions.draggable(true);
                    coordList.add(new LatLng(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude));
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                    mMap.addMarker(markerOptions).setFlat(true);
//              move map camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPlaceLatLng, 13.5f));
                    linearLayoutNameCount.setVisibility(View.GONE);
                    linearLayoutSearchBottom.setVisibility(View.GONE);
                    linearLayoutBottom.setVisibility(View.GONE);
                    linearLayoutMarkerNameCount.setVisibility(View.GONE);
//                    linearLayoutMarkerBottom.setVisibility(View.VISIBLE);
                    imageViewMarkerBack.setVisibility(View.VISIBLE);
                    btnMarkerGetDirection.setText("Confirm Booking");
                    btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                    btnMarkerGetDirection.setEnabled(false);
                    btnMarkerGetDirection.setFocusable(false);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight(400);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ApplicationUtils.showMessageDialog("Once reach your destination, \nConfirm Booking Button will be enabled &" +
                                    " you can reserve your booking spot!!!", context);
                        }
                    }, 1000);
                }
            } else if (getDirectionMarkerButtonClicked == 1) {
//                ApplicationUtils.showMessageDialog("Once reach your destination you can reserve your booking spot!!!", context);
//                getDirectionMarkerButtonClicked--;
                if (mMap != null) {
//                    mMap.clear();
//                    animateCamera(onConnectedLocation);
//                    fetchSensors(onConnectedLocation);
//                    buttonSearch.setVisibility(View.VISIBLE);
//                    bookingSensorsArrayListGlobal.clear();
//                    bookingSensorsArrayList.clear();
//                    bookingSensorsMarkerArrayList.clear();
//                    fetchBottomSheetSensors(onConnectedLocation);
//                    layoutMarkerVisible(false, "", "", "", null);
//                    linearLayoutBottom.setVisibility(View.GONE);
//                    linearLayoutSearchBottom.setVisibility(View.GONE);
//                    btnMarkerGetDirection.setText("Get Direction");
//                    btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
//                    BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//                    navBar.setVisibility(View.VISIBLE);
//                    fromMarkerRouteDrawn = 0;
//                    markerAlreadyClicked = 0;
//                    Timber.e("btnMarkerGetDirection flag ----> markerAlreadyClicked -> %s", markerAlreadyClicked);
                    TaskParser taskParser = new TaskParser();
                    double distance = taskParser.showDistance(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                            new LatLng(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude));
                    if (distance < 0.1) {
                        btnMarkerGetDirection.setText("Confirm Booking");
                        btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                        btnMarkerGetDirection.setEnabled(true);
                        btnMarkerGetDirection.setFocusable(true);
                        bookedLayout.setVisibility(View.VISIBLE);

                        Bundle bundle = new Bundle();
                        bundle.putBoolean("m", false); //m for more
                        ScheduleFragment scheduleFragment = new ScheduleFragment();
                        scheduleFragment.setArguments(bundle);
                        listener.fragmentChange(scheduleFragment);
                        bottomSheet.setVisibility(View.GONE);
                    }
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight(400);
                }
            }
        });

        btnBottomSheetGetDirection.setOnClickListener(v -> {
//            Toast.makeText(context, "bottom sheet", Toast.LENGTH_SHORT).show();
            if (getDirectionBottomSheetButtonClicked == 0) {
                getDirectionBottomSheetButtonClicked++;
                if (bottomSheetPlaceLatLng != null) {
                    EventBus.getDefault().post(new GetDirectionBottomSheetEvent(bottomSheetPlaceLatLng));
                    fetchSensors(onConnectedLocation);
                    bookingSensorsArrayListGlobal.clear();
                    bookingSensorsArrayList.clear();
//                    fetchBottomSheetSensors(onConnectedLocation);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(bottomSheetPlaceLatLng);
//                    markerOptions.title(name);
                    coordList.add(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                    mMap.addMarker(markerOptions).setFlat(true);
                    //move map camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bottomSheetPlaceLatLng, 13.5f));
//                    linearLayoutNameCount.setVisibility(View.VISIBLE);
                    linearLayoutSearchBottom.setVisibility(View.GONE);
                    linearLayoutBottom.setVisibility(View.GONE);
                    linearLayoutMarkerBottom.setVisibility(View.GONE);
//                    imageViewMarkerBack.setVisibility(View.GONE);
                    linearLayoutBottomSheetBottom.setVisibility(View.VISIBLE);
                    imageViewBottomSheetBack.setVisibility(View.VISIBLE);

                    btnBottomSheetGetDirection.setText("Confirm Booking");
                    btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                    btnBottomSheetGetDirection.setEnabled(false);
                    btnBottomSheetGetDirection.setFocusable(false);

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight(400);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ApplicationUtils.showMessageDialog("Once reach your destination, \nConfirm Booking Button will be enabled &" +
                                    " you can reserve your booking spot!!!", context);
                        }
                    }, 1000);
                }
            } else if (getDirectionBottomSheetButtonClicked == 1) {
//                getDirectionBottomSheetButtonClicked--;
//                ApplicationUtils.showMessageDialog("Once reach your destination you can reserve your booking spot!!!", context);
                if (mMap != null) {
//                    mMap.clear();
//                    animateCamera(onConnectedLocation);
//                    buttonSearch.setText(null);
//                    buttonSearch.setVisibility(View.VISIBLE);
//                    bottomSheetBehavior.setPeekHeight(400);
////                    if (bottomSheetPlaceLatLng != null) {
////                        Timber.e("bottomSheetPlaceLatLng if  dhukche");
////                        getActivity().getSupportFragmentManager().beginTransaction().replace(this.getId(),
////                                new HomeFragment()).commit();
////                    } else {
////                        Timber.e("searchPlaceLatLng else  dhukche");
////                        getActivity().getSupportFragmentManager().beginTransaction().replace(this.getId(),
////                                new HomeFragment()).commit();
////                    }
////                    LatLng latLng = new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude());
////                    MarkerOptions markerOptions = new MarkerOptions();
////                    markerOptions.position(latLng);
////                    markerOptions.title("Current Position");
//////                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
////                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_small));
////                    mMap.addMarker(markerOptions).setFlat(true);
//                    fetchSensors(onConnectedLocation);
//                    bookingSensorsArrayListGlobal.clear();
//                    bookingSensorsArrayList.clear();
//                    bookingSensorsMarkerArrayList.clear();
//                    fetchBottomSheetSensors(onConnectedLocation);
//                    layoutBottomSheetVisible(false, "", "", "", "", null);
//                    linearLayoutBottom.setVisibility(View.GONE);
//                    linearLayoutSearchBottom.setVisibility(View.GONE);
//                    linearLayoutMarkerBottom.setVisibility(View.GONE);
//                    btnBottomSheetGetDirection.setText("Get Direction");
//                    btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
//                    BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//                    navBar.setVisibility(View.VISIBLE);
                    fromMarkerRouteDrawn = 0;
                    markerAlreadyClicked = 0;
                    TaskParser taskParser = new TaskParser();
                    double distance = taskParser.showDistance(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                            new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
                    if (distance < 0.1) {
                        btnBottomSheetGetDirection.setText("Confirm Booking");
                        btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                        btnBottomSheetGetDirection.setEnabled(true);
                        btnBottomSheetGetDirection.setFocusable(true);
                        bookedLayout.setVisibility(View.VISIBLE);

                        Bundle bundle = new Bundle();
                        bundle.putBoolean("m", false); //m for more
                        ScheduleFragment scheduleFragment = new ScheduleFragment();
                        scheduleFragment.setArguments(bundle);
                        listener.fragmentChange(scheduleFragment);
                        bottomSheet.setVisibility(View.GONE);
                    }
//                    Timber.e("btnBottomSheetGetDirection flag ----> markerAlreadyClicked -> %s", markerAlreadyClicked);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight(400);
                }
            }
        });

        moreBtn.setOnClickListener(v -> {
            ScheduleFragment scheduleFragment = new ScheduleFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("m", true);
            bundle.putLong("a", arrived);
            bundle.putLong("d", departure);
            scheduleFragment.setArguments(bundle);
            listener.fragmentChange(scheduleFragment);
        });

        btnLiveParking.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());

        textViewTermsCondition.setOnClickListener(v -> Toast.makeText(context, "Coming Soon...", Toast.LENGTH_SHORT).show());
    }

    private void setTimer(long difference) {
        new CountDownTimer(difference, 1000) {

            public void onTick(long millisUntilFinished) {
                countDownTV.setText(getTimeDiffrence(millisUntilFinished));
            }

            public void onFinish() {
                countDownTV.setText("done!");
            }
        }.start();
    }

    @SuppressLint("DefaultLocale")
    private String getTimeDiffrence(long difference) {

        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(difference),
                TimeUnit.MILLISECONDS.toMinutes(difference) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(difference)) // The change is in this line
        );
    }

    private String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

