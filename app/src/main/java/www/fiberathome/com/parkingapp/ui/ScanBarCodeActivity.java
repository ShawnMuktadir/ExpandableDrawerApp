package www.fiberathome.com.parkingapp.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;

import java.io.IOException;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.databinding.ActivityScanBarCodeBinding;
import www.fiberathome.com.parkingapp.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.model.response.sensors.SensorArea;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.ui.schedule.ScheduleActivity;
import www.fiberathome.com.parkingapp.utils.MathUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

public class ScanBarCodeActivity extends BaseActivity implements FragmentChangeListener {

    protected BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    String intentData = "";

    ActivityScanBarCodeBinding binding;
    protected BaseActivity context;
    protected Location currentLocation;

    //Boolean variable to mark if the transaction is safe
    private boolean isTransactionSafe;

    //Boolean variable to mark if there is any transaction pending
    private boolean isTransactionPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        binding = ActivityScanBarCodeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        LocationManager mLocationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        //Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
        currentLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (currentLocation == null) {
            currentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        setListeners();
    }

    /*
    onPostResume is called only when the activity's state is completely restored. In this we will
    set our boolean variable to true. Indicating that transaction is safe now
    */
    @Override
    public void onPostResume() {
        super.onPostResume();
        isTransactionSafe = true;

        /* Here after the activity is restored we check if there is any transaction pending from
        the last restoration */
        if (isTransactionPending) {
            fragmentChange(HomeFragment.newInstance());
        }
    }

    /*
    onPause is called just before the activity moves to background and also before onSaveInstanceState. In this
    we will mark the transaction as unsafe
    */
    @Override
    public void onPause() {
        super.onPause();
        isTransactionSafe = false;
    }

    @Override
    public void fragmentChange(Fragment fragment) {
        try {
            if (isTransactionSafe) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                );
                ft.replace(R.id.nav_host_fragment, fragment);
                ft.addToBackStack(null);
                ft.commit();
                isTransactionPending = false;
            } else {
                 /*
                 If any transaction is not done because the activity is in background. We set the
                 isTransactionPending variable to true so that we can pick this up when we come back to
                 foreground */
                isTransactionPending = true;
            }
        } catch (IllegalStateException e) {
            // There's no way to avoid getting this if saveInstanceState has already been called.
            Timber.e(e.getCause());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        cameraSource.release();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    private void setListeners() {
        binding.btnAction.setOnClickListener(v -> {
            if (intentData.length() > 0) {
                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(intentData)));
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.confirm_booking));
            }
        });
    }

    private void initialiseDetectorsAndSources() {
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        binding.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        //cameraSource.start(binding.surfaceView.getHolder());
                        if (cameraSource == null) {
                            return;
                        }
                        cameraSource.start(binding.surfaceView.getHolder());
                    } else {
                        mPermissionResult.launch(Manifest.permission.CAMERA);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                ///Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    binding.txtBarcodeValue.post(() -> {
                        binding.btnAction.setText(context.getResources().getString(R.string.confirm_booking));
                        intentData = barcodes.valueAt(0).displayValue;
                        SensorArea sensorArea = new Gson().fromJson(intentData, SensorArea.class);
                        Timber.e(sensorArea.getParkingArea());
                        //List<String> baseStringList = new ArrayList<>();
                        //baseStringList.add(intentData);
                        parseQRIntentData(sensorArea);
                    });
                } else {
                    Timber.e("else called");
                }
            }
        });
    }

    private void parseQRIntentData(SensorArea intentData) {
        String parkingArea, placeId, count = "";
        double lat, lng;
        Timber.e("List intentData -> %s", intentData);

        parkingArea = intentData.getParkingArea();
        placeId = intentData.getPlaceId();
        count = intentData.getCount();
        lat = intentData.getEndLat();
        lng = intentData.getEndLng();

        Bundle bundle = new Bundle();
        bundle.putDouble("lat", lat);
        bundle.putDouble("long", lng);
        bundle.putString("areaName", parkingArea);
        bundle.putString("parkingSlotCount", count);
        bundle.putString("areaPlacedId", placeId);
        bundle.putBoolean("isInArea", true);
        context.startActivityWithFinishBundle(ScheduleActivity.class, bundle);
    }

    private double calculateDistance(Double startLatitude, Double startLongitude, Double endLatitude, Double endLongitude) {
        return MathUtils.getInstance().calculateDistance(startLatitude, startLongitude, endLatitude, endLongitude);
    }

    private final ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        if (ActivityCompat.checkSelfPermission(ScanBarCodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            try {
                                cameraSource.start(binding.surfaceView.getHolder());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Log.e("ScanBarCodeActivity", "onActivityResult: PERMISSION DENIED");
                    }
                }
            });
}
