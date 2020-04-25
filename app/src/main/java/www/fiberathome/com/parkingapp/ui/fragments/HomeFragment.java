package www.fiberathome.com.parkingapp.ui.fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
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
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.GoogleMapWebServiceNDistance.DirectionsParser;
import www.fiberathome.com.parkingapp.GoogleMapWebServiceNDistance.GooglePlaceSearchNearbySearchListener;
import www.fiberathome.com.parkingapp.GoogleMapWebServiceNDistance.MovingMarker.LatLngInterpolator;
import www.fiberathome.com.parkingapp.GoogleMapWebServiceNDistance.MovingMarker.MarkerAnimation;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.AppConfig;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionAfterButtonClickEvent;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionBottomSheetEvent;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionForMarkerEvent;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionForSearchEvent;
import www.fiberathome.com.parkingapp.eventBus.SetMarkerEvent;
import www.fiberathome.com.parkingapp.gps.GPSTracker;
import www.fiberathome.com.parkingapp.gps.GPSTrackerListener;
import www.fiberathome.com.parkingapp.model.BookingSensors;
import www.fiberathome.com.parkingapp.model.GlobalVars;
import www.fiberathome.com.parkingapp.model.MyLocation;
import www.fiberathome.com.parkingapp.model.SensorArea;
import www.fiberathome.com.parkingapp.model.SensorList;
import www.fiberathome.com.parkingapp.module.PlayerPrefs;
import www.fiberathome.com.parkingapp.ui.DialogForm;
import www.fiberathome.com.parkingapp.ui.booking.BookingSensorAdapter;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.RecyclerTouchListener;
import www.fiberathome.com.parkingapp.utils.SharedData;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

// Add an import statement for the client library.

/**
 * A simple {@link Fragment} subclass.
 */

public class HomeFragment extends Fragment implements
        OnMapReadyCallback, GooglePlaceSearchNearbySearchListener, GoogleMap.OnMarkerClickListener,
        GPSTrackerListener, GoogleMap.OnInfoWindowClickListener, View.OnClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraIdleListener, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

