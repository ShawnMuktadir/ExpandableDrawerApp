package www.fiberathome.com.parkingapp.ui.home;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.api.Common;
import www.fiberathome.com.parkingapp.model.api.IGoogleApi;
import www.fiberathome.com.parkingapp.model.data.AppConstants;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.response.BaseResponse;
import www.fiberathome.com.parkingapp.model.response.booking.BookingSensors;
import www.fiberathome.com.parkingapp.model.response.search.SearchVisitorData;
import www.fiberathome.com.parkingapp.model.response.search.SelectedPlace;
import www.fiberathome.com.parkingapp.model.response.sensors.Sensor;
import www.fiberathome.com.parkingapp.model.response.sensors.SensorArea;
import www.fiberathome.com.parkingapp.model.response.sensors.SensorsResponse;
import www.fiberathome.com.parkingapp.module.GoogleMapWebServiceNDistance.directionModules.DirectionFinder;
import www.fiberathome.com.parkingapp.module.GoogleMapWebServiceNDistance.directionModules.DirectionFinderListener;
import www.fiberathome.com.parkingapp.module.eventBus.GetDirectionAfterButtonClickEvent;
import www.fiberathome.com.parkingapp.module.eventBus.GetDirectionBottomSheetEvent;
import www.fiberathome.com.parkingapp.module.eventBus.GetDirectionForMarkerEvent;
import www.fiberathome.com.parkingapp.module.eventBus.GetDirectionForSearchEvent;
import www.fiberathome.com.parkingapp.module.eventBus.SetMarkerEvent;
import www.fiberathome.com.parkingapp.module.geoFenceInterface.IOnLoadLocationListener;
import www.fiberathome.com.parkingapp.module.geoFenceInterface.MyLatLng;
import www.fiberathome.com.parkingapp.ui.booking.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.ui.bottomSheet.BottomSheetAdapter;
import www.fiberathome.com.parkingapp.ui.bottomSheet.CustomLinearLayoutManager;
import www.fiberathome.com.parkingapp.ui.location.LocationActivity;
import www.fiberathome.com.parkingapp.ui.schedule.ScheduleFragment;
import www.fiberathome.com.parkingapp.ui.search.SearchActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.GpsUtils;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.RecyclerTouchListener;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.model.response.searchHistory.AppConstants.FIRST_TIME_INSTALLED;
import static www.fiberathome.com.parkingapp.model.response.searchHistory.AppConstants.HISTORY_PLACE_SELECTED;
import static www.fiberathome.com.parkingapp.model.response.searchHistory.AppConstants.NEW_PLACE_SELECTED;
import static www.fiberathome.com.parkingapp.model.response.searchHistory.AppConstants.NEW_SEARCH_ACTIVITY_REQUEST_CODE;
import static www.fiberathome.com.parkingapp.utils.GoogleMapHelper.defaultMapSettings;
import static www.fiberathome.com.parkingapp.utils.GoogleMapHelper.getDefaultPolyLines;

