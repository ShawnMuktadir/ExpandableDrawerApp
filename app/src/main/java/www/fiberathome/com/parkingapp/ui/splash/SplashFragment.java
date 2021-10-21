package www.fiberathome.com.parkingapp.ui.splash;

import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.permission.PermissionActivity;
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.LocationHelper;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class SplashFragment extends BaseFragment implements LocationListener {

    private static final String TAG = "SplashFragment";

    @BindView(R.id.splash_iv_logo)
    ImageView imageViewSplashLogo;

    private Unbinder unbinder;

    private SplashActivity context;

    private LocationManager mLocationManager;

    private boolean isLocationEnabled = false;

    public SplashFragment() {
        // Required empty public constructor
    }

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);

        context = (SplashActivity) getActivity();

        checkUserLogin();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            //mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

            if (isLocationEnabled) {
                if (new LocationHelper(context).isLocationEnabled() && mLocationManager != null) {
                    showLoading(context, context.getResources().getString(R.string.please_wait));

                    new Handler().postDelayed(() -> {
                        hideLoading();
                        context.startActivityWithFinish(HomeActivity.class);
                    }, 4000);
                } else {
                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.rules_for_using_app_through_gps));
                }
            }
        } catch (NullPointerException e) {
            e.getCause();
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
        }
    }

    @Override
    public void onDestroyView() {
        mLocationManager.removeUpdates(context);
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    private void openActivity(Intent intent) {
        new Handler().postDelayed(() -> {
            if (ConnectivityUtils.getInstance().checkInternet(context)) {
                if (ConnectivityUtils.getInstance().isGPSEnabled(context)) {
                    context.startActivity(intent);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.finish();
                } else {
                    isLocationEnabled = false;
                    DialogUtils.getInstance().alertDialog(context,
                            requireActivity(),
                            context.getResources().getString(R.string.enable_gps), context.getResources().getString(R.string.locc_smart_parking_app_needs_permission_to_access_device_location_to_provide_required_services_please_allow_the_permission),
                            context.getResources().getString(R.string.allow), "",
                            new DialogUtils.DialogClickListener() {
                                @Override
                                public void onPositiveClick() {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent, GPS_REQUEST_CODE);
                                }

                                @Override
                                public void onNegativeClick() {
                                    Timber.e("Negative Button Clicked");
                                }
                            }).show();
                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.please_enable_gps));
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
                                    checkUserLogin();
                                } else {
                                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_splash));
                                    new Handler().postDelayed(() -> context.finish(), 700);
                                }
                            }

                            @Override
                            public void onNegativeClick() {
                                Timber.e("Negative Button Clicked");
                                context.finish();
                            }
                        }).show();
            }
        }, 1000);
    }

    private void checkUserLogin() {
        // Check user is logged in
        if (Preferences.getInstance(context).isLoggedIn() && Preferences.getInstance(context) != null && Preferences.getInstance(context).isWaitingForLocationPermission()) {
            Timber.e("activity start if -> %s", Preferences.getInstance(context).isWaitingForLocationPermission());
            openActivity(new Intent(context, HomeActivity.class));
        } else if (Preferences.getInstance(context).isLoggedIn() && !Preferences.getInstance(context).isWaitingForLocationPermission()) {
            Timber.e("activity start else if -> %s", Preferences.getInstance(context).isWaitingForLocationPermission());
            openActivity(new Intent(context, PermissionActivity.class));
        } else {
            Timber.e("activity start else -> %s", Preferences.getInstance(context).isWaitingForLocationPermission());
            openActivity(new Intent(context, LoginActivity.class));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.e("onActivityResult SplashFragment called");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_REQUEST_CODE) {

            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            assert locationManager != null;
            boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (providerEnabled) {
                isLocationEnabled = true;
                showLoading(context);
                new Handler().postDelayed(() -> {
                    hideLoading();
                    //Toast.makeText(context, "GPS is enabled", Toast.LENGTH_SHORT).show();
                    context.startActivityWithFinish(HomeActivity.class);
                }, 6000);

            } else {
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.gps_network_not_enabled));
            }
        } else {
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.gps_not_enabled_unable_to_show_user_location));
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Timber.e("location lat -> %s", String.valueOf(location.getLatitude()));
        //Timber.e("location lng -> %s", String.valueOf(location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Timber.e("status -> %s", "Provider " + provider + " has now status: " + status);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Timber.e("provider enable -> %s", "Provider " + provider + " is enabled");
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Timber.e("provider disable -> %s", "Provider " + provider + " is disabled");
    }
}