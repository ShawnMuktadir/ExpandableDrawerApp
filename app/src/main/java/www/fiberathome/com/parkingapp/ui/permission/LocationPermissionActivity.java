package www.fiberathome.com.parkingapp.ui.permission;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.utils.PermissionUtil;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class LocationPermissionActivity extends AppCompatActivity {

    private TextView permissionTV;
    private Button btnPermissions;

    private Context context;

    public static final int REQUEST_CODE_PERMISSIONS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_permission);

        context = this;
        setStatusBarColor(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(context.getResources().getColor(R.color.lightBg));
        }

        initUI();

        btnPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLocationPermission();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    private void initUI() {
        permissionTV = findViewById(R.id.permissionTV);
        btnPermissions = findViewById(R.id.btnPermissions);
    }

    public void setStatusBarColor(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int startColor = getWindow().getStatusBarColor();
            int endColor = ContextCompat.getColor(context, R.color.lightBg);
            ObjectAnimator.ofArgb(getWindow(), "statusBarColor", startColor, endColor).start();
        }
    }

    private void requestLocationPermission() {

        boolean foreground = ActivityCompat.checkSelfPermission(this,
                ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (foreground) {
            boolean background = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;

            if (background) {
                handleLocationUpdates();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE_PERMISSIONS);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE_PERMISSIONS);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {

            boolean foreground = false, background = false;

            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equalsIgnoreCase(ACCESS_COARSE_LOCATION)) {
                    //foreground permission allowed
                    if (grantResults[i] >= 0) {
                        foreground = true;
                        permissionTV.setText(context.getResources().getString(R.string.foreground_location_permission_allowed));
                        permissionTV.setTextColor(ContextCompat.getColor(this, R.color.colorOrange));
                        //Toast.makeText(getApplicationContext(), "Foreground location permission allowed", Toast.LENGTH_SHORT).show();
                        continue;
                    } else {
                        permissionTV.setText(context.getResources().getString(R.string.location_permission_denied_please_click_allow));
                        permissionTV.setTextColor(ContextCompat.getColor(this, R.color.red));
                        Timber.e("Background location permission denied 1st");

                        PermissionUtil.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION,
                                new PermissionUtil.PermissionAskListener() {
                                    @Override
                                    public void onPermissionAsk() {
                                        ActivityCompat.requestPermissions(
                                                LocationPermissionActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                                REQUEST_CODE_PERMISSIONS
                                        );
                                    }

                                    @Override
                                    public void onPermissionPreviouslyDenied() {

                                        Timber.e("Background location permission denied 2nd");
                                    }

                                    @Override
                                    public void onPermissionDisabled() {
                                        /*SharedPreManager.getInstance(context).setIsLocationPermissionGiven(false);
                                        SharedPreManager.getInstance(context).logout();
                                        finish();*/
                                        new AlertDialog.Builder(context).setTitle(context.getResources().getString(R.string.permission_denied_permanently)).
                                                //setMessage(context.getResources().getString(R.string.allow_this_permission_from_settings)).
                                                setMessage(context.getResources().getString(R.string.u_cant_use_this_app_anymore)).
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
                                                }).show();
                                    }

                                    @Override
                                    public void onPermissionGranted() {
                                        Toast.makeText(getApplicationContext(), "Location Permission granted", Toast.LENGTH_SHORT).show();
                                    }
                                    //Toast.makeText(getApplicationContext(), "Location Permission denied", Toast.LENGTH_SHORT).show();
                                    //break;
                                });
                    }

                    if (permissions[i].equalsIgnoreCase(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        if (grantResults[i] >= 0) {
                            foreground = true;
                            background = true;
                            SharedPreManager.getInstance(context).setIsLocationPermissionGiven(true);
                            permissionTV.setText(context.getResources().getString(R.string.background_location_permission_allowed));
                            permissionTV.setTextColor(ContextCompat.getColor(this, R.color.colorOrange));
                            //Toast.makeText(getApplicationContext(), "Background location location permission allowed", Toast.LENGTH_SHORT).show();
                        } else {
                            permissionTV.setText(context.getResources().getString(R.string.background_location_permission_denied_allow_all_the_time_for_using_parking_time));
                            permissionTV.setTextColor(ContextCompat.getColor(this, R.color.red));
                            Timber.e("Background location permission denied 2nd");
                            SharedPreManager.getInstance(context).setIsLocationPermissionGiven(false);
                            //Toast.makeText(getApplicationContext(), "Background location location permission denied", Toast.LENGTH_SHORT).show();
                            new AlertDialog.Builder(context).setTitle(context.getResources().getString(R.string.permission_all_time)).
                                    //setMessage(context.getResources().getString(R.string.allow_this_permission_from_settings)).
                                            setMessage(context.getResources().getString(R.string.u_cant_use_this_app_anymore)).
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
                                    }).show();
                        }
                    }
                }
                if (permissions[i].equalsIgnoreCase(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    if (grantResults[i] >= 0) {
                        foreground = true;
                        background = true;
                        SharedPreManager.getInstance(context).setIsLocationPermissionGiven(true);
                        permissionTV.setText(context.getResources().getString(R.string.background_location_permission_allowed));
                        permissionTV.setTextColor(ContextCompat.getColor(this, R.color.colorOrange));
                        //Toast.makeText(getApplicationContext(), "Background location location permission allowed", Toast.LENGTH_SHORT).show();
                    } else {
                        permissionTV.setText(context.getResources().getString(R.string.background_location_permission_denied_allow_all_the_time_for_using_parking_time));
                        permissionTV.setTextColor(ContextCompat.getColor(this, R.color.red));
                        Timber.e("Background location permission denied 2nd");
                        SharedPreManager.getInstance(context).setIsLocationPermissionGiven(false);
                        new AlertDialog.Builder(context).setTitle(context.getResources().getString(R.string.permission_all_time)).
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
                                }).show();
                        //Toast.makeText(getApplicationContext(), "Background location location permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                if (foreground) {
                    if (background) {
                        handleLocationUpdates();
                    } else {
                        handleForegroundLocationUpdates();
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getOpPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void handleLocationUpdates() {
        Timber.e("handleLocationUpdates");
        //foreground and background
        permissionTV.setText(context.getResources().getString(R.string.start_foreground_background_updates));
        permissionTV.setTextColor(ContextCompat.getColor(this, R.color.green));

        SharedPreManager.getInstance(context).setIsLocationPermissionGiven(true);

        Intent intent = new Intent(LocationPermissionActivity.this, HomeActivity.class);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        startActivity(intent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);

        //Toast.makeText(getApplicationContext(),"Start Foreground and Background Location Updates",Toast.LENGTH_SHORT).show();
    }

    private void handleForegroundLocationUpdates() {
        //handleForeground Location Updates
        Timber.e("handleForegroundLocationUpdates");
        permissionTV.setText(context.getResources().getString(R.string.start_foreground_location_updates_please_click_allow_all_the_time_for_using_parking_app));
        permissionTV.setTextColor(ContextCompat.getColor(this, R.color.colorOrange));
        SharedPreManager.getInstance(context).setIsLocationPermissionGiven(false);
        //Toast.makeText(getApplicationContext(),"Start foreground location updates",Toast.LENGTH_SHORT).show();
    }
}
