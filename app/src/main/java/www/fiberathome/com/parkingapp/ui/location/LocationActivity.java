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
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.LocationHelper;

import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;

//import static www.fiberathome.com.parkingapp.ui.MainActivity.GPS_REQUEST_CODE;

public class LocationActivity extends BaseActivity {

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

    public boolean isGPSEnabled() {

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
                progressDialog = ApplicationUtils.progressDialog(context, "Enabling GPS ....");
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

    @Override
    protected void onDestroy() {
        dismissDialog();
        super.onDestroy();
    }

    private void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}