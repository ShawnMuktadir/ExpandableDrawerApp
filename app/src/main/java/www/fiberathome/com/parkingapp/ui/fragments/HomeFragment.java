package www.fiberathome.com.parkingapp.ui.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.GoogleMapWebService.DirectionsParser;
import www.fiberathome.com.parkingapp.GoogleMapWebService.GooglePlaceSearchNearbySearchListener;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionAfterButtonClickEvent;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionEvent;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionForSearchEvent;
import www.fiberathome.com.parkingapp.eventBus.SetMarkerEvent;
import www.fiberathome.com.parkingapp.gps.GPSTracker;
import www.fiberathome.com.parkingapp.gps.GPSTrackerListener;
import www.fiberathome.com.parkingapp.model.GlobalVars;
import www.fiberathome.com.parkingapp.model.MyLocation;
import www.fiberathome.com.parkingapp.model.SensorArea;
import www.fiberathome.com.parkingapp.model.SensorList;
import www.fiberathome.com.parkingapp.module.PlayerPrefs;
import www.fiberathome.com.parkingapp.ui.DialogForm;
import www.fiberathome.com.parkingapp.base.AppConfig;
import www.fiberathome.com.parkingapp.ui.MainActivity;
import www.fiberathome.com.parkingapp.ui.parking.ParkingFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

// Add an import statement for the client library.

/**
 * A simple {@link Fragment} subclass.
 */


