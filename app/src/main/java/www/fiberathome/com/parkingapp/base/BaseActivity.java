package www.fiberathome.com.parkingapp.base;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.GeoFenceBroadcastReceiver;
import www.fiberathome.com.parkingapp.utils.GeofenceConstants;
import www.fiberathome.com.parkingapp.utils.SnackBarUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.internet.Connectivity;

import static www.fiberathome.com.parkingapp.ui.home.HomeActivity.GPS_REQUEST_CODE;

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

    private static final int GPS_ENABLE_REQUEST = 0x1001;
    private static final int WIFI_ENABLE_REQUEST = 0x1006;
    private static final int UPDATE_CODE = 1000;

    private Snackbar snackbar;

    private final List<Geofence> geofenceList = new ArrayList<>();
    private Geofence geoFence;
    private GeofencingClient geofencingClient;

    protected LocationManager mLocationManager;

    private Context context;

    private boolean isFastConnection;

    private final BroadcastReceiver mNetworkDetectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkInternetConnection();
        }
    };

    private AlertDialog mInternetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        isFastConnection = Connectivity.isConnectedFast(context);

        geofencingClient = LocationServices.getGeofencingClient(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(context.getResources().getColor(R.color.updatedColorPrimaryDark));
        }

        snackbar = Snackbar.make(this.findViewById(android.R.id.content), context.getResources().getString(R.string.connect_to_internet), 86400000);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        context.registerReceiver(mNetworkDetectReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerReceiver(mBackgroundLocationReceiver, new IntentFilter(Manifest.permission.ACCESS_BACKGROUND_LOCATION));
        }*/

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, GPS_ENABLE_REQUEST);
        } else {
            // permission granted
            return;
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //ForceUpdateChecker.with(context).onUpdateNeeded(BaseActivity.this).check();
    }

    @Override
    protected void onDestroy() {
        mLocationManager.removeUpdates(this);

        context.unregisterReceiver(mNetworkDetectReceiver);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.unregisterReceiver(mBackgroundLocationReceiver);
        } */

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledDialog();
        }
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
            Timber.e("requestCode WIFI_ENABLE_REQUEST");
        }

        /*else if (requestCode == UPDATE_CODE) {
            if (!BuildConfig.VERSION_NAME.equalsIgnoreCase(ForceUpdateChecker.KEY_CURRENT_VERSION)) {
                DialogUtil.getInstance().alertDialog(
                        (Activity) context,
                        context.getResources().getString(R.string.new_version_available), context.getResources().getString(R.string.please_update_the_app),
                        context.getResources().getString(R.string.update), context.getResources().getString(R.string.no_thanks),
                        new DialogUtil.DialogClickListener() {
                            @Override
                            public void onPositiveClick() {
                                redirectStore(ForceUpdateChecker.KEY_UPDATE_URL);
                            }

                            @Override
                            public void onNegativeClick() {
                                finishAffinity();
                                TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                            }
                        }).show();
            }*/

        //}
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private ProgressDialog progressDialog;

    protected void showLoading(Context context) {
        progressDialog = DialogUtils.getInstance().progressDialog(context, context.getResources().getString(R.string.please_wait));
    }

    protected void showLoading(Context context, String message) {
        progressDialog = DialogUtils.getInstance().progressDialog(context, message);
    }

    protected void hideLoading() {
        if (progressDialog == null) return;

        progressDialog.dismiss();
        progressDialog.cancel();
    }

    private void checkInternetConnection() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = manager.getActiveNetworkInfo();

        boolean isConnected = false;
        if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED && isFastConnection) {
            isConnected = true;
            snackbar.dismiss();
            SnackBarUtils.getInstance().showSuccessSnackBar(context, snackbar.getView(), "Back online", "");
            //overlay.setVisibility(View.GONE);
            //showNoConnectionSnackBar("Connected", isConnected, 1500);

        } else {
            isConnected = false;
            //showNoConnectionSnackBar("No active Internet connection found.", isConnected, 6000);
            showInternetConnectionSnackBar(context.getResources().getString(R.string.connect_to_internet), isConnected, 86400000);
            //showInternetConnectionSnackBar(context.getResources().getString(R.string.connect_to_internet), isConnected, Snackbar.LENGTH_INDEFINITE);
            //overlay.setVisibility(View.VISIBLE);
        }
    }

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

    @SuppressLint("InflateParams")
    private void showInternetConnectionSnackBar(String message, boolean isConnected, int duration) {
        // Inflate our custom view
        View snackView = getLayoutInflater().inflate(R.layout.snackbar_internet, null);
        snackbar = Snackbar.make(this.findViewById(android.R.id.content), message, duration);
        View sbView = snackbar.getView();
        TextView tv = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.TRANSPARENT);
        //snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.transparent_white));
        // Configure our custom view
        View overlay = snackView.findViewById(R.id.overlay);

        if (isConnected) {
            snackbar.dismiss();
            sbView.setBackgroundColor(getResources().getColor(R.color.transparent_white));
            snackView.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
        } else {
            sbView.setBackgroundColor(getResources().getColor(R.color.transparent_white));
            overlay.setVisibility(View.VISIBLE);
            snackView.setVisibility(View.VISIBLE);
            // Create the Snackbar
            LinearLayout.LayoutParams objLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            //Snackbar snackbar = Snackbar.make(this.findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);

            // Get the Snackbar layout view
            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

            // Set snackbar layout params
            int navbarHeight = getNavBarHeight(this);
            FrameLayout.LayoutParams parentParams = (FrameLayout.LayoutParams) layout.getLayoutParams();
            parentParams.gravity = Gravity.TOP;
            //parentParams.setMargins(0, 0, 0, 0 - navbarHeight + 50); //from bottom
            //parentParams.setMargins(0, navbarHeight - 150, 0, 0); //from top
            layout.setLayoutParams(parentParams);
            layout.setPadding(0, 0, 0, 0);
            layout.setLayoutParams(parentParams);

            TextView messageTextView = snackView.findViewById(R.id.message_text_view);
            //messageTextView.setTextColor(context.getResources().getColor(R.color.transparent_white));
            messageTextView.setText(message);

            TextView textViewOne = snackView.findViewById(R.id.first_text_view);
            textViewOne.setText(context.getResources().getString(R.string.retry));
            textViewOne.setOnClickListener(v -> {
                Timber.d("showTwoButtonSnackbar() : allow clicked");
                //snackbar.dismiss();
                if (ConnectivityUtils.getInstance().checkInternet(context)) {
                    snackbar.dismiss();
                } else {
                    snackbar.show();
                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                }
            });

            TextView textViewTwo = snackView.findViewById(R.id.second_text_view);
            textViewTwo.setText(context.getResources().getString(R.string.close_app));
            textViewTwo.setOnClickListener(v -> {
                Timber.d("showTwoButtonSnackbar() : deny clicked");
                snackbar.dismiss();
                if (context != null) {
                    finishAffinity();
                    TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                }
            });

            // Add our custom view to the Snackbar's layout
            layout.addView(snackView, objLayoutParams);

            // Show the Snackbar
            snackbar.show();
        }
    }

    public static int getNavBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void startActivity(Class activityClass) {
        startActivity(new Intent(getApplicationContext(), activityClass));
    }

    public void startActivityWithFinish(Class activityClass) {
        startActivity(new Intent(getApplicationContext(), activityClass));
        finish();
    }

    public void startActivityWithFinishAffinity(Class activityClass) {
        startActivity(new Intent(getApplicationContext(), activityClass));
        finishAffinity();
    }

    public void setStatusBarColor(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int startColor = getWindow().getStatusBarColor();
            int endColor = ContextCompat.getColor(context, R.color.updatedColorPrimaryDark);
            ObjectAnimator.ofArgb(getWindow(), "statusBarColor", startColor, endColor).start();
        }
    }

    public void showGPSDisabledDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(context.getResources().getString(R.string.gps_disabled));
        builder.setMessage(context.getResources().getString(R.string.rules_for_using_app_through_gps));
        builder.setPositiveButton(context.getResources().getString(R.string.enable_gps),
                (dialog, which) ->
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                                GPS_ENABLE_REQUEST))
                .setNegativeButton(context.getResources().getString(R.string.close_app),
                        (dialog, which) -> {
                            if (context != null) {
                                finishAffinity();
                                TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                            }
                        });
        AlertDialog mGPSDialog = builder.create();
        mGPSDialog.show();
        mGPSDialog.setCanceledOnTouchOutside(false);
    }

    private void showNoConnectionSnackBar(String message, boolean isConnected, int duration) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                message, duration);
        View sbView = snackbar.getView();
        TextView textView = sbView
                .findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.white));

        if (isConnected) {
            sbView.setBackgroundColor(getResources().getColor(R.color.transparent_gray));
            return;
        } else {
            sbView.setBackgroundColor(getResources().getColor(R.color.transparent_gray));
            snackbar.setAction(context.getString(R.string.retry), view -> {
                /*Intent internetOptionsIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                startActivityForResult(internetOptionsIntent, WIFI_ENABLE_REQUEST);*/
                if (ConnectivityUtils.getInstance().checkInternet(context)) {
                    return;
                } else {
                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
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
            if (ConnectivityUtils.getInstance().checkInternet(context)) {
                return;
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
            }
        }).setNegativeButton(context.getString(R.string.close_app), (dialog, which) -> {
            dialog.dismiss();
            if (context != null) {
                finishAffinity();
                TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
            }
        });
        mInternetDialog = builder.create();
        mInternetDialog.show();
    }

    public void setGeoFencing(LatLng latlng) {

        geofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(GeofenceConstants.GEOFENCE_ID)

                .setCircularRegion(
                        latlng.latitude,
                        latlng.longitude,
                        GeofenceConstants.GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(GeofenceConstants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        geofencingClient.addGeofences(getGeoFencingRequest(), getGeoFencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        // ...
                        Timber.e("Success");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        // ...
                        //Allow location Access should be all the time
                        Timber.e("Fail -> %s", e.getMessage());
                        /*new android.app.AlertDialog.Builder(context).setTitle(context.getResources().getString(R.string.permission_all_time)).
                                //setMessage(context.getResources().getString(R.string.allow_this_permission_from_settings)).
                                        setMessage(context.getResources().getString(R.string.in_order_to_use_this_app)).
                                setPositiveButton(context.getResources().getString(R.string.allow), new DialogInterface.OnClickListener() {

                                    @RequiresApi(api = Build.VERSION_CODES.Q)
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        openSettings();
                                        dialog.dismiss();
                                    }
                                }).
                                setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();*/
                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getOpPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private GeofencingRequest getGeoFencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }
    //geoFencing

    private PendingIntent geoFencePendingIntent;

    private PendingIntent getGeoFencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geoFencePendingIntent != null) {
            return geoFencePendingIntent;
        }
        Intent intent = new Intent(context, GeoFenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geoFencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geoFencePendingIntent;
    }

    private boolean isConnectedFast(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && isConnectionFast(info.getType(), info.getSubtype(), context));
    }

    private NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    private boolean isConnectionFast(int type, int subType, Context context) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;

        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps

                // Above API level 7, make sure to set android:targetSdkVersion to appropriate level to use these

                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    public void setAppLocale(String localeCode) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            config.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(config, dm);
    }

    protected void setActionBarBackButton() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
