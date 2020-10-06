package www.fiberathome.com.parkingapp.view.start;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//import com.iotsens.sdk.IoTSensApiClient;
//import com.iotsens.sdk.IoTSensApiClientBuilder;
//import com.iotsens.sdk.sensors.SensorsRequest;
//import com.iotsens.sdk.sensors.SensorsRequestBuilder;

import androidx.appcompat.app.AlertDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.view.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.view.main.MainActivity;
import www.fiberathome.com.parkingapp.view.permission.PermissionActivity;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.view.signUp.SignUpActivity;

public class StartActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.button_login)
    Button btnLogin;
    @BindView(R.id.button_signup)
    Button btnSignup;

    private Context context;

    public static final String APPLICATION_ID = "FIBERATHOMEAPP"; // must be proper application identifier
    public static final String SECRET = "6b42713e7fcb0f08b9e01298eaad5805"; // must be proper secret
    public static final String DEFAULT_USER = "atif.hafizuddin"; // must be a proper user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        context = this;

        if (SharedPreManager.getInstance(getApplicationContext()).isLoggedIn()) {
            splash();
        }

        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                // Do something
                Intent loginIntent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                break;

            case R.id.button_signup:
                // Do something
                Intent signUpIntent = new Intent(StartActivity.this, SignUpActivity.class);
//                Intent signUpIntent = new Intent(StartActivity.this, RegistrationActivity.class);
                startActivity(signUpIntent);
                finish();
                break;
        }
    }

    public void splash() {
        //rotateAndZoomAnimation();
        Thread timerTread = new Thread() {
            public void run() {
                try {
                    // sleep(1000);

                    // Check user is logged in
                    if (SharedPreManager.getInstance(getApplicationContext()).isLoggedIn() && SharedPreManager.getInstance(context) != null && SharedPreManager.getInstance(context).isWaitingForLocationPermission()) {
                        Timber.e("activity start if -> %s", SharedPreManager.getInstance(context).isWaitingForLocationPermission());
                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else if (SharedPreManager.getInstance(getApplicationContext()).isLoggedIn() && !SharedPreManager.getInstance(context).isWaitingForLocationPermission()) {
                        Timber.e("activity start else if -> %s", SharedPreManager.getInstance(context).isWaitingForLocationPermission());
                        Intent intent = new Intent(StartActivity.this, PermissionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Timber.e("activity start else -> %s", SharedPreManager.getInstance(context).isWaitingForLocationPermission());
                        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                } finally {
//                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish();
//                }
            }
        };
        timerTread.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showMessage(String message) {
        Toast.makeText(StartActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        StartActivity.super.onBackPressed();
                        TastyToastUtils.showTastySuccessToast(context, "Thanks for being with us");
                    }
                }).create();
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
                //dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
            }
        });
        dialog.show();
    }
}