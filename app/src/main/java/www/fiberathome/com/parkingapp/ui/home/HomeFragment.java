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
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
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
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
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
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.api.Common;
import www.fiberathome.com.parkingapp.model.api.IGoogleApi;
import www.fiberathome.com.parkingapp.model.data.AppConstants;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.model.response.booking.BookingSensors;
import www.fiberathome.com.parkingapp.model.response.search.SearchVisitorData;
import www.fiberathome.com.parkingapp.model.response.search.SelectedPlace;
import www.fiberathome.com.parkingapp.model.sensors.SensorArea;
import www.fiberathome.com.parkingapp.module.GoogleMapWebServiceNDistance.DirectionsParser;
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

@SuppressLint("NonConstantResourceId")
public class HomeFragment extends BaseFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener,
        IOnLoadLocationListener, GeoQueryEventListener, BottomSheetAdapter.AdapterCallback, IOnBackPressListener {

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
    Button btnBottomSheetGetDirection;

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

    private Unbinder unbinder;

    private HomeActivity context;

    public BottomSheetBehavior bottomSheetBehavior;

    public static Location currentLocation;
    public static LatLng location;
    public LatLng searchPlaceLatLng;
    public LatLng bottomSheetPlaceLatLng;
    public LatLng markerPlaceLatLng;
    public String address, city, state, country, subAdminArea, test, knownName, postalCode = "";
    public GoogleMap mMap;

    //used in fetchSensor()
    public double nDistance = 132116456;
    public double nLatitude;
    public double nLongitude;
    public int fromRouteDrawn = 0;
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
    private ArrayList<LatLng> coordList = new ArrayList<LatLng>();
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
    private ArrayList<BookingSensors> bookingSensorsMarkerArrayList = new ArrayList<>();

    private Marker previousMarker = null;
    private Location onConnectedLocation;
    private final View.OnClickListener clickListener = view -> {
        if (view.getId() == R.id.currentLocationImageButton && mMap != null && onConnectedLocation != null)
            animateCamera(onConnectedLocation);
    };

    private ArrayList<BookingSensors> bookingSensorsArrayList = new ArrayList<>();
    private ArrayList<BookingSensors> bookingSensorsBottomSheetArrayList = new ArrayList<>();
    private double searchDistance;
    private JSONArray clickEventJsonArray;
    private JSONArray searchPlaceEventJsonArray;
    private JSONArray bottomSheetPlaceEventJsonArray;
    private JSONArray adapterPlaceEventJsonArray;
    private MyLatLng myLatLng;
    private String markerUid;
    private ArrayList<BookingSensors> bookingSensorsArrayListGlobal = new ArrayList<>();

    private double adjustValue = 2;
    private LatLng origin;
    private int countadd = 0;
    private BookingSensorsRoom bookingSensorsRoom;
    private double adapterDistance;
    private ArrayList<BookingSensors> bookingSensorsAdapterArrayList = new ArrayList<>();
    private String adapterUid;
    private String bottomUid;
    private ArrayList<BookingSensors> bookingSensorsArrayListGlobalRoom = new ArrayList<>();
    private Marker markerClicked;
    private boolean isNotificationSent=false;

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

    private static int countDuplicates(ArrayList<BookingSensors> list) {
        ArrayList<BookingSensors> distinctList = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            distinctList = (ArrayList<BookingSensors>) list.stream().distinct().collect(Collectors.toList());
        }
        int dublicates = list.size() - distinctList.size();
        return dublicates;
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
        if (bookingSensorsArrayListGlobal.isEmpty() && !bookingSensorsArrayListGlobalRoom.isEmpty()) {
            fetchDataFromRoom();
        }
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

            progressDialog = new ProgressDialog(requireActivity());
            progressDialog.setMessage("Initializing....");

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

        /*if (isServicesOk()) {
            if (isGPSEnabled()) {
                supportMapFragment = SupportMapFragment.newInstance();
                if (context != null) {
                    FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction().
                            replace(R.id.map, supportMapFragment);
                    ft.commit();
                    supportMapFragment.getMapAsync(this);
                } else {
                    Toast.makeText(context, "Enable your Gps Location", Toast.LENGTH_SHORT).show();
                }

                progressDialog = new ProgressDialog(requireActivity());
                progressDialog.setMessage("Initializing....");

                new GpsUtils(context).turnGPSOn(new GpsUtils.onGpsListener() {
                    @Override
                    public void gpsStatus(boolean isGPSEnable) {
                        // turn on GPS
                        isGPS = isGPSEnable;
                    }
                });

                if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            */

        /*ActivityCompat.requestPermissions(context,new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission
                    .ACCESS_COARSE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);*/

        /*
                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    Log.d(TAG, "onViewCreated: in if");
                } else {
                  //  supportMapFragment.getMapAsync(this);
                    Log.d(TAG, "onViewCreated: in else");
                }

                arrivedtimeTV.setText("Arrived " + getDate(arrived));
                departuretimeTV.setText("Departure " + getDate(departure));
                timeDifferenceTV.setText(getTimeDifference(difference) + " min");
                //dekhte hobee eta koi boshbe
                if (getDirectionButtonClicked == 0) {
                    linearLayoutParkingAdapterBackBottom.setOnClickListener(v -> {
                        ApplicationUtils.showMessageDialog("Once reach your destination you can reserve your booking spot!!!", context);
                    });
                } else {
                    linearLayoutParkingAdapterBackBottom.setOnClickListener(v -> {
                        ApplicationUtils.showMessageDialog("Hey Shawn!!!", getContext());
                    });
                }
            }
            else {
                Toast.makeText(context, "Enable your Gps Location", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Enable your Gps Location", Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Timber.e("onMapReady called");

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            Timber.d("onMapReady: if cond");
        }

        //changing map style
        /*try {
            boolean isSuccess = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.uber_map_style20));

            if (!isSuccess) {
                Timber.e("Error...Map Style load failed!!!");
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }*/

        mMap = googleMap;
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
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.setMyLocationEnabled(false);//if false it remove the blue dot over icon
        mMap.setBuildingsEnabled(false);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(false);
        buildGoogleApiClient();
        //mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);

        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }

        //add circle for dangerous area
        //addCircleArea();
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onMarkerClick(Marker marker) {
        String uid = "";

        String markerAreaName = "";

        if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
            if (bookingSensorsMarkerArrayList != null)
                bookingSensorsMarkerArrayList.clear();

            if (fromRouteDrawn == 0) {
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
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_gray));
                            markerClicked= marker;
                            isNotificationSent = false;
                        }
                    }

//                    context.setGeoFencing(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));