//    LocationListener interface for onLocationChanged method

    //from adapter
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
    LinearLayout linearLayoutSearchBottom;
    @BindView(R.id.linearLayoutSearchBottomButton)
    LinearLayout linearLayoutSearchBottomButton;
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

    private Context context;
    private String name, count = "";
    private String distance;
    private String duration;
    public String address, city, state, country, subAdminArea, test, knownName, postalCode = "";

    private static final String TAG = HomeFragment.class.getSimpleName();
    // google map objects
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;

    //Current Marker Moving Variables
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5445;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentLocationMarker;
    public static Location currentLocation = null;
    private boolean firstTimeFlag = true;

    public String selectedSensor;
    public String selectedSensorStatus = "Occupied";
    public TextView parkingReqSpot;
    public double nDistance = 132116456;
    public double nLatitude;
    public double nLongitude;

    /*Authors: Shawn And  Maruf*/
    public static LatLng location;
    private LatLng nearestRouteCoordinate;
    public LatLng searchPlaceLatLng;
    private LatLng markerPlaceLatLng;
    private LatLng bottomSheetPlaceLatLng;
    public static final int TWO_MINUTES = 1000 * 60 * 2;
    // global view
    private View view;
    ArrayList<LatLng> coordList = new ArrayList<LatLng>();
    private SupportMapFragment supportMapFragment;
    private View mapView;
    private GoogleMap googleMap;
    private Marker userLocationMarker;
    private GPSTracker gpsTracker;
    private Button nearest;
    private boolean isGoogleDone = false;
    // global distance
    private boolean isMyServerDone = false;
    private String sensorStatus = "Occupied";
    private static final int LOCATION_REQUEST = 500;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 22;
    public int flag = 0;
    ArrayList<LatLng> listPoints;
    public static DecimalFormat df1 = new DecimalFormat(".##");
    public static DecimalFormat df2 = new DecimalFormat(".##");
    private AutocompleteSupportFragment autocompleteFragment;
    private AutocompleteFilter typeFilter;
    private String parkingNumberOfIndividualMarker = "";

    //Todo: Get SensorArea Data in Suitable LifeCycle Method from SharedData after selecting from Parking Adapter

    private List<LatLng> polyLineList;
    private Marker marker;
    private LocationManager mLocationManager;

    public Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private int getDirectionButtonClicked = 0;
    private int getDirectionSearchButtonClicked = 0;
    private int getDirectionMarkerButtonClicked = 0;
    private int getDirectionBottomSheetButtonClicked = 0;
    private ProgressDialog progressDialog;
    private int markerAlreadyClicked = 0;
    private int fromMarkerRouteDrawn = 0;
    private static float angle;

    //booking
    private ArrayList<BookingSensors> bookingSensorsArrayListGlobal = new ArrayList<>();
    public RecyclerView bottomSheetRecyclerView;
    private BookingSensorAdapter bookingSensorAdapter;

    public BottomSheetBehavior bottomSheetBehavior;
    private CoordinatorLayout coordinatorLayout;
    private ProgressDialog bottomSheetProgressDialog;

    private boolean isFirstTime = false;
    private boolean isMarkerRotating = false;
    GoogleMap.CancelableCallback callback;
    private boolean animatePath, repeatDrawingPath;
    public int bottomSheetSearch = 0;
    private float bearing = 0.5f;

    public HomeFragment() {

    }

    private final View.OnClickListener clickListener = view -> {
        if (view.getId() == R.id.currentLocationImageButton && googleMap != null && currentLocation != null)
            animateCamera(currentLocation);
    };

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Timber.e("mLocationCallback call hoiche");
            if (locationResult.getLastLocation() == null)
                return;
            currentLocation = locationResult.getLastLocation();
            Timber.e("mLocationCallback currentLocation -> %s", currentLocation);
            if (firstTimeFlag && googleMap != null) {
                animateCamera(currentLocation);
                firstTimeFlag = false;
            }
            showMarker(currentLocation);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.e("onCreate called");
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (mGoogleApiClient != null &&
                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        if (!checkPermission()) {
            requestPermission();
        }
//        callback = new GoogleMap.CancelableCallback() {
//            @Override
//            public void onFinish() {
//            }
//
//            @Override
//            public void onCancel() {
//            }
//        };

        listPoints = new ArrayList<>();
        polyLineList = new ArrayList<>();
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.e("onCreateView called");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        initUI(view);
        bottomSheetRecyclerView = view.findViewById(R.id.bottomsheet_recyclerview);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayoutBottomSheet);
        LinearLayout bottomSheet = view.findViewById(R.id.layout_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(100);
        bottomSheetBehavior.setHideable(false);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
//                        btn.setText("Close Sheet");
                    case BottomSheetBehavior.STATE_COLLAPSED:
//                        btn.setText("Expand Sheet");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        //fetch booking spot
        fetchBottomSheetSensors();
        setBottomSheetFragmentControls(bookingSensorsArrayListGlobal);
        //fetch parking spot
        fetchSensors();
        setListeners();
        //value getting from parking adapter
        if (SharedData.getInstance().getSensorArea() != null) {
            bottomSheetBehavior.setPeekHeight(300);
            SensorArea sensorArea = SharedData.getInstance().getSensorArea();
            Timber.e("Sensor Area from SharedData -> %s", new Gson().toJson(sensorArea));
            textViewParkingAreaName.setText(ApplicationUtils.capitalize(sensorArea.getParkingArea()));
            textViewParkingAreaCount.setText(sensorArea.getCount());
            String distance = new DecimalFormat("##.##").format(sensorArea.getDistance()) + " km";
            textViewParkingDistance.setText(distance);
//            textViewParkingTravelTime.setText(sensorArea.getDuration());
            getDestinationInfoForDuration(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                    new LatLng(sensorArea.getLat(), sensorArea.getLng()));
        } else {
            Timber.e("Genjam");
        }

        try {
            initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        Timber.e("onMapReady called");
        this.googleMap = mMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        currentLocation = getLastBestLocation();
        Timber.e("getLastBestLocation currentBestLocation onMapReady -> %s", currentLocation);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
//                googleMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
//            googleMap.setMyLocationEnabled(true);
        }
        geoLocate();
//        googleMap.setMyLocationEnabled(true);
//        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setOnInfoWindowClickListener(this);
//        refreshUserGPSLocation();
        //Registering the listener to update location for every 100m displacement
//        startLocationUpdates();
        //getting the latest point from the queue for every 5 seconds
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            SubscribetoTimer();
//        }
//        initGPS();
//        geoLocate();
//        googleMap.setMyLocationEnabled(true);
//        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        //  Place current location marker
//        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Position");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_car_running));
//        googleMap.addMarker(markerOptions);
//        currentLocationMarker = googleMap.addMarker(markerOptions);

        //move map camera
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 0));
//        googleMap.getUiSettings().setZoomControlsEnabled(true);
//        if (googleMap != null) {
//            googleMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("Current Position"));
//            if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext()
//                    , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext()
//                    , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//        }
//
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (ContextCompat.checkSelfPermission(getActivity(),
//                        Manifest.permission.ACCESS_FINE_LOCATION)
//                        == PackageManager.PERMISSION_GRANTED) {
//                    buildGoogleApiClient();
//                    googleMap.setMyLocationEnabled(true);
//                }
//            } else {
//                buildGoogleApiClient();
//                googleMap.setMyLocationEnabled(true);
//            }
//
//        }
//        initGPS();
//        refreshUserGPSLocation();
//        geoLocate();
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
//            return;
//        }
//        googleMap.setMyLocationEnabled(true);
//        googleMap.setOnMyLocationChangeListener(myLocationChangeListener);

//        getDestinationInfo(new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude));

    }

    // Trigger new location updates at interval
//    @SuppressLint("MissingPermission")
//    protected void startLocationUpdates() {
//
//        // Create the location request to start receiving updates
//        LocationRequest mLocationRequest = new LocationRequest();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setSmallestDisplacement(100f);
//        mLocationRequest.setInterval(0);
//        mLocationRequest.setFastestInterval(0);
//
//        // Create LocationSettingsRequest object using location request
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
//        builder.addLocationRequest(mLocationRequest);
//        LocationSettingsRequest locationSettingsRequest = builder.build();
//
//        // Check whether location settings are satisfied
//        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
//        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
//        settingsClient.checkLocationSettings(locationSettingsRequest);
//
//        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
//        getFusedLocationProviderClient(getActivity()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
//                    @Override
//                    public void onLocationResult(LocationResult locationResult) {
//                        points.add(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()));
//
//                    }
//                },
//                Looper.myLooper());
//    }
//
//    @SuppressLint("CheckResult")
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private void SubscribetoTimer() {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            Observable.interval(5, TimeUnit.MILLISECONDS)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<Long>() {
//                        @Override
//                        public void accept(Long aLong) throws Exception {
//                            SendNextPoints();
//                        }
//                    });
//        }
//    }
//
//
//    private void SendNextPoints() {
//
//        if (!animatorSet.isRunning() && !points.isEmpty())
//            UpdateMarker(points.poll()); // taking the points f rom head of the queue.
//
//    }
//
//    private void UpdateMarker(LatLng newlatlng) {
//
//        if (marker != null) {
//            float bearingangle = Calculatebearingagle(newlatlng);
//            marker.setAnchor(0.5f, 0.5f);
//            animatorSet = new AnimatorSet();
//            animatorSet.playTogether(rotateMarker(Float.isNaN(bearingangle) ? -1 : bearingangle, marker.getRotation()), moveVechile(newlatlng, marker.getPosition()));
//            animatorSet.start();
//        } else
//            AddMarker(newlatlng);
//
//
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition
//                (new CameraPosition.Builder().target(newlatlng)
//                        .zoom(16f).build()));
//
//
//    }

//    private void AddMarker(LatLng initialpos) {
//
//        MarkerOptions markerOptions = new MarkerOptions().position(initialpos).flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_car_running));
//        marker = googleMap.addMarker(markerOptions);
//
//
//    }

    private float Calculatebearingagle(LatLng newlatlng) {
        Location destinationLoc = new Location("service Provider");
        Location userLoc = new Location("service Provider");
        userLoc.setLatitude(marker.getPosition().latitude);
        userLoc.setLongitude(marker.getPosition().longitude);

        destinationLoc.setLatitude(newlatlng.latitude);
        destinationLoc.setLongitude(newlatlng.longitude);

        return userLoc.bearingTo(destinationLoc);

    }

    public synchronized ValueAnimator rotateMarker(final float toRotation, final float startRotation) {

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(1555);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                float t = Float.parseFloat(valueAnimator.getAnimatedValue().toString());

                float rot = t * toRotation + (1 - t) * startRotation;

                marker.setRotation(-rot > 180 ? rot / 2 : rot);


            }
        });
        return valueAnimator;
    }

    public synchronized ValueAnimator moveVechile(final LatLng finalPosition, final LatLng startPosition) {

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(3000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float t = Float.parseFloat(valueAnimator.getAnimatedValue().toString());

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + (finalPosition.latitude) * t,
                        startPosition.longitude * (1 - t) + (finalPosition.longitude) * t);
                marker.setPosition(currentPosition);


            }
        });

        return valueAnimator;
    }

    private void startCurrentLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                return;
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (ConnectionResult.SUCCESS == status)
            return true;
        else {
            if (googleApiAvailability.isUserResolvableError(status))
                Toast.makeText(getActivity(), "Please Install google play services to use this application", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void animateCamera(@NonNull Location location) {
        Timber.e("animateCamera call hoiche");
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)));
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        Timber.e("getCameraPositionWithBearing call hoiche");
        return new CameraPosition.Builder().target(latLng).zoom(13).build();
    }

    private void showMarker(@NonNull Location currentLocation) {
        Timber.d("showMarker call hoiche");
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        if (currentLocationMarker == null) {
//            currentLocationMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(latLng).flat(true));
            currentLocationMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_small)).position(latLng).flat(true)
                    .anchor(0.5f, 0.5f)
                    .rotation(bearing));
        } else
            MarkerAnimation.animateMarkerToGB(currentLocationMarker, latLng, new LatLngInterpolator.Spherical());
    }

    private void initUI(View view) {
        view.findViewById(R.id.currentLocationImageButton).setOnClickListener(clickListener);
        autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
    }

    private void setListeners() {

        imageViewBack.setOnClickListener(v -> {
            if (googleMap != null) {
                bottomSheetBehavior.setPeekHeight(100);
                googleMap.clear();
//                onLocationChanged(currentLocation);
                animateCamera(currentLocation);
//                showMarker(currentLocation);
                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
//                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_small));
                googleMap.addMarker(markerOptions).setFlat(true);
                fetchSensors();
                fetchBottomSheetSensors();
                setBottomSheetFragmentControls(bookingSensorsArrayListGlobal);
                BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                navBar.setVisibility(View.VISIBLE);
                layoutVisible(false, "", "", " ", null);
                linearLayoutBottom.setVisibility(View.GONE);
                linearLayoutSearchBottom.setVisibility(View.GONE);
                linearLayoutMarkerBottom.setVisibility(View.GONE);
                SharedData.getInstance().setSensorArea(null);
            }
        });

        imageViewSearchBack.setOnClickListener(v -> {
            if (googleMap != null) {
                Timber.e("imageViewSearchBack call hoiche");
//                imageViewSearchBackClicked = true;
                btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                bottomSheetBehavior.setPeekHeight(100);
                googleMap.clear();
//                onLocationChanged(currentLocation);
                animateCamera(currentLocation);
                if (getDirectionSearchButtonClicked == 1) {
                    btnSearchGetDirection.setText("Get Direction");
                    btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                    getDirectionSearchButtonClicked--;
                }
//                showMarker(currentLocation);
                autocompleteFragment.setText("");
                fetchSensors();
                fetchBottomSheetSensors();
                setBottomSheetFragmentControls(bookingSensorsArrayListGlobal);
                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
//                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_small));
                googleMap.addMarker(markerOptions).setFlat(true);
                BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                navBar.setVisibility(View.VISIBLE);
                layoutSearchVisible(false, "", "", "", null);
                linearLayoutBottom.setVisibility(View.GONE);
                linearLayoutSearchBottom.setVisibility(View.GONE);
                linearLayoutMarkerBottom.setVisibility(View.GONE);
                SharedData.getInstance().setBookingSensors(null);
            }
        });

        imageViewMarkerBack.setOnClickListener(v -> {
            if (googleMap != null) {
                bottomSheetBehavior.setPeekHeight(100);
                googleMap.clear();
//                onLocationChanged(currentLocation);
                animateCamera(currentLocation);
                if (getDirectionMarkerButtonClicked == 1) {
                    btnMarkerGetDirection.setText("Get Direction");
                    btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                    getDirectionMarkerButtonClicked--;
                }
//                showMarker(currentLocation);
                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
//                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_small));
                googleMap.addMarker(markerOptions).setFlat(true);
                fetchSensors();
                fetchBottomSheetSensors();
                setBottomSheetFragmentControls(bookingSensorsArrayListGlobal);
                BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                navBar.setVisibility(View.VISIBLE);
                layoutMarkerVisible(false, "", "", "", null);
                linearLayoutBottom.setVisibility(View.GONE);
                linearLayoutSearchBottom.setVisibility(View.GONE);
                linearLayoutMarkerBottom.setVisibility(View.GONE);
                markerAlreadyClicked = 0;
            }
        });

        imageViewBottomSheetBack.setOnClickListener(v -> {
            if (googleMap != null) {
                bottomSheetBehavior.setPeekHeight(100);
                googleMap.clear();
                autocompleteFragment.setText("");
//                onLocationChanged(currentLocation);
                animateCamera(currentLocation);
                fetchSensors();
                fetchBottomSheetSensors();
                setBottomSheetFragmentControls(bookingSensorsArrayListGlobal);
                if (getDirectionBottomSheetButtonClicked == 1) {
                    btnBottomSheetGetDirection.setText("Get Direction");
                    btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                    getDirectionBottomSheetButtonClicked--;
                }
//                showMarker(currentLocation);
                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
//                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_small));
                googleMap.addMarker(markerOptions).setFlat(true);
                BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                navBar.setVisibility(View.VISIBLE);
                layoutBottomSheetVisible(false, "", "", "", "", null);
                linearLayoutBottom.setVisibility(View.GONE);
                linearLayoutSearchBottom.setVisibility(View.GONE);
                linearLayoutMarkerBottom.setVisibility(View.GONE);
                markerAlreadyClicked = 0;
            }
        });

        btnGetDirection.setOnClickListener(v -> {
            if (getDirectionButtonClicked == 0) {
                getDirectionButtonClicked++;
                if (location != null) {
                    EventBus.getDefault().post(new GetDirectionAfterButtonClickEvent(location));
//                    progressDialog = new ProgressDialog(getActivity());
//                    progressDialog.setMessage("Please wait...");
//                    progressDialog.show();
//                btnGetDirection.setVisibility(View.GONE);
//                    onLocationChanged(mLastLocation);
//                    onLocationChanged(currentLocation);
                    animateCamera(currentLocation);
//                    showMarker(currentLocation);
                    fetchSensors();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(location);
                    markerOptions.title(name);
                    markerOptions.draggable(true);
                    coordList.add(new LatLng(location.latitude, location.longitude));
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    googleMap.addMarker(markerOptions).setFlat(true);
//              move map camera
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
//                    linearLayoutNameCount.setVisibility(View.VISIBLE);
                    linearLayoutBottom.setVisibility(View.VISIBLE);
                    linearLayoutSearchBottom.setVisibility(View.GONE);
                    linearLayoutMarkerBottom.setVisibility(View.GONE);
                    imageViewBack.setVisibility(View.VISIBLE);
                    btnGetDirection.setText("Cancel Direction");
                    btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.red));
                    BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                    navBar.setVisibility(View.GONE);
//                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
//                    btnGetDirection.setLayoutParams(layoutParams);
                }
            } else if (getDirectionButtonClicked == 1) {
                getDirectionButtonClicked--;
                if (googleMap != null) {
                    bottomSheetBehavior.setPeekHeight(100);
                    googleMap.clear();
                    autocompleteFragment.setText("");
//                    onLocationChanged(mLastLocation);
//                    onLocationChanged(currentLocation);
                    animateCamera(currentLocation);
//                    showMarker(currentLocation);
                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Current Position");
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_small));
                    googleMap.addMarker(markerOptions).setFlat(true);
                    fetchSensors();
                    layoutVisible(false, "", "", "", null);
                    linearLayoutBottom.setVisibility(View.GONE);
                    linearLayoutSearchBottom.setVisibility(View.GONE);
                    linearLayoutMarkerBottom.setVisibility(View.GONE);
//                    linearLayoutNameCount.setVisibility(View.GONE);
                    btnGetDirection.setText("Get Direction");
                    btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                    BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                    navBar.setVisibility(View.VISIBLE);
                    SharedData.getInstance().setSensorArea(null);
                }
            }
        });

        btnSearchGetDirection.setOnClickListener(v -> {
            if (getDirectionSearchButtonClicked == 0) {
                Timber.e("1st click getDirectionSearchButtonClicked before increase-> %s", getDirectionButtonClicked);
                getDirectionSearchButtonClicked++;
                Timber.e("1st click getDirectionSearchButtonClicked after increase-> %s", getDirectionButtonClicked);
                if (searchPlaceLatLng != null) {
                    EventBus.getDefault().post(new GetDirectionForSearchEvent(searchPlaceLatLng));
//                    onLocationChanged(currentLocation);
                    animateCamera(currentLocation);
//                    showMarker(currentLocation);
                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Current Position");
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_small));
                    googleMap.addMarker(markerOptions).setFlat(true);
                    fetchSensors();
                    MarkerOptions markerDestinationPositionOptions = new MarkerOptions();
                    markerDestinationPositionOptions.position(searchPlaceLatLng);
                    markerDestinationPositionOptions.title(name);
                    markerDestinationPositionOptions.draggable(true);
                    coordList.add(new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
                    markerDestinationPositionOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    googleMap.addMarker(markerDestinationPositionOptions).setFlat(true);
//              move map camera
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 13));
//                    linearLayoutNameCount.setVisibility(View.VISIBLE);
//                    linearLayoutSearchBottom.setVisibility(View.VISIBLE);
                    linearLayoutSearchBottomButton.setVisibility(View.VISIBLE);
                    linearLayoutBottom.setVisibility(View.GONE);
                    linearLayoutMarkerBottom.setVisibility(View.GONE);
                    imageViewSearchBack.setVisibility(View.VISIBLE);
//                    if (imageViewSearchBackClicked){
//                        btnSearchGetDirection.setText("Get Direction");
//                        btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
//                    }else{
                    btnSearchGetDirection.setText("Cancel Direction");
                    btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.red));