public class HomeFragment extends Fragment implements
        OnMapReadyCallback, GooglePlaceSearchNearbySearchListener, GoogleMap.OnMarkerClickListener,
        GPSTrackerListener, GoogleMap.OnInfoWindowClickListener, View.OnClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraIdleListener, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

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

    private Context context;
    private String name, count;
    private LatLng location;
    private double distance;
    private String duration;

    //Create field for map button.
    private View locationButton;

    private static final String TAG = HomeFragment.class.getSimpleName();
    // google map objects
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    public String selectedSensor;
    public String selectedSensorStatus = "Occupied";
    public TextView parkingReqSpot;
    public double nDistance = 132116456;
    public double nLatitude;
    public double nLongitude;

    /*Authors: Shawn And  Maruf*/
    public LatLng nearestRouteCoordinate;
    public LatLng searchPlaceLatLng;
    // global view
    View view;
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

    private List<LatLng> polyLineList;
    private Marker marker;
    private LocationManager mLocationManager;

    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private int getDirectionButtonClicked = 0;
    private ProgressDialog progressDialog;

    public HomeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        context = getActivity();

        if (!checkPermission()) {
            requestPermission();
        }
        listPoints = new ArrayList<>();
        polyLineList = new ArrayList<>();
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            SensorArea sensorArea = bundle.getParcelable("sensor"); // Key
            Timber.e("sensorArea -> %s", sensorArea);
            if (sensorArea != null) {
                textViewParkingAreaName.setText(sensorArea.getParkingArea());
                textViewParkingAreaCount.setText(sensorArea.getCount());
                textViewParkingDistance.setText(String.valueOf(sensorArea.getDistance()));
            } else {
                Toast.makeText(context, "Genjam", Toast.LENGTH_SHORT).show();
            }
        } else {
            Timber.e("Genjam");
        }
        setListeners();
        try {
            initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        setListeners();
        return view;
    }

    private void setListeners() {
        imageViewBack.setOnClickListener(v -> {
            if (googleMap != null) {
                googleMap.clear();
                fetchSensors();
                BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                navBar.setVisibility(View.VISIBLE);
                layoutVisible(false, "", "", 0.0, null);
            }
        });

        btnGetDirection.setOnClickListener(v -> {
            if (getDirectionButtonClicked == 0) {
                getDirectionButtonClicked = 2;
                if (location != null) {
                    EventBus.getDefault().post(new GetDirectionAfterButtonClickEvent(location));
//                    progressDialog = new ProgressDialog(getActivity());
//                    progressDialog.setMessage("Please wait...");
//                    progressDialog.show();
//                btnGetDirection.setVisibility(View.GONE);
                    fetchSensors();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(location);
                    markerOptions.title(name);
                    markerOptions.draggable(true);
                    coordList.add(new LatLng(location.latitude, location.longitude));
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    googleMap.addMarker(markerOptions);
//              move map camera
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
//                    linearLayoutNameCount.setVisibility(View.VISIBLE);
                    linearLayoutBottom.setVisibility(View.VISIBLE);
                    imageViewBack.setVisibility(View.VISIBLE);
                    btnGetDirection.setText("Cancel Direction");
                    btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.red));
                    BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                    navBar.setVisibility(View.GONE);
//                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
//                    btnGetDirection.setLayoutParams(layoutParams);
                }

//                if (searchPlaceLatLng != null) {
//                    EventBus.getDefault().post(new GetDirectionForSearchEvent(searchPlaceLatLng));
//                    progressDialog = new ProgressDialog(getActivity());
//                    progressDialog.setMessage("Please wait...");
//                    progressDialog.show();
//                    btnGetDirection.setVisibility(View.VISIBLE);
//                    linearLayoutNameCount.setVisibility(View.VISIBLE);
//                    imageViewBack.setVisibility(View.VISIBLE);
//                    btnGetDirection.setText("Cancel Direction");
//                    btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.red));
//                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
//                    btnGetDirection.setLayoutParams(layoutParams);
//                    progressDialog.dismiss();
//                }
            } else if (getDirectionButtonClicked == 2) {
                getDirectionButtonClicked = 0;
                if (googleMap != null) {
                    googleMap.clear();
                    fetchSensors();
                    linearLayoutBottom.setVisibility(View.GONE);
                    linearLayoutNameCount.setVisibility(View.GONE);
                    btnGetDirection.setText("Get Direction");
                    btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.black));
                    BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                    navBar.setVisibility(View.VISIBLE);
                }
            } else if (getDirectionButtonClicked == 1) {
                getDirectionButtonClicked = 2;
                EventBus.getDefault().post(new GetDirectionForSearchEvent(searchPlaceLatLng));
                //initialize the progress dialog and show it
//                    progressDialog = new ProgressDialog(getActivity());
//                    progressDialog.setMessage("Please wait...");
//                    progressDialog.show();
                fetchSensors();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(searchPlaceLatLng);
                markerOptions.title(name);
                markerOptions.draggable(true);
                coordList.add(new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                googleMap.addMarker(markerOptions);
//              move map camera
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 13));
//                    linearLayoutNameCount.setVisibility(View.VISIBLE);
                linearLayoutBottom.setVisibility(View.VISIBLE);
                linearLayoutNameCount.setVisibility(View.VISIBLE);
                imageViewBack.setVisibility(View.VISIBLE);
                btnGetDirection.setText("Cancel Direction");
                btnGetDirection.setBackgroundColor(context.getResources().getColor(R.color.red));
                BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                navBar.setVisibility(View.GONE);
