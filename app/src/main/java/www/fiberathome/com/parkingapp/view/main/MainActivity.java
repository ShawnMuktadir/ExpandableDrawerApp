package www.fiberathome.com.parkingapp.view.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.karumi.dexter.PermissionToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.module.eventBus.GetDirectionEvent;
import www.fiberathome.com.parkingapp.module.eventBus.SetMarkerEvent;
import www.fiberathome.com.parkingapp.view.permission.listener.DexterPermissionListener;
import www.fiberathome.com.parkingapp.view.permission.listener.PermissionInterface;
import www.fiberathome.com.parkingapp.model.loginUser.User;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.LocationHelper;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.view.location.LocationActivity;
import www.fiberathome.com.parkingapp.view.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.view.permission.PermissionActivity;
import www.fiberathome.com.parkingapp.view.booking.ScheduleFragment;
import www.fiberathome.com.parkingapp.view.booking.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.view.booking.newBooking.BookingFragment;
import www.fiberathome.com.parkingapp.view.booking.oldBooking.OldBookingDetailsFragment;
import www.fiberathome.com.parkingapp.view.dialog.DialogForm;
import www.fiberathome.com.parkingapp.view.followUs.FollowUsFragment;
import www.fiberathome.com.parkingapp.view.changePassword.ChangePasswordFragment;
import www.fiberathome.com.parkingapp.view.main.home.HomeFragment;
import www.fiberathome.com.parkingapp.view.law.LawFragment;
import www.fiberathome.com.parkingapp.view.notification.NotificationFragment;
import www.fiberathome.com.parkingapp.view.profile.ProfileFragment;
import www.fiberathome.com.parkingapp.view.getDiscount.GetDiscountFragment;
import www.fiberathome.com.parkingapp.view.parking.ParkingFragment;
import www.fiberathome.com.parkingapp.view.privacyPolicy.PrivacyPolicyFragment;
import www.fiberathome.com.parkingapp.view.ratingReview.RatingReviewFragment;
import www.fiberathome.com.parkingapp.view.settings.SettingsFragment;
import www.fiberathome.com.parkingapp.view.share.ShareFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        DialogForm.DialogFormListener, FragmentChangeListener, PermissionInterface {

    @BindView(R.id.tvTimeToolbar)
    public TextView tvTimeToolbar;
    @BindView(R.id.linearLayoutToolbarTime)
    public LinearLayout linearLayoutToolbarTime;
    @BindView(R.id.navigationView)
    public NavigationView navigationView;
    @BindView(R.id.drawerlayoutMain)
    DrawerLayout drawerLayoutMain;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    private Context context;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private boolean exit = false;

    private DexterPermissionListener permissionListener;

    public static final int GPS_REQUEST_CODE = 9003;

    private static final int PLAY_SERVICES_ERROR_CODE = 9002;

    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.e("MainActivity onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;

        setSupportActionBar(toolbar);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        setupNavigationDrawer();
        setupNavigationDrawerHeader();
        //hide menu
        hideMenuItem();

        changeDefaultActionBarDrawerToogleIcon();
        colorizeToolbarOverflowButton(toolbar, context.getResources().getColor(R.color.black));

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

        if (savedInstanceState == null && SharedPreManager.getInstance(context).isWaitingForLocationPermission() && new LocationHelper(this).isLocationEnabled()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            linearLayoutToolbarTime.setVisibility(View.VISIBLE);
            navigationView.getMenu().getItem(0).setChecked(true);
//            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else {
            //  startActivity(new Intent(MainActivity.this, LocationActivity.class));
            Intent intent = new Intent(MainActivity.this, LocationActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);

            return;
            //  TastyToastUtils.showTastyInfoToast(context,"Sorry! You can't use Parking App. For use, please enable your GPS!");
        }

        if (!SharedPreManager.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setListeners();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.menu_change_password) {
            // do your code
            toolbar.setTitle("Change Password");
            navigationView.getMenu().getItem(0).setChecked(true);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ChangePasswordFragment.newInstance()).commit();
            return true;
        } else if (item.getItemId() == R.id.menu_logout) {
            // do your code
            SharedPreManager.getInstance(this).logout();
            Intent intentLogout = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentLogout);
            finishAffinity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayoutMain.closeDrawers();
        navigationView.setCheckedItem(item.getItemId());
        switch (item.getItemId()) {
            case R.id.nav_home:
                toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                tvTimeToolbar.setVisibility(View.VISIBLE);
                linearLayoutToolbarTime.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance()).commit();
                SharedData.getInstance().setOnConnectedLocation(null);
                break;

            case R.id.nav_parking:
                if (ApplicationUtils.checkInternet(context)) {
                    toolbar.setTitle(context.getResources().getString(R.string.parking));
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ParkingFragment.newInstance()).commit();
                    tvTimeToolbar.setVisibility(View.GONE);
                    linearLayoutToolbarTime.setVisibility(View.GONE);
                    // Remove any previous data from SharedData's sensor Data Parking Information
                    SharedData.getInstance().setSensorArea(null);
                } else {
                    TastyToastUtils.showTastyWarningToast(context, "Please connect to internet");
                }
                break;

            case R.id.nav_booking:
                if (ApplicationUtils.checkInternet(context)) {
                    toolbar.setTitle(context.getResources().getString(R.string.bookings));
                    tvTimeToolbar.setVisibility(View.GONE);
                    linearLayoutToolbarTime.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, BookingFragment.newInstance()).commit();
                } else {
                    TastyToastUtils.showTastyWarningToast(context, "Please connect to internet");
                }
                break;

            case R.id.nav_law:
                toolbar.setTitle(context.getResources().getString(R.string.law));
                tvTimeToolbar.setVisibility(View.GONE);
                linearLayoutToolbarTime.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, LawFragment.newInstance()).commit();
                break;

            case R.id.nav_notification:
                toolbar.setTitle(context.getResources().getString(R.string.notification));
                tvTimeToolbar.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, NotificationFragment.newInstance()).commit();
                break;

            case R.id.nav_profile:
                if (ApplicationUtils.checkInternet(context)) {
                    toolbar.setTitle(context.getResources().getString(R.string.profile));
                    tvTimeToolbar.setVisibility(View.GONE);
                    linearLayoutToolbarTime.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ProfileFragment.newInstance()).commit();
                } else {
                    TastyToastUtils.showTastyWarningToast(context, "Please connect to internet");
                }
                break;

            case R.id.nav_settings:
                toolbar.setTitle(context.getResources().getString(R.string.action_settings));
                tvTimeToolbar.setVisibility(View.GONE);
                linearLayoutToolbarTime.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, SettingsFragment.newInstance()).commit();
                break;

            case R.id.nav_get_discount:
                toolbar.setTitle(context.getResources().getString(R.string.get_discount));
                tvTimeToolbar.setVisibility(View.GONE);
                linearLayoutToolbarTime.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, GetDiscountFragment.newInstance()).commit();
                break;

            case R.id.nav_rating_review:
                toolbar.setTitle(context.getResources().getString(R.string.give_review_rating));
                tvTimeToolbar.setVisibility(View.GONE);
                linearLayoutToolbarTime.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, RatingReviewFragment.newInstance()).commit();
                break;

            case R.id.nav_follow_us:
                toolbar.setTitle(context.getResources().getString(R.string.follow_us));
                tvTimeToolbar.setVisibility(View.GONE);
                linearLayoutToolbarTime.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, FollowUsFragment.newInstance()).commit();
                break;

            case R.id.nav_privacy_policy:
                if (ApplicationUtils.checkInternet(context)) {
                    toolbar.setTitle(context.getResources().getString(R.string.privacy_policy));
                    tvTimeToolbar.setVisibility(View.GONE);
                    linearLayoutToolbarTime.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, PrivacyPolicyFragment.newInstance()).commit();
                } else {
                    TastyToastUtils.showTastyWarningToast(context, "Please connect to internet");
                }
                break;

            case R.id.nav_share:
                toolbar.setTitle(context.getResources().getString(R.string.share));
                tvTimeToolbar.setVisibility(View.GONE);
                linearLayoutToolbarTime.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ShareFragment.newInstance()).commit();
                break;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onStart() {
        Timber.e("MainActivity onStart called");
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        Timber.e("MainActivity onStop called");
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        Timber.e("MainActivity onResume called");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.e("MainActivity onDestroy called");
    }

    @Override
    public void  onBackPressed() {
        if (isGPSEnabled() && ApplicationUtils.checkInternet(context)) {

            if (exit) {
                super.onBackPressed();
                return;
            }
            try {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentByTag(context.getResources().getString(R.string.welcome_to_locc_parking));
                if (fragment != null) {
                    if (fragment.isVisible()) {
                        if (drawerLayoutMain.isDrawerOpen(GravityCompat.START)) {
                            drawerLayoutMain.closeDrawer(GravityCompat.START);
                        } else {
                            this.exit = true;
                            ApplicationUtils.showExitDialog(this);
//                    Toast.makeText(this, "Press Back again to Exit", Toast.LENGTH_SHORT).show();
                        }
                        exit = false;
                    } else {
                        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
                        for (Fragment f : fragmentList) {
                            if (f instanceof ScheduleFragment) {
                                ((ScheduleFragment) f).onBackPressed();
                                toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                            } else if (f instanceof HomeFragment) {
//                                this.exit = true;
                                ApplicationUtils.showExitDialog(this);
                            } else {
                                this.exit = true;
                                fragment = HomeFragment.newInstance();
                                getFragmentManager().popBackStack();
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, context.getResources().getString(R.string.welcome_to_locc_parking)).commit();
                                toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                                tvTimeToolbar.setVisibility(View.VISIBLE);
                                linearLayoutToolbarTime.setVisibility(View.VISIBLE);
                                drawerLayoutMain.closeDrawers();
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
//                                ApplicationUtils.showExitDialog(this);
                            }
                        }
                    }
                } else {
                    fragment = HomeFragment.newInstance();
                    getFragmentManager().popBackStack();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, context.getResources().getString(R.string.welcome_to_locc_parking)).commit();
                    drawerLayoutMain.closeDrawers();
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
                    toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                    tvTimeToolbar.setVisibility(View.VISIBLE);
                    linearLayoutToolbarTime.setVisibility(View.VISIBLE);
                    if (SharedData.getInstance().getOnConnectedLocation() != null) {
                        HomeFragment.newInstance().animateCamera(SharedData.getInstance().getOnConnectedLocation());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 2000);
        } else {
            TastyToastUtils.showTastyWarningToast(context, "Please enable GPS or turn on Internet to use Parking App");
            ApplicationUtils.showAlertDialog(context.getString(R.string.exit_message_main), context, context.getString(R.string.ok), context.getString(R.string.cancel), (dialog, which) -> {
                Timber.e("Positive Button clicked");
                dialog.dismiss();
//                if (context != null) {
                finish();
                TastyToastUtils.showTastySuccessToast(context, "Thanks for being with us");
//                }
//                if (isGPSEnabled() && ApplicationUtils.checkInternet(context)){
//                    ApplicationUtils.checkInternet(context)
//                } else {
//                    TastyToastUtils.showTastyWarningToast(context, "Please connect to internet");
//                }
            }, (dialog, which) -> {
                Timber.e("Negative Button Clicked");
                dialog.dismiss();
            });
        }
    }

    @Override
    public void applyTexts(String username, String password, String mobile) {

        Timber.e(username);
        Timber.e(password);
        Timber.e(mobile);
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
//                Toast.makeText(context, "GPS not enabled. Unable to show user location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void fragmentChange(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
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

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

//        if (menu instanceof MenuBuilder) {
//            ((MenuBuilder) menu).setOptionalIconsVisible(true);
//        }
//
//        //change menu icon color programmatically & changing a particular icon of one of menus, use break in the for loop
//        for (int i = 0; i < menu.size(); i++) {
//            Drawable drawable = menu.getItem(i).getIcon();
//            if (drawable != null) {
//                drawable.mutate();
//                drawable.setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
//                break;
//            }
//        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(GetDirectionEvent event) {
        Timber.e("parkingFragment GetDirectionEvent onMessageEvent called");
        navigationView.getMenu().getItem(0).setChecked(true);
        toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
        tvTimeToolbar.setVisibility(View.VISIBLE);
        linearLayoutToolbarTime.setVisibility(View.VISIBLE);
        ProgressDialog progressDialog = ApplicationUtils.progressDialog(context, "Please wait...");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance()).commit();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Timber.e("GetDirectionEvent MainActivity called");
                // Do something after 2s = 2000ms
                try {
                    EventBus.getDefault().post(new SetMarkerEvent(event.location));
                    progressDialog.dismiss();
                } catch (EventBusException e) {
                    e.getCause();
                }
            }
        }, 4000);
    }

    public void onParkingAdapterItemClickBottomSheetChanged(LatLng latLng) {
        Timber.e("onParkingAdapterItemClickBottomSheetChanged MainActivity called");
        navigationView.getMenu().getItem(0).setChecked(true);
        toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
        tvTimeToolbar.setVisibility(View.VISIBLE);
        linearLayoutToolbarTime.setVisibility(View.VISIBLE);
        ProgressDialog progressDialog = ApplicationUtils.progressDialog(context, "Please wait...");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Timber.e("onParkingAdapterItemClickBottomSheetChanged MainActivity called");
                // Do something after 2s = 2000ms
                try {
                    EventBus.getDefault().post(new SetMarkerEvent(latLng));
                    progressDialog.dismiss();
                } catch (EventBusException e) {
                    e.getCause();
                }
            }
        }, 2000);
    }

    private void setupNavigationDrawer() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayoutMain, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayoutMain.addDrawerListener(actionBarDrawerToggle);
        drawerLayoutMain.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //Called when a drawer's position changes.

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //Called when a drawer has settled in a completely open state.
                //The drawer is interactive at this point.
                // If you have 2 drawers (left and right) you can distinguish
                // them by using id of the drawerView. int id = drawerView.getId();
                // id will be your layout's id: for example R.id.left_drawer
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // Called when a drawer has settled in a completely closed state.
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Called when the drawer motion state changes. The new state will be one of STATE_IDLE, STATE_DRAGGING or STATE_SETTLING.
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(drawerLayoutMain.getWindowToken(), 0);
            }
        });
        actionBarDrawerToggle.syncState();

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        //drawer menu text color while pressed
        setNavMenuItemThemeColors(context.getResources().getColor(R.color.gray));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        setDrawerState(true);
    }

    private void setupNavigationDrawerHeader() {
        User user = SharedPreManager.getInstance(this).getUser();
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserFullName = headerView.findViewById(R.id.header_fullname);
        TextView tvUserVehicleNo = headerView.findViewById(R.id.header_vehicle_no);
        ImageView ivUserProfile = headerView.findViewById(R.id.header_profile_pic);

        tvUserFullName.setText(ApplicationUtils.capitalize(user.getFullName()));

        StringBuilder stringBuilder = new StringBuilder();
        if (user.getVehicleNo() != null) {
            stringBuilder.append("Vehicle No: ").append(user.getVehicleNo());
        }
        tvUserVehicleNo.setText(stringBuilder.toString());
//        tvUserVehicleNo.setText("Vehicle No: " + user.getVehicleNo());

        RequestOptions requestOptions = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.blank_profile)
                .error(R.drawable.blank_profile);
        String url = AppConfig.IMAGES_URL + user.getProfilePic() + ".jpg";
        Timber.e("user profile photo url -> %s", url);
        Glide.with(this).load(url).apply(requestOptions).override(200, 200).into(ivUserProfile);

        String text = user.getMobileNo() + " - ";
        text = text + user.getVehicleNo();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