//                    }
                    BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                    navBar.setVisibility(View.GONE);
//                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
//                    btnGetDirection.setLayoutParams(layoutParams);
                }
            } else if (getDirectionSearchButtonClicked == 1) {
                Timber.e("2nd click getDirectionSearchButtonClicked before decrease -> %s", getDirectionButtonClicked);
                getDirectionSearchButtonClicked--;
                Timber.e("2nd click getDirectionSearchButtonClicked after decrease-> %s", getDirectionButtonClicked);
                if (googleMap != null) {
                    bottomSheetBehavior.setPeekHeight(100);
                    googleMap.clear();
                    autocompleteFragment.setText("");
                    fetchBottomSheetSensors();
                    setBottomSheetFragmentControls(bookingSensorsArrayListGlobal);
//                    onLocationChanged(mLastLocation);
//                    onLocationChanged(currentLocation);
//                    showMarker(currentLocation);
                    animateCamera(currentLocation);
                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Current Position");
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_small));
                    googleMap.addMarker(markerOptions).setFlat(true);
                    fetchSensors();
                    layoutSearchVisible(false, "", "", "", null);
                    btnSearchGetDirection.setText("Get Direction");
                    btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                    linearLayoutBottom.setVisibility(View.GONE);
                    linearLayoutSearchBottom.setVisibility(View.GONE);
                    linearLayoutMarkerBottom.setVisibility(View.GONE);
//                    linearLayoutSearchNameCount.setVisibility(View.GONE);
                    BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                    navBar.setVisibility(View.VISIBLE);
                }
            }
        });

        btnMarkerGetDirection.setOnClickListener(v -> {
            if (getDirectionMarkerButtonClicked == 0) {
                getDirectionMarkerButtonClicked++;
                if (markerPlaceLatLng != null) {
                    EventBus.getDefault().post(new GetDirectionForMarkerEvent(markerPlaceLatLng));
//                    progressDialog = new ProgressDialog(getActivity());
//                    progressDialog.setMessage("Please wait...");
//                    progressDialog.show();
//                btnGetDirection.setVisibility(View.GONE);
//                    onLocationChanged(mLastLocation);
//                    onLocationChanged(currentLocation);
                    animateCamera(currentLocation);
//                    showMarker(currentLocation);
                    fetchSensors();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(markerPlaceLatLng);
                    markerOptions.title(name);
                    markerOptions.draggable(true);
                    coordList.add(new LatLng(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude));
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    googleMap.addMarker(markerOptions).setFlat(true);
//              move map camera
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPlaceLatLng, 13));
//                    linearLayoutNameCount.setVisibility(View.VISIBLE);
                    linearLayoutSearchBottom.setVisibility(View.GONE);
                    linearLayoutBottom.setVisibility(View.GONE);
                    linearLayoutMarkerBottom.setVisibility(View.VISIBLE);
                    imageViewMarkerBack.setVisibility(View.VISIBLE);
                    btnMarkerGetDirection.setText("Cancel Direction");
                    btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.red));
                    BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                    navBar.setVisibility(View.GONE);
//                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
//                    btnGetDirection.setLayoutParams(layoutParams);
                }
            } else if (getDirectionMarkerButtonClicked == 1) {
                getDirectionMarkerButtonClicked--;
                if (googleMap != null) {
                    bottomSheetBehavior.setPeekHeight(100);
                    googleMap.clear();
                    autocompleteFragment.setText("");
//                    onLocationChanged(currentLocation);
                    animateCamera(currentLocation);
                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Current Position");
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_small));
                    googleMap.addMarker(markerOptions).setFlat(true);
                    fetchSensors();
                    layoutMarkerVisible(false, "", "", "", null);
                    linearLayoutBottom.setVisibility(View.GONE);
                    linearLayoutSearchBottom.setVisibility(View.GONE);
//                    linearLayoutMarkerNameCount.setVisibility(View.GONE);
//                    imageViewMarkerBack.setVisibility(View.GONE);
//                    btnMarkerGetDirection.setVisibility(View.GONE);
                    btnMarkerGetDirection.setText("Get Direction");
                    btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                    BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                    navBar.setVisibility(View.VISIBLE);
                    fromMarkerRouteDrawn = 0;
                    markerAlreadyClicked = 0;
                    Timber.e("btnMarkerGetDirection flag ----> markerAlreadyClicked -> %s", markerAlreadyClicked);
                }
            }
        });

        btnBottomSheetGetDirection.setOnClickListener(v -> {
            if (getDirectionBottomSheetButtonClicked == 0) {
                getDirectionBottomSheetButtonClicked++;
                if (bottomSheetPlaceLatLng != null) {
                    EventBus.getDefault().post(new GetDirectionBottomSheetEvent(bottomSheetPlaceLatLng));
//                    progressDialog = new ProgressDialog(getActivity());
//                    progressDialog.setMessage("Please wait...");
//                    progressDialog.show();
//                btnGetDirection.setVisibility(View.GONE);
//                    onLocationChanged(mLastLocation);
//                    onLocationChanged(currentLocation);
                    animateCamera(currentLocation);
//                    showMarker(currentLocation);
                    fetchSensors();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(bottomSheetPlaceLatLng);
                    markerOptions.title(name);
                    markerOptions.draggable(true);
                    coordList.add(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    googleMap.addMarker(markerOptions).setFlat(true);
                    //move map camera
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bottomSheetPlaceLatLng, 13));
//                    linearLayoutNameCount.setVisibility(View.VISIBLE);
                    linearLayoutSearchBottom.setVisibility(View.GONE);
                    linearLayoutBottom.setVisibility(View.GONE);
                    linearLayoutMarkerBottom.setVisibility(View.GONE);
                    imageViewMarkerBack.setVisibility(View.GONE);
                    linearLayoutBottomSheetBottom.setVisibility(View.VISIBLE);
                    imageViewBottomSheetBack.setVisibility(View.VISIBLE);
                    btnBottomSheetGetDirection.setText("Cancel Direction");
                    btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.red));
                    BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                    navBar.setVisibility(View.GONE);
//                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
//                    btnGetDirection.setLayoutParams(layoutParams);
                }
            } else if (getDirectionBottomSheetButtonClicked == 1) {
                getDirectionBottomSheetButtonClicked--;
                if (googleMap != null) {
                    bottomSheetBehavior.setPeekHeight(100);
                    googleMap.clear();
                    autocompleteFragment.setText("");
//                    onLocationChanged(currentLocation);
                    animateCamera(currentLocation);
                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Current Position");
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_small));
                    googleMap.addMarker(markerOptions).setFlat(true);
                    fetchSensors();
                    layoutBottomSheetVisible(false, "", "", "", "", null);
                    linearLayoutBottom.setVisibility(View.GONE);
                    linearLayoutSearchBottom.setVisibility(View.GONE);
                    linearLayoutMarkerBottom.setVisibility(View.GONE);
                    btnBottomSheetGetDirection.setText("Get Direction");
                    btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                    BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                    navBar.setVisibility(View.VISIBLE);
                    fromMarkerRouteDrawn = 0;
                    markerAlreadyClicked = 0;
                    Timber.e("btnBottomSheetGetDirection flag ----> markerAlreadyClicked -> %s", markerAlreadyClicked);
                }
            }
        });
    }

