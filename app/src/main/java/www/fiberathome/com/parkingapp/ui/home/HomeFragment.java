package www.fiberathome.com.parkingapp.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
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
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.model.response.booking.BookingSensors;
import www.fiberathome.com.parkingapp.model.response.search.SearchVisitorData;
import www.fiberathome.com.parkingapp.model.response.search.SelectedPlace;
import www.fiberathome.com.parkingapp.model.response.sensors.Sensor;
import www.fiberathome.com.parkingapp.model.response.sensors.SensorArea;
import www.fiberathome.com.parkingapp.model.response.sensors.SensorsResponse;
import www.fiberathome.com.parkingapp.module.GoogleMapWebServiceNDistance.DirectionsParser;
import www.fiberathome.com.parkingapp.module.GoogleMapWebServiceNDistance.directionModules.DirectionFinder;
import www.fiberathome.com.parkingapp.module.GoogleMapWebServiceNDistance.directionModules.DirectionFinderListener;
import www.fiberathome.com.parkingapp.module.eventBus.GetDirectionAfterButtonClickEvent;
import www.fiberathome.com.parkingapp.module.eventBus.GetDirectionBottomSheetEvent;
import www.fiberathome.com.parkingapp.module.eventBus.GetDirectionForMarkerEvent;
import www.fiberathome.com.parkingapp.module.eventBus.GetDirectionForSearchEvent;
import www.fiberathome.com.parkingapp.module.eventBus.SetMarkerEvent;
import www.fiberathome.com.parkingapp.module.geoFenceInterface.IOnLoadLocationListener;
import www.fiberathome.com.parkingapp.module.geoFenceInterface.MyLatLng;
import www.fiberathome.com.parkingapp.module.room.BookingSensorsRoom;
import www.fiberathome.com.parkingapp.module.room.DatabaseClient;
import www.fiberathome.com.parkingapp.ui.booking.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.ui.bottomSheet.BottomSheetAdapter;
import www.fiberathome.com.parkingapp.ui.bottomSheet.CustomLinearLayoutManager;
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
import static www.fiberathome.com.parkingapp.model.searchHistory.AppConstants.FIRST_TIME_INSTALLED;
import static www.fiberathome.com.parkingapp.model.searchHistory.AppConstants.HISTORY_PLACE_SELECTED;
import static www.fiberathome.com.parkingapp.model.searchHistory.AppConstants.NEW_PLACE_SELECTED;
import static www.fiberathome.com.parkingapp.model.searchHistory.AppConstants.NEW_SEARCH_ACTIVITY_REQUEST_CODE;
import static www.fiberathome.com.parkingapp.utils.GoogleMapHelper.defaultMapSettings;
import static www.fiberathome.com.parkingapp.utils.GoogleMapHelper.getDefaultPolyLines;

