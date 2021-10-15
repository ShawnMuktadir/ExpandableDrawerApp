package www.fiberathome.com.parkingapp.module.booking_service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.data.Constants;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;

import static www.fiberathome.com.parkingapp.model.data.Constants.BOOKING_SERVICE_ID;

public class BookingService extends Service {

    public static final String TAG = "LocationTrackService";
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    private Context context;

    public Location previousBestLocation = null;

    public static final int LOCATION_CHECK_DELAY = 1000 * 60; // 3 min
    public static Boolean isRunning = false;

    private CountDownTimer countDownTimer;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("not Implemented yet");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null) {
            if (action.equals(Constants.START_BOOKING_TRACKING)) {
                //startTrackingLocation();
                mHandlerTask.run();
            } else if (action.equals(Constants.STOP_BOOKING_TRACKING)) {
                stopTrackingLocation();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startTrackingLocation() {
        isRunning = true;
        context = getApplicationContext();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = Constants.NOTIFICATION_CHANNEL_BOOKING;
        Intent targetIntent = new Intent(context, HomeActivity.class);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 2, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long[] v = {500, 1000};
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Booking in progress...")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(false)
                .setVibrate(v)
                .setSound(uri)
                .setContentIntent(contentIntent);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Location Tracking...", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Location Tracking Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(100)
                .setFastestInterval(500)
                .setMaxWaitTime(100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setSmallestDisplacement(1f);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            if (fusedLocationProviderClient != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                }
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        findDifference(getDate(Preferences.getInstance(context).getBooked().getArriveDate()), getDate(Preferences.getInstance(context).getBooked().getDepartedDate()));
        startForeground(BOOKING_SERVICE_ID, mBuilder.build());
    }

    private void stopTrackingLocation() {
        mHandler.removeCallbacks(mHandlerTask);
        isRunning = false;
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            if (isBetterLocation(locationResult.getLastLocation(), previousBestLocation)) {
                super.onLocationResult(locationResult);
                previousBestLocation = locationResult.getLastLocation();
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                Log.d(TAG, "LocationResult- lat" + latitude + " -lon" + longitude);
                sendLocationUpdate(locationResult.getLastLocation());
            }
        }
    };

    private void sendLocationUpdate(Location l) {
        Intent intent = new Intent("GPSLocationUpdates");
        // You can also include some extra data.
        Bundle b = new Bundle();
        b.putParcelable("Location", l);
        intent.putExtra("Location", b);
        //SharePreferenceUtils sharePreferenceUtils = new SharePreferenceUtils(context);
        //sharePreferenceUtils.addUserLocation(l);
        //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > LOCATION_CHECK_DELAY;
        boolean isSignificantlyOlder = timeDelta < -LOCATION_CHECK_DELAY;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else return isNewer && !isSignificantlyLessAccurate && isFromSameProvider;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    Handler mHandler = new Handler();
    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            if (!isRunning) {
                startTrackingLocation();
            }
            mHandler.postDelayed(mHandlerTask, LOCATION_CHECK_DELAY);
        }
    };

    @SuppressLint("SetTextI18n")
    private void startCountDown(long timerMilliDifference) {
        countDownTimer = new CountDownTimer(timerMilliDifference, 1000) {
            @SuppressLint("DefaultLocale")
            public void onTick(long millisUntilFinished) {
                int numMessages = 0;
                mBuilder.setContentText("" + String.format("%d min, %d sec remaining",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))  // <-- your timer value
                        .setNumber(++numMessages);
                // Because the ID remains unchanged, the existing notification is
                // updated.
                notificationManager.notify(
                        BOOKING_SERVICE_ID,  // <-- Place your notification id here
                        mBuilder.build());
            }

            public void onFinish() {
                //tvCountDown.setText(context.getResources().getString(R.string.please_wait));
            }
        }.start();
    }

    public void findDifference(String start_date,
                               String end_date) {

        // SimpleDateFormat converts the
        // string format to date object
        SimpleDateFormat sdf
                = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss");

        // try Block
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            // Calucalte time difference
            // in milliseconds
            if (d1 != null && d2 != null) {
                long difference_In_Time
                        = d2.getTime() - d1.getTime();

                // Calculate time difference in
                // seconds, minutes, hours, years,
                // and days
                long difference_In_Seconds
                        = (difference_In_Time
                        / 1000)
                        % 60;

                long difference_In_Minutes
                        = (difference_In_Time
                        / (1000 * 60))
                        % 60;

                long difference_In_Hours
                        = (difference_In_Time
                        / (1000 * 60 * 60))
                        % 24;

                long difference_In_Years
                        = (difference_In_Time
                        / (1000l * 60 * 60 * 24 * 365));

                long difference_In_Days
                        = (difference_In_Time
                        / (1000 * 60 * 60 * 24))
                        % 365;

                // Print the date difference in
                // years, in days, in hours, in
                // minutes, and in seconds
                //tvTimeDifference.setText(difference_In_Hours + " hr " + difference_In_Minutes + " min " + difference_In_Seconds + " sec");
                startCountDown(difference_In_Time);

                System.out.print(
                        "Difference "
                                + "between two dates is: ");

                System.out.println(
                        difference_In_Years
                                + " years, "
                                + difference_In_Days
                                + " days, "
                                + difference_In_Hours
                                + " hours, "
                                + difference_In_Minutes
                                + " minutes, "
                                + difference_In_Seconds
                                + " seconds");
            }
        }

        // catch the Exception
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter
                = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss");
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
