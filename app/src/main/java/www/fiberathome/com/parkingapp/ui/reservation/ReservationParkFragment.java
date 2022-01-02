package www.fiberathome.com.parkingapp.ui.reservation;

import static www.fiberathome.com.parkingapp.utils.GoogleMapHelper.defaultMapSettings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.data.model.BookedPlace;
import www.fiberathome.com.parkingapp.data.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.data.model.response.reservation.BookingParkStatusResponse;
import www.fiberathome.com.parkingapp.data.model.response.reservation.CloseReservationResponse;
import www.fiberathome.com.parkingapp.databinding.FragmentBookingParkBinding;
import www.fiberathome.com.parkingapp.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.ui.reservation.schedule.ScheduleActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DateTimeUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.MathUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("SimpleDateFormat")
public class ReservationParkFragment extends BaseFragment implements OnMapReadyCallback {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static BookingParkStatusResponse.Sensors sensors;

    Handler mHandler = new Handler();
    private FragmentChangeListener listener;
    private CountDownTimer countDownTimer;
    private GoogleMap mMap;

    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    protected Marker mCurrLocationMarker;

    private FragmentBookingParkBinding binding;
    private ReservationViewModel reservationViewModel;
    private FusedLocationProviderClient mFusedLocationClient;
    private BaseActivity context;

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

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            if (sensors != null) {
                String extraTime = getTimeDifference(System.currentTimeMillis() - DateTimeUtils.getInstance().getStringDateToMillis(sensors.getTimeEnd()) > 0 ? (System.currentTimeMillis() - DateTimeUtils.getInstance().getStringDateToMillis(sensors.getTimeEnd())) : 0);
                binding.tvExtraParkingTime.setText(String.format(context.getResources().getString(R.string.exceedParktime) + " %s", extraTime));

                if (System.currentTimeMillis() - DateTimeUtils.getInstance().getStringDateToMillis(sensors.getTimeEnd()) >= 0) {
                    binding.tvExtraParkingTime.setTextColor(context.getResources().getColor(R.color.red));
                }
            }
            mHandler.postDelayed(mHandlerTask, 1000);
        }
    };

    public ReservationParkFragment() {
        // Required empty public constructor
    }

    public static ReservationParkFragment newInstance(BookingParkStatusResponse.Sensors mSensors) {
        sensors = mSensors;
        return new ReservationParkFragment();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBookingParkBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof HomeActivity) {
            context = (HomeActivity) getActivity();
        } else if (getActivity() instanceof ReservationActivity) {
            context = (ReservationActivity) getActivity();
        } else if (getActivity() instanceof ScheduleActivity) {
            context = (ScheduleActivity) getActivity();
        }
        reservationViewModel = new ViewModelProvider(this).get(ReservationViewModel.class);
        mHandlerTask.run();
        setBroadcast();
        listener = (FragmentChangeListener) getActivity();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (ApplicationUtils.isServicesOk(context)) {
            SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
            if (context != null) {
                if (getActivity() instanceof HomeActivity) {
                    ((HomeActivity) context).setActionToolBarVisibilityGone();
                } else if (getActivity() instanceof ReservationActivity) {
                    ((ReservationActivity) context).setActionToolBarVisibilityGone();
                } else if (getActivity() instanceof ScheduleActivity) {
                    ((ScheduleActivity) context).setActionToolBarVisibilityGone();
                }
                FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction().
                        replace(R.id.map, supportMapFragment);
                ft.commit();
                supportMapFragment.getMapAsync(this);
                if (sensors != null) {
                    BookedPlace mBookedPlace = Preferences.getInstance(context).getBooked();
                    mBookedPlace.setExceedRunning(true);
                    Preferences.getInstance(context).setBooked(mBookedPlace);
                    ApplicationUtils.startBookingExceedService(context, DateTimeUtils.getInstance().getStringDateToMillis(sensors.getTimeEnd()));
                    binding.tvArrivedTime.setText(String.format(context.getResources().getString(R.string.booking_time) + " %s", sensors.getTimeStart()));
                    binding.tvDepartureTime.setText(String.format(context.getResources().getString(R.string.departuretime) + " %s", sensors.getTimeEnd()));
                    binding.tvParkingAreaName.setText(sensors.getParkingArea());
                    if (Preferences.getInstance(context).getBooked().getPsId() != null && !Preferences.getInstance(context).getBooked().getPsId().equalsIgnoreCase("")) {
                        binding.tvParkingPsId.setText("( " + context.getResources().getString(R.string.parking_spot_id) + " " + Preferences.getInstance(context).getBooked().getPsId() + " )");
                    } else {
                        binding.tvParkingPsId.setText("");
                    }
                    String currentDateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    findDifference(currentDateAndTime, sensors.getTimeEnd(), "");
                    findDifference(sensors.getTimeStart(), sensors.getTimeEnd(), "TimeStart");
                    String extraTime;
                    String earlyParkingTime;
                    earlyParkingTime = getTimeDifference(DateTimeUtils.getInstance().getStringDateToMillis(sensors.getTimeStart()) - DateTimeUtils.getInstance().getStringDateToMillis(sensors.getP_date()) >= 0 ? (DateTimeUtils.getInstance().getStringDateToMillis(sensors.getTimeStart()) - DateTimeUtils.getInstance().getStringDateToMillis(sensors.getP_date())) : 0);
                    extraTime = getTimeDifference(System.currentTimeMillis() - DateTimeUtils.getInstance().getStringDateToMillis(sensors.getTimeEnd()) > 0 ? (System.currentTimeMillis() - DateTimeUtils.getInstance().getStringDateToMillis(sensors.getTimeEnd())) : 0);
                    binding.tvEarlyParkingTime.setText(context.getResources().getString(R.string.earlyparkingtime) + " " + earlyParkingTime);
                    binding.tvExtraParkingTime.setText(context.getResources().getString(R.string.exceedParktime) + " " + extraTime);
                    if (System.currentTimeMillis() - DateTimeUtils.getInstance().getStringDateToMillis(sensors.getTimeEnd()) >= 0) {
                        binding.tvExtraParkingTime.setTextColor(context.getResources().getColor(R.color.red));
                    } else {
                        findDifference(currentDateAndTime, sensors.getTimeEnd(), "");
                    }
                    if (DateTimeUtils.getInstance().getStringDateToMillis(sensors.getTimeStart()) - DateTimeUtils.getInstance().getStringDateToMillis(sensors.getP_date()) >= 0) {
                        binding.tvEarlyParkingTime.setTextColor(context.getResources().getColor(R.color.red));
                    }
                } else {
                    ToastUtils.getInstance().showToastMessage(context, "sensor null");
                }
            } else {
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.unable_to_load_map));
            }
        } else {
            ToastUtils.getInstance().showToastMessage(context, "Play services are required by this application");
        }
        setListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setBroadcast() {
        LocalBroadcastManager.getInstance(context).registerReceiver(bookingEndedReceiver,
                new IntentFilter("booking_ended"));
    }

    private final BroadcastReceiver bookingEndedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context mContext, Intent intent) {
            // Get extra data included in the Intent
            if (isAdded() && mMap != null) {
                try {
                    mHandler.removeCallbacks(mHandlerTask);
                    sensors = null;
                    Preferences.getInstance(context).clearBooking();
                    DialogUtils.getInstance().alertDialog(context,
                            context,
                            context.getResources().getString(R.string.as_your_reservation_time_had_exceed_over_five_mins),
                            context.getResources().getString(R.string.ok), "",
                            new DialogUtils.DialogClickListener() {
                                @Override
                                public void onPositiveClick() {
                                    Timber.e("Positive Button clicked");
                                    listener.fragmentChange(HomeFragment.newInstance());
                                }

                                @Override
                                public void onNegativeClick() {
                                }
                            }).show();
                    Timber.e("BroadcastReceiver called");
                } catch (Exception e) {
                    Timber.e(e.getCause());
                    e.getCause();
                }
            }
        }
    };

    private void setListeners() {
        binding.btnCarDeparture.setOnClickListener(v -> {
            if (ConnectivityUtils.getInstance().checkInternet(context)) {
                endBooking();
            }
        });

        binding.fabCurrentLocation.setOnClickListener(v -> {
            if (mLastLocation != null)
                animateCamera(mLastLocation);
        });

        binding.btnLiveParking.setOnClickListener(v -> Toast.makeText(context, "Live Parking Coming Soon!!!", Toast.LENGTH_SHORT).show());

        binding.textViewTermsCondition.setOnClickListener(v -> Toast.makeText(context, "T&C Coming Soon!!!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        buildLocationRequest();
        defaultMapSettings(context, mMap, mFusedLocationClient, mLocationRequest, mLocationCallback);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                        Objects.requireNonNull(Looper.myLooper()));
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                    Objects.requireNonNull(Looper.myLooper()));
            mMap.setMyLocationEnabled(true);
        }

        try {
            if (sensors != null) {
                LatLng bookedLocation = new LatLng(MathUtils.getInstance().convertToDouble(sensors.getLatitude()), MathUtils.getInstance().convertToDouble(sensors.getLongitude()));
                mMap.addMarker(new MarkerOptions()
                        .position(bookedLocation)
                        .title("Car Parked Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_car_running)));
            }
        } catch (Exception e) {
            Timber.e(e.getCause());
            e.getCause();
        }

    }

    public void animateCamera(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (mMap != null)
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)));
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(13.8f).build();
    }

    private void buildLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setSmallestDisplacement(10f);
    }

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
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(context,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
                                mLocationCallback, Objects.requireNonNull(Looper.myLooper()));
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
        if (isAdded()) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            mHandler.removeCallbacks(mHandlerTask);
        }
    }

    private void endBooking() {
        showLoading(context);
        String spotUid = "";
        String reservationId = "";
        if (sensors != null) {
            spotUid = sensors.getSpotId();
            reservationId = sensors.getId();
        }

        reservationViewModel.initCloseReservation(Preferences.getInstance(context).getUser().getMobileNo(), spotUid, reservationId);
        reservationViewModel.getCloseReservationMutableLiveData().observe(requireActivity(), (@NonNull CloseReservationResponse response) -> {
            hideLoading();
            if (!response.getError()) {
                if (isAdded() && countDownTimer != null) {
                    countDownTimer.cancel();
                }
                mHandler.removeCallbacks(mHandlerTask);
                sensors = null;
                Preferences.getInstance(context).clearBooking();
                ApplicationUtils.stopBookingTrackService(context);
                DialogUtils.getInstance().alertDialog(context,
                        context,
                        context.getResources().getString(R.string.reservation_closed_successfully),
                        context.getResources().getString(R.string.ok), "",
                        new DialogUtils.DialogClickListener() {
                            @Override
                            public void onPositiveClick() {
                                if (getActivity() instanceof HomeActivity) {
                                    listener.fragmentChange(HomeFragment.newInstance());
                                } else if (getActivity() instanceof ReservationActivity) {
                                    context.startActivity(HomeActivity.class);
                                } else if (getActivity() instanceof ScheduleActivity) {
                                    context.startActivity(HomeActivity.class);
                                }
                            }

                            @Override
                            public void onNegativeClick() {
                            }
                        }).show();
            } else {
                if (isAdded() && countDownTimer != null) {
                    countDownTimer.cancel();
                }
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private String getTimeDifference(long difference) {

        return String.format("%02d hr: %02d min: %02d sec",
                TimeUnit.MILLISECONDS.toHours(difference),
                TimeUnit.MILLISECONDS.toMinutes(difference) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(difference)), TimeUnit.MILLISECONDS.toSeconds(difference) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toMinutes(difference) // The change is in this line
                        ));
    }

    @SuppressLint("SetTextI18n")
    private void startCountDown(long timerMilliDifference) {
        countDownTimer = new CountDownTimer(timerMilliDifference, 1000) {
            @SuppressLint("DefaultLocale")
            public void onTick(long millisUntilFinished) {
                binding.tvCountDown.setText("" + String.format(context.getString(R.string.remaining_time) + " %d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                binding.tvCountDown.setText(context.getString(R.string.remaining_time) + "00:00");
            }
        }.start();
    }

    @SuppressLint("SetTextI18n")
    public void findDifference(String start_date,
                               String end_date, String timeStart) {

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

            // Calculate time difference
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
                        / (1000L * 60 * 60 * 24 * 365));

                long difference_In_Days
                        = (difference_In_Time
                        / (1000 * 60 * 60 * 24))
                        % 365;

                // Print the date difference in
                // years, in days, in hours, in
                // minutes, and in seconds
                if (timeStart.equalsIgnoreCase("TimeStart")) {
                    binding.tvDifferenceTime.setText(difference_In_Hours + " hr " + difference_In_Minutes + " min " + difference_In_Seconds + " sec");
                } else {
                    startCountDown(difference_In_Time);
                }

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