package www.fiberathome.com.parkingapp.ui.booking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.response.booking.BookingParkStatusResponse;
import www.fiberathome.com.parkingapp.model.response.booking.CloseReservationResponse;
import www.fiberathome.com.parkingapp.ui.booking.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.ui.schedule.ScheduleFragment;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.MathUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

import static www.fiberathome.com.parkingapp.ui.home.HomeFragment.PLAY_SERVICES_ERROR_CODE;

public class BookingParkFragment extends BaseFragment implements OnMapReadyCallback {
    private long arrived, departure;
    private TextView tvArrivedTime, tvDepartureTime, tvTimeDifference, tvTermsCondition, tvParkingAreaName, tvCountDown;
    private long difference;
    private Button btnMore;
    private Button btnCarDeparture;
    private Button btnLiveParking;
    private FragmentChangeListener listener;
    private CountDownTimer countDownTimer;

    private GoogleMap mMap;
    SupportMapFragment supportMapFragment;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    public static BookingParkStatusResponse.Sensors sensors;
    private HomeActivity context;
    private BookingParkStatusResponse.Sensors mSensors;

    public BookingParkFragment() {
        // Required empty public constructor
    }

    public static BookingParkFragment newInstance(BookingParkStatusResponse.Sensors mSensors) {
        sensors = mSensors;
        return new BookingParkFragment();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_booked, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = (HomeActivity) getActivity();
        initView(view);
        listener = (FragmentChangeListener) getActivity();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        if (isServicesOk()) {
            supportMapFragment = SupportMapFragment.newInstance();

            if (context != null) {
                context.setActionToolBarVisibilityGone();
                FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction().
                        replace(R.id.map, supportMapFragment);
                ft.commit();
                supportMapFragment.getMapAsync(this);
                if (sensors != null) {
                    //from HomeActivity
                    tvParkingAreaName.setText(sensors.getParkingArea());
                    tvArrivedTime.setText(sensors.getTimeStart());
                    tvDepartureTime.setText(sensors.getTimeEnd());
                    String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    findDifference(currentDateandTime, sensors.getTimeEnd());

                } else {
                    //from HomeFragment
                    getBookingParkStatus(Preferences.getInstance(context).getUser().getMobileNo());
                }
            } else {
                ToastUtils.getInstance().showToastMessage(context, "Unable to load map");
            }
        } else {
            ToastUtils.getInstance().showToastMessage(context, "Play services are required by this application");
        }
        setListeners();
    }

