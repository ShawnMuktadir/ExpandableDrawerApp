package www.fiberathome.com.parkingapp.ui.permission;

import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;

import android.Manifest;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.permission.listener.DexterPermissionListener;
import www.fiberathome.com.parkingapp.ui.permission.listener.PermissionInterface;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

public class PermissionActivity extends BaseActivity implements PermissionInterface {

    private DexterPermissionListener permissionListener;
    private TextView permissionTV;
    private BaseActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        context = this;

        permissionTV = findViewById(R.id.permissionTV);
        permissionListener = new DexterPermissionListener(this);
    }

    public void takeLocationPermission(View view) {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(permissionListener).check();
    }

    @Override
    public void showPermissionGranted(String permissionName) {
        if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissionName)) {
            if (ConnectivityUtils.getInstance().isGPSEnabled(context)) {
                Intent intent = new Intent(PermissionActivity.this, HomeActivity.class);
                Preferences.getInstance(context).setIsLocationPermissionGiven(true);
                startActivity(intent);
                new Handler().postDelayed(this::finishAffinity, 1000);
            } else {
                DialogUtils.getInstance().alertDialog(context,
                        context,
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

                            }
                        }).show();
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.please_enable_gps));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void handlePermanentDeniedPermission(String permissionName) {

        if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissionName)) {
            permissionTV.setText(context.getResources().getString(R.string.permission_denied_permanently));
            Preferences.getInstance(context).setIsLocationPermissionGiven(false);
            permissionTV.setTextColor(ContextCompat.getColor(this, R.color.LogoRed));
        }

        DialogUtils.getInstance().alertDialog(context,
                context,
                context.getResources().getString(R.string.u_cant_use_this_app_anymore),
                context.getResources().getString(R.string.allow_this_permission_from_settings),
                context.getResources().getString(R.string.allow), context.getResources().getString(R.string.cancel),
                new DialogUtils.DialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                        Timber.e("Positive Button clicked");
                        openSettings();
                    }

                    @Override
                    public void onNegativeClick() {
                        Timber.e("Negative Button Clicked");
                    }
                }).show();
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
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.gps_network_not_enabled));
            }
        } else {
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.gps_not_enabled_unable_to_show_user_location));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getOpPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void showPermissionDenied(String permissionName) {
        if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissionName)) {
            permissionTV.setText(context.getResources().getString(R.string.permission_denied_u_cant_search_nearest_parking_location_from_you));
            Preferences.getInstance(context).setIsLocationPermissionGiven(false);
            permissionTV.setTextColor(ContextCompat.getColor(this, R.color.LogoRed));
        }
    }

    @Override
    public void showPermissionRational(PermissionToken token) {

        DialogUtils.getInstance().alertDialog(context,
                context,
                context.getResources().getString(R.string.we_need_this_permission_for_find_nearest_parking_places),
                context.getString(R.string.allow_this_permission_to_further_use_of_this_app),
                context.getString(R.string.allow),
                context.getString(R.string.cancel),
                new DialogUtils.DialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                        token.continuePermissionRequest();
                    }

                    @Override
                    public void onNegativeClick() {
                        token.cancelPermissionRequest();
                    }
                }).show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        }
    }

    @Override
    public void onBackPressed() {
        DialogUtils.getInstance().alertDialog(context,
                context,
                context.getResources().getString(R.string.are_you_sure_exit_without_giving_permission),
                context.getResources().getString(R.string.yes), context.getResources().getString(R.string.no),
                new DialogUtils.DialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                        finishAffinity();
                        Preferences.getInstance(context).setIsLocationPermissionGiven(false);
                        TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                    }

                    @Override
                    public void onNegativeClick() {
                        //null for this
                    }
                }).show();
    }
}
