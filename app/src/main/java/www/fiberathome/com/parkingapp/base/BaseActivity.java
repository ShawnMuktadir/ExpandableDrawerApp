package www.fiberathome.com.parkingapp.base;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

/**
 * Base activity to check GPS disabled and Internet <br/>
 * this activity requires following permission(s) to be added in the AndroidManifest.xml file:
 *
 * <ul>
 * <li>android.permission.ACCESS_FINE_LOCATION</li>
 * <li>android.permission.INTERNET</li>
 * <li>android.permission.ACCESS_NETWORK_STATE</li>
 *
 * </ul>
 */
public class BaseActivity extends AppCompatActivity implements LocationListener {

    protected LocationManager mLocationManager;
    private static final int GPS_ENABLE_REQUEST = 0x1001;
    private static final int WIFI_ENABLE_REQUEST = 0x1006;

    private Context context;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(ParkingApp.localeManager.setLocale(base));
        Timber.e("attachBaseContext");
    }

    private BroadcastReceiver mNetworkDetectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkInternetConnection();
        }
    };
    private AlertDialog mInternetDialog;
    private AlertDialog mGPSDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        registerReceiver(mNetworkDetectReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        mLocationManager.removeUpdates(this);
        unregisterReceiver(mNetworkDetectReceiver);
        super.onDestroy();
    }

    /*public void checkInternetConnection() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = manager.getActiveNetworkInfo();

        if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {

        } else {
            showNoInternetDialog();
        }
    }*/

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledDialog();
        }
    }

    private boolean isConnected = false;

    private void checkInternetConnection() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = manager.getActiveNetworkInfo();


        if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
            isConnected = true;
            //showNoConnectionSnackBar("Connected", isConnected, 1500);

        } else {
            isConnected = false;
            showNoConnectionSnackBar("No active Internet connection found.", isConnected, 6000);
        }
    }

    private void showNoConnectionSnackBar(String message, boolean isConnected, int duration) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                message, duration);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView
                .findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.white));

        if (isConnected) {
            //sbView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            return;
        } else {
            sbView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            snackbar.setAction(context.getString(R.string.retry), view -> {
                /*Intent internetOptionsIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                startActivityForResult(internetOptionsIntent, WIFI_ENABLE_REQUEST);*/
                if (ApplicationUtils.checkInternet(context)) {
                    return;
                } else {
                    TastyToastUtils.showTastyWarningToast(context, "Please connect to internet");
                }
            });
            snackbar.setActionTextColor(getResources().getColor(R.color.white));
        }

        snackbar.show();
    }

    private void showNoInternetDialog() {

        /*if (ApplicationUtils.checkInternet(context)) {
            return;
        } else {
            ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                Timber.e("Positive Button clicked");
                if (ApplicationUtils.checkInternet(context)) {
                    return;
                } else {
                    TastyToastUtils.showTastyWarningToast(context, "Please connect to internet");
                }
            }, (dialog, which) -> {
                Timber.e("Negative Button Clicked");
                dialog.dismiss();
                if (context != null) {
                    finishAffinity();
                    TastyToastUtils.showTastySuccessToast(context, "Thanks for being with us");
                }
            });
        }*/

        if (mInternetDialog != null && mInternetDialog.isShowing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Internet Disabled!");
        builder.setMessage("No active Internet connection found.");
        builder.setPositiveButton(context.getString(R.string.retry), (dialog, which) -> {
            /*Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
            startActivityForResult(gpsOptionsIntent, WIFI_ENABLE_REQUEST);*/
            if (ApplicationUtils.checkInternet(context)) {
                return;
            } else {
                TastyToastUtils.showTastyWarningToast(context, "Please connect to internet");
            }
        }).setNegativeButton(context.getString(R.string.close_app), (dialog, which) -> {
            dialog.dismiss();
            if (context != null) {
                finishAffinity();
                TastyToastUtils.showTastySuccessToast(context, "Thanks for being with us");
            }
        });
        mInternetDialog = builder.create();
        mInternetDialog.show();
    }

    public void showGPSDisabledDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS Disabled");
        builder.setMessage("Gps is disabled, in order to use the application properly you need to enable GPS of your device");
        builder.setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_ENABLE_REQUEST);
            }
        }).setNegativeButton("No, Just Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        mGPSDialog = builder.create();
        mGPSDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GPS_ENABLE_REQUEST) {
            if (mLocationManager == null) {
                mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            }

            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showGPSDisabledDialog();
            }

        } else if (requestCode == WIFI_ENABLE_REQUEST) {

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

/*@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    private AlertDialog alertDialog;
    private static final int WIFI_ENABLE_REQUEST = 0x1006;



    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkInternetConnection();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        registerReceiver(receiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        Utility.resetActivityTitle(this);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WIFI_ENABLE_REQUEST) {
            // Toast.makeText(BaseActivity.this, "Connected", Toast.LENGTH_LONG).show();
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (!wifiManager.isWifiEnabled()) {

            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            // Toast.makeText(BaseActivity.this, "Connected", Toast.LENGTH_LONG).show();
        } else {
            showNoInternetDialog();
        }
    }

    private void showNoInternetDialog() {

        if (ApplicationUtils.checkInternet(context)) {
            return;
        } else {
            ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                Timber.e("Positive Button clicked");
                if (ApplicationUtils.checkInternet(context)) {
                    return;
                } else {
                    TastyToastUtils.showTastyWarningToast(context, "Please connect to internet");
                }
            }, (dialog, which) -> {
                Timber.e("Negative Button Clicked");
                dialog.dismiss();
                if (context != null) {
                    finishAffinity();
                    TastyToastUtils.showTastySuccessToast(context, "Thanks for being with us");
                }
            });

        *//*if (alertDialog != null && alertDialog.isShowing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Internet Disabled!");
        builder.setMessage("No active Internet connection found.");
        builder.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent enableWifi = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                startActivityForResult(enableWifi, WIFI_ENABLE_REQUEST);
            }
        }).setNegativeButton("No, Just Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                TastyToastUtils.showTastySuccessToast(context, "Thanks for being with us");
            }
        });
        alertDialog = builder.create();
        alertDialog.show();*//*
        }
    }
}*/
