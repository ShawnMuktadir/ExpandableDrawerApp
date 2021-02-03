package www.fiberathome.com.parkingapp.ui.start;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.ui.permission.PermissionActivity;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.ui.signUp.SignUpActivity;

public class StartActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.button_login)
    Button btnLogin;
    @BindView(R.id.button_signup)
    Button btnSignup;

    private Unbinder unbinder;
    private Context context;

    public static final String APPLICATION_ID = "FIBERATHOMEAPP"; // must be proper application identifier
    public static final String SECRET = "6b42713e7fcb0f08b9e01298eaad5805"; // must be proper secret
    public static final String DEFAULT_USER = "atif.hafizuddin"; // must be a proper user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        unbinder = ButterKnife.bind(this);
        context = this;

        if (Preferences.getInstance(getApplicationContext()).isLoggedIn()) {
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
                /*Intent loginIntent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();*/
                openActivity(new Intent(StartActivity.this, LoginActivity.class));
                break;

            case R.id.button_signup:
                // Do something
                /*Intent signUpIntent = new Intent(StartActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
                finish();*/
                openActivity(new Intent(StartActivity.this, SignUpActivity.class));
                break;
        }
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
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> {
                    StartActivity.super.onBackPressed();
                    TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
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

    private void openActivity(Intent intent) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ApplicationUtils.checkInternet(context)) {
                    startActivity(intent);
                    finish();
                } else {
                    ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                        Timber.e("Positive Button clicked");
                        /*if (userManager.isLoggedIn())
                            openActivity(new Intent(context, MainActivity.class));
                        else
                            openActivity(new Intent(context, LoginActivity.class));*/
                        if (ApplicationUtils.checkInternet(context)) {
                            if (Preferences.getInstance(getApplicationContext()).isLoggedIn() && Preferences.getInstance(context) != null && Preferences.getInstance(context).isWaitingForLocationPermission()) {
                                Timber.e("activity start if -> %s", Preferences.getInstance(context).isWaitingForLocationPermission());
                                Intent intent = new Intent(StartActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else if (Preferences.getInstance(getApplicationContext()).isLoggedIn() && !Preferences.getInstance(context).isWaitingForLocationPermission()) {
                                Timber.e("activity start else if -> %s", Preferences.getInstance(context).isWaitingForLocationPermission());
                                Intent intent = new Intent(StartActivity.this, PermissionActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Timber.e("activity start else -> %s", Preferences.getInstance(context).isWaitingForLocationPermission());
                                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        } else{
                            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                        }
                    }, (dialog, which) -> {
                        Timber.e("Negative Button Clicked");
                        dialog.dismiss();
                        finish();
                    });
//            ApplicationUtils.showMessageDialog(getString(R.string.open_error), context);
                }
            }
        }, 1000);
    }

    public void splash() {
        //rotateAndZoomAnimation();
        Thread timerTread = new Thread() {
            public void run() {
                try {
                    //sleep(1000);

                    // Check user is logged in
                    if (Preferences.getInstance(getApplicationContext()).isLoggedIn() && Preferences.getInstance(context) != null && Preferences.getInstance(context).isWaitingForLocationPermission()) {
                        Timber.e("activity start if -> %s", Preferences.getInstance(context).isWaitingForLocationPermission());
                        Intent intent = new Intent(StartActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else if (Preferences.getInstance(getApplicationContext()).isLoggedIn() && !Preferences.getInstance(context).isWaitingForLocationPermission()) {
                        Timber.e("activity start else if -> %s", Preferences.getInstance(context).isWaitingForLocationPermission());
                        Intent intent = new Intent(StartActivity.this, PermissionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Timber.e("activity start else -> %s", Preferences.getInstance(context).isWaitingForLocationPermission());
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

    private void showMessage(String message) {
        Toast.makeText(StartActivity.this, message, Toast.LENGTH_LONG).show();
    }
}