//            QRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void setNavMenuItemThemeColors(int color) {
//        Setting default colors for menu item Text and Icon
        int navDefaultTextColor = Color.parseColor("#000000");

        //Defining ColorStateList for menu item Text
        ColorStateList navMenuTextList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_pressed}
                },
                new int[]{
                        color,
                        navDefaultTextColor,
                        navDefaultTextColor,
                        navDefaultTextColor,
                        navDefaultTextColor
                }
        );

        navigationView.setItemTextColor(navMenuTextList);
    }

    public void setDrawerState(boolean isEnabled) {
        if (isEnabled) {
            drawerLayoutMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
            actionBarDrawerToggle.syncState();
        } else {
            drawerLayoutMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(null);
        }
    }

    private void hideMenuItem() {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_home).setVisible(false);
        nav_Menu.findItem(R.id.nav_notification).setVisible(false);
    }

    private void changeDefaultActionBarDrawerToogleIcon() {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(context.getResources().getColor(R.color.black));
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayoutMain.isDrawerVisible(GravityCompat.START)) {
                    drawerLayoutMain.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayoutMain.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    //toolbar menu overflow icon change method
    public static boolean colorizeToolbarOverflowButton(@NonNull Toolbar toolbar, @ColorInt int color) {
        final Drawable overflowIcon = toolbar.getOverflowIcon();
        if (overflowIcon == null) return false;
        toolbar.setOverflowIcon(getTintedDrawable(toolbar.getContext(), overflowIcon, color));
        return true;
    }

    public static Drawable getTintedDrawable(@NonNull Context context, @NonNull Drawable inputDrawable, @ColorInt int color) {
        Drawable wrapDrawable = DrawableCompat.wrap(inputDrawable);
        DrawableCompat.setTint(wrapDrawable, color);
        DrawableCompat.setTintMode(wrapDrawable, PorterDuff.Mode.SRC_IN);
        return wrapDrawable;
    }

    private void setListeners() {
//        btnTimeToolbar.setOnClickListener(v-> {
//            toolbar.setTitle(context.getResources().getString(R.string.schedule_parking));
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScheduleFragment()  ).commit();
//        })
        linearLayoutToolbarTime.setOnClickListener(v -> {
            if (isGPSEnabled()) {
                toolbar.setTitle(context.getResources().getString(R.string.schedule_parking));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ScheduleFragment()).commit();
            } else {
                TastyToastUtils.showTastyWarningToast(context, "Please enable GPS!");
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void replaceFragmentWithBundle(String s) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction t1 = fragmentManager.beginTransaction();
        OldBookingDetailsFragment oldBookingDetailsFragment = new OldBookingDetailsFragment();

        Bundle b2 = new Bundle();
        b2.putString("s", s);

        oldBookingDetailsFragment.setArguments(b2);
//        t1.replace(R.id.frame1, bookingDetailsFragment);
//        t1.commit();

        // Move the MainActivity with Map Fragement
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, oldBookingDetailsFragment).commit();
    }

    public void replaceFragment() {
        toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance()).commit();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void handleLocationPermissionCheck(Context context) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            finish();
            Intent intent = new Intent(MainActivity.this, PermissionActivity.class);
            startActivity(intent);
            SharedPreManager.getInstance(context).setIsLocationPermissionGiven(false);
//            return;
        } else {
            // Write you code here if permission already given.
            SharedPreManager.getInstance(context).setIsLocationPermissionGiven(true);

        }
    }

    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {

//            AlertDialog alertDialog = new AlertDialog.Builder(context)
//                    .setTitle("GPS Permissions")
//                    .setMessage("GPS is required for this app to work. Please enable GPS.")
//                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
//                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivityForResult(intent, GPS_REQUEST_CODE);
//                    }))
//                    .setCancelable(false)
//                    .show();

        }

        return false;
    }

    private boolean isServicesOk() {

        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();

        int result = googleApi.isGooglePlayServicesAvailable(context);

        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApi.isUserResolvableError(result)) {
            Dialog dialog = googleApi.getErrorDialog(this, result, PLAY_SERVICES_ERROR_CODE, task ->
                    Toast.makeText(context, "Dialog is cancelled by User", Toast.LENGTH_SHORT).show());
            dialog.show();
        } else {
            Toast.makeText(context, "Play services are required by this application", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private LocationCallback locationCallback;
    private Location lastLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private void buildLocationCallBack() {
        Timber.e("buildLocationCallBack MainActivity call hoiche");
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {
//                if (mMap != null) {
                lastLocation = locationResult.getLastLocation();
                SharedData.getInstance().setLastLocation(lastLocation);
//                    addUserMarker();
//                }
            }
        };
    }
}