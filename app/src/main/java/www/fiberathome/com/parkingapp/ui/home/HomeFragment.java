package www.fiberathome.com.parkingapp.ui.home;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.model.data.AppConstants.FIRST_TIME_INSTALLED;
import static www.fiberathome.com.parkingapp.model.data.AppConstants.HISTORY_PLACE_SELECTED;
import static www.fiberathome.com.parkingapp.model.data.AppConstants.NEW_PLACE_SELECTED;
import static www.fiberathome.com.parkingapp.model.data.AppConstants.NEW_SEARCH_ACTIVITY_REQUEST_CODE;
import static www.fiberathome.com.parkingapp.utils.GoogleMapHelper.defaultMapSettings;
import static www.fiberathome.com.parkingapp.utils.GoogleMapHelper.getDefaultPolyLines;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.databinding.FragmentHomeBinding;
import www.fiberathome.com.parkingapp.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.model.BookedPlace;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.api.CommonGoogleApi;
import www.fiberathome.com.parkingapp.model.api.IGoogleApi;
import www.fiberathome.com.parkingapp.model.data.AppConstants;
import www.fiberathome.com.parkingapp.model.data.Constants;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.response.BaseResponse;
import www.fiberathome.com.parkingapp.model.response.booking.BookingParkStatusResponse;
import www.fiberathome.com.parkingapp.model.response.booking.BookingSensors;
import www.fiberathome.com.parkingapp.model.response.booking.ReservationCancelResponse;
import www.fiberathome.com.parkingapp.model.response.booking.SensorAreaStatusResponse;
import www.fiberathome.com.parkingapp.model.response.parkingSlot.ParkingSlotResponse;
import www.fiberathome.com.parkingapp.model.response.search.SearchVisitorData;
import www.fiberathome.com.parkingapp.model.response.search.SelectedPlace;
import www.fiberathome.com.parkingapp.model.response.sensors.Sensor;
import www.fiberathome.com.parkingapp.model.response.sensors.SensorArea;
import www.fiberathome.com.parkingapp.model.response.sensors.SensorStatus;
import www.fiberathome.com.parkingapp.service.booking_service.BookingService;
import www.fiberathome.com.parkingapp.service.geoFenceInterface.IOnLoadLocationListener;
import www.fiberathome.com.parkingapp.service.geoFenceInterface.MyLatLng;
import www.fiberathome.com.parkingapp.service.googleService.directionModules.DirectionFinder;
import www.fiberathome.com.parkingapp.service.googleService.directionModules.DirectionFinderListener;
import www.fiberathome.com.parkingapp.service.notification.NotificationPublisher;
import www.fiberathome.com.parkingapp.ui.booking.BookingParkFragment;
import www.fiberathome.com.parkingapp.ui.bottomSheet.BottomSheetAdapter;
import www.fiberathome.com.parkingapp.ui.bottomSheet.CustomLinearLayoutManager;
import www.fiberathome.com.parkingapp.ui.schedule.ScheduleFragment;
import www.fiberathome.com.parkingapp.ui.search.SearchActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.GpsUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.MathUtils;
import www.fiberathome.com.parkingapp.utils.RecyclerTouchListener;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;
import www.fiberathome.com.parkingapp.utils.ViewUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class HomeFragment extends BaseFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener,
        IOnLoadLocationListener, GeoQueryEventListener,
        IOnBackPressListener, DirectionFinderListener,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener {

    private static boolean isBooked = false;
    private static String bookedUid;
    private static BookedPlace bookedPlace;
    private final String TAG = getClass().getSimpleName();

    public static final int GPS_REQUEST_CODE = 9003;
    public static final int PLAY_SERVICES_ERROR_CODE = 9002;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private HomeActivity context;

    public final ArrayList<LatLng> cordList = new ArrayList<>();

    public static Location currentLocation = null;
    public String address, city, state, country, subAdminArea, test, knownName, postalCode = "";
    public GoogleMap mMap;

    //used in fetchSensor()
    public double nDistance = 132116456;
    public double nLatitude;
    public double nLongitude;
    public int isRouteDrawn = 0;
    private FragmentChangeListener listener;
    private long arrived, departure;
    private TextView arrivedTimeTV, departureTimeTV, timeDifferenceTV, countDownTV, textViewTermsCondition;
    private long difference;
    protected final ArrayList<MarkerOptions> mMarkerArrayList = new ArrayList<>();
    private BottomSheetAdapter bottomSheetAdapter;
    private Marker currentLocationMarker;
    public BottomSheetBehavior<View> bottomSheetBehavior;
    private String nearByDuration;
    private String fetchDuration;
    private SupportMapFragment supportMapFragment;

    private String name, count = "";
    protected String distance;
    private boolean isGPS;
    private GoogleApiClient googleApiClient;
    protected String sensorStatus = "Occupied";

    //route flag
    private final int flag = 0;

    //polyline animate
    private List<LatLng> polyLineList;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, grayPolyline;
    private IGoogleApi mService;
    private String searchPlaceCount = "0";

    //geoFence
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentUser;
    private GeoFire geoFire;
    private Location lastLocation;
    private GeoQuery geoQuery;
    private boolean isInAreaEnabled;

    private String parkingNumberOfIndividualMarker = "";

    private Marker previousMarker = null;

    private Location onConnectedLocation;

    private final ArrayList<BookingSensors> bookingSensorsArrayList = new ArrayList<>();
    private final ArrayList<BookingSensors> bookingSensorsArrayListGlobal = new ArrayList<>();

    private final double adjustValue = 2;

    private LatLng origin;
    private Button moreBtn, btnLiveParking, departureBtn;

    private double adapterDistance;

    private String parkingAreaPlacedId;
    private Marker markerClicked;

    private boolean isNotificationSent = false;

    public Polyline polyline;

    public List<LatLng> points = new ArrayList<>();

    private double lat, lng;
    private String areaName, parkingSlotCount;

    public Marker previousGetDestinationMarker;
    private Location myPreviousLocation;
    public double oldTotalDistanceInKm, totalDistanceInKm;

    protected boolean firstDraw = true;
    private List<List<String>> list = new ArrayList<>();
    private List<List<String>> sensorAreaStatusList = new ArrayList<>();
    private ParkingSlotResponse parkingSlotResponse;
    private SensorAreaStatusResponse sensorAreaStatusResponse;
    private List<List<String>> parkingSlotList = new ArrayList<>();
    private List<List<String>> sensorStatusList = new ArrayList<>();
    private String parkingArea;
    private String placeId;
    private double endLat;
    private double endLng;
    private double fetchDistance;
    private final List<SensorArea> sensorAreaArrayList = new ArrayList<>();
    private boolean parkingAreaChanged = false;
    private boolean isAreaChangedForSearch = false;
    private String sensorAreaStatusAreaId, sensorAreaStatusTotalSensorCount, sensorAreaStatusTotalOccupied;
    private final List<SensorStatus> sensorStatusArrayList = new ArrayList<>();

    private boolean isBackClicked = false;
    private String previousOrigin;

    protected AlarmManager alarmManager;
    protected PendingIntent pendingIntent;
    protected PendingIntent pendingIntent2;
    protected PendingIntent pendingIntent3;

    FragmentHomeBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    private String destination = null;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
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

    public static HomeFragment newInstance(BookedPlace mBookedPlace) {
        bookedPlace = mBookedPlace;
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.e("onCreate called");
        setHasOptionsMenu(false);
        super.onCreate(savedInstanceState);
        context = (HomeActivity) getActivity();

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    static int requestCode;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Timber.e("onViewCreated called");
        super.onViewCreated(view, savedInstanceState);
        if (context != null) {
            context.changeDefaultActionBarDrawerToogleIcon();
            listener = context;
        }
        bookedPlace = Preferences.getInstance(context).getBooked();
        setBroadcast();
        try {
            if (isAdded()) {
                bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetLayout.layoutBottomSheet);
                bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._92sdp));
                bottomSheetBehavior.setHideable(false);
                bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View view, int newState) {
                        switch (newState) {
                            case BottomSheetBehavior.STATE_HIDDEN:
                            case BottomSheetBehavior.STATE_SETTLING:
                            case BottomSheetBehavior.STATE_HALF_EXPANDED:
                                break;
                            case BottomSheetBehavior.STATE_EXPANDED:
                                binding.bottomSheetLayout.layoutBottomSheet.requestLayout();
                                binding.bottomSheetLayout.layoutBottomSheet.invalidate();
                                binding.bottomSheetLayout.bottomSheetRecyclerView.smoothScrollToPosition(0);
                                toolbarAnimVisibility(view, false);
                                break;
                            case BottomSheetBehavior.STATE_COLLAPSED:
                                binding.bottomSheetLayout.bottomSheetRecyclerView.smoothScrollToPosition(0);
                                toolbarAnimVisibility(view, true);
                                if (bottomSheetAdapter != null)
                                    bottomSheetAdapter.onAttachedToRecyclerView(binding.bottomSheetLayout.bottomSheetRecyclerView);
                                break;
                            case BottomSheetBehavior.STATE_DRAGGING:
                                toolbarAnimVisibility(view, true);
                                if (bottomSheetAdapter != null)
                                    bottomSheetAdapter.onAttachedToRecyclerView(binding.bottomSheetLayout.bottomSheetRecyclerView);
                                break;
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View view, float slideOffset) {
                        if (isAdded()) {
                            Timber.e("onSlide called");
                        }
                    }
                });
                buildLocationRequest();
                buildLocationCallBack();
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

                if (mMap == null) {
                    showLoading(context, context.getResources().getString(R.string.please_wait));
                } else {
                    hideLoading();
                }

                if (isBooked && bookedPlace != null) {
                    oldDestination = "" + bookedPlace.getLat() + ", " + bookedPlace.getLon();
                    Timber.e("bookedPlace.getLat(), bookedPlace.getLon() -> %s, %s", bookedPlace.getLat(), bookedPlace.getLon());
                }

                if (isServicesOk()) {
                    supportMapFragment = SupportMapFragment.newInstance();

                    if (context != null) {
                        FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction().
                                replace(R.id.map, supportMapFragment);
                        ft.commit();
                        supportMapFragment.getMapAsync(this);
                    } else {
                        ToastUtils.getInstance().showToastMessage(context, "Unable to load map");
                    }
                } else {
                    ToastUtils.getInstance().showToastMessage(context, "Play services are required by this application");
                }

                polyLineList = new ArrayList<>();

                mService = CommonGoogleApi.getGoogleApi();
            }
        } catch (Resources.NotFoundException e) {
            e.getCause();
        }
    }

    private Marker pinMarker;

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Timber.e("onMapReady called");
        mMap = googleMap;

        if (Preferences.getInstance(context).getBooked() != null && Preferences.getInstance(context).getBooked().getIsBooked()) {
            setCircleOnLocation(new LatLng(Preferences.getInstance(context).getBooked().getLat(),
                    Preferences.getInstance(context).getBooked().getLon()));
        }

        hideLoading();
        defaultMapSettings(context, mMap, fusedLocationProviderClient, locationRequest, locationCallback);
        buildGoogleApiClient();

        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);
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

    private SensorArea markerTagObj;

    protected double myLocationChangedDistance;

    private void getDirectionPinMarkerDraw(LatLng pinPosition, String markerUid, boolean fromSearch) {
        if (pinMarker != null) {
            pinMarker.remove();
        }

        if (mMap != null) {
            pinMarker = mMap.addMarker(newMarkerPinInstance().position(pinPosition)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_pin))
                    .title(markerUid));
        }
        if (fromSearch) {
            isAreaChangedForSearch = true;
            parkingAreaChanged = false;
        } else {
            parkingAreaChanged = true;
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

    private void toolbarAnimVisibility(View view, boolean show) {
        Transition transition = new Fade();
        transition.setDuration(600);
        transition.addTarget(R.id.image);
        View toolbar = context.findViewById(R.id.toolbar);
        TransitionManager.beginDelayedTransition((ViewGroup) view, transition);
        toolbar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private String parkingAreaPlaceName = "";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onMarkerClick(Marker marker) {
        hideNoData();
        final String[] uid = {""};
        final String[] uid1 = {""};
        final String[] markerAreaName1 = {""};
        if (marker.getTag() != null)
            markerTagObj = (SensorArea) marker.getTag();

        try {
            if (markerTagObj != null) {
                Timber.e("marker if UID: -> %s", markerTagObj.getParkingArea());
            } else {
                Timber.e("marker else UID: did not work");
            }
        } catch (Exception e) {
            Timber.e(e.getCause());
            e.getCause();
        }

        try {
            if (currentLocationMarker != null && calculateDistance(currentLocationMarker.getPosition().latitude, currentLocationMarker.getPosition().longitude,
                    marker.getPosition().latitude, marker.getPosition().longitude) * 1000 <= 0.01) {
                double distance = calculateDistance(currentLocationMarker.getPosition().latitude, currentLocationMarker.getPosition().longitude,
                        marker.getPosition().latitude, marker.getPosition().longitude);

                marker.setTitle("My Location");

                isMyCurrentLocation = true;
            } else {
                isMyCurrentLocation = false;
            }
        } catch (Exception e) {
            e.getCause();
        }

        try {
            if (isGPSEnabled() && ConnectivityUtils.getInstance().checkInternet(context)) {
                bookingSensorsArrayList.clear();

                if (isRouteDrawn == 0) {
                    if (mMap != null) {

                        if (marker.getTitle() != null) {
                            if (!marker.getTitle().equals("My Location")) {
                                if (previousMarker != null) {
                                    previousMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                } else {
                                    Timber.e("else called");
                                }
                                previousMarker = marker;
                                removeCircle();
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_gray));
                                parkingAreaChanged = true;
                                cordList.add(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
                                setButtonText(context.getResources().getString(R.string.confirm_booking), context.getResources().getColor(R.color.black));
                                markerClicked = marker;
                                isNotificationSent = false;
                                isInAreaEnabled = false;
                            }
                        }
                        //calculate Duration
                        parkingSpotLatLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                        destination = "" + parkingSpotLatLng.latitude + ", " + parkingSpotLatLng.longitude;
                        binding.fabGetDirection.setVisibility(View.VISIBLE);
                        cordList.add(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));

                        if (!isMyCurrentLocation) {
                            populateNearestPlaceBottomSheet(parkingSpotLatLng);
                        }
                    }
                } else {
                    DialogUtils.getInstance().alertDialog(context,
                            context,
                            context.getString(R.string.you_have_to_exit_from_current_destination),
                            context.getString(R.string.yes), context.getString(R.string.no),
                            new DialogUtils.DialogClickListener() {
                                @Override
                                public void onPositiveClick() {
                                    if (marker.getTitle() != null) {
                                        if (!marker.getTitle().equals("My Location")) {
                                            if (previousMarker != null) {
                                                previousMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                            } else {
                                                Timber.e("else called");
                                            }
                                            previousMarker = marker;
                                            removeCircle();
                                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_gray));
                                            markerClicked = marker;
                                            parkingAreaChanged = true;
                                            isNotificationSent = false;
                                            isInAreaEnabled = false;
                                            searchPlaceCount = "";
                                            setButtonText(context.getResources().getString(R.string.confirm_booking), context.getResources().getColor(R.color.black));
                                        }
                                    }

                                    if (polyline == null || !polyline.isVisible())
                                        return;

                                    points = polyline.getPoints();

                                    if (polyline != null) {
                                        polyline.remove();
                                    }

                                    getDirectionPinMarkerDraw(marker.getPosition(), parkingAreaPlacedId, false);
                                    cordList.add(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));

                                    String origin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();

                                    binding.fabGetDirection.setVisibility(View.VISIBLE);
                                    parkingSpotLatLng = marker.getPosition();

                                    marker.getPosition();
                                    destination = "" + parkingSpotLatLng.latitude + ", " + parkingSpotLatLng.longitude;

                                    for (int i = 0; i < sensorAreaArrayList.size(); i++) {
                                        JSONObject jsonObject;
                                        SensorArea sensor = sensorAreaArrayList.get(i);
                                        try {
                                            uid1[0] = sensor.getPlaceId();
                                            markerAreaName1[0] = sensor.getParkingArea();
                                            double distanceForCount = calculateDistance(marker.getPosition().latitude, marker.getPosition().longitude,
                                                    sensor.getEndLat(), sensor.getEndLng());

                                            if (distanceForCount < 0.001) {
                                                parkingNumberOfIndividualMarker = sensor.getCount();
                                                binding.textViewParkingAreaCount.setText(parkingNumberOfIndividualMarker);
                                                break;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    String finalUid = uid1[0];

                                    String markerPlaceName = markerAreaName1[0];

                                    double markerDistance;

                                    markerDistance = calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                                            marker.getPosition().latitude, marker.getPosition().longitude);

                                    double kim = (markerDistance / 1000) + adjustValue;
                                    double markerDoubleDuration = MathUtils.getInstance().convertToDouble(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(markerDistance * 2.43));
                                    String markerStringDuration = String.valueOf(markerDoubleDuration);

                                    //showCurrentLocationButton();
                                    if (markerTagObj != null) {
                                        bookingSensorsArrayList.add(new BookingSensors(markerTagObj.getParkingArea(), marker.getPosition().latitude, marker.getPosition().longitude,
                                                markerDistance, markerTagObj.getCount(), markerStringDuration,
                                                context.getResources().getString(R.string.nearest_parking_from_your_destination),
                                                BookingSensors.TEXT_INFO_TYPE, 0));

                                        setBottomSheetList(() -> {
                                            if (bottomSheetAdapter != null) {
                                                bookingSensorsArrayListGlobal.clear();
                                                bookingSensorsArrayListGlobal.addAll(bookingSensorsArrayList);
                                                bottomSheetAdapter.notifyDataSetChanged();
                                            } else {
                                                Timber.e("bottomSheetAdapter null");
                                            }
                                        }, sensorAreaArrayList, marker.getPosition(), bookingSensorsArrayList, finalUid);
                                        bottomSheetAdapter.setDataList(bookingSensorsArrayList);
                                    }
                                }

                                @Override
                                public void onNegativeClick() {
                                    Timber.e("Negative Button Clicked");
                                }
                            }).show();
                }
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        } catch (Resources.NotFoundException e) {
            e.getCause();
        }

        return true;
    }

    private void populateNearestPlaceBottomSheet(LatLng latLng) {
        try {
            if (onConnectedLocation != null && latLng != null) {
                bookingSensorsArrayList.clear();
                parkingSpotLatLng = latLng;
                for (int i = 0; i < sensorAreaArrayList.size(); i++) {
                    SensorArea sensor = sensorAreaArrayList.get(i);
                    try {
                        parkingPlaceId = sensor.getPlaceId();
                        parkingAreaPlaceName = sensor.getParkingArea();
                        double distanceForCount = calculateDistance(parkingSpotLatLng.latitude, parkingSpotLatLng.longitude,
                                sensor.getEndLat(),
                                sensor.getEndLng());
                        if (distanceForCount < 0.001) {
                            parkingAreaPlacedId = parkingPlaceId;
                            Timber.e("markerUid -> %s", parkingAreaPlacedId);
                            parkingNumberOfIndividualMarker = sensor.getCount();
                            binding.textViewParkingAreaCount.setText(parkingNumberOfIndividualMarker);
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                double bottomSheetDistance = calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                        parkingSpotLatLng.latitude, parkingSpotLatLng.longitude);
                double bottomSheetDoubleDuration = MathUtils.getInstance().convertToDouble(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(bottomSheetDistance * 2.43));
                String bottomSheetStringDuration = String.valueOf(bottomSheetDoubleDuration);
                String occupied = null;

                for (SensorStatus status : sensorStatusArrayList) {
                    if (status.getAreaId().equalsIgnoreCase(place_id[0])) {
                        occupied = status.getOccupiedCount();
                    }
                }
                bookingSensorsArrayList.add(new BookingSensors(parkingAreaPlaceName, parkingSpotLatLng.latitude, parkingSpotLatLng.longitude,
                        bottomSheetDistance, occupied != null ? occupied + "/" + binding.textViewParkingAreaCount.getText().toString() : binding.textViewParkingAreaCount.getText().toString(), bottomSheetStringDuration,
                        context.getResources().getString(R.string.nearest_parking_from_your_destination),
                        BookingSensors.TEXT_INFO_TYPE, 0));
                setBottomSheetList(() -> {
                    if (bottomSheetAdapter != null) {
                        bookingSensorsArrayListGlobal.clear();
                        bookingSensorsArrayListGlobal.addAll(bookingSensorsArrayList);
                        bottomSheetAdapter.notifyDataSetChanged();
                        bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._142sdp));
                    } else {
                        Timber.e("bottomSheetAdapter null");
                    }
                }, sensorAreaArrayList, parkingSpotLatLng, bookingSensorsArrayList, parkingAreaPlacedId);
            }
        } catch (Resources.NotFoundException e) {
            e.getCause();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        //locationRequest.setSmallestDisplacement(10f); //100 meter
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                // ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        } catch (Exception e) {
            Timber.e(e.getCause());
            e.getCause();
        }

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            onConnectedLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            SharedData.getInstance().setOnConnectedLocation(onConnectedLocation);

        } catch (Exception e) {
            Timber.e(e.getCause());
            e.getCause();
        }

        try {
            if (mMap != null && onConnectedLocation != null) {
                LatLng latLng = new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.5f));
                animateCamera(onConnectedLocation);
                bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._92sdp));
                if (ConnectivityUtils.getInstance().checkInternet(context) && isGPSEnabled()) {
                    ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
                    Call<SensorAreaStatusResponse> call = request.getSensorAreaStatus();
                    getSensorAreaStatus(call);
                } else {
                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet_gps));
                }
            }
        } catch (Resources.NotFoundException e) {
            e.getCause();
        }
        try {
            buildLocationRequest();
            buildLocationCallBack();
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            settingGeoFire();
        } catch (Exception e) {
            Timber.e(e.getCause());
            e.getCause();
        }
    }

    private List<LatLng> initialRoutePoints;

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLocation = location;

        if (onConnectedLocation != null) {
            myLocationChangedDistance = calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(), location.getLatitude(), location.getLongitude()) * 1000;
        }

        onConnectedLocation = location;

        SharedData.getInstance().setOnConnectedLocation(location);

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }

        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_car_running))
                .title("My Location")
                .rotation(location.getBearing())
                .flat(false)
                .zIndex(1)
                .anchor(0.5f, 0.5f));


        if (markerClicked != null) {
            checkParkingSpotDistance(latLng, markerClicked.getPosition());
        } else if (parkingSpotLatLng != null) {
            checkParkingSpotDistance(latLng, parkingSpotLatLng);
        } else if (Preferences.getInstance(context).getBooked() != null && Preferences.getInstance(context).getBooked().getIsBooked()) {
            if (mMap != null) {
                checkParkingSpotDistance(latLng, new LatLng(Preferences.getInstance(context).getBooked().getLat(),
                        Preferences.getInstance(context).getBooked().getLon()));
            }
        }

        String origin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();

        try {
            if (isRouteDrawn == 1) {
                if (!(isBooked && bookedPlace != null)) {
                    if (previousOrigin != null && oldDestination != null && onConnectedLocation != null) {
                        String[] latlong = previousOrigin.split(",");
                        String[] latlong2 = oldDestination.split(",");
                        if (!oldDestination.equals("")) {
                            double lat = MathUtils.getInstance().convertToDouble(latlong[0].trim());
                            double lon = MathUtils.getInstance().convertToDouble(latlong[1].trim());
                            double lat2 = MathUtils.getInstance().convertToDouble(latlong2[0].trim());
                            double lon2 = MathUtils.getInstance().convertToDouble(latlong2[1].trim());
                            double distanceMoved = calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(), lat, lon) * 1000;
                            double distanceFromDestination = calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(), lat2, lon2) * 1000;
                            if (distanceMoved >= 100) {
                                reDrawRoute(origin);
                                previousOrigin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();
                            } else if ((distanceFromDestination <= 20 && distanceFromDestination >= 10) && distanceMoved >= 9) {
                                reDrawRoute(origin);
                                previousOrigin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();
                            }
                        } else {
                            previousOrigin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();
                        }
                    }
                }
            }
            /*else if (isBooked && bookedPlace != null) {
                Gson gson = new Gson();
                String json = bookedPlace.getRoute();
                Type type = new TypeToken<List<LatLng>>() {
                }.getType();
                if (json != null && json.length() > 1 && !json.equals("")) {
                    if (polyline != null) {
                        polyline.remove();
                        polyline = mMap.addPolyline(getDefaultPolyLines(gson.fromJson(json, type)));
                        isRouteDrawn = 1;
                        getDirectionPinMarkerDraw(new LatLng(bookedPlace.getLat(), bookedPlace.getLon()), bookedPlace.getBookedUid(), false);
                        zoomRoute(mMap, polyline.getPoints());
                    }
                }
            }*/
        } catch (JsonSyntaxException e) {
            e.getCause();
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

    private void reDrawRoute(String origin) {
        Timber.e("reDrawRoute called");

        String[] latLng = origin.split(",");
        double lat = Double.parseDouble(latLng[0]);
        double lon = Double.parseDouble(latLng[1]);

        if (origin.isEmpty() || oldDestination.isEmpty()) {
            Timber.e("Please first fill all the fields!");
            return;
        }

        if (!origin.contains(",") || !oldDestination.contains(",")) {
            Timber.e("Invalid data fill in fields!");
            return;
        }

        try {
            if (polyline == null || !polyline.isVisible())
                return;

            if (polyline != null)
                polyline.remove();

            new DirectionFinder(this, origin, oldDestination).execute();
            hideLoading();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            hideLoading();
        }
    }

    @Override
    public void onStart() {
        Timber.e("onStart called");
        super.onStart();
        isBooked = Preferences.getInstance(context).getBooked().getIsBooked();
        if (isBooked) {
            startBookingTrackService();
        } else {
            stopBookingTrackService();
        }
    }

    @Override
    public void onResume() {
        Timber.e("onResume called");
        super.onResume();
        bookedPlace = Preferences.getInstance(context).getBooked();
        isBooked = Preferences.getInstance(context).getBooked().getIsBooked();
        if (!isBooked && parkingSpotLatLng == null) {
            commonBackOperation();
        }
        if (isBooked) {
            binding.fabGetDirection.setVisibility(View.VISIBLE);
        }
        if (isAdded())
            setListeners();
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
        super.onDestroyView();
    }

    private boolean isLocationTrackingServiceRunning() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                String a = Service.class.getName();
                serviceInfo.service.getClassName();
                if (serviceInfo.foreground) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }


    private void startBookingTrackService() {
        if (!isLocationTrackingServiceRunning()) {
            Intent intent = new Intent(context, BookingService.class);
            intent.setAction(Constants.START_BOOKING_TRACKING);
            context.startService(intent);
        }
    }

    private void stopBookingTrackService() {
//        if (isLocationTrackingServiceRunning()) {
        Intent intent = new Intent(context, BookingService.class);
        intent.setAction(Constants.STOP_BOOKING_TRACKING);
        context.startService(intent);
//        }
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
                sendNotification("You are near a parking spot", "You can book parking");

                if (isBooked && bookedPlace != null) {
                    destination = "" + bookedPlace.getLat() + ", " + bookedPlace.getLon();
                    populateNearestPlaceBottomSheet(new LatLng(bookedPlace.getLat(), bookedPlace.getLon()));
                    setButtonText(context.getResources().getString(R.string.park), context.getResources().getColor(R.color.black));
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppConstants.GPS_REQUEST && resultCode == RESULT_OK) {
            isGPS = true;// flag maintain before get location
            Timber.e("isGPS -> %s", isGPS);
        }

        if (requestCode == NEW_SEARCH_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            bookingSensorsArrayList.clear();
            searchPlaceCount = "0";
            binding.fabGetDirection.setVisibility(View.VISIBLE);
            parkingAreaChanged = false;
            if (searchPlaceCount.equals("0")) {
                setButtonText(context.getResources().getString(R.string.unavailable_parking_spot), context.getResources().getColor(R.color.gray3));
            }
            //new search result
            SelectedPlace selectedPlace = (SelectedPlace) data.getSerializableExtra(NEW_PLACE_SELECTED); //This line may produce null point exception

            if (selectedPlace != null) {
                previousMarker = null;
                hideNoData();
                parkingSpotLatLng = new LatLng(selectedPlace.getLatitude(), selectedPlace.getLongitude());
                destination = "" + parkingSpotLatLng.latitude + ", " + parkingSpotLatLng.longitude;

                String areaName = selectedPlace.getAreaName();
                String areaAddress = selectedPlace.getAreaAddress();
                String placeId = selectedPlace.getPlaceId();

                //store visited place
                storeVisitedPlace(Preferences.getInstance(context).getUser().getMobileNo(), placeId,
                        selectedPlace.getLatitude(), selectedPlace.getLongitude(),
                        onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(), areaName);

                plotSearchAddressData(areaName, placeId, parkingSpotLatLng, searchPlaceCount);
            }

            //search history result
            SearchVisitorData searchVisitorData = (SearchVisitorData) data.getSerializableExtra(HISTORY_PLACE_SELECTED);
            if (searchVisitorData != null) {
                previousMarker = null;
                hideNoData();
                parkingSpotLatLng = new LatLng(searchVisitorData.getEndLat(), searchVisitorData.getEndLng());
                destination = "" + parkingSpotLatLng.latitude + ", " + parkingSpotLatLng.longitude;

                String areaName = searchVisitorData.getVisitedArea();
                String placeId = searchVisitorData.getPlaceId();

                plotSearchAddressData(areaName, placeId, parkingSpotLatLng, searchPlaceCount);
            }
        }

        if (requestCode == GPS_REQUEST_CODE) {

            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            showLoading(context, context.getResources().getString(R.string.enabling_gps));

            new Handler().postDelayed(() -> {
                hideLoading();

                if (providerEnabled) {
                    ToastUtils.getInstance().showToastMessage(context,
                            context.getResources().getString(R.string.gps_enabled));
                    Timber.e("providerEnabled HomeFragment check called");

                    supportMapFragment = SupportMapFragment.newInstance();

                    if (context != null) {
                        FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction().
                                replace(R.id.map, supportMapFragment);
                        ft.commit();
                        supportMapFragment.getMapAsync(HomeFragment.this);
                        ApplicationUtils.reLoadFragment(getParentFragmentManager(), HomeFragment.this);
                    } else {
                        ToastUtils.getInstance().showToastMessage(context, "Enable your Gps Location");
                    }

                    showLoading(context);

                    new GpsUtils(context).turnGPSOn(isGPSEnable -> {
                        // turn on GPS
                        isGPS = isGPSEnable;
                    });

                    if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                        requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    }
                } else {
                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.gps_network_not_enabled));
                }
            }, 6000);
        }
    }

    private void plotSearchAddressData(String areaName, String placeId, LatLng parkingSpotLatLng, String searchPlaceCount) {
        binding.buttonSearch.setVisibility(View.GONE);
        binding.linearLayoutBottom.setVisibility(View.VISIBLE);
        binding.btnConfirmBooking.setVisibility(View.VISIBLE);
        binding.btnConfirmBooking.setEnabled(true);
        binding.imageViewBack.setVisibility(View.VISIBLE);

        cordList.add(new LatLng(parkingSpotLatLng.latitude, parkingSpotLatLng.longitude));
        getDirectionPinMarkerDraw(parkingSpotLatLng, "", true);

        double searchDistance;

        if (mMap != null) {
            //move map camera
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(parkingSpotLatLng, 13.5f), 500, null);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parkingSpotLatLng, 13.5f));
        }

        if (onConnectedLocation != null) {
            searchDistance = calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                    parkingSpotLatLng.latitude, parkingSpotLatLng.longitude);

            layoutVisible(true, areaName, searchPlaceCount,
                    binding.textViewParkingDistance.getText().toString(), parkingSpotLatLng);

            bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._142sdp));

            double searchDoubleDuration = MathUtils.getInstance().convertToDouble(new DecimalFormat("##.#",
                    new DecimalFormatSymbols(Locale.US)).format(searchDistance * 2.43));

            String searchStringDuration = String.valueOf(searchDoubleDuration);

            bookingSensorsArrayList.add(new BookingSensors(areaName, parkingSpotLatLng.latitude, parkingSpotLatLng.longitude,
                    searchDistance, searchPlaceCount, searchStringDuration,
                    context.getResources().getString(R.string.nearest_parking_from_your_destination),
                    BookingSensors.TEXT_INFO_TYPE, 0));
            for (int i = 0; i < sensorAreaArrayList.size(); i++) {
                Timber.e("SensorAreaArrayListSearch ->%s", new Gson().toJson(sensorAreaArrayList.get(i)));
                SensorArea sensor = sensorAreaArrayList.get(i);
                String nearestSearchAreaName = sensor.getParkingArea();

                double distanceForNearbyLoc = calculateDistance(parkingSpotLatLng.latitude, parkingSpotLatLng.longitude,
                        sensor.getEndLat(), sensor.getEndLng());

                if (distanceForNearbyLoc < 5) {
                    origin = new LatLng(parkingSpotLatLng.latitude, parkingSpotLatLng.longitude);

                    String parkingNumberOfNearbyDistanceLoc = sensor.getCount();

                    double nearbySearchDoubleDuration = MathUtils.getInstance().convertToDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(distanceForNearbyLoc * 2.43));

                    String nearbySearchStringDuration = String.valueOf(nearbySearchDoubleDuration);

                    bookingSensorsArrayList.add(new BookingSensors(nearestSearchAreaName,
                            sensor.getEndLat(),
                            sensor.getEndLng(), adjustDistance(distanceForNearbyLoc), parkingNumberOfNearbyDistanceLoc,
                            nearbySearchStringDuration,
                            BookingSensors.INFO_TYPE, 1));

                    bubbleSortArrayList(bookingSensorsArrayList);
                }
                if (bottomSheetAdapter != null) {
                    bookingSensorsArrayListGlobal.clear();
                    bookingSensorsArrayListGlobal.addAll(bookingSensorsArrayList);
                    bottomSheetAdapter.notifyDataSetChanged();
                }
            }
        } else {
            ToastUtils.getInstance().showToastMessage(context, "Location cannot be identified!!!");
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

                if (isLocationEnabled(context)) {
                    supportMapFragment.getMapAsync(this);
                }
            } else if (grantResults.length == FIRST_TIME_INSTALLED && context != null) {
                if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
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
        return new CameraPosition.Builder().target(latLng).zoom(13.8f).build();
    }

    @Override
    public void onCameraMoveStarted(int reason) {

        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            Timber.e("OnCameraMoveStartedListener called");

        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_API_ANIMATION) {
            Timber.e("OnCameraMoveStartedListener called");

        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_DEVELOPER_ANIMATION) {
            Timber.e("OnCameraMoveStartedListener called");
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

    private void setBroadcast() {
        LocalBroadcastManager.getInstance(context).registerReceiver(bookingEndedReceiver,
                new IntentFilter("booking_ended"));
    }

    private final BroadcastReceiver bookingEndedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            if (isAdded() && mMap != null) {
                try {
                    commonBackOperation();
                    Timber.e("BroadcastReceiver called");
                } catch (Exception e) {
                    Timber.e(e.getCause());
                    e.getCause();
                }
            }
        }
    };

    private void getSensorAreaStatus(Call<SensorAreaStatusResponse> call) {
        showLoading(context);
        call.enqueue(new Callback<SensorAreaStatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<SensorAreaStatusResponse> call,
                                   @NonNull retrofit2.Response<SensorAreaStatusResponse> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!response.body().getError()) {
                            if (response.body().getSensorAreaStatusArrayList() != null) {
                                sensorAreaStatusList = response.body().getSensorAreaStatusArrayList();

                                sensorAreaStatusResponse = response.body();
                                sensorStatusList = sensorAreaStatusResponse.getSensorAreaStatusArrayList();
                                if (sensorStatusList != null) {
                                    for (List<String> baseStringList : sensorStatusList) {
                                        for (int i = 0; i < baseStringList.size(); i++) {
                                            sensorAreaStatusAreaId = baseStringList.get(0);
                                            sensorAreaStatusTotalSensorCount = baseStringList.get(1);
                                            sensorAreaStatusTotalOccupied = baseStringList.get(2);

                                            SensorStatus sensorStatus = new SensorStatus();
                                            sensorStatus.setAreaId(sensorAreaStatusAreaId);
                                            sensorStatus.setTotalCount(sensorAreaStatusTotalSensorCount);
                                            sensorStatus.setOccupiedCount(sensorAreaStatusTotalOccupied);
                                            sensorStatusArrayList.add(sensorStatus);
                                        }
                                    }
                                    commonBackOperation();
                                }
                            } else {
                                commonBackOperation();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SensorAreaStatusResponse> call, @NonNull Throwable t) {
                Timber.e("onFailure -> %s", t.getMessage());
                hideLoading();
            }
        });
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

    private synchronized void setBottomSheetList(SetBottomSheetCallBack setBottomSheetCallBack, List<SensorArea> sensorArrayList,
                                                 LatLng latLng, ArrayList<BookingSensors> bookingSensorsArrayList, String markerUid) {
        final int[] count = {0};
        int count2 = sensorArrayList.size();
        for (int i = 0; i < sensorArrayList.size(); i++) {

            SensorArea sensor = sensorArrayList.get(i);
            try {
                String uid = sensor.getPlaceId();

                String parkingArea = sensor.getParkingArea();

                double distanceForNearbyLoc = calculateDistance(latLng.latitude, latLng.longitude,
                        sensor.getEndLat(), sensor.getEndLng());

                final String[] nearbyAreaName = {""};

                if (distanceForNearbyLoc < 5 && !markerUid.equals(uid)) {
                    origin = new LatLng(latLng.latitude, latLng.longitude);

                    nearbyAreaName[0] = parkingArea;

                    String parkingNumberOfNearbyDistanceLoc = null;
                    try {
                        parkingNumberOfNearbyDistanceLoc = sensor.getCount();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    count[0]++;

                    double nearbySearchDoubleDuration = MathUtils.getInstance().convertToDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(distanceForNearbyLoc * 2.43));

                    String nearbySearchStringDuration = String.valueOf(nearbySearchDoubleDuration);

                    bookingSensorsArrayList.add(new BookingSensors(nearbyAreaName[0], sensor.getEndLat(),
                            sensor.getEndLng(), adjustDistance(distanceForNearbyLoc), sensor.getOccupiedCount() != null ? sensor.getOccupiedCount() + "/" + parkingNumberOfNearbyDistanceLoc : parkingNumberOfNearbyDistanceLoc,
                            nearbySearchStringDuration,
                            BookingSensors.INFO_TYPE, 1));

                    bubbleSortArrayList(bookingSensorsArrayList);

                    setBottomSheetCallBack.setBottomSheet();
                    bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._142sdp));
                    if (parkingSpotLatLng != null) {
                        binding.linearLayoutBottom.setVisibility(View.VISIBLE);
                        binding.linearLayoutParkingAdapterBackBottom.setVisibility(View.VISIBLE);
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

    private final List<Sensor> sensorArrayList = new ArrayList<>();
    private final List<Sensor> sensorInitialArrayList = new ArrayList<>();

    private void fetchParkingSlotSensors(Location location) {
        Timber.e("fetchParkingSlotSensors called");
        this.onConnectedLocation = location;
        showLoading(context);
        startShimmer();
        bookingSensorsArrayListGlobal.clear();
        sensorAreaArrayList.clear();

        ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<ParkingSlotResponse> call = request.getParkingSlots();
        call.enqueue(new Callback<ParkingSlotResponse>() {
            @Override
            public void onResponse(@NonNull Call<ParkingSlotResponse> call,
                                   @NonNull retrofit2.Response<ParkingSlotResponse> response) {
                hideLoading();
                stopShimmer();
                if (response.body() != null) {
                    list = response.body().getSensors();
                    parkingSlotResponse = response.body();
                    parkingSlotList = parkingSlotResponse.getSensors();
                    if (parkingSlotList != null) {
                        for (List<String> baseStringList : parkingSlotList) {
                            for (int i = 0; i < baseStringList.size(); i++) {

                                if (i == 1) {
                                    parkingArea = baseStringList.get(i);
                                }

                                if (i == 0) {
                                    placeId = baseStringList.get(i);
                                }

                                if (i == 2) {
                                    endLat = Double.parseDouble(baseStringList.get(i).trim());
                                }

                                if (i == 3) {
                                    endLng = Double.parseDouble(baseStringList.get(i).trim());
                                }

                                if (i == 4) {
                                    count = baseStringList.get(i);
                                }

                                fetchDistance = MathUtils.getInstance().calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                                        endLat, endLng);

                                if (fetchDistance > 1.9) {
                                    fetchDistance = fetchDistance + 2;
                                } else if (fetchDistance < 1.9 && fetchDistance > 1) {
                                    fetchDistance = fetchDistance + 1;
                                } else {
                                    fetchDistance = fetchDistance + 0.5;
                                }
                            }

                            SensorArea sensorArea = new SensorArea(parkingArea, placeId, endLat, endLng, count,
                                    fetchDistance);

                            sensorAreaArrayList.add(sensorArea);
                        }

                        Collections.sort(sensorAreaArrayList, (c1, c2) -> Double.compare(c1.getDistance(), c2.getDistance()));

                        for (int i = 0; i < sensorAreaArrayList.size(); i++) {
                            renderParkingSensors(sensorAreaArrayList.get(i), location);
                        }

                        new Handler().postDelayed(() -> {
                            if (isGPSEnabled() && ConnectivityUtils.getInstance().checkInternet(context)) {
                                isBooked = Preferences.getInstance(context).getBooked().getIsBooked();
                                if (isBooked && bookedPlace != null) {
                                    parkingSpotLatLng = new LatLng(bookedPlace.getLat(), bookedPlace.getLon());
                                    destination = "" + parkingSpotLatLng.latitude + ", " + parkingSpotLatLng.longitude;
                                    populateNearestPlaceBottomSheet(parkingSpotLatLng);
                                    if (!isInAreaEnabled)
                                        setButtonText(context.getResources().getString(R.string.park), context.getResources().getColor(R.color.gray3));
                                    hideNoData();
                                    binding.buttonSearch.setVisibility(View.GONE);
                                } else if (lat != 0 && lng != 0 && areaName != null && !areaName.equalsIgnoreCase("") && parkingSlotCount != null) {
                                    parkingSpotLatLng = new LatLng(lat, lng);
                                    binding.fabGetDirection.setVisibility(View.VISIBLE);
                                    hideNoData();
                                    getDirectionPinMarkerDraw(new LatLng(lat, lng), parkingAreaPlacedId, false);
                                    cordList.add(new LatLng(lat, lng));
                                    destination = "" + parkingSpotLatLng.latitude + ", " + parkingSpotLatLng.longitude;
                                    populateNearestPlaceBottomSheet(parkingSpotLatLng);
                                    setButtonText(context.getResources().getString(R.string.confirm_booking), context.getResources().getColor(R.color.black));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 13.5f), 500, null);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 13.5f));
                                }
                            } else {
                                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
                            }
                        }, 1000);
                        setBottomSheetFragmentControls(bookingSensorsArrayListGlobal);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ParkingSlotResponse> call, @NonNull Throwable t) {
                Timber.e("onFailure -> %s", t.getMessage());
                hideLoading();
                t.getCause();
            }
        });
    }

    private void setButtonText(String text, int color) {
        binding.btnConfirmBooking.setText(text);
        binding.btnConfirmBooking.setBackgroundColor(color);
    }

    private void renderParkingSensors(SensorArea sensor, Location location) {
        String areaName = sensor.getParkingArea();
        String parkingCount = sensor.getCount();
        double latitude = sensor.getEndLat();
        double longitude = sensor.getEndLng();
        double tDistance = calculateDistance(latitude, longitude, location.getLatitude(), location.getLongitude());
        if (tDistance < nDistance) {
            nDistance = tDistance;
            nLatitude = latitude;
            nLongitude = longitude;
        }
        sensorStatus = "Empty";
        if (mMap != null) {
            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(sensor.getPlaceId())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
            Marker marker1 = mMap.addMarker(marker);
            assert marker1 != null;
            marker1.setTag(sensor);
            mMarkerArrayList.add(marker);
        }
        double fetchDistance = calculateDistance(location.getLatitude(), location.getLongitude(),
                latitude, longitude);
        double doubleDuration = MathUtils.getInstance().convertToDouble(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(fetchDistance * 2.43));
        String initialNearestDuration = String.valueOf(doubleDuration);
        if (fetchDistance < 7) {
            if (!sensorStatusArrayList.isEmpty()) {
                for (SensorStatus status : sensorStatusArrayList) {
                    if (sensor.getPlaceId().equalsIgnoreCase(status.getAreaId())) {
                        sensor.setOccupiedCount(status.getOccupiedCount());
                        break;
                    }
                }
            }
            origin = new LatLng(location.getLatitude(), location.getLongitude());
            bookingSensorsArrayListGlobal.add(new BookingSensors(areaName, latitude, longitude,
                    adjustDistance(fetchDistance), sensor.getOccupiedCount() != null ? sensor.getOccupiedCount() + "/" + parkingCount : parkingCount, initialNearestDuration,
                    BookingSensors.INFO_TYPE, 1));
            //fetch distance in ascending order
            Collections.sort(bookingSensorsArrayListGlobal, (BookingSensors c1, BookingSensors c2) -> Double.compare(c1.getDistance(), c2.getDistance()));
        }
    }

    @SuppressLint("SetTextI18n")
    private void setBottomSheetFragmentControls(ArrayList<BookingSensors> sensors) {
        binding.bottomSheetLayout.bottomSheetRecyclerView.setHasFixedSize(false);
        binding.bottomSheetLayout.bottomSheetRecyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new CustomLinearLayoutManager(getActivity());
        binding.bottomSheetLayout.bottomSheetRecyclerView.setLayoutManager(mLayoutManager);
        binding.bottomSheetLayout.bottomSheetRecyclerView.addItemDecoration(new DividerItemDecoration(context, CustomLinearLayoutManager.VERTICAL));
        binding.bottomSheetLayout.bottomSheetRecyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.bottomSheetLayout.bottomSheetRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), binding.bottomSheetLayout.bottomSheetRecyclerView,
                new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Timber.e("bottomSheetRecyclerView smoothScrollToPosition called");
                        binding.bottomSheetLayout.bottomSheetRecyclerView.smoothScrollToPosition(0);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
        ViewCompat.setNestedScrollingEnabled(binding.bottomSheetLayout.bottomSheetRecyclerView, false);
        setBottomSheetRecyclerViewAdapter(sensors);
    }

    String parkingPlaceId = "";

    final String[] place_id = {""};

    private void setBottomSheetRecyclerViewAdapter(ArrayList<BookingSensors> bookingSensors) {
        bottomSheetAdapter = null;
        bottomSheetAdapter = new BottomSheetAdapter(context, this, onConnectedLocation, (BookingSensors sensors) -> {
            bookingSensorsArrayList.clear();
            bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._142sdp));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            parkingSpotLatLng = new LatLng(sensors.getLat(), sensors.getLng());
            destination = "" + parkingSpotLatLng.latitude + ", " + parkingSpotLatLng.longitude;
            binding.fabGetDirection.setVisibility(View.VISIBLE);
            hideNoData();
            try {
                if (isRouteDrawn == 0) {
                    //for getting the location name
                    if (bottomSheetAdapter != null) {
                        getDirectionPinMarkerDraw(parkingSpotLatLng, parkingAreaPlacedId, false);
                        cordList.add(new LatLng(parkingSpotLatLng.latitude, parkingSpotLatLng.longitude));
                        //move map camera
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(parkingSpotLatLng.latitude, parkingSpotLatLng.longitude), 13.5f), 500, null);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(parkingSpotLatLng.latitude, parkingSpotLatLng.longitude), 13.5f));
                    }
                    populateNearestPlaceBottomSheet(parkingSpotLatLng);
                    searchPlaceCount = "";
                    setButtonText(context.getResources().getString(R.string.confirm_booking), context.getResources().getColor(R.color.black));
                } else {
                    DialogUtils.getInstance().alertDialog(context,
                            context,
                            context.getString(R.string.you_have_to_exit_from_current_destination),
                            context.getString(R.string.yes),
                            context.getString(R.string.no),
                            new DialogUtils.DialogClickListener() {
                                @Override
                                public void onPositiveClick() {
                                    if (polyline == null || !polyline.isVisible())
                                        return;
                                    points = polyline.getPoints();
                                    if (polyline != null) {
                                        polyline.remove();
                                    }
                                    if (parkingSpotLatLng != null) {
                                        getDirectionPinMarkerDraw(parkingSpotLatLng, parkingAreaPlacedId, false);
                                        cordList.add(new LatLng(parkingSpotLatLng.latitude, parkingSpotLatLng.longitude));
                                        //move map camera
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(parkingSpotLatLng.latitude, parkingSpotLatLng.longitude), 13.5f), 500, null);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(parkingSpotLatLng.latitude, parkingSpotLatLng.longitude), 13.5f));
                                    }
                                    searchPlaceCount = "";
                                    binding.btnConfirmBooking.setVisibility(View.VISIBLE);
                                    setButtonText(context.getResources().getString(R.string.confirm_booking), context.getResources().getColor(R.color.black));
                                    String origin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();
                                    if (parkingSpotLatLng != null) {
                                        destination = "" + parkingSpotLatLng.latitude + ", " + parkingSpotLatLng.longitude;
                                    }
                                    bookingSensorsArrayList.clear();
                                    String bottomSheetPlaceName = null;
                                    populateNearestPlaceBottomSheet(parkingSpotLatLng);
                                }

                                @Override
                                public void onNegativeClick() {
                                    Timber.e("Negative Button Clicked");
                                }
                            }).show();
                }
            } catch (Resources.NotFoundException e) {
                e.getCause();
            }
        });
        bottomSheetAdapter.setDataList(bookingSensors);
        binding.bottomSheetLayout.bottomSheetRecyclerView.setAdapter(bottomSheetAdapter);
        if (bookingSensors.size() == 0) {
            setNoData();
        } else {
            hideNoData();
        }
    }

    private double calculateDistance(Double startLatitude, Double startLongitude, Double endLatitude, Double endLongitude) {
        return MathUtils.getInstance().calculateDistance(startLatitude, startLongitude, endLatitude, endLongitude);
    }

    private void storeVisitedPlace(String mobileNo, String placeId, double endLatitude, double endLongitude,
                                   double startLatitude, double startLongitude, String areaAddress) {
        ApiService service = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<BaseResponse> sensorsCall = service.storeSearchHistory(mobileNo, placeId, String.valueOf(endLatitude),
                String.valueOf(endLongitude), String.valueOf(startLatitude), String.valueOf(startLongitude), String.valueOf(areaAddress));
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

    //getting the direction url
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Key
        String key = "key=" + context.getResources().getString(R.string.google_maps_key);
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
                            assert response.body() != null;
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
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        ToastUtils.getInstance().showToastMessage(context, "Error: " + t.getMessage());
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
        int top = ViewUtils.getInstance().getToolBarHeight(context);
        int bottom = (int) context.getResources().getDimension(R.dimen._200sdp);
        LatLngBounds latLngBounds = boundsBuilder.build();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
        googleMap.setPadding(left, top, right, bottom);
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
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

    private LatLng parkingSpotLatLng;

    @SuppressLint("SetTextI18n")
    private void layoutVisible(boolean isVisible, String name, String count,
                               String distance, LatLng location) {
        this.areaName = name;
        this.parkingSlotCount = count;
        this.distance = distance;
        this.parkingSpotLatLng = location;
        if (isVisible && isAdded()) {
            try {
                binding.linearLayoutNameCount.setVisibility(View.GONE);
                binding.linearLayoutBottom.setVisibility(View.VISIBLE);
                binding.btnConfirmBooking.setEnabled(true);
                binding.btnConfirmBooking.setFocusable(true);
            } catch (Exception e) {
                Timber.e("catch called");
                e.getCause();
            }
        } else {
            Timber.e("else called");
        }
    }

    public void commonBackOperation() {
        try {
            if (isGPSEnabled() && ConnectivityUtils.getInstance().checkInternet(context)) {
                if (mMap != null && isAdded()) {
                    if (polyline != null) {
                        polyline.remove();
                        polyline = null;
                    }
                    if (pinMarker != null) {
                        pinMarker.remove();
                    }
                    if (!isBooked)
                        binding.fabGetDirection.setVisibility(View.GONE);
                    mMap.clear();
                    mMap.setTrafficEnabled(true);
                    parkingAreaChanged = false;
                    previousGetDestinationMarker = null;
                    previousMarker = null;
                    isRouteDrawn = 0;
                    oldDestination = "";
                    searchPlaceCount = "";
                    if (bottomSheetAdapter != null) {
                        bottomSheetAdapter.clear();
                        bottomSheetAdapter = null;
                    }
                    bookingSensorsArrayList.clear();
                    if (onConnectedLocation != null) {
                        fetchParkingSlotSensors(onConnectedLocation);
                        animateCamera(onConnectedLocation);
                    }
                    binding.linearLayoutBottom.setVisibility(View.GONE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._92sdp));
                    binding.buttonSearch.setText(null);
                    binding.buttonSearch.setVisibility(View.VISIBLE);
                    if (isBackClicked) {
                        if (getArguments() != null) {
                            getArguments().clear();
                            areaName = null;
                        }
                        if (initialRoutePoints != null)
                            initialRoutePoints.clear();
                    }
                    try {
                        if (polyline == null || !polyline.isVisible())
                            return;
                        points = polyline.getPoints();
                        polyline.remove();
                    } catch (Exception e) {
                        Timber.e("catch called");
                        e.getCause();
                    }
                }
            } else {
                DialogUtils.getInstance().alertDialog(context,
                        context,
                        context.getString(R.string.connect_to_internet),
                        context.getString(R.string.retry),
                        context.getString(R.string.close_app),
                        new DialogUtils.DialogClickListener() {
                            @Override
                            public void onPositiveClick() {
                                Timber.e("Positive Button clicked");
                                if (ConnectivityUtils.getInstance().checkInternet(context)) {
                                    fetchParkingSlotSensors(onConnectedLocation);
                                } else {
                                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                                }
                            }

                            @Override
                            public void onNegativeClick() {
                                Timber.e("Negative Button Clicked");
                                if (getActivity() != null) {
                                    getActivity().finish();
                                    TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                                }
                            }
                        }).show();
            }
        } catch (Resources.NotFoundException e) {
            e.getCause();
        }
    }

    private void setListeners() {

        binding.fabCurrentLocation.setOnClickListener(v -> {
            if (mMap != null && onConnectedLocation != null)
                animateCamera(onConnectedLocation);
        });

        binding.fabGetDirection.setOnClickListener(v -> {
            try {
                String origin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();
                mMap.setTrafficEnabled(false);
                if (isBooked && !parkingAreaChanged) {
                    String bookedDestination = "" + bookedPlace.getLat() + ", " + bookedPlace.getLon();
                    fetchDirections(origin, bookedDestination);
                    getDirectionPinMarkerDraw(new LatLng(bookedPlace.getLat(), bookedPlace.getLon()), bookedPlace.getPlaceId(), false);
                } else {
                    if (destination != null && parkingAreaChanged)
                        fetchDirections(origin, destination);
                    getDirectionPinMarkerDraw(parkingSpotLatLng, parkingAreaPlacedId, false);
                }
            } catch (Exception e) {
                Timber.e(e.getCause());
                e.getCause();
            }
        });

        binding.buttonSearch.setOnClickListener(v -> {
            if (isGPSEnabled() && ConnectivityUtils.getInstance().checkInternet(context)) {
                Intent intent = new Intent(context, SearchActivity.class);
                startActivityForResult(intent, NEW_SEARCH_ACTIVITY_REQUEST_CODE);
                if (mMap != null)
                    mMap.clear();
                if (parkingSpotLatLng != null) {
                    bookingSensorsArrayList.clear();
                }
                bookingSensorsArrayListGlobal.clear();
                bookingSensorsArrayList.clear();
                //get sensor area status
                ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
                Call<SensorAreaStatusResponse> call = request.getSensorAreaStatus();
                getSensorAreaStatus(call);
                animateCamera(onConnectedLocation);
                bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._92sdp));
                binding.buttonSearch.setText("");
                binding.linearLayoutBottom.setVisibility(View.GONE);
            } else {
                DialogUtils.getInstance().alertDialog(context,
                        context,
                        context.getString(R.string.connect_to_internet),
                        context.getString(R.string.retry),
                        context.getString(R.string.close_app),
                        new DialogUtils.DialogClickListener() {
                            @Override
                            public void onPositiveClick() {
                                Timber.e("Positive Button clicked");
                                if (ConnectivityUtils.getInstance().checkInternet(context)) {
                                    ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
                                    Call<SensorAreaStatusResponse> call = request.getSensorAreaStatus();
                                    getSensorAreaStatus(call);
                                } else {
                                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                                }
                            }

                            @Override
                            public void onNegativeClick() {
                                Timber.e("Negative Button Clicked");
                                if (getActivity() != null) {
                                    getActivity().finish();
                                    TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                                }
                            }
                        }).show();
            }
        });

        binding.imageViewBack.setOnClickListener(v -> {
            isBackClicked = true;
            commonBackOperation();
            layoutVisible(false, "", "", " ", null);
            SharedData.getInstance().setParkingLocation(null);
            SharedData.getInstance().setSensorArea(null);
            if (isBooked) {
                setButtonText(context.getResources().getString(R.string.park), context.getResources().getColor(R.color.black));
            } else {
                setButtonText(context.getResources().getString(R.string.confirm_booking), context.getResources().getColor(R.color.black));
            }
            binding.btnConfirmBooking.setEnabled(true);
            binding.btnConfirmBooking.setFocusable(true);
        });

        binding.btnConfirmBooking.setOnClickListener(v -> {
            if (isGPSEnabled() && ConnectivityUtils.getInstance().checkInternet(context)) {
                isBooked = Preferences.getInstance(context).getBooked().getIsBooked();
                if (isBooked && bookedPlace != null) {
                    double distanceBetween = calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                            bookedPlace.getLat(), bookedPlace.getLon()) * 1000;
                    double distance = circle != null ? circle.getRadius() : 70;
                    assert circle != null;
                    Timber.e(String.valueOf(circle.getRadius()));
                    if (distanceBetween <= distance && (bookedPlace.getArriveDate() - System.currentTimeMillis()) <= 300000) {
                        DialogUtils.getInstance().alertDialog(context,
                                context,
                                "Your park has been started",
                                context.getString(R.string.ok), "",
                                new DialogUtils.DialogClickListener() {
                                    @Override
                                    public void onPositiveClick() {
                                        if (isBooked) {
                                            setBookingPark(Preferences.getInstance(context).getUser().getMobileNo(), bookedPlace.getBookedUid());
                                        } else {
                                            DialogUtils.getInstance().alertDialog(context,
                                                    requireActivity(),
                                                    context.getResources().getString(R.string.booking_already_canceled), context.getResources().getString(R.string.booking_already_canceled_msg),
                                                    context.getResources().getString(R.string.ok), "",
                                                    new DialogUtils.DialogClickListener() {
                                                        @Override
                                                        public void onPositiveClick() {
                                                            Timber.e("Positive Button clicked");
                                                            commonBackOperation();
                                                        }

                                                        @Override
                                                        public void onNegativeClick() {
                                                            Timber.e("Negative Button Clicked");
                                                        }
                                                    }).show();
                                        }
                                    }

                                    @Override
                                    public void onNegativeClick() {

                                    }
                                }).show();
                        setButtonText(context.getResources().getString(R.string.parking), context.getResources().getColor(R.color.black));
                    } else if (parkingAreaChanged && isBooked) {
                        if (!isAreaChangedForSearch) {
                            DialogUtils.getInstance().showMessageDialog(context.getResources().getString(R.string.park_message), context);
                        } else {
                            DialogUtils.getInstance().showMessageDialog(context.getResources().getString(R.string.already_booked_msg), context);
                        }
                    } else {
                        DialogUtils.getInstance().showMessageDialog(context.getResources().getString(R.string.park_message), context);
                    }
                } else {
                    if (searchPlaceCount.equals("0")) {
                        DialogUtils.getInstance().showMessageDialog(context.getResources().getString(R.string.no_parking_spot_message), context);
                    } else {
                        Timber.e("else called");
                    }
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    if (parkingNumberOfIndividualMarker.equals("0") || searchPlaceCount.equals("0")) {
                        DialogUtils.getInstance().showMessageDialog(context.getResources().getString(R.string.no_parking_spot_message), context);
                        setButtonText(context.getResources().getString(R.string.unavailable_parking_spot), context.getResources().getColor(R.color.gray3));
                        binding.btnConfirmBooking.setEnabled(true);
                        binding.btnConfirmBooking.setFocusable(true);
                    } else {
                        setButtonText(context.getResources().getString(R.string.confirm_booking), context.getResources().getColor(R.color.black));
                        binding.btnConfirmBooking.setEnabled(true);
                        binding.btnConfirmBooking.setFocusable(true);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("m", false); //m for more
                        bundle.putString("markerUid", parkingAreaPlacedId);
                        bundle.putString("areaName", parkingAreaPlaceName);
                        bundle.putString("parkingSlotCount", parkingNumberOfIndividualMarker);
                        bundle.putDouble("lat", parkingSpotLatLng.latitude);
                        bundle.putDouble("long", parkingSpotLatLng.longitude);
                        bundle.putString("route", new Gson().toJson(initialRoutePoints));
                        ScheduleFragment scheduleFragment = new ScheduleFragment();
                        scheduleFragment.setArguments(bundle);
                        listener.fragmentChange(scheduleFragment);
                        binding.bottomSheetLayout.layoutBottomSheet.setVisibility(View.GONE);
                    }
                }
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_gps));
            }
        });
    }

    private void setBookingPark(String mobileNo, String uid) {
        showLoading(context);
        ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<ReservationCancelResponse> call = request.setBookingPark(mobileNo, uid);
        call.enqueue(new Callback<ReservationCancelResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call<ReservationCancelResponse> call, @NonNull Response<ReservationCancelResponse> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        stopBookingTrackService();
                        getBookingParkStatus(Preferences.getInstance(context).getUser().getMobileNo());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReservationCancelResponse> call, @NonNull Throwable t) {
                Timber.e("onFailure -> %s", t.getMessage());
                hideLoading();
            }
        });
    }

    private void setTimer(long difference) {
        new CountDownTimer(difference, 1000) {

            public void onTick(long millisUntilFinished) {
                countDownTV.setText(getTimeDifference(millisUntilFinished));
            }

            public void onFinish() {
                countDownTV.setText(context.getResources().getString(R.string.please_wait));
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

    @SuppressWarnings("SameParameterValue")
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
        List<LatLng> dangerousArea = new ArrayList<>();
        for (MyLatLng myLatLng : latLngs) {
            LatLng convert = new LatLng(myLatLng.getLatitude(), myLatLng.getLongitude());
            dangerousArea.add(convert);
        }

        //clear map and add again
        if (mMap != null) {
            mMap.clear();
            //Add user Marker
            addUserMarker();
        }
    }

    @Override
    public void onLoadLocationFailed(String message) {
        ToastUtils.getInstance().showToastMessage(context, message);
    }

    private void addUserMarker() {
        if (geoFire != null) {
            geoFire.setLocation("You", new GeoLocation(lastLocation.getLatitude(),
                    lastLocation.getLongitude()), (key, error) -> {
                if (currentUser != null) currentUser.remove();
            });
        }
    }

    private void settingGeoFire() {
        DatabaseReference myLocationRef = FirebaseDatabase.getInstance().getReference("MyLocation");
        geoFire = new GeoFire(myLocationRef);
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull final LocationResult locationResult) {
                if (mMap != null) {
                    lastLocation = locationResult.getLastLocation();
                    SharedData.getInstance().setLastLocation(lastLocation);
                    addUserMarker();
                }
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
    }

    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {
            Timber.e("else called");
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
                    ToastUtils.getInstance().showToastMessage(context, "Dialog is cancelled by User"));
            if (dialog != null) {
                dialog.show();
            }
        }

        return false;
    }

    private void startShimmer() {
        Timber.e("startShimmer");
        binding.bottomSheetLayout.mShimmerViewContainer.setVisibility(View.VISIBLE);
        binding.bottomSheetLayout.mShimmerViewContainer.startShimmer();
        binding.bottomSheetLayout.bottomSheetRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void stopShimmer() {
        Timber.e("stopShimmer");
        binding.bottomSheetLayout.mShimmerViewContainer.setVisibility(View.GONE);
        binding.bottomSheetLayout.mShimmerViewContainer.stopShimmer();
        binding.bottomSheetLayout.bottomSheetRecyclerView.setVisibility(View.VISIBLE);
    }

    public void setNoData() {
        binding.bottomSheetLayout.textViewNoData.setVisibility(View.VISIBLE);
        binding.bottomSheetLayout.textViewNoData.setText(context.getString(R.string.no_nearest_parking_area_found));
    }

    public void hideNoData() {
        binding.bottomSheetLayout.textViewNoData.setVisibility(View.GONE);
    }

    public interface SetBottomSheetCallBack {
        void setBottomSheet();
    }

    public interface AddressCallBack {
        void addressCall(String address);

    }

    String oldDestination = "";

    public void fetchDirections(String origin, String destination) {
        mMap.setTrafficEnabled(false);
        String bookedDestination = "" + bookedPlace.getLat() + ", " + bookedPlace.getLon();
        if (!oldDestination.equalsIgnoreCase(destination)) {
            oldDestination = destination;

            polyline = mMap.addPolyline(getDefaultPolyLines(points));

            if (isBooked && bookedPlace != null) {
                if (!destination.equalsIgnoreCase(bookedDestination)) {
                    //binding.btnConfirmBooking.setText(context.getResources().getString(R.string.confirm_booking));
                    setButtonText(context.getResources().getString(R.string.confirm_booking), context.getResources().getColor(R.color.black));
                    parkingAreaChanged = true;
                }
            }

            if (origin.isEmpty() || destination.isEmpty()) {
                ToastUtils.getInstance().showToastMessage(context, "Please first fill all the fields!");
                return;
            }

            if (!origin.contains(",") || !destination.contains(",")) {
                ToastUtils.getInstance().showToastMessage(context, "Invalid data fill in fields!");
                return;
            }

            if (!polyline.isVisible())
                return;

            points = polyline.getPoints();
            try {
                if (polyline == null || !polyline.isVisible())
                    return;
                points = polyline.getPoints();
                polyline.remove();
                new DirectionFinder(this, origin, destination).execute();
                isRouteDrawn = 1;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            mMap.setTrafficEnabled(false);
        }
    }

    @Override
    public void onDirectionFinderStart() {
        if (isAdded()) {
            showLoading(context);
            binding.overlay.setVisibility(View.VISIBLE);
        }
    }

    List<www.fiberathome.com.parkingapp.service.googleService.directionModules.Route> updatedRoute;

    @Override
    public void onDirectionFinderSuccess(List<www.fiberathome.com.parkingapp.service.googleService.directionModules.Route> route) {
        if (isAdded()) {
            hideLoading();
            binding.overlay.setVisibility(View.GONE);
        }
        if (!route.isEmpty() && polyline != null) polyline.remove();
        try {
            updatedRoute = route;
            for (www.fiberathome.com.parkingapp.service.googleService.directionModules.Route mRoute : route) {
                PolylineOptions polylineOptions = getDefaultPolyLines(mRoute.points);
                initialRoutePoints = mRoute.points;
                polyline = mMap.addPolyline(polylineOptions);
                firstDraw = true;
                if (isAdded()) {
                    binding.btnConfirmBooking.setEnabled(true);
                    binding.btnConfirmBooking.setFocusable(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //  Toast.makeText(context, "Error occurred on finding the directions...", Toast.LENGTH_SHORT).show();
        }
    }

    private void startAlarm(Calendar arrive, Calendar departure) {
        Timber.e("startAlarm called");
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent firstIntent = new Intent(context, NotificationPublisher.class); // trigger before 15 mins
        Intent secondIntent = new Intent(context, NotificationPublisher.class); // trigger at end time
        Intent thiredIntent = new Intent(context, NotificationPublisher.class); // trigger at end time
        firstIntent.putExtra("started", "Book Time started");
        secondIntent.putExtra("ended", "Book Time Up");
        pendingIntent = PendingIntent.getBroadcast(context, 1, firstIntent, 0);
        pendingIntent2 = PendingIntent.getBroadcast(context, 2, secondIntent, 0);
        pendingIntent3 = PendingIntent.getBroadcast(context, 3, thiredIntent, 0);
        if (arrive.before(Calendar.getInstance())) {
            arrive.add(Calendar.DATE, 1);
        }
        if (departure.before(Calendar.getInstance())) {
            departure.add(Calendar.DATE, 1);
        }
        Objects.requireNonNull(alarmManager).setExact(AlarmManager.RTC_WAKEUP,
                departure.getTimeInMillis() - 900000, pendingIntent3);
        Objects.requireNonNull(alarmManager).setExact(AlarmManager.RTC_WAKEUP,
                arrive.getTimeInMillis(), pendingIntent);
        Objects.requireNonNull(alarmManager).setExact(AlarmManager.RTC_WAKEUP,
                departure.getTimeInMillis(), pendingIntent2);
    }

    public Calendar convertLongToCalendar(Long source) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(source);
        return calendar;
    }

    private void getBookingParkStatus(String mobileNo) {
        showLoading(context);
        ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<BookingParkStatusResponse> call = request.getBookingParkStatus(mobileNo);
        call.enqueue(new Callback<BookingParkStatusResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call<BookingParkStatusResponse> call, @NonNull Response<BookingParkStatusResponse> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getSensors() != null) {
                            BookingParkStatusResponse.Sensors sensors = response.body().getSensors();
                            listener.fragmentChange(BookingParkFragment.newInstance(sensors));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingParkStatusResponse> call, @NonNull Throwable t) {
                Timber.e("onFailure -> %s", t.getMessage());
                hideLoading();
            }
        });
    }
}