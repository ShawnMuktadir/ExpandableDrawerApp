package www.fiberathome.com.parkingapp.ui.permission.location;

import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.data.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.databinding.ActivityLocationBinding;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.utils.LocationHelper;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
public class LocationActivity extends BaseActivity {

    private BaseActivity context;
    protected ActivityLocationBinding binding;

    public AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = this;
        setListeners();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.e("onActivityResult LocationActivity called");
        super.onActivityResult(requestCode, resultCode, data);

        /*if (requestCode == GPS_REQUEST_CODE) {
            if (mLocationManager == null) {
                mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            }

            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showGPSDisabledDialog();
            } else {
                if (resultCode == Activity.RESULT_OK) {
                    String result = data.getStringExtra("result");
                    showLoading(context);
                    new Handler().postDelayed(() -> {
                        hideLoading();
                        Toast.makeText(context, "GPS is enabled", Toast.LENGTH_SHORT).show();
                        startActivityWithFinish(HomeActivity.class);
                    }, 6000);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }*/

        if (requestCode == GPS_REQUEST_CODE) {

            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            assert locationManager != null;
            boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (providerEnabled) {
                showLoading(context);
                new Handler().postDelayed(() -> {
                    hideLoading();
                    Toast.makeText(context, "GPS is enabled", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LocationActivity.this, HomeActivity.class));
                    finish();
                }, 6000);

            } else {
                Toast.makeText(context, "GPS not enabled.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(context, "GPS not enabled. Unable to show user location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
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
        mLocationManager.
                requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        if (new LocationHelper(context).isLocationEnabled() && mLocationManager != null) {
            showLoading(context, context.getResources().getString(R.string.initialize_location));

            new Handler().postDelayed(() -> {
                hideLoading();
                Intent intent = new Intent(LocationActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }, 4000);
        }
    }

    @Override
    protected void onDestroy() {
        mLocationManager.removeUpdates(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to exit without giving permission?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> {
                    //PermissionActivity.super.onBackPressed();
                    finish();
                    Preferences.getInstance(context).setIsLocationPermissionGiven(false);
                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.thanks_message));
                }).create();
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
            //dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
        });
        dialog.show();
    }

    private void setListeners() {
        binding.btnGrant.setOnClickListener(view -> startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                GPS_REQUEST_CODE));
    }

    public boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {
            alertDialog = new AlertDialog.Builder(context)
                    .setTitle("GPS Permissions")
                    .setMessage("GPS is required for this app to work. Please enable GPS.")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);

                    }))
                    .setCancelable(false)
                    .show();

        }

        return false;
    }
}