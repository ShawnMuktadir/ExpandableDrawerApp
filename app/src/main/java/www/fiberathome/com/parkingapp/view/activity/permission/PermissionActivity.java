package www.fiberathome.com.parkingapp.view.activity.permission;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.listener.DexterPermissionListener;
import www.fiberathome.com.parkingapp.listener.PermissionInterface;
import www.fiberathome.com.parkingapp.view.activity.main.MainActivity;

public class PermissionActivity extends AppCompatActivity implements PermissionInterface {

    private DexterPermissionListener permissionListener;
    private TextView permissionTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        permissionTV = findViewById(R.id.permissionTV);
        permissionListener = new DexterPermissionListener(this);
    }


    public void takeLocationPermission(View view) {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(permissionListener).check();
    }

    @Override
    public void showPermissionGranted(String permissionName) {
        switch (permissionName) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                Intent intent = new Intent(PermissionActivity.this, MainActivity.class);
                startActivity(intent);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
                break;
        }
    }

    @Override
    public void handlePermanentDeniedPermission(String permissionName) {

        switch (permissionName) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                permissionTV.setText("Permission Denied permanently");
                permissionTV.setTextColor(ContextCompat.getColor(this, R.color.LogoRed));
                break;
        }

        new AlertDialog.Builder(this).setTitle("Permission Denied permanently,You can't use this app any more.").
                setMessage("Please allow this permission from settings").
                setPositiveButton("Allow", new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openSettings();
                        dialog.dismiss();
                    }
                }).
                setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                }).show();

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
        switch (permissionName) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                permissionTV.setText("Permission Denied,You can't search nearest \n parking location for you. For further use please allow location");
                permissionTV.setTextColor(ContextCompat.getColor(this, R.color.LogoRed));
                break;
        }
    }

    @Override
    public void showPermissionRational(PermissionToken token) {
        new AlertDialog.Builder(this).setTitle("We need this permission for find nearest parking places").
                setMessage("Please allow this permission to further use this app").
                setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        token.continuePermissionRequest();
                        dialog.dismiss();
                    }
                }).
                setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        token.cancelPermissionRequest();
                        dialog.dismiss();
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                token.cancelPermissionRequest();
            }
        }).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()){
            overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        }
    }

}