//                    context.startGeofence(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));

                    bookingSensorsMarkerArrayList.clear();

                    if (parkingNumberOfIndividualMarker != null) {
                        if (parkingNumberOfIndividualMarker.equals("0") && getDirectionMarkerButtonClicked == 1) {
                            Timber.e("parkingNumberOfIndividualMarker onMarkerClick if called");
                            btnMarkerGetDirection.setText(context.getResources().getString(R.string.get_direction));
                            btnMarkerGetDirection.setEnabled(true);
                            btnMarkerGetDirection.setFocusable(true);
                            btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                            getDirectionMarkerButtonClicked = 0;
                        }
                       /* else if (parkingNumberOfIndividualMarker.equals("0") && getDirectionMarkerButtonClicked == 0) {
                            Timber.e("parkingNumberOfIndividualMarker onMarkerClick else called");
                            btnMarkerGetDirection.setText("Get Direction");
                            btnMarkerGetDirection.setEnabled(true);
                            btnMarkerGetDirection.setFocusable(true);
                            btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                            getDirectionMarkerButtonClicked = 1;
                        }*/
                    }

                    //calculate Duration
                    markerPlaceLatLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                    getDestinationInfoForDuration(markerPlaceLatLng);

                    if (marker.getTitle() != null) {
                        if (!marker.getTitle().equals("My Location")) {

                        /*mMap.addCircle(new CircleOptions().center(markerPlaceLatLng)
                                .radius(100) // 500m
                                //.strokeColor(Color.TRANSPARENT)
                                 //22 is transparent code
                                 //.fillColor(0x220000FF)
                                 // .strokeWidth(5.0f)
                        );*/

                            if (geoQuery != null) {
                                geoQuery.removeGeoQueryEventListener(this);
                                geoQuery.removeAllListeners();
                            }

                            geoQuery = geoFire.queryAtLocation(new GeoLocation(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude), 2f); // 500m
                            geoQuery.addGeoQueryEventListener(this);
                        }
                    }

                    for (int i = 0; i < clickEventJsonArray.length(); i++) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = clickEventJsonArray.getJSONObject(i);
                            String latitude1 = jsonObject.get("latitude").toString();
                            String longitude1 = jsonObject.get("longitude").toString();
                            uid = jsonObject.get("uid").toString();
                            markerAreaName = jsonObject.get("parking_area").toString();
                            Timber.e("jsonUid -> %s", uid);
                            double distanceForCount = calculateDistance(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude, ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));

                            if (distanceForCount < 0.001) {
                                parkingNumberOfIndividualMarker = jsonObject.get("no_of_parking").toString();
                                Timber.e("onMarkerClick initial parkingNumberOfIndividualMarker -> %s", parkingNumberOfIndividualMarker);
                                textViewMarkerParkingAreaCount.setText(parkingNumberOfIndividualMarker);
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Timber.e("clickEventJsonArray for loop sesh hoiche");
                    }

                    String finalUid = uid;
                    Timber.e("jsonUid finalUid -> %s", finalUid);
                    //getAddress(context, markerPlaceLatLng.latitude, markerPlaceLatLng.longitude, address -> {

                    String markerPlaceName = markerAreaName;
                    TaskParser taskParser = new TaskParser();
                    double markerDistance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                            new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
                    layoutMarkerVisible(true, markerPlaceName, parkingNumberOfIndividualMarker,
                            String.valueOf(markerDistance), marker.getPosition());

                    if (markerPlaceLatLng != null) {

                        markerUid = marker.getTitle();
                        Timber.e("markerUid -> %s", markerUid);

                        if (markerDistance < 3000) {
                            adjustValue = 1;
                        }

                        double kim = (markerDistance / 1000) + adjustValue;
                        double markerDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(markerDistance * 2.43));
                        //double markerDoubleDuration = ApplicationUtils.round(markerDistance * 2.43,1);
                        //double markerDoubleDuration = ApplicationUtils.convertToDouble(String.format(Locale.US, "%.2f", ApplicationUtils.convertToDouble(new DecimalFormat("##.##").format(markerDistance * 2.43))));
                        String markerStringDuration = String.valueOf(markerDoubleDuration);

                        bookingSensorsMarker = new BookingSensors(markerPlaceName, markerPlaceLatLng.latitude, markerPlaceLatLng.longitude,
                                ApplicationUtils.round(markerDistance,1), parkingNumberOfIndividualMarker, markerStringDuration,
                                context.getResources().getString(R.string.nearest_parking_from_your_destination),
                                BookingSensors.TEXT_INFO_TYPE, 0);
                        if (marker.getTitle() != null && bookingSensorsMarker.getCount() != null) {
                            if (bookingSensorsMarker.getCount().equals("") || marker.getTitle().equals("My Location")) {
                                parkingNumberOfIndividualMarker = "0";
                            }
                        }

                        bookingSensorsMarkerArrayList.add(new BookingSensors(markerPlaceName, markerPlaceLatLng.latitude, markerPlaceLatLng.longitude,
                                markerDistance, parkingNumberOfIndividualMarker, markerStringDuration,
                                context.getResources().getString(R.string.nearest_parking_from_your_destination),
                                BookingSensors.TEXT_INFO_TYPE, 0));

                        Timber.e("onMarkerClick bookingSensorsMarkerArrayList TEXT_INFO_TYPE-> %s", new Gson().toJson(bookingSensorsMarkerArrayList));

                        if (clickEventJsonArray != null) {

                            setBottomSheetList(new SetBottomSheetCallBack() {
                                @Override
                                public void setBottomSheet() {
                                    if (bookingSensorsMarkerArrayList != null && bottomSheetAdapter != null) {
                                        Timber.e("onMarkerClick if called");
                                        bookingSensorsArrayListGlobal.clear();
                                        bookingSensorsArrayListGlobal.addAll(bookingSensorsMarkerArrayList);
                                        bottomSheetAdapter.notifyDataSetChanged();
                                    } else {
                                        Timber.e("onMarkerClick if else called");
                                    }
                                }
                            }, clickEventJsonArray, markerPlaceLatLng, bookingSensorsMarkerArrayList, finalUid);

                        } else {
                            Toast.makeText(context, "Something went wrong!!! Please check your Internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //});
                }
            } else {
                ApplicationUtils.showOnlyMessageDialog(context.getResources().getString(R.string.you_have_to_exit_from_current_destination), context);
            }
        } else {
            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
        }

        return true;
    }

    /*@NonNull
    private Geofence getGeofence() {
        LatLng latLng = Constants.AREA_LANDMARKS.get(Constants.GEOFENCE_ID);
        return new Geofence.Builder()
                .setRequestId(Constants.GEOFENCE_ID)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setCircularRegion(latLng.latitude, latLng.longitude, Constants.GEOFENCE_RADIUS_IN_METERS)
                .setNotificationResponsiveness(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }*/

    private synchronized void setBottomSheetList(SetBottomSheetCallBack setBottomSheetCallBack, JSONArray jsonArray, LatLng latLng, ArrayList<BookingSensors> bookingSensorsArrayList, String markerUid) {
        final int[] count = {0};
        int count2 = jsonArray.length();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject;
            try {
                jsonObject = jsonArray.getJSONObject(i);
                String latitude1 = jsonObject.get("latitude").toString();
                String longitude1 = jsonObject.get("longitude").toString();
                String uid = jsonObject.get("uid").toString();
                String parkingArea = jsonObject.get("parking_area").toString();

                double distanceForNearbyLoc = calculateDistance(latLng.latitude, latLng.longitude,
                        ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));

                Timber.e("distanceForNearbyLoc -> %s", distanceForNearbyLoc);

                final String[] nearbyAreaName = {""};

                if (distanceForNearbyLoc < 5 && !markerUid.equals(uid)) {
                    origin = new LatLng(latLng.latitude, latLng.longitude);
                    /*getAddress(context, ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1), new AddressCallBack() {
                        @Override
                        public void addressCall(String address) {*/
                    nearbyAreaName[0] = parkingArea;
                    Timber.e("addresses -> %s", address);
                    //String nearbyAreaName = address;
                    String parkingNumberOfNearbyDistanceLoc = null;
                    try {
                        parkingNumberOfNearbyDistanceLoc = jsonObject.get("no_of_parking").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    count[0]++;
                    int adjustNearbyValue = 2;
                    /*if (distanceForNearbyLoc < 1000) {
                        adjustNearbyValue = 1;
                    }*/

                    double kim = (distanceForNearbyLoc / 1000) + adjustNearbyValue;

                    if (distanceForNearbyLoc > 1.9) {
                        distanceForNearbyLoc = distanceForNearbyLoc + 2;
                    } else if (distanceForNearbyLoc > 1.9 && distanceForNearbyLoc < 1) {
                        distanceForNearbyLoc = distanceForNearbyLoc + 1;
                    } else {
                        distanceForNearbyLoc = distanceForNearbyLoc + 0.5;
                    }

                    double nearbySearchDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(distanceForNearbyLoc * 2.43));
                    //double nearbySearchDoubleDuration = ApplicationUtils.convertToDouble(String.format(Locale.US, "%.2f", ApplicationUtils.convertToDouble(new DecimalFormat("##.##").format(km * 2.43))));
                    String nearbySearchStringDuration = String.valueOf(nearbySearchDoubleDuration);

                    bookingSensorsArrayList.add(new BookingSensors(nearbyAreaName[0], ApplicationUtils.convertToDouble(latitude1),
                            ApplicationUtils.convertToDouble(longitude1), distanceForNearbyLoc, parkingNumberOfNearbyDistanceLoc,
                            nearbySearchStringDuration,
                            BookingSensors.INFO_TYPE, 1));

                    Timber.e("onMarkerClick bookingSensorsMarkerArrayList INFO_TYPE-> %s", new Gson().toJson(bookingSensorsArrayList));

                    bubbleSortArrayList(bookingSensorsArrayList);

                    setBottomSheetCallBack.setBottomSheet();
                    bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._130sdp));
                    if (markerPlaceLatLng != null) {
                        linearLayoutMarkerBackNGetDirection.setVisibility(View.VISIBLE);
                    } else if (location != null) {
                        linearLayoutBottom.setVisibility(View.VISIBLE);
                        linearLayoutParkingAdapterBackBottom.setVisibility(View.VISIBLE);
                    } else if (searchPlaceLatLng != null) {
                        linearLayoutSearchBottomButton.setVisibility(View.VISIBLE);
                    } else if (bottomSheetPlaceLatLng != null) {
                        linearLayoutBottomSheetGetDirection.setVisibility(View.VISIBLE);
                    }
                    progressDialog.dismiss();
                        /*}
                    });*/
                }
                /*setBottomSheetCallBack.setBottomSheet();
                linearLayoutMarkerBottom.setVisibility(View.VISIBLE);
                linearLayoutMarkerBackNGetDirection.setVisibility(View.VISIBLE);*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //setBottomSheetCallBack.setBottomSheet();
                //linearLayoutMarkerBottom.setVisibility(View.VISIBLE);
                //bottomSheetBehavior.setPeekHeight(450);
                bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._130sdp));
                if (markerPlaceLatLng != null) {
                    linearLayoutMarkerBackNGetDirection.setVisibility(View.VISIBLE);
                } else if (location != null) {
                    linearLayoutBottom.setVisibility(View.VISIBLE);
                    linearLayoutParkingAdapterBackBottom.setVisibility(View.VISIBLE);
                } else if (searchPlaceLatLng != null) {
                    linearLayoutSearchBottomButton.setVisibility(View.VISIBLE);
                } else if (bottomSheetPlaceLatLng != null) {
                    linearLayoutBottomSheetGetDirection.setVisibility(View.VISIBLE);
                }
                progressDialog.dismiss();
            }
        }, 1000);*/
    }

    private synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Log.d(TAG, "onConnected: ");
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

        /*if (onConnectedLocation == null) {
            Log.d(TAG, "onConnected: im currently showing progress");
            Toast.makeText(context, "im currently showing progress", Toast.LENGTH_SHORT).show();

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
            onConnectedLocation = new Location(LocationManager.NETWORK_PROVIDER);
            onConnectedLocation.setLatitude(23.8103);
            onConnectedLocation.setLongitude(90.4125);

            LatLng latLng = new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16f));
            animateCamera(onConnectedLocation);
            fetchSensors(onConnectedLocation);
            fetchBottomSheetSensors(onConnectedLocation);
            SharedData.getInstance().setOnConnectedLocation(onConnectedLocation);
            progressDialog.dismiss();

            buildLocationRequest();
            buildLocationCallBack();
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

            initArea();
            settingGeoFire();

        }*/

        if (onConnectedLocation != null) {
            Timber.e("onConnected not null called");
            LatLng latLng = new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(16f));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13.8f));
            animateCamera(onConnectedLocation);
            bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));
            if (ApplicationUtils.checkInternet(context)) {
                fetchSensors(onConnectedLocation);
                fetchBottomSheetSensors(onConnectedLocation);
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
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(16f));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13.8f));
            bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));
            try {
                linearLayoutParkingAdapterBackBottom.setVisibility(View.INVISIBLE);
            } catch (Exception e) {
                e.getCause();
            }
            animateCamera(onConnectedLocation);
            if (ApplicationUtils.checkInternet(context)) {
                fetchSensors(onConnectedLocation);
                fetchBottomSheetSensors(onConnectedLocation);
            } else {
                //ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.connect_to_internet), context);
                ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
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
                });
            }
        }

        SharedData.getInstance().setOnConnectedLocation(onConnectedLocation);

        progressDialog.dismiss();

        buildLocationRequest();

        buildLocationCallBack();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        initArea();

        settingGeoFire();

        //value getting from parking adapter
        if (SharedData.getInstance().getSensorArea() != null) {
            SensorArea sensorArea = SharedData.getInstance().getSensorArea();
            //Timber.e("Sensor Area from SharedData -> %s", new Gson().toJson(sensorArea));

            textViewParkingAreaName.setText(ApplicationUtils.capitalize(sensorArea.getParkingArea()));
            textViewParkingAreaCount.setText(sensorArea.getCount());
            String distance = new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(sensorArea.getDistance()) + " km";
            //textViewParkingDistance.setText(distance);
            textViewParkingDistance.setText(context.getResources().getString(R.string.distance, distance));
            //textViewParkingDistance.setText(NumberFormat.getInstance().format(distance));
            //textViewParkingTravelTime.setText(sensorArea.getDuration());
            getDestinationInfoForDuration(new LatLng(sensorArea.getLat(), sensorArea.getLng()));
        } else {
            Timber.e("Genjam");
        }
    }

    public void animateCamera(@NonNull Location location) {
        //Timber.e("animateCamera called");
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (mMap != null)
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)));
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        //Timber.e("getCameraPositionWithBearing called");
        return new CameraPosition.Builder().target(latLng).zoom(13.8f).build();
        //return new CameraPosition.Builder().target(latLng).zoom(16f).build();
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
        //Timber.e("onLocationChanged currentLocation -> %s", currentLocation);
        //previousMarker = null;
        //Toast.makeText(context, "900 onLocationChanged previous null", Toast.LENGTH_SHORT).show();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }

        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_car_running))
                .title("My Location")
                .rotation(location.getBearing()).flat(true).anchor(0.5f, 0.5f));
        if(markerClicked!=null){
            checkParkingSpotDistance(latLng,markerClicked.getPosition());
        }

    }

    private void checkParkingSpotDistance(LatLng car, LatLng spot) {
        double distanceBetween = ApplicationUtils.distance(car.latitude, car.longitude, spot.latitude, spot.longitude);
       if(distanceBetween <=70 && !isNotificationSent ){
           sendNotification("You Entered parking spot","You can book parking slot");

           isNotificationSent = true;
       }

        animateMarker(currentLocationMarker, location); // Helper method for smooth animation
    }

    public void animateMarker(final Marker marker, final Location location) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final LatLng startLatLng = marker.getPosition();
        final double startRotation = marker.getRotation();
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);

                double lng = t * location.getLongitude() + (1 - t)
                        * startLatLng.longitude;
                double lat = t * location.getLatitude() + (1 - t)
                        * startLatLng.latitude;

                float rotation = (float) (t * location.getBearing() + (1 - t)
                        * startRotation);

                marker.setPosition(new LatLng(lat, lng));
                marker.setRotation(rotation);

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
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
        /*if (!new LocationHelper(requireActivity()).isLocationEnabled()) {
            Intent intent = new Intent(requireActivity(), LocationActivity.class);
            startActivity(intent);
        }*/
        /*nearest.setOnClickListener(this);
        if (isServicesOk()) {
            if (isGPSEnabled()) {
            supportMapFragment.getMapAsync(this);
            }
        }*/
    }

    @Override
    public void onPause() {
        Timber.e("onPause called");
        super.onPause();
        //dismissProgressDialog();
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
        //dismissProgressDialog();
        hideLoading();

        //ToDo (after booking payment, remove geoFencing)
        /*geofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(context, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences removed
                        // ...
                    }
                })
                .addOnFailureListener(context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to remove geofences
                        // ...
                    }
                });*/
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
        Timber.e("onActivityResult HomeFragment called");
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d("onMapReady: onActivityResult");

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

                //Toast.makeText(context, "1639  previous null", Toast.LENGTH_SHORT).show();
                searchPlaceLatLng = new LatLng(selectedPlace.getLatitude(), selectedPlace.getLongitude());
                Timber.e("selectedPlace -> %s", selectedPlace.getAreaName());
                Timber.e("selectedPlace LatLng -> %s", new LatLng(selectedPlace.getLatitude(), selectedPlace.getLongitude()));
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
                Timber.e("selectedPlace searchPlaceLatLng -> %s", searchPlaceLatLng);

                if (mMap != null)
                    mMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(searchPlaceLatLng);
                coordList.add(new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                mMap.addMarker(markerOptions);
                //move map camera
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 16f), 500, null);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 16f));

                if (ApplicationUtils.checkInternet(context)) {
                    fetchSensors(onConnectedLocation);
                } else {
                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                }
                //get Address from search location
                /*getAddress(context, searchPlaceLatLng.latitude, searchPlaceLatLng.longitude, new AddressCallBack() {
                    @Override
                    public void addressCall(String address) {*/
                String searchPlaceName = areaName;
                if (onConnectedLocation != null && searchPlaceLatLng != null) {
                    TaskParser taskParser = new TaskParser();
                    searchDistance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                            new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
                    /*searchDistance = calculationByDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                            new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));*/
                    Timber.e("searchDistance -> %s", searchDistance);

                    layoutSearchVisible(true, searchPlaceName, "0", textViewSearchParkingDistance.getText().toString(), searchPlaceLatLng);

                    bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));

                    if (searchDistance < 3000) {
                        adjustValue = 1;
                    }

                    double kim = (searchDistance / 1000) + adjustValue;

                    double searchDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(searchDistance * 2.43));

                    String searchStringDuration = String.valueOf(searchDoubleDuration);

                    bookingSensorsArrayList.add(new BookingSensors(searchPlaceName, searchPlaceLatLng.latitude, searchPlaceLatLng.longitude,
                            searchDistance, "0", searchStringDuration,
                            context.getResources().getString(R.string.nearest_parking_from_your_destination),
                            BookingSensors.TEXT_INFO_TYPE, 0));

                    if (searchPlaceEventJsonArray != null) {
                        for (int i = 0; i < searchPlaceEventJsonArray.length(); i++) {
                            JSONObject jsonObject;
                            try {
                                jsonObject = searchPlaceEventJsonArray.getJSONObject(i);
                                String latitude1 = jsonObject.get("latitude").toString();
                                String longitude1 = jsonObject.get("longitude").toString();
                                String nearestSearchAreaName = jsonObject.get("parking_area").toString();

                                double distanceForNearbyLoc = calculateDistance(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude,
                                        ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));
                                //Timber.e("DistanceForNearbyLoc -> %s", distanceForNearbyLoc);

                                if (distanceForNearbyLoc < 5) {
                                    origin = new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude);
                                            /*getAddress(context, ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1), new AddressCallBack() {
                                                @Override
                                                public void addressCall(String address) {*/

                                    String nearbyAreaName = nearestSearchAreaName;
                                    String parkingNumberOfNearbyDistanceLoc = null;
                                    try {
                                        parkingNumberOfNearbyDistanceLoc = jsonObject.get("no_of_parking").toString();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    int adjustNearbyValue = 2;
                                            /*if (distanceForNearbyLoc < 1000) {
                                                adjsutNearbyValue = 1;
                                            }*/

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

                                    bookingSensorsArrayList.add(new BookingSensors(nearbyAreaName, ApplicationUtils.convertToDouble(latitude1),
                                            ApplicationUtils.convertToDouble(longitude1), distanceForNearbyLoc, parkingNumberOfNearbyDistanceLoc,
                                            nearbySearchStringDuration,
                                            BookingSensors.INFO_TYPE, 1));

                                    bubbleSortArrayList(bookingSensorsArrayList);
                                    //bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));

                                    if (bookingSensorsArrayList != null) {
                                        if (bottomSheetAdapter != null) {
                                            bookingSensorsArrayListGlobal.clear();
                                            bookingSensorsArrayListGlobal.addAll(bookingSensorsArrayList);
                                            bottomSheetAdapter.notifyDataSetChanged();
                                        }
                                    }
                                                /*}
                                            });*/
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast.makeText(context, "Something went wrong!!! Please check your Internet connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Location cannot be identified!!!", Toast.LENGTH_SHORT).show();
                }
                    /*}
                });*/

                if (bookingSensorsArrayList != null) {
                    if (bottomSheetAdapter != null) {
                        bookingSensorsArrayListGlobal.clear();
                        bookingSensorsArrayListGlobal.addAll(bookingSensorsArrayList);
                        bottomSheetAdapter.notifyDataSetChanged();
                    }
                }

            }

            //search history result
            SearchVisitorData searchVisitorData = (SearchVisitorData) data.getSerializableExtra(HISTORY_PLACE_SELECTED);
            if (searchVisitorData != null) {
                previousMarker = null;
                //Toast.makeText(context, "1764  previous null", Toast.LENGTH_SHORT).show();
                searchPlaceLatLng = new LatLng(searchVisitorData.getEndLat(), searchVisitorData.getEndLng());
                Timber.e("selectedPlace LatLng -> %s", new LatLng(searchVisitorData.getEndLat(), searchVisitorData.getEndLng()));
                String areaName = searchVisitorData.getVisitedArea();
                String placeId = searchVisitorData.getPlaceId();
                buttonSearch.setVisibility(View.GONE);
                linearLayoutSearchBottom.setVisibility(View.VISIBLE);
                linearLayoutSearchBottomButton.setVisibility(View.VISIBLE);
                btnSearchGetDirection.setVisibility(View.VISIBLE);
                btnSearchGetDirection.setEnabled(true);
                imageViewSearchBack.setVisibility(View.VISIBLE);
                Timber.e("selectedPlace searchPlaceLatLng -> %s", searchPlaceLatLng);

                if (mMap != null)
                    mMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(searchPlaceLatLng);
                coordList.add(new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                mMap.addMarker(markerOptions);
                //move map camera
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 16f), 500, null);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 16f));
                if (ApplicationUtils.checkInternet(context)) {
                    fetchSensors(onConnectedLocation);
                } else {
                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));

                }
                //get address from search history location
                /*getAddress(context, searchPlaceLatLng.latitude, searchPlaceLatLng.longitude, new AddressCallBack() {
                    @Override
                    public void addressCall(String address) {*/
                        String searchPlaceName = areaName;
                        if (onConnectedLocation != null && searchPlaceLatLng != null) {
                            TaskParser taskParser = new TaskParser();
                            searchDistance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                                    new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
                            /*searchDistance = calculationByDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                                    new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));*/
                            Timber.e("searchDistance -> %s", searchDistance);

                            layoutSearchVisible(true, searchPlaceName, "0", textViewSearchParkingDistance.getText().toString(), searchPlaceLatLng);

                            bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));

                            /*if (searchDistance < 3000) {
                                adjustValue = 1;
                            }*/

                            double kim = (searchDistance / 1000) + adjustValue;

                            double searchDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(searchDistance * 2.43));

                            String searchStringDuration = String.valueOf(searchDoubleDuration);

                            bookingSensorsArrayList.add(new BookingSensors(searchPlaceName, searchPlaceLatLng.latitude, searchPlaceLatLng.longitude,
                                    searchDistance, "0", searchStringDuration,
                                    context.getResources().getString(R.string.nearest_parking_from_your_destination),
                                    BookingSensors.TEXT_INFO_TYPE, 0));

                            if (searchPlaceEventJsonArray != null) {
                                for (int i = 0; i < searchPlaceEventJsonArray.length(); i++) {
                                    JSONObject jsonObject;
                                    try {
                                        jsonObject = searchPlaceEventJsonArray.getJSONObject(i);
                                        String latitude1 = jsonObject.get("latitude").toString();
                                        String longitude1 = jsonObject.get("longitude").toString();
                                        String nearestSearchAreaName = jsonObject.get("parking_area").toString();

                                        double distanceForNearbyLoc = calculateDistance(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude,
                                                ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));
                                        //Timber.e("DistanceForNearbyLoc -> %s", distanceForNearbyLoc);

                                        if (distanceForNearbyLoc < 5) {
                                            origin = new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude);
                                            /*getAddress(context, ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1), new AddressCallBack() {
                                                @Override
                                                public void addressCall(String address) {*/

                                            String nearbyAreaName = nearestSearchAreaName;
                                            String parkingNumberOfNearbyDistanceLoc = null;
                                            try {
                                                parkingNumberOfNearbyDistanceLoc = jsonObject.get("no_of_parking").toString();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            int adjustNearbyValue = 2;
                                            /*if (distanceForNearbyLoc < 1000) {
                                                adjustNearbyValue = 1;
                                            }*/

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

                                            if (bookingSensorsArrayList != null) {
                                                if (bottomSheetAdapter != null) {
                                                    bookingSensorsArrayListGlobal.clear();
                                                    bookingSensorsArrayListGlobal.addAll(bookingSensorsArrayList);
                                                    bottomSheetAdapter.notifyDataSetChanged();
                                                }
                                            }
                                                /*}
                                            });*/
                                        } else {
                                            if (bookingSensorsArrayList != null) {
                                                if (bottomSheetAdapter != null) {
                                                    bookingSensorsArrayListGlobal.clear();
                                                    bookingSensorsArrayListGlobal.addAll(bookingSensorsArrayList);
                                                    bottomSheetAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Something went wrong!!! Please check your Internet connection", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Location cannot be identified!!!", Toast.LENGTH_SHORT).show();
                        }
                    /*}
                });*/
            }
        } else {
            //Toast.makeText(context, "Sorry, Places cannot be identified, Please try again!", Toast.LENGTH_SHORT).show();
            if (SharedData.getInstance().getOnConnectedLocation() != null) {
                //  fetchSensors(SharedData.getInstance().getOnConnectedLocation());
            }
        }

        if (requestCode == GPS_REQUEST_CODE) {

            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            progressDialog = ApplicationUtils.progressDialog(context, "Enabling GPS ....");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
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
                    /*ActivityCompat.requestPermissions(context,new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission
                    .ACCESS_COARSE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);*/
                            requestPermissions(new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                            Timber.d("onViewCreated: in if");
                        } else {
                            //  supportMapFragment.getMapAsync(this);
                            Timber.d("onViewCreated: in else");
                        }

                        arrivedTimeTV.setText(new StringBuilder().append("Arrived ").append(getDate(arrived)).toString());
                        departureTimeTV.setText(new StringBuilder().append("Departure ").append(getDate(departure)).toString());
                        timeDifferenceTV.setText(new StringBuilder().append(getTimeDifference(difference)).append(" min").toString());
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
                progressDialog.show();
                if (isLocationEnabled(context))
                    supportMapFragment.getMapAsync(this);
                //Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();

            } else if (grantResults.length == FIRST_TIME_INSTALLED && context != null) {
                if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    Timber.d("onViewCreated: in if");
                    //Toast.makeText(context, "Ok Clicked", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        /*bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        btnGetDirection.setVisibility(View.GONE);
        linearLayoutParkingAdapterBackBottom.setVisibility(View.GONE);
        linearLayoutSearchBottomButton.setVisibility(View.GONE);
        linearLayoutMarkerBackNGetDirection.setVisibility(View.GONE);
        linearLayoutBottomSheetBottom.setVisibility(View.GONE);
        btnSearchGetDirection.setVisibility(View.GONE);
        btnMarkerGetDirection.setVisibility(View.GONE);
        btnBottomSheetGetDirection.setVisibility(View.GONE);
        buttonSearch.setVisibility(View.VISIBLE);
        if (mMap != null) {
            mMap.clear();
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13.8f));
        }
        if (SharedData.getInstance().getOnConnectedLocation() != null) {
            animateCamera(SharedData.getInstance().getOnConnectedLocation());
            fetchSensors(SharedData.getInstance().getOnConnectedLocation());
            fetchBottomSheetSensorsWithoutProgressBar(SharedData.getInstance().getOnConnectedLocation());
        } else {
            ApplicationUtils.refreshFragment(getParentFragmentManager(), this, R.id.nav_host_fragment);
        }*/
        return false;
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        else if (bottomSheetProgressDialog != null && bottomSheetProgressDialog.isShowing())
            bottomSheetProgressDialog.dismiss();
    }

    public void fetchSensors(Location location) {
        this.onConnectedLocation = location;
        /*progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Fetching The Parking Sensors....");
        progressDialog.setCancelable(false);
        progressDialog.show();*/

        //Timber.d("fetchSensors: " + mMap);
        if (mMap != null) {
            //Toast.makeText(context, "ye huppey", Toast.LENGTH_SHORT).show();
            //Log.d(TAG, "fetchSensors: yeaaaaaaaa");
        }

        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_SENSORS, response -> {
            //progressDialog.dismiss();
            Timber.e(" fetchSensors Response -> %s", response);
            try {
                JSONObject object = new JSONObject(response);
                JSONArray jsonArray = object.getJSONArray("sensors");
                clickEventJsonArray = object.getJSONArray("sensors");
                searchPlaceEventJsonArray = object.getJSONArray("sensors");
                adapterPlaceEventJsonArray = object.getJSONArray("sensors");
                bottomSheetPlaceEventJsonArray = object.getJSONArray("sensors");
                Timber.e("fetchSensors -> %s", new Gson().toJson(jsonArray));

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    double latitude = ApplicationUtils.convertToDouble(jsonObject.get("latitude").toString());
                    double longitude = ApplicationUtils.convertToDouble(jsonObject.get("longitude").toString());
                    //Timber.e("latitude latitude -> %s %s", latitude, longitude);
                    myLatLng = new MyLatLng(latitude, longitude);
                    dangerousArea.add(new LatLng(myLatLng.getLatitude(), myLatLng.getLongitude()));
                    SharedData.getInstance().setDangerousArea(dangerousArea);
                    SharedData.getInstance().setLatLng(new LatLng(myLatLng.getLatitude(), myLatLng.getLongitude()));
                    //Timber.e("myLatLng -> %s", new Gson().toJson(myLatLng));
                    double tDistance = calculateDistance(latitude, longitude, location.getLatitude(), location.getLongitude());
                    //Timber.e("tDistance: -> %s", tDistance);
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
                                //MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Booked And Parked").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                //Timber.e("Booked position -> %s", new LatLng(lat, lon));
                                mMap.addMarker(marker);

                                /*mMap.addCircle(new CircleOptions().center(new LatLng(lat, lon))
                                        .radius(200) // 500m
                                        .strokeColor(Color.BLUE)
                                        .fillColor(0x220000FF) //22 is transparent code
                                        .strokeWidth(5.0f)
                                );*/
                            }
                        } else {
                            sensorStatus = "Empty";
                            double lat = latitude;
                            double lon = longitude;

                            if (mMap != null) {
                                //MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Occupied.").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                //Timber.e("Occupied position -> %s", new LatLng(lat, lon));
                                mMap.addMarker(marker);

                                /*mMap.addCircle(new CircleOptions().center(new LatLng(lat, lon))
                                        .radius(200) // 500m
                                        .strokeColor(Color.BLUE)
                                        .fillColor(0x220000FF) //22 is transparent code
                                        .strokeWidth(5.0f)
                                );*/
                            }
                        }
                    } else {
                        if (jsonObject.get("reserve_status").toString().equalsIgnoreCase("1")) {
                            sensorStatus = "Occupied";
                            double lat = latitude;
                            double lon = longitude;
                            if (mMap != null) {
                                //MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Booked but No Vehicle").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                //Timber.e("No Vehicle position -> %s", new LatLng(lat, lon));
                                mMap.addMarker(marker);

                                /*mMap.addCircle(new CircleOptions().center(new LatLng(lat, lon))
                                        .radius(200) // 500m
                                        .strokeColor(Color.BLUE)
                                        .fillColor(0x220000FF) //22 is transparent code
                                        .strokeWidth(5.0f)
                                );*/
                            }

                        } else {
                            sensorStatus = "Empty";
                            double lat = latitude;
                            double lon = longitude;
                            if (mMap != null) {
                                // MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Empty").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                mMap.addMarker(marker);

                                /*mMap.addCircle(new CircleOptions().center(new LatLng(lat, lon))
                                        .radius(200) // 500m
                                        .strokeColor(Color.BLUE)
                                        .fillColor(0x220000FF) //22 is transparent code
                                        .strokeWidth(5.0f)
                                );*/
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
                /*Timber.e("getAddress:  address -> %s", address);
                Timber.e("getAddress:  city -> %s", city);
                Timber.e("getAddress:  country -> %s", country);
                Timber.e("getAddress:  test -> %s", test);
                Timber.e("getAddress:  premises -> %s", subAdminArea);
                Timber.e("getAddress:  state -> %s", state);
                Timber.e("getAddress:  postalCode -> %s", postalCode);
                Timber.e("getAddress:  knownName -> %s", knownName);*/
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
               /* Timber.e("getAddress:  address -> %s", address);
                Timber.e("getAddress:  city -> %s", city);
                Timber.e("getAddress:  country -> %s", country);
                Timber.e("getAddress:  test -> %s", test);
                Timber.e("getAddress:  premises -> %s", subAdminArea);
                Timber.e("getAddress:  state -> %s", state);
                Timber.e("getAddress:  postalCode -> %s", postalCode);
                Timber.e("getAddress:  knownName -> %s", knownName);*/

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
            Timber.e("addressesAbdurRock3--->%s", e.toString());

            //Toast.makeText(context, " IOException"+e, Toast.LENGTH_SHORT).show();
            addressTemp = googleApiAddressCall(context, LATITUDE, LONGITUDE, new AddressCallBack() {
                @Override
                public void addressCall(String address) {

                    //  Toast.makeText(context, address, Toast.LENGTH_SHORT).show();

                    addressCallBack.addressCall(address);

                    Timber.e("addressesAbdur--->%s", address);
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
                            //address= formatted_address[0];
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
                /*if (progressDialog != null) progressDialog.dismiss();
                showMessage(error.getMessage());*/
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
               /* params.put("mobile_no", mobileNo);
                params.put("password", password);*/
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ParkingApp.getInstance().addToRequestQueue(stringRequest, TAG);

        return formatted_address[0];
    }

    //fetch bottom sheet sensors
    private void fetchBottomSheetSensors(Location location) {
        Timber.e("fetchBottomSheetSensors called");
        if (bookingSensorsArrayListGlobal != null) {
            bookingSensorsArrayListGlobal.clear();
        }
        //initialize the progress dialog and show it
        if (!context.isFinishing())
            showLoading(context);
        if (mShimmerViewContainer != null)
            startShimmer();
        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_SENSORS, response -> {
            hideLoading();
            //bottomSheetProgressDialog.dismiss();
            if (mShimmerViewContainer != null)
                stopShimmer();
            try {
                JSONObject object = new JSONObject(response);
                JSONArray jsonArray = object.getJSONArray("sensors");
                Timber.e("bottomSheet response -> %s", new Gson().toJson(jsonArray));

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String areaName = jsonObject.get("parking_area").toString();
                    double latitude = ApplicationUtils.convertToDouble(jsonObject.get("latitude").toString());
                    double longitude = ApplicationUtils.convertToDouble(jsonObject.get("longitude").toString());
                    //Timber.e("api lat -> %s lon -> %s", latitude, longitude);
                    String count = jsonObject.get("no_of_parking").toString();

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

                    double doubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(fetchDistance * 2.43));
                    Timber.e("kim doubleDuration -> %s", doubleDuration);
                    //double doubleDuration = ApplicationUtils.convertToDouble(String.format(Locale.US, "%.2f", ApplicationUtils.convertToDouble(new DecimalFormat("##.##").format(kim * 2.43))));
                    String initialNearestDuration = String.valueOf(doubleDuration);
                    Timber.e("kim initialNearestDuration -> %s", initialNearestDuration);

                    if (fetchDistance < 5) {
                        origin = new LatLng(location.getLatitude(), location.getLongitude());
                        getAddress(context, latitude, longitude, new AddressCallBack() {
                            @Override
                            public void addressCall(String address) {
                                //bottomSheetBehavior.setPeekHeight(400)
                            }
                        });
                        //storing room database from json (No need at this moment)
                        bookingSensorsRoom = new BookingSensorsRoom();
                        bookingSensorsRoom.setParkingArea(areaName);
                        bookingSensorsRoom.setNoOfParking(count);
                        bookingSensorsRoom.setLatitude(latitude);
                        bookingSensorsRoom.setLongitude(longitude);

                        String nearestCurrentAreaName = areaName;
                        Timber.e("nearestCurrentAreaName -> %s", nearestCurrentAreaName);
                        bookingSensorsArrayListGlobal.add(new BookingSensors(nearestCurrentAreaName, latitude, longitude,
                                fetchDistance, count, initialNearestDuration,
                                BookingSensors.INFO_TYPE, 1));
                        //fetch distance in ascending order
                        Collections.sort(bookingSensorsArrayListGlobal, (c1, c2) -> Double.compare(c1.getDistance(), c2.getDistance()));
                        /*if (bookingSensorsArrayListGlobal.isEmpty()) {
                            setNoData();
                        } else {
                            hideNoData();
                        }*/
                    }
                }
                setBottomSheetFragmentControls(bookingSensorsArrayListGlobal);
                saveTask();
                //Collections.sort(bookingSensorsArrayListGlobal, BookingSensors.BY_NAME_ASCENDING_ORDER);
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
        strReq.setShouldCache(true);
        ParkingApp.getInstance().addToRequestQueue(strReq);
    }

    private void fetchBottomSheetSensorsWithoutProgressBar(Location location) {
        Timber.e("fetchBottomSheetSensorsWithoutProgressBar called");
        if (mShimmerViewContainer != null)
            startShimmer();
        if (bookingSensorsArrayListGlobal != null) {
            bookingSensorsArrayListGlobal.clear();
        }
        StringRequest strReq = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_SENSORS, response -> {
            if (mShimmerViewContainer != null)
                stopShimmer();
            try {
                JSONObject object = new JSONObject(response);
                JSONArray jsonArray = object.getJSONArray("sensors");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String areaName = jsonObject.get("parking_area").toString();
                    double latitude = ApplicationUtils.convertToDouble(jsonObject.get("latitude").toString());
                    double longitude = ApplicationUtils.convertToDouble(jsonObject.get("longitude").toString());

                    //Timber.e("api lat -> %s lon -> %s", latitude, longitude);
                    String count = jsonObject.get("no_of_parking").toString();

                    TaskParser taskParser = new TaskParser();
                    double fetchDistance = taskParser.showDistance(new LatLng(location.getLatitude(), location.getLongitude()),
                            new LatLng(latitude, longitude));

                    double kim = (fetchDistance / 1000) + adjustValue;
                    double doubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(kim * 2.43));

                    String initialNearestDuration = String.valueOf(doubleDuration);

                    if (fetchDistance < 5) {
                        origin = new LatLng(location.getLatitude(), location.getLongitude());
                        getAddress(context, latitude, longitude, new AddressCallBack() {
                            @Override
                            public void addressCall(String address) {
                                String nearestCurrentAreaName = areaName;
                                Timber.e("nearestCurrentAreaName without progressBar-> %s", nearestCurrentAreaName);
                                bookingSensorsArrayListGlobal.add(new BookingSensors(nearestCurrentAreaName, latitude, longitude,
                                        fetchDistance, count, initialNearestDuration,
                                        BookingSensors.INFO_TYPE, 1));
                                //fetch distance in ascending order
                                Collections.sort(bookingSensorsArrayListGlobal, (c1, c2) -> Double.compare(c1.getDistance(), c2.getDistance()));
                            }
                        });
                    }
                }
                setBottomSheetFragmentControls(bookingSensorsArrayListGlobal);
                //Collections.sort(bookingSensorsArrayListGlobal, BookingSensors.BY_NAME_ASCENDING_ORDER);
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

    @SuppressLint("SetTextI18n")
    private void setBottomSheetFragmentControls(ArrayList<BookingSensors> sensors) {
        bottomSheetRecyclerView.setHasFixedSize(true);
        bottomSheetRecyclerView.setItemViewCacheSize(20);
        bottomSheetRecyclerView.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        bottomSheetRecyclerView.setLayoutManager(mLayoutManager);
        bottomSheetRecyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        bottomSheetRecyclerView.setItemAnimator(new DefaultItemAnimator());
        bottomSheetRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, bottomSheetRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Timber.e("bottomSheetRecyclerView smoothScrollToPosition called");
                bottomSheetRecyclerView.smoothScrollToPosition(0);
                //Toast.makeText(getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        ViewCompat.setNestedScrollingEnabled(bottomSheetRecyclerView, false);

        setBottomSheetRecyclerViewAdapter(sensors);
    }

    private ArrayList<BookingSensors> separatorFunction(ArrayList<BookingSensors> userInfoList) {

        ArrayList<BookingSensors> sublist = new ArrayList<>();

        for (int i = 0; i < userInfoList.size(); i++) {
            String name = userInfoList.get(i).getParkingArea();

            boolean valid = true;
            for (int j = 0; j < sublist.size(); j++) {
                if (name != null && name.equals(sublist.get(j).getParkingArea())) {
                    valid = false;
                }
            }
            if (valid) {
                sublist.add(userInfoList.get(i));
            }
        }
        return sublist;
    }

    private void setBottomSheetRecyclerViewAdapter(ArrayList<BookingSensors> bookingSensors) {
        bottomSheetAdapter = new BottomSheetAdapter(context, this, bookingSensors, onConnectedLocation, this);
        bottomSheetRecyclerView.setAdapter(bottomSheetAdapter);
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
                    Timber.e("jsonObject if called");
                    //showMessage(jsonObject.getString("message"));
                    Timber.e("error message if block-> %s", jsonObject.getString("message"));

                } else {
                    //showMessage(jsonObject.getString("message"));
                    Timber.e("jsonObject else called");
                    Timber.e("error message else block-> %s", jsonObject.getString("message"));
                }
            } catch (JSONException e) {
                Timber.e("jsonObject catch -> %s", e.getMessage());
                e.printStackTrace();
            }

        }, error -> {
            Timber.e("jsonObject onErrorResponse -> %s", error.getMessage());
            //showMessage(error.getMessage());
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

    private void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
                                /*if (bottomSheetSearch == 0)
                                textViewMarkerParkingTravelTime.setText(duration);*/
                                textViewSearchParkingDistance.setText(distance);
                                textViewSearchParkingTravelTime.setText(duration);
                                /*Timber.e("search distance duration-> %s %s",
                                        textViewSearchParkingDistance.getText().toString(),
                                        textViewSearchParkingTravelTime.getText().toString());*/
                                textViewBottomSheetParkingTravelTime.setText(duration);
                                //Timber.e("textViewBottomSheetParkingTravelTime duration-> %s", textViewBottomSheetParkingTravelTime.getText().toString());
                                nearByDuration = duration;
                                //nearByDistance = distance;
                                fetchDuration = duration;
                                /*Timber.e("fetchDuration -> %s", fetchDuration);
                                Timber.e("inmethod nearByDuration nearByDistance -> %s %s", nearByDuration, nearByDistance);*/
                                //------------Displaying Distance and Time-----------------\\
                                //showingDistanceTime(distance, duration); // Showing distance and time to the user in the UI \\
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
        //String key = "key=AIzaSyDMWfYh5kjSQTALbZb-C0lSNACpcH5RDU4";
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

        //Timber.e("Directions URL: -> %s", url);

        mService.getDataFromGoogleApi(url)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                        //Timber.e("retrofit onResponse called");
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

                            /*mMap.addMarker(new MarkerOptions().
                                    position(polyLineList.get(polyLineList.size() - 1)));

                            Animator
                            final ValueAnimator polyLineAnimator = ValueAnimator.ofInt(0, 100);
                            polyLineAnimator.setDuration(2000);
                            polyLineAnimator.setRepeatCount(ValueAnimator.INFINITE);
                            //polyLineAnimator.setRepeatMode(ValueAnimator.RESTART);
                            polyLineAnimator.setInterpolator(new LinearInterpolator());
                            polyLineAnimator.addUpdateListener(valueAnimator -> {
                                List<LatLng> points = grayPolyline.getPoints();
                                //List<LatLng> points = blackPolyline.getPoints();
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
                                    //List<LatLng> greyLatLng = blackPolyline.getPoints();
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
                            polyLineAnimator.start();*/
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
        //googleMap.moveCamera(CameraUpdateFactory.zoomTo(13.5f));
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
        previousMarker = null;
        //Toast.makeText(context, "2287  previous null", Toast.LENGTH_SHORT).show();
        location = event.location;

        //Toast.makeText(context, "Geche", Toast.LENGTH_SHORT).show();
        //if (bottomSheetProgressDialog.isShowing()) bottomSheetProgressDialog.dismiss();
        hideLoading();
        layoutVisible(true, ApplicationUtils.capitalize(name), count, distance, event.location);
        /*if (bookingSensorsAdapterArrayList != null) {
            bookingSensorsAdapterArrayList.clear();
        }*/
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(event.location);
        coordList.add(new LatLng(event.location.latitude, event.location.longitude));

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin)).title(adapterUid);
        if (mMap != null) {
            mMap.addMarker(markerOptions);
            //move map camera
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(event.location, 16f));
        }
        Timber.e("updateBottomSheetForParkingAdapter animate camera er pore called");

        if (bookingSensorsArrayListGlobal.isEmpty()) {
            //todo
            Timber.e("bookingSensorsArrayListGlobal isEmpty() called");
            fetchDataFromRoom();
        }

        if (ApplicationUtils.checkInternet(context) && isGPSEnabled()) {
            Timber.e("updateBottomSheetForParkingAdapter if checking 2 conditions tcalled");
            String uid = "";
            String adapterAreaName = "";
            for (int i = 0; i < adapterPlaceEventJsonArray.length(); i++) {
                JSONObject jsonObject;
                try {
                    jsonObject = adapterPlaceEventJsonArray.getJSONObject(i);
                    String latitude1 = jsonObject.get("latitude").toString();
                    String longitude1 = jsonObject.get("longitude").toString();
                    uid = jsonObject.get("uid").toString();
                    adapterAreaName = jsonObject.get("parking_area").toString();
                    double distanceForCount = calculateDistance(event.location.latitude, event.location.longitude, ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));

                    if (distanceForCount < 0.001) {
                        adapterUid = uid;
                        /*mMap.addMarker(new MarkerOptions().position(new LatLng(ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1)))
                          .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_car_running))
                          .title(uid));*/
                        Timber.e("adapterUid -> %s", adapterUid);
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Timber.e("clickEventJsonArray for loop sesh hoiche");
            }

            if (SharedData.getInstance().getOnConnectedLocation() != null) {
                Timber.e("updateBottomSheetForParkingAdapter if tcalled");
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
                //getAddress(context, event.location.latitude, event.location.longitude, address -> {
                String adapterPlaceName = adapterAreaName;

                if (adapterDistance < 3000) {
                    adjustValue = 1;
                }

                double kim = (adapterDistance / 1000) + adjustValue;
                double adapterDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(adapterDistance * 2.43));
                //double adapterDoubleDuration = ApplicationUtils.convertToDouble(String.format(Locale.US, "%.2f", ApplicationUtils.convertToDouble(new DecimalFormat("##.##").format(adapterDistance * 2.43))));
                String adapterStringDuration = String.valueOf(adapterDoubleDuration);

                bookingSensorsAdapterArrayList.add(new BookingSensors(adapterPlaceName, event.location.latitude, event.location.longitude,
                        adapterDistance, textViewParkingAreaCount.getText().toString(), adapterStringDuration,
                        context.getResources().getString(R.string.nearest_parking_from_your_destination),
                        BookingSensors.TEXT_INFO_TYPE, 0));

                if (adapterPlaceEventJsonArray != null) {
                    setBottomSheetList(() -> {
                        if (bookingSensorsAdapterArrayList != null && bottomSheetAdapter != null) {
                                /*if (bottomSheetProgressDialog.isShowing()) {
                                    bottomSheetProgressDialog.dismiss();
                                    Timber.e("bottomSheetProgressDialog if called");
                                } else {
                                    Timber.e("bottomSheetProgressDialog else called");
                                    bottomSheetProgressDialog.dismiss();
                                }*/
                            hideLoading();
                            Timber.e("onMarkerClick if called");
                            bookingSensorsArrayListGlobal.clear();
                            bookingSensorsArrayListGlobal.addAll(bookingSensorsAdapterArrayList);
                            bottomSheetAdapter.notifyDataSetChanged();
                        } else {
                            Timber.e("onMarkerClick if else called");
                        }
                    }, adapterPlaceEventJsonArray, event.location, bookingSensorsAdapterArrayList, finalUid);
                }
                //});
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
        //Toast.makeText(context, "Adapter Click e Geche", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                location = event.location;
                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(location);

                coordList.add(new LatLng(location.latitude, location.longitude));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                mMap.addMarker(markerOptions);
                //move map camera
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f));

                btnGetDirection.setVisibility(View.VISIBLE);
                linearLayoutBottom.setVisibility(View.VISIBLE);
                imageViewBack.setVisibility(View.VISIBLE);

                String url = getDirectionsUrl(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()), event.location);
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);

                setMapZoomLevelDirection(new LatLng(onConnectedLocation.getLatitude(),
                        onConnectedLocation.getLongitude()), event.location);
            }
        }, 1000);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onBottomSheetDirectionEvent(GetDirectionBottomSheetEvent event) {
        //Toast.makeText(context, "BottomSheet Click e Geche", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomSheetPlaceLatLng = event.location;
                location = event.location;
                searchPlaceLatLng = event.location;
                markerPlaceLatLng = event.location;
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(bottomSheetPlaceLatLng);
                coordList.add(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                mMap.addMarker(markerOptions);
                //move map camera
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bottomSheetPlaceLatLng, 16f));
                btnBottomSheetGetDirection.setVisibility(View.VISIBLE);
                linearLayoutBottomSheetBottom.setVisibility(View.VISIBLE);
                imageViewBottomSheetBack.setVisibility(View.VISIBLE);

                String url = getDirectionsUrl(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()), event.location);

                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);

                setMapZoomLevelDirection(new LatLng(onConnectedLocation.getLatitude(),
                        onConnectedLocation.getLongitude()), event.location);

            }
        }, 1000);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSearchDirectionEvent(GetDirectionForSearchEvent event) {
        //Toast.makeText(context, "Search Click e Geche", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //animateCamera(currentLocation);
                searchPlaceLatLng = event.location;
                //EventBus.getDefault().post(new SetMarkerEvent(searchPlaceLatLng));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(searchPlaceLatLng);
                /*markerOptions.title(name);
                markerOptions.draggable(true);*/
                coordList.add(new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
                //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                mMap.addMarker(markerOptions);
                //move map camera
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 16f));
                btnSearchGetDirection.setVisibility(View.VISIBLE);
                /*linearLayoutNameCount.setVisibility(View.VISIBLE);
                linearLayoutSearchBottom.setVisibility(View.VISIBLE);*/
                linearLayoutSearchBottomButton.setVisibility(View.VISIBLE);
                imageViewSearchBack.setVisibility(View.VISIBLE);
                linearLayoutBottom.setVisibility(View.GONE);
                linearLayoutMarkerBottom.setVisibility(View.GONE);

                String url = getDirectionsUrl(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()), searchPlaceLatLng);

                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);

                setMapZoomLevelDirection(new LatLng(onConnectedLocation.getLatitude(),
                        onConnectedLocation.getLongitude()), searchPlaceLatLng);
            }
        }, 1000);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMarkerDirectionEvent(GetDirectionForMarkerEvent event) {
        //Toast.makeText(context, "Marker Click e Geche", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //animateCamera(currentLocation);
                markerPlaceLatLng = event.location;
                //EventBus.getDefault().post(new SetMarkerEvent(markerPlaceLatLng));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(markerPlaceLatLng);
                coordList.add(new LatLng(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                mMap.addMarker(markerOptions);
                //move map camera
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPlaceLatLng, 16f));
                btnMarkerGetDirection.setVisibility(View.VISIBLE);

                String url = getDirectionsUrl(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()), markerPlaceLatLng);

                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);
                fromMarkerRouteDrawn = 1;

                setMapZoomLevelDirection(new LatLng(onConnectedLocation.getLatitude(),
                        onConnectedLocation.getLongitude()), markerPlaceLatLng);

                /*float zoomLevel = getZoomLevel(markerDistance);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPlaceLatLng, zoomLevel));*/

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
            //padding = (int) (Math.min(width, height) * 0.15);
            padding = ((height) / 500);

            LatLngBounds bounds = builder.build();
            //mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
            //getBoundsZoomLevel(startPosition,endPosition,width,height);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
        }
    }

    @SuppressLint("SetTextI18n")
    private void layoutVisible(boolean isVisible, String name, String count,
                               String distance, LatLng location) {
        this.name = name;
        this.count = count;
        this.distance = distance;
        HomeFragment.location = location;

        if (isVisible) {
            /*linearLayoutBottom.setVisibility(View.VISIBLE);
            linearLayoutParkingAdapterBackBottom.setVisibility(View.VISIBLE);*/
            linearLayoutMarkerBackNGetDirection.setVisibility(View.VISIBLE);
            linearLayoutNameCount.setVisibility(View.GONE);
        } else {
            //linearLayoutBottom.setVisibility(View.GONE);
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
            /*BottomNavigationView navBar = context.findViewById(R.id.bottomNavigationView);
            navBar.setVisibility(View.VISIBLE);*/
            linearLayoutSearchBottom.setVisibility(View.VISIBLE);
            linearLayoutSearchNameCount.setVisibility(View.GONE);
            textViewSearchParkingAreaCount.setText(count);
            textViewSearchParkingAreaName.setText(ApplicationUtils.capitalize(name));
            isSearchAreaVisible = true;
        } else {
            /*BottomNavigationView navBar = context.findViewById(R.id.bottomNavigationView);
            navBar.setVisibility(View.VISIBLE);*/
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
            /*if (markerPlaceLatLng != null) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(markerPlaceLatLng);
                coordList.add(new LatLng(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                mMap.addMarker(markerOptions);
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPlaceLatLng, 16f));
            }*/
            linearLayoutMarkerNameCount.setVisibility(View.GONE);
            linearLayoutMarkerBottom.setVisibility(View.VISIBLE);
            /*textViewMarkerParkingAreaCount.setText(count);
            textViewMarkerParkingAreaName.setText(ApplicationUtils.capitalize(name));
            if (parkingNumberOfIndividualMarker.equals("0")) {
                btnMarkerGetDirection.setText("Unavailable Parking Spot");
                btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                btnMarkerGetDirection.setEnabled(false);
                btnMarkerGetDirection.setFocusable(false);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ApplicationUtils.showMessageDialog("You cannot reserve this spot for booking, as there is no parking slot!", context);
                    }
                }, 1000);
            } else {*/
            btnMarkerGetDirection.setText(context.getResources().getString(R.string.get_direction));
            btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
            btnMarkerGetDirection.setEnabled(true);
            btnMarkerGetDirection.setFocusable(true);
            //}
        } else {
            /*BottomNavigationView navBar = context.findViewById(R.id.bottomNavigationView);
            navBar.setVisibility(View.VISIBLE);*/
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
                //markerOptions.title(name);
                coordList.add(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
                //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                mMap.addMarker(markerOptions);
                //move map camera
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bottomSheetPlaceLatLng, 16f));

                for (int i = 0; i < bottomSheetPlaceEventJsonArray.length(); i++) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = bottomSheetPlaceEventJsonArray.getJSONObject(i);
                        String latitude1 = jsonObject.get("latitude").toString();
                        String longitude1 = jsonObject.get("longitude").toString();
                        uid = jsonObject.get("uid").toString();
                        if (isSearchAreaVisible) {
                            locationName = name;
                        } else {
                            locationName = jsonObject.get("parking_area").toString();
                        }
                        double distanceForCount = calculateDistance(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude, ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1));

                        if (distanceForCount < 0.001) {
                            bottomUid = uid;
                            /*mMap.addMarker(new MarkerOptions().position(new LatLng(ApplicationUtils.convertToDouble(latitude1), ApplicationUtils.convertToDouble(longitude1)))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_car_running))
                                    .title(uid));*/
                            Timber.e("bottomUid -> %s", bottomUid);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Timber.e("clickEventJsonArray for loop sesh hoiche");
                }
            }
            linearLayoutBottomSheetBottom.setVisibility(View.VISIBLE);
            linearLayoutBottomSheetNameCount.setVisibility(View.GONE);
            textViewBottomSheetParkingAreaCount.setText(count);
            textViewBottomSheetParkingAreaName.setText(ApplicationUtils.capitalize(name));
            //textViewBottomSheetParkingDistance.setText(new DecimalFormat("##.##").format(distance) + " km");
            textViewBottomSheetParkingDistance.setText(distance.substring(0, 3) + " km");
            //textViewMarkerParkingTravelTime.setText(duration);
            getDestinationInfoForDuration(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));

        } else {
            linearLayoutBottomSheetBottom.setVisibility(View.GONE);
        }

        if (isClicked) {
            Timber.e("isClicked called");
            if (bottomSheetPlaceLatLng != null && bookingSensorsBottomSheetArrayList != null && bottomSheetAdapter != null) {
                bookingSensorsBottomSheetArrayList.clear();
                context.setGeoFencing(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
                //for getting the location name
                String finalUid = uid;
                /*getAddress(context, bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude, new AddressCallBack() {
                    @Override
                    public void addressCall(String address) {*/

                String bottomSheetPlaceName = locationName;

                TaskParser taskParser = new TaskParser();
                double bottomSheetDistance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                        new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
                Timber.e(" searchDistance bottomSheetDistance -> %s", bottomSheetDistance);
                Timber.e(" bottomSheetDistance -> %s", searchDistance);

                        /*if (bottomSheetDistance < 3000) {
                            adjustValue = 1;
                        }*/

                double kim = (bottomSheetDistance / 1000) + adjustValue;

                if (kim > 1.9) {
                    kim = kim + 2;
                } else if (kim == 1.5) {
                    kim = kim + 1;
                } else {
                    kim = kim + 0.5;
                }

                double bottomSheetDoubleDuration = ApplicationUtils.convertToDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(bottomSheetDistance * 2.43));
                //double bottomSheetDoubleDuration = ApplicationUtils.convertToDouble(String.format(Locale.US, "%.2f", ApplicationUtils.convertToDouble(new DecimalFormat("##.##").format(bottomSheetDistance * 2.43))));
                String bottomSheetStringDuration = String.valueOf(bottomSheetDoubleDuration);

                bookingSensorsBottomSheetArrayList.add(new BookingSensors(bottomSheetPlaceName, bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude,
                        bottomSheetDistance, textViewBottomSheetParkingAreaCount.getText().toString(), bottomSheetStringDuration,
                        context.getResources().getString(R.string.nearest_parking_from_your_destination),
                        BookingSensors.TEXT_INFO_TYPE, 0));

                if (bottomSheetPlaceEventJsonArray != null) {
                    setBottomSheetList(() -> {
                        if (bookingSensorsBottomSheetArrayList != null && bottomSheetAdapter != null) {
                            Timber.e("setBottomSheet if called");
                            bookingSensorsArrayListGlobal.clear();
                            bookingSensorsArrayListGlobal.addAll(bookingSensorsBottomSheetArrayList);
                            bottomSheetAdapter.notifyDataSetChanged();
                            bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));
                        } else {
                            Timber.e("setBottomSheet if else called");
                        }
                    }, bottomSheetPlaceEventJsonArray, bottomSheetPlaceLatLng, bookingSensorsBottomSheetArrayList, finalUid);
                } else {
                    Toast.makeText(context, "Something went wrong!!! Please check your Internet connection", Toast.LENGTH_SHORT).show();
                }
                    /*}
                });*/
            }
        }

        /*else {
                ApplicationUtils.showMessageDialog("You have to exit from current destination to change new parking spot", context);
        }*/
    }

    private void setListeners() {

        buttonSearch.setOnClickListener(v -> {
            if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
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

                if (SharedData.getInstance().getOnConnectedLocation() != null) {
                    fetchBottomSheetSensorsWithoutProgressBar(SharedData.getInstance().getOnConnectedLocation());
                    fetchSensors(onConnectedLocation);
                }

                buttonSearch.setText(null);
                bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));
                linearLayoutBottom.setVisibility(View.GONE);
                linearLayoutSearchBottom.setVisibility(View.GONE);
                linearLayoutMarkerBottom.setVisibility(View.GONE);
                linearLayoutBottomSheetBottom.setVisibility(View.GONE);
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        });

        imageViewBack.setOnClickListener(v -> {
            if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
                if (mMap != null) {
                    mMap.clear();
                    mMap.setTrafficEnabled(true);

                    if (mShimmerViewContainer != null)
                        startShimmer();

                    previousMarker = null;
                    fromRouteDrawn = 0;
                    bookingSensorsArrayListGlobal.clear();
                    bookingSensorsArrayList.clear();
                    bookingSensorsMarkerArrayList.clear();
                    bookingSensorsAdapterArrayList.clear();

                    animateCamera(SharedData.getInstance().getOnConnectedLocation());

                    if (ApplicationUtils.checkInternet(context)) {
                        fetchSensors(SharedData.getInstance().getOnConnectedLocation());
                        fetchBottomSheetSensors(SharedData.getInstance().getOnConnectedLocation());
                    } else {
                        ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
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
                        });
                    }

                    buttonSearch.setText(null);
                    buttonSearch.setVisibility(View.VISIBLE);
                    layoutVisible(false, "", "", " ", null);
                    SharedData.getInstance().setParkingLocation(null);
                    //ApplicationUtils.reLoadFragment(getParentFragmentManager(), this);
                    linearLayoutBottom.setVisibility(View.GONE);
                    linearLayoutSearchBottom.setVisibility(View.GONE);
                    linearLayoutMarkerBottom.setVisibility(View.GONE);
                    linearLayoutBottomSheetBottom.setVisibility(View.GONE);

                    SharedData.getInstance().setSensorArea(null);

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));
                    btnGetDirection.setText(context.getResources().getString(R.string.get_direction));
                    btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                    btnGetDirection.setEnabled(true);
                    btnGetDirection.setFocusable(true);
                }
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        });

        imageViewSearchBack.setOnClickListener(v -> {
            if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
                if (mMap != null) {
                    mMap.clear();
                    mMap.setTrafficEnabled(true);
                    fromRouteDrawn = 0;
                    previousMarker = null;
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));

                    if (getDirectionSearchButtonClicked == 1) {
                        btnSearchGetDirection.setText(context.getResources().getString(R.string.get_direction));
                        btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                        getDirectionSearchButtonClicked--;
                    }

                    buttonSearch.setText(null);
                    buttonSearch.setVisibility(View.VISIBLE);
                    bookingSensorsArrayListGlobal.clear();
                    bookingSensorsArrayList.clear();
                    bookingSensorsAdapterArrayList.clear();
                    bookingSensorsMarkerArrayList.clear();

                    animateCamera(SharedData.getInstance().getOnConnectedLocation());

                    if (ApplicationUtils.checkInternet(context)) {
                        fetchSensors(SharedData.getInstance().getOnConnectedLocation());
                        fetchBottomSheetSensors(SharedData.getInstance().getOnConnectedLocation());
                    } else {
                        ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
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
                        });
                    }

                    ApplicationUtils.refreshFragment(getParentFragmentManager(), this, R.id.nav_host_fragment);
                    //ApplicationUtils.replaceFragmentWithAnimation(context.getSupportFragmentManager(), this);
                    layoutSearchVisible(false, "", "", "", null);
                    linearLayoutBottom.setVisibility(View.GONE);
                    linearLayoutSearchBottom.setVisibility(View.GONE);
                    linearLayoutMarkerBottom.setVisibility(View.GONE);
                    linearLayoutBottomSheetBottom.setVisibility(View.GONE);

                    SharedData.getInstance().setBookingSensors(null);

                    btnSearchGetDirection.setText(context.getResources().getString(R.string.get_direction));
                    btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                    btnSearchGetDirection.setEnabled(true);
                    btnSearchGetDirection.setFocusable(true);
                }
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        });

        imageViewMarkerBack.setOnClickListener(v -> {
            if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
                if (mMap != null) {
                    mMap.clear();
                    mMap.setTrafficEnabled(true);

                    fromRouteDrawn = 0;
                    previousMarker = null;
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));

                    if (getDirectionMarkerButtonClicked == 1) {
                        btnMarkerGetDirection.setText(context.getResources().getString(R.string.get_direction));
                        btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                        getDirectionMarkerButtonClicked--;
                    }

                    bookingSensorsArrayListGlobal.clear();
                    bookingSensorsArrayList.clear();
                    bookingSensorsAdapterArrayList.clear();
                    bookingSensorsMarkerArrayList.clear();

                    animateCamera(SharedData.getInstance().getOnConnectedLocation());

                    if (ApplicationUtils.checkInternet(context)) {
                        fetchSensors(SharedData.getInstance().getOnConnectedLocation());
                        fetchBottomSheetSensors(SharedData.getInstance().getOnConnectedLocation());
                    } else {
                        //ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.connect_to_internet), context);
                        ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                            Timber.e("Positive Button clicked");
                            if (ApplicationUtils.checkInternet(context)) {
                                fetchSensors(onConnectedLocation);
                                fetchBottomSheetSensors(onConnectedLocation);
                                bottomSheetAdapter.notifyDataSetChanged();
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

                    buttonSearch.setText(null);
                    buttonSearch.setVisibility(View.VISIBLE);
                    ApplicationUtils.refreshFragment(getParentFragmentManager(), this, R.id.nav_host_fragment);
                    //ApplicationUtils.replaceFragmentWithAnimation(context.getSupportFragmentManager(), this);
                    layoutMarkerVisible(false, "", "", "", null);
                    linearLayoutBottom.setVisibility(View.GONE);
                    linearLayoutSearchBottom.setVisibility(View.GONE);
                    linearLayoutMarkerBottom.setVisibility(View.GONE);
                    linearLayoutBottomSheetBottom.setVisibility(View.GONE);
                    btnMarkerGetDirection.setText(context.getResources().getString(R.string.get_direction));
                    btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                    btnMarkerGetDirection.setEnabled(true);
                    btnMarkerGetDirection.setFocusable(true);
                }
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        });

        imageViewBottomSheetBack.setOnClickListener(v -> {
            if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
                if (mMap != null) {
                    mMap.clear();
                    mMap.setTrafficEnabled(true);

                    fromRouteDrawn = 0;
                    previousMarker = null;
                    buttonSearch.setText(null);
                    buttonSearch.setVisibility(View.VISIBLE);
                    bookingSensorsArrayListGlobal.clear();
                    bookingSensorsArrayList.clear();
                    bookingSensorsAdapterArrayList.clear();
                    bookingSensorsMarkerArrayList.clear();
                    bookingSensorsBottomSheetArrayList.clear();
                    bottomSheetAdapter.updateData(bookingSensorsArrayListGlobal);

                    if (ApplicationUtils.checkInternet(context)) {
                        fetchSensors(SharedData.getInstance().getOnConnectedLocation());
                        fetchBottomSheetSensors(SharedData.getInstance().getOnConnectedLocation());
                        bottomSheetAdapter.notifyDataSetChanged();
                    } else {
                        //ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.connect_to_internet), context);
                        ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
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
                        });
                    }

                    animateCamera(SharedData.getInstance().getOnConnectedLocation());

                    if (getDirectionBottomSheetButtonClicked == 1) {
                        btnBottomSheetGetDirection.setText(context.getResources().getString(R.string.get_direction));
                        btnBottomSheetGetDirection.setEnabled(true);
                        btnBottomSheetGetDirection.setFocusable(true);
                        btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                        getDirectionBottomSheetButtonClicked--;
                    }

                    layoutBottomSheetVisible(false, "", "", "", "", null, false);
                    ApplicationUtils.refreshFragment(getParentFragmentManager(), this, R.id.nav_host_fragment);
                    //ApplicationUtils.replaceFragmentWithAnimation(context.getSupportFragmentManager(), this);
                    linearLayoutBottom.setVisibility(View.GONE);
                    linearLayoutSearchBottom.setVisibility(View.GONE);
                    linearLayoutMarkerBottom.setVisibility(View.GONE);
                    linearLayoutBottomSheetBottom.setVisibility(View.GONE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._90sdp));
                    btnBottomSheetGetDirection.setText(context.getResources().getString(R.string.get_direction));
                    btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                    btnBottomSheetGetDirection.setEnabled(true);
                    btnBottomSheetGetDirection.setFocusable(true);
                    getDirectionBottomSheetButtonClicked = 0;
                }
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        });

        btnGetDirection.setOnClickListener(v -> {
            if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
                mMap.setTrafficEnabled(false);
                fromRouteDrawn = 1;
                //Toast.makeText(context, "adapter", Toast.LENGTH_SHORT).show();
                if (getDirectionButtonClicked == 0) {
                    getDirectionButtonClicked++;
                    //ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.confirm_booking_message), context);
                    if (location != null) {
                        EventBus.getDefault().post(new GetDirectionAfterButtonClickEvent(location));
                        buttonSearch.setVisibility(View.GONE);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(location);
                        coordList.add(new LatLng(location.latitude, location.longitude));
                        mMap.addMarker(markerOptions);
                        //move map camera
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f));
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
                            context.setGeoFencing(new LatLng(location.latitude, location.longitude));
                            btnGetDirection.setEnabled(true);
                            btnGetDirection.setFocusable(true);
                            btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                            Toast.makeText(context, context.getResources().getString(R.string.you_can_book_parking_slot), Toast.LENGTH_LONG).show();
                        } else {
                            btnGetDirection.setEnabled(true);
                            btnGetDirection.setFocusable(true);
                            btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                            //Toast.makeText(context, "Go to the Selected Parking Area", Toast.LENGTH_LONG).show();
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
                    /*if (mMap != null) {
                        TaskParser taskParser = new TaskParser();
                        double distance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                            new LatLng(location.latitude, location.longitude));
                    if (distance < 0.1) {
                        btnGetDirection.setText("Confirm Booking");
                        btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
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
                }*/
                }
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        });

        btnSearchGetDirection.setOnClickListener(v -> {
            if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
                mMap.setTrafficEnabled(false);
                fromRouteDrawn = 1;
                //Toast.makeText(context, "search", Toast.LENGTH_SHORT).show();
                if (getDirectionSearchButtonClicked == 0) {
                    //ApplicationUtils.showOnlyMessageDialog(context.getResources().getString(R.string.no_parking_spot_message), context);
                    getDirectionSearchButtonClicked++;
                    if (searchPlaceLatLng != null) {
                        EventBus.getDefault().post(new GetDirectionForSearchEvent(searchPlaceLatLng));
                        buttonSearch.setVisibility(View.GONE);
                        MarkerOptions markerDestinationPositionOptions = new MarkerOptions();
                        markerDestinationPositionOptions.position(searchPlaceLatLng);
                        coordList.add(new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
                        mMap.addMarker(markerDestinationPositionOptions);
                        //move map camera
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 16f));
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
                    /*if (distance < 0.1) {
                        btnSearchGetDirection.setText("Unavailable Parking Spot");
                        btnSearchGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                        btnSearchGetDirection.setEnabled(false);
                        btnSearchGetDirection.setFocusable(false);
                        bookedLayout.setVisibility(View.VISIBLE);

                        Bundle bundle = new Bundle();
                        bundle.putBoolean("m", false); //m for more
                        ScheduleFragment scheduleFragment = new ScheduleFragment();
                        scheduleFragment.setArguments(bundle);
                        listener.fragmentChange(scheduleFragment);
                        bottomSheet.setVisibility(View.GONE);
                    }*/
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        });

        btnMarkerGetDirection.setOnClickListener(v -> {
            //Toast.makeText(context, "marker btn clicked", Toast.LENGTH_SHORT).show();
            if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
                mMap.setTrafficEnabled(false);
                fromRouteDrawn = 1;
                //Toast.makeText(context, "adapter", Toast.LENGTH_SHORT).show();
                if (getDirectionMarkerButtonClicked == 0) {
                    getDirectionMarkerButtonClicked++;
                    //ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.confirm_booking_message), context);
                    if (markerPlaceLatLng != null) {
                        EventBus.getDefault().post(new GetDirectionForMarkerEvent(markerPlaceLatLng));
                        buttonSearch.setVisibility(View.GONE);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(markerPlaceLatLng);
                        coordList.add(new LatLng(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude));
                        mMap.addMarker(markerOptions);
                        linearLayoutBottom.setVisibility(View.GONE);
                        linearLayoutSearchBottom.setVisibility(View.GONE);
                        linearLayoutMarkerBottom.setVisibility(View.VISIBLE);
                        imageViewBack.setVisibility(View.VISIBLE);
                        btnMarkerGetDirection.setText(context.getString(R.string.confirm_booking));
                        btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                        btnMarkerGetDirection.setEnabled(true);
                        btnMarkerGetDirection.setFocusable(true);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        if (parkingNumberOfIndividualMarker.equals("0")) {
                            Timber.e("count 0 if condition called");
                            btnMarkerGetDirection.setText(context.getResources().getString(R.string.unavailable_parking_spot));
                            btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                            btnMarkerGetDirection.setEnabled(true);
                            btnMarkerGetDirection.setFocusable(true);
                            ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.no_parking_spot_message), context);
                        } else {
                            Timber.e("count not 0 else condition called");
                            btnMarkerGetDirection.setText(context.getResources().getString(R.string.confirm_booking));
                            btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                            //ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.confirm_booking_message), context);
                            btnMarkerGetDirection.setEnabled(true);
                            btnMarkerGetDirection.setFocusable(true);
                        }
                        if (isInAreaEnabled ) {
                            context.setGeoFencing(new LatLng(markerPlaceLatLng.latitude, markerPlaceLatLng.longitude));
                            btnMarkerGetDirection.setEnabled(true);
                            btnMarkerGetDirection.setFocusable(true);
                            btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                            Toast.makeText(context, context.getResources().getString(R.string.you_can_book_parking_slot), Toast.LENGTH_LONG).show();
                        } else {
                            btnMarkerGetDirection.setEnabled(true);
                            btnMarkerGetDirection.setFocusable(true);
                            btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                            //Toast.makeText(context, "Go to the Selected Parking Area", Toast.LENGTH_LONG).show();
                        }
                    }
                } else if (getDirectionMarkerButtonClicked == 1) {
                    //getDirectionMarkerButtonClicked--;
                    if (parkingNumberOfIndividualMarker.equals("0")) {
                        Timber.e("count 0 if condition called");
                        btnMarkerGetDirection.setText(context.getResources().getString(R.string.unavailable_parking_spot));
                        btnMarkerGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                        btnMarkerGetDirection.setEnabled(true);
                        btnMarkerGetDirection.setFocusable(true);
                        ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.no_parking_spot_message), context);
                    } else {
                        ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.confirm_booking_message), context);
                    }
                    if (isInAreaEnabled) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("m", false); //m for more
                        bundle.putString("markerUid", markerUid);
                        ScheduleFragment scheduleFragment = new ScheduleFragment();
                        scheduleFragment.setArguments(bundle);
                        listener.fragmentChange(scheduleFragment);
                        bottomSheet.setVisibility(View.GONE);
                    }
                    /*if (mMap != null) {
                        TaskParser taskParser = new TaskParser();
                        double distance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
                            new LatLng(location.latitude, location.longitude));
                    if (distance < 0.1) {
                        btnGetDirection.setText("Confirm Booking");
                        btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
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
                }*/
                }
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        });

        btnBottomSheetGetDirection.setOnClickListener(v -> {
            /*Toast.makeText(context, "bottom sheet btn clicked", Toast.LENGTH_SHORT).show();
            getDirectionBottomSheetButtonClicked++;*/
            if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
                mMap.setTrafficEnabled(false);
                fromRouteDrawn = 1;
                //Toast.makeText(context, "bottom sheet", Toast.LENGTH_SHORT).show();
                if (getDirectionBottomSheetButtonClicked == 0) {
                    getDirectionBottomSheetButtonClicked++;
                    if (count.equals("0")) {
                        ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.no_parking_spot_message), context);
                    } else {
                        //ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.confirm_booking_message), context);
                    }
                    if (bottomSheetPlaceLatLng != null || location != null || markerPlaceLatLng != null || searchPlaceLatLng != null) {
                        Timber.e("all location called");
                        EventBus.getDefault().post(new GetDirectionBottomSheetEvent(bottomSheetPlaceLatLng));

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(bottomSheetPlaceLatLng);
                        coordList.add(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin));
                        mMap.addMarker(markerOptions);
                        //move map camera
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bottomSheetPlaceLatLng, 16f));
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
                            //ApplicationUtils.showMessageDialog(context.getResources().getString(R.string.confirm_booking_message), context);
                            btnBottomSheetGetDirection.setEnabled(true);
                            btnBottomSheetGetDirection.setFocusable(true);
                        }

                        if (isInAreaEnabled) {
                            if (count.equals("0")) {
                                btnBottomSheetGetDirection.setText(context.getResources().getString(R.string.unavailable_parking_spot));
                                btnBottomSheetGetDirection.setEnabled(true);
                                btnBottomSheetGetDirection.setFocusable(true);
                                btnBottomSheetGetDirection.setBackgroundColor(context.getResources().getColor(R.color.gray3));
                                //ApplicationUtils.showMessageDialog("You cannot reserve this spot for booking, as there is no parking slot!", context);
                            } else {
                                context.setGeoFencing(new LatLng(bottomSheetPlaceLatLng.latitude, bottomSheetPlaceLatLng.longitude));
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
                            //Toast.makeText(context, "Go to the Selected Parking Area", Toast.LENGTH_LONG).show();
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
                        /*markerAlreadyClicked = 0;
                    TaskParser taskParser = new TaskParser();
                    double distance = taskParser.showDistance(new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude()),
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
                    }*/
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
        /*Toast.makeText(context, "3308  previous null", Toast.LENGTH_SHORT).show();
        mMap.addCircle(new CircleOptions().center(latLng)
                .radius(100) // 500m
                .strokeColor(Color.TRANSPARENT)
                .fillColor(0x220000FF) //22 is transparent code
                .strokeWidth(5.0f)
        );*/
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
//        sendNotification("ParkingApp", String.format("%s entered the Parking Area", key));
        isInAreaEnabled = true;
    }

    @Override
    public void onKeyExited(String key) {
//        sendNotification("ParkingApp", String.format("%s leave the Parking Area", key));
        isInAreaEnabled = false;
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
        //Now, after dangerous area have data, we will call Map Display
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
       /* SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);

        supportMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction().
                replace(R.id.map, supportMapFragment);
        ft.commit();*/

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

        //load dangerous area from database
        /*myCity.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<MyLatLng> latLngList = new ArrayList<>();
                for (DataSnapshot locationSnapShot : dataSnapshot.getChildren()) {
                    MyLatLng latLng = locationSnapShot.getValue(MyLatLng.class);
                    latLngList.add(latLng);
                }
                locationListener.onLoadLocationSuccess(latLngList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                locationListener.onLoadLocationFailed(databaseError.getMessage());
            }
        });

        myCity.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Update dangerous areaList
                ArrayList<MyLatLng> latLngList = new ArrayList<>();

                for (DataSnapshot locationSnapShot : dataSnapshot.getChildren()) {
                    MyLatLng latLng = locationSnapShot.getValue(MyLatLng.class);
                    latLngList.add(latLng);
                }
                locationListener.onLoadLocationSuccess(latLngList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Timber.e("onCancelled database -> %s", databaseError.getMessage());
            }
        });*/
        dangerousArea = new ArrayList<>();
        //dangerousArea.add(new LatLng(23.7852, 90.4563));
        //dangerousArea.add(new LatLng(23.7209, 90.4833));
        dangerousArea.add(new LatLng(23.7759521, 90.4101246));
        Timber.e("dangerousArea -> %s", new Gson().toJson(dangerousArea));
        /*dangerousArea.add(SharedData.getInstance().getLatLng());

        dangerousArea.add(new LatLng(myLatLng.getLatitude(), myLatLng.getLongitude()));

        if (bookingSensorsArrayListGlobal != null) {
            for (int i = 0; i < bookingSensorsArrayListGlobal.size(); i++) {
                dangerousArea.add(new LatLng(bookingSensorsArrayListGlobal.get(i).getLat(), bookingSensorsArrayListGlobal.get(i).getLat()));
                Timber.e("dangerousArea -> %s", new Gson().toJson(dangerousArea));
                Timber.e("dangerousArea bookingSensorsArrayListGlobal if-> %s", new Gson().toJson(bookingSensorsArrayListGlobal));
            }
        } else {
            Toast.makeText(context, "bookingSensorsArrayListGlobal null", Toast.LENGTH_SHORT).show();
            Timber.e("dangerousArea bookingSensorsArrayListGlobal else-> %s", new Gson().toJson(bookingSensorsArrayListGlobal));
        }*/


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

        /*if (SharedData.getInstance().getLatLng() != null) {
            Timber.e("SharedData.getInstance().getLatLng() != null called");
            for (LatLng latLng : dangerousArea) {
                mMap.addCircle(new CircleOptions().center(latLng)
                        .radius(500) // 500m
                        .strokeColor(Color.BLUE)
                        .fillColor(0x220000FF) //22 is transparent code
                        .strokeWidth(5.0f)
                );

                //create GeoQuery when user in dangerous location
                geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 0.5f); // 500m
                geoQuery.addGeoQueryEventListener(this);
            }
        } else {*/
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
        //}
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

    private void fetchDataFromRoom() {
        Thread thread = new Thread(() -> {
            Timber.e("fetchfromRoom called");
            List<BookingSensorsRoom> sensorsRooms = DatabaseClient.getInstance(context).getAppDatabase().bookingSensorsDao().getAll();

            bookingSensorsArrayListGlobalRoom.clear();
            for (BookingSensorsRoom sensorsRoom : sensorsRooms) {
                BookingSensors bookingSensorsRoom = new BookingSensors(sensorsRoom.getParkingArea(), sensorsRoom.getLatitude(),
                        sensorsRoom.getLongitude(), sensorsRoom.getNoOfParking());
                Timber.e("fetchfromRoom booking sensors for room -> %s", new Gson().toJson(bookingSensorsRoom));
                bookingSensorsArrayList.clear();
                bookingSensorsArrayListGlobal.add(bookingSensorsRoom);
                bookingSensorsArrayListGlobalRoom.add(bookingSensorsRoom);
            }

            //refreshing recycler view
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //bottomSheetAdapter.updateData(bookingSensorsArrayListGlobalRoom);
                    setBottomSheetRecyclerViewAdapter(bookingSensorsArrayListGlobalRoom);
                    bottomSheetAdapter.notifyDataSetChanged();
                }
            });
        });
        thread.start();
    }

    private void startShimmer() {
        Timber.e("startShimmer");
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        bottomSheetRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void stopShimmer() {
        Timber.e("stopShimmer");
        mShimmerViewContainer.setVisibility(View.GONE);
        mShimmerViewContainer.stopShimmer();
        bottomSheetRecyclerView.setVisibility(View.VISIBLE);
    }

    public void setNoData() {
        textViewNoData.setVisibility(View.VISIBLE);
        textViewNoData.setText(context.getString(R.string.no_record_found));
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
                Timber.e("lists size -> %s", lists.size());
                //Toast.makeText(context, "No Points", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Timber.e("lists size -> %s", lists.size());
            }

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();
                //for (int j = 0; j < path.size(); j++) {
                for (HashMap<String, String> point : path) {
                    double lat = ApplicationUtils.convertToDouble(point.get("lat"));
                    double lon = ApplicationUtils.convertToDouble(point.get("lon"));
                        /*if (j == 0) {    // Get distance from the list
                            distance = (String) point.get("distance");
                            continue;
                        } else if (j == 1) { // Get duration from the list
                            duration = (String) point.get("duration");
                            continue;
                        }

                    Timber.e("duration -> %s", duration);*/

                    points.add(new LatLng(lat, lon));
                }
            }
            polylineOptions.addAll(points);
            polylineOptions.width(5);
            if (flag == 1) {
                //polylineOptions.color(Color.BLACK);
                polylineOptions.color(context.getResources().getColor(R.color.route_color));
                polylineOptions.width(5);
            }
                /*else if (flag == 2) {
                    if (googleMap != null)
                       googleMap.clear();
                    polylineOptions.color(Color.BLACK);
                    polylineOptions.width(5);
                }*/
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