//    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
//        @Override
//        public void onMyLocationChange(Location location) {
//            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
////            if (googleMap != null) {
////                googleMap.clear();
////            googleMap.addMarker(new MarkerOptions()
////                    .position(loc)
////                    .title("Current Position"))
////                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_car_running));
////            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
////            googleMap.addMarker(new MarkerOptions().position(loc)).setVisible(true);
//
//            // Move the camera instantly to location with a zoom of 15.
////            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
//
//            // Zoom in, animating the camera.
////            googleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
////            }
//            Timber.e("OnMyLocationChangeListener onMyLocationChange called");
//        }
//    };

    //useful method for making marker
//    private void drawMarker(Location location) {
//        if (googleMap != null) {
//            googleMap.clear();
//            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
//            googleMap.addMarker(new MarkerOptions()
//                    .position(gps)
//                    .title("Current Position"))
//                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_car_running));
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 12));
//        }
//    }

    @Override
    public void onStart() {
        Timber.e("onStart called");
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        Timber.e("onResume called");
        super.onResume();
        nearest.setOnClickListener(this);
        if (isGooglePlayServicesAvailable()) {
            fusedLocationProviderClient = getFusedLocationProviderClient(getActivity());
            startCurrentLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        Timber.e("onPause called");
        super.onPause();
//        gpsTracker.stopUsingGPS();
        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onStop() {
        Timber.e("onStop called");
        EventBus.getDefault().unregister(this);
        super.onStop();
//        gpsTracker.stopUsingGPS();
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onDestroy() {
        Timber.e("onDestroy called");
        super.onDestroy();
        fusedLocationProviderClient = null;
        googleMap = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showMessage("Permission Granted, Now you can access location data.");
            } else {
                showMessage("Permission Denied, You cannot access location data.");
            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                Toast.makeText(getActivity(), "Permission denied by uses", Toast.LENGTH_SHORT).show();
            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startCurrentLocationUpdates();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
//        Timber.e("onLocationChanged called");
//        Toast.makeText(getActivity(), "Location Changed " + location.getLatitude()
//                + location.getLongitude(), Toast.LENGTH_LONG).show();
        currentLocation = location;
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
            fetchSensors();
            Timber.d("Firing onLocationChanged..............................................");
//            currentLocation = location;
//            updateUI();
        }

//        if (isFirstTime) {
//          Place current location marker
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
//          markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_small));
        currentLocationMarker = googleMap.addMarker(markerOptions);

        //move map camera
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//          googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
//            CameraUpdate center =
//                    CameraUpdateFactory.newLatLng(latLng);
//            CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);

//            googleMap.moveCamera(center);
//            googleMap.animateCamera(zoom);
        //move map camera
        // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.0f));
//            isFirstTime = false;
    }

//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 13));
//        if(currentLocationMarker == null) {
//            currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()))
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//        } else {
//            currentLocationMarker.setPosition(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
//        }

//        currentLocationMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
    //smoothly move the current position in Google Maps
//        refreshMapPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 45);

//        locationUpdate(location);
//        if (currentLocation != null) {
//            double bearing = angleFromCoordinate(currentLocation.getLatitude(), currentLocation.getLongitude(), location.getLatitude(), location.getLongitude());
//            changeMarkerPosition(bearing);
//        }
//        currentLocation = location;

//        currentLocation = getLastBestLocation();
//        Timber.e("currentBestLocation from getLastBestLocation()-> %s", currentLocation);
//        getLastBestLocation();
//        Timber.e("currentBestLocation from getLastBestLocation method -> %s", currentLocation);
//        fetchSensors();

//        makeUseOfNewLocation(location);
//        if (currentBestLocation == null) {
//            this.currentBestLocation = location;
//            Timber.e("currentBestLocation -> %s", currentBestLocation);
//        }
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
//                new LatLng(location.getLatitude(), location.getLongitude()), 15);
//        googleMap.animateCamera(cameraUpdate);

//        Toast.makeText(getActivity(), "Location Changed " + location.getLatitude()
//                + location.getLongitude(), Toast.LENGTH_LONG).show();

    //  Place current location marker


//        if(mCurrLocationMarker == null) {
//            Timber.e("mCurrLocationMarker is null");
//            mCurrLocationMarker = googleMap.addMarker(new MarkerOptions().position(latLng))
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_car_running));
//        } else {
//            Timber.e("mCurrLocationMarker is not null");
//            mCurrLocationMarker.remove();
//            mCurrLocationMarker.setPosition(latLng);
//        }

//        //stop location updates
//        if (mGoogleApiClient != null) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        }

//        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
//            @Override
//            public void onCameraIdle() {
//                LatLng center = googleMap.getCameraPosition().target;
//                if (marker != null) {
//                    marker.remove();
//                    googleMap.addMarker(new MarkerOptions().position(center).title("New Position"));
////                    LatLng latLng1 = marker.getPosition();
//                }
//            }
//        });
//    }

    private void locationUpdate(Location location) {
        LatLng latLng = new LatLng((location.getLatitude()), (location.getLongitude()));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_car_running));
        googleMap.clear();
        marker = googleMap.addMarker(markerOptions);
        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                .zoom(19)
                .tilt(30)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    private void changeMarkerPosition(double position) {
        float direction = (float) position;
        Timber.e("LocationBearing -> %s", direction);

        if (direction == 360.0) {
            //default
            marker.setRotation(angle);
        } else {
            marker.setRotation(direction);
            angle = direction;
        }
    }

    private double angleFromCoordinate(double lat1, double long1, double lat2,
                                       double long2) {
        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(x, y);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng;
        return brng;
    }

    private void refreshMapPosition(LatLng pos, float angle) {
        CameraPosition.Builder positionBuilder = new CameraPosition.Builder();
        positionBuilder.target(pos);
        positionBuilder.zoom(15f);
        positionBuilder.bearing(angle);
        positionBuilder.tilt(60);
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(positionBuilder.build()));
    }

    /**
     * @return the last know best location
     */
    private Location getLastBestLocation() {
        Timber.e("getLastBestLocation");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) {
            GPSLocationTime = locationGPS.getTime();
            Timber.e("GPSLocationTime -> %s", GPSLocationTime);
            currentLocation = locationGPS;
            Timber.e("currentBestLocation inside -> %s", currentLocation);
        }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if (0 < GPSLocationTime - NetLocationTime) {
            return locationGPS;
        } else {
            return locationNet;
        }
    }

    /**
     * This method modify the last know good location according to the arguments.
     *
     * @param location The possible new location.
     */
    private void makeUseOfNewLocation(Location location) {
        if (isBetterLocation(location, currentLocation)) {
            currentLocation = location;
        }
    }


    /**
     * Determines whether one location reading is better than the current location fix
     *
     * @param location            The new location that you want to evaluate
     * @param currentBestLocation The current location fix, to which you want to compare the new one.
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        Timber.e("isBetterLocation");
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location,
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse.
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else return isNewer && !isSignificantlyLessAccurate && isFromSameProvider;
    }

    // Checks whether two providers are the same
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private void updateUI() {
        Timber.d("updateUI called .............");
        if (null != currentLocation) {

            LatLng allLatLang = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(allLatLang);
            markerOptions.title("Current Position");
            markerOptions.snippet("Users Basic Information");
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_small));
            currentLocationMarker = googleMap.addMarker(markerOptions);

            //You can add this lines if you want to show the realtime data change on any TextView
            String lat = String.valueOf(currentLocation.getLatitude());
            String lng = String.valueOf(currentLocation.getLongitude());
            Timber.e("Latitude: " + lat + "\n" +
                    "Longitude: " + lng + "\n" +
                    "Accuracy: " + currentLocation.getAccuracy() + "\n" +
                    "Provider: " + currentLocation.getProvider());
        } else {
            Timber.d("location is null ...............");
        }
    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onGPSTrackerLocationChanged(Location location) {

    }

    @Override
    public void onGPSTrackerStatusChanged(String provider, int status, Bundle extras) {
        showMessage("Status Changed");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    @Override
    public void onGooglePlaceSearchStart() {

    }

    @Override
    public void onGooglePlaceSearchSuccess(SensorList sensorList) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        bottomSheetBehavior.setPeekHeight(300);
        Timber.e("onInfoWindowClick -> %s", marker.getTitle());

//        markerAlreadyClicked = 1;
        if (markerAlreadyClicked == 1) {
            if (googleMap != null)
                googleMap.clear();
            fetchSensors();
            if (getActivity() != null) {
                BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                navBar.setVisibility(View.VISIBLE);
            }
//            onLocationChanged(mLastLocation);
//            onLocationChanged(currentLocation);
            animateCamera(currentLocation);
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_small));
            googleMap.addMarker(markerOptions).setFlat(true);
            linearLayoutMarkerBottom.setVisibility(View.GONE);
            linearLayoutBottom.setVisibility(View.GONE);
            linearLayoutSearchBottom.setVisibility(View.GONE);
            btnMarkerGetDirection.setText("Get Direction");
            btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
            getDirectionMarkerButtonClicked = 0;
            if (fromMarkerRouteDrawn == 1 && markerAlreadyClicked == 1) {
                ApplicationUtils.showMessageDialog("You have already selected a parking slot! \nPlease try again!", context);
                fetchSensors();
            } else {
                fromMarkerRouteDrawn = 0;
                fetchSensors();
            }
            markerAlreadyClicked = 0;
            Timber.e("onInfoWindowClick markerAlreadyClicked -> %s", markerAlreadyClicked);
        } else {
            Timber.e("onInfoWindowClick else markerAlreadyClicked -> %s", markerAlreadyClicked);
            if (googleMap != null)
                googleMap.clear();
            fetchSensors();
//            onLocationChanged(mLastLocation);
//            onLocationChanged(currentLocation);
            animateCamera(currentLocation);
            fetchBottomSheetSensors();
            setBottomSheetFragmentControls(bookingSensorsArrayListGlobal);
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_small));
            googleMap.addMarker(markerOptions).setFlat(true);
            String spotstatus = marker.getSnippet();
            String spotid = marker.getTitle();

            //calculate Duration
            markerPlaceLatLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            getDestinationInfoForDuration(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                    markerPlaceLatLng);

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
                    Timber.e("DistanceForCount -> %s", distanceForCount);
                    if (distanceForCount < 0.1) {
                        String parkingNumberOfIndividualMarker = jsonObject.get("no_of_parking").toString();
                        textViewMarkerParkingAreaCount.setText(parkingNumberOfIndividualMarker);
                        Timber.e("parkingNumberOfIndividualMarker -> %s", parkingNumberOfIndividualMarker);
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            getAddress(getActivity(), markerPlaceLatLng.latitude, markerPlaceLatLng.longitude);
            String searchPlaceName = address;
            TaskParser taskParser = new TaskParser();
            double markerDistance = taskParser.showDistance(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                    new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
//            layoutMarkerVisible(true, searchPlaceName, "1", ApplicationUtils.distance(currentLocation.getLatitude(), currentLocation.getLongitude(), marker.getPosition().latitude, marker.getPosition().longitude), marker.getPosition());
            layoutMarkerVisible(true, searchPlaceName, textViewMarkerParkingAreaCount.getText().toString(),
                    String.valueOf(markerDistance), marker.getPosition());
            Timber.e("layoutMarkerVisible textViewMarkerParkingAreaCount -> %s", textViewMarkerParkingAreaCount.getText().toString());
            markerAlreadyClicked++;
            Timber.e("onInfoWindowClick else last markerAlreadyClicked -> %s", markerAlreadyClicked);
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nearest:
                // get the nearest sensor information
                nearest.setText("Reverse Spot");
                nearestRouteCoordinate = new LatLng(nLatitude, nLongitude); //Store these lat lng values somewhere. These should be constant.
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(nearestRouteCoordinate, 18);
                googleMap.animateCamera(location);
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getActivity() != null)
                    getActivity().finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Timber.i("Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                Toast.makeText(getActivity(), "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_SHORT).show();
                String address = place.getAddress();
                // do query with address

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getActivity(), "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Timber.i(status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    /**
     * Draw polyline on map, get distance and duration of the route
     *
     * @param latLngDestination LatLng of the destination
     */