//                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
//                    btnGetDirection.setLayoutParams(layoutParams);
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

    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        nearest.setOnClickListener(this);
        if (mGoogleApiClient != null &&
                ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        gpsTracker.stopUsingGPS();
        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
        gpsTracker.stopUsingGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showMessage("Permission Granted, Now you can access location data.");
                } else {
                    showMessage("Permission Denied, You cannot access location data.");
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        this.googleMap = mMap;
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setMyLocationEnabled(true);
//        googleMap.setOnMyLocationChangeListener(myLocationChangeListener);
        initGPS();
        refreshUserGPSLocation();
        geoLocate();
//        getDestinationInfo(new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
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
        Timber.e("onLocationChanged called");
//        Toast.makeText(getActivity(), "Location Changed " + location.getLatitude()
//                + location.getLongitude(), Toast.LENGTH_LONG).show();
        this.mLastLocation = location;
        fetchSensors();
//        if (googleMap != null)
//            googleMap.clear();
//        Timber.d("Firing onLocationChanged..............................................");

//        this.mLastLocation = location;
//        updateUI();
        //  Place current location marker
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Position");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_car_running));
//        googleMap.addMarker(markerOptions);
//        mCurrLocationMarker = googleMap.addMarker(markerOptions);

        //move map camera
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//        if(mCurrLocationMarker == null) {
//            Timber.e("mCurrLocationMarker is null");
////            mCurrLocationMarker = googleMap.addMarker(new MarkerOptions().position(latLng));
////                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_car_running)));
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
    }

    private void updateUI() {
        Timber.d("UI update initiated .............");
        if (null != mLastLocation) {

            LatLng allLatLang = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(allLatLang);
            markerOptions.title("Current Position");
            markerOptions.snippet("Users Basic Information");
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_car_running));
            mCurrLocationMarker = googleMap.addMarker(markerOptions);

            //You can add this lines if you want to show the realtime data change on any TextView
            String lat = String.valueOf(mLastLocation.getLatitude());
            String lng = String.valueOf(mLastLocation.getLongitude());
            Timber.e("Latitude: " + lat + "\n" +
                    "Longitude: " + lng + "\n" +
                    "Accuracy: " + mLastLocation.getAccuracy() + "\n" +
                    "Provider: " + mLastLocation.getProvider());
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
        Timber.e("onInfoWindowClick -> %s", marker.getTitle());
        String spotstatus = marker.getSnippet();
        String spotid = marker.getTitle();

//        if (spotstatus.equalsIgnoreCase("Empty") || spotstatus.equalsIgnoreCase("Occupied.")) {
//            //Toast.makeTextHome(getContext(),"Sensor details will be shown here..",Toast.LENGTH_SHORT);
//
//            selectedSensor = marker.getTitle();
//            Timber.e("openDialog");
//            openDialog(selectedSensor);
//
//            //R.id.nearest:
//            // get the nearest sensor information
//            selectedSensor = marker.getTitle();
//
//            //parkingReqSpot.setText(selectedSensor.toString());
//
//            selectedSensorStatus = "Empty";
//
//            nearest.setText("Reverse Spot");
//            Toast.makeText(getContext(), marker.getTitle() + " Is Selected For Reservation!", Toast.LENGTH_SHORT).show();
//
//            Timber.e("Sensor details will be shown here..");
//        } else {
//            nearest.setText("Find Nearest");
//            selectedSensorStatus = "Occupied";
//            Toast.makeText(getContext(), marker.getTitle() + " Is already occupied, please an empty parking space.", Toast.LENGTH_LONG).show();
//        }
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
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.search:
//                onSearchCalled();
//                return true;
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
            if (resultCode == RESULT_OK) {
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
                              double distance, LatLng location) {
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
        gpsTracker = new GPSTracker(getContext(), this);
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

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));

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

    private void geoLocate() {
        String apiKey = getString(R.string.google_maps_key);
        Log.d("apiKey", apiKey);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            if (getActivity() != null) {
                Places.initialize(getActivity(), apiKey);
            }
        }

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getActivity().getApplicationContext());
        placesClient.findAutocompletePredictions(FindAutocompletePredictionsRequest.builder().build());


        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS, Place.Field.PLUS_CODE, Place.Field.TYPES));
            Timber.d(String.valueOf(Place.Field.ID));
            Timber.d(String.valueOf(Place.Field.NAME));
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    Timber.i("Place: => %s", place.getLatLng());
                    String name = place.getName();
                    Timber.e(name);
                    searchPlaceLatLng = place.getLatLng();
                    Timber.e(String.valueOf(searchPlaceLatLng));

                    if (searchPlaceLatLng != null) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(searchPlaceLatLng);
                        markerOptions.title(name);
                        markerOptions.draggable(true);
                        coordList.add(new LatLng(searchPlaceLatLng.latitude, searchPlaceLatLng.longitude));
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        googleMap.addMarker(markerOptions);
                        //move map camera
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 15));
                        BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
                        navBar.setVisibility(View.GONE);
                        linearLayoutBottom.setVisibility(View.VISIBLE);
                        linearLayoutNameCount.setVisibility(View.VISIBLE);
                        btnGetDirection.setVisibility(View.VISIBLE);
                        imageViewBack.setVisibility(View.VISIBLE);
                        getDirectionButtonClicked = 1;