@SuppressLint("NonConstantResourceId")
public class HomeFragment extends BaseFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener,
        IOnLoadLocationListener, GeoQueryEventListener,
        IOnBackPressListener, DirectionFinderListener,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener {

    private final String TAG = getClass().getSimpleName();

    public static final int GPS_REQUEST_CODE = 9003;
    private static final int PLAY_SERVICES_ERROR_CODE = 9002;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    @BindView(R.id.linearLayoutBottom)
    public LinearLayout linearLayoutBottom;

    @BindView(R.id.linearLayoutSearchBottom)
    public LinearLayout linearLayoutSearchBottom;

    @BindView(R.id.linearLayoutSearchBottomButton)
    public LinearLayout linearLayoutSearchBottomButton;

    @BindView(R.id.linearLayoutMarkerBackNGetDirection)
    public LinearLayout linearLayoutMarkerBackNGetDirection;

    @BindView(R.id.textViewBottomSheetParkingDistance)
    public TextView textViewBottomSheetParkingDistance;

    @BindView(R.id.textViewBottomSheetParkingTravelTime)
    public TextView textViewBottomSheetParkingTravelTime;

    @BindView(R.id.linearLayoutBottomSheetBottom)
    public LinearLayout linearLayoutBottomSheetBottom;

    //from parking adapter
    @BindView(R.id.btnGetDirection)
    Button btnGetDirection;

    @BindView(R.id.linearLayoutParkingSlot)
    LinearLayout linearLayoutParkingBackNGetDirection;

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
    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout mShimmerViewContainer;

    @BindView(R.id.textViewNoData)
    TextView textViewNoData;

    @BindView(R.id.btnBottomSheetGetDirection)
    public Button btnBottomSheetGetDirection;

    @BindView(R.id.imageViewBottomSheetBack)
    ImageView imageViewBottomSheetBack;

    @BindView(R.id.textViewBottomSheetParkingAreaCount)
    TextView textViewBottomSheetParkingAreaCount;

    @BindView(R.id.textViewBottomSheetParkingAreaName)
    TextView textViewBottomSheetParkingAreaName;

    @BindView(R.id.linearLayoutBottomSheetNameCount)
    LinearLayout linearLayoutBottomSheetNameCount;

    @BindView(R.id.linearLayoutBottomSheetGetDirection)
    LinearLayout linearLayoutBottomSheetGetDirection;

    @BindView(R.id.view)
    View view;

    @BindView(R.id.input_search)
    Button buttonSearch;

    @BindView(R.id.currentLocationImageButton)
    ImageButton currentLocationImageButton;

    private Unbinder unbinder;

    private HomeActivity context;

    public BottomSheetBehavior bottomSheetBehavior;

    public static Location currentLocation;
    public static LatLng adapterPlaceLatLng;
    public LatLng searchPlaceLatLng;
    public LatLng bottomSheetPlaceLatLng;
    public LatLng markerPlaceLatLng;
    public String address, city, state, country, subAdminArea, test, knownName, postalCode = "";
    public GoogleMap mMap;

    //used in fetchSensor()
    public double nDistance = 132116456;
    public double nLatitude;
    public double nLongitude;
    public int isRouteDrawn = 0;
    private LinearLayout bottomSheet;
    private FragmentChangeListener listener;
    private long arrived, departure;
    private TextView arrivedTimeTV, departureTimeTV, timeDifferenceTV, countDownTV, textViewTermsCondition;
    private long difference;
    private Button moreBtn, btnLiveParking;
    private LinearLayout bookedLayout;
    private RecyclerView bottomSheetRecyclerView;
    private BottomSheetAdapter bottomSheetAdapter;

    private Marker currentLocationMarker;
    public final ArrayList<LatLng> coordList = new ArrayList<LatLng>();
    private String nearByDuration;
    private String fetchDuration;
    private SupportMapFragment supportMapFragment;

    private String name, count = "";
    private String distance;
    private String duration;
    private boolean isGPS;
    private static GoogleApiClient mGoogleApiClient;
    private String sensorStatus = "Occupied";

    //flags
    private int getDirectionButtonClicked = 0;
    private int getDirectionSearchButtonClicked = 0;
    private int getDirectionMarkerButtonClicked = 0;
    private int getDirectionBottomSheetButtonClicked = 0;

    public int fromMarkerRouteDrawn = 0;

    //route flag
    private final int flag = 0;

    //polyline animate
    private List<LatLng> polyLineList;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, grayPolyline;
    private IGoogleApi mService;
    private String searchPlaceCount = "0";

    //geoFence
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentUser;
    private GeoFire geoFire;
    private List<LatLng> dangerousArea = new ArrayList<>();
    private Location mLastLocation;
    private GeoQuery geoQuery;
    private boolean isInAreaEnabled = false;

    private String parkingNumberOfIndividualMarker = "";
    private BookingSensors bookingSensorsMarker;
    private final ArrayList<BookingSensors> bookingSensorsMarkerArrayList = new ArrayList<>();

    private Marker previousMarker = null;

    private Location onConnectedLocation;

    private final ArrayList<BookingSensors> bookingSensorsArrayList = new ArrayList<>();
    private final ArrayList<BookingSensors> bookingSensorsBottomSheetArrayList = new ArrayList<>();
    private double searchDistance;
    private String markerUid;
    private final ArrayList<BookingSensors> bookingSensorsArrayListGlobal = new ArrayList<>();

    private double adjustValue = 2;

    private LatLng origin;

    private int countadd = 0;

    private double adapterDistance;

    private final ArrayList<BookingSensors> bookingSensorsAdapterArrayList = new ArrayList<>();

    private String adapterUid;
    private String bottomUid;
    private Marker markerClicked;

    private boolean isNotificationSent = false;

    public Polyline polyline;

    public List<LatLng> points = new ArrayList<>();

    private Double lat, lng;
    private String areaName, parkingSlotCount;

    public Marker previousGetDestinationMarker;
    private Location myPreviousLocation;
    private LocationManager mLocationManager;
    private List<LatLng> initialRoutePoints;
    private boolean firstDraw = true;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public static HomeFragment newInstance(double lat, double lng, String areaName, String count) {
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble("lat", lat);
        bundle.putDouble("lng", lng);
        bundle.putString("areaName", areaName);
        bundle.putString("count", count);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.e("onCreate called");

        setHasOptionsMenu(false);

        super.onCreate(savedInstanceState);

        if (context != null) {
            FirebaseApp.initializeApp(context);
        }

        if (getArguments() != null) {
            lat = getArguments().getDouble("lat", 0.0);
            lng = getArguments().getDouble("lng", 0.0);
            areaName = getArguments().getString("areaName", null);
            parkingSlotCount = getArguments().getString("count", null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.e("onCreateView called");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Timber.e("onViewCreated called");
        super.onViewCreated(view, savedInstanceState);

        context = (HomeActivity) getActivity();

        if (context != null) {
            context.changeDefaultActionBarDrawerToogleIcon();
            listener = context;
            mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        }

        unbinder = ButterKnife.bind(this, view);

        if (isAdded()) {

            initUI(view);

            setListeners();
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._92sdp));
            bottomSheetBehavior.setHideable(false);
            bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View view, int i) {
                    switch (i) {
                        case BottomSheetBehavior.STATE_HIDDEN:
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:
                            toolbarAnimVisibility(view, false);
                            //fadeOutAnimation(toolbar);
                            break;
                        case BottomSheetBehavior.STATE_COLLAPSED:
                            bottomSheetRecyclerView.smoothScrollToPosition(0);
                            toolbarAnimVisibility(view, true);
                            //fadeInAnimation(toolbar);
                            break;
                        case BottomSheetBehavior.STATE_DRAGGING:
                            toolbarAnimVisibility(view, true);
                            //fadeInAnimation(toolbar);
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:
                            break;
                        case BottomSheetBehavior.STATE_HALF_EXPANDED:
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View view, float slideOffset) {
                    if (isAdded()) {
                        //bottomSheetAdapter.isItemClicked = false;
                    }
                }
            });

            buildLocationRequest();

            buildLocationCallBack();

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

            if (getArguments() != null) {
                if (getArguments().getBoolean("s")) {
                    bookedLayout.setVisibility(View.VISIBLE);
                    arrived = getArguments().getLong("arrived", 0);
                    departure = getArguments().getLong("departure", 0);
                    difference = departure - arrived;

                    setTimer(difference);

                    Timber.d("onCreateView: " + arrived + "    " + departure);
                    Timber.d("onCreateView: difference: -> %s", difference);
                }
            }

            if (mMap == null) {
                showLoading(context, context.getResources().getString(R.string.please_wait));
            }

            if (isServicesOk()) {
                supportMapFragment = SupportMapFragment.newInstance();

                if (context != null) {
                    FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction().
                            replace(R.id.map, supportMapFragment);
                    ft.commit();
                    supportMapFragment.getMapAsync(this);
                } else {
                    Toast.makeText(context, "Unable to load map", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(context, "Play services are required by this application", Toast.LENGTH_SHORT).show();
            }

            polyLineList = new ArrayList<>();

            mService = Common.getGoogleApi();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Timber.e("onMapReady called");

        mMap = googleMap;

        hideLoading();

        defaultMapSettings(context, mMap, fusedLocationProviderClient, mLocationRequest, mLocationCallback);

        buildGoogleApiClient();

        //only view BD map

        /*LatLng v1 = new LatLng(26.633914, 92.6801153); //northeast
        LatLng v2 = new LatLng(20.3794, 88.00861410000002); //southwest

        LatLngBounds latLngBounds = new LatLngBounds(
                v2, v1
        );

        //mMap.setLatLngBoundsForCameraTarget(latLngBounds);

        //int padding = 15; // offset from edges of the map in pixels
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(BANGLADESH, padding);
        //mMap.moveCamera(cameraUpdate);

        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen

        new Handler().postDelayed(() -> {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, width, height, padding));
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, width, height, padding));
        },500);*/

        mMap.setOnMarkerClickListener(this);

        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);
    }

    private void toolbarAnimVisibility(View view, boolean show) {
        Transition transition = new Fade();
        transition.setDuration(600);
        transition.addTarget(R.id.image);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TransitionManager.beginDelayedTransition((ViewGroup) view, transition);
        toolbar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    void fadeOutAnimation(View viewToFadeOut) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(viewToFadeOut, "alpha", 1f, 0f);

        fadeOut.setDuration(500);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // We wanna set the view to GONE, after it's fade out. so it actually disappear from the layout & don't take up space.
                super.onAnimationEnd(animation);
                viewToFadeOut.setVisibility(View.GONE);
            }
        });

        fadeOut.start();
    }

    void fadeInAnimation(View viewToFadeIn) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(viewToFadeIn, "alpha", 0f, 1f);
        fadeIn.setDuration(500);

        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                viewToFadeIn.setVisibility(View.VISIBLE);
                viewToFadeIn.setAlpha(0);
            }
        });

        fadeIn.start();
    }

    public static MarkerOptions markerOptionsPin;

    public static MarkerOptions newMarkerPinInstance() {
        if (markerOptionsPin == null) {
            markerOptionsPin = new MarkerOptions();
        }
        return markerOptionsPin;
    }

    boolean isMyCurrentLocation = false;

    private Circle circle;

    private Sensor markerTagObj;

    private int fromSearchMultipleRouteDrawn = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onMarkerClick(Marker marker) {

        final String[] uid = {""};

        String markerAreaName = "";

        final String[] uid1 = {""};

        hideNoData();

        final String[] markerAreaName1 = {""};

        if (marker.getTag() != null)
            markerTagObj = (Sensor) marker.getTag();

        if (markerTagObj != null) {
            Timber.e("marker if UID: -> %s", markerTagObj.getAddress());
        } else {
            Timber.e("marker else UID: did not work");
        }

        if (currentLocationMarker != null && calculateDistance(currentLocationMarker.getPosition().latitude, currentLocationMarker.getPosition().longitude,
                marker.getPosition().latitude, marker.getPosition().longitude) <= 0.001) {
            double distance = calculateDistance(currentLocationMarker.getPosition().latitude, currentLocationMarker.getPosition().longitude,
                    marker.getPosition().latitude, marker.getPosition().longitude);

            marker.setTitle("My Location");

            isMyCurrentLocation = true;
        } else {
            isMyCurrentLocation = false;
        }

        if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {

            bookingSensorsArrayList.clear();
            bookingSensorsMarkerArrayList.clear();
            bookingSensorsAdapterArrayList.clear();

            if (isRouteDrawn == 0) {
                if (mMap != null) {

                    if (marker.getTitle() != null) {
                        if (!marker.getTitle().equals("My Location")) {
                            if (previousMarker != null) {
                                previousMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                if (previousSecondMarkerDestinationMarker != null) {
                                    previousSecondMarkerDestinationMarker.remove();
                                    previousSecondMarkerDestinationMarker = null;
                                }
                                /*else {
                                    previousSecondMarkerDestinationMarker = mMap.addMarker(markerOptions);
                                }*/
                                //Toast.makeText(context, "previous", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(context, "previous null", Toast.LENGTH_SHORT).show();
                            }
                            previousMarker = marker;
                            removeCircle();
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_gray));
                            //getDirectionPinMarkerDraw(marker.getPosition(), markerUid);

                            coordList.add(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));

                            markerClicked = marker;
                            isNotificationSent = false;
                            isInAreaEnabled = false;
                        }
                    }

                    if (parkingNumberOfIndividualMarker != null) {
                        if (parkingNumberOfIndividualMarker.equals("0") && getDirectionMarkerButtonClicked == 1) {
                            btnMarkerGetDirection.setText(context.getResources().getString(R.string.get_direction));
                            btnMarkerGetDirection.setEnabled(true);
                            btnMarkerGetDirection.setFocusable(true);
                            btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                            getDirectionMarkerButtonClicked = 0;
                        }
                    }

                    //calculate Duration
                    markerPlaceLatLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                    coordList.add(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));

                    getDestinationInfoForDuration(markerPlaceLatLng);

                    if (marker.getTitle() != null) {
                        if (!marker.getTitle().equals("My Location")) {
                            if (geoQuery != null) {
                                geoQuery.removeGeoQueryEventListener(this);
                                geoQuery.removeAllListeners();
                            }

                            geoQuery = geoFire.queryAtLocation(new GeoLocation(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude), 2f); // 500m
                            geoQuery.addGeoQueryEventListener(this);
                        }
                    }

                    if (!isMyCurrentLocation) {

                        for (int i = 0; i < sensorArrayList.size(); i++) {
                            JSONObject jsonObject;
                            Sensor sensor = sensorArrayList.get(i);
                            try {
                                // jsonObject = clickEventJsonArray.getJSONObject(i);
                                String latitude1 = sensor.getLatitude();
                                String longitude1 = sensor.getLongitude();
                                uid[0] = sensor.getUid();
                                markerAreaName = sensor.getParkingArea();
                                //Timber.e("jsonUid -> %s", uid[0]);
                                //TaskParser taskParser = new TaskParser();
                                double distanceForCount = calculateDistance(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude,
                                        ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));

                                if (distanceForCount < 0.001) {
                                    parkingNumberOfIndividualMarker = sensor.getNoOfParking();
                                    textViewMarkerParkingAreaCount.setText(parkingNumberOfIndividualMarker);
                                    break;
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        String finalUid = uid[0];
                        Timber.e("jsonUid finalUid -> %s", finalUid);

                        String markerPlaceName = markerAreaName;

                        double markerDistance = 0;

                        markerDistance = calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                                marker.getPosition().latitude, marker.getPosition().longitude);

                        layoutMarkerVisible(true, markerAreaName, parkingNumberOfIndividualMarker,
                                String.valueOf(markerDistance), marker.getPosition());

                        if (markerPlaceLatLng != null) {

                            markerUid = marker.getTitle();

                            Timber.e("markerUid -> %s", markerUid);

                            if (markerDistance < 3000) {
                                adjustValue = 1;
                            }

                            double kim = (markerDistance / 1000) + adjustValue;
                            double markerDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(markerDistance * 2.43));
                            String markerStringDuration = String.valueOf(markerDoubleDuration);

                            bookingSensorsMarker = new BookingSensors(markerPlaceName, marker.getPosition().latitude, marker.getPosition().longitude,
                                    ApplicationUtils.round(markerDistance, 1), parkingNumberOfIndividualMarker, markerStringDuration,
                                    context.getResources().getString(R.string.nearest_parking_from_your_destination),
                                    BookingSensors.TEXT_INFO_TYPE, 0);

                            if (marker.getTitle() != null && bookingSensorsMarker.getCount() != null) {
                                if (bookingSensorsMarker.getCount().equals("") || marker.getTitle().equals("My Location")) {
                                    parkingNumberOfIndividualMarker = "0";
                                }
                            }

                            bookingSensorsMarkerArrayList.add(new BookingSensors(markerPlaceName, marker.getPosition().latitude, marker.getPosition().longitude,
                                    markerDistance, parkingNumberOfIndividualMarker, markerStringDuration,
                                    context.getResources().getString(R.string.nearest_parking_from_your_destination),
                                    BookingSensors.TEXT_INFO_TYPE, 0));

                            if (sensorArrayList != null) {

                                setBottomSheetList(() -> {
                                    if (bottomSheetAdapter != null) {
                                        bookingSensorsArrayListGlobal.clear();
                                        bookingSensorsArrayListGlobal.addAll(bookingSensorsMarkerArrayList);
                                        bottomSheetAdapter.notifyDataSetChanged();
                                    } else {
                                        Timber.e("marker click else -> %s", markerUid);
                                    }
                                }, sensorArrayList, marker.getPosition(), bookingSensorsMarkerArrayList, finalUid);
                            }

                        } else {
                            Toast.makeText(context, "Something went wrong!!! Please check your Internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else {
                ApplicationUtils.showAlertDialog(context.getString(R.string.you_have_to_exit_from_current_destination), context, context.getString(R.string.yes), context.getString(R.string.no), (dialog, which) -> {

                    if (marker.getTitle() != null) {
                        if (!marker.getTitle().equals("My Location")) {
                            if (previousMarker != null) {
                                previousMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                //Toast.makeText(context, "previous", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(context, "previous null", Toast.LENGTH_SHORT).show();
                            }
                            previousMarker = marker;
                            removeCircle();
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_gray));
                            markerClicked = marker;
                            isNotificationSent = false;
                            isInAreaEnabled = false;
                        }
                    }

                    if (polyline == null || !polyline.isVisible())
                        return;

                    points = polyline.getPoints();

                    if (polyline != null) {
                        polyline.remove();
                    }

                    getDirectionPinMarkerDraw(marker.getPosition(), markerUid);
                    coordList.add(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));

                    if (searchPlaceLatLng != null) {
                        btnSearchGetDirection.setText(context.getResources().getString(R.string.confirm_booking));
                        btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                        btnSearchGetDirection.setEnabled(true);
                        btnSearchGetDirection.setFocusable(true);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        fromSearchMultipleRouteDrawn = 1;
                    }

                    isDestinationMarkerDrawn = true;

                    String origin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();

                    String destination = null;

                    if (marker.getPosition() != null) {
                        destination = "" + marker.getPosition().latitude + ", " + marker.getPosition().longitude;
                    }

                    fetchDirections(origin, destination);

                    fromMarkerRouteDrawn = 1;

                    for (int i = 0; i < sensorArrayList.size(); i++) {
                        JSONObject jsonObject;
                        Sensor sensor = sensorArrayList.get(i);
                        try {
                            //jsonObject = clickEventJsonArray.getJSONObject(i);
                            String latitude1 = sensor.getLatitude();
                            String longitude1 = sensor.getLongitude();
                            uid1[0] = sensor.getUid();
                            markerAreaName1[0] = sensor.getParkingArea();
                            double distanceForCount = calculateDistance(marker.getPosition().latitude, marker.getPosition().longitude,
                                    ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));

                            if (distanceForCount < 0.001) {
                                parkingNumberOfIndividualMarker = sensor.getNoOfParking();
                                textViewMarkerParkingAreaCount.setText(parkingNumberOfIndividualMarker);
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    String finalUid = uid1[0];

                    String markerPlaceName = markerAreaName1[0];

                    //TaskParser taskParser = new TaskParser();

                    double markerDistance = 0;

                    markerDistance = calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                            marker.getPosition().latitude, marker.getPosition().longitude);

                    double kim = (markerDistance / 1000) + adjustValue;
                    double markerDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(markerDistance * 2.43));
                    String markerStringDuration = String.valueOf(markerDoubleDuration);

                    bookingSensorsMarkerArrayList.add(new BookingSensors(markerTagObj.getParkingArea(), marker.getPosition().latitude, marker.getPosition().longitude,
                            markerDistance, markerTagObj.getNoOfParking(), markerStringDuration,
                            context.getResources().getString(R.string.nearest_parking_from_your_destination),
                            BookingSensors.TEXT_INFO_TYPE, 0));

                    if (sensorArrayList != null) {

                        setBottomSheetList(() -> {
                            if (bottomSheetAdapter != null) {
                                bookingSensorsArrayListGlobal.clear();
                                bookingSensorsArrayListGlobal.addAll(bookingSensorsMarkerArrayList);
                                bottomSheetAdapter.notifyDataSetChanged();
                            } else {
                                Timber.e("bottomSheetAdapter null");
                            }
                        }, sensorArrayList, marker.getPosition(), bookingSensorsMarkerArrayList, finalUid);
                    }

                    bottomSheetAdapter.setDataList(bookingSensorsMarkerArrayList);

                }, (dialog, which) -> {
                    Timber.e("Negative Button Clicked");
                    dialog.dismiss();
                });
            }
        } else {
            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
        }

        return true;
    }

    private Marker pinMarker;

    private void getDirectionPinMarkerDraw(LatLng pinPosition, String markerUid) {
        if (pinMarker != null) {
            pinMarker.remove();
        }

        if (mMap != null) {
            pinMarker = mMap.addMarker(newMarkerPinInstance().position(pinPosition)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin))
                    .title(markerUid));
        }
    }

    private double latitude, longitude;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        /*if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }*/

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        buildLocationCallBack();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            onConnectedLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        if (onConnectedLocation != null) {
            Timber.e("onConnected not null called");

            SharedData.getInstance().setOnConnectedLocation(onConnectedLocation);

            if (mMap != null) {

                LatLng latLng = new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.5f));

                animateCamera(onConnectedLocation);

                bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._92sdp));

                if (ApplicationUtils.checkInternet(context) && isGPSEnabled()) {
                    fetchSensorRetrofit(onConnectedLocation);
                } else {
                    ApplicationUtils.showToastMessage(context, context.getResources().getString(R.string.connect_to_internet_gps));
                }

                new Handler().postDelayed(() -> {
                    if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
                        if (lat != null && lng != null && areaName != null && parkingSlotCount != null) {

                            if (!context.isFinishing())
                                showLoading(context);

                            //hideNoData();

                            getDirectionPinMarkerDraw(new LatLng(lat, lng), adapterUid);

                            coordList.add(new LatLng(lat, lng));

                            //move map camera
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 13.5f), 500, null);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 13.5f));

                            String uid = "";

                            String adapterAreaName = "";

                            if (sensorArrayList != null && !sensorArrayList.isEmpty()) {
                                for (int i = 0; i < sensorArrayList.size(); i++) {
                                    Timber.e("sensorArrayList size -> %s", sensorArrayList.size());
                                    Sensor sensor = sensorArrayList.get(i);
                                    try {
                                        String latitude1 = sensor.getLatitude();

                                        String longitude1 = sensor.getLongitude();

                                        uid = sensor.getUid();

                                        adapterAreaName = sensor.getParkingArea();

                                        double distanceForCount = calculateDistance(lat, lng,
                                                ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));

                                        if (distanceForCount < 0.001) {
                                            adapterUid = uid;

                                            Timber.e("adapterUid -> %s", adapterUid);

                                            break;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                Timber.e("sensorArrayList size -> %s", sensorArrayList != null ? sensorArrayList.size() : 0);
                                //Toast.makeText(context, "sensorArrayList empty", Toast.LENGTH_SHORT).show();
                            }

                            bookingSensorsAdapterArrayList.clear();

                            bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._92sdp));

                            adapterDistance = calculateDistance(onConnectedLocation.getLatitude(),
                                    onConnectedLocation.getLongitude(),
                                    lat, lng);

                            String finalUid = uid;

                            String adapterPlaceName = adapterAreaName;

                            double adapterDoubleDuration = ApplicationUtils.
                                    convertToDouble(new DecimalFormat("##.#",
                                            new DecimalFormatSymbols(Locale.US)).format(adapterDistance * 2.43));
                            String adapterStringDuration = String.valueOf(adapterDoubleDuration);

                            layoutVisible(true, ApplicationUtils.capitalizeFirstLetter(areaName), parkingSlotCount,
                                    String.valueOf(adapterDistance), new LatLng(lat, lng));

                            bookingSensorsAdapterArrayList.add(new BookingSensors(ApplicationUtils.capitalizeFirstLetter(areaName), lat, lng,
                                    adjustDistance(adapterDistance), parkingSlotCount, adapterStringDuration,
                                    context.getResources().getString(R.string.nearest_parking_from_your_destination),
                                    BookingSensors.TEXT_INFO_TYPE, 0));

                            if (sensorArrayList != null) {
                                setBottomSheetList(() -> {
                                    if (bottomSheetAdapter != null) {
                                        hideLoading();
                                        bookingSensorsArrayListGlobal.clear();
                                        bookingSensorsArrayListGlobal.addAll(bookingSensorsAdapterArrayList);
                                        bottomSheetAdapter.notifyDataSetChanged();
                                    } else {
                                        Timber.e("sensorArrayList null");
                                    }
                                }, sensorArrayList, new LatLng(lat, lng), bookingSensorsAdapterArrayList, finalUid);
                            }

                            //value getting from parking adapter
                            if (SharedData.getInstance().getSensorArea() != null) {
                                SensorArea sensorArea = SharedData.getInstance().getSensorArea();

                                textViewParkingAreaName.setText(ApplicationUtils.capitalizeFirstLetter(sensorArea.getParkingArea()));
                                textViewParkingAreaCount.setText(sensorArea.getCount());
                                String distance = new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(sensorArea.getDistance()) + " km";

                                textViewParkingDistance.setText(context.getResources().getString(R.string.distance, distance));

                                getDestinationInfoForDuration(new LatLng(sensorArea.getEndLat(), sensorArea.getEndLng()));
                            } else {
                                Timber.e("Genjam");
                            }
                        }
                    } else {
                        TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
                    }
                }, 1500);

                settingGeoFire();
            }
        }
        else {
            Timber.e("onConnected null called");

            previousMarker = null;

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            buildLocationRequest();

            buildLocationCallBack();

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

            onConnectedLocation = new Location(LocationManager.NETWORK_PROVIDER);

            initArea();

            settingGeoFire();

            if (SharedData.getInstance().getLastLocation() != null) {
                Timber.e("getLastLocation not null called");
                onConnectedLocation.setLatitude(SharedData.getInstance().getLastLocation().getLatitude());
                onConnectedLocation.setLongitude(SharedData.getInstance().getLastLocation().getLongitude());
                Timber.e("getLastLocation not null called, onConnectedLocation -> %s", onConnectedLocation);
            } else {
                Timber.e("getLastLocation null called");
                onConnectedLocation.setLatitude(23.774525);
                onConnectedLocation.setLongitude(90.415730);
                Timber.e("getLastLocation null called, onConnectedLocation -> %s", onConnectedLocation);
            }

            SharedData.getInstance().setOnConnectedLocation(onConnectedLocation);

            //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            /*if (onConnectedLocation == null) {
                startLocationUpdates(); // bind interface if your are not getting the lastlocation. or bind as per your requirement.
            }

            if (onConnectedLocation != null) {
                while (latitude == 0 || longitude == 0) {
                    showLoading(context, "Getting Location");

                    latitude = onConnectedLocation.getLatitude();
                    longitude = onConnectedLocation.getLongitude();

                    if (latitude != 0 && longitude != 0) {
                        stopLocationUpdates(); // unbind the locationlistner here or wherever you want as per your requirement.
                        hideLoading();// location data received, dismiss dialog, call your method or perform your task.
                        onConnectedLocation.setLatitude(latitude);
                        onConnectedLocation.setLongitude(longitude);
                    }
                }
            }*/

            Timber.e("onConnectedLocation null else-> %s", onConnectedLocation);
            LatLng latLng = new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)));
            //showCurrentLocationButton(true);

            bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._92sdp));

            try {
                linearLayoutParkingAdapterBackBottom.setVisibility(View.INVISIBLE);
            } catch (Exception e) {
                e.getCause();
            }

            if (ApplicationUtils.checkInternet(context)) {
                fetchSensorRetrofit(onConnectedLocation);
            } else {
                ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                    Timber.e("Positive Button clicked");
                    if (ApplicationUtils.checkInternet(context)) {
                        fetchSensorRetrofit(onConnectedLocation);
                    } else {
                        TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
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
    }

    private double adjustDistance(double distance) {

        if (distance > 1.9) {
            distance = distance + 2;
        } else if (distance < 1.9 && distance > 1) {
            distance = distance + 1;
        } else {
            distance = distance + 0.5;
        }

        return distance;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    int temp;
    double myLocationChangedDistance;
    double oldTotalDistanceInKm, totalDistanceInKm;

    @Override
    public void onLocationChanged(Location location) {
        //Timber.e("onLocationChanged: ");
        currentLocation = location;

        /*if (mGoogleApiClient != null)
            if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            } else if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }*/

        if (location != null) {

          if(onConnectedLocation!=null){
              myLocationChangedDistance = ApplicationUtils.calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(), location.getLatitude(), location.getLongitude());
          }

            onConnectedLocation = location;

            //Timber.e("onLocationChanged: onConnectedLocation -> %s", onConnectedLocation);
            SharedData.getInstance().setOnConnectedLocation(location);

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            if (currentLocationMarker != null) {
                currentLocationMarker.remove();
            }

            currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_car_running))
                    .title("My Location")
                    .rotation(location.getBearing())
                    .flat(false)
                    .anchor(0.5f, 0.5f));

            if (markerClicked != null) {
                checkParkingSpotDistance(latLng, markerClicked.getPosition());
            } else if (adapterPlaceLatLng != null) {
                checkParkingSpotDistance(latLng, adapterPlaceLatLng);
            } else if (bottomSheetPlaceLatLng != null) {
                checkParkingSpotDistance(latLng, bottomSheetPlaceLatLng);
            } else if (searchPlaceLatLng != null) {
                checkParkingSpotDistance(latLng, searchPlaceLatLng);
            }
        }

        String origin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();

       if(isRouteDrawn == 1) {
           if (polyline == null && !points.isEmpty()) {
               polyline = mMap.addPolyline(getDefaultPolyLines(points));
               Timber.e("polyline null -> %s", polyline);
           }
           else if(polyline != null) {

               Timber.e("polyline not null-> %s", polyline);

               boolean isUserOnRoute = PolyUtil.isLocationOnPath(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                       polyline.getPoints(), false, 60.0f);


               if (!isUserOnRoute) {

                   if (myLocationChangedDistance >= 0.001) {
                       //polylineOptions.addAll(polyline.getPoints());

                       String[] latlong = oldDestination.split(",");
                       double lat = Double.parseDouble(latlong[0].trim());
                       double lon = Double.parseDouble(latlong[1].trim());

                       totalDistanceInKm = ApplicationUtils.
                               calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(), lat, lon);

                       if (totalDistanceInKm < oldTotalDistanceInKm) {
                           reDrawRoute(origin);
                       } else {
                           if (myPreviousLocation != null) {
                               double distanceTraveledLast = ApplicationUtils.
                                       calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                                               myPreviousLocation.getLatitude(), myPreviousLocation.getLongitude()) * 1000;

                               if (distanceTraveledLast > 500) {
                                   myPreviousLocation = onConnectedLocation;
                                   reDrawRoute(origin);
                               } else {
                                   if (points != null) {
                                       points.clear();
                                   }else{
                                       points = new ArrayList<>();
                                   }

                                   points.addAll(polyline.getPoints());
                                   points.add(0,new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()));
                                   polyline.remove();
                                   polyline = mMap.addPolyline(getDefaultPolyLines(points));
                               }
                           } else {
                               if (points != null) {
                                   points.clear();
                               }else{
                                   points = new ArrayList<>();
                               }
                               points.addAll(polyline.getPoints());
                               points.add(0,new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()));
                               polyline.remove();
                               polyline = mMap.addPolyline(getDefaultPolyLines(points));
                               myPreviousLocation = onConnectedLocation;
                           }
                       }
                       oldTotalDistanceInKm = totalDistanceInKm;

                       double totalDistanceInMeters = totalDistanceInKm * 1000;

                    /*if (totalDistanceInMeters < 500) {
                        reDrawRoute(origin);
                    } else if (totalDistanceInMeters < 1500) {
                        reDrawRoute(origin);
                    } else if (totalDistanceInMeters < 3000) {
                        reDrawRoute(origin);
                    } else if (totalDistanceInMeters < 6000) {
                        reDrawRoute(origin);
                    } else if (totalDistanceInMeters < 10000) {
                        reDrawRoute(origin);
                    } else if (totalDistanceInMeters < 15000) {
                        reDrawRoute(origin);
                    } else if (totalDistanceInMeters < 25000) {
                        reDrawRoute(origin);
                    }*/
                   }
               }
               else {

                   if (myPreviousLocation != null) {
                       if(onConnectedLocation.getLatitude()!=myPreviousLocation.getLatitude()&&onConnectedLocation.getLongitude()!=myPreviousLocation.getLongitude()) {
//                           List<LatLng> pointsNew = ApplicationUtils.getUpdatedPolyline(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
//                                   polyline.getPoints(), false, false, 60.0f);
                           List<LatLng> pointsNew;
                           if(!initialRoutePoints.isEmpty()){
                               pointsNew = new ArrayList<>(initialRoutePoints);
                           }else {
                               pointsNew = polyline.getPoints();
                           }

                           int point = PolyUtil.locationIndexOnPath(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),pointsNew,false,30.0);
                           if(point>=0){

                               for(int i =point; i>=0;--i) {
                                   pointsNew.remove(i);
                               }
                               pointsNew.add(0,new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()));
                              if(pointsNew.size()>2){
                                  double distance = ApplicationUtils.calculateDistance(pointsNew.get(0).latitude,pointsNew.get(0).longitude,pointsNew.get(1).latitude,pointsNew.get(1).longitude)*1000;
                                if(distance<10){
                                    pointsNew.remove(1);
                                }
                              }
                               polyline.remove();
                               polyline = mMap.addPolyline(getDefaultPolyLines(pointsNew));
                           }

                       }
                       double distanceTravledLast = ApplicationUtils.
                               calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(), myPreviousLocation.getLatitude(), myPreviousLocation.getLongitude()) * 1000;
                       if (distanceTravledLast > 1) {
//                           List<LatLng> pointsNew =polyline.getPoints();
//                           pointsNew.remove(0);
//                           polyline.remove();
//                           polyline = mMap.addPolyline(getDefaultPolyLines(pointsNew));
                           myPreviousLocation = onConnectedLocation;
                       //    reDrawRoute(origin);
                       }
                   } else {
                       myPreviousLocation = onConnectedLocation;
                   }
