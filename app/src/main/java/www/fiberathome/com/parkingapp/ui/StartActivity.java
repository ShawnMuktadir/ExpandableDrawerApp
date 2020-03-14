package www.fiberathome.com.parkingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.iotsens.sdk.IoTSensApiClient;
import com.iotsens.sdk.IoTSensApiClientBuilder;
import com.iotsens.sdk.sensors.SensorsRequest;
import com.iotsens.sdk.sensors.SensorsRequestBuilder;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.utils.BaseActivity;
import www.fiberathome.com.parkingapp.utils.SharedPreManager;

public class StartActivity extends BaseActivity implements View.OnClickListener {

    public static final String APPLICATION_ID = "FIBERATHOMEAPP"; // must be proper application identifier
    public static final String SECRET = "6b42713e7fcb0f08b9e01298eaad5805"; // must be proper secret
    public static final String DEFAULT_USER = "atif.hafizuddin"; // must be a proper user
    private Button loginBtn;
    private Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        IoTSensApiClient apiClient = IoTSensApiClientBuilder.aIoTSensClient().withApplication(APPLICATION_ID).withSecret(SECRET).withDefaultUser(DEFAULT_USER).build();

        SensorsRequest sensorsRequest = SensorsRequestBuilder.aSensorRequest().build();

        if (SharedPreManager.getInstance(getApplicationContext()).isLoggedIn()) {
            splash();
        }

        setContentView(R.layout.activity_start);

        loginBtn = findViewById(R.id.button_login);
        loginBtn.setOnClickListener(this);

        signupBtn = findViewById(R.id.button_signup);
        signupBtn.setOnClickListener(this);
        View view = new View(getBaseContext());

        loginBtn = view.findViewById(R.id.button_login);
        signupBtn = view.findViewById(R.id.button_signup);


//        for (SensorBasic sensorBasic :  apiClient.getSensors(sensorsRequest)) {
//            System.out.println("Sensor = " + sensorBasic.toString());
//        }

        //for (SensorBasic sensorBasic :  apiClient.getSensors(sensorsRequest)) {
        //System.out.println("Sensor = " + sensorsRequest);
        //}


        // splash();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                // Do something
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.button_signup:
                // Do something
                Intent intent1 = new Intent(StartActivity.this, SignUpActivity.class);
                startActivity(intent1);
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
                    if (SharedPreManager.getInstance(getApplicationContext()).isLoggedIn()) {
                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
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
}
