package www.fiberathome.com.parkingapp.ui.fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.GoogleMapWebService.DirectionsJSONParser;
import www.fiberathome.com.parkingapp.GoogleMapWebService.DirectionsParser;
import www.fiberathome.com.parkingapp.GoogleMapWebService.GooglePlaceSearchNearbySearchListener;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.eventBus.SetMarkerEvent;
import www.fiberathome.com.parkingapp.gps.GPSTracker;
import www.fiberathome.com.parkingapp.gps.GPSTrackerListener;
import www.fiberathome.com.parkingapp.model.GlobalVars;
import www.fiberathome.com.parkingapp.model.MyLocation;
import www.fiberathome.com.parkingapp.model.SensorList;
import www.fiberathome.com.parkingapp.module.PlayerPrefs;
import www.fiberathome.com.parkingapp.ui.DialogForm;
import www.fiberathome.com.parkingapp.utils.AppConfig;

// Add an import statement for the client library.

/**
 * A simple {@link Fragment} subclass.
 */


public class HomeFragment extends Fragment implements
        OnMapReadyCallback, GooglePlaceSearchNearbySearchListener, GoogleMap.OnMarkerClickListener,
        GPSTrackerListener, GoogleMap.OnInfoWindowClickListener, View.OnClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraIdleListener, LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

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
    private View mapView;;
    private GoogleMap googleMap;
    private Marker userLocationMarker;
    private GPSTracker gpsTracker;
    private Button nearest;
    private boolean isGoogleDone = false;
    // global distance
    private boolean isMyServerDone = false;
    private String sensorStatus = "Occupied";
    private static final int LOCATION_REQUEST = 500;
    public int flag = 0;
    ArrayList<LatLng> listPoints;
    public static DecimalFormat df1 = new DecimalFormat(".##");
    public static DecimalFormat df2 = new DecimalFormat(".##");

    private List<LatLng> polyLineList;
    private Marker marker;
    private LocationManager mLocationManager;

    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    public HomeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        try {
            initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        nearest.setOnClickListener(this);
    }

    private void drawMarker(Location location) {
        if (googleMap != null) {
            googleMap.clear();
            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .position(gps)

                    .title("Current Position"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 12));
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        gpsTracker.stopUsingGPS();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
        gpsTracker.stopUsingGPS();
    }

    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
        this.googleMap.setOnInfoWindowClickListener(this);

        initGPS();
        refreshUserGPSLocation();
        geoLocate();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
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
                    polylineOptions.color(Color.GREEN);
                    polylineOptions.width(4);
                } else if (flag == 2) {
                    polylineOptions.color(Color.RED);
                    polylineOptions.width(4);
                }
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

    @Override
    public void onLocationChanged(Location location) {

//        if (googleMap!= null){
//            googleMap.clear();
//        }
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = googleMap.addMarker(markerOptions);

        //move map camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }


    }

    double showDistance(LatLng from, LatLng to) {

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
        //Toast.makeText(getContext(),marker.getTitle(),Toast.LENGTH_LONG).show();
        String spotstatus = marker.getSnippet();
        String spotid = marker.getTitle();

        if (spotstatus.equalsIgnoreCase("Empty") || spotstatus.equalsIgnoreCase("Occupied.")) {
            //Toast.makeTextHome(getContext(),"Sensor details will be shown here..",Toast.LENGTH_SHORT);

            selectedSensor = marker.getTitle();
            Log.e("openDialog", "openDialog");
            openDialog(selectedSensor);


            //R.id.nearest:
            // get the nearest sensor information
            selectedSensor = marker.getTitle();

            //parkingReqSpot.setText(selectedSensor.toString());

            selectedSensorStatus = "Empty";

            nearest.setText("Reverse Spot");
            Toast.makeText(getContext(), marker.getTitle() + " Is Selected For Reservation!", Toast.LENGTH_SHORT).show();

            Log.e("MarkerClick", "Sensor details will be shown here..");
        } else {
            nearest.setText("Find Nearest");
            selectedSensorStatus = "Occupied";
            Toast.makeText(getContext(), marker.getTitle() + " Is already occupied, please an empty parking space.", Toast.LENGTH_LONG).show();
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
        }
    }

    /**
     * CHECK PERMISSION FOR: ACCESS FINE LOCATION & ACCESS COARSE LOCATION
     * ===================================================================================
     * if there is not permission granted, just enable the permission.
     */
    private boolean checkPermission() {
        int access_fine_location = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        int access_coarse_location = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION);

        return access_fine_location == PackageManager.PERMISSION_GRANTED && access_coarse_location == PackageManager.PERMISSION_GRANTED;
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
        gpsTracker = new GPSTracker(getContext(), this);
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();

            if (latitude != 0.0 || longitude != 0.0) {
                GlobalVars.location = new MyLocation(latitude, longitude);
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
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
//            googleMap.setMyLocationEnabled(true);
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
//            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            // Enable / Disable zooming functionality
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
        }
    }

    private void animateMarker(Marker marker, Location location) {
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
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
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
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchPlaceLatLng, 11));

                        String url = getDirectionsUrl(new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude), searchPlaceLatLng);
                        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                        taskRequestDirections.execute(url);
                    }

                }

                @Override
                public void onError(Status status) {
                    // TODO: Handle the error.
                    Timber.i("An error occurred: " + status);
                    Toast.makeText(getActivity(), "Place selection failed: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public void fetchSensors() {

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_FETCH_SENSORS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Timber.e(" Sensor Response -> %s", response);

                if (googleMap != null) {
                    googleMap.clear();
                }

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
                                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Booked And Parked").icon(BitmapDescriptorFactory.fromResource(R.drawable.mapiconyellow));
                                googleMap.addMarker(marker);}

                            } else {
                                sensorStatus = "Empty";
                                double lat = Double.parseDouble(latitude1);
                                double lon = Double.parseDouble(longitude1);

                                if (googleMap != null) {
                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Occupied.").icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon));
                                    googleMap.addMarker(marker);
                                }
                            }
                        } else {
                            if (jsonObject.get("reserve_status").toString().equalsIgnoreCase("1")) {
                                sensorStatus = "Occupied";
                                double lat = Double.parseDouble(latitude1);
                                double lon = Double.parseDouble(longitude1);

                                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Booked but No Vehicle").icon(BitmapDescriptorFactory.fromResource(R.drawable.mapiconblue));
                                googleMap.addMarker(marker);

                            } else {
                                sensorStatus = "Empty";
                                double lat = Double.parseDouble(latitude1);
                                double lon = Double.parseDouble(longitude1);
                                if (googleMap != null) {
                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title(jsonObject.get("uid").toString()).snippet("Empty").icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicongreen));
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

    private void showDirection(String url) throws IOException {
        final OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Timber.e("Route onFailire");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Timber.e("Route onResponse");
                new ParserTask().execute();
            }
        });
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jsonObject = new JSONObject(jsonData[0]);
                Log.i(TAG, "Parser JSONObject: " + jsonObject.toString());

                // Starts parsing data
                routes = new DirectionsJSONParser().parse(jsonObject);
                Log.i(TAG, "Parser Routes: " + routes.toString());
            } catch (Exception e) {
                Log.i(TAG, e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            try {
                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;

                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(5);
                    lineOptions.color(Color.BLUE);
                }

                // Drawing polyline in the Google Map for the i-th route
                googleMap.addPolyline(lineOptions);
            } catch (Exception e) {
                Timber.i(e.toString());
                e.printStackTrace();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetMarkerEvent event) {
//        Toast.makeText(getActivity(), "Geche", Toast.LENGTH_SHORT).show();
        if (googleMap != null) {
            googleMap.clear();
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(event.location));
        String url = getDirectionsUrl(new LatLng(GlobalVars.getUserLocation().latitude, GlobalVars.getUserLocation().longitude), event.location);
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
        taskRequestDirections.execute(url);
    }

}


