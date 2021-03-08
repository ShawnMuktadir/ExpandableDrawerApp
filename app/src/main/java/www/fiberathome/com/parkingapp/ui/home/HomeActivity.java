package www.fiberathome.com.parkingapp.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.PermissionToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.ui.NavigationActivity;
import www.fiberathome.com.parkingapp.ui.booking.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.ui.booking.newBooking.BookingFragment;
import www.fiberathome.com.parkingapp.ui.followUs.FollowUsFragment;
import www.fiberathome.com.parkingapp.ui.getDiscount.GetDiscountFragment;
import www.fiberathome.com.parkingapp.ui.law.LawFragment;
import www.fiberathome.com.parkingapp.ui.parking.ParkingFragment;
import www.fiberathome.com.parkingapp.ui.permission.PermissionActivity;
import www.fiberathome.com.parkingapp.ui.permission.listener.PermissionInterface;
import www.fiberathome.com.parkingapp.ui.privacyPolicy.PrivacyPolicyFragment;
import www.fiberathome.com.parkingapp.ui.profile.ProfileFragment;
import www.fiberathome.com.parkingapp.ui.ratingReview.RatingReviewFragment;
import www.fiberathome.com.parkingapp.ui.schedule.ScheduleFragment;
import www.fiberathome.com.parkingapp.ui.settings.SettingsFragment;
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

@SuppressLint("NonConstantResourceId")
public class HomeActivity extends NavigationActivity implements FragmentChangeListener, PermissionInterface {

    @BindView(R.id.tvTimeToolbar)
    public TextView tvTimeToolbar;

    @BindView(R.id.linearLayoutToolbarTime)
    public LinearLayout linearLayoutToolbarTime;

    private Unbinder unbinder;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location lastLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private boolean exit = true;
    private Toast exitToast;
    private int exitCounter = 1;

    public static final int GPS_REQUEST_CODE = 9003;

    private Context context;
    private PendingIntent pendingIntent;
    private GeofencingRequest geofencingRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        unbinder = ButterKnife.bind(this);

        setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));

        //location permission check
        handleLocationPermissionCheck(context);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        buildLocationCallBack();
        locationRequest = new LocationRequest();
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
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }

        double lat = getIntent().getDoubleExtra("lat", 0.0);
        //Timber.e("lat parking activity -> %s",lat);
        double lng = getIntent().getDoubleExtra("lng", 0.0);
        //Timber.e("lng parking activity -> %s",lng);
        String areaName = getIntent().getStringExtra("areaName");
        String count = getIntent().getStringExtra("count");

        //initialize home fragment
        ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                HomeFragment.newInstance(lat, lng, areaName, count), R.id.nav_host_fragment);
        linearLayoutToolbarTime.setVisibility(View.VISIBLE);
        navigationView.getMenu().getItem(0).setChecked(true);

        if (!Preferences.getInstance(context).isLoggedIn()) {
            startActivityWithFinishAffinity(LoginActivity.class);
            return;
        }

        setListeners();
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
    }

    @Override
    public void onStop() {
        Timber.e("onStop called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {
            navigationView.getMenu().getItem(0).setChecked(true);
            drawerLayout.closeDrawers();
            //toolbar.setSubtitle("");
            //super.onBackPressed(); delete this line
            // and start your fragment:

            Fragment fragment = getSupportFragmentManager().findFragmentByTag(context.getResources().getString(R.string.welcome_to_locc_parking));
            if (fragment != null) {
                if (fragment.isVisible()) {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                }
            } else {
                List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
                for (Fragment f : fragmentList) {
                    if (f instanceof HomeFragment) {
                        ((HomeFragment) f).onBackPressed();
                        if (exit) {
                            Timber.e("onBackPressed exit if");
                            exitCounter--;
                            if (exitCounter == 0) {
                                Timber.e("onBackPressed exitCounter if");
                                exitCounter = 1;
                                drawerLayout.closeDrawers();
                                ApplicationUtils.showExitDialog(this);
                            } else {
                                Timber.e("onBackPressed exitCounter else");
                                ApplicationUtils.showToast(context, "Press Back again to exit", 200);
                            }
                        } else {
                            Timber.e("onBackPressed exit else");
                            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                                Timber.e("onBackPressed exit else if");
                                if (exitToast == null || exitToast.getView() == null || exitToast.getView().getWindowToken() == null) {
                                    exitToast = Toast.makeText(this, "Press Back Button again to exit", Toast.LENGTH_LONG);
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
                    } else if (f instanceof BookingFragment) {
                        ((BookingFragment) f).onBackPressed();
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
                    } else if (f instanceof GetDiscountFragment) {
                        ((GetDiscountFragment) f).onBackPressed();
                        toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                    } else if (f instanceof RatingReviewFragment) {
                        ((RatingReviewFragment) f).onBackPressed();
                        toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                    } else if (f instanceof FollowUsFragment) {
                        ((FollowUsFragment) f).onBackPressed();
                        toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                    } else if (f instanceof PrivacyPolicyFragment) {
                        ((PrivacyPolicyFragment) f).onBackPressed();
                        toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                    }
                }
                tvTimeToolbar.setVisibility(View.VISIBLE);
                linearLayoutToolbarTime.setVisibility(View.VISIBLE);
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
                /*if (SharedData.getInstance().getOnConnectedLocation() != null) {
                    HomeFragment.newInstance(lat, lng).animateCamera(SharedData.getInstance().getOnConnectedLocation());
                }*/
            }
        } else {
            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_parking_app));
            ApplicationUtils.showAlertDialog(context.getString(R.string.exit_message_main), context, context.getString(R.string.ok), context.getString(R.string.cancel), (dialog, which) -> {
                Timber.e("Positive Button clicked");
                dialog.dismiss();
                //if (context != null) {
                finish();
                TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                /*}
                if (isGPSEnabled() && ApplicationUtils.checkInternet(context)){
                    ApplicationUtils.checkInternet(context)
                } else {
                    TastyToastUtils.showTastyWarningToast(context, "Please connect to internet");
                }*/
            }, (dialog, which) -> {
                Timber.e("Negative Button Clicked");
                dialog.dismiss();
            });
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
        Timber.e("onActivityResult MainActivity called");
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
                Timber.e("providerEnabled MainActivity called");
                Toast.makeText(context, "GPS is enabled", Toast.LENGTH_SHORT).show();
            }
        } else {
            //Toast.makeText(context, "GPS not enabled. Unable to show user location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void fragmentChange(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.nav_host_fragment, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void setListeners() {
        linearLayoutToolbarTime.setOnClickListener(v -> {
            if (isGPSEnabled()) {
                ApplicationUtils.addFragmentToActivity(getSupportFragmentManager(),
                        ScheduleFragment.newInstance(), R.id.nav_host_fragment);
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_gps));
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

    public boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {

            /*AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("GPS Permissions")
                    .setMessage("GPS is required for this app to work. Please enable GPS.")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();*/

        }

        return false;
    }

    private void buildLocationCallBack() {
        Timber.e("buildLocationCallBack MainActivity call hoiche");
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                //if (mMap != null) {
                lastLocation = locationResult.getLastLocation();
                SharedData.getInstance().setLastLocation(lastLocation);
                //addUserMarker();
                //}
            }
        };
    }
}
