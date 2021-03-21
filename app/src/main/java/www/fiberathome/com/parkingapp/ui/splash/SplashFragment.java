package www.fiberathome.com.parkingapp.ui.splash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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
import www.fiberathome.com.parkingapp.ui.location.LocationActivity;
import www.fiberathome.com.parkingapp.ui.permission.PermissionActivity;
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.ForceUpdateChecker;
import www.fiberathome.com.parkingapp.utils.LocationHelper;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

import static android.content.Context.LOCATION_SERVICE;
import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;

@SuppressLint("NonConstantResourceId")
public class SplashFragment extends BaseFragment implements ForceUpdateChecker.OnUpdateNeededListener {

    @BindView(R.id.splash_iv_logo)
    ImageView imageViewSplashLogo;

    private Unbinder unbinder;

    private SplashActivity context;

    private LocationManager mLocationManager;

    private static final int UPDATE_CODE = 1000;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ForceUpdateChecker.with(context).onUpdateNeeded(SplashFragment.this).check();
            mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
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
            mLocationManager.
                    requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);

            if (new LocationHelper(context).isLocationEnabled() && mLocationManager != null) {
                showLoading(context, context.getResources().getString(R.string.initialize_location));

                new Handler().postDelayed(() -> {
                    hideLoading();
                    context.startActivityWithFinish(HomeActivity.class);
                }, 4000);
            }
        } catch (Exception e) {
            e.getCause();
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
            if (ApplicationUtils.checkInternet(context)) {
                if (ApplicationUtils.isGPSEnabled(context)){
                    context.startActivity(intent);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.finish();
                } else {
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
                                    /*context.finishAffinity();
                                    TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));*/
                                }
                            }).show();
                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.please_enable_gps));
                }
            } else {
                ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                    Timber.e("Positive Button clicked");
                    if (ApplicationUtils.checkInternet(context)) {
                        checkUserLogin();
                    } else {
                        TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet_splash));
                        new Handler().postDelayed(() -> {
                            dialog.dismiss();
                            context.finish();
                        }, 700);
                    }
                }, (dialog, which) -> {
                    Timber.e("Negative Button Clicked");
                    dialog.dismiss();
                    context.finish();
                });
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
                showLoading(context);
                new Handler().postDelayed(() -> {
                    hideLoading();
                    //Toast.makeText(context, "GPS is enabled", Toast.LENGTH_SHORT).show();
                    context.startActivityWithFinish(HomeActivity.class);
                }, 6000);

            } else {
                ApplicationUtils.showToastMessage(context, context.getResources().getString(R.string.gps_network_not_enabled));
            }
        } else {
            ApplicationUtils.showToastMessage(context, context.getResources().getString(R.string.gps_not_enabled_unable_to_show_user_location));
        }
    }

    @Override
    public void onUpdateNeeded(final String updateUrl) {
        DialogUtils.getInstance().alertDialog(context,
                context,
                context.getResources().getString(R.string.new_version_available), context.getResources().getString(R.string.please_update_the_app),
                context.getResources().getString(R.string.update), context.getResources().getString(R.string.no_thanks),
                new DialogUtils.DialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                        redirectStore(updateUrl);
                    }

                    @Override
                    public void onNegativeClick() {
                        context.finishAffinity();
                        TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                    }
                }).show();
    }

    @Override
    public void noUpdateNeeded() {
        checkUserLogin();
    }

    private void redirectStore(String updateUrl) {
        try {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.getCause();
            TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.places_try_again));
        }
    }
}