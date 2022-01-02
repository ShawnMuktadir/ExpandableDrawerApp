package www.fiberathome.com.parkingapp.ui.home;

import static www.fiberathome.com.parkingapp.data.model.data.Constants.LANGUAGE_BN;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.PermissionToken;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.data.model.data.preference.LanguagePreferences;
import www.fiberathome.com.parkingapp.data.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.data.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.data.model.response.reservation.BookingParkStatusResponse;
import www.fiberathome.com.parkingapp.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.ui.NavigationActivity;
import www.fiberathome.com.parkingapp.ui.booking.BookingFragment;
import www.fiberathome.com.parkingapp.ui.booking.BookingParkFragment;
import www.fiberathome.com.parkingapp.ui.booking.PaymentFragment;
import www.fiberathome.com.parkingapp.ui.auth.login.LoginActivity;
import www.fiberathome.com.parkingapp.ui.followUs.FollowUsFragment;
import www.fiberathome.com.parkingapp.ui.law.LawFragment;
import www.fiberathome.com.parkingapp.ui.parking.ParkingFragment;
import www.fiberathome.com.parkingapp.ui.permission.PermissionActivity;
import www.fiberathome.com.parkingapp.ui.permission.listener.PermissionInterface;
import www.fiberathome.com.parkingapp.ui.privacyPolicy.PrivacyPolicyFragment;
import www.fiberathome.com.parkingapp.ui.profile.ProfileFragment;
import www.fiberathome.com.parkingapp.ui.reservation.ReservationFragment;
import www.fiberathome.com.parkingapp.ui.reservation.ReservationParkFragment;
import www.fiberathome.com.parkingapp.ui.reservation.ReservationViewModel;
import www.fiberathome.com.parkingapp.ui.reservation.schedule.ScheduleFragment;
import www.fiberathome.com.parkingapp.ui.settings.SettingsFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
public class HomeActivity extends NavigationActivity implements FragmentChangeListener, PermissionInterface {

    public static final int GPS_REQUEST_CODE = 9003;

    private LocationCallback locationCallback;
    private Location lastLocation;
    private Toast exitToast;

    private int exitCounter = 1;
    private double lat;
    private double lng;
    private String areaName;
    private String count;
    private String placeId;

    //Boolean variable to mark if the transaction is safe
    private boolean isTransactionSafe;

    //Boolean variable to mark if there is any transaction pending
    private boolean isTransactionPending;

    private Context context;
    private ReservationViewModel reservationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        reservationViewModel = new ViewModelProvider(this).get(ReservationViewModel.class);

        setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));

        //location permission check
        handleLocationPermissionCheck(context);

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        buildLocationCallBack();
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (fusedLocationProviderClient != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Objects.requireNonNull(Looper.myLooper()));
        }

        lat = getIntent().getDoubleExtra("lat", 0.0);
        lng = getIntent().getDoubleExtra("lng", 0.0);
        areaName = getIntent().getStringExtra("areaName");
        count = getIntent().getStringExtra("count");
        placeId = getIntent().getStringExtra("placeId");

        if (ConnectivityUtils.getInstance().isGPSEnabled(context)) {
            //initialize home fragment
            getBookingParkStatus(Preferences.getInstance(context).getUser().getMobileNo());
            binding.appBarMain.linearLayoutToolbarTime.setVisibility(View.GONE);
            navigationView.getMenu().getItem(0).setChecked(true);
        }

        if (!Preferences.getInstance(context).isLoggedIn()) {
            startActivityWithFinishAffinity(LoginActivity.class);
            return;
        }

        setListeners();
        hideLoading();
    }

    @Override
    protected void onStart() {
        Timber.e("onStart called");
        super.onStart();
        setNavDrawerItem(R.id.nav_home);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideLoading();
        if (LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_BN)) {
            setAppLocale(LANGUAGE_BN);
        } else {
            setAppLocale(Preferences.getInstance(context).getAppLanguage());
        }
    }

    /*
    onPostResume is called only when the activity's state is completely restored. In this we will
    set our boolean variable to true. Indicating that transaction is safe now
    */
    @Override
    public void onPostResume() {
        super.onPostResume();
        isTransactionSafe = true;

        /* Here after the activity is restored we check if there is any transaction pending from
        the last restoration */
        if (isTransactionPending) {
            fragmentChange(HomeFragment.newInstance());
        }
    }

    /*
    onPause is called just before the activity moves to background and also before onSaveInstanceState. In this
    we will mark the transaction as unsafe
    */
    @Override
    public void onPause() {
        super.onPause();
        isTransactionSafe = false;
    }

    @Override
    public void onStop() {
        Timber.e("onStop called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isGPSEnabled() && ConnectivityUtils.getInstance().checkInternet(context)) {
            navigationView.getMenu().getItem(0).setChecked(true);
            drawerLayout.closeDrawers();
            //super.onBackPressed(); delete this line
            // and start your fragment:

            Fragment fragment = getSupportFragmentManager().findFragmentByTag(context.getResources().getString(R.string.welcome_to_locc_parking));
            if (fragment != null) {
                if (fragment.isVisible()) {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                }
            }
            else {
                List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
                for (Fragment f : fragmentList) {
                    if (f instanceof HomeFragment) {
                        ((HomeFragment) f).onBackPressed();
                        boolean exit = true;
                        if (exit) {
                            Timber.e("onBackPressed exit if");
                            exitCounter--;
                            if (exitCounter == 0) {
                                Timber.e("onBackPressed exitCounter if");
                                exitCounter = 1;
                                drawerLayout.closeDrawers();
                                DialogUtils.getInstance().showExitDialog(this);
                            } else {
                                Timber.e("onBackPressed exitCounter else");
                                ToastUtils.getInstance().showToastWithDelay(context, context.getResources().getString(R.string.press_back_again_to_exit), 200);
                            }
                        } else {
                            Timber.e("onBackPressed exit else");
                            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                                Timber.e("onBackPressed exit else if");
                                if (exitToast == null || exitToast.getView() == null || exitToast.getView().getWindowToken() == null) {
                                    exitToast = Toast.makeText(context, context.getResources().getString(R.string.press_back_again_to_exit), Toast.LENGTH_LONG);
                                    exitToast.show();
                                } else {
                                    Timber.e("onBackPressed exit else else");
                                    exitToast.cancel();
                                    super.onBackPressed();
                                }
                            } else {
                                super.onBackPressed();
                            }
                        }
                    } else if (f instanceof ParkingFragment) {
                        ((ParkingFragment) f).onBackPressed();
                        toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                    } else if (f instanceof ReservationFragment) {
                        ((ReservationFragment) f).onBackPressed();
                        toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                    } else if (f instanceof LawFragment) {
                        ((LawFragment) f).onBackPressed();
                        toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                    } else if (f instanceof ProfileFragment) {
                        ((ProfileFragment) f).onBackPressed();
                        toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                    } else if (f instanceof ScheduleFragment) {
                        ((ScheduleFragment) f).onBackPressed();
                        toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                    } else if (f instanceof SettingsFragment) {
                        ((SettingsFragment) f).onBackPressed();
                        toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                    } else if (f instanceof FollowUsFragment) {
                        ((FollowUsFragment) f).onBackPressed();
                        toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                    } else if (f instanceof PrivacyPolicyFragment) {
                        ((PrivacyPolicyFragment) f).onBackPressed();
                        toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                    }
                }
                binding.appBarMain.tvTimeToolbar.setVisibility(View.GONE);
                binding.appBarMain.linearLayoutToolbarTime.setVisibility(View.GONE);
                drawerLayout.closeDrawers();
                navigationView.getMenu().getItem(0).setChecked(true);
                navigationView.getMenu().getItem(1).setChecked(false);
                navigationView.getMenu().getItem(2).setChecked(false);
                navigationView.getMenu().getItem(3).setChecked(false);
                navigationView.getMenu().getItem(4).setChecked(false);
                navigationView.getMenu().getItem(5).setChecked(false);
                navigationView.getMenu().getItem(6).setChecked(false);
                navigationView.getMenu().getItem(7).setChecked(false);
                navigationView.getMenu().getItem(8).setChecked(false);
                navigationView.getMenu().getItem(9).setChecked(false);
                navigationView.getMenu().getItem(10).setChecked(false);
            }

            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            for (Fragment f : fragmentList) {
                if (f instanceof PaymentFragment) {
                    fragmentChange(ScheduleFragment.newInstance());
                    toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                }
            }
        } else {
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_parking_app));
            DialogUtils.getInstance().alertDialog(context,
                    (Activity) context,
                    context.getResources().getString(R.string.exit_message_main),
                    context.getResources().getString(R.string.ok), context.getResources().getString(R.string.cancel),
                    new DialogUtils.DialogClickListener() {
                        @Override
                        public void onPositiveClick() {
                            Timber.e("Positive Button clicked");
                            finishAffinity();
                            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.thanks_message));
                        }

                        @Override
                        public void onNegativeClick() {
                            Timber.e("Negative Button Clicked");
                        }
                    }).show();
        }
    }

    @Override
    public void showPermissionGranted(String permissionName) {

    }

    @Override
    public void handlePermanentDeniedPermission(String permissionName) {

    }

    @Override
    public void showPermissionDenied(String permissionName) {

    }

    @Override
    public void showPermissionRational(PermissionToken token) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.e("onActivityResult HomeActivity called");
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == GPS_REQUEST_CODE) {

            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            boolean providerEnabled = false;
            if (locationManager != null) {
                providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }

            if (providerEnabled) {
                Timber.e("providerEnabled HomeActivity called");
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.enable_gps));
            }
        } else {
            //Toast.makeText(context, "GPS not enabled. Unable to show user location", Toast.LENGTH_SHORT).show();
            Timber.e("requestCode else called");
        }
    }

    @Override
    public void fragmentChange(Fragment fragment) {
        try {
            if (isTransactionSafe) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                );
                ft.replace(R.id.nav_host_fragment, fragment);
                ft.addToBackStack(null);
                ft.commit();
                isTransactionPending = false;
            } else {
                 /*
                 If any transaction is not done because the activity is in background. We set the
                 isTransactionPending variable to true so that we can pick this up when we come back to
                 foreground */
                isTransactionPending = true;
            }
        } catch (IllegalStateException e) {
            // There's no way to avoid getting this if saveInstanceState has already been called.
            Timber.e(e.getCause());
        }
    }

    private void setListeners() {
        binding.appBarMain.linearLayoutToolbarTime.setOnClickListener(v -> {
            if (isGPSEnabled()) {
                ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                        ScheduleFragment.newInstance(), R.id.nav_host_fragment);
            } else {
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.please_enable_gps));
            }
        });
    }

    private void handleLocationPermissionCheck(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            finish();
            Intent intent = new Intent(context, PermissionActivity.class);
            //Intent intent = new Intent(context, LocationPermissionActivity.class);
            startActivity(intent);
            Preferences.getInstance(context).setIsLocationPermissionGiven(false);
            //return;
        } else {
            // Write you code here if permission already given.
            Preferences.getInstance(context).setIsLocationPermissionGiven(true);

        }
    }

    private void buildLocationCallBack() {
        Timber.e("buildLocationCallBack HomeActivity call hoiche");
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull final LocationResult locationResult) {
                //if (mMap != null) {
                lastLocation = locationResult.getLastLocation();
                SharedData.getInstance().setLastLocation(lastLocation);
                //addUserMarker();
                //}
            }
        };
    }

    private void getBookingParkStatus(String mobileNo) {
        showLoading(context);
        reservationViewModel.initBookingParkStatus(mobileNo);
        reservationViewModel.getBookingParkStatus().observe(this, bookingParkStatusResponse -> {
            hideLoading();
            if (bookingParkStatusResponse.getSensors() != null) {
                BookingParkStatusResponse.Sensors sensors = bookingParkStatusResponse.getSensors();
                ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                        ReservationParkFragment.newInstance(sensors), R.id.nav_host_fragment);
            } else {
                ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                        HomeFragment.newInstance(lat, lng, areaName, count, placeId), R.id.nav_host_fragment);
            }
        });
    }
}
