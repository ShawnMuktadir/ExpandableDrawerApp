package www.fiberathome.com.parkingapp.ui.permission;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
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
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.permission.listener.DexterPermissionListener;
import www.fiberathome.com.parkingapp.ui.permission.listener.PermissionInterface;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;

public class PermissionActivity extends AppCompatActivity implements PermissionInterface {

    private DexterPermissionListener permissionListener;
    private TextView permissionTV;
    private Context context;

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
        switch (permissionName) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                //Intent intent = new Intent(PermissionActivity.this, MainActivity.class);
                Intent intent = new Intent(PermissionActivity.this, HomeActivity.class);
                SharedPreManager.getInstance(context).setIsLocationPermissionGiven(true);
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
                SharedPreManager.getInstance(context).setIsLocationPermissionGiven(false);
                permissionTV.setTextColor(ContextCompat.getColor(this, R.color.LogoRed));
                break;
        }

        new AlertDialog.Builder(this).setTitle("Permission Denied permanently,You can't use this \napp any more.").
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
                permissionTV.setText("Permission Denied,You can't search nearest \n parking location from you. For further use please allow location");
                SharedPreManager.getInstance(context).setIsLocationPermissionGiven(false);
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
        if (isFinishing()) {
            overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        }
    }

    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to exit without giving permission?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
//                        PermissionActivity.super.onBackPressed();
                        finish();
                        SharedPreManager.getInstance(context).setIsLocationPermissionGiven(false);
                        TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                    }
                }).create();
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
                //dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
            }
        });
        dialog.show();
    }

}