//    private void getDestinationInfo(LatLng latLngDestination) {
////        progressDialog();
//        String serverKey = getResources().getString(R.string.google_maps_key); // Api Key For Google Direction API \\
//        final LatLng origin = new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude);
//        final LatLng destination = latLngDestination;
//        //-------------Using AK Exorcist Google Direction Library---------------\\
//        GoogleDirection.withServerKey(serverKey)
//                .from(origin)
//                .to(destination)
//                .transportMode(TransportMode.DRIVING)
//                .execute(new DirectionCallback() {
//                    @Override
//                    public void onDirectionSuccess(Direction direction, String rawBody) {
////                        dismissDialog();
//                        String status = direction.getStatus();
//                        if (status.equals(RequestResult.OK)) {
//                            Route route = direction.getRouteList().get(0);
//                            Leg leg = route.getLegList().get(0);
//                            Info distanceInfo = leg.getDistance();
//                            Info durationInfo = leg.getDuration();
//                            String distance = distanceInfo.getText();
//                            String duration = durationInfo.getText();
//
//                            //------------Displaying Distance and Time-----------------\\
//                            Timber.e("Distance Duration -> %s -> %s", distance, duration);
////                            showingDistanceTime(distance, duration); // Showing distance and time to the user in the UI \\
////                            String message = "Total Distance is " + distance + " and Estimated Time is " + duration;
////                            StaticMethods.customSnackBar(consumerHomeActivity.parentLayout, message,
////                                    getResources().getColor(R.color.colorPrimary),
////                                    getResources().getColor(R.color.colorWhite), 3000);
//
//                            //--------------Drawing Path-----------------\\
////                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
////                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getActivity(),
////                                    directionPositionList, 5, getResources().getColor(R.color.colorPrimary));
////                            googleMap.addPolyline(polylineOptions);
//                            //--------------------------------------------\\
//
//                            //-----------Zooming the map according to marker bounds-------------\\
////                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
////                            builder.include(origin);
////                            builder.include(destination);
////                            LatLngBounds bounds = builder.build();
////
////                            int width = getResources().getDisplayMetrics().widthPixels;
////                            int height = getResources().getDisplayMetrics().heightPixels;
////                            int padding = (int) (width * 0.20); // offset from edges of the map 10% of screen
////
////                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
////                            googleMap.animateCamera(cu);
//                            //------------------------------------------------------------------\\
//
//                        } else if (status.equals(RequestResult.NOT_FOUND)) {
//                            Toast.makeText(getActivity(), "No routes exist", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onDirectionFailure(Throwable t) {
//                        // Do something here
//                    }
//                });
//        //-------------------------------------------------------------------------------\\
//
//    }

    /**
     * CHECK PERMISSION FOR: ACCESS FINE LOCATION & ACCESS COARSE LOCATION
     * ===================================================================================
     * if there is not permission granted, just enable the permission.
     */
    private boolean checkPermission() {
        int accessFineLocation = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        int accessCoarseLocation = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION);

        return accessFineLocation == PackageManager.PERMISSION_GRANTED && accessCoarseLocation == PackageManager.PERMISSION_GRANTED;
    }

    public void layoutVisible(boolean isVisible, String name, String count,
                              String distance, LatLng location) {
        this.name = name;
        this.count = count;
        this.location = location;
        this.distance = distance;
        this.duration = duration;

        if (isVisible) {
//            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
//            navBar.setVisibility(View.GONE);
            linearLayoutBottom.setVisibility(View.VISIBLE);
//            textViewParkingAreaCount.setText(count);
//            textViewParkingAreaName.setText(name);
//            textViewParkingDistance.setText(new DecimalFormat("##.##").format(distance) + " km");
//            textViewParkingTravelTime.setText(duration);
        } else {
            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
            navBar.setVisibility(View.VISIBLE);
            linearLayoutBottom.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    public void layoutSearchVisible(boolean isVisible, String name, String count,
                                    String distance, LatLng location) {
        this.name = name;
        this.count = count;
        this.searchPlaceLatLng = location;
        this.distance = distance;
        this.duration = duration;
//        bookingSensors = new BookingSensors(name, searchPlaceLatLng.latitude, searchPlaceLatLng.longitude, distance, count);
//        Timber.e("layoutSearchVisible bookingSensors -> %s", new Gson().toJson(bookingSensors));
//        SharedData.getInstance().setBookingSensors(bookingSensors);
        if (isVisible) {
            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
            navBar.setVisibility(View.GONE);
            linearLayoutSearchBottom.setVisibility(View.VISIBLE);
            textViewSearchParkingAreaCount.setText(count);
            textViewSearchParkingAreaName.setText(ApplicationUtils.capitalize(name));
//            textViewSearchParkingDistance.setText(new DecimalFormat("##.##").format(distance) + " km");
            textViewSearchParkingDistance.setText(distance.substring(0, 3) + " km");
            getDestinationInfoForDuration(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                    new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
//            textViewSearchParkingTravelTime.setText(duration);
        } else {
            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
            navBar.setVisibility(View.VISIBLE);
            linearLayoutSearchBottom.setVisibility(View.GONE);
        }
    }

    public void layoutMarkerVisible(boolean isVisible, String name, String count,
                                    String distance, LatLng location) {
        this.name = name;
        this.count = count;
        Timber.e("layoutMarkerVisible count -> %s", count);
        this.distance = distance;
        this.markerPlaceLatLng = location;
        this.duration = duration;

        if (isVisible) {
            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
            navBar.setVisibility(View.GONE);
            if (markerPlaceLatLng != null) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(markerPlaceLatLng);
                markerOptions.title(name);
                markerOptions.draggable(true);
                coordList.add(new LatLng(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                googleMap.addMarker(markerOptions).setFlat(true);
                //move map camera
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPlaceLatLng, 13));
            }
            linearLayoutMarkerBottom.setVisibility(View.VISIBLE);
            textViewMarkerParkingAreaCount.setText(count);
//            textViewMarkerParkingAreaCount.setText(parkingNumberOfIndividualMarker);
            textViewMarkerParkingAreaName.setText(ApplicationUtils.capitalize(name));
//            textViewMarkerParkingDistance.setText(new DecimalFormat("##.##").format(distance) + " km");
            textViewMarkerParkingDistance.setText(distance.substring(0, 4) + " km");
//            textViewMarkerParkingTravelTime.setText(duration);
        } else {
            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
            navBar.setVisibility(View.VISIBLE);
            linearLayoutMarkerBottom.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    public void layoutBottomSheetVisible(boolean isVisible, String name, String count,
                                         String distance, String duration, LatLng location) {
        this.name = name;
        this.count = count;
        Timber.e("layoutBottomSheetVisible count -> %s", count);
        this.distance = distance;
        this.bottomSheetPlaceLatLng = location;
        this.duration = duration;

        if (isVisible) {
            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
            navBar.setVisibility(View.GONE);
            if (bottomSheetPlaceLatLng != null) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(bottomSheetPlaceLatLng);
                markerOptions.title(name);
                markerOptions.draggable(true);
                coordList.add(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                googleMap.addMarker(markerOptions).setFlat(true);
                //move map camera
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bottomSheetPlaceLatLng, 13));
            }
            linearLayoutBottomSheetBottom.setVisibility(View.VISIBLE);
            textViewBottomSheetParkingAreaCount.setText(count);
            textViewBottomSheetParkingAreaName.setText(ApplicationUtils.capitalize(name));
//            textViewBottomSheetParkingDistance.setText(new DecimalFormat("##.##").format(distance) + " km");
            textViewBottomSheetParkingDistance.setText(distance.substring(0, 3) + " km");
//            textViewMarkerParkingTravelTime.setText(duration);
            getDestinationInfoForDuration(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                    new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
        } else {
            BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
            navBar.setVisibility(View.VISIBLE);
            linearLayoutBottomSheetBottom.setVisibility(View.GONE);
        }
    }

    /**
     * REQUEST PERMISSION FOR: ACCESS FINE LOCATION & ACCESS COARSE LOCATION
     * ===================================================================================
     * if there is not permission available, just enable the permission.
     */
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            showMessage("GPS permission allows us to access location data. Please allow in App Settings for additional functionality.");
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE_LOCATION);
        }
    }

    private void initialize() throws IOException {

        View locationButton = ((View) view.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 180, 30, 0);

        PlayerPrefs.Initialize(getContext());

        initMap();
        initComponents();
        fetchSensors();
    }

    private void initMap() {
        if (googleMap == null) {
            supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapView = supportMapFragment.getView();
            supportMapFragment.getMapAsync(this);
        }
    }

    private void initGPS() {
        Timber.e("initGPS");
        gpsTracker = new GPSTracker(getContext());
//        googleMap.setOnMyLocationChangeListener(myLocationChangeListener);
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();

            if (latitude != 0.0 || longitude != 0.0) {
                GlobalVars.location = new MyLocation(latitude, longitude);
                Timber.e("Current Location -> %s -> %s ", GlobalVars.location.latitude, GlobalVars.location.longitude);
            }
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),15));
//            userLocationMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude)).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_car_running)));

        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void initComponents() {
        nearest = view.findViewById(R.id.nearest);
        parkingReqSpot = view.findViewById(R.id.parking_req_spot);
    }

    private void refreshUserGPSLocation() {
        if (userLocationMarker != null) userLocationMarker.remove();
        GlobalVars.IsFakeGPS = false;
        MyLocation userLocation = GlobalVars.getUserLocation();

        if (userLocation != null) {
            LatLng userLatLng = new LatLng(userLocation.latitude, userLocation.longitude);
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setMyLocationEnabled(true);
            googleMap.setBuildingsEnabled(false);
            googleMap.setTrafficEnabled(true);
            googleMap.setIndoorEnabled(false);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 13));

            googleMap.getUiSettings().setAllGesturesEnabled(true);
            // Disable: Disable zooming controls
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            // Disable / Disable my location button
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            // Enable / Disable Compass icon
            googleMap.getUiSettings().setCompassEnabled(true);
            // Enable / Disable Rotate gesture
            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            // Enable / Disable zooming functionality
            googleMap.getUiSettings().setZoomGesturesEnabled(true);

