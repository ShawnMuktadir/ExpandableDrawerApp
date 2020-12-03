package www.fiberathome.com.parkingapp.ui.location;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.utils.LocationHelper;

import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;

//import static www.fiberathome.com.parkingapp.ui.MainActivity.GPS_REQUEST_CODE;

public class LocationActivity extends AppCompatActivity {

    private Context context;
    private TextView permissionTV;
    private Button btn_grant;
    private static final String TAG = LocationActivity.class.getCanonicalName();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        context = this;
        permissionTV = findViewById(R.id.permissionTV);
        btn_grant = findViewById(R.id.btn_grant);

        setListeners();
    }

    private void setListeners(){
        btn_grant.setOnClickListener(view -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
    }

   /* private void setListeners() {
        btn_grant.setOnClickListener(v -> {
            if (isGPSEnabled()) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Enabling GPS ....");
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Intent intent = new Intent(LocationActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }, 4000);

            } else {
                TastyToastUtils.showTastyWarningToast(context, "GPS is required for this app to work. Please enable GPS.");
            }
        });
    }
*/
    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.e("onActivityResult LocationActivity called");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_REQUEST_CODE) {

            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            assert locationManager != null;
            boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (providerEnabled) {

                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Enabling GPS ....");
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(context, "GPS is enabled", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(LocationActivity.this, MainActivity.class));
                        startActivity(new Intent(LocationActivity.this, HomeActivity.class));
                        finish();
                    }
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

        if(new LocationHelper(this).isLocationEnabled()){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Initializing location ....");
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    //Intent intent = new Intent(LocationActivity.this, MainActivity.class);
                    Intent intent = new Intent(LocationActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }, 4000);

        }
    }

    //    private void checkWhetherLocationSettingsAreSatisfied() {
//
//        LocationRequest mLocationRequest = LocationRequest.create()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(1000)
//                .setNumUpdates(2);
//
//        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
//        builder.setAlwaysShow(true);
//        builder.setNeedBle(true);
//        SettingsClient client = LocationServices.getSettingsClient(this);
//        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
//        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
//            @Override
//            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                Log.d(TAG, "onSuccess() called with: locationSettingsResponse = [" + locationSettingsResponse + "]");
//                hasLocationPermission();
//
//            }
//        });
//        task.addOnFailureListener(this, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "onSuccess --> onFailure() called with: e = [" + e + "]");
//                if (e instanceof ResolvableApiException) {
//                    // Location settings are not satisfied, but this can be fixed
//                    // by showing the user a dialog.
//                    try {
//                        // Show the dialog by calling startResolutionForResult(),
//                        // and check the result in onActivityResult().
//                        ResolvableApiException resolvable = (ResolvableApiException) e;
//                        resolvable.startResolutionForResult(LocationActivity.this,
//                                AppConstants.REQUEST_CHECK_SETTINGS);
//                    } catch (IntentSender.SendIntentException e1) {
//
//                        e1.printStackTrace();
//                    }
//                }
//
//            }
//        });
//    }
//
//    private void hasLocationPermission() {
//
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == AppConstants.REQUEST_CHECK_SETTINGS) {
//
//            // Make sure the request was successful
//            if (resultCode == RESULT_OK) {
//
//                hasLocationPermission();
//
//            } else {
//                //User clicks No
//            }
//        }
//
//    }
}