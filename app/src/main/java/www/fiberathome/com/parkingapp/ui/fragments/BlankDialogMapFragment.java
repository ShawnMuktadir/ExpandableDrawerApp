package www.fiberathome.com.parkingapp.ui.fragments;


import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

import www.fiberathome.com.parkingapp.GoogleMapWebService.GooglePlaceSearchNearbySearchListener;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.gps.GPSTracker;
import www.fiberathome.com.parkingapp.gps.GPSTrackerListener;
import www.fiberathome.com.parkingapp.model.GlobalVars;
import www.fiberathome.com.parkingapp.model.MyLocation;
import www.fiberathome.com.parkingapp.model.SensorList;
import www.fiberathome.com.parkingapp.module.PlayerPrefs;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankDialogMapFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GooglePlaceSearchNearbySearchListener,
        GoogleMap.OnMarkerClickListener,
        GPSTrackerListener,
        GoogleMap.OnInfoWindowClickListener,
        View.OnClickListener {


    public BlankDialogMapFragment() {
        // Required empty public constructor
    }


    private static final String TAG = HomeFragment.class.getSimpleName();

    // global view
    View view;

    // google map objects
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleMap googleMap;
    private Marker placeMarker;
    private List<Marker> markers = new ArrayList<>();
    private Marker userLocationMarker;
    private GPSTracker gpsTracker;


    private ProgressDialog progressDialog;
    private Button nearest;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private TextView userFullName;
    private TextView userVehicleNo;
    private ImageView userProfilePic;

    private boolean isGoogleDone = false;
    private boolean isMyServerDone = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Payment");
        view = inflater.inflate(R.layout.fragment_home, container, false);


        initialize();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        nearest.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        gpsTracker.stopUsingGPS();
    }

    @Override
    public void onStop() {
        super.onStop();
        gpsTracker.stopUsingGPS();
    }


    private void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private void initialize() {

        PlayerPrefs.Initialize(getContext());
        initMap();
        initComponents();
        initGPS();

    }


    private void initMap() {
        if (googleMap == null) {
            SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
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
        } else {
            gpsTracker.showSettingsAlert();
        }

    }

    private void initComponents() {
        nearest = view.findViewById(R.id.nearest);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        refreshUserGPSLocation();
        googleMap.setOnInfoWindowClickListener(this);


    }
    private void refreshUserGPSLocation() {
        if (userLocationMarker != null)
            userLocationMarker.remove();

        GlobalVars.IsFakeGPS = false;
        MyLocation userLocation = GlobalVars.getUserLocation();
        if (userLocation != null) {
            LatLng userLatLng = new LatLng(userLocation.latitude, userLocation.longitude);
            //userLocationMarker = googleMap.addMarker(new MarkerOptions().position(userLatLng).title("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mylocation)));
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 18));
            //googleMap.setTrafficEnabled(true);

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

        }

    }


//    public  void userLocationFAB(View view){
//        FloatingActionButton FAB = view.findViewById(R.id.myLocationButton);
//        FAB.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("MissingPermission")
//            @Override
//            public void onClick(View view) {
//                if (gpsTracker.canGetLocation()) {
//                    double latitude = gpsTracker.getLatitude();
//                    double longitude = gpsTracker.getLongitude();
//
//                    LatLng latLng = new LatLng(latitude, longitude);
//                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
//                    googleMap.animateCamera(cameraUpdate);
//
//                } else {
//                    gpsTracker.showSettingsAlert();
//                }
//            }
//        });
//    }





    @Override
    public void onGPSTrackerLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        showMessage(latitude + "---" + longitude);
        if (latitude != 0 || longitude != 0) {
            GlobalVars.location = new MyLocation(latitude, longitude);
        }

    }


    @Override
    public void onGPSTrackerStatusChanged(String provider, int status, Bundle extras) {
        showMessage("Status Changed");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.nearest:
                // get the nearest sensor information
                nearest.setText("Reverse Spot");
                break;
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onGooglePlaceSearchStart() {

    }

    @Override
    public void onGooglePlaceSearchSuccess(SensorList sensorList) {

    }

}