//            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.car_icon);

            //_marker.setIcon(icon);
        }
    }

    private BookingSensors bookingSensors;
    private ArrayList<BookingSensors> bookingSensorsArrayList = new ArrayList<>();

    private void geoLocate() {
        String apiKey = context.getResources().getString(R.string.google_maps_key);
        Timber.e("apiKey -> %s", apiKey);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            if (getActivity() != null) {
                Places.initialize(getActivity(), apiKey);
            }
        }

        final double HEADING_NORTH_EAST = 45;
        final double HEADING_SOUTH_WEST = 215;
        final double diagonalBoundsSize = 1000; // 1km

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getActivity().getApplicationContext());
        placesClient.findAutocompletePredictions(FindAutocompletePredictionsRequest.builder().build());
//        LatLng center = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//        LatLng northSide = SphericalUtil.computeOffset(center, diagonalBoundsSize / 2, HEADING_NORTH_EAST);
//        LatLng southSide = SphericalUtil.computeOffset(center, diagonalBoundsSize / 2, HEADING_SOUTH_WEST);
//        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        // Create a RectangularBounds object.
//        RectangularBounds bounds = RectangularBounds.newInstance(southSide, northSide);
//        RectangularBounds bounds = RectangularBounds.newInstance(new LatLng(23.622641, 90.499794),
//                new LatLng(23.999941, 90.420273));
        // Use the builder to create a FindAutocompletePredictionsRequest.
//        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
        // Call either setLocationBias() OR setLocationRestriction().
//                .setLocationBias(bounds)
//                .setLocationRestriction(bounds)
//                .setOrigin(center)
//                .setCountries("BD")
//                .setTypeFilter(TypeFilter.ADDRESS)
//                .setSessionToken(token)
//                .setQuery("query")
//                .build();

//        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
//            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
//                Timber.e("prediction -> %s", prediction.getPlaceId());
//                Timber.e(TAG, prediction.getPrimaryText(null).toString());
//            }
//        }).addOnFailureListener((exception) -> {
//            if (exception instanceof ApiException) {
//                ApiException apiException = (ApiException) exception;
//                Timber.e(TAG, "Place not found: " + apiException.getStatusCode());
//            }
//        });

//        typeFilter = new AutocompleteFilter.Builder()
//                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
//                .setTypeFilter(3)
//                .build();
//        RectangularBounds bounds = RectangularBounds.newInstance(northSide, southSide);
//                .include(northSide)
//                .include(southSide)
//                .build();

