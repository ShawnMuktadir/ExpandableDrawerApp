package www.fiberathome.com.parkingapp.ui.home;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.model.data.AppConstants.FIRST_TIME_INSTALLED;
import static www.fiberathome.com.parkingapp.model.data.AppConstants.HISTORY_PLACE_SELECTED;
import static www.fiberathome.com.parkingapp.model.data.AppConstants.HISTORY_PLACE_SELECTED_OBJ;
import static www.fiberathome.com.parkingapp.model.data.AppConstants.NEW_PLACE_SELECTED;
import static www.fiberathome.com.parkingapp.model.data.AppConstants.NEW_PLACE_SELECTED_OBJ;
import static www.fiberathome.com.parkingapp.utils.GoogleMapHelper.defaultMapSettings;
import static www.fiberathome.com.parkingapp.utils.GoogleMapHelper.getDefaultPolyLines;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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
import www.fiberathome.com.parkingapp.model.data.AppConstants;
import www.fiberathome.com.parkingapp.model.data.Constants;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.response.BaseResponse;
import www.fiberathome.com.parkingapp.model.response.booking.BookingParkStatusResponse;
import www.fiberathome.com.parkingapp.model.response.booking.BookingSensors;
import www.fiberathome.com.parkingapp.model.response.booking.ReservationCancelResponse;
import www.fiberathome.com.parkingapp.model.response.booking.ReservationResponse;
import www.fiberathome.com.parkingapp.model.response.booking.SensorAreaStatusResponse;
import www.fiberathome.com.parkingapp.model.response.parkingSlot.ParkingSlotResponse;
import www.fiberathome.com.parkingapp.model.response.search.SearchVisitorData;
import www.fiberathome.com.parkingapp.model.response.search.SelectedPlace;
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
import www.fiberathome.com.parkingapp.utils.ToastUtils;
import www.fiberathome.com.parkingapp.utils.ViewUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class HomeFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        IOnLoadLocationListener,
        IOnBackPressListener, DirectionFinderListener {

    public static final int GPS_REQUEST_CODE = 9003;
    public static final int PLAY_SERVICES_ERROR_CODE = 9002;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    public static Location currentLocation = null;
    public static MarkerOptions markerOptionsPin;
    private static boolean isBooked = false;
    private static String bookedUid;
    private static BookedPlace bookedPlace;

    private final String TAG = getClass().getSimpleName();

    private final ArrayList<BookingSensors> bookingSensorsArrayList = new ArrayList<>();
    private final ArrayList<BookingSensors> bookingSensorsArrayListGlobal = new ArrayList<>();
    protected final ArrayList<MarkerOptions> mMarkerArrayList = new ArrayList<>();
    private final List<SensorArea> sensorAreaArrayList = new ArrayList<>();
    private final List<SensorStatus> sensorStatusArrayList = new ArrayList<>();
    public List<LatLng> points = new ArrayList<>();

    public GoogleMap mMap;
    public int isRouteDrawn = 0;

    public BottomSheetBehavior<View> bottomSheetBehavior;
    public Polyline polyline;

    protected String sensorStatus = "Occupied";
    protected double myLocationChangedDistance;

    String parkingPlaceId = "";
    String oldDestination = "";
    List<www.fiberathome.com.parkingapp.service.googleService.directionModules.Route> updatedRoute;
    private HomeActivity context;
    private FragmentChangeListener listener;
    private long arrived, departure;
    private long difference;
    private BottomSheetAdapter bottomSheetAdapter;
    private Marker currentLocationMarker;
    private SupportMapFragment supportMapFragment;
    private String count = "";
    private boolean isGPS;

    private PolylineOptions polylineOptions, blackPolylineOptions;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastLocation;
    private Location onConnectedLocation;

    private String searchPlaceCount = "0";
    private String parkingNumberOfIndividualMarker = "";
    private String parkingAreaPlaceName = "";
    private String areaName, parkingSlotCount, parkingAreaPlacedId, parkingArea, placeId, previousOrigin;
    private String sensorAreaStatusAreaId, sensorAreaStatusTotalSensorCount, sensorAreaStatusTotalOccupied;
    private String destination = null;
    private double lat, lng, endLat, endLng, fetchDistance;

    private boolean isInAreaEnabled = false;
    private boolean parkingAreaChanged = false;
    private boolean isNotificationSent = false;
    protected boolean isAreaChangedForSearch = false;
    private boolean isBackClicked = false;

    private Marker previousMarker = null;
    private Marker pinMarker;

    private Circle circle;
    private SensorArea markerTagObj;
    private List<LatLng> initialRoutePoints;
    private LatLng origin, parkingSpotLatLng;

    FragmentHomeBinding binding;

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
    private String argPlaceId;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public static HomeFragment newInstance(double lat, double lng, String areaName, String count, String placeId) {
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble("lat", lat);
        bundle.putDouble("lng", lng);
        bundle.putString("areaName", areaName);
        bundle.putString("count", count);
        bundle.putString("placeId", placeId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static MarkerOptions newMarkerPinInstance() {
        if (markerOptionsPin == null) {
            markerOptionsPin = new MarkerOptions();
        }
        return markerOptionsPin;
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
            argPlaceId = getArguments().getString("placeId", null);
            if (lat != 0 && lng != 0 && areaName != null && !areaName.equalsIgnoreCase("") && parkingSlotCount != null) {
                parkingSpotLatLng = new LatLng(lat, lng);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        if (ApplicationUtils.isServicesOk(context)) {
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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Timber.e("onViewCreated called");
        super.onViewCreated(view, savedInstanceState);
        if (context != null) {
            context.changeDefaultActionBarDrawerToogleIcon();
            listener = context;
        }
        bookedPlace = Preferences.getInstance(context).getBooked();
        if (!bookedPlace.getIsBooked() && bookedPlace.isPaid()) {
            if (ConnectivityUtils.getInstance().isGPSEnabled(context)) {
                storeReservation(Preferences.getInstance(context).getUser().getMobileNo(),
                        getDate(bookedPlace.getArriveDate()), getDate(bookedPlace.getDepartedDate()), bookedPlace.getPlaceId());
            } else {
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet));
            }
        }
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

                if (mMap == null) {
                    showLoading(context, context.getResources().getString(R.string.please_wait));
                } else {
                    hideLoading();
                }

                if (isBooked && bookedPlace != null) {
                    oldDestination = "" + bookedPlace.getLat() + ", " + bookedPlace.getLon();
                    Timber.e("bookedPlace.getLat(), bookedPlace.getLon() -> %s, %s", bookedPlace.getLat(), bookedPlace.getLon());
                }

            }
        } catch (Resources.NotFoundException e) {
            e.getCause();
        }
    }

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
        try {
            setupLocationBuilder();
        } catch (Exception e) {
            e.getCause();
        }
        defaultMapSettings(context, mMap, fusedLocationProviderClient, locationRequest, locationCallback);

        mMap.setOnMarkerClickListener(this);
    }

    private void setupLocationBuilder() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(buildLocationRequest(), buildLocationCallBack(), Objects.requireNonNull(Looper.myLooper()));
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(context, task -> {
            Location location = task.getResult();
            if (location != null) {
                onConnectedLocation = location;
                SharedData.getInstance().setOnConnectedLocation(onConnectedLocation);
            }
            try {
                if (mMap != null && onConnectedLocation != null) {
                    LatLng latLng = new LatLng(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.5f));
                    animateCamera(onConnectedLocation);
                    bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._92sdp));
                    if (ConnectivityUtils.getInstance().checkInternet(context) && ApplicationUtils.isGPSEnabled(context)) {
                        getSensorAreaStatus();
                    } else {
                        ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet_gps));
                    }
                }
            } catch (Exception e) {
                Timber.e(e.getCause());
            }
        });

    }

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


    private void toolbarAnimVisibility(View view, boolean show) {
        Transition transition = new Fade();
        transition.setDuration(600);
        transition.addTarget(R.id.image);
        View toolbar = context.findViewById(R.id.toolbar);
        TransitionManager.beginDelayedTransition((ViewGroup) view, transition);
        toolbar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onMarkerClick(Marker marker) {

        if (mMap != null) {
            if (markerTagObj != null) {
                if (marker.getTag() != null) {
                    SensorArea tempMarkerTagObj = (SensorArea) marker.getTag();
                    if (!markerTagObj.getPlaceId().equalsIgnoreCase(tempMarkerTagObj.getPlaceId())) {
                        markerTagObj = tempMarkerTagObj;
                        if (isRouteDrawn == 0) {
                            markerParkingSetup(markerTagObj, marker);
                        } else {
                            showParkingSpotChangeDialog(markerTagObj, marker);
                        }
                    }
                }

            } else {
                if (marker.getTag() != null) {
                    markerTagObj = (SensorArea) marker.getTag();
                    if (isRouteDrawn == 0) {
                        markerParkingSetup(markerTagObj, marker);
                    } else {
                        showParkingSpotChangeDialog(markerTagObj, marker);
                    }
                }
            }
        }


        return true;
    }

    private void showParkingSpotChangeDialog(SensorArea mMarkerTagObj, Marker mMarker) {
        DialogUtils.getInstance().alertDialog(context,
                context,
                context.getResources().getString(R.string.you_have_to_exit_from_current_destination),
                context.getResources().getString(R.string.yes), context.getResources().getString(R.string.no),
                new DialogUtils.DialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                        if (polyline == null || !polyline.isVisible())
                            return;
                        points = polyline.getPoints();
                        if (polyline != null) {
                            polyline.remove();
                        }
                        getDirectionPinMarkerDraw(mMarker.getPosition(), mMarkerTagObj.getPlaceId(), false);
                        String origin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();
                        markerParkingSetup(mMarkerTagObj, mMarker);
                    }

                    @Override
                    public void onNegativeClick() {
                        Timber.e("Negative Button Clicked");
                        markerTagObj = null;
                    }
                }).show();
    }

    private void markerParkingSetup(SensorArea markerTagObj, Marker marker) {
        hideNoData();
        searchPlaceCount = "";
        final String[] uid = {""};
        final String[] uid1 = {""};
        final String[] markerAreaName1 = {""};
        try {
            if (markerTagObj != null) {
                Timber.e("marker if UID: -> %s", markerTagObj.getParkingArea());
                try {
                    if (ApplicationUtils.isGPSEnabled(context) && ConnectivityUtils.getInstance().checkInternet(context)) {
                        if (previousMarker != null) {
                            previousMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                        } else {
                            Timber.e("else called");
                        }
                        previousMarker = marker;
                        removeCircle();
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_gray));
                        parkingAreaChanged = true;
                        setButtonText(context.getResources().getString(R.string.confirm_booking), context.getResources().getColor(R.color.black));
                        isNotificationSent = false;
                        isInAreaEnabled = false;
                        //calculate Duration
                        parkingSpotLatLng = marker.getPosition();
                        destination = "" + parkingSpotLatLng.latitude + ", " + parkingSpotLatLng.longitude;
                        binding.fabGetDirection.setVisibility(View.VISIBLE);
                        populateNearestPlaceBottomSheet(parkingSpotLatLng, markerTagObj);
                    } else {
                        ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet_gps));
                    }
                } catch (Resources.NotFoundException e) {
                    e.getCause();
                }
            } else {
                Timber.e("marker else UID: did not work");
            }
        } catch (Exception e) {
            Timber.e(e.getCause());
            e.getCause();
        }
    }

    private void populateNearestPlaceBottomSheet(LatLng latLng, SensorArea SelectedSensorArea) {
        try {
            if (onConnectedLocation != null && latLng != null) {
                bookingSensorsArrayList.clear();
                parkingSpotLatLng = latLng;

                try {
                    parkingPlaceId = SelectedSensorArea.getPlaceId();
                    parkingAreaPlaceName = SelectedSensorArea.getParkingArea();

                    parkingAreaPlacedId = parkingPlaceId;
                    parkingNumberOfIndividualMarker = SelectedSensorArea.getCount();
                    binding.textViewParkingAreaCount.setText(parkingNumberOfIndividualMarker);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                double bottomSheetDistance = calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                        parkingSpotLatLng.latitude, parkingSpotLatLng.longitude);
                double bottomSheetDoubleDuration = MathUtils.getInstance().convertToDouble(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US)).format(bottomSheetDistance * 2.43));
                String bottomSheetStringDuration = String.valueOf(bottomSheetDoubleDuration);
                String occupied = null;

                for (SensorStatus status : sensorStatusArrayList) {
                    if (status.getAreaId().equalsIgnoreCase(parkingAreaPlacedId)) {
                        occupied = status.getOccupiedCount();
                    }
                }
                bookingSensorsArrayList.add(new BookingSensors(parkingAreaPlaceName, parkingSpotLatLng.latitude, parkingSpotLatLng.longitude,
                        bottomSheetDistance, occupied != null ? occupied + "/" + binding.textViewParkingAreaCount.getText().toString() : binding.textViewParkingAreaCount.getText().toString(), bottomSheetStringDuration,
                        context.getResources().getString(R.string.nearest_parking_from_your_destination),
                        BookingSensors.SELECTED_INFO_TYPE, 0, parkingPlaceId));
                binding.btnConfirmBooking.setVisibility(View.VISIBLE);
                setBottomSheetList((ArrayList<BookingSensors> mBookingSensorsArrayList) -> {
                    if (bottomSheetAdapter != null) {
                        bookingSensorsArrayListGlobal.clear();
                        bookingSensorsArrayListGlobal.addAll(mBookingSensorsArrayList);
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
        if (mMap != null) {
            if (Preferences.getInstance(context).getBooked() != null && Preferences.getInstance(context).getBooked().getIsBooked()) {
                if (parkingSpotLatLng != null) {
                    double distanceForCount = calculateDistance(parkingSpotLatLng.latitude, parkingSpotLatLng.longitude,
                            Preferences.getInstance(context).getBooked().getLat(),
                            Preferences.getInstance(context).getBooked().getLon());
                    if (distanceForCount < 0.001) {
                        checkParkingSpotDistance(latLng, new LatLng(Preferences.getInstance(context).getBooked().getLat(),
                                Preferences.getInstance(context).getBooked().getLon()));
                    }
                } else {
                    checkParkingSpotDistance(latLng, new LatLng(Preferences.getInstance(context).getBooked().getLat(),
                            Preferences.getInstance(context).getBooked().getLon()));
                }
            }
        }

        String origin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();

        try {
            if (isRouteDrawn == 1) {
                if (oldDestination != null) {
                    if (previousOrigin != null) {
                        if (!oldDestination.isEmpty() && onConnectedLocation != null) {
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
                    } else {
                        previousOrigin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();
                    }
                }
            }
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
        bookedPlace = Preferences.getInstance(context).getBooked();
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
        hideLoading();
        bookedPlace = Preferences.getInstance(context).getBooked();
        isBooked = Preferences.getInstance(context).getBooked().getIsBooked();
        if (fusedLocationProviderClient == null) {
            setupLocationBuilder();
        }
        if (Preferences.getInstance(context).isBookingCancelled) {
            Preferences.getInstance(context).isBookingCancelled = false;
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

    private void startBookingTrackService() {
        if (!ApplicationUtils.isLocationTrackingServiceRunning(context)) {
            Intent intent = new Intent(context, BookingService.class);
            intent.setAction(Constants.START_BOOKING_TRACKING);
            context.startService(intent);
        }
    }

    private void stopBookingTrackService() {
        Intent intent = new Intent(context, BookingService.class);
        intent.setAction(Constants.STOP_BOOKING_TRACKING);
        context.startService(intent);
    }

    private void checkParkingSpotDistance(LatLng car, LatLng spot) {
        float[] distance = new float[2];
        if (circle != null) {
            Location.distanceBetween(car.latitude, car.longitude, circle.getCenter().latitude, circle.getCenter().longitude, distance);
            if (distance[0] <= circle.getRadius() && !isNotificationSent) {
                // Inside The Circle
                isNotificationSent = true;
                sendNotification("You are near your parking spot", "You can Park");
                isInAreaEnabled = true;
                if (isBooked && bookedPlace != null) {
                    destination = "" + bookedPlace.getLat() + ", " + bookedPlace.getLon();

                    SensorArea sensorArea = createSensorAreaObj(bookedPlace.getAreaName(), bookedPlace.getPlaceId(), bookedPlace.getLat(), bookedPlace.getLon(), bookedPlace.getParkingSlotCount());

                    populateNearestPlaceBottomSheet(new LatLng(bookedPlace.getLat(), bookedPlace.getLon()), sensorArea);
                    setButtonText(context.getResources().getString(R.string.park), context.getResources().getColor(R.color.black));
                }
            } else {
                if (distance[0] > circle.getRadius()) {
                    isNotificationSent = false;
                    isInAreaEnabled = false;
                    if (isBooked && bookedPlace != null) {
                        setButtonText(context.getResources().getString(R.string.park), context.getResources().getColor(R.color.gray3));
                    }
                }
            }
        }
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    if (data != null) {
                        if (NEW_PLACE_SELECTED.equalsIgnoreCase(data.getStringExtra(NEW_PLACE_SELECTED))) {
                            SelectedPlace selectedPlace = data.getParcelableExtra(NEW_PLACE_SELECTED_OBJ); //This line may produce null point exception
                            setupSearchResult(selectedPlace);
                        } else if (HISTORY_PLACE_SELECTED.equalsIgnoreCase(data.getStringExtra(HISTORY_PLACE_SELECTED))) {
                            SearchVisitorData sVData = data.getParcelableExtra(HISTORY_PLACE_SELECTED_OBJ); //This line may produce null point exception
                            if (sVData != null) {
                                SelectedPlace selectedPlace = new SelectedPlace(sVData.getPlaceId(), sVData.getVisitedArea(), "", sVData.getEndLat(), sVData.getEndLng());
                                setupSearchResult(selectedPlace);
                            }
                        }

                    }
                }
            });

    private void setupSearchResult(SelectedPlace selectedPlace) {
        searchPlaceCount = "0";
        binding.fabGetDirection.setVisibility(View.VISIBLE);
        parkingAreaChanged = false;
        if (searchPlaceCount.equals("0")) {
            setButtonText(context.getResources().getString(R.string.unavailable_parking_spot), context.getResources().getColor(R.color.gray3));
        }
        if (selectedPlace != null) {
            previousMarker = null;
            hideNoData();
            parkingSpotLatLng = new LatLng(selectedPlace.getLatitude(), selectedPlace.getLongitude());
            destination = "" + parkingSpotLatLng.latitude + ", " + parkingSpotLatLng.longitude;
            String areaName = selectedPlace.getAreaName();
            String placeId = selectedPlace.getPlaceId();

            //store visited place
            storeVisitedPlace(Preferences.getInstance(context).getUser().getMobileNo(), placeId,
                    selectedPlace.getLatitude(), selectedPlace.getLongitude(),
                    onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(), areaName);

            plotSearchAddressData(areaName, placeId, parkingSpotLatLng, searchPlaceCount);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.GPS_REQUEST && resultCode == RESULT_OK) {
            isGPS = true;// flag maintain before get location
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
                    BookingSensors.SELECTED_INFO_TYPE, 0, placeId));
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
                            BookingSensors.INFO_TYPE, 1, sensor.getPlaceId()));

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

    private void setBroadcast() {
        LocalBroadcastManager.getInstance(context).registerReceiver(bookingEndedReceiver,
                new IntentFilter("booking_ended"));
    }

    private void getSensorAreaStatus() {
        showLoading(context);
        ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<SensorAreaStatusResponse> call = request.getSensorAreaStatus();
        call.enqueue(new Callback<SensorAreaStatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<SensorAreaStatusResponse> call,
                                   @NonNull retrofit2.Response<SensorAreaStatusResponse> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!response.body().getError()) {
                            if (response.body().getSensorAreaStatusArrayList() != null) {
                                List<List<String>> sensorStatusList = response.body().getSensorAreaStatusArrayList();
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
                                                 LatLng latLng, ArrayList<BookingSensors> bookingSensorsArrayList, String mPlaceId) {
        String parkingNumberOfNearbyDistanceLoc = null;
        String nearbySearchStringDuration;
        for (int i = 0; i < sensorArrayList.size(); i++) {
            SensorArea sensor = sensorArrayList.get(i);
            try {
                String uid = sensor.getPlaceId();
                String parkingArea = sensor.getParkingArea();
                double distanceForNearbyLoc = calculateDistance(latLng.latitude, latLng.longitude,
                        sensor.getEndLat(), sensor.getEndLng());
                final String[] nearbyAreaName = {""};

                if (distanceForNearbyLoc < 5 && !mPlaceId.equals(uid)) {
                    origin = new LatLng(latLng.latitude, latLng.longitude);
                    nearbyAreaName[0] = parkingArea;
                    try {
                        parkingNumberOfNearbyDistanceLoc = sensor.getCount();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    double nearbySearchDoubleDuration = MathUtils.getInstance().convertToDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US)).format(distanceForNearbyLoc * 2.43));
                    nearbySearchStringDuration = String.valueOf(nearbySearchDoubleDuration);

                    bookingSensorsArrayList.add(new BookingSensors(nearbyAreaName[0], sensor.getEndLat(),
                            sensor.getEndLng(), adjustDistance(distanceForNearbyLoc), sensor.getOccupiedCount() != null ? sensor.getOccupiedCount() + "/" + parkingNumberOfNearbyDistanceLoc : parkingNumberOfNearbyDistanceLoc,
                            nearbySearchStringDuration,
                            BookingSensors.INFO_TYPE, 1, sensor.getPlaceId()));

                    bubbleSortArrayList(bookingSensorsArrayList);
                } else {
                    Timber.e("else called");
                }
                setBottomSheetCallBack.setBottomSheet(bookingSensorsArrayList);
                bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._142sdp));
                if (parkingSpotLatLng != null) {
                    binding.linearLayoutBottom.setVisibility(View.VISIBLE);
                    binding.linearLayoutParkingAdapterBackBottom.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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
                    List<List<String>> parkingSlotList = response.body().getSensors();
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
                            }

                            SensorArea sensorArea = createSensorAreaObj(parkingArea, placeId, endLat, endLng, count);

                            sensorAreaArrayList.add(sensorArea);
                        }

                        Collections.sort(sensorAreaArrayList, (c1, c2) -> Double.compare(c1.getDistance(), c2.getDistance()));

                        for (SensorArea sensorArea : sensorAreaArrayList) {
                            renderParkingSensors(sensorArea, location);
                        }

                        new Handler().postDelayed(() -> {
                            if (ApplicationUtils.isGPSEnabled(context) && ConnectivityUtils.getInstance().checkInternet(context)) {
                                isBooked = Preferences.getInstance(context).getBooked().getIsBooked();
                                if (lat != 0 && lng != 0 && areaName != null && !areaName.equalsIgnoreCase("") && parkingSlotCount != null) {
                                    parkingSpotLatLng = new LatLng(lat, lng);
                                    binding.fabGetDirection.setVisibility(View.VISIBLE);
                                    hideNoData();
                                    getDirectionPinMarkerDraw(new LatLng(lat, lng), parkingAreaPlacedId, false);
                                    destination = "" + parkingSpotLatLng.latitude + ", " + parkingSpotLatLng.longitude;
                                    SensorArea sensorArea = createSensorAreaObj(areaName, argPlaceId, lat, lng, parkingSlotCount);
                                    populateNearestPlaceBottomSheet(parkingSpotLatLng, sensorArea);
                                    setButtonText(context.getResources().getString(R.string.confirm_booking), context.getResources().getColor(R.color.black));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 13.5f), 500, null);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 13.5f));
                                } else if (isBooked && bookedPlace != null) {
                                    parkingSpotLatLng = new LatLng(bookedPlace.getLat(), bookedPlace.getLon());
                                    destination = "" + parkingSpotLatLng.latitude + ", " + parkingSpotLatLng.longitude;
                                    SensorArea sensorArea = createSensorAreaObj(bookedPlace.getAreaName(), bookedPlace.getPlaceId(), bookedPlace.getLat(), bookedPlace.getLon(), bookedPlace.getParkingSlotCount());
                                    populateNearestPlaceBottomSheet(parkingSpotLatLng, sensorArea);
                                    if (!isInAreaEnabled)
                                        setButtonText(context.getResources().getString(R.string.park), context.getResources().getColor(R.color.gray3));
                                    else
                                        setButtonText(context.getResources().getString(R.string.park), context.getResources().getColor(R.color.black));
                                    hideNoData();
                                    binding.buttonSearch.setVisibility(View.GONE);
                                }
                            } else {
                                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet_gps));
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
                    BookingSensors.INFO_TYPE, 1, sensor.getPlaceId()));
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
            searchPlaceCount = "";
            try {
                if (isRouteDrawn == 0) {
                    //for getting the location name
                    if (bottomSheetAdapter != null) {
                        getDirectionPinMarkerDraw(parkingSpotLatLng, parkingAreaPlacedId, false);
                        //move map camera
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(parkingSpotLatLng.latitude, parkingSpotLatLng.longitude), 13.5f), 500, null);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(parkingSpotLatLng.latitude, parkingSpotLatLng.longitude), 13.5f));
                    }
                    SensorArea sensorArea = createSensorAreaObj(sensors.getParkingArea(), sensors.getParkingPlaceId(), sensors.getLat(), sensors.getLng(), sensors.getCount());
                    populateNearestPlaceBottomSheet(parkingSpotLatLng, sensorArea);
                    setButtonText(context.getResources().getString(R.string.confirm_booking), context.getResources().getColor(R.color.black));
                } else {
                    DialogUtils.getInstance().alertDialog(context,
                            context,
                            context.getResources().getString(R.string.you_have_to_exit_from_current_destination),
                            context.getResources().getString(R.string.yes),
                            context.getResources().getString(R.string.no),
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
                                        //move map camera
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(parkingSpotLatLng.latitude, parkingSpotLatLng.longitude), 13.5f), 500, null);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(parkingSpotLatLng.latitude, parkingSpotLatLng.longitude), 13.5f));
                                    }
                                    binding.btnConfirmBooking.setVisibility(View.VISIBLE);
                                    setButtonText(context.getResources().getString(R.string.confirm_booking), context.getResources().getColor(R.color.black));
                                    String origin = "" + onConnectedLocation.getLatitude() + ", " + onConnectedLocation.getLongitude();
                                    if (parkingSpotLatLng != null) {
                                        destination = "" + parkingSpotLatLng.latitude + ", " + parkingSpotLatLng.longitude;
                                    }
                                    SensorArea sensorArea = createSensorAreaObj(sensors.getParkingArea(), sensors.getParkingPlaceId(), sensors.getLat(), sensors.getLng(), sensors.getCount());
                                    populateNearestPlaceBottomSheet(parkingSpotLatLng, sensorArea);
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

    private SensorArea createSensorAreaObj(String parkingArea, String parkingPlaceId, double lat, double lng, String count) {
        double mFetchDistance = MathUtils.getInstance().calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                lat, lng);
        return new SensorArea(parkingArea, parkingPlaceId, lat, lng, count, adjustDistance(mFetchDistance));

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

    @SuppressLint("SetTextI18n")
    private void layoutVisible(boolean isVisible, String name, String count,
                               String distance, LatLng location) {
        this.areaName = name;
        this.parkingSlotCount = count;
        this.parkingSpotLatLng = location;
        if (isVisible && isAdded()) {
            try {
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
            if (ApplicationUtils.isGPSEnabled(context) && ConnectivityUtils.getInstance().checkInternet(context)) {
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
                    previousMarker = null;
                    isRouteDrawn = 0;
                    oldDestination = "";
                    searchPlaceCount = "";
                    parkingSpotLatLng = null;
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
                        context.getResources().getString(R.string.connect_to_internet),
                        context.getResources().getString(R.string.retry),
                        context.getResources().getString(R.string.close_app),
                        new DialogUtils.DialogClickListener() {
                            @Override
                            public void onPositiveClick() {
                                Timber.e("Positive Button clicked");
                                if (ConnectivityUtils.getInstance().checkInternet(context)) {
                                    fetchParkingSlotSensors(onConnectedLocation);
                                } else {
                                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet));
                                }
                            }

                            @Override
                            public void onNegativeClick() {
                                Timber.e("Negative Button Clicked");
                                if (getActivity() != null) {
                                    getActivity().finish();
                                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.thanks_message));
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
                    if (destination != null) {
                        fetchDirections(origin, destination);
                        getDirectionPinMarkerDraw(parkingSpotLatLng, parkingAreaPlacedId, false);
                    }
                }
            } catch (Exception e) {
                Timber.e(e.getCause());
                e.getCause();
            }
        });

        binding.buttonSearch.setOnClickListener(v -> {
            if (ApplicationUtils.isGPSEnabled(context) && ConnectivityUtils.getInstance().checkInternet(context)) {
                Intent intent = new Intent(context, SearchActivity.class);
                activityResultLauncher.launch(intent);

                if (mMap != null)
                    mMap.clear();
                if (parkingSpotLatLng != null) {
                    bookingSensorsArrayList.clear();
                }
                bookingSensorsArrayListGlobal.clear();
                bookingSensorsArrayList.clear();
                //get sensor area status
                getSensorAreaStatus();
                animateCamera(onConnectedLocation);
                bottomSheetBehavior.setPeekHeight((int) context.getResources().getDimension(R.dimen._92sdp));
                binding.buttonSearch.setText("");
                binding.linearLayoutBottom.setVisibility(View.GONE);
            } else {
                DialogUtils.getInstance().alertDialog(context,
                        context,
                        context.getResources().getString(R.string.connect_to_internet),
                        context.getResources().getString(R.string.retry),
                        context.getResources().getString(R.string.close_app),
                        new DialogUtils.DialogClickListener() {
                            @Override
                            public void onPositiveClick() {
                                Timber.e("Positive Button clicked");
                                if (ConnectivityUtils.getInstance().checkInternet(context)) {
                                    getSensorAreaStatus();
                                } else {
                                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet));
                                }
                            }

                            @Override
                            public void onNegativeClick() {
                                Timber.e("Negative Button Clicked");
                                if (getActivity() != null) {
                                    getActivity().finish();
                                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.thanks_message));
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
            if (ApplicationUtils.isGPSEnabled(context) && ConnectivityUtils.getInstance().checkInternet(context)) {
                isBooked = Preferences.getInstance(context).getBooked().getIsBooked();
                if (isBooked && bookedPlace != null) {
                    double distanceBetween = calculateDistance(onConnectedLocation.getLatitude(), onConnectedLocation.getLongitude(),
                            bookedPlace.getLat(), bookedPlace.getLon()) * 1000;
                    double distance = circle != null ? circle.getRadius() : 70;
                    if (distanceBetween <= distance && (bookedPlace.getArriveDate() - System.currentTimeMillis()) <= 300000) {
                        DialogUtils.getInstance().alertDialog(context,
                                context,
                                context.getResources().getString(R.string.park_has_started),
                                context.getResources().getString(R.string.ok), "",
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
                                                            commonBackOperation();
                                                        }

                                                        @Override
                                                        public void onNegativeClick() {
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
                        DialogUtils.getInstance().showMessageDialog(context.getResources().getString(R.string.already_booked_msg), context);
                    } else {
                        DialogUtils.getInstance().showMessageDialog(context.getResources().getString(R.string.park_message), context);
                    }
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    if (searchPlaceCount.equals("0")) {
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
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet_gps));
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
    public void onLoadLocationSuccess(List<MyLatLng> latLongs) {
        Timber.e("onLoadLocationSuccess called");
    }

    @Override
    public void onLoadLocationFailed(String message) {
        ToastUtils.getInstance().showToastMessage(context, message);
    }


    private LocationCallback buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull final LocationResult locationResult) {
                if (mMap != null) {
                    lastLocation = locationResult.getLastLocation();
                    onLocationChanged(lastLocation);
                    SharedData.getInstance().setLastLocation(lastLocation);
                }
            }
        };
        return locationCallback;
    }

    private LocationRequest buildLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        return locationRequest;
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
        binding.bottomSheetLayout.textViewNoData.setText(context.getResources().getString(R.string.no_nearest_parking_area_found));
    }

    public void hideNoData() {
        binding.bottomSheetLayout.textViewNoData.setVisibility(View.GONE);
    }

    public void fetchDirections(String origin, String destination) {
        mMap.setTrafficEnabled(false);
        String bookedDestination = "" + bookedPlace.getLat() + ", " + bookedPlace.getLon();
        if (!oldDestination.equalsIgnoreCase(destination)) {
            oldDestination = destination;

            polyline = mMap.addPolyline(getDefaultPolyLines(points));

            if (isBooked && bookedPlace != null) {
                if (!destination.equalsIgnoreCase(bookedDestination)) {
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

    private void storeReservation(String mobileNo, String arrivalTime, String departureTime, String mPlaceId) {
        showLoading(context);
        ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<ReservationResponse> call = request.storeReservation(mobileNo, arrivalTime, departureTime, mPlaceId, "2");
        call.enqueue(new Callback<ReservationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReservationResponse> call,
                                   @NonNull retrofit2.Response<ReservationResponse> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getUid() != null) {
                            Timber.e("response -> %s", new Gson().toJson(response.body()));
                            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.reservation_successful));
                            //set booked place info
                            bookedPlace.setBookedUid(response.body().getUid());
                            bookedPlace.setAreaName(areaName);
                            bookedPlace.setParkingSlotCount(parkingSlotCount);
                            bookedPlace.setPlaceId(mPlaceId);
                            bookedPlace.setReservation(response.body().getReservation());
                            bookedPlace.setIsBooked(true);

                            Preferences.getInstance(context).setBooked(bookedPlace);
                            startAlarm(convertLongToCalendar(Preferences.getInstance(context).getBooked().getArriveDate()));
                        } else {
                            DialogUtils.getInstance().showOnlyMessageDialog(context.getResources().getString(R.string.parking_slot_not_available), context);
                        }
                    } else {
                        Toast.makeText(getContext(), context.getResources().getString(R.string.reservation_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReservationResponse> call, @NonNull Throwable t) {
                Timber.e("onFailure -> %s", t.getMessage());
                hideLoading();
            }
        });
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
                            isRouteDrawn = 0;
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

    public interface SetBottomSheetCallBack {
        void setBottomSheet(ArrayList<BookingSensors> bookingSensorsArrayList);
    }

    private String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private void startAlarm(Calendar c) {
        Timber.e("startAlarm called");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationPublisher.class);
        intent.putExtra("Started", "Booked Time About to start for : \n" + Preferences.getInstance(context).getBooked().getAreaName());
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        Objects.requireNonNull(alarmManager).setExact(AlarmManager.RTC_WAKEUP,
                c.getTimeInMillis() - 900000, pendingIntent);
    }

    public Calendar convertLongToCalendar(Long source) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(source);
        return calendar;
    }
}