//                        String url = getDirectionsUrl(new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude), searchPlaceLatLng);
//                        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
//                        if (googleMap != null) {
//                            googleMap.clear();
//                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//                            googleMap.addMarker(markerOptions);
//                            fetchSensors();
//                            taskRequestDirections.execute(url);
//                        }
                    }
                }

                @Override
                public void onError(Status status) {
                    // TODO: Handle the error.
                    Timber.i("An error occurred: -> %s", status);
//                    Toast.makeText(getActivity(), "Place selection failed: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void fetchSensors() {

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_FETCH_SENSORS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Timber.e(" Sensor Response -> %s", response);

//                if (googleMap != null) {
//                    googleMap.clear();
//                }

                try {
                    JSONObject object = new JSONObject(response);

                    JSONArray jsonArray = object.getJSONArray("sensors");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        // Log.e("Sensor Info: ", Jasonobject.toString());
                        // Log.e("Sensor Info: ", Jasonobject.get("uid").toString());

                        String latitude1 = jsonObject.get("latitude").toString();
                        String longitude1 = jsonObject.get("longitude").toString();
                        // find distance
                        double tDistance = distance(Double.valueOf(latitude1), Double.valueOf(longitude1), GlobalVars.location.latitude, GlobalVars.location.longitude);
                        //Log.e("tDistance:",""+tDistance);
                        if (tDistance < nDistance) {
                            nDistance = tDistance;
                            nLatitude = Double.parseDouble(latitude1);
                            nLongitude = Double.parseDouble(longitude1);
                        }

                        if (jsonObject.get("s_status").toString().equalsIgnoreCase("1")) {

                            if (jsonObject.get("reserve_status").toString().equalsIgnoreCase("1")) {
                                sensorStatus = "Occupied";
                                double lat = Double.parseDouble(latitude1);
                                double lon = Double.parseDouble(longitude1);
                                if (googleMap != null) {
                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Booked And Parked").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                    googleMap.addMarker(marker);
                                }
                            } else {
                                sensorStatus = "Empty";
                                double lat = Double.parseDouble(latitude1);
                                double lon = Double.parseDouble(longitude1);

                                if (googleMap != null) {
                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Occupied.").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                    googleMap.addMarker(marker);
                                }
                            }
                        } else {
                            if (jsonObject.get("reserve_status").toString().equalsIgnoreCase("1")) {
                                sensorStatus = "Occupied";
                                double lat = Double.parseDouble(latitude1);
                                double lon = Double.parseDouble(longitude1);

                                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Booked but No Vehicle").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                googleMap.addMarker(marker);

                            } else {
                                sensorStatus = "Empty";
                                double lat = Double.parseDouble(latitude1);
                                double lon = Double.parseDouble(longitude1);
                                if (googleMap != null) {
                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Empty").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_blue));
                                    googleMap.addMarker(marker);
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

        ParkingApp.getInstance().addToRequestQueue(strReq);
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
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }


    private double distance(Double latitude, Double longitude, double e, double f) {
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

//    private void showDirection(String url) throws IOException {
//        final OkHttpClient client = new OkHttpClient();
//        MediaType JSON = MediaType.get("application/json; charset=utf-8");
//
//        okhttp3.Request request = new okhttp3.Request.Builder()
//                .url(url)
//                .build();
//
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                Timber.e("Route onFailire");
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
//                Timber.e("Route onResponse");
//                new ParserTask().execute();
//            }
//        });
//    }

//    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
//
//        // Parsing the data in non-ui thread
//        @Override
//        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
//
//            JSONObject jsonObject;
//            List<List<HashMap<String, String>>> routes = null;
//
//            try {
//                jsonObject = new JSONObject(jsonData[0]);
//                Timber.i("Parser JSONObject: " + jsonObject.toString());
//
//                // Starts parsing data
//                routes = new DirectionsJSONParser().parse(jsonObject);
//                Timber.i("Parser Routes: " + routes.toString());
//            } catch (Exception e) {
//                Timber.i(e.toString());
//                e.printStackTrace();
//            }
//            return routes;
//        }
//
//        // Executes in UI thread, after the parsing process
//        @Override
//        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
//
//            try {
//                ArrayList<LatLng> points = null;
//                PolylineOptions lineOptions = null;
//
//                // Traversing through all the routes
//                for (int i = 0; i < result.size(); i++) {
//                    points = new ArrayList<LatLng>();
//                    lineOptions = new PolylineOptions();
//
//                    // Fetching i-th route
//                    List<HashMap<String, String>> path = result.get(i);
//
//                    // Fetching all the points in i-th route
//                    for (int j = 0; j < path.size(); j++) {
//                        HashMap<String, String> point = path.get(j);
//
//                        double lat = Double.parseDouble(point.get("lat"));
//                        double lng = Double.parseDouble(point.get("lng"));
//                        LatLng position = new LatLng(lat, lng);
//
//                        points.add(position);
//                    }
//
//                    // Adding all the points in the route to LineOptions
//                    lineOptions.addAll(points);
//                    lineOptions.width(5);
//                    lineOptions.color(Color.BLUE);
//                }
//
//                // Drawing polyline in the Google Map for the i-th route
//                googleMap.addPolyline(lineOptions);
//            } catch (Exception e) {
//                Timber.i(e.toString());
//                e.printStackTrace();
//            }
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetMarkerEvent event) {
//        Toast.makeText(getActivity(), "Geche", Toast.LENGTH_SHORT).show();
        layoutVisible(true, name, count, distance, event.location);
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
        Toast.makeText(getActivity(), "Adapter Click e Geche", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                if(googleMap!=null)
//                    googleMap.clear();
                // Do something after 2s = 2000ms
//                progressDialog.dismiss();
                btnGetDirection.setVisibility(View.VISIBLE);
//                linearLayoutNameCount.setVisibility(View.VISIBLE);
                linearLayoutBottom.setVisibility(View.VISIBLE);
                imageViewBack.setVisibility(View.VISIBLE);
                String url = getDirectionsUrl(new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude), event.location);
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);
            }
        }, 1000);


//        String url = getDirectionsUrl(new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude), event.location);
//        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
//        taskRequestDirections.execute(url);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchDirectionEvent(GetDirectionForSearchEvent event) {
        Toast.makeText(getActivity(), "Search Click e Geche", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                if(googleMap!=null)
//                    googleMap.clear();
//                progressDialog.dismiss();
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
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 15));
                btnGetDirection.setVisibility(View.VISIBLE);
//                linearLayoutNameCount.setVisibility(View.VISIBLE);
                linearLayoutBottom.setVisibility(View.VISIBLE);
                imageViewBack.setVisibility(View.VISIBLE);
                String url = getDirectionsUrl(new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude), searchPlaceLatLng);
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);
            }
        }, 1000);

//        String url = getDirectionsUrl(new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude), event.location);
//        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
//        taskRequestDirections.execute(url);
    }

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

//            if (lists.size() < 1) {
//                Toast.makeText(getActivity(), "No Points", Toast.LENGTH_SHORT).show();
//                return;
//            }

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat, lon));
                }


                polylineOptions.addAll(points);
                polylineOptions.width(7);
                if (flag == 1) {
//                    if (googleMap != null)
//                        googleMap.clear();
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

            }

            if (polylineOptions != null) {
                googleMap.addPolyline(polylineOptions);

            } else {
                Toast.makeText(getActivity(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }
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
        Timber.i("" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return (Radius * c);
    }
}