//        autocompleteFragment.setLocationRestriction(bounds);
//        autocompleteFragment.setTypeFilter(typeFilter);

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        if (autocompleteFragment != null) {
            autocompleteFragment.setCountries("BD");
            autocompleteFragment.setHint("Where to?");
            autocompleteFragment.onPictureInPictureModeChanged(true);
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS, Place.Field.PLUS_CODE, Place.Field.TYPES));
//Location Restriction from abdullahpur to kachpur bridge
//            autocompleteFragment.setLocationRestriction(RectangularBounds.newInstance(
//                    new LatLng(23.7036, 90.5176), //kachpur bridge
//                    new LatLng(24.3895, 91.1657))); //abdullahpur
            Timber.d(String.valueOf(Place.Field.ID));
            Timber.d(String.valueOf(Place.Field.NAME));
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {

//                    bottomSheetSearch = 0;
                    Timber.i("Place: => %s", place.getLatLng());
                    String name = place.getName();
                    Timber.e(name);
                    if (googleMap != null)
                        googleMap.clear();
                    bottomSheetBehavior.setPeekHeight(320);
                    searchPlaceLatLng = place.getLatLng();
                    Timber.e(String.valueOf(searchPlaceLatLng));

                    if (searchPlaceLatLng != null) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(searchPlaceLatLng);
                        markerOptions.title(name);
                        markerOptions.draggable(true);
                        coordList.add(new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        googleMap.addMarker(markerOptions).setFlat(true);
                        //move map camera
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 13));
                        fetchSensors();
                        BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                        navBar.setVisibility(View.GONE);
                        linearLayoutSearchBottom.setVisibility(View.VISIBLE);
                        linearLayoutSearchBottomButton.setVisibility(View.VISIBLE);
                        btnSearchGetDirection.setVisibility(View.VISIBLE);
                        imageViewSearchBack.setVisibility(View.VISIBLE);
                        bottomSheetSearch = 0;
                        getAddress(getActivity(), searchPlaceLatLng.latitude, searchPlaceLatLng.longitude);
                        String searchPlaceName = address;
                        Timber.e("searchPlaceName -> %s", searchPlaceName);
                        TaskParser taskParser = new TaskParser();

                        double searchDistance = taskParser.showDistance(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
                        Timber.e("searchDistance -> %s", searchDistance);
                        layoutSearchVisible(true, searchPlaceName, "0", String.valueOf(searchDistance), searchPlaceLatLng);
                        bottomSheetBehavior.setPeekHeight(300);

//                        if (SharedData.getInstance().getBookingSensors() != null) {
//                            bookingSensors = SharedData.getInstance().getBookingSensors();
//                            Timber.e("bookingSensors only Search -> %s", new Gson().toJson(bookingSensors));
//                            bookingSensorsArrayList.add(bookingSensors);
//                        }

                        for (int i = 0; i < searchPlaceEventJsonArray.length(); i++) {
                            JSONObject jsonObject;
                            try {
                                jsonObject = searchPlaceEventJsonArray.getJSONObject(i);
                                String latitude1 = jsonObject.get("latitude").toString();
                                String longitude1 = jsonObject.get("longitude").toString();

                                double distanceForNearbyLoc = calculateDistance(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude,
                                        ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));
                                Timber.e("DistanceForNearbyLoc -> %s", distanceForNearbyLoc);

                                if (distanceForNearbyLoc <= 3) {
                                    bottomSheetSearch = 1;
                                    origin = new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude);
                                    getAddress(getActivity(), ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));
                                    String nearbyAreaName = address;
                                    String parkingNumberOfNearbyDistanceLoc = jsonObject.get("no_of_parking").toString();
                                    Timber.e("nearbyDistance nearByDuration -> %s -> %s", nearByDistance, nearByDuration);
                                    bookingSensors = new BookingSensors(nearbyAreaName, ApplicationUtils.convertToDouble(latitude1),
                                            ApplicationUtils.convertToDouble(longitude1), nearByDistance + " Km", parkingNumberOfNearbyDistanceLoc);
                                    Timber.e("bookingSensors -> %s", new Gson().toJson(bookingSensors));
                                    bookingSensorsArrayList.add(bookingSensors);
                                    bottomSheetBehavior.setPeekHeight(320);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
//                        bookingSensorsArrayList.add(bookingSensors);
                        bookingSensorAdapter.updateData(bookingSensorsArrayList);
                        Timber.e("setBottomSheetRecyclerViewAdapter(bookingSensorsArrayList) call hoiche for loop");
                    }
                }

                @Override
                public void onError(@NonNull Status status) {
                    // TODO: Handle the error.
                    Timber.i("An error occurred: -> %s", status);
//                    Toast.makeText(getActivity(), "Place selection failed: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
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
                Timber.e("getAddress:  address -> %s", address);
                Timber.e("getAddress:  city -> %s", city);
                Timber.e("getAddress:  country -> %s", country);
                Timber.e("getAddress:  test -> %s", test);
                Timber.e("getAddress:  premises -> %s", subAdminArea);
                Timber.e("getAddress:  state -> %s", state);
                Timber.e("getAddress:  postalCode -> %s", postalCode);
                Timber.e("getAddress:  knownName -> %s", knownName);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONArray clickEventJsonArray;
    private JSONArray searchPlaceEventJsonArray;

    //fetch parking marker into map
    private void fetchSensors() {

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_FETCH_SENSORS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Timber.e(" Sensor Response -> %s", response);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("sensors");
                    clickEventJsonArray = object.getJSONArray("sensors");
                    searchPlaceEventJsonArray = object.getJSONArray("sensors");
                    Timber.e(" Sensor JSONArray -> %s", jsonArray);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String latitude1 = jsonObject.get("latitude").toString();
                        String longitude1 = jsonObject.get("longitude").toString();
////
//                        for (int j = 0; j < jsonArray.length(); j++) {
//                            parkingNumberOfIndividualMarker = jsonObject.get("no_of_parking").toString();
//                        }
//                        markerParkingCount(parkingNumberOfIndividualMarker);

                        // find distance
//                        double tDistance = distance(Double.valueOf(latitude1), Double.valueOf(longitude1), currentLocation.getLatitude(), currentLocation.getLongitude());

                        double tDistance = calculateDistance(ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1), currentLocation.getLatitude(), currentLocation.getLongitude());
                        Timber.e("tDistance: -> %s", tDistance);
                        if (tDistance < nDistance) {
                            nDistance = tDistance;
                            nLatitude = Double.parseDouble(latitude1);
                            nLongitude = Double.parseDouble(longitude1);
                        }

                        if (jsonObject.get("s_status").toString().equalsIgnoreCase("1")) {
                            if (jsonObject.get("reserve_status").toString().equalsIgnoreCase("1")) {
                                sensorStatus = "Occupied";
                                double lat = ApplicationUtils.convertToDouble(latitude1);
                                double lon = ApplicationUtils.convertToDouble(longitude1);
                                if (googleMap != null) {
                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Booked And Parked").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                    Timber.e("Booked position -> %s", new LatLng(lat, lon));
                                    googleMap.addMarker(marker);
                                }
                            } else {
                                sensorStatus = "Empty";
                                double lat = ApplicationUtils.convertToDouble(latitude1);
                                double lon = ApplicationUtils.convertToDouble(longitude1);

                                if (googleMap != null) {
                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Occupied.").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                    Timber.e("Occupied position -> %s", new LatLng(lat, lon));
                                    googleMap.addMarker(marker);
                                }
                            }
                        } else {
                            if (jsonObject.get("reserve_status").toString().equalsIgnoreCase("1")) {
                                sensorStatus = "Occupied";
                                double lat = ApplicationUtils.convertToDouble(latitude1);
                                double lon = ApplicationUtils.convertToDouble(longitude1);
                                if (googleMap != null) {
                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Booked but No Vehicle").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                    Timber.e("No Vehicle position -> %s", new LatLng(lat, lon));
                                    googleMap.addMarker(marker);
                                }

                            } else {
                                sensorStatus = "Empty";
                                double lat = ApplicationUtils.convertToDouble(latitude1);
                                double lon = ApplicationUtils.convertToDouble(longitude1);
                                if (googleMap != null) {
                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Empty").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                    googleMap.addMarker(marker);
                                    Timber.e("Empty position -> %s", new LatLng(lat, lon));
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //System.out.println(e.getMessage());
                }
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

    //fetch bottom sheet sensors
    private void fetchBottomSheetSensors() {
        //initialize the progress dialog and show it
        bottomSheetProgressDialog = new ProgressDialog(context);
        bottomSheetProgressDialog.setMessage("Fetching The Parking Sensors....");
        bottomSheetProgressDialog.show();
        ArrayList<BookingSensors> bookingSensorsList = new ArrayList<>();
        this.bookingSensorsArrayListGlobal = bookingSensorsList;

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_SENSORS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                bottomSheetProgressDialog.dismiss();
                Timber.e(ParkingApp.class.getCanonicalName(), "" + response);

                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("sensors");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

//                        JSONArray array = jsonArray.getJSONArray(i);
                        String areaName = jsonObject.get("parking_area").toString();
                        double latitude = ApplicationUtils.convertToDouble(jsonObject.get("latitude").toString());
                        double longitude = ApplicationUtils.convertToDouble(jsonObject.get("longitude").toString());
                        Timber.e("api lat -> %s lat -> %s", latitude, longitude);
                        String count = jsonObject.get("no_of_parking").toString();

                        TaskParser taskParser = new TaskParser();
                        double fetchDistance = taskParser.showDistance(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                new LatLng(latitude, longitude));
                        BookingSensors bookingSensors = new BookingSensors(areaName, latitude, longitude, String.valueOf(fetchDistance), count, "");
                        bookingSensorsList.add(bookingSensors);
                    }

                    setBottomSheetFragmentControls(bookingSensorsList);
                    Timber.e("test bookingSensorsList -> %s", new Gson().toJson(bookingSensorsList));
                    Collections.sort(bookingSensorsArrayListGlobal, BookingSensors.BY_NAME_ASCENDING_ORDER);

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

        strReq.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ParkingApp.getInstance().addToRequestQueue(strReq);
    }

    @SuppressLint("SetTextI18n")
    private void setBottomSheetFragmentControls(ArrayList<BookingSensors> sensors) {
        this.bookingSensorsArrayListGlobal = sensors;
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
        Timber.e("bensorSensorArrayList check -> %s", new Gson().toJson(sensors));

        setBottomSheetRecyclerViewAdapter(sensors);

//        Timber.e("booking sensor recyclerView -> %s", bottomSheetRecyclerView.getAdapter().getItemCount());
    }

    private void setBottomSheetRecyclerViewAdapter(ArrayList<BookingSensors> bookingSensors) {
        this.bookingSensorsArrayListGlobal = bookingSensors;
        Timber.e("setBottomSheetRecyclerViewAdapter bookingSensors -> %s", new Gson().toJson(bookingSensors));
        bookingSensorAdapter = new BookingSensorAdapter(context, this, bookingSensors);
        bottomSheetRecyclerView.setAdapter(bookingSensorAdapter);
//        bookingSensorAdapter.updateData(bookingSensorsArrayListGlobal);
    }

    /**
     * Open Dialog Box Method
     */
    public void openDialog(String sId) {

        String selectedSpt = sId;
        DialogForm dialogForm = new DialogForm();
        Bundle bundle = new Bundle();
        bundle.putString("selectedSpt", selectedSpt);
        dialogForm.setArguments(bundle);
        dialogForm.show(getFragmentManager(), "dialog form");

    }

    private void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private double calculateDistance(Double latitude, Double longitude, double e, double f) {
        double d2r = Math.PI / 180;

        double dlong = (longitude - f) * d2r;
        double dlat = (latitude - e) * d2r;
        double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(e * d2r) * Math.cos(latitude * d2r) * Math.pow(Math.sin(dlong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = 6367 * c;
        return d;
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Key
        String key = "key=AIzaSyDMWfYh5kjSQTALbZb-C0lSNACpcH5RDU4";
//        key=
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

        Timber.e("Directions URL: -> %s", url);
        return url;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetMarkerEvent event) {
//        Toast.makeText(getActivity(), "Geche", Toast.LENGTH_SHORT).show();
        layoutVisible(true, ApplicationUtils.capitalize(name), count, distance, event.location);
        fetchSensors();
        Timber.e("Zoom call hoiche");
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(event.location);
        markerOptions.title(name);
        markerOptions.draggable(true);
        coordList.add(new LatLng(event.location.latitude, event.location.longitude));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        googleMap.addMarker(markerOptions);
        //move map camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(event.location, 13));
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
//                if (googleMap != null)
//                    googleMap.clear();
                // Do something after 2s = 2000ms
//                progressDialog.dismiss();
//                btnGetDirection.setVisibility(View.VISIBLE);
////                linearLayoutNameCount.setVisibility(View.VISIBLE);
//                linearLayoutBottom.setVisibility(View.VISIBLE);
//                imageViewBack.setVisibility(View.VISIBLE);
//                String url = getDirectionsUrl(new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude), event.location);
//                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
//                taskRequestDirections.execute(url);
//                onLocationChanged(mLastLocation);
                onLocationChanged(currentLocation);
//                showMarker(currentLocation);
                location = event.location;
                EventBus.getDefault().post(new SetMarkerEvent(location));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(location);
                markerOptions.title(name);
                markerOptions.draggable(true);
                coordList.add(new LatLng(location.latitude, location.longitude));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                googleMap.addMarker(markerOptions);
                //move map camera
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
                btnGetDirection.setVisibility(View.VISIBLE);
//                linearLayoutNameCount.setVisibility(View.VISIBLE);
                linearLayoutBottom.setVisibility(View.VISIBLE);
                imageViewBack.setVisibility(View.VISIBLE);
//                String url = getDirectionsUrl(new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude), event.location);
                String url = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), event.location);
                Timber.e("url -> %s", url);
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
//                if (googleMap != null)
//                    googleMap.clear();
                // Do something after 2s = 2000ms
//                progressDialog.dismiss();
//                btnGetDirection.setVisibility(View.VISIBLE);
////                linearLayoutNameCount.setVisibility(View.VISIBLE);
//                linearLayoutBottom.setVisibility(View.VISIBLE);
//                imageViewBack.setVisibility(View.VISIBLE);
//                String url = getDirectionsUrl(new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude), event.location);
//                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
//                taskRequestDirections.execute(url);
//                onLocationChanged(mLastLocation);
//                onLocationChanged(currentLocation);
//                showMarker(currentLocation);
                animateCamera(currentLocation);
                bottomSheetPlaceLatLng = event.location;
                EventBus.getDefault().post(new SetMarkerEvent(bottomSheetPlaceLatLng));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(bottomSheetPlaceLatLng);
                markerOptions.title(name);
                markerOptions.draggable(true);
                coordList.add(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                googleMap.addMarker(markerOptions);
                //move map camera
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
                btnBottomSheetGetDirection.setVisibility(View.VISIBLE);
//                linearLayoutNameCount.setVisibility(View.VISIBLE);
                linearLayoutBottomSheetBottom.setVisibility(View.VISIBLE);
                imageViewBottomSheetBack.setVisibility(View.VISIBLE);
//                String url = getDirectionsUrl(new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude), event.location);
                String url = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), event.location);
                Timber.e("url -> %s", url);
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
//                if (googleMap != null)
//                    googleMap.clear();
//                progressDialog.dismiss();
//                onLocationChanged(mLastLocation);
//                onLocationChanged(currentLocation);
                animateCamera(currentLocation);
//                showMarker(currentLocation);
                searchPlaceLatLng = event.location;
                EventBus.getDefault().post(new SetMarkerEvent(searchPlaceLatLng));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(searchPlaceLatLng);
                markerOptions.title(name);
                markerOptions.draggable(true);
                coordList.add(new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                googleMap.addMarker(markerOptions);
                //move map camera
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 13));
                btnSearchGetDirection.setVisibility(View.VISIBLE);
//                linearLayoutNameCount.setVisibility(View.VISIBLE);
//                linearLayoutSearchBottom.setVisibility(View.VISIBLE);
                linearLayoutSearchBottomButton.setVisibility(View.VISIBLE);
                linearLayoutBottom.setVisibility(View.GONE);
                linearLayoutMarkerBottom.setVisibility(View.GONE);
                imageViewSearchBack.setVisibility(View.VISIBLE);
//                String url = getDirectionsUrl(new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude), searchPlaceLatLng);
                String url = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), searchPlaceLatLng);
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);