@SuppressLint("NonConstantResourceId")
public class HomeFragment extends BaseFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener,
        IOnLoadLocationListener, GeoQueryEventListener, BottomSheetAdapter.AdapterCallback,
        IOnBackPressListener, DirectionFinderListener {

    private final String TAG = getClass().getSimpleName();

    public static final int GPS_REQUEST_CODE = 9003;
    private static final int PLAY_SERVICES_ERROR_CODE = 9002;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 100;

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
    private Button departureBtn;
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
    private GoogleApiClient googleApiClient;
    private String sensorStatus = "Occupied";

    //flags
    private int getDirectionButtonClicked = 0;
    private int getDirectionSearchButtonClicked = 0;
    private int getDirectionMarkerButtonClicked = 0;
    private int getDirectionBottomSheetButtonClicked = 0;
    private ProgressDialog progressDialog;
    private ProgressDialog bottomSheetProgressDialog;
    public int fromMarkerRouteDrawn = 0;

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
    private String searchPlaceCount = "0";

    //geoFence
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentUser;
    private DatabaseReference myLocationRef;
    private GeoFire geoFire;
    private List<LatLng> dangerousArea = new ArrayList<>();
    private IOnLoadLocationListener locationListener;
    private DatabaseReference myCity;
    private Location lastLocation;
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
    private JSONArray clickEventJsonArray;
    private JSONArray searchPlaceEventJsonArray;
    private JSONArray bottomSheetPlaceEventJsonArray;
    private JSONArray adapterPlaceEventJsonArray;
    private MyLatLng myLatLng;
    private String markerUid;
    private final ArrayList<BookingSensors> bookingSensorsArrayListGlobal = new ArrayList<>();

    private double adjustValue = 2;
    private LatLng origin;
    private int countadd = 0;
    private BookingSensorsRoom bookingSensorsRoom;
    private double adapterDistance;
    private final ArrayList<BookingSensors> bookingSensorsAdapterArrayList = new ArrayList<>();
    private String adapterUid;
    private String bottomUid;
    private final ArrayList<BookingSensors> bookingSensorsArrayListGlobalRoom = new ArrayList<>();
    private Marker markerClicked;
    private boolean isNotificationSent = false;

    public Polyline polyline;
    public List<LatLng> points = new ArrayList<>();

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.e("onCreate called");

        setHasOptionsMenu(false);

        super.onCreate(savedInstanceState);

        if (context != null) {
            FirebaseApp.initializeApp(context);
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

        unbinder = ButterKnife.bind(this, view);

        context = (HomeActivity) getActivity();

        if (context != null) {
            context.changeDefaultActionBarDrawerToogleIcon();
        }

        if (isAdded()) {

            initUI(view);

            setListeners();

            bottomSheet = view.findViewById(R.id.layout_bottom_sheet);
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));
            bottomSheetBehavior.setHideable(false);
            bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View view, int i) {
                    switch (i) {
                        case BottomSheetBehavior.STATE_HIDDEN:
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:
                            //btn.setText("Close Sheet");
                        case BottomSheetBehavior.STATE_COLLAPSED:
                            bottomSheetRecyclerView.smoothScrollToPosition(0);
                            //btn.setText("Expand Sheet");
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

            listener = (FragmentChangeListener) context;

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
                    Toast.makeText(requireActivity(), "Unable to load map", Toast.LENGTH_SHORT).show();
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

        defaultMapSettings(context, mMap, fusedLocationProviderClient, locationRequest, locationCallback);

        buildGoogleApiClient();

        //mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    private final View.OnClickListener clickListener = view -> {
        if (view.getId() == R.id.currentLocationImageButton && mMap != null && onConnectedLocation != null)
            animateCamera(onConnectedLocation);
        currentLocationButton = 0;
    };

    private final boolean isCameraChange = false;
    private int currentLocationButton = 1;

    private void showCurrentLocationButton() {
        if (currentLocationButton == 1) {
            currentLocationImageButton.setVisibility(View.VISIBLE);
            currentLocationButton--;
        } else if (currentLocationButton == 0) {
            currentLocationImageButton.setVisibility(View.GONE);
            currentLocationButton++;
        }
    }

    boolean isMyCurrentLocation = false;

    private Circle circle;

    private Sensor markerTagObj;

    private MarkerOptions markerOptionsForMarkerClick;

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

        if (currentLocationMarker != null && ApplicationUtils.calculateDistance(currentLocationMarker.getPosition().latitude, currentLocationMarker.getPosition().longitude,
                marker.getPosition().latitude, marker.getPosition().longitude) <= 0.001) {
            double distance = ApplicationUtils.calculateDistance(currentLocationMarker.getPosition().latitude, currentLocationMarker.getPosition().longitude,
                    marker.getPosition().latitude, marker.getPosition().longitude);

            marker.setTitle("My Location");

            isMyCurrentLocation = true;
        } else {
            isMyCurrentLocation = false;
        }

        if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {

            bookingSensorsMarkerArrayList.clear();
            bookingSensorsAdapterArrayList.clear();

            if (isRouteDrawn == 0) {
                if (mMap != null) {

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

                    if (marker.getPosition() != null) {
                        markerOptions = new MarkerOptions();
                        markerOptions.position(marker.getPosition()).title(markerUid);
                        coordList.add(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                    }

                    if (previousDestinationMarker != null && markerPlaceLatLng != null) {
                        previousDestinationMarker.remove();
                        previousDestinationMarker = null;
                    } else {
                        previousDestinationMarker = mMap.addMarker(markerOptions);
                    }

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

                        TaskParser taskParser = new TaskParser();

                        double markerDistance = 0;

                        markerDistance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                                new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));

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

                    if (marker.getPosition() != null) {
                        markerOptionsForMarkerClick = new MarkerOptions();
                        markerOptionsForMarkerClick.position(marker.getPosition()).title(markerUid);
                        coordList.add(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
                        markerOptionsForMarkerClick.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                    }

                    if (previousGetDestinationMarker != null) {
                        previousGetDestinationMarker.remove();
                        previousGetDestinationMarker = null;
                    } else if (markerOptionsForMarkerClick != null) {
                        previousGetDestinationMarker = mMap.addMarker(markerOptionsForMarkerClick);
                    } else {
                        Toast.makeText(context, "All null", Toast.LENGTH_SHORT).show();
                    }

                    if (previousDestinationMarker != null) {
                        previousDestinationMarker.remove();
                        previousDestinationMarker = null;
                    } else {
                        previousDestinationMarker = mMap.addMarker(markerOptionsForMarkerClick);
                    }

                    if (SharedData.getInstance().getPreviousAdapterSetMarkerEvent() != null && previousAdapterSetMarkerEvent != null) {
                        previousAdapterSetMarkerEvent.remove();
                        previousAdapterSetMarkerEvent = null;
                        SharedData.getInstance().setPreviousAdapterSetMarkerEvent(null);
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

                    TaskParser taskParser = new TaskParser();

                    double markerDistance = 0;

                    markerDistance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                            new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        onConnectedLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (onConnectedLocation != null) {
            Timber.e("onConnected not null called");

            LatLng latLng = new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude());
            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
            animateCamera(onConnectedLocation);

            bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));

            if (ApplicationUtils.checkInternet(context)) {
                //fetchSensors(onConnectedLocation);
                fetchSensorRetrofit(onConnectedLocation);
                //fetchBottomSheetSensors(onConnectedLocation);
            } else {
                /*ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                    Timber.e("Positive Button clicked");
                    if (ApplicationUtils.checkInternet(context)) {
                        fetchSensors(onConnectedLocation);
                        fetchBottomSheetSensors(onConnectedLocation);
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
                });*/
            }

        } else {
            Timber.e("onConnected null called");

            previousMarker = null;

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);

            onConnectedLocation = new Location(LocationManager.NETWORK_PROVIDER);

            if (SharedData.getInstance().getLastLocation() != null) {
                Timber.e("getLastLocation not null called");
                onConnectedLocation.setLatitude(SharedData.getInstance().getLastLocation().getLatitude());
                onConnectedLocation.setLongitude(SharedData.getInstance().getLastLocation().getLongitude());
            } else {
                Timber.e("getLastLocation null called");
                onConnectedLocation.setLatitude(23.774525);
                onConnectedLocation.setLongitude(90.415730);
            }

            Timber.e("onConnectedLocation null else-> %s", onConnectedLocation);

            LatLng latLng = new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            animateCamera(onConnectedLocation);

            bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));

            try {
                linearLayoutParkingAdapterBackBottom.setVisibility(View.INVISIBLE);
            } catch (Exception e) {
                e.getCause();
            }

            if (ApplicationUtils.checkInternet(context)) {
                fetchSensorRetrofit(onConnectedLocation);
                //fetchBottomSheetSensors(onConnectedLocation);
            } else {
                ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                    Timber.e("Positive Button clicked");
                    if (ApplicationUtils.checkInternet(context)) {
                        fetchSensorRetrofit(onConnectedLocation);
                        //fetchBottomSheetSensors(onConnectedLocation);
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

        SharedData.getInstance().setOnConnectedLocation(onConnectedLocation);

        buildLocationRequest();

        buildLocationCallBack();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        initArea();

        settingGeoFire();

        //value getting from parking adapter
        if (SharedData.getInstance().getSensorArea() != null) {
            SensorArea sensorArea = SharedData.getInstance().getSensorArea();

            textViewParkingAreaName.setText(ApplicationUtils.capitalizeFirstLetter(sensorArea.getParkingArea()));
            textViewParkingAreaCount.setText(sensorArea.getCount());
            String distance = new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(sensorArea.getDistance()) + " km";

            textViewParkingDistance.setText(context.getResources().getString(R.string.distance, distance));

            getDestinationInfoForDuration(new LatLng(sensorArea.getLat(), sensorArea.getLng()));
        } else {
            Timber.e("Genjam");
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
        //Timber.e("onLocationChanged: ");
        currentLocation = location;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }

        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_car_running))
                .title("My Location")
                .rotation(location.getBearing()).flat(true).anchor(0.5f, 0.5f));

        if (markerClicked != null) {
            checkParkingSpotDistance(latLng, markerClicked.getPosition());
        } else if (adapterPlaceLatLng != null) {
            checkParkingSpotDistance(latLng, adapterPlaceLatLng);
        } else if (bottomSheetPlaceLatLng != null) {
            checkParkingSpotDistance(latLng, bottomSheetPlaceLatLng);
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
    }

    private void checkParkingSpotDistance(LatLng car, LatLng spot) {
        double distanceBetween = ApplicationUtils.calculateDistance(car.latitude, car.longitude, spot.latitude, spot.longitude);

        if (markerPlaceLatLng != null) {
            setCircleOnLocation(markerPlaceLatLng);
        } else if (bottomSheetPlaceLatLng != null) {
            setCircleOnLocation(bottomSheetPlaceLatLng);
        } else if (adapterPlaceLatLng != null) {
            setCircleOnLocation(adapterPlaceLatLng);
        }

        float[] distance = new float[2];

        Location.distanceBetween(car.latitude, car.longitude, circle.getCenter().latitude, circle.getCenter().longitude, distance);

        if (distance[0] <= circle.getRadius()) {
            // Inside The Circle
            isInAreaEnabled = true;
            isNotificationSent = true;
            sendNotification("You Entered parking spot", "You can book parking slot");
            //Toast.makeText(context, "inside circle", Toast.LENGTH_SHORT).show();
        } else {
            // Outside The Circle
            isInAreaEnabled = false;
            isNotificationSent = false;
            //Toast.makeText(context, "outside circle", Toast.LENGTH_SHORT).show();
        }
    }

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
    }

    @Override
    public void onPause() {
        Timber.e("onPause called");
        super.onPause();
        hideLoading();
    }

    @Override
    public void onStop() {
        Timber.e("onStop called");
        EventBus.getDefault().unregister(this);
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
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
        //Timber.e("onActivityResult HomeFragment called");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppConstants.GPS_REQUEST && resultCode == RESULT_OK) {
            isGPS = true; // flag maintain before get location
        }

        if (requestCode == NEW_SEARCH_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            if (bookingSensorsArrayList != null) {
                bookingSensorsArrayList.clear();
            }

            //new search result
            SelectedPlace selectedPlace = (SelectedPlace) data.getSerializableExtra(NEW_PLACE_SELECTED); //This line may produce null point exception
            if (selectedPlace != null) {
                previousMarker = null;
                hideNoData();

                //Toast.makeText(context, "1639  previous null", Toast.LENGTH_SHORT).show();
                searchPlaceLatLng = new LatLng(selectedPlace.getLatitude(), selectedPlace.getLongitude());

                String areaName = selectedPlace.getAreaName();
                String areaAddress = selectedPlace.getAreaAddress();
                String placeId = selectedPlace.getPlaceId();

                storeVisitedPlace(SharedPreManager.getInstance(context).getUser().getMobileNo(), placeId,
                        selectedPlace.getLatitude(), selectedPlace.getLongitude(),
                        onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(), areaName);

                buttonSearch.setVisibility(View.GONE);
                linearLayoutSearchBottom.setVisibility(View.VISIBLE);
                linearLayoutSearchBottomButton.setVisibility(View.VISIBLE);
                btnSearchGetDirection.setVisibility(View.VISIBLE);
                btnSearchGetDirection.setEnabled(true);
                imageViewSearchBack.setVisibility(View.VISIBLE);
                //Timber.e("selectedPlace searchPlaceLatLng -> %s", searchPlaceLatLng);

                if (mMap != null)
                    mMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(searchPlaceLatLng);
                coordList.add(new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                mMap.addMarker(markerOptions);
                //move map camera
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 13.5f), 500, null);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 13.5f));

                /*if (ApplicationUtils.checkInternet(context)) {
                    fetchSensorRetrofit(onConnectedLocation);
                } else {
                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                }*/

                if (onConnectedLocation != null && searchPlaceLatLng != null) {
                    TaskParser taskParser = new TaskParser();
                    searchDistance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                            new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));

                    Timber.e("searchDistance -> %s", searchDistance);

                    layoutSearchVisible(true, areaName, "0", textViewSearchParkingDistance.getText().toString(), searchPlaceLatLng);

                    bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._130sdp));

                    if (searchDistance < 3000) {
                        adjustValue = 1;
                    }

                    double kim = (searchDistance / 1000) + adjustValue;

                    double searchDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(searchDistance * 2.43));

                    String searchStringDuration = String.valueOf(searchDoubleDuration);

                    bookingSensorsArrayList.add(new BookingSensors(areaName, searchPlaceLatLng.latitude, searchPlaceLatLng.longitude,
                            searchDistance, "0", searchStringDuration,
                            context.getResources().getString(R.string.nearest_parking_from_your_destination),
                            BookingSensors.TEXT_INFO_TYPE, 0));

                    if (sensorArrayList != null) {
                        for (int i = 0; i < sensorArrayList.size(); i++) {
                            // JSONObject jsonObject;
                            //jsonObject = searchPlaceEventJsonArray.getJSONObject(i);
                            Sensor sensor = sensorArrayList.get(i);
                            String latitude1 = sensor.getLatitude();
                            String longitude1 = sensor.getLongitude();
                            String nearestSearchAreaName = sensor.getParkingArea();

                            double distanceForNearbyLoc = calculateDistance(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude,
                                    ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));
                            //Timber.e("DistanceForNearbyLoc -> %s", distanceForNearbyLoc);

                            if (distanceForNearbyLoc < 5) {
                                origin = new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude);

                                String parkingNumberOfNearbyDistanceLoc = sensor.getNoOfParking();

                                int adjustNearbyValue = 2;

                                double km = (distanceForNearbyLoc / 1000) + adjustNearbyValue;

                                if (distanceForNearbyLoc > 1.9) {
                                    distanceForNearbyLoc = distanceForNearbyLoc + 2;
                                } else if (distanceForNearbyLoc > 1.9 && distanceForNearbyLoc < 1) {
                                    distanceForNearbyLoc = distanceForNearbyLoc + 1;
                                } else {
                                    distanceForNearbyLoc = distanceForNearbyLoc + 0.5;
                                }

                                double nearbySearchDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(distanceForNearbyLoc * 2.43));

                                String nearbySearchStringDuration = String.valueOf(nearbySearchDoubleDuration);

                                bookingSensorsArrayList.add(new BookingSensors(nearestSearchAreaName, ApplicationUtils.convertToDouble(latitude1),
                                        ApplicationUtils.convertToDouble(longitude1), distanceForNearbyLoc, parkingNumberOfNearbyDistanceLoc,
                                        nearbySearchStringDuration,
                                        BookingSensors.INFO_TYPE, 1));

                                bubbleSortArrayList(bookingSensorsArrayList);

                                if (bottomSheetAdapter != null) {
                                    bookingSensorsArrayListGlobal.clear();
                                    bottomSheetAdapter.setDataList(bookingSensorsArrayList);
                                    //bookingSensorsArrayListGlobal.addAll(bookingSensorsArrayList);
                                    //bottomSheetAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Something went wrong!!! Please check your Internet connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Location cannot be identified!!!", Toast.LENGTH_SHORT).show();
                }

                if (bottomSheetAdapter != null) {
                    bookingSensorsArrayListGlobal.clear();
                    bookingSensorsArrayListGlobal.addAll(bookingSensorsArrayList);
                    bottomSheetAdapter.notifyDataSetChanged();
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

                if (mMap != null)
                    mMap.clear();

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(searchPlaceLatLng);
                coordList.add(new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                mMap.addMarker(markerOptions);
                //move map camera
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 13.5f), 500, null);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 13.5f));

                /*if (ApplicationUtils.checkInternet(context)) {
                    fetchSensorRetrofit(onConnectedLocation);
                } else {
                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));

                }*/

                String searchPlaceName = areaName;

                if (onConnectedLocation != null && searchPlaceLatLng != null) {
                    TaskParser taskParser = new TaskParser();
                    searchDistance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                            new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));

                    Timber.e("searchDistance -> %s", searchDistance);

                    layoutSearchVisible(true, searchPlaceName, "0", textViewSearchParkingDistance.getText().toString(), searchPlaceLatLng);

                    bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._130sdp));

                    double kim = (searchDistance / 1000) + adjustValue;

                    double searchDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(searchDistance * 2.43));

                    String searchStringDuration = String.valueOf(searchDoubleDuration);

                    bookingSensorsArrayList.add(new BookingSensors(searchPlaceName, searchPlaceLatLng.latitude, searchPlaceLatLng.longitude,
                            searchDistance, "0", searchStringDuration,
                            context.getResources().getString(R.string.nearest_parking_from_your_destination),
                            BookingSensors.TEXT_INFO_TYPE, 0));

                    if (sensorArrayList != null) {
                        for (int i = 0; i < sensorArrayList.size(); i++) {
                            //JSONObject jsonObject;
                            //jsonObject = searchPlaceEventJsonArray.getJSONObject(i);
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

                                int adjustNearbyValue = 2;

                                double km = (distanceForNearbyLoc / 1000) + adjustNearbyValue;

                                if (distanceForNearbyLoc > 1.9) {
                                    distanceForNearbyLoc = distanceForNearbyLoc + 2;
                                } else if (distanceForNearbyLoc > 1.9 && distanceForNearbyLoc > 1) {
                                    distanceForNearbyLoc = distanceForNearbyLoc + 1;
                                } else {
                                    distanceForNearbyLoc = distanceForNearbyLoc + 0.5;
                                }

                                double nearbySearchDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(distanceForNearbyLoc * 2.43));

                                String nearbySearchStringDuration = String.valueOf(nearbySearchDoubleDuration);

                                bookingSensorsArrayList.add(new BookingSensors(nearbyAreaName, ApplicationUtils.convertToDouble(latitude1),
                                        ApplicationUtils.convertToDouble(longitude1), distanceForNearbyLoc, parkingNumberOfNearbyDistanceLoc,
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
        } else {
            if (SharedData.getInstance().getOnConnectedLocation() != null) {
                //  fetchSensors(SharedData.getInstance().getOnConnectedLocation());
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

                    progressDialog = ApplicationUtils.progressDialog(context, "Initializing....");

                    new GpsUtils(context).turnGPSOn(new GpsUtils.onGpsListener() {
                        @Override
                        public void gpsStatus(boolean isGPSEnable) {
                            // turn on GPS
                            isGPS = isGPSEnable;
                        }
                    });

                    if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
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
        Timber.d("onRequestPermissionsResult: on requestPermission");
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            Timber.d("onRequestPermissionsResult: First time evoked");

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Timber.d("onRequestPermissionResult: on requestPermission if-if");
                if (progressDialog != null) {
                    progressDialog.show();
                }
                if (isLocationEnabled(context)) {
                    supportMapFragment.getMapAsync(this);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
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

    private void initUI(View view) {
        view.findViewById(R.id.currentLocationImageButton).setOnClickListener(clickListener);
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
        departureBtn = view.findViewById(R.id.departureBtn);
    }

    public void animateCamera(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (mMap != null)
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)));
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
            JSONObject jsonObject;
            Sensor sensor = sensorArrayList.get(i);
            try {
                //jsonObject = jsonArray.getJSONObject(i);
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
                    int adjustNearbyValue = 2;

                    double kim = (distanceForNearbyLoc / 1000) + adjustNearbyValue;

                    if (distanceForNearbyLoc > 1.9) {
                        distanceForNearbyLoc = distanceForNearbyLoc + 2;
                    } else if (distanceForNearbyLoc > 1.9 && distanceForNearbyLoc < 1) {
                        distanceForNearbyLoc = distanceForNearbyLoc + 1;
                    } else {
                        distanceForNearbyLoc = distanceForNearbyLoc + 0.5;
                    }

                    double nearbySearchDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(distanceForNearbyLoc * 2.43));

                    String nearbySearchStringDuration = String.valueOf(nearbySearchDoubleDuration);

                    bookingSensorsArrayList.add(new BookingSensors(nearbyAreaName[0], ApplicationUtils.convertToDouble(latitude1),
                            ApplicationUtils.convertToDouble(longitude1), distanceForNearbyLoc, parkingNumberOfNearbyDistanceLoc,
                            nearbySearchStringDuration,
                            BookingSensors.INFO_TYPE, 1));

                    bubbleSortArrayList(bookingSensorsArrayList);

                    setBottomSheetCallBack.setBottomSheet();
                    bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._130sdp));
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
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        else if (bottomSheetProgressDialog != null && bottomSheetProgressDialog.isShowing())
            bottomSheetProgressDialog.dismiss();
    }

    private final ArrayList<MarkerOptions> mMarkerArrayList = new ArrayList<>();
    private List<Sensor> sensorArrayList = new ArrayList<>();

    public void fetchSensorRetrofit(Location location) {

        this.onConnectedLocation = location;

        bookingSensorsArrayListGlobal.clear();

        // UI Service.
        ApiService service = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<SensorsResponse> sensorsCall = service.getSensors();

        showLoading(context);

        startShimmer();

        // Gathering results.
        sensorsCall.enqueue(new Callback<SensorsResponse>() {
            @Override
            public void onResponse(@NonNull Call<SensorsResponse> call, @NonNull retrofit2.Response<SensorsResponse> response) {
                Timber.e("response -> %s", response.message());

                if (response.body() != null && !response.body().getError()) {
                    if (response.isSuccessful()) {

                        hideLoading();

                        stopShimmer();

                        sensorArrayList = response.body().getSensors();

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

                            TaskParser taskParser = new TaskParser();
                            double fetchDistance = taskParser.showDistance(new LatLng(location.getLatitude(), location.getLongitude()),
                                    new LatLng(latitude, longitude));
                            Timber.e("kim fetchDistance -> %s", fetchDistance);

                            double kim = (fetchDistance / 1000) + adjustValue;
                            Timber.e("kim fetchBottomSheetSensors -> %s", kim);

                            if (fetchDistance > 1.9) {
                                fetchDistance = fetchDistance + 2;
                                Timber.e("kim 1st if -> %s", kim);
                            } else if (fetchDistance < 1.9 && fetchDistance > 1) {
                                fetchDistance = fetchDistance + 1;
                                Timber.e("kim 2nd if-> %s", kim);
                            } else {
                                fetchDistance = fetchDistance + 0.5;
                                Timber.e("kim else-> %s", kim);
                            }

                            double doubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(fetchDistance * 2.43));
                            Timber.e("kim doubleDuration -> %s", doubleDuration);

                            String initialNearestDuration = String.valueOf(doubleDuration);
                            Timber.e("kim initialNearestDuration -> %s", initialNearestDuration);

                            if (fetchDistance < 5) {
                                origin = new LatLng(location.getLatitude(), location.getLongitude());

                                String nearestCurrentAreaName = areaName;
                                Timber.e("nearestCurrentAreaName without progressBar-> %s", nearestCurrentAreaName);
                                bookingSensorsArrayListGlobal.add(new BookingSensors(nearestCurrentAreaName, latitude, longitude,
                                        fetchDistance, parkingCount, initialNearestDuration,
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
            }
        });
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

    private String getAddress(Context context, double LATITUDE, double LONGITUDE) {
        //Set Address
        String addressTemp;
        List<Address> addresses;
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            Timber.e("addressesAbdur--->%s", addresses.toString());

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

                Timber.e("addressesAbdur2--->%s", addresses.toString());
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
            Timber.e("addressesAbdur--->%s", addresses.toString());

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

                Timber.e("addressesAbdur--->%s", addresses.toString());
            } else {
                countadd++;

                Timber.e("addressesAbdurRock2--->%s", addresses.toString());
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

    @SuppressLint("SetTextI18n")
    private void setBottomSheetFragmentControls(ArrayList<BookingSensors> sensors) {
        bottomSheetRecyclerView.setHasFixedSize(true);
        bottomSheetRecyclerView.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager mLayoutManager = new CustomLinearLayoutManager(getActivity());
        bottomSheetRecyclerView.setLayoutManager(mLayoutManager);
        bottomSheetRecyclerView.addItemDecoration(new DividerItemDecoration(context, CustomLinearLayoutManager.VERTICAL));
        bottomSheetRecyclerView.setItemAnimator(new DefaultItemAnimator());
        bottomSheetRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), bottomSheetRecyclerView, new RecyclerTouchListener.ClickListener() {
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

        bottomSheetAdapter = new BottomSheetAdapter(context, this, bookingSensors, onConnectedLocation, this, bookingSensors1 -> {

            bookingSensorsBottomSheetArrayList.clear();

            bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._130sdp));

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            if (previousMarker != null)
                previousMarker.remove();

            if (previousDestinationMarker != null)
                previousDestinationMarker.remove();

            if (previousGetDestinationMarker != null)
                previousGetDestinationMarker.remove();

            if (previousBottomSheetGetDestinationMarker != null)
                previousBottomSheetGetDestinationMarker.remove();

            if (newBottomSheetMarker != null) {
                newBottomSheetMarker.remove();
                newBottomSheetMarker = null;
            }

            bottomSheetPlaceLatLng = new LatLng(bookingSensors1.getLat(), bookingSensors1.getLng());

            if (isRouteDrawn == 0) {
                //for getting the location name

                if (bottomSheetAdapter != null) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(bottomSheetPlaceLatLng);

                    coordList.add(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                    previousDestinationMarker = mMap.addMarker(markerOptions);

                    for (int i = 0; i < sensorArrayList.size(); i++) {
                        Sensor sensor = sensorArrayList.get(i);
                        try {
                            String latitude1 = sensor.getLatitude();
                            String longitude1 = sensor.getLongitude();
                            uid = sensor.getUid();
                            /*if (isSearchAreaVisible) {
                                locationName = name;
                            } else {*/
                            locationName = sensor.getParkingArea();
                            //}
                            double distanceForCount = calculateDistance(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude, ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));

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

                TaskParser taskParser = new TaskParser();
                double bottomSheetDistance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                        new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));

                Timber.e(" searchDistance bottomSheetDistance -> %s", bottomSheetDistance);
                Timber.e(" bottomSheetDistance -> %s", searchDistance);

                double kim = (bottomSheetDistance / 1000) + adjustValue;

                if (kim > 1.9) {
                    kim = kim + 2;
                } else if (kim == 1.5) {
                    kim = kim + 1;
                } else {
                    kim = kim + 0.5;
                }

                double bottomSheetDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(bottomSheetDistance * 2.43));

                String bottomSheetStringDuration = String.valueOf(bottomSheetDoubleDuration);

                layoutBottomSheetVisible(true, bookingSensors1.getParkingArea(), bookingSensors1.getCount(),
                        String.valueOf(bottomSheetDistance), bottomSheetStringDuration,
                        new LatLng(bookingSensors1.getLat(), bookingSensors1.getLng()), false);

                bookingSensorsBottomSheetArrayList.add(new BookingSensors(bottomSheetPlaceName, bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude,
                        bottomSheetDistance, textViewBottomSheetParkingAreaCount.getText().toString(), bottomSheetStringDuration,
                        context.getResources().getString(R.string.nearest_parking_from_your_destination),
                        BookingSensors.TEXT_INFO_TYPE, 0));

                if (sensorArrayList != null) {
                    setBottomSheetList(() -> {
                        if (bottomSheetAdapter != null) {
                            Timber.e("setBottomSheet if called");
                            bookingSensorsArrayListGlobal.clear();
                            bookingSensorsArrayListGlobal.addAll(bookingSensorsBottomSheetArrayList);
                            bottomSheetAdapter.notifyDataSetChanged();
                            bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));
                        } else {
                            Timber.e("setBottomSheet if else called");
                        }
                    }, sensorArrayList, bottomSheetPlaceLatLng, bookingSensorsBottomSheetArrayList, finalUid);
                } else {
                    Toast.makeText(context, "Something went wrong!!! Please check your Internet connection", Toast.LENGTH_SHORT).show();
                }
            } else {
                ApplicationUtils.showAlertDialog(context.getString(R.string.you_have_to_exit_from_current_destination), context, context.getString(R.string.yes), context.getString(R.string.no), (dialog, which) -> {

                    if (polyline == null || !polyline.isVisible())
                        return;

                    points = polyline.getPoints();

                    if (polyline != null) {
                        polyline.remove();
                    }

                    if (previousMarker != null) {
                        previousMarker.remove();
                        previousMarker = null;
                    }

                    if (previousGetDestinationMarker != null) {
                        previousGetDestinationMarker.remove();
                        //previousGetDestinationMarker = null;
                    }

                    if (previousDestinationMarker != null) {
                        previousDestinationMarker.remove();
                        previousDestinationMarker = null;
                    }

                    if (newBottomSheetMarker != null) {
                        newBottomSheetMarker.remove();
                        newBottomSheetMarker = null;
                    }

                    if (bottomSheetPlaceLatLng != null) {
                        markerOptions = new MarkerOptions();
                        markerOptions.position(bottomSheetPlaceLatLng).title(markerUid);
                        coordList.add(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                        previousDestinationMarker = mMap.addMarker(markerOptions);
                    }

                    btnBottomSheetGetDirection.setVisibility(View.VISIBLE);

                    String origin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();

                    String destination = null;

                    if (bottomSheetPlaceLatLng != null) {
                        destination = "" + bottomSheetPlaceLatLng.latitude + ", " + bottomSheetPlaceLatLng.longitude;
                    }

                    fetchDirections(origin, destination);

                    //fromMarkerRouteDrawn = 1;

                    if (bookingSensorsBottomSheetArrayList != null)
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

                    TaskParser taskParser = new TaskParser();

                    double bottomSheetDistance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                            new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));

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
                                bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._130sdp));
                            } else {

                            }
                        }, sensorArrayList, bottomSheetPlaceLatLng, bookingSensorsBottomSheetArrayList, bottomUid);
                    }

                    bottomSheetAdapter.setDataList(bookingSensorsBottomSheetArrayList);

                }, (dialog, which) -> {
                    Timber.e("Negative Button Clicked");
                    dialog.dismiss();
                });
            }
        });
        bottomSheetRecyclerView.setAdapter(bottomSheetAdapter);
        if (bookingSensors.size() == 0) {
            setNoData();
        } else {
            hideNoData();
        }
        bottomSheetAdapter.notifyDataSetChanged();
    }

    private void storeVisitedPlace(String mobileNo, String placeId, double endLatitude, double endLongitude,
                                   double startLatitude, double startLongitude, String areaAddress) {
        HttpsTrustManager.allowAllSSL();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SAVE_SEARCH_HISTORY_POST, response -> {

            try {
                //converting response to json object
                JSONObject jsonObject = new JSONObject(response);
                Timber.e("jsonObject -> %s", jsonObject.toString());

                // if no error response
                if (!jsonObject.getBoolean("error")) {
                    Timber.e("storeVisitedPlace jsonObject if called");
                    Timber.e("storeVisitedPlace error message if block-> %s", jsonObject.getString("message"));

                } else {
                    Timber.e("storeVisitedPlace jsonObject else called");
                    Timber.e("storeVisitedPlace error message else block-> %s", jsonObject.getString("message"));
                }
            } catch (JSONException e) {
                Timber.e("storeVisitedPlace jsonObject catch -> %s", e.getMessage());
                e.printStackTrace();
            }

        }, error -> {
            Timber.e("storeVisitedPlace jsonObject onErrorResponse -> %s", error.getMessage());
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mobile_number", mobileNo);
                params.put("place_id", placeId);
                params.put("end_let", String.valueOf(endLatitude));
                params.put("end_long", String.valueOf(endLongitude));
                params.put("start_let", String.valueOf(startLatitude));
                params.put("start_long", String.valueOf(startLongitude));
                params.put("address", areaAddress);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ParkingApp.getInstance().addToRequestQueue(stringRequest, TAG);
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
        //Timber.e("condition getDestinationInfoForDuration");
        if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
            if (onConnectedLocation != null) {
                origin = new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude());
            } else {
                origin = new LatLng(SharedData.getInstance().getOnConnectedLocation().getLatitude(), SharedData.getInstance().getOnConnectedLocation().getLongitude());
            }

            LatLng destination = latLngDestination;

            //-------------Using AK Exorcist Google Direction Library---------------\\
            GoogleDirection.withServerKey(serverKey)
                    .from(origin)
                    .to(destination)
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
                                //Timber.e("duration message -> %s", message);
                            } else if (status.equals(RequestResult.NOT_FOUND)) {
                                Toast.makeText(context, "No routes exist", Toast.LENGTH_SHORT).show();
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

        layoutVisible(true, ApplicationUtils.capitalizeFirstLetter(name), count, distance, event.location);

        markerOptions = new MarkerOptions();

        markerOptions.position(event.location);

        coordList.add(new LatLng(event.location.latitude, event.location.longitude));

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin)).title(adapterUid);

        if (mMap != null) {
            previousAdapterSetMarkerEvent = mMap.addMarker(markerOptions);
            //move map camera
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(event.location, 16f));
            SharedData.getInstance().setPreviousAdapterSetMarkerEvent(previousAdapterSetMarkerEvent);
        }

        if (ApplicationUtils.checkInternet(context) && isGPSEnabled()) {
            String uid = "";
            String adapterAreaName = "";

            for (int i = 0; i < sensorArrayList.size(); i++) {
                Sensor sensor = sensorArrayList.get(i);
                try {
                    String latitude1 = sensor.getLatitude();

                    String longitude1 = sensor.getLongitude();

                    uid = sensor.getUid();

                    adapterAreaName = sensor.getParkingArea();

                    double distanceForCount = calculateDistance(event.location.latitude, event.location.longitude,
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

            if (SharedData.getInstance().getOnConnectedLocation() != null) {

                Timber.e("updateBottomSheetForParkingAdapter if called");

                if (bookingSensorsAdapterArrayList != null) {
                    bookingSensorsAdapterArrayList.clear();
                }

                bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));

                if (geoQuery != null) {
                    geoQuery.removeGeoQueryEventListener(this);
                    geoQuery.removeAllListeners();
                }

                geoQuery = geoFire.queryAtLocation(new GeoLocation(event.location.latitude, event.location.longitude), 0.1f); // 500m

                geoQuery.addGeoQueryEventListener(this);

                TaskParser taskParser = new TaskParser();

                adapterDistance = taskParser.showDistance(new LatLng(SharedData.getInstance().getOnConnectedLocation().getLatitude(), SharedData.getInstance().getOnConnectedLocation().getLongitude()),
                        new LatLng(event.location.latitude, event.location.longitude));

                String finalUid = uid;

                String adapterPlaceName = adapterAreaName;

                if (adapterDistance < 3000) {
                    adjustValue = 1;
                }

                double kim = (adapterDistance / 1000) + adjustValue;
                double adapterDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(adapterDistance * 2.43));
                String adapterStringDuration = String.valueOf(adapterDoubleDuration);

                bookingSensorsAdapterArrayList.add(new BookingSensors(adapterPlaceName, event.location.latitude, event.location.longitude,
                        adapterDistance, textViewParkingAreaCount.getText().toString(), adapterStringDuration,
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
                    }, sensorArrayList, event.location, bookingSensorsAdapterArrayList, finalUid);
                }
            } else {
                Timber.e("updateBottomSheetForParkingAdapter else called");
                Toast.makeText(context, "Something went wrong!!! Location cannot be identified!!!", Toast.LENGTH_SHORT).show();
            }

        } else {
            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCustomDirectionEvent(GetDirectionAfterButtonClickEvent event) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapterPlaceLatLng = event.location;

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

    private Marker newBottomSheetMarker;

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

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(bottomSheetPlaceLatLng);
                coordList.add(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                newBottomSheetMarker = mMap.addMarker(markerOptions);

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

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(searchPlaceLatLng);

                coordList.add(new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                mMap.addMarker(markerOptions);

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
    String YourUniqueKey = "Direction";
    HashMap<String, Marker> hashMapMarker = new HashMap<>();
    public boolean isDestinationMarkerDrawn = false;
    public MarkerOptions markerOptions;

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMarkerDirectionEvent(GetDirectionForMarkerEvent event) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                markerPlaceLatLng = event.location;

                if (markerPlaceLatLng != null && adapterPlaceLatLng == null) {
                    markerOptions = new MarkerOptions();
                    markerOptions.position(markerPlaceLatLng).title(markerUid);
                    coordList.add(new LatLng(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude));
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                }

                if (previousDestinationMarker != null) {
                    previousDestinationMarker.remove();
                    previousDestinationMarker = null;
                }

                previousDestinationMarker = mMap.addMarker(markerOptions);

                isDestinationMarkerDrawn = true;

                btnMarkerGetDirection.setVisibility(View.VISIBLE);

                String origin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();

                String destination = null;

                if (event.location != null) {
                    destination = "" + event.location.latitude + ", " + event.location.longitude;
                }

                fetchDirections(origin, destination);

                setMapZoomLevelDirection(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()), markerPlaceLatLng);

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
            padding = (int) (Math.min(width, height) * 0.35);
            //padding = ((height) / 500);

            LatLngBounds bounds = builder.build();

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
        }
    }

    @SuppressLint("SetTextI18n")
    private void layoutVisible(boolean isVisible, String name, String count,
                               String distance, LatLng location) {
        this.name = name;
        this.count = count;
        this.distance = distance;
        HomeFragment.adapterPlaceLatLng = location;

        if (isVisible) {
            linearLayoutMarkerBackNGetDirection.setVisibility(View.VISIBLE);
            linearLayoutNameCount.setVisibility(View.GONE);
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
        this.duration = duration;

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

    public String[] finalUid;

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

                if (previousMarker != null)
                    previousMarker.remove();

                coordList.add(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                previousMarker = mMap.addMarker(markerOptions);

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

                        double distanceForCount = calculateDistance(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude, ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));

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

        if (isClicked) {
            Timber.e("isClicked called");
            if (bottomSheetPlaceLatLng != null && bottomSheetAdapter != null) {

                bookingSensorsBottomSheetArrayList.clear();

                //for getting the location name
                String finalUid = uid;

                String bottomSheetPlaceName = locationName;

                TaskParser taskParser = new TaskParser();
                double bottomSheetDistance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                        new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));

                Timber.e(" searchDistance bottomSheetDistance -> %s", bottomSheetDistance);
                Timber.e(" bottomSheetDistance -> %s", searchDistance);

                double kim = (bottomSheetDistance / 1000) + adjustValue;

                if (kim > 1.9) {
                    kim = kim + 2;
                } else if (kim == 1.5) {
                    kim = kim + 1;
                } else {
                    kim = kim + 0.5;
                }

                double bottomSheetDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(bottomSheetDistance * 2.43));

                String bottomSheetStringDuration = String.valueOf(bottomSheetDoubleDuration);

                bookingSensorsBottomSheetArrayList.add(new BookingSensors(bottomSheetPlaceName, bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude,
                        bottomSheetDistance, textViewBottomSheetParkingAreaCount.getText().toString(), bottomSheetStringDuration,
                        context.getResources().getString(R.string.nearest_parking_from_your_destination),
                        BookingSensors.TEXT_INFO_TYPE, 0));

                if (bottomSheetPlaceEventJsonArray != null) {
                    setBottomSheetList(() -> {
                        if (bottomSheetAdapter != null) {
                            Timber.e("setBottomSheet if called");
                            bookingSensorsArrayListGlobal.clear();
                            bookingSensorsArrayListGlobal.addAll(bookingSensorsBottomSheetArrayList);
                            bottomSheetAdapter.notifyDataSetChanged();
                            bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));
                        } else {
                            Timber.e("setBottomSheet if else called");
                        }
                    }, sensorArrayList, bottomSheetPlaceLatLng, bookingSensorsBottomSheetArrayList, finalUid);
                } else {
                    Toast.makeText(context, "Something went wrong!!! Please check your Internet connection", Toast.LENGTH_SHORT).show();
                }
            }
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
                    bookingSensorsArrayList.clear();
                    bookingSensorsAdapterArrayList.clear();
                    bookingSensorsMarkerArrayList.clear();
                    bookingSensorsBottomSheetArrayList.clear();
                    bottomSheetAdapter.clear();
                    bottomSheetAdapter = null;
                }

                if (onConnectedLocation != null) {
                    fetchSensorRetrofit(onConnectedLocation);
                    animateCamera(onConnectedLocation);
                }

                ApplicationUtils.replaceFragmentWithAnimation(getParentFragmentManager(), this);

                linearLayoutBottom.setVisibility(View.GONE);
                linearLayoutSearchBottom.setVisibility(View.GONE);
                linearLayoutMarkerBottom.setVisibility(View.GONE);
                linearLayoutBottomSheetBottom.setVisibility(View.GONE);

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));

                buttonSearch.setText(null);
                buttonSearch.setVisibility(View.VISIBLE);

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
                    //fetchBottomSheetSensors(onConnectedLocation);
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

    public Marker previousGetDestinationMarker;
    public Marker previousBottomSheetGetDestinationMarker;
    public Marker previousAdapterDestinationMarker;
    public Marker previousAdapterGetDirectionMarker;
    public Marker previousAdapterSetMarkerEvent;
    public Marker previousAdapterGetDestinationMarkerNew;

    private void setListeners() {

        buttonSearch.setOnClickListener(v -> {
            if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
                Intent intent = new Intent(context, SearchActivity.class);

                startActivityForResult(intent, NEW_SEARCH_ACTIVITY_REQUEST_CODE);

                if (searchPlaceLatLng != null)
                    bookingSensorsArrayList.clear();

                if (mMap != null)
                    mMap.clear();

                bookingSensorsArrayListGlobal.clear();
                bookingSensorsArrayList.clear();
                bookingSensorsMarkerArrayList.clear();

                fetchSensorRetrofit(onConnectedLocation);

                /*fetchBottomSheetSensorsWithoutProgressBar(onConnectedLocation);
                fetchSensors(onConnectedLocation);*/

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
                        //fetchBottomSheetSensors(onConnectedLocation);
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

            /*btnMarkerGetDirection.setText(context.getResources().getString(R.string.get_direction));
            btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
            btnMarkerGetDirection.setEnabled(true);
            btnMarkerGetDirection.setFocusable(true);*/

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

                        buttonSearch.setVisibility(View.GONE);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(adapterPlaceLatLng);
                        coordList.add(new LatLng(adapterPlaceLatLng.latitude, adapterPlaceLatLng.longitude));

                        previousAdapterGetDirectionMarker = mMap.addMarker(markerOptions);

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

                isRouteDrawn = 1;

                if (getDirectionSearchButtonClicked == 0) {

                    getDirectionSearchButtonClicked++;

                    if (searchPlaceLatLng != null) {
                        EventBus.getDefault().post(new GetDirectionForSearchEvent(searchPlaceLatLng));

                        buttonSearch.setVisibility(View.GONE);

                        MarkerOptions markerDestinationPositionOptions = new MarkerOptions();
                        markerDestinationPositionOptions.position(searchPlaceLatLng);
                        coordList.add(new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));

                        mMap.addMarker(markerDestinationPositionOptions);

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
                    ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.no_parking_spot_message), context);
                    //getDirectionSearchButtonClicked--;
                    if (mMap != null) {
                        TaskParser taskParser = new TaskParser();

                        double distance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                                new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));

                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
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

                        linearLayoutMarkerNameCount.setVisibility(View.GONE);
                        buttonSearch.setVisibility(View.GONE);
                        bookingSensorsArrayListGlobal.clear();
                        bookingSensorsArrayList.clear();

                        if (markerPlaceLatLng != null) {
                            markerOptions = new MarkerOptions();
                            markerOptions.position(markerPlaceLatLng).title(markerUid);
                            coordList.add(new LatLng(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude));
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                        }

                        if (previousDestinationMarker != null) {
                            previousDestinationMarker.remove();
                            previousDestinationMarker = null;
                            previousDestinationMarker = mMap.addMarker(markerOptions);
                        }

                        isDestinationMarkerDrawn = true;

                        btnMarkerGetDirection.setVisibility(View.VISIBLE);

                        String origin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();

                        String destination = null;

                        if (markerPlaceLatLng != null) {
                            destination = "" + markerPlaceLatLng.latitude + ", " + markerPlaceLatLng.longitude;
                        }

                        fetchDirections(origin, destination);

                        fromMarkerRouteDrawn = 1;

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
                            //Timber.e("parkingNumberOfIndividualMarker not 0 else condition called");

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
                    if (bottomSheetPlaceLatLng != null || adapterPlaceLatLng != null || markerPlaceLatLng != null || searchPlaceLatLng != null) {
                        //Timber.e("all location called");

                        EventBus.getDefault().post(new GetDirectionBottomSheetEvent(bottomSheetPlaceLatLng));

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(bottomSheetPlaceLatLng);
                        coordList.add(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                        previousBottomSheetGetDestinationMarker = mMap.addMarker(markerOptions);

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

    @Override
    public void onMethodCallback(LatLng latLng) {
        Timber.e("onMethodCallback called");
        previousMarker = null;

        if (geoQuery != null) {
            geoQuery.removeGeoQueryEventListener(this);
            geoQuery.removeAllListeners();
        }
        geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 0.1f); // 500m
        geoQuery.addGeoQueryEventListener(HomeFragment.this);
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
        myCity = FirebaseDatabase.getInstance().getReference("DangerousArea").child("MyCity");
        locationListener = this;

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
            geoFire.setLocation("You", new GeoLocation(lastLocation.getLatitude(),
                    lastLocation.getLongitude()), new GeoFire.CompletionListener() {
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
        myLocationRef = FirebaseDatabase.getInstance().getReference("MyLocation");
        geoFire = new GeoFire(myLocationRef);
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                if (mMap != null) {
                    lastLocation = locationResult.getLastLocation();
                    SharedData.getInstance().setLastLocation(lastLocation);
                    addUserMarker();
                }
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
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

    private void saveTask() {
        ArrayList<BookingSensors> bookingSensorsRooms = new ArrayList<>();
        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                //creating a task
                for (int i = 0; i < bookingSensorsRooms.size(); i++) {
                    bookingSensorsRoom = new BookingSensorsRoom();
                    bookingSensorsRoom.setParkingArea(bookingSensorsRooms.get(i).getParkingArea());
                    bookingSensorsRoom.setNoOfParking(bookingSensorsRooms.get(i).getCount());
                    bookingSensorsRoom.setLatitude(bookingSensorsRooms.get(i).getLat());
                    bookingSensorsRoom.setLongitude(bookingSensorsRooms.get(i).getLng());
                    Timber.e("bookingSensorsRoom -> %s", new Gson().toJson(bookingSensorsRoom));

                    DatabaseClient.getInstance(context).getAppDatabase()
                            .bookingSensorsDao().insert(bookingSensorsRoom);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
            }
        }

        SaveTask saveTask = new SaveTask();
        saveTask.execute();
    }

    private void startShimmer() {
        Timber.e("startShimmer");
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        bottomSheetRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void stopShimmer() {
        Timber.e("stopShimmer");
        if (mShimmerViewContainer != null) {
            mShimmerViewContainer.setVisibility(View.GONE);
            mShimmerViewContainer.stopShimmer();
        }
        bottomSheetRecyclerView.setVisibility(View.VISIBLE);
    }

    public void setNoData() {
        textViewNoData.setVisibility(View.VISIBLE);
        textViewNoData.setText(context.getString(R.string.no_nearest_parking_area_found));
    }

    public void hideNoData() {
        textViewNoData.setVisibility(View.GONE);
    }

    public interface SetBottomSheetCallBack {
        void setBottomSheet();
    }

    public interface AddressCallBack {
        void addressCall(String address);
    }

    public void fetchDirections(String origin, String destination) {

        polyline = mMap.addPolyline(getDefaultPolyLines(points));

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

    @Override
    public void onDirectionFinderSuccess(List<www.fiberathome.com.parkingapp.module.GoogleMapWebServiceNDistance.directionModules.Route> route) {
        hideLoading();
        if (!route.isEmpty() && polyline != null) polyline.remove();
        try {
            for (www.fiberathome.com.parkingapp.module.GoogleMapWebServiceNDistance.directionModules.Route mRoute : route) {
                PolylineOptions polylineOptions = getDefaultPolyLines(mRoute.points);
                /*if (polylineStyle == PolylineStyle.DOTTED)
                    polylineOptions = getDottedPolylines(route.points);*/
                polyline = mMap.addPolyline(polylineOptions);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error occurred on finding the directions...", Toast.LENGTH_SHORT).show();
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
                    /*if (j == 0) {    // Get distance from the list
                            distance = (String) point.get("distance");
                            continue;
                        } else if (j == 1) { // Get duration from the list
                            duration = (String) point.get("duration");
                            continue;
                        }*/

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
    }
}