//                   int ixLastPoint = 0;
//                   for (int i = 0; i < polyline.getPoints().size(); i++) {
//                       LatLng point1 = polyline.getPoints().get(i);
//                       List<LatLng> currentSegment = new ArrayList<>();
//                       currentSegment.add(point1);
//                       if (PolyUtil.isLocationOnPath(new LatLng(myPreviousLocation.getLatitude(), myPreviousLocation.getLongitude()), currentSegment, true, 50)) {
//                           // save index of last point and exit loop
//                           ixLastPoint = i;
//                           break;
//                       }
//                   }
//                   List<LatLng> pathPoints = polyline.getPoints();
//                   for (int i = 0; i < ixLastPoint; i++) {
//                       pathPoints.remove(0);
//                   }
//                   polyline.remove();
//                   polyline = mMap.addPolyline(getDefaultPolyLines(pathPoints));
               }
           }
       }
    }

    private void reDrawRoute(String origin) {
        Timber.e("reDrawRoute called");

        String[] latlong = origin.split(",");
        double lat = Double.parseDouble(latlong[0]);
        double lon = Double.parseDouble(latlong[1]);

        if (origin.isEmpty() || oldDestination.isEmpty()) {
            Toast.makeText(context, "Please first fill all the fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!origin.contains(",") || !oldDestination.contains(",")) {
            Toast.makeText(context, "Invalid data fill in fields!", Toast.LENGTH_SHORT).show();
            return;
        }


        try {
            if (polyline == null || !polyline.isVisible())
                return;


            if (polyline != null)
                polyline.remove();

            ApplicationUtils.showToastMessage(context, "Route re-drawn");
            new DirectionFinder(this, origin, oldDestination).execute();
            hideLoading();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            hideLoading();
        }
    }

    private void setCircleOnLocation(LatLng latLng) {
        circle = mMap.addCircle(
                new CircleOptions()
                        .center(latLng)
                        .radius(70)
                        .strokeWidth(0f)
                        .fillColor(context.getResources().getColor(R.color.transparent))
        );
        isNotificationSent = false;
    }

    private void checkParkingSpotDistance(LatLng car, LatLng spot) {
        double distanceBetween = calculateDistance(car.latitude, car.longitude, spot.latitude, spot.longitude);

        float[] distance = new float[2];

        if (circle != null) {
            Location.distanceBetween(car.latitude, car.longitude, circle.getCenter().latitude, circle.getCenter().longitude, distance);

            if (distance[0] <= circle.getRadius() && !isNotificationSent) {
                // Inside The Circle
                isInAreaEnabled = true;
                isNotificationSent = true;
                sendNotification("You Entered parking spot", "You can book parking slot");
                //Toast.makeText(context, "inside circle", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
    }

    @Override
    public void onStart() {
        Timber.e("onStart called");
        super.onStart();

        /*if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }*/

        EventBus.getDefault().register(this);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.getFusedLocationProviderClient(context).getLastLocation().addOnSuccessListener(location -> {
            //TODO: UI updates.
            onConnectedLocation = location;
        });
    }

    @Override
    public void onResume() {
        Timber.e("onResume called");
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        } else {
            if (isGPSEnabled()) {
                return;
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.please_enable_gps));
            }
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        Timber.e("onPause called");
        super.onPause();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        hideLoading();
    }

    @Override
    public void onStop() {
        Timber.e("onStop called");
        EventBus.getDefault().unregister(this);
        //stopLocationUpdates();
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Timber.e("onDestroy called");
        super.onDestroy();
        hideLoading();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppConstants.GPS_REQUEST && resultCode == RESULT_OK) {
            isGPS = true; // flag maintain before get location
        }

        if (requestCode == NEW_SEARCH_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            bookingSensorsArrayList.clear();

            //new search result
            SelectedPlace selectedPlace = (SelectedPlace) data.getSerializableExtra(NEW_PLACE_SELECTED); //This line may produce null point exception

            if (selectedPlace != null) {
                previousMarker = null;

                hideNoData();

                searchPlaceLatLng = new LatLng(selectedPlace.getLatitude(), selectedPlace.getLongitude());

                String areaName = selectedPlace.getAreaName();
                String areaAddress = selectedPlace.getAreaAddress();
                String placeId = selectedPlace.getPlaceId();

                //store visited place
                storeVisitedPlace(Preferences.getInstance(context).getUser().getMobileNo(), placeId,
                        selectedPlace.getLatitude(), selectedPlace.getLongitude(),
                        onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(), areaName);

                buttonSearch.setVisibility(View.GONE);
                linearLayoutSearchBottom.setVisibility(View.VISIBLE);
                linearLayoutSearchBottomButton.setVisibility(View.VISIBLE);
                btnSearchGetDirection.setVisibility(View.VISIBLE);
                btnSearchGetDirection.setEnabled(true);
                imageViewSearchBack.setVisibility(View.VISIBLE);

                coordList.add(new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
                getDirectionPinMarkerDraw(searchPlaceLatLng, "");

                if (mMap != null) {
                    //move map camera
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 13.5f), 500, null);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 13.5f));
                }

                if (onConnectedLocation != null && searchPlaceLatLng != null) {
                    //TaskParser taskParser = new TaskParser();
                    searchDistance = calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                            searchPlaceLatLng.latitude, searchPlaceLatLng.longitude);

                    Timber.e("searchDistance -> %s", searchDistance);

                    layoutSearchVisible(true, areaName, "0",
                            textViewSearchParkingDistance.getText().toString(), searchPlaceLatLng);

                    bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._142sdp));

                    double kim = (searchDistance / 1000) + adjustValue;

                    double searchDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.#",
                            new DecimalFormatSymbols(Locale.US)).format(searchDistance * 2.43));

                    String searchStringDuration = String.valueOf(searchDoubleDuration);

                    bookingSensorsArrayList.add(new BookingSensors(areaName, searchPlaceLatLng.latitude, searchPlaceLatLng.longitude,
                            searchDistance, "0", searchStringDuration,
                            context.getResources().getString(R.string.nearest_parking_from_your_destination),
                            BookingSensors.TEXT_INFO_TYPE, 0));

                    if (sensorArrayList != null) {
                        for (int i = 0; i < (sensorArrayList != null ? sensorArrayList.size() : 0); i++) {

                            Sensor sensor = sensorArrayList.get(i);
                            String latitude1 = sensor.getLatitude();
                            String longitude1 = sensor.getLongitude();
                            String nearestSearchAreaName = sensor.getParkingArea();

                            double distanceForNearbyLoc = calculateDistance(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude,
                                    ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));

                            if (distanceForNearbyLoc < 5) {
                                origin = new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude);

                                String parkingNumberOfNearbyDistanceLoc = sensor.getNoOfParking();

                                double nearbySearchDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(distanceForNearbyLoc * 2.43));

                                String nearbySearchStringDuration = String.valueOf(nearbySearchDoubleDuration);

                                bookingSensorsArrayList.add(new BookingSensors(nearestSearchAreaName, ApplicationUtils.convertToDouble(latitude1),
                                        ApplicationUtils.convertToDouble(longitude1), adjustDistance(distanceForNearbyLoc), parkingNumberOfNearbyDistanceLoc,
                                        nearbySearchStringDuration,
                                        BookingSensors.INFO_TYPE, 1));

                                bubbleSortArrayList(bookingSensorsArrayList);

                                if (sensorArrayList != null) {
                                    if (bottomSheetAdapter != null) {
                                        bookingSensorsArrayListGlobal.clear();
                                        bookingSensorsArrayListGlobal.addAll(bookingSensorsArrayList);
                                        bottomSheetAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                if (sensorArrayList != null) {
                                    if (bottomSheetAdapter != null) {
                                        bookingSensorsArrayListGlobal.clear();
                                        bookingSensorsArrayListGlobal.addAll(bookingSensorsArrayList);
                                        bottomSheetAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Something went wrong!!! Please check your Internet connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Location cannot be identified!!!", Toast.LENGTH_SHORT).show();
                }
            }

            //search history result
            SearchVisitorData searchVisitorData = (SearchVisitorData) data.getSerializableExtra(HISTORY_PLACE_SELECTED);

            if (searchVisitorData != null) {
                previousMarker = null;

                hideNoData();

                searchPlaceLatLng = new LatLng(searchVisitorData.getEndLat(), searchVisitorData.getEndLng());

                String areaName = searchVisitorData.getVisitedArea();
                String placeId = searchVisitorData.getPlaceId();

                buttonSearch.setVisibility(View.GONE);
                linearLayoutSearchBottom.setVisibility(View.VISIBLE);
                linearLayoutSearchBottomButton.setVisibility(View.VISIBLE);
                btnSearchGetDirection.setVisibility(View.VISIBLE);
                btnSearchGetDirection.setEnabled(true);
                imageViewSearchBack.setVisibility(View.VISIBLE);

                coordList.add(new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
                getDirectionPinMarkerDraw(searchPlaceLatLng, "");

                if (mMap != null) {
                    //move map camera
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 13.5f), 500, null);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 13.5f));
                }

                String searchPlaceName = areaName;

                if (onConnectedLocation != null && searchPlaceLatLng != null) {
                    //TaskParser taskParser = new TaskParser();
                    searchDistance = calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                            searchPlaceLatLng.latitude, searchPlaceLatLng.longitude);

                    Timber.e("searchDistance -> %s", searchDistance);

                    layoutSearchVisible(true, searchPlaceName, "0",
                            textViewSearchParkingDistance.getText().toString(), searchPlaceLatLng);

                    bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._142sdp));

                    double kim = (searchDistance / 1000) + adjustValue;

                    double searchDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.#",
                            new DecimalFormatSymbols(Locale.US)).format(searchDistance * 2.43));

                    String searchStringDuration = String.valueOf(searchDoubleDuration);

                    bookingSensorsArrayList.add(new BookingSensors(searchPlaceName, searchPlaceLatLng.latitude, searchPlaceLatLng.longitude,
                            searchDistance, "0", searchStringDuration,
                            context.getResources().getString(R.string.nearest_parking_from_your_destination),
                            BookingSensors.TEXT_INFO_TYPE, 0));

                    if (sensorArrayList != null) {
                        for (int i = 0; i < (sensorArrayList != null ? sensorArrayList.size() : 0); i++) {

                            Sensor sensor = sensorArrayList.get(i);
                            String latitude1 = sensor.getLatitude();
                            String longitude1 = sensor.getLongitude();
                            String nearestSearchAreaName = sensor.getParkingArea();

                            double distanceForNearbyLoc = calculateDistance(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude,
                                    ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));

                            if (distanceForNearbyLoc < 5) {
                                origin = new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude);

                                String nearbyAreaName = nearestSearchAreaName;
                                String parkingNumberOfNearbyDistanceLoc = null;
                                parkingNumberOfNearbyDistanceLoc = sensor.getNoOfParking();

                                double nearbySearchDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(distanceForNearbyLoc * 2.43));

                                String nearbySearchStringDuration = String.valueOf(nearbySearchDoubleDuration);

                                bookingSensorsArrayList.add(new BookingSensors(nearbyAreaName, ApplicationUtils.convertToDouble(latitude1),
                                        ApplicationUtils.convertToDouble(longitude1), adjustDistance(distanceForNearbyLoc), parkingNumberOfNearbyDistanceLoc,
                                        nearbySearchStringDuration,
                                        BookingSensors.INFO_TYPE, 1));

                                bubbleSortArrayList(bookingSensorsArrayList);

                            }
                            if (sensorArrayList != null) {
                                if (bottomSheetAdapter != null) {
                                    bookingSensorsArrayListGlobal.clear();
                                    bookingSensorsArrayListGlobal.addAll(bookingSensorsArrayList);
                                    bottomSheetAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Something went wrong!!! Please check your Internet connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Location cannot be identified!!!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (SharedData.getInstance().getOnConnectedLocation() != null) {
                Timber.e("SharedData.getInstance().getOnConnectedLocation() not null");
            }
        }

        if (requestCode == GPS_REQUEST_CODE) {

            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            showLoading(context, context.getResources().getString(R.string.enabling_gps));

            new Handler().postDelayed(() -> {
                hideLoading();

                if (providerEnabled) {
                    Toast.makeText(context, "GPS is enabled", Toast.LENGTH_SHORT).show();
                    Timber.e("providerEnabled HomeFragment check called");

                    supportMapFragment = SupportMapFragment.newInstance();

                    if (context != null) {
                        FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction().
                                replace(R.id.map, supportMapFragment);
                        ft.commit();
                        supportMapFragment.getMapAsync(HomeFragment.this);
                        ApplicationUtils.reLoadFragment(getParentFragmentManager(), HomeFragment.this);
                    } else {
                        Toast.makeText(context, "Enable your Gps Location", Toast.LENGTH_SHORT).show();
                    }

                    //progressDialog = ApplicationUtils.progressDialog(context, "Initializing....");
                    showLoading(context);

                    new GpsUtils(context).turnGPSOn(new GpsUtils.onGpsListener() {
                        @Override
                        public void gpsStatus(boolean isGPSEnable) {
                            // turn on GPS
                            isGPS = isGPSEnable;
                        }
                    });

                    if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                        requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                        Timber.d("onViewCreated: in if");
                    } else {
                        //  supportMapFragment.getMapAsync(this);
                        Timber.d("onViewCreated: in else");
                    }

                    arrivedTimeTV.setText("Arrived " + getDate(arrived));
                    departureTimeTV.setText("Departure " + getDate(departure));
                    timeDifferenceTV.setText(getTimeDifference(difference) + " min");
                    //dekhte hobee eta koi boshbe
                    if (getDirectionButtonClicked == 0) {
                        linearLayoutParkingAdapterBackBottom.setOnClickListener(v -> {
                            ApplicationUtils.showOnlyMessageDialog(context.getResources().getString(R.string.when_user_can_book), context);
                        });
                    } else {
                        linearLayoutParkingAdapterBackBottom.setOnClickListener(v -> {
                            //ApplicationUtils.showMessageDialog("Hey Shawn!!!", getContext());
                            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                        });
                    }

                } else {
                    Toast.makeText(context, "GPS not enabled. Unable to show user location", Toast.LENGTH_SHORT).show();
                }

            }, 6000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Timber.d("onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            Timber.d("onRequestPermissionsResult: First time evoked");

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Timber.d("onRequestPermissionResult: on requestPermission if-if");
                /*if (progressDialog != null) {
                    progressDialog.show();
                }*/
                if (isLocationEnabled(context)) {
                    supportMapFragment.getMapAsync(this);
                    /*if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }*/
                }
                //Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else if (grantResults.length == FIRST_TIME_INSTALLED && context != null) {
                if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    Timber.d("onViewCreated: in if");
                }
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        //Timber.e("getCameraPositionWithBearing called");
        return new CameraPosition.Builder().target(latLng).zoom(13.8f).build();
    }

    @Override
    public void onCameraMoveStarted(int reason) {

        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            /*Toast.makeText(context, "The user gestured on the map.",
                    Toast.LENGTH_SHORT).show();*/
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_API_ANIMATION) {
            /*Toast.makeText(context, "The user tapped something on the map.",
                    Toast.LENGTH_SHORT).show();*/
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_DEVELOPER_ANIMATION) {
            /*Toast.makeText(context, "The app moved the camera.",
                    Toast.LENGTH_SHORT).show();*/
        }
    }

    @Override
    public void onCameraMove() {
        /*Toast.makeText(context, "The camera is moving.",
                Toast.LENGTH_SHORT).show();*/
    }

    @Override
    public void onCameraMoveCanceled() {
        /*Toast.makeText(context, "Camera movement canceled.",
                Toast.LENGTH_SHORT).show();*/
    }

    @Override
    public void onCameraIdle() {
        /*Toast.makeText(context, "The camera has stopped moving.",
                Toast.LENGTH_SHORT).show();*/
    }

    private static Boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            return lm.isLocationEnabled();

        } else {
            // This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }

    private final View.OnClickListener clickListener = view -> {
        if (view.getId() == R.id.currentLocationImageButton && mMap != null && onConnectedLocation != null)
            animateCamera(onConnectedLocation);
    };

    private final boolean isCameraChange = false;

    private void showCurrentLocationButton(boolean isVisible) {
        if (isVisible) {
            currentLocationImageButton.setVisibility(View.VISIBLE);
        } else {
            currentLocationImageButton.setVisibility(View.GONE);
        }
    }

    private void initUI(View view) {
        view.findViewById(R.id.currentLocationImageButton).setOnClickListener(clickListener);
        bottomSheet = view.findViewById(R.id.layout_bottom_sheet);
        bottomSheetRecyclerView = view.findViewById(R.id.bottomsheet_recyclerview);
        //for booking
        arrivedTimeTV = view.findViewById(R.id.arrivedtimeTV);
        departureTimeTV = view.findViewById(R.id.departureTimeTV);
        timeDifferenceTV = view.findViewById(R.id.timeDifferenceTV);
        countDownTV = view.findViewById(R.id.countDownTV);
        moreBtn = view.findViewById(R.id.moreBtn);
        btnLiveParking = view.findViewById(R.id.btnLiveParking);
        textViewTermsCondition = view.findViewById(R.id.textViewTermsCondition);
        bookedLayout = view.findViewById(R.id.bookedLayout);
        Button departureBtn = view.findViewById(R.id.departureBtn);
    }

    public void animateCamera(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)));
            //currentLocationImageButton.setVisibility(View.GONE);
        }
    }

    public void removeCircle() {
        if (circle != null) {
            circle.remove();
        }
    }

    private synchronized void setBottomSheetList(SetBottomSheetCallBack setBottomSheetCallBack, List<Sensor> sensorArrayList, LatLng latLng, ArrayList<BookingSensors> bookingSensorsArrayList, String markerUid) {
        final int[] count = {0};
        int count2 = sensorArrayList.size();
        for (int i = 0; i < sensorArrayList.size(); i++) {

            Sensor sensor = sensorArrayList.get(i);
            try {
                String latitude1 = sensor.getLatitude();

                String longitude1 = sensor.getLongitude();

                String uid = sensor.getUid();

                String parkingArea = sensor.getParkingArea();

                double distanceForNearbyLoc = calculateDistance(latLng.latitude, latLng.longitude,
                        ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));

                final String[] nearbyAreaName = {""};

                if (distanceForNearbyLoc < 5 && !markerUid.equals(uid)) {
                    origin = new LatLng(latLng.latitude, latLng.longitude);

                    nearbyAreaName[0] = parkingArea;

                    String parkingNumberOfNearbyDistanceLoc = null;
                    try {
                        parkingNumberOfNearbyDistanceLoc = sensor.getNoOfParking();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    count[0]++;

                    double nearbySearchDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(distanceForNearbyLoc * 2.43));

                    String nearbySearchStringDuration = String.valueOf(nearbySearchDoubleDuration);

                    bookingSensorsArrayList.add(new BookingSensors(nearbyAreaName[0], ApplicationUtils.convertToDouble(latitude1),
                            ApplicationUtils.convertToDouble(longitude1), adjustDistance(distanceForNearbyLoc), parkingNumberOfNearbyDistanceLoc,
                            nearbySearchStringDuration,
                            BookingSensors.INFO_TYPE, 1));

                    bubbleSortArrayList(bookingSensorsArrayList);

                    setBottomSheetCallBack.setBottomSheet();
                    bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._142sdp));
                    if (markerPlaceLatLng != null) {
                        linearLayoutMarkerBackNGetDirection.setVisibility(View.VISIBLE);
                    } else if (adapterPlaceLatLng != null) {
                        linearLayoutBottom.setVisibility(View.VISIBLE);
                        linearLayoutParkingAdapterBackBottom.setVisibility(View.VISIBLE);
                    } else if (searchPlaceLatLng != null) {
                        linearLayoutSearchBottomButton.setVisibility(View.VISIBLE);
                    } else if (bottomSheetPlaceLatLng != null) {
                        linearLayoutBottomSheetGetDirection.setVisibility(View.VISIBLE);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    private final ArrayList<MarkerOptions> mMarkerArrayList = new ArrayList<>();

    private List<Sensor> sensorArrayList = new ArrayList<>();

    public void fetchSensorRetrofit(Location location) {

        this.onConnectedLocation = location;

        bookingSensorsArrayListGlobal.clear();

        // UI Service.
        ApiService service = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<SensorsResponse> sensorsCall = service.getSensors();

        if (!context.isFinishing())
            showLoading(context);

        startShimmer();

        // Gathering results.
        sensorsCall.enqueue(new Callback<SensorsResponse>() {
            @Override
            public void onResponse(@NonNull Call<SensorsResponse> call, @NonNull retrofit2.Response<SensorsResponse> response) {
                Timber.e("response -> %s", response.message());

                hideLoading();

                if (response.body() != null && !response.body().getError()) {
                    if (response.isSuccessful()) {

                        stopShimmer();

                        sensorArrayList = response.body().getSensors();
                        Sensor rajSensor = new Sensor();
                        Sensor rajSensor2 = new Sensor();
                        rajSensor.setAddress("Rajshahi");
                        rajSensor.setAreaId("raj6100");
                        rajSensor.setAreaNo("1232");
                        rajSensor.setLatitude("24.36674279444273");
                        rajSensor.setLongitude("88.60069189220667");
                        rajSensor.setId("879");
                        rajSensor.setNoOfParking("63");
                        rajSensor.setParkingArea("Shaheb Bazar, Rajshahi");
                        rajSensor.setUid("raj7687");
                        rajSensor.setsStatus("1");
                        rajSensor.setReserveStatus(1);

                        rajSensor2.setAddress("Rajshahi");
                        rajSensor2.setAreaId("raj61001");
                        rajSensor2.setAreaNo("1231");
                        rajSensor2.setLatitude("24.374818820697296");
                        rajSensor2.setLongitude("88.59884049743414");
                        rajSensor2.setId("879");
                        rajSensor2.setNoOfParking("30");
                        rajSensor2.setParkingArea("Getter road, Rajshahi");
                        rajSensor2.setUid("raj7688");
                        rajSensor2.setsStatus("1");
                        rajSensor2.setReserveStatus(1);
                        sensorArrayList.add(rajSensor);
                        sensorArrayList.add(rajSensor2);

                        for (int i = 0; i < sensorArrayList.size(); i++) {

                            Sensor sensor = sensorArrayList.get(i);

                            String areaName = sensor.getParkingArea();

                            String parkingCount = sensor.getNoOfParking();

                            double latitude = ApplicationUtils.convertToDouble(sensor.getLatitude());

                            double longitude = ApplicationUtils.convertToDouble(sensor.getLongitude());

                            double tDistance = calculateDistance(latitude, longitude, location.getLatitude(), location.getLongitude());

                            if (tDistance < nDistance) {
                                nDistance = tDistance;
                                nLatitude = latitude;
                                nLongitude = longitude;
                            }

                            if (sensor.getsStatus().equalsIgnoreCase("1")) {
                                if (sensor.getReserveStatus().toString().equalsIgnoreCase("1")) {
                                    sensorStatus = "Occupied";
                                    double lat = latitude;
                                    double lon = longitude;
                                    if (mMap != null) {
                                        MarkerOptions marker = new MarkerOptions()
                                                .position(new LatLng(lat, lon))
                                                .title(sensor.getUid())
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));

                                        Marker marker1 = mMap.addMarker(marker);
                                        marker1.setTag(sensor);
                                        mMarkerArrayList.add(marker);
                                    }
                                } else {
                                    sensorStatus = "Empty";
                                    double lat = latitude;
                                    double lon = longitude;

                                    if (mMap != null) {
                                        MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(sensor.getUid()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                        Marker marker1 = mMap.addMarker(marker);
                                        marker1.setTag(sensor);
                                        mMarkerArrayList.add(marker);
                                    }
                                }
                            } else {
                                if (sensor.getReserveStatus().toString().equalsIgnoreCase("1")) {
                                    sensorStatus = "Occupied";
                                    double lat = latitude;
                                    double lon = longitude;
                                    if (mMap != null) {
                                        MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(sensor.getUid()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                        Marker marker1 = mMap.addMarker(marker);
                                        marker1.setTag(sensor);
                                        mMarkerArrayList.add(marker);
                                    }

                                } else {
                                    sensorStatus = "Empty";
                                    double lat = latitude;
                                    double lon = longitude;
                                    if (mMap != null) {
                                        MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(sensor.getUid()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                        Marker marker1 = mMap.addMarker(marker);
                                        marker1.setTag(sensor);
                                        mMarkerArrayList.add(marker);
                                    }
                                }
                            }

                            //TaskParser taskParser = new TaskParser();
                            double fetchDistance = calculateDistance(location.getLatitude(), location.getLongitude(),
                                    latitude, longitude);

                            double doubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(fetchDistance * 2.43));
                            Timber.e("kim doubleDuration -> %s", doubleDuration);

                            String initialNearestDuration = String.valueOf(doubleDuration);
                            Timber.e("kim initialNearestDuration -> %s", initialNearestDuration);

                            if (fetchDistance < 7) {
                                origin = new LatLng(location.getLatitude(), location.getLongitude());

                                String nearestCurrentAreaName = areaName;

                                Timber.e("nearestCurrentAreaName -> %s", nearestCurrentAreaName);

                                bookingSensorsArrayListGlobal.add(new BookingSensors(nearestCurrentAreaName, latitude, longitude,
                                        adjustDistance(fetchDistance), parkingCount, initialNearestDuration,
                                        BookingSensors.INFO_TYPE, 1));

                                //fetch distance in ascending order
                                Collections.sort(bookingSensorsArrayListGlobal, (c1, c2) -> Double.compare(c1.getDistance(), c2.getDistance()));
                            }
                        }
                        setBottomSheetFragmentControls(bookingSensorsArrayListGlobal);
                    } else {
                        Timber.e("Errors: ");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SensorsResponse> call, @NonNull Throwable errors) {
                Timber.e("Throwable Errors: -> %s", errors.toString());
                hideLoading();
                ApplicationUtils.showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setBottomSheetFragmentControls(ArrayList<BookingSensors> sensors) {
        bottomSheetRecyclerView.setHasFixedSize(true);
        bottomSheetRecyclerView.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager mLayoutManager = new CustomLinearLayoutManager(getActivity());
        bottomSheetRecyclerView.setLayoutManager(mLayoutManager);
        bottomSheetRecyclerView.addItemDecoration(new DividerItemDecoration(context, CustomLinearLayoutManager.VERTICAL));
        bottomSheetRecyclerView.setItemAnimator(new DefaultItemAnimator());
        bottomSheetRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), bottomSheetRecyclerView,
                new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Timber.e("bottomSheetRecyclerView smoothScrollToPosition called");
                        bottomSheetRecyclerView.smoothScrollToPosition(0);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
        ViewCompat.setNestedScrollingEnabled(bottomSheetRecyclerView, false);

        setBottomSheetRecyclerViewAdapter(sensors);
    }

    String uid = "";

    String markerAreaName = "";

    final String[] uid1 = {""};

    final String[] markerAreaName1 = {""};

    String locationName = "";

    private void setBottomSheetRecyclerViewAdapter(ArrayList<BookingSensors> bookingSensors) {
        bottomSheetAdapter = null;

        bottomSheetAdapter = new BottomSheetAdapter(context, this, onConnectedLocation, (BookingSensors sensors) -> {

            bookingSensorsBottomSheetArrayList.clear();

            bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._142sdp));

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            bottomSheetPlaceLatLng = new LatLng(sensors.getLat(), sensors.getLng());
            try {
                if (isRouteDrawn == 0) {
                    //for getting the location name

                    if (bottomSheetAdapter != null) {

                        getDirectionPinMarkerDraw(bottomSheetPlaceLatLng, bottomUid);
                        coordList.add(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));

                        //move map camera
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude), 13.5f), 500, null);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude), 13.5f));

                        for (int i = 0; i < sensorArrayList.size(); i++) {
                            Sensor sensor = sensorArrayList.get(i);
                            try {
                                String latitude1 = sensor.getLatitude();
                                String longitude1 = sensor.getLongitude();
                                uid = sensor.getUid();

                                locationName = sensor.getParkingArea();

                                double distanceForCount = calculateDistance(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude,
                                        ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));

                                if (distanceForCount < 0.001) {
                                    bottomUid = uid;
                                    Timber.e("bottomUid -> %s", bottomUid);
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    String finalUid = uid;

                    String bottomSheetPlaceName = locationName;

                    double bottomSheetDistance = calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                            bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude);

                    double bottomSheetDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(bottomSheetDistance * 2.43));

                    String bottomSheetStringDuration = String.valueOf(bottomSheetDoubleDuration);

                    layoutBottomSheetVisible(true, sensors.getParkingArea(), sensors.getCount(),
                            String.valueOf(adjustDistance(bottomSheetDistance)), bottomSheetStringDuration,
                            new LatLng(sensors.getLat(), sensors.getLng()), false);

                    bookingSensorsBottomSheetArrayList.add(new BookingSensors(bottomSheetPlaceName, bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude,
                            adjustDistance(bottomSheetDistance), textViewBottomSheetParkingAreaCount.getText().toString(), bottomSheetStringDuration,
                            context.getResources().getString(R.string.nearest_parking_from_your_destination),
                            BookingSensors.TEXT_INFO_TYPE, 0));

                    if (sensorArrayList != null) {
                        setBottomSheetList(() -> {
                            if (bottomSheetAdapter != null) {
                                Timber.e("setBottomSheet if called");
                                bookingSensorsArrayListGlobal.clear();
                                bookingSensorsArrayListGlobal.addAll(bookingSensorsBottomSheetArrayList);
                                bottomSheetAdapter.notifyDataSetChanged();
                                bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._92sdp));
                            } else {
                                Timber.e("setBottomSheet if else called");
                            }
                        }, sensorArrayList, bottomSheetPlaceLatLng, bookingSensorsBottomSheetArrayList, finalUid);
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong_please_check_internet_connection),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ApplicationUtils.showAlertDialog(context.getString(R.string.you_have_to_exit_from_current_destination), context, context.getString(R.string.yes), context.getString(R.string.no), (dialog, which) -> {

                        if (polyline == null || !polyline.isVisible())
                            return;

                        points = polyline.getPoints();

                        if (polyline != null) {
                            polyline.remove();
                        }

                        if (bottomSheetPlaceLatLng != null) {
                            getDirectionPinMarkerDraw(bottomSheetPlaceLatLng, bottomUid);
                            coordList.add(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));

                            //move map camera
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude), 13.5f), 500, null);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude), 13.5f));
                        }

                        if (searchPlaceLatLng != null) {
                            btnSearchGetDirection.setText(context.getResources().getString(R.string.confirm_booking));
                            btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                            btnSearchGetDirection.setEnabled(true);
                            btnSearchGetDirection.setFocusable(true);
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            fromSearchMultipleRouteDrawn = 1;
                        }

                        btnBottomSheetGetDirection.setVisibility(View.VISIBLE);

                        String origin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();

                        String destination = null;

                        if (bottomSheetPlaceLatLng != null) {
                            destination = "" + bottomSheetPlaceLatLng.latitude + ", " + bottomSheetPlaceLatLng.longitude;
                        }

                        fetchDirections(origin, destination);

                        bookingSensorsBottomSheetArrayList.clear();

                        String bottomSheetPlaceName = null;
                        for (int i = 0; i < sensorArrayList.size(); i++) {

                            Sensor sensor = sensorArrayList.get(i);
                            try {
                                String latitude1 = sensor.getLatitude();
                                String longitude1 = sensor.getLongitude();
                                uid1[0] = sensor.getUid();
                                bottomSheetPlaceName = sensor.getParkingArea();
                                double distanceForCount = calculateDistance(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude,
                                        ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));

                                if (distanceForCount < 0.001) {
                                    bottomUid = uid1[0];
                                    Timber.e("bottomUid -> %s", bottomUid);

                                    parkingNumberOfIndividualMarker = sensor.getNoOfParking();
                                    textViewMarkerParkingAreaCount.setText(parkingNumberOfIndividualMarker);
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        double bottomSheetDistance = calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                                bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude);

                        double kim = (bottomSheetDistance / 1000) + adjustValue;
                        double bottomSheetDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(bottomSheetDistance * 2.43));
                        String bottomSheetStringDuration = String.valueOf(bottomSheetDoubleDuration);

                        bookingSensorsBottomSheetArrayList.add(new BookingSensors(bottomSheetPlaceName, bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude,
                                bottomSheetDistance, textViewBottomSheetParkingAreaCount.getText().toString(), bottomSheetStringDuration,
                                context.getResources().getString(R.string.nearest_parking_from_your_destination),
                                BookingSensors.TEXT_INFO_TYPE, 0));

                        if (sensorArrayList != null) {

                            setBottomSheetList(() -> {
                                if (bottomSheetAdapter != null) {
                                    bookingSensorsArrayListGlobal.clear();
                                    bookingSensorsArrayListGlobal.addAll(bookingSensorsBottomSheetArrayList);
                                    bottomSheetAdapter.notifyDataSetChanged();
                                    bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._142sdp));
                                } else {
                                    Timber.e("bottomSheetAdapter null");
                                }
                            }, sensorArrayList, bottomSheetPlaceLatLng, bookingSensorsBottomSheetArrayList, bottomUid);
                        }

                        bottomSheetAdapter.setDataList(bookingSensorsBottomSheetArrayList);

                    }, (dialog, which) -> {
                        Timber.e("Negative Button Clicked");
                        dialog.dismiss();
                    });
                }
            } catch (Exception e) {
                e.getCause();
                ApplicationUtils.showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
            }
        });

        bottomSheetAdapter.setDataList(bookingSensors);

        bottomSheetRecyclerView.setAdapter(bottomSheetAdapter);

        if (bookingSensors.size() == 0) {
            setNoData();
        } else {
            hideNoData();
        }
    }

    private double calculateDistance(Double startLatitude, Double startLongitude, Double endLatitude, Double endLongitude) {
        return ApplicationUtils.calculateDistance(startLatitude, startLongitude, endLatitude, endLongitude);
    }

    private String getAddress(Context context, double LATITUDE, double LONGITUDE) {
        //Set Address
        String addressTemp;
        List<Address> addresses;
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            Timber.e("getAddress if--->%s", addresses.toString());

            if (addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                test = addresses.get(0).getAddressLine(1); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                subAdminArea = addresses.get(0).getSubAdminArea();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                addressTemp = addresses.get(0).getAddressLine(0);
            } else {
                countadd++;

                Timber.e("getAddress else--->%s", addresses.toString());
                addressTemp = googleApiAddressCall(context, LATITUDE, LONGITUDE, new AddressCallBack() {
                    @Override
                    public void addressCall(String address) {

                    }
                });

            }

        } catch (IOException e) {
            e.printStackTrace();
            addressTemp = googleApiAddressCall(context, LATITUDE, LONGITUDE, new AddressCallBack() {
                @Override
                public void addressCall(String address) {
                }
            });

        }

        return addressTemp;
    }

    private String getAddress(Context context, double LATITUDE, double LONGITUDE, AddressCallBack addressCallBack) {

        String addressTemp;
        List<Address> addresses;
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            Timber.e("getAddress try--->%s", addresses.toString());

            if (addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                test = addresses.get(0).getAddressLine(1); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                subAdminArea = addresses.get(0).getSubAdminArea();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getFeatureName();
                // Only if available else return NULL
                addressTemp = addresses.get(0).getAddressLine(0);

                String finalAddressTemp = addressTemp;

                addressCallBack.addressCall(finalAddressTemp);

                Timber.e("getAddress if size >0--->%s", addresses.toString());
            } else {
                countadd++;

                Timber.e("getAddress else--->%s", addresses.toString());
                addressTemp = googleApiAddressCall(context, LATITUDE, LONGITUDE, new AddressCallBack() {
                    @Override
                    public void addressCall(String address) {
                        addressCallBack.addressCall(address);
                        // Toast.makeText(context, "else "+address, Toast.LENGTH_SHORT).show();
                    }
                });

            }

        } catch (IOException e) {
            e.printStackTrace();

            addressTemp = googleApiAddressCall(context, LATITUDE, LONGITUDE, new AddressCallBack() {
                @Override
                public void addressCall(String address) {

                    addressCallBack.addressCall(address);
                }
            });
        }

        return addressTemp;
    }

    private String googleApiAddressCall(Context context, double latitude, double longitude, AddressCallBack addressCallBack) {

        final String[] formatted_address = {""};
        HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=AIzaSyCsEedODXq-mkA1JYedp-Y-QARH0x4h0kI",
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        Timber.e("jsonObject -> %s", jsonObject.toString());

                        if (jsonObject.getString("status").equals("OK")) {

                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray("results");
                            formatted_address[0] = jsonArray.getJSONObject(0).getString("formatted_address");
                            Timber.e("jsonArray areaName -> %s", formatted_address[0]);
                            addressCallBack.addressCall(formatted_address[0]);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Timber.e("Error Message -> %s ", error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ParkingApp.getInstance().addToRequestQueue(stringRequest, TAG);

        return formatted_address[0];
    }

    private void storeVisitedPlace(String mobileNo, String placeId, double endLatitude, double endLongitude,
                                   double startLatitude, double startLongitude, String areaAddress) {

        // UI Service.
        ApiService service = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<BaseResponse> sensorsCall = service.storeSearchHistory(mobileNo, placeId, String.valueOf(endLatitude),
                String.valueOf(endLongitude), String.valueOf(startLatitude), String.valueOf(startLongitude), String.valueOf(areaAddress));

        // Gathering results.
        sensorsCall.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull retrofit2.Response<BaseResponse> response) {
                Timber.e("response -> %s", response.message());

                if (response.body() != null && !response.body().getError()) {
                    if (response.isSuccessful()) {
                        Timber.e("response search result store -> %s", new Gson().toJson(response.body()));
                    } else {
                        Timber.e("Errors: -> %s", new Gson().toJson(response.message()));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable errors) {
                Timber.e("Throwable Errors: -> %s", errors.toString());
            }
        });
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

        String serverKey = context.getResources().getString(R.string.google_maps_key); // Api Key For Google Direction API \\

        if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {

            if (onConnectedLocation != null) {
                origin = new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude());
            } else {
                origin = new LatLng(SharedData.getInstance().getOnConnectedLocation().getLatitude(),
                        SharedData.getInstance().getOnConnectedLocation().getLongitude());
            }

            //-------------Using AK Exorcist Google Direction Library---------------\\
            GoogleDirection.withServerKey(serverKey)
                    .from(origin)
                    .to(latLngDestination)
                    .transportMode(TransportMode.DRIVING)
                    .execute(new DirectionCallback() {

                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            //dismissDialog();
                            String status = direction.getStatus();
                            if (status.equals(RequestResult.OK)) {
                                Route route = direction.getRouteList().get(0);
                                Leg leg = route.getLegList().get(0);
                                Info distanceInfo = leg.getDistance();
                                Info durationInfo = leg.getDuration();
                                String distance = distanceInfo.getText();
                                String duration = durationInfo.getText();

                                textViewParkingTravelTime.setText(duration);
                                textViewSearchParkingDistance.setText(distance);

                                textViewSearchParkingTravelTime.setText(duration);
                                textViewBottomSheetParkingTravelTime.setText(duration);

                                nearByDuration = duration;
                                //nearByDistance = distance;
                                fetchDuration = duration;

                                //------------Displaying Distance and Time-----------------\\
                                String message = "Total Distance is " + distance + " and Estimated Time is " + duration;
                                Timber.e("duration message -> %s", message);
                            } else if (status.equals(RequestResult.NOT_FOUND)) {
                                Toast.makeText(context, context.getResources().getString(R.string.no_route_exist), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            // Do something here
                        }
                    });
        } else {
            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
        }

        //-------------------------------------------------------------------------------\\

    }

    //getting the direction url
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Key
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

        mService.getDataFromGoogleApi(url)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {

                        try {
                            JSONObject jsonObject =
                                    new JSONObject(response.body());
                            JSONArray jsonArray = jsonObject.getJSONArray("routes");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject route = jsonArray.getJSONObject(i);
                                JSONObject poly = route.getJSONObject("overview_polyline");
                                String polyLine = poly.getString("points");
                                if (polyLineList != null) {
                                    polyLineList.clear();
                                }
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
                            //polylineOptions.color(Color.GRAY);
                            polylineOptions.color(context.getResources().getColor(R.color.route_color));
                            polylineOptions.width(7f);
                            polylineOptions.geodesic(true);
                            polylineOptions.startCap(new SquareCap());
                            polylineOptions.endCap(new SquareCap());
                            polylineOptions.jointType(JointType.ROUND);
                            polylineOptions.addAll(polyLineList);
                            grayPolyline = mMap.addPolyline(polylineOptions);

                            blackPolylineOptions = new PolylineOptions();
                            //blackPolylineOptions.color(Color.BLACK);
                            blackPolylineOptions.color(context.getResources().getColor(R.color.route_color));
                            blackPolylineOptions.width(7f);
                            blackPolylineOptions.zIndex(5f);
                            blackPolylineOptions.geodesic(true);
                            blackPolylineOptions.startCap(new SquareCap());
                            blackPolylineOptions.endCap(new SquareCap());
                            blackPolylineOptions.jointType(JointType.ROUND);
                            blackPolylineOptions.addAll(polyLineList);
                            blackPolyline = mMap.addPolyline(blackPolylineOptions);

                            zoomRoute(mMap, polyLineList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(mMap.getCameraPosition().target)
                .zoom(16f)
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

        int routePadding = 250;
        int left = 50;
        int right = 50;
        int top = ApplicationUtils.getToolBarHeight(context);
        int bottom = (int) context.getResources().getDimension(R.dimen._200sdp);

        LatLngBounds latLngBounds = boundsBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
        googleMap.setPadding(left, top, right, bottom);
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetMarkerEvent event) {

        adapterPlaceLatLng = event.location;

        hideLoading();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCustomDirectionEvent(GetDirectionAfterButtonClickEvent event) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapterPlaceLatLng = event.location;

                coordList.add(new LatLng(event.location.latitude, event.location.longitude));

                getDirectionPinMarkerDraw(adapterPlaceLatLng, adapterUid);

                btnGetDirection.setVisibility(View.VISIBLE);
                linearLayoutBottom.setVisibility(View.VISIBLE);
                imageViewBack.setVisibility(View.VISIBLE);

                String origin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();

                String destination = null;

                if (event.location != null) {
                    destination = "" + event.location.latitude + ", " + event.location.longitude;
                }

                fetchDirections(origin, destination);

                setMapZoomLevelDirection(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()), adapterPlaceLatLng);

                isRouteDrawn = 1;
            }
        }, 1000);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onBottomSheetDirectionEvent(GetDirectionBottomSheetEvent event) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomSheetPlaceLatLng = event.location;

                adapterPlaceLatLng = event.location;

                searchPlaceLatLng = event.location;

                markerPlaceLatLng = event.location;

                btnBottomSheetGetDirection.setVisibility(View.VISIBLE);
                linearLayoutBottomSheetBottom.setVisibility(View.VISIBLE);
                imageViewBottomSheetBack.setVisibility(View.VISIBLE);

                String origin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();

                String destination = null;

                destination = "" + event.location.latitude + ", " + event.location.longitude;

                fetchDirections(origin, destination);

                isRouteDrawn = 1;

                setMapZoomLevelDirection(new LatLng(onConnectedLocation.getLatitude(),
                        onConnectedLocation.getLongitude()), event.location);

            }
        }, 1000);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSearchDirectionEvent(GetDirectionForSearchEvent event) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchPlaceLatLng = event.location;

                btnSearchGetDirection.setVisibility(View.VISIBLE);

                linearLayoutSearchBottomButton.setVisibility(View.VISIBLE);
                imageViewSearchBack.setVisibility(View.VISIBLE);
                linearLayoutBottom.setVisibility(View.GONE);
                linearLayoutMarkerBottom.setVisibility(View.GONE);

                String origin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();

                String destination = null;

                if (searchPlaceLatLng != null) {
                    destination = "" + searchPlaceLatLng.latitude + ", " + searchPlaceLatLng.longitude;
                }

                fetchDirections(origin, destination);

                setMapZoomLevelDirection(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()), searchPlaceLatLng);
            }
        }, 1000);
    }

    public Marker previousDestinationMarker;
    public Marker previousSecondMarkerDestinationMarker;
    public boolean isDestinationMarkerDrawn = false;

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMarkerDirectionEvent(GetDirectionForMarkerEvent event) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                markerPlaceLatLng = event.location;

                getDirectionPinMarkerDraw(markerPlaceLatLng, markerUid);

                isDestinationMarkerDrawn = true;

                btnMarkerGetDirection.setVisibility(View.VISIBLE);

                String origin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();

                String destination = null;

                if (event.location != null) {
                    destination = "" + event.location.latitude + ", " + event.location.longitude;
                }

                fetchDirections(origin, destination);

                setMapZoomLevelDirection(new LatLng(onConnectedLocation.getLatitude(),
                        onConnectedLocation.getLongitude()), event.location);

                fromMarkerRouteDrawn = 1;
            }
        }, 1000);
    }

    private void setMapZoomLevelDirection(LatLng startPosition, LatLng endPosition) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(startPosition);
        builder.include(endPosition);

        if (getContext() != null) {
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding;

            //padding = (int) (height * 0.05);
            padding = (int) (Math.min(width, height) * 0.30);
            //padding = ((height) / 500);

            LatLngBounds bounds = builder.build();

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
        }
    }

    @SuppressLint("SetTextI18n")
    private void layoutVisible(boolean isVisible, String name, String count,
                               String distance, LatLng location) {
        this.areaName = name;
        this.parkingSlotCount = count;
        this.distance = distance;
        HomeFragment.adapterPlaceLatLng = location;

        if (isVisible) {
            try {
                linearLayoutParkingAdapterBackBottom.setVisibility(View.VISIBLE);
                linearLayoutBottom.setVisibility(View.GONE);
                linearLayoutNameCount.setVisibility(View.GONE);
            } catch (Exception e) {
                e.getCause();
            }
        } else {

        }
    }

    private boolean isSearchAreaVisible = false;

    @SuppressLint("SetTextI18n")
    private void layoutSearchVisible(boolean isVisible, String name, String count,
                                     String distance, LatLng location) {
        this.name = name;
        this.count = count;
        this.searchPlaceCount = count;
        this.searchPlaceLatLng = location;
        this.distance = distance;

        if (isVisible) {
            linearLayoutSearchBottom.setVisibility(View.VISIBLE);
            linearLayoutSearchNameCount.setVisibility(View.GONE);
            textViewSearchParkingAreaCount.setText(count);
            textViewSearchParkingAreaName.setText(ApplicationUtils.capitalizeFirstLetter(name));
            isSearchAreaVisible = true;
        } else {
            linearLayoutSearchBottom.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void layoutMarkerVisible(boolean isVisible, String name, String count,
                                     String distance, LatLng location) {
        this.name = name;
        this.count = count;
        this.distance = distance;
        this.markerPlaceLatLng = location;
        this.duration = duration;

        if (isVisible) {

            linearLayoutMarkerNameCount.setVisibility(View.GONE);
            linearLayoutMarkerBottom.setVisibility(View.VISIBLE);

            btnMarkerGetDirection.setText(context.getResources().getString(R.string.get_direction));
            btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
            btnMarkerGetDirection.setEnabled(true);
            btnMarkerGetDirection.setFocusable(true);
        } else {
            linearLayoutMarkerBottom.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    public void layoutBottomSheetVisible(boolean isVisible, String name, String count,
                                         String distance, String duration, LatLng location, boolean isClicked) {
        this.name = name;
        this.count = count;
        this.searchPlaceCount = count;
        this.distance = distance;
        this.bottomSheetPlaceLatLng = location;
        this.duration = duration;

        String uid = "";

        String locationName = "";

        if (isVisible) {
            if (bottomSheetPlaceLatLng != null) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(bottomSheetPlaceLatLng);

                coordList.add(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));

                for (int i = 0; i < sensorArrayList.size(); i++) {

                    Sensor sensor = sensorArrayList.get(i);
                    try {
                        String latitude1 = sensor.getLatitude();
                        String longitude1 = sensor.getLongitude();
                        uid = sensor.getUid();

                        if (isSearchAreaVisible) {
                            locationName = name;
                        } else {
                            locationName = sensor.getParkingArea();
                        }

                        double distanceForCount = calculateDistance(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude,
                                ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));

                        if (distanceForCount < 0.001) {
                            bottomUid = uid;
                            Timber.e("bottomUid -> %s", bottomUid);
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Timber.e("clickEventJsonArray for loop sesh hoiche");
                }
                getDirectionPinMarkerDraw(bottomSheetPlaceLatLng, bottomUid);
            }
            linearLayoutBottomSheetBottom.setVisibility(View.VISIBLE);
            linearLayoutBottomSheetNameCount.setVisibility(View.GONE);
            textViewBottomSheetParkingAreaCount.setText(count);
            textViewBottomSheetParkingAreaName.setText(ApplicationUtils.capitalizeFirstLetter(name));
            textViewBottomSheetParkingDistance.setText(distance.substring(0, 3) + " km");
            getDestinationInfoForDuration(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));

        } else {
            linearLayoutBottomSheetBottom.setVisibility(View.GONE);
        }
    }

    public void commonBackOperation() {
        if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
            if (mMap != null) {
                mMap.clear();
                mMap.setTrafficEnabled(true);

                previousDestinationMarker = null;
                previousGetDestinationMarker = null;
                previousMarker = null;
                isRouteDrawn = 0;
                fromMarkerRouteDrawn = 0;

                getDirectionButtonClicked = 0;
                getDirectionSearchButtonClicked = 0;
                getDirectionMarkerButtonClicked = 0;
                getDirectionBottomSheetButtonClicked = 0;

                if (bottomSheetAdapter != null) {
                    bottomSheetAdapter.clear();
                    bottomSheetAdapter = null;
                }

                bookingSensorsArrayList.clear();
                bookingSensorsAdapterArrayList.clear();
                bookingSensorsMarkerArrayList.clear();
                bookingSensorsBottomSheetArrayList.clear();

                if (getArguments() != null) {
                    getArguments().clear();
                }

                if (onConnectedLocation != null) {
                    fetchSensorRetrofit(onConnectedLocation);
                    animateCamera(onConnectedLocation);
                }

                linearLayoutBottom.setVisibility(View.GONE);
                linearLayoutSearchBottom.setVisibility(View.GONE);
                linearLayoutMarkerBottom.setVisibility(View.GONE);
                linearLayoutBottomSheetBottom.setVisibility(View.GONE);

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._92sdp));

                buttonSearch.setText(null);
                buttonSearch.setVisibility(View.VISIBLE);

                ApplicationUtils.recreateFragment(getParentFragmentManager(), this);

                try {
                    if (polyline == null || !polyline.isVisible())
                        return;

                    points = polyline.getPoints();
                    polyline.remove();

                } catch (Exception e) {
                    e.getCause();
                }
            }
        } else {
            ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                Timber.e("Positive Button clicked");
                if (ApplicationUtils.checkInternet(context)) {
                    fetchSensorRetrofit(onConnectedLocation);
                } else {
                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                }
            }, (dialog, which) -> {
                Timber.e("Negative Button Clicked");
                dialog.dismiss();
                if (getActivity() != null) {
                    getActivity().finish();
                    TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                }
            });
        }
    }

    private void setListeners() {

        buttonSearch.setOnClickListener(v -> {
            if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
                Intent intent = new Intent(context, SearchActivity.class);

                startActivityForResult(intent, NEW_SEARCH_ACTIVITY_REQUEST_CODE);

                if (mMap != null)
                    mMap.clear();

                if (searchPlaceLatLng != null) {
                    bookingSensorsArrayList.clear();
                }

                bookingSensorsArrayListGlobal.clear();
                bookingSensorsArrayList.clear();
                bookingSensorsMarkerArrayList.clear();

                fetchSensorRetrofit(onConnectedLocation);

                buttonSearch.setText(null);
                linearLayoutBottom.setVisibility(View.GONE);
                linearLayoutSearchBottom.setVisibility(View.GONE);
                linearLayoutMarkerBottom.setVisibility(View.GONE);
                linearLayoutBottomSheetBottom.setVisibility(View.GONE);
            } else {
                ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                    Timber.e("Positive Button clicked");
                    if (ApplicationUtils.checkInternet(context)) {
                        fetchSensorRetrofit(onConnectedLocation);
                    } else {
                        TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                    }
                }, (dialog, which) -> {
                    Timber.e("Negative Button Clicked");
                    dialog.dismiss();
                    if (getActivity() != null) {
                        getActivity().finish();
                        TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                    }
                });
            }
        });

        imageViewBack.setOnClickListener(v -> {

            commonBackOperation();

            layoutVisible(false, "", "", " ", null);

            SharedData.getInstance().setParkingLocation(null);

            SharedData.getInstance().setSensorArea(null);

            btnGetDirection.setText(context.getResources().getString(R.string.get_direction));
            btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
            btnGetDirection.setEnabled(true);
            btnGetDirection.setFocusable(true);
        });

        imageViewSearchBack.setOnClickListener(v -> {

            commonBackOperation();

            fromSearchMultipleRouteDrawn = 0;

            layoutSearchVisible(false, "", "", "", null);

            btnSearchGetDirection.setText(context.getResources().getString(R.string.get_direction));
            btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
            btnSearchGetDirection.setEnabled(true);
            btnSearchGetDirection.setFocusable(true);

            if (getDirectionSearchButtonClicked == 1) {
                btnSearchGetDirection.setText(context.getResources().getString(R.string.get_direction));
                btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                getDirectionSearchButtonClicked--;
            }
        });

        imageViewMarkerBack.setOnClickListener(v -> {

            commonBackOperation();

            layoutMarkerVisible(false, "", "", "", null);

            if (getDirectionMarkerButtonClicked == 1) {
                btnMarkerGetDirection.setText(context.getResources().getString(R.string.get_direction));
                btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                getDirectionMarkerButtonClicked--;
            }
        });

        imageViewBottomSheetBack.setOnClickListener(v -> {

            commonBackOperation();

            layoutBottomSheetVisible(false, "", "", "", "", null, false);

            if (getDirectionBottomSheetButtonClicked == 1) {
                btnBottomSheetGetDirection.setText(context.getResources().getString(R.string.get_direction));
                btnBottomSheetGetDirection.setEnabled(true);
                btnBottomSheetGetDirection.setFocusable(true);
                btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                getDirectionBottomSheetButtonClicked--;
            }

            btnBottomSheetGetDirection.setText(context.getResources().getString(R.string.get_direction));
            btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
            btnBottomSheetGetDirection.setEnabled(true);
            btnBottomSheetGetDirection.setFocusable(true);
            getDirectionBottomSheetButtonClicked = 0;
        });

        btnGetDirection.setOnClickListener(v -> {
            if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {

                mMap.setTrafficEnabled(false);

                isRouteDrawn = 1;

                if (getDirectionButtonClicked == 0) {

                    getDirectionButtonClicked++;


                    if (adapterPlaceLatLng != null) {
                        EventBus.getDefault().post(new GetDirectionAfterButtonClickEvent(adapterPlaceLatLng));

                        setCircleOnLocation(adapterPlaceLatLng);

                        buttonSearch.setVisibility(View.GONE);

                        linearLayoutBottom.setVisibility(View.VISIBLE);
                        linearLayoutSearchBottom.setVisibility(View.GONE);
                        linearLayoutMarkerBottom.setVisibility(View.GONE);
                        imageViewBack.setVisibility(View.VISIBLE);

                        btnGetDirection.setText(context.getString(R.string.confirm_booking));
                        btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                        btnGetDirection.setEnabled(true);
                        btnGetDirection.setFocusable(true);

                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                        if (isInAreaEnabled) {
                            btnGetDirection.setEnabled(true);
                            btnGetDirection.setFocusable(true);
                            btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                            Toast.makeText(context, context.getResources().getString(R.string.you_can_book_parking_slot), Toast.LENGTH_LONG).show();
                        } else {
                            btnGetDirection.setEnabled(true);
                            btnGetDirection.setFocusable(true);
                            btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                        }
                    }
                } else if (getDirectionButtonClicked == 1) {
                    ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.confirm_booking_message), context);
                    //getDirectionButtonClicked--;
                    if (isInAreaEnabled) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("m", false); //m for more
                        bundle.putString("markerUid", adapterUid);
                        ScheduleFragment scheduleFragment = new ScheduleFragment();
                        scheduleFragment.setArguments(bundle);
                        listener.fragmentChange(scheduleFragment);
                        bottomSheet.setVisibility(View.GONE);
                    }
                }

            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        });

        btnSearchGetDirection.setOnClickListener(v -> {
            if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {

                mMap.setTrafficEnabled(false);

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                isRouteDrawn = 1;

                if (getDirectionSearchButtonClicked == 0) {

                    getDirectionSearchButtonClicked++;

                    if (searchPlaceLatLng != null) {
                        EventBus.getDefault().post(new GetDirectionForSearchEvent(searchPlaceLatLng));

                        buttonSearch.setVisibility(View.GONE);

                        linearLayoutSearchBottomButton.setVisibility(View.VISIBLE);
                        linearLayoutBottom.setVisibility(View.GONE);
                        linearLayoutMarkerBottom.setVisibility(View.GONE);
                        imageViewSearchBack.setVisibility(View.VISIBLE);

                        btnSearchGetDirection.setText(context.getResources().getString(R.string.unavailable_parking_spot));
                        btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                        btnSearchGetDirection.setEnabled(true);
                        btnSearchGetDirection.setFocusable(true);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }

                } else if (getDirectionSearchButtonClicked == 1) {
                    if (fromSearchMultipleRouteDrawn == 0) {
                        ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.no_parking_spot_message), context);
                    } else {
                        ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.confirm_booking_message), context);
                    }

                    if (isInAreaEnabled) {
                        if (markerPlaceLatLng != null) {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("m", false); //m for more
                            bundle.putString("markerUid", markerUid);
                            ScheduleFragment scheduleFragment = new ScheduleFragment();
                            scheduleFragment.setArguments(bundle);
                            listener.fragmentChange(scheduleFragment);
                            bottomSheet.setVisibility(View.GONE);
                        } else if (bottomSheetPlaceLatLng != null) {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("m", false); //m for more
                            bundle.putString("markerUid", bottomUid);
                            ScheduleFragment scheduleFragment = new ScheduleFragment();
                            scheduleFragment.setArguments(bundle);
                            listener.fragmentChange(scheduleFragment);
                            bottomSheet.setVisibility(View.GONE);
                        }
                    }
                    //getDirectionSearchButtonClicked--;
                }
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        });

        btnMarkerGetDirection.setOnClickListener(v -> {

            if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
                if (parkingNumberOfIndividualMarker.equals("0")) {
                    ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.no_parking_spot_message), context);
                } else {
                    //ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.confirm_booking_message), context);
                }

                mMap.setTrafficEnabled(false);
                isRouteDrawn = 1;

                if (getDirectionMarkerButtonClicked == 0) {

                    getDirectionMarkerButtonClicked++;

                    if (markerPlaceLatLng != null) {
                        setCircleOnLocation(markerPlaceLatLng);
                        linearLayoutMarkerNameCount.setVisibility(View.GONE);
                        buttonSearch.setVisibility(View.GONE);
                        bookingSensorsArrayListGlobal.clear();
                        bookingSensorsArrayList.clear();

                        coordList.add(new LatLng(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude));

                        isDestinationMarkerDrawn = true;

                        btnMarkerGetDirection.setVisibility(View.VISIBLE);

                        String origin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();

                        String destination = null;

                        if (markerPlaceLatLng != null) {
                            destination = "" + markerPlaceLatLng.latitude + ", " + markerPlaceLatLng.longitude;
                        }

                        fetchDirections(origin, destination);

                        fromMarkerRouteDrawn = 1;
                        getDirectionPinMarkerDraw(markerPlaceLatLng, markerUid);

                        linearLayoutNameCount.setVisibility(View.GONE);
                        linearLayoutSearchBottom.setVisibility(View.GONE);
                        linearLayoutBottom.setVisibility(View.GONE);
                        linearLayoutMarkerNameCount.setVisibility(View.GONE);
                        imageViewMarkerBack.setVisibility(View.VISIBLE);

                        if (parkingNumberOfIndividualMarker.equals("0")) {
                            Timber.e("parkingNumberOfIndividualMarker 0 if condition called");

                            btnMarkerGetDirection.setText(context.getString(R.string.unavailable_parking_spot));
                            btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                            btnMarkerGetDirection.setEnabled(true);
                            btnMarkerGetDirection.setFocusable(true);

                            getDirectionMarkerButtonClicked = 1;
                        } else {
                            btnMarkerGetDirection.setText(context.getResources().getString(R.string.confirm_booking));
                            btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                            btnMarkerGetDirection.setEnabled(true);
                            btnMarkerGetDirection.setFocusable(true);
                        }

                        if (isInAreaEnabled) {
                            if (parkingNumberOfIndividualMarker.equals("0")) {
                                btnMarkerGetDirection.setText(context.getResources().getString(R.string.unavailable_parking_spot));
                                btnMarkerGetDirection.setEnabled(true);
                                btnMarkerGetDirection.setFocusable(true);
                                btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                            } else {
                                btnMarkerGetDirection.setText(context.getResources().getString(R.string.confirm_booking));
                                btnMarkerGetDirection.setEnabled(true);
                                btnMarkerGetDirection.setFocusable(true);
                                btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                                Toast.makeText(context, context.getResources().getString(R.string.you_can_book_parking_slot), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            btnMarkerGetDirection.setEnabled(true);
                            btnMarkerGetDirection.setFocusable(true);
                            btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                        }

                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }

                } else if (getDirectionMarkerButtonClicked == 1) {
                    if (parkingNumberOfIndividualMarker.equals("0")) {
                        ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.no_parking_spot_message), context);
                    } else {
                        ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.confirm_booking_message), context);
                        if (isInAreaEnabled) {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("m", false); //m for more
                            bundle.putString("markerUid", markerUid);
                            ScheduleFragment scheduleFragment = new ScheduleFragment();
                            scheduleFragment.setArguments(bundle);
                            listener.fragmentChange(scheduleFragment);
                            bottomSheet.setVisibility(View.GONE);
                        }
                    }
                }
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        });

        btnBottomSheetGetDirection.setOnClickListener(v -> {
            if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {

                mMap.setTrafficEnabled(false);

                isRouteDrawn = 1;

                if (getDirectionBottomSheetButtonClicked == 0) {

                    getDirectionBottomSheetButtonClicked++;
                    if (count.equals("0")) {
                        ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.no_parking_spot_message), context);
                    } else {
                        //ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.confirm_booking_message), context);
                    }
                    if (bottomSheetPlaceLatLng != null ||
                            adapterPlaceLatLng != null ||
                            markerPlaceLatLng != null ||
                            searchPlaceLatLng != null) {
                        //Timber.e("all location called");

                        setCircleOnLocation(bottomSheetPlaceLatLng);
                        EventBus.getDefault().post(new GetDirectionBottomSheetEvent(bottomSheetPlaceLatLng));

                        linearLayoutBottom.setVisibility(View.GONE);
                        linearLayoutSearchBottom.setVisibility(View.GONE);
                        linearLayoutMarkerBottom.setVisibility(View.GONE);
                        linearLayoutBottomSheetBottom.setVisibility(View.VISIBLE);
                        btnBottomSheetGetDirection.setText(context.getResources().getString(R.string.confirm_booking));
                        btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                        btnBottomSheetGetDirection.setEnabled(true);
                        btnBottomSheetGetDirection.setFocusable(true);

                        if (count.equals("0")) {
                            Timber.e("count 0 if condition called");
                            btnBottomSheetGetDirection.setText(context.getResources().getString(R.string.unavailable_parking_spot));
                            btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                            btnBottomSheetGetDirection.setEnabled(true);
                            btnBottomSheetGetDirection.setFocusable(true);
                            getDirectionBottomSheetButtonClicked = 1;
                        } else {
                            Timber.e("count not 0 else condition called");
                            btnBottomSheetGetDirection.setText(context.getResources().getString(R.string.confirm_booking));
                            btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                            btnBottomSheetGetDirection.setEnabled(true);
                            btnBottomSheetGetDirection.setFocusable(true);
                        }

                        if (isInAreaEnabled) {
                            if (count.equals("0")) {
                                btnBottomSheetGetDirection.setText(context.getResources().getString(R.string.unavailable_parking_spot));
                                btnBottomSheetGetDirection.setEnabled(true);
                                btnBottomSheetGetDirection.setFocusable(true);
                                btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                            } else {
                                btnBottomSheetGetDirection.setText(context.getResources().getString(R.string.confirm_booking));
                                btnBottomSheetGetDirection.setEnabled(true);
                                btnBottomSheetGetDirection.setFocusable(true);
                                btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                                Toast.makeText(context, context.getResources().getString(R.string.you_can_book_parking_slot), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            btnBottomSheetGetDirection.setEnabled(true);
                            btnBottomSheetGetDirection.setFocusable(true);
                            btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                        }

                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                } else if (getDirectionBottomSheetButtonClicked == 1) {
                    //getDirectionBottomSheetButtonClicked--;
                    ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.confirm_booking_message), context);
                    if (isInAreaEnabled) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("m", false); //m for more
                        bundle.putString("markerUid", bottomUid);
                        ScheduleFragment scheduleFragment = new ScheduleFragment();
                        scheduleFragment.setArguments(bundle);
                        listener.fragmentChange(scheduleFragment);
                        bottomSheet.setVisibility(View.GONE);
                    }

                    if (mMap != null) {
                        fromMarkerRouteDrawn = 0;
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
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
                countDownTV.setText(getTimeDifference(millisUntilFinished));
            }

            public void onFinish() {
                countDownTV.setText("done!");
            }
        }.start();
    }

    @SuppressLint("DefaultLocale")
    private String getTimeDifference(long difference) {

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

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        //sendNotification("ParkingApp", String.format("%s entered the Parking Area", key));
        //isInAreaEnabled = true;
    }

    @Override
    public void onKeyExited(String key) {
        //sendNotification("ParkingApp", String.format("%s leave the Parking Area", key));
        //isInAreaEnabled = false;
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        //sendNotification("ParkingApp", String.format("%s move within the Parking Area", key));
    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {

    }

    private void sendNotification(String title, String content) {
        String NOTIFICATION_CHANNEL_ID = "Shawn_Muktadir";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            //config
            notificationChannel.setDescription("Parking App Booking Notification");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        Notification notification = builder.build();
        if (notificationManager != null) {
            notificationManager.notify(new Random().nextInt(), notification);
        }
    }

    @Override
    public void onLoadLocationSuccess(List<MyLatLng> latLngs) {
        Timber.e("onLoadLocationSuccess called");
        dangerousArea = new ArrayList<>();
        for (MyLatLng myLatLng : latLngs) {
            LatLng convert = new LatLng(myLatLng.getLatitude(), myLatLng.getLongitude());
            dangerousArea.add(convert);
        }

        //clear map and add again
        if (mMap != null) {
            mMap.clear();
            //Add user Marker
            addUserMarker();

            //Add circle of dangerous area
            //addCircleArea();
        }
    }

    @Override
    public void onLoadLocationFailed(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void initArea() {
        DatabaseReference myCity = FirebaseDatabase.getInstance().getReference("DangerousArea").child("MyCity");
        IOnLoadLocationListener locationListener = this;

        dangerousArea = new ArrayList<>();
        //dangerousArea.add(new LatLng(23.7852, 90.4563));
        //dangerousArea.add(new LatLng(23.7209, 90.4833));
        dangerousArea.add(new LatLng(23.7759521, 90.4101246));
        //Timber.e("dangerousArea -> %s", new Gson().toJson(dangerousArea));

        //after submit to database, comment it
        FirebaseDatabase.getInstance().getReference("DangerousArea")
                .child("MyCity")
                .setValue(dangerousArea)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addUserMarker() {
        if (geoFire != null) {
            geoFire.setLocation("You", new GeoLocation(mLastLocation.getLatitude(),
                    mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    if (currentUser != null) currentUser.remove();
                    /*currentUser = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lastLocation.getLatitude(),
                                    lastLocation.getLongitude())).title("You"));
                    //after add marker move camera
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUser.getPosition(), 16f));*/
                }
            });
        }
    }

    private void settingGeoFire() {
        DatabaseReference myLocationRef = FirebaseDatabase.getInstance().getReference("MyLocation");
        geoFire = new GeoFire(myLocationRef);
    }

    private void buildLocationCallBack() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                        if (mMap != null) {
                            mLastLocation = locationResult.getLastLocation();
                            SharedData.getInstance().setLastLocation(mLastLocation);
                            addUserMarker();
                        }
                    }
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void buildLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setSmallestDisplacement(10f);
    }

    private void addCircleArea() {
        if (geoQuery != null) {
            geoQuery.removeGeoQueryEventListener(this);
            geoQuery.removeAllListeners();
        }

        Timber.e("SharedData.getInstance().getLatLng() == null called");

        for (LatLng latLng : dangerousArea) {
            mMap.addCircle(new CircleOptions().center(latLng)
                    .radius(300) // 500m
                    .strokeColor(Color.BLUE)
                    .fillColor(0x220000FF) //22 is transparent code
                    .strokeWidth(5.0f)
            );

            //create GeoQuery when user in dangerous location
            geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 0.3f); // 500m
            geoQuery.addGeoQueryEventListener(this);
        }
    }

    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {
            /*AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("GPS Permissions")
                    .setMessage("GPS is required for this app to work. Please enable GPS.")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();*/
        }

        return false;
    }

    private boolean isServicesOk() {

        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();

        int result = googleApi.isGooglePlayServicesAvailable(context);

        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApi.isUserResolvableError(result)) {
            Dialog dialog = googleApi.getErrorDialog(context, result, PLAY_SERVICES_ERROR_CODE, task ->
                    Toast.makeText(context, "Dialog is cancelled by User", Toast.LENGTH_SHORT).show());
            dialog.show();
        }

        return false;
    }

    private void startShimmer() {
        Timber.e("startShimmer");
        if (mShimmerViewContainer != null) {
            mShimmerViewContainer.setVisibility(View.VISIBLE);
            mShimmerViewContainer.startShimmer();
            bottomSheetRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            return;
        }
    }

    private void stopShimmer() {
        Timber.e("stopShimmer");
        if (mShimmerViewContainer != null) {
            mShimmerViewContainer.setVisibility(View.GONE);
            mShimmerViewContainer.stopShimmer();
            bottomSheetRecyclerView.setVisibility(View.VISIBLE);
        } else {
            return;
        }
    }

    public void setNoData() {
        if (textViewNoData != null) {
            textViewNoData.setVisibility(View.VISIBLE);
            textViewNoData.setText(context.getString(R.string.no_nearest_parking_area_found));
        }
    }

    public void hideNoData() {
        if (textViewNoData != null) {
            textViewNoData.setVisibility(View.GONE);
        } else {
            return;
        }
    }

    public interface SetBottomSheetCallBack {
        void setBottomSheet();
    }

    public interface AddressCallBack {
        void addressCall(String address);

    }

    String oldDestination = "";

    public void fetchDirections(String origin, String destination) {

        polyline = mMap.addPolyline(getDefaultPolyLines(points));

        if (!oldDestination.equalsIgnoreCase(destination))
            oldDestination = destination;

        if (origin.isEmpty() || destination.isEmpty()) {
            Toast.makeText(context, "Please first fill all the fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!origin.contains(",") || !destination.contains(",")) {
            Toast.makeText(context, "Invalid data fill in fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!polyline.isVisible())
            return;

        points = polyline.getPoints();

        polyline.remove();

        try {
            if (polyline == null || !polyline.isVisible())
                return;

            points = polyline.getPoints();

            polyline.remove();
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {
        showLoading(context);
    }

    List<www.fiberathome.com.parkingapp.module.GoogleMapWebServiceNDistance.directionModules.Route> updatedRoute;

    @Override
    public void onDirectionFinderSuccess(List<www.fiberathome.com.parkingapp.module.GoogleMapWebServiceNDistance.directionModules.Route> route) {
        hideLoading();

        if (!route.isEmpty() && polyline != null) polyline.remove();
        try {
            updatedRoute = route;
            for (www.fiberathome.com.parkingapp.module.GoogleMapWebServiceNDistance.directionModules.Route mRoute : route) {
                PolylineOptions polylineOptions = getDefaultPolyLines(mRoute.points);
                initialRoutePoints = mRoute.points;
                /*if (polylineStyle == PolylineStyle.DOTTED)
                    polylineOptions = getDottedPolylines(route.points);*/
                polyline = mMap.addPolyline(polylineOptions);
                firstDraw = true;
//                for(int i= 0; i< initialRoutePoints.size();i++){
//                    mMap.addMarker(new MarkerOptions().position(initialRoutePoints.get(i))
//                            .title(String.valueOf(i)));
//                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error occurred on finding the directions...", Toast.LENGTH_SHORT).show();
        }
    }

    /*@SuppressLint("StaticFieldLeak")
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

            StringBuilder stringBuilder = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            responseString = stringBuilder.toString();
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
    }*/

    /*@SuppressLint("StaticFieldLeak")
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

            try {
                if (lists != null && lists.size() < 1) {
                    Timber.e("lists size -> %s", lists.size());
                    //Toast.makeText(context, "No Points", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Timber.e("lists size -> %s", lists.size());
                }
            } catch (Exception e) {
                e.getCause();
            }
            String duration = "";


            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();
                //for (int j = 0; j < path.size(); j++) {
                for (HashMap<String, String> point : path) {
                    double lat = ApplicationUtils.convertToDouble(point.get("lat"));
                    double lon = ApplicationUtils.convertToDouble(point.get("lon"));

                    //TODO
                    if (j == 0) {    // Get distance from the list
                            distance = (String) point.get("distance");
                            continue;
                        } else if (j == 1) { // Get duration from the list
                            duration = (String) point.get("duration");
                            continue;
                        }

                    Timber.e("duration -> %s", duration);

                    points.add(new LatLng(lat, lon));
                }
            }
            polylineOptions.addAll(points);
            polylineOptions.width(5);
            if (flag == 1) {
                //polylineOptions.color(Color.BLACK);
                polylineOptions.color(context.getResources().getColor(R.color.route_color));
                polylineOptions.width(5);
            } else if (flag == 2) {
                if (mMap != null)
                    mMap.clear();
                polylineOptions.color(Color.TRANSPARENT);
                polylineOptions.width(5);
            }
            flag++;

            polylineOptions.geodesic(true);
            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(context, "Direction not found!", Toast.LENGTH_SHORT).show();
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

            double c = 2.5 * Math.asin(Math.sqrt(a));
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
    }*/
}