    private void setListeners() {
        btnMore.setOnClickListener(v -> {
            ScheduleFragment scheduleFragment = new ScheduleFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("m", true);
            bundle.putLong("a", arrived);
            bundle.putLong("d", departure);
            scheduleFragment.setArguments(bundle);
            listener.fragmentChange(scheduleFragment);
        });

        btnCarDeparture.setOnClickListener(v -> {
            endBooking();
        });

        btnLiveParking.setOnClickListener(v -> Toast.makeText(context, "Live Parking Coming Soon!!!", Toast.LENGTH_SHORT).show());

        tvTermsCondition.setOnClickListener(v -> Toast.makeText(context, "T&C Coming Soon!!!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        setMapSettings(mMap);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                        Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                    Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }

        if (mSensors != null) {
            LatLng bookedLocation = new LatLng(MathUtils.getInstance().convertToDouble(mSensors.getLatitude()), MathUtils.getInstance().convertToDouble(mSensors.getLongitude()));
            mMap.addMarker(new MarkerOptions()
                    .position(bookedLocation)
                    .title("Departure Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_gray)));
        } else if (sensors != null) {
            LatLng bookedLocation = new LatLng(MathUtils.getInstance().convertToDouble(sensors.getLatitude()), MathUtils.getInstance().convertToDouble(sensors.getLongitude()));
            mMap.addMarker(new MarkerOptions()
                    .position(bookedLocation)
                    .title("Departure Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_gray)));
        }
    }

    private void setMapSettings(GoogleMap mMap) {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        mMap.setBuildingsEnabled(false);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(false);

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //move map camera
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latLng.latitude, latLng.longitude)).zoom(13.5f).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(context)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(context,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    // if not allow a permission, the application will exit
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    context.finish();
                    System.exit(0);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

    private void initView(View view) {
        tvArrivedTime = view.findViewById(R.id.tvArrivedTime);
        tvDepartureTime = view.findViewById(R.id.tvDepartureTime);
        tvTimeDifference = view.findViewById(R.id.tvDifferenceTime);
        tvTermsCondition = view.findViewById(R.id.textViewTermsCondition);
        btnMore = view.findViewById(R.id.btnMore);
        btnCarDeparture = view.findViewById(R.id.btnCarDeparture);
        btnLiveParking = view.findViewById(R.id.btnLiveParking);
        tvParkingAreaName = view.findViewById(R.id.tvParkingAreaName);
        tvCountDown = view.findViewById(R.id.tvCountDown);
    }

    private String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private boolean isServicesOk() {

        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();

        int result = googleApi.isGooglePlayServicesAvailable(context);

        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApi.isUserResolvableError(result)) {
            Dialog dialog = googleApi.getErrorDialog((Activity) context, result, PLAY_SERVICES_ERROR_CODE, task ->
                    ToastUtils.getInstance().showToastMessage(context, "Dialog is cancelled by User"));
            if (dialog != null) {
                dialog.show();
            }
        }
        return false;
    }

    private void endBooking() {
        showLoading(context);
        String spotUid;
        if (mSensors != null) {
            spotUid = mSensors.getSpotId();
        } else {
            spotUid = sensors.getSpotId();
        }
        ApiService request = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<CloseReservationResponse> call = request.endReservation(Preferences.getInstance(context).getUser().getMobileNo(), spotUid);
        call.enqueue(new Callback<CloseReservationResponse>() {
            @Override
            public void onResponse(@NonNull Call<CloseReservationResponse> call, @NonNull Response<CloseReservationResponse> response) {
                hideLoading();
                countDownTimer.cancel();
                sensors = null;
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        DialogUtils.getInstance().alertDialog(context,
                                (Activity) context,
                                response.body().getMessage(),
                                context.getString(R.string.ok), "",
                                new DialogUtils.DialogClickListener() {
                                    @Override
                                    public void onPositiveClick() {
                                        Timber.e("Positive Button clicked");
                                        Preferences.getInstance(context).clearBooking();
                                        listener.fragmentChange(new HomeFragment());
                                    }

                                    @Override
                                    public void onNegativeClick() {
                                    }
                                }).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CloseReservationResponse> call, @NonNull Throwable t) {
                Timber.e("onFailure -> %s", t.getMessage());
                countDownTimer.cancel();
                hideLoading();
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
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
                        mSensors = response.body().getSensors();
                        if (mSensors != null) {
                            tvArrivedTime.setText(mSensors.getTimeStart());
                            tvDepartureTime.setText(mSensors.getTimeEnd());
                            tvParkingAreaName.setText(mSensors.getParkingArea());
                            findDifference(mSensors.getTimeStart(), mSensors.getTimeEnd());
                        } else {
                            //ToastUtils.getInstance().showToast(context, "Sorry, there is no parking");
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingParkStatusResponse> call, @NonNull Throwable t) {
                Timber.e("onFailure -> %s", t.getMessage());
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
                hideLoading();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void startCountDown(long timerMilliDifference) {
        long millis = Preferences.getInstance(context).getBooked().getDepartedDate();
        countDownTimer = new CountDownTimer(timerMilliDifference, 1000) {
            @SuppressLint("DefaultLocale")
            public void onTick(long millisUntilFinished) {
                tvCountDown.setText("" + String.format("%d min, %d sec remaining",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                //tvCountDown.setText(context.getResources().getString(R.string.please_wait));
            }
        }.start();
    }

    public void findDifference(String start_date,
                               String end_date) {

        // SimpleDateFormat converts the
        // string format to date object
        SimpleDateFormat sdf
                = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss");

        // try Block
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            // Calucalte time difference
            // in milliseconds
            if (d1 != null && d2 != null) {
                long difference_In_Time
                        = d2.getTime() - d1.getTime();

                // Calculate time difference in
                // seconds, minutes, hours, years,
                // and days
                long difference_In_Seconds
                        = (difference_In_Time
                        / 1000)
                        % 60;

                long difference_In_Minutes
                        = (difference_In_Time
                        / (1000 * 60))
                        % 60;

                long difference_In_Hours
                        = (difference_In_Time
                        / (1000 * 60 * 60))
                        % 24;

                long difference_In_Years
                        = (difference_In_Time
                        / (1000l * 60 * 60 * 24 * 365));

                long difference_In_Days
                        = (difference_In_Time
                        / (1000 * 60 * 60 * 24))
                        % 365;

                // Print the date difference in
                // years, in days, in hours, in
                // minutes, and in seconds
                tvTimeDifference.setText(difference_In_Hours + " hr " + difference_In_Minutes + " min " + difference_In_Seconds + " sec");
                startCountDown(difference_In_Time);

                System.out.print(
                        "Difference "
                                + "between two dates is: ");

                System.out.println(
                        difference_In_Years
                                + " years, "
                                + difference_In_Days
                                + " days, "
                                + difference_In_Hours
                                + " hours, "
                                + difference_In_Minutes
                                + " minutes, "
                                + difference_In_Seconds
                                + " seconds");
            }
        }

        // catch the Exception
        catch (ParseException e) {
            e.printStackTrace();
        }
    }
}