//                double bearing = bearingBetweenLocations(currentLocationMarker.getPosition(), event.location);
//                double bearing = getBearing(currentLocationMarker.getPosition(), event.location);
//                rotateMarker(currentLocationMarker, (float) bearing);

                //duration refers to the animation time. By default it will take 3000 even if 0 is passed.
//                CarMoveAnim.startcarAnimation(currentLocationMarker, googleMap,
//                        new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
//                        searchPlaceLatLng, 0, callback);
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
//                if (googleMap != null)
//                    googleMap.clear();
//                progressDialog.dismiss();
//                onLocationChanged(mLastLocation);
//                onLocationChanged(currentLocation);
                animateCamera(currentLocation);
//                showMarker(currentLocation);
                markerPlaceLatLng = event.location;
                EventBus.getDefault().post(new SetMarkerEvent(markerPlaceLatLng));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(markerPlaceLatLng);
                markerOptions.title(name);
                markerOptions.draggable(true);
                coordList.add(new LatLng(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                googleMap.addMarker(markerOptions);
                //move map camera
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPlaceLatLng, 13));
                btnMarkerGetDirection.setVisibility(View.VISIBLE);
//                linearLayoutNameCount.setVisibility(View.VISIBLE);
//                linearLayoutMarkerBottom.setVisibility(View.VISIBLE);
//                imageViewMarkerBack.setVisibility(View.VISIBLE);
//                String url = getDirectionsUrl(new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude), markerPlaceLatLng);
//                String url = getDirectionsUrl(new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude), markerPlaceLatLng);
                String url = getDirectionsUrl(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), markerPlaceLatLng);
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);
//car bearing
//                double bearing = bearingBetweenLocations(currentLocationMarker.getPosition(), event.location);
//                double bearing = getBearing(currentLocationMarker.getPosition(), event.location);
//                rotateMarker(currentLocationMarker, (float) bearing);

//             duration refers to the animation time. By default it will take 3000 even if 0 is passed.
//                CarMoveAnim.startcarAnimation(currentLocationMarker, googleMap,
//                        new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
//                        markerPlaceLatLng, 0, callback);

                fromMarkerRouteDrawn = 1;

            }
        }, 1000);
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
            polylineOptions.width(10);
            if (flag == 1) {
//                    if (googleMap != null)
//                        googleMap.clear();
                polylineOptions.color(Color.BLACK);
                polylineOptions.width(10);
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
                googleMap.addPolyline(polylineOptions);
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
            Timber.i("showDistance" + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec);

            return (Radius * c);
        }


    }

    /**
     * Draw polyline on map, get distance and duration of the route
     *
     * @param latLngDestination LatLng of the destination
     */

    private LatLng origin;
    private String nearByDistance;
    private String nearByDuration;
    private String fetchDuration;

    private void getDestinationInfoForDuration(LatLng origin, LatLng latLngDestination) {
        this.origin = origin;
        Timber.e(" -> %s", origin);
        Timber.e("getDestinatigetDestinationInfoForDuration originonInfoForDuration bottomSheetSearch -> %s", bottomSheetSearch);
//        progressDialog();
        String serverKey = context.getResources().getString(R.string.google_maps_key); // Api Key For Google Direction API \\
//        final LatLng origin;
        if (searchPlaceLatLng != null && bottomSheetSearch == 1) {
            Timber.e("1st if condition getDestinationInfoForDuration");
            origin = new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude);
        } else if (searchPlaceLatLng != null && bottomSheetSearch == 0) {
            Timber.e("2nd if condition getDestinationInfoForDuration");
            origin = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        } else {
            Timber.e("else condition getDestinationInfoForDuration");
            origin = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }
//        final LatLng destination = latLngDestination;
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
                            textViewSearchParkingTravelTime.setText(duration);
                            textViewMarkerParkingTravelTime.setText(duration);
//                            textViewBottomSheetParkingTravelTime.setText(duration);
                            nearByDuration = duration;
                            nearByDistance = distance;
                            fetchDuration = duration;
                            Timber.e("fetchDuration -> %s", fetchDuration);
                            Timber.e("inmethod nearByDuration nearByDistance -> %s %s", nearByDuration, nearByDistance);
                            //------------Displaying Distance and Time-----------------\\
//                            showingDistanceTime(distance, duration); // Showing distance and time to the user in the UI \\
                            String message = "Total Distance is " + distance + " and Estimated Time is " + duration;
                            Timber.e("duration message -> %s", message);
//                            StaticMethods.customSnackBar(consumerHomeActivity.parentLayout, message,
//                                    getResources().getColor(R.color.colorPrimary),
//                                    getResources().getColor(R.color.colorWhite), 3000);

                            //--------------Drawing Path-----------------\\
//                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
//                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getActivity(),
//                                    directionPositionList, 5, getResources().getColor(R.color.colorPrimary));
//                            googleMap.addPolyline(polylineOptions);
                            //--------------------------------------------\\

                            //-----------Zooming the map according to marker bounds-------------\\
//                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                            builder.include(origin);
//                            builder.include(destination);
//                            LatLngBounds bounds = builder.build();
//
//                            int width = getResources().getDisplayMetrics().widthPixels;
//                            int height = getResources().getDisplayMetrics().heightPixels;
//                            int padding = (int) (width * 0.20); // offset from edges of the map 10% of screen
//
//                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
//                            googleMap.animateCamera(cu);
                            //------------------------------------------------------------------\\

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

    private float getBearing(LatLng startPosition, LatLng newPos) {
        double lat = Math.abs(startPosition.latitude - newPos.latitude);
        double lng = Math.abs(startPosition.longitude - newPos.longitude);

        if (startPosition.latitude < newPos.latitude && startPosition.longitude < newPos.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if ((startPosition.latitude >= newPos.latitude && startPosition.longitude < newPos.longitude))
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if ((startPosition.latitude >= newPos.latitude && startPosition.longitude >= newPos.longitude))
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if ((startPosition.latitude < newPos.latitude && startPosition.longitude >= newPos.longitude))
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat)) + 270));
        return -1;
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

    //  The following code will give you bearing between two locations:
    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    /**
     * calculates the distance between two locations in MILES
     */
    private double distanceInMiles(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }

    //   Finally, we need to rotate the car-marker by the angle that we get from above method.
    private void rotateMarker(final Marker marker, final float toRotation) {
        if (